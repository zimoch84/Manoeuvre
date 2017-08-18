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
import manouvre.game.CardCommandFactory;
import manouvre.game.Combat;
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
public class MapCardPickUnitState implements MapState, Serializable{

    @Override
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue , MapInputStateHandler handler) {
       
      
            if(!getPossibleUnitPostionToSelect(game).isEmpty())
                if(getPossibleUnitPostionToSelect(game).contains(pos))
                    {
                     Unit pickedUnit = game.getCurrentPlayerUnitAtPosition(pos);
                     
                     Card playingCard = game.getCardCommandFactory().getPlayingCard();
                            switch(playingCard.getCardType()){
                                case Card.UNIT:{
                                    
                                    Combat combat = game.getCombat() ;
                                    if(combat != null)
                                        /*
                                        If its withdraw then select attacking unit position
                                        */
                                        if(combat.getState() == Combat.WITHRDAW) 
                                        {
                                            pickedUnit.setSelected(true);
                                            /*
                                            Chosen unit to advance
                                            */
                                            pickedUnit.setAdvanced(true);
                                            game.getCardCommandFactory().awakeObserver();
                                            game.getCardCommandFactory().notifyObservers(CardCommandFactory.PICKED_ADVANCE);
                                            
                                            
                                            
                                        }
                                   
                                break;
                                } 
                            }
                    }
                    
                    
   }
    
 private ArrayList<Position> getPossibleUnitPostionToSelect(Game game){
    
     ArrayList<Position> returnPositions = new ArrayList<>();
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
                /*
                Defending player 
                */
                if(playingCard.getHQType() == Card.WITHDRAW)   
                    {
                           returnPositions.add(game.getCombat().getDefendingUnit().getPosition());
                           return returnPositions;
                        
                    }
                    
                    
                break;
                }
                case Card.UNIT:
                {
                    /*
                    Attacking player has unit set as playnig card
                    */
                    Combat combat = game.getCombat() ;
                    if(combat != null)
                        /*
                        If its withdraw then select attacking unit position
                        */
                        if(combat.getState() == Combat.WITHRDAW) 
                        {
                           returnPositions.add(combat.getAttackingUnit().getPosition());
                           return returnPositions;
                        }
                            
                    
                    
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
    return returnPositions;
}
 
private void showConfirmationCardDialog(CommandQueue cmdQueue, Game game){
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
private void showCannotPlayCardDialog(CommandQueue cmdQueue, Game game){
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
 private void showCardNoValidTargetDialog(CommandQueue cmdQueue, Game game){
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
