/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import manouvre.interfaces.MapState;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.Card;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;
import manouvre.commands.CommandQueue;
import manouvre.commands.MoveUnitCommand;
import manouvre.game.CardCommandFactory;
import manouvre.gui.CustomDialog;
import manouvre.gui.GameWindow;
import manouvre.interfaces.CardCommand;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author xeon
 */
public class MapMoveUnitByCardState implements MapState, Serializable{

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MapMoveUnitByCardState.class.getName());
    
    Card playingCard;
    CardCommandFactory ccf;

    public MapMoveUnitByCardState(Card playingCard, CardCommandFactory ccf) {
        this.playingCard = playingCard;
        this.ccf = ccf;
    }
    
    
    
    @Override
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue , MapStateHandler handler) {
       
                Unit selectedUnit = game.getSelectedUnit();
                
                if(getMovePositions(playingCard, game).contains(pos))
                {
                    MoveUnitCommand moveUnit = new MoveUnitCommand(game.getCurrentPlayer().getName() , selectedUnit,  pos);
                    /*
                    We attach move command to wrap it to postpone execution in card command
                    */
                    ccf.setAttachedCommand(moveUnit);
                    /*
                    Confirmation dialog
                    */
                    
                    showConfirmationCardDialog(cmdQueue, game);
                    LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapInputStateHandler.NOSELECTION");
                    handler.setState(MapStateHandler.NOSELECTION);
                }
                /*
                Deselect card and clear Card Factory
                or do nothing 
                */
                else {
                    /*
                    LOGGER.debug(game.getCurrentPlayer().getName() + " game.getCardCommandFactory().resetPlayingCard();");
                    game.getCardCommandFactory().resetPlayingCard();
                    LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapStateHandler.NOSELECTION");
              
                    game.unselectAllUnits();
                    handler.setState(MapStateHandler.NOSELECTION);
                        */
                }
                



    
    }
private ArrayList<Position> getPossibleUnitPostionToSelect(Game game){
    
     ArrayList<Position> returnPositions = new ArrayList<>();
     
     if(playingCard!= null)
        {
            switch(playingCard.getCardType()){
                case Card.HQCARD:
                {
                if(playingCard.getHQType() == Card.SUPPLY)
                {
                   if(game.getPhase() == Game.MOVE)
                       return game.positionCalculator.getCurrentPlayerNotMovedUnits();
                   
                   if(game.getPhase() == Game.RESTORATION)
                     return game.positionCalculator.getCurrentPlayerInjuredUnitPositions();
                           
                }
                if(playingCard.getHQType() == Card.WITHDRAW)   
                    {
                    returnPositions.add(game.getCombat().getDefendingUnit().getPosition());
                    return returnPositions;
      
                    }
                
                break;
                }
                case Card.UNIT:
                {
                    if(game.getPhase() == Game.RESTORATION)
                    {
                       returnPositions.add(game.getCurrentPlayerUnitByName(playingCard.getCardName()).getPosition());
                       return returnPositions;
                    }  
                break;
                }    
                
                case Card.LEADER:
                {
                    if(game.getPhase() == Game.RESTORATION)
                     return game.positionCalculator.getCurrentPlayerInjuredUnitPositions();
                break;        
                }    
                
                    
                
            }
                
        }
    return null;
    
    
    
    }
private void showConfirmationCardDialog(CommandQueue cmdQueue, Game game){
            
    if(!game.supressConfirmation)
    {
    /*
        Confirmation dialog
        */
       // game.lockGUI();
        CustomDialog dialog = 
                new CustomDialog(CustomDialog.YES_NO_TYPE, 
                        "Are You sure to play that card? " ,
                        cmdQueue, game);
        try {
            dialog.setOkCommand(ccf.createCardCommand( playingCard ));
            /*
            TODO create cancel command
            dialog.setCancelCommand(ccf.resetFactoryCommand());
            */
            //dialog.setCancelCommand(moveUnit);
        } catch (Exception ex) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        dialog.setVisible(true);
        
    }
    
    else 
    {
    cmdQueue.storeAndExecuteAndSend(ccf.createCardCommand(playingCard));
    }
        
    }
private ArrayList<Position> getMovePositions(Card playingCard, Game game){
     
            ArrayList<Position> movePositions = new ArrayList<>();
            if(game.getSelectedUnit()!= null)
                {   
                    Unit selectedUnit = game.getSelectedUnit();

                    switch(playingCard.getCardType()){
                        
                        case Card.HQCARD :
                        {
                            switch(playingCard.getHQType()){
                           
                                case Card.FORCED_MARCH:
                                {
                                   movePositions = game.getOneSquareMovements(selectedUnit.getPosition()); 
                                   break;
                                }
                                case Card.SUPPLY:
                                {
                                   movePositions = game.getPossibleMovement(selectedUnit); 
                                   break;
                                }
                                
                                case Card.WITHDRAW:
                                {
                                   movePositions = game.positionCalculator.getRetreatPositions(selectedUnit); 
                                   break;
                                }
                                
                                case Card.SKIRMISH:
                                {
                                   movePositions = game.getTwoSquareMovements(selectedUnit.getPosition()); 
                                   break;
                                }
                                
                            }
                            break;
                        }
     
                    }
                }
    return movePositions;
}
}