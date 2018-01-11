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
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.commands.CommandQueue;
import manouvre.game.Card;
import manouvre.game.Combat;
import manouvre.game.Unit;
import manouvre.gui.CustomDialog;
import manouvre.gui.GameWindow;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author xeon
 */
public class MapMultiPickUnitState implements MapState, Serializable{
private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MapMultiPickUnitState.class.getName());     
    @Override
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue , MapInputStateHandler handler) {
       
          ArrayList<Position> avalaiblePositions =  game.getCurrentPlayerAvalibleUnitToSelect();
                    
        if(avalaiblePositions.contains(pos))
        {
        
            if(game.getCombat()!=null)
                if(game.getCombat().getState() == Combat.PICK_SUPPORT_UNIT)
                {
                      Card leader = game.getCombat().getSupportingLeader();
                      int maxSupporters = leader.getLederCommand() - 1; 
                      int currentSupporters = game.getCombat().getSupportUnitCount();
                  
                      Unit supportingUnit =  game.getUnitAtPosition(pos);
                      
                      if(!supportingUnit.isSupporting() && maxSupporters > currentSupporters)
                      {
                          supportingUnit.setSupporting(true);
                          game.getCombat().addSupportUnit(supportingUnit);
                      }
                      
                      else if(supportingUnit.isSupporting())
                      {
                          supportingUnit.setSupporting(false);
                          game.getCombat().removeSupportUnit(supportingUnit);
                      }
                      else if (!supportingUnit.isSupporting() && maxSupporters <=  currentSupporters)
                      {
                          game.setInfoBarText("You can only pick up to " + maxSupporters + " units");
                      }
                      
                
                }
            
        }
                    
                    
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
        LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
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
          LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
        game.getCardCommandFactory().resetFactory();
        
    }
}
