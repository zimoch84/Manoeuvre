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
    public final static String CARD_PLAYING_STATE = "CARD_PLAYING_STATE";
       
    public String unitSelectionMode = PICK_ONE_UNIT;
    
    public MapState currentState, previousState;

    private PlayerState playerState;
    
    private MapStateChooserByCard mapStateChooserByCard;
    
    public MapStateHandler(Game game, PlayerState playerState) {
        this.game = game;
        this.playerState = playerState;
        LOGGER = LogManager.getLogger(MapStateHandler.class.getName());
        currentState = new MapPickAvalibleUnitState();
        mapStateChooserByCard = new MapStateChooserByCard();
    }
    
    /*
    Ca
    */
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
            
            case CARD_PLAYING_STATE : 
                mapStateChooserByCard.setMapStateByCard(playingCard, game, this);
            break;  
            
            case PICK_UNIT_BY_CARD : currentState = new MapPickUnitByCardState(playingCard,playerState.cardCommandFactory);
            break;
            
            case PICK_MOVE_POSITION_BY_CARD : currentState = new MapMoveUnitByCardState(playingCard, playerState.cardCommandFactory);
            break;                 
            
            default:
                LOGGER.error("Nie ma takiego stanu obsługi karty");
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

    @Override
    public void update(Observable o, Object arg) {
        
        System.out.println("Object attached to event()" +  o.getClass().toString() );
        
        String dialogType = (String) arg;
        
        LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        
        switch(dialogType){
            case EventType.DEFENDER_WITHDRAW:
            {
            if(game.getCurrentPlayer().isActive())
            {
                LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_MOVE_POSITION ");
                setState(MapStateHandler.PICK_MOVE_POSITION);
            }
            else 
            { 
                LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.NOSELECTION ");
                setState(MapStateHandler.NOSELECTION);
            }
            break;
            }
            case EventType.PUSRUIT_AFTER_COMBAT_RESOLUTION:
                if(game.getCurrentPlayer().isActive())
                    setState(MapStateHandler.PICK_UNIT_BY_CARD);
            break;
            
            case EventType.PUSRUIT_AFTER_WITHDRAW:
                if(game.getCurrentPlayer().isActive())
                    setState(MapStateHandler.PICK_ONE_UNIT);
            break;
            
            case EventType.PURSUIT:
                if(game.getCurrentPlayer().isActive())
                    setState(MapStateHandler.PICK_ONE_UNIT);
                else
                    setState(MapStateHandler.NOSELECTION);
                break;
                
            case EventType.ASSAULT_BEGINS:
                setState(MapStateHandler.NOSELECTION);
                break;
             
            case EventType.BOMBARD_BEGINS:
                setState(MapStateHandler.NOSELECTION);
                break;
            case EventType.THROW_DICE:
                setState(MapStateHandler.NOSELECTION);
            break;    
            
            case EventType.DEFENDING_CARDS_PLAYED:
                setState(MapStateHandler.NOSELECTION);
            break;
            
            case EventType.PICK_SUPPORT_UNIT:
                setState(MapStateHandler.PICK_MULTIPLE_UNITS);
            break;
            
            case EventType.LEADER_END_PICKING_SUPPORT: 
            case EventType.LEADER_DESELECTED:
                setState(MapStateHandler.NOSELECTION);
            break;
            case EventType.END_COMBAT:
                setState(MapStateHandler.NOSELECTION);
            break;
            
            case EventType.SKIRMISH_DESELECTED:
                 setState(MapStateHandler.NOSELECTION);
            break;
                
   
        }
        
        
        
        
    }
}
