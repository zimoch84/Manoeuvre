/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.Card;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;
import manouvre.commands.CommandQueue;
import manouvre.gui.CustomDialog;
import manouvre.gui.GameWindow;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author xeon
 */
public class MapCardPlayingState implements MapState, Serializable{

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MapCardPlayingState.class.getName());
    @Override
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue , MapInputStateHandler handler) {
       
                Card playingCard = game.getCardCommandFactory().getPlayingCard();
                switch (playingCard.getActionType(game.getPhase())){
                
                case Card.MOVE_ACTION:
                {
                Unit selectedUnit = game.getSelectedUnit();
                if(selectedUnit != null)
                {  
                
                if(!getMovePositions(playingCard, game).isEmpty())
                {
                    LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapInputStateHandler.PICK_MOVE_POSITION_BY_CARD");
                    handler.setState(MapInputStateHandler.PICK_MOVE_POSITION_BY_CARD);
                }   /*
                    Deselect card and clear Card Factory
                    */
                else {
                        LOGGER.debug(game.getCurrentPlayer().getName() + " game.getCardCommandFactory().resetPlayingCard();");
                        game.getCardCommandFactory().resetPlayingCard();
                        LOGGER.debug(game.getCurrentPlayer().getName() + " handler.setPreviousState()");
                        handler.setPreviousState();
      
                    }
                } 
                /* 
                We have to select unit - SUPPLY CARD
                */
                else {
                    if(!getPossibleUnitPostionToSelect(game).isEmpty())
                    LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapInputStateHandler.PICK_UNIT_BY_CARD");
                    game.getCurrentPlayerUnitAtPosition(pos).setSelected(true);
                    handler.setState(MapInputStateHandler.PICK_MOVE_POSITION_BY_CARD);
                    }
                break;
                }
                case Card.PICK_ACTION:    
                {
                if(!getAvaliblePositionToSelect(game,handler).isEmpty())    
                    if(getAvaliblePositionToSelect(game, handler).contains(pos))
                    {
                        if(playingCard.getCardType() == Card.UNIT )
                            {
                                Unit attackedUnit = game.getOpponentPlayerUnitAtPosition(pos);
                                game.getCardCommandFactory().setAttackedUnit(attackedUnit);
                                showConfirmationCardDialog(cmdQueue, game);
                            }

                    }
                    else 
                    showCannotPlayCardDialog(cmdQueue, game);    
                
                else 
                {
                showCardNoValidTargetDialog(cmdQueue, game);
                game.getCardCommandFactory().resetPlayingCard();
                
                
                }   
                break;
                }
                case Card.MULTIPLE_PICK_ACTION:    
                {
                int availaibleUnits = getAvaliblePositionToSelect(game, handler).size();
                
                if(availaibleUnits>0) 
                    {
                    int commandValue =game.getCardCommandFactory().getPlayingCard().getLederCommand();
                    
                    int maxSelections = ( commandValue > availaibleUnits ? availaibleUnits : commandValue );
                    /*
                    If we need to select more 
                    */
                    if(game.getNumberOfSupportingUnit() < maxSelections)
                    {    
                        if(getAvaliblePositionToSelect(game, handler).contains(pos))
                        {
                           game.getCurrentPlayerUnitAtPosition(pos).setSupporting(true);
                           if(game.getNumberOfSupportingUnit() == maxSelections)
                                    showConfirmationCardDialog(cmdQueue, game);
                        }
                        //else  this.repaint();
                    }
                    }
                else 
                showCannotPlayCardDialog(cmdQueue, game);    
                game.getCardCommandFactory().resetPlayingCard();
               
                break;
                }
                
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
                                   movePositions = game.getRetreatPositions(selectedUnit); 
                                   break;
                                }
                                
                            }
                            break;
                        }
     
                    }
                }
    return movePositions;
}
 protected ArrayList<Position> getAvaliblePositionToSelect(Game game, MapInputStateHandler handler)
    {
        Card playingCard = game.getCardCommandFactory().getPlayingCard();
        if(playingCard!=null)
        {   
            switch(playingCard.getCardType()){
                case Card.HQCARD:
                {
                if(playingCard.getHQType() == Card.SUPPLY)
                {
                    if(game.getPhase() == Game.MOVE)
                        
                        if ( handler.currentState instanceof MapCardPlayingState)
                            return game.getCurrentPlayerNotMovedUnits();
                    
                    
                    if(game.getPhase() == Game.RESTORATION)
                        return game.getCurrentPlayerInjuredUnitPositions();

                }
                break;
                }
                case Card.UNIT:
                {
                    /*
                    calculate possible targets if we know playing Card Mode            
                    */    
                    if(game.getPhase() == Game.COMBAT)
                    {    if(playingCard.getPlayingCardMode() != null )
                        {
                            game.getCardCommandFactory().calculateAttackingPositions(game.getSelectedUnit());    
                            return  game.getCardCommandFactory().getAttackingPositions();

                        }
                    }
                    if (game.getPhase() == Game.RESTORATION)
                    {
                       return  getPossibleUnitPostionToSelect(game);
                    }
                
                }    
                
                case Card.LEADER:
                {
                    return game.getPossibleSupportingUnitsPositions(game.getCardCommandFactory().getAttackedUnit());
  
                }    
                    
                
            }
                
        }
    return null;
    
    }
protected ArrayList<Position> getPossibleUnitPostionToSelect(Game game){
    Card playingCard = game.getCardCommandFactory().getPlayingCard();
     if(playingCard!=null)
        {
  
            switch(playingCard.getCardType()){
                case Card.HQCARD:
                {
                if(playingCard.getHQType() == Card.SUPPLY)
                {
                   if(game.getPhase() == Game.MOVE)
                       return game.getCurrentPlayerNotMovedUnits();
                   
                   if(game.getPhase() == Game.RESTORATION)
                     return game.getCurrentPlayerInjuredUnitPositions();
                           
                }
                break;
                }
                case Card.UNIT:
                {
                    if(game.getPhase() == Game.RESTORATION)
                    {
                       ArrayList<Position>  positions = new ArrayList<>();
                       positions.add(game.getCurrentPlayerUnitByName(playingCard.getCardName()).getPosition());
                       return positions;
                    }  
                break;
                }    
                
                case Card.LEADER:
                {
                    if(game.getPhase() == Game.RESTORATION)
                     return game.getCurrentPlayerInjuredUnitPositions();
                break;        
                }    
                
                    
                
            }
                
        }
    return null;
    
    
    
    }
protected void showConfirmationCardDialog(CommandQueue cmdQueue, Game game){
    /*
        Confirmation dialog
        */
       // game.lockGUI();
        CustomDialog dialog = 
                new CustomDialog(CustomDialog.YES_NO_TYPE, 
                        "Are You sure to play that card? " ,
                        cmdQueue, game);
        try {
            dialog.setOkCommand(game.getCardCommandFactory().createCardCommand());
            dialog.setCancelCommand(game.getCardCommandFactory().resetFactoryCommand());
            
            //dialog.setCancelCommand(moveUnit);
        } catch (Exception ex) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        dialog.setVisible(true);
        
    }
protected void showCannotPlayCardDialog(CommandQueue cmdQueue, Game game){
    /*
        Confirmation dialog
        */
        CustomDialog dialog = 
                new CustomDialog(CustomDialog.CONFIRMATION_TYPE, 
                        "You cannot play this card",
                        cmdQueue, game);
        dialog.setVisible(true);
          LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
        game.getCardCommandFactory().resetFactory();
        

}
 protected void showCardNoValidTargetDialog(CommandQueue cmdQueue, Game game){
    /*
        Confirmation dialog
        */
        CustomDialog dialog = 
                new CustomDialog(CustomDialog.CONFIRMATION_TYPE, 
                        "This card doesn't have valid target",
                        cmdQueue, game);
        dialog.setVisible(true);
          LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
        game.getCardCommandFactory().resetFactory();
        
    }
}
