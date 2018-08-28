/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;


import manouvre.interfaces.MapState;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.commands.CommandQueue;
import manouvre.events.EventType;
import manouvre.game.Card;
import manouvre.game.CardCommandFactory;
import manouvre.game.Combat;
import manouvre.game.Unit;
import manouvre.gui.CustomDialogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author xeon
 */
public class MapStateHandler implements Observer, Serializable{
    
    Logger LOGGER;
    Game game;
    
    public final static String NOSELECTION = "NOSELECTION";
    public final static String PICK_ONE_UNIT = "PICK_ONE_UNIT";
    public final static String PICK_MOVE_POSITION = "PICK_MOVE_POSITION";
    public final static String PICK_MULTIPLE_UNITS = "PICK_MULTIPLE_UNITS";
    public final static String PICK_UNIT_BY_CARD = "PICK_UNIT_BY_CARD";
    public final static String PICK_MOVE_POSITION_BY_CARD = "PICK_MOVE_POSITION_BY_CARD";
    public final static String CHOOSE_BY_CARD = "CARD_PLAYING_STATE";
       
    public String unitSelectionMode = PICK_ONE_UNIT;
    
    public MapState currentState, previousState;

    private CardCommandFactory cardCommandFactory;
    
    
    public MapStateHandler(Game game, CardCommandFactory cardCommandFactory) {
        this.game = game;
        this.cardCommandFactory = cardCommandFactory;
        LOGGER = LogManager.getLogger(MapStateHandler.class.getName());
        currentState = new MapPickAvalibleUnitState();
        }
    
    public void setState(String nextState)
    {
        LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu z:" + currentState + " na: "   + nextState );
        previousState = currentState;
        
        switch(nextState)
        {
            case NOSELECTION : currentState = new MapPickNoneState();
            break;
            
            case PICK_ONE_UNIT : currentState = new MapPickAvalibleUnitState();
            break;
            
            case PICK_MOVE_POSITION : currentState = new MapPickMovePositionState();
            break;
            
            case PICK_MULTIPLE_UNITS : currentState = new MapMultiPickUnitState();
            break;
            
            default: 
                LOGGER.error("Nie ma takiego stanu obsługi mapy");
            
        }
    }
    
    public void setStateByCard(Card playingCard, String nextState)
    {
        switch(nextState){
            
            case CHOOSE_BY_CARD : 
                setMapStateByCard(playingCard);
            break;  
            
            case PICK_UNIT_BY_CARD : currentState = new MapPickUnitByCardState(playingCard,cardCommandFactory);
            break;
            
            case PICK_MOVE_POSITION_BY_CARD : currentState = new MapMoveUnitByCardState(playingCard, cardCommandFactory);
            break;                 
            
            default:
                LOGGER.error("Nie ma takiego stanu obsługi karty: " + playingCard + " na stan: " + nextState );
        }
    }
    
    public void setPreviousState()
    {
        MapState copyCurrentState = currentState;
        if(previousState != null)
        { 
            currentState = previousState;
            previousState = copyCurrentState;
        }
    }
    public void handle(Position pos, Game game, CommandQueue cmdQueue)
    {
         currentState.handleInput(pos, game, cmdQueue, this);
    }
    public void setInitStateForPhase (Game game){
    
        LOGGER.debug(game.getCurrentPlayer().getName() + " setInitStateForPhase "  );
            switch (game.getPhase())
            {
                
                case Game.SETUP : setState(MapStateHandler.NOSELECTION);
                break;
                
                case Game.DISCARD : setState(MapStateHandler.NOSELECTION);
                break;
                
                case Game.MOVE : setState(MapStateHandler.PICK_ONE_UNIT);
                break;
                
                case Game.COMBAT : setState(MapStateHandler.NOSELECTION);
                break;
                
                case Game.RESTORATION : setState(MapStateHandler.NOSELECTION);
                break;
            }
    }

    
    
    private void setMapStateByCard(Card playingCard)
    {
         switch (getCardActionType(playingCard)){
                case Card.MOVE_UNIT_ACTION:
                {
                Unit selectedUnit = game.getSelectedUnit();
                if(selectedUnit != null)
                    if(!game.positionCalculator.getMovePositionsByCard(playingCard, selectedUnit).isEmpty())
                    {
                        setStateByCard(playingCard, MapStateHandler.PICK_MOVE_POSITION_BY_CARD);
                    }   
                    else 
                    {
                        game.notifyAbout(EventType.CARD_THERE_IS_NO_ROOM_FOR_MOVE);
                        setStateByCard(playingCard, MapStateHandler.NOSELECTION);
                    }
                else 
                    LOGGER.error("There is not unit selected");
                break;
                }
                case Card.PICK_UNIT_ACTION:    
                    if(!game.positionCalculator.getUnitsPositionToSelectByCard(playingCard).isEmpty())    
                            setStateByCard(playingCard, MapStateHandler.PICK_UNIT_BY_CARD);
                        else 
                            CustomDialogFactory.showCardNoValidTargetDialog();
                    break;
                case Card.MULTIPLE_UNIT_PICK_ACTION:   
                    
                    setStateByCard(playingCard, MapStateHandler.PICK_MULTIPLE_UNITS);
                break;
                
                case Card.NO_ACTION:
                    setState(MapStateHandler.NOSELECTION);
                    break;
                }
    }
    
    private int getCardActionType(Card card) {
        switch(game.getPhase()){
            case Game.MOVE:
            case Game.RESTORATION:    
                if (card.getCardType() == Card.LEADER) 
                    return Card.MULTIPLE_UNIT_PICK_ACTION;
               
                if (card.getHQType() == Card.FORCED_MARCH || card.getHQType() == Card.WITHDRAW || card.getHQType() == Card.SKIRMISH) 
                    return Card.MOVE_UNIT_ACTION;
                
                if (card.getHQType() == Card.SUPPLY || card.getHQType() == Card.REDOUBDT || card.getHQType() == Card.REGROUP || card.getHQType() == Card.AMBUSH || card.getCardType() == Card.UNIT) 
                    return Card.PICK_UNIT_ACTION;
                
           break;
           case Game.COMBAT:
               switch(game.getCombat().getState()){
                   case Combat.PICK_DEFENSE_CARDS:
                   case Combat.PICK_SUPPORT_CARDS:   
                       switch(card.getHQType()){
                           case Card.SKIRMISH:
                                return Card.MOVE_UNIT_ACTION;
                           default: return Card.NO_ACTION  ;
                       }
                       
                   default:
                        if (card.getCardType() == Card.LEADER) 
                            return Card.MULTIPLE_UNIT_PICK_ACTION;
                        
                        if (card.getHQType() == Card.FORCED_MARCH || card.getHQType() == Card.WITHDRAW )
                            return Card.MOVE_UNIT_ACTION;
                        
                        if (card.getHQType() == Card.SUPPLY || card.getHQType() == Card.AMBUSH) 
                            return Card.PICK_UNIT_ACTION;
                        if(card.getCardType() == Card.UNIT)
                        {
                            if(card.getPlayingCardMode().equals(Card.NO_TYPE))
                                return Card.NO_ACTION;
                            else return Card.PICK_UNIT_ACTION;
                                        
                        }
                        
                        
                             
               }
               
           break;    
        }
    return Card.NO_ACTION;
    }
    
    @Override
    public void update(Observable o, Object arg) {
        String dialogType = (String) arg;
        switch(dialogType){
            
            case EventType.NEXT_PHASE: 
            case EventType.END_TURN:
                if(game.getCurrentPlayer().isActive())
                    setInitStateForPhase(game);
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);    
            break;
            case EventType.SETUP_FINISHED:
                if(game.getCurrentPlayer().isFinishedSetup())
                setState(MapStateHandler.NOSELECTION);
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);   
            break;
            case EventType.COMBAT_DEFENDER_WITHDRAW:
                if(game.getCurrentPlayer().isActive())
                    setState(MapStateHandler.PICK_MOVE_POSITION);
                else 
                    setState(MapStateHandler.NOSELECTION);
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);   
            break;
            case EventType.COMBAT_PUSRUIT_AFTER_RESOLUTION:
                if(game.getCurrentPlayer().isActive())
                    setState(MapStateHandler.PICK_UNIT_BY_CARD);
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);   
            break;
            
            case EventType.PUSRUIT_AFTER_WITHDRAW:
                if(game.getCurrentPlayer().isActive())
                    setState(MapStateHandler.PICK_ONE_UNIT);
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);   
            break;
            
            case EventType.COMBAT_PURSUIT_STARTED:
                if(game.getCurrentPlayer().isActive())
                    setState(MapStateHandler.PICK_ONE_UNIT);
                else
                    setState(MapStateHandler.NOSELECTION);
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);   
            break;
                
            case EventType.ASSAULT_BEGINS:
            case EventType.BOMBARD_BEGINS:
            case EventType.COMBAT_THROW_DICE:
            case EventType.DEFENDING_CARDS_PLAYED:
            case EventType.LEADER_END_PICKING_SUPPORT: 
            case EventType.LEADER_DESELECTED:
            case EventType.END_COMBAT: 
            case EventType.SKIRMISH_DESELECTED:    
                if(game.getCurrentPlayer().isActive())
                    setState(MapStateHandler.NOSELECTION);
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);   
                break;
            
            case EventType.PICK_SUPPORT_UNIT:
                setState(MapStateHandler.PICK_MULTIPLE_UNITS);
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);   
            break;
            
            
            
            
   
        }
    }
    
    
}
