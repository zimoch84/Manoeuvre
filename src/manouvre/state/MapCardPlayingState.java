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
import manouvre.game.commands.CommandQueue;
import manouvre.gui.CustomDialog;
import manouvre.gui.GameWindow;

/**
 *
 * @author xeon
 */
public class MapCardPlayingState implements MapState, Serializable{

    @Override
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue , MapInputStateHandler handler) {
       
                Card playingCard = game.getCardCommandFactory().getPlayingCard();
                switch (playingCard.getAtionType(game.getPhase())){
                
                case Card.MOVE_ACTION:
                {
                if(game.getSelectedUnit()!= null)
                {  
                Unit selectedUnit = game.getSelectedUnit();
                if(!getMovePositions(playingCard, game).isEmpty())
                    
                    handler.setState(MapInputStateHandler.PICK_MOVE_POSITION_BY_CARD);
                    /*
                    Deselect card and clear Card Factory
                    */
                else {
                        game.getCardCommandFactory().resetPlayingCard();
                        handler.setPreviousState();
                        
                    }
                } 
                /* 
                We have to select unit - SUPPLY CARD
                */
                else {
                    if(!getPossibleUnitPostionToSelect(game).isEmpty())
                         handler.setState(MapInputStateHandler.PICK_UNIT_BY_CARD);

                    }
                break;
                }
                case Card.PICK_ACTION:    
                {
                if(!getAvaliblePositionToSelect(game).isEmpty())    
                    if(getAvaliblePositionToSelect(game).contains(pos))
                    {
                        Unit attackedUnit = game.getOpponentPlayerUnitAtPosition(pos);
                        game.getCardCommandFactory().setAttackedUnit(attackedUnit);
                        showConfirmationCardDialog(cmdQueue, game);
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
                int availaibleUnits = getAvaliblePositionToSelect(game).size();
                
                if(availaibleUnits>0) 
                    {
                    int commandValue =game.getCardCommandFactory().getPlayingCard().getLederCommand();
                    
                    int maxSelections = ( commandValue > availaibleUnits ? availaibleUnits : commandValue );
                    /*
                    If we need to select more 
                    */
                    if(game.getNumberOfSupportingUnit() < maxSelections)
                    {    
                        if(getAvaliblePositionToSelect(game).contains(pos))
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
 protected ArrayList<Position> getAvaliblePositionToSelect(Game game)
    {
        if(game.getCurrentPlayer().isPlayingCard())
        {
            Card playingCard = game.getCardCommandFactory().getPlayingCard();
            switch(playingCard.getCardType()){
                case Card.HQCARD:
                {
                if(playingCard.getHQType() == Card.SUPPLY)
                {
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
                    {    if(playingCard.getPlayingCardMode() > 0  )
                        {
                             game.getCardCommandFactory().setSelectedUnit(game.getSelectedUnit());
                             game.getCardCommandFactory().calculateAttackingPositions();    
                             return  game.getCardCommandFactory().getAttackingPositions();

                        }
                    }
                    if (game.getPhase() == Game.RESTORATION)
                    {
                       return  getPossibleUnitPostionToSelect(game);
                    }
                
                }    
                
                case Card.HQLEADER:
                {
                    return game.getPossibleSupportingUnitsPositions(game.getCardCommandFactory().getAttackedUnit());
  
                }    
                    
                
            }
                
        }
    return null;
    
    }
protected ArrayList<Position> getPossibleUnitPostionToSelect(Game game){
    
     if(game.getCurrentPlayer().isPlayingCard())
        {
            Card playingCard = game.getCardCommandFactory().getPlayingCard();
            
            
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
                
                case Card.HQLEADER:
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
        game.getCardCommandFactory().resetFactory();
        
    }
}
