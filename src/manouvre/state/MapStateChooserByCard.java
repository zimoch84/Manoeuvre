/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.io.Serializable;
import java.util.logging.Logger;
import manouvre.game.Card;
import manouvre.game.Game;
import manouvre.game.Unit;
import manouvre.commands.CommandQueue;
import manouvre.events.EventType;
import manouvre.gui.CustomDialog;
import manouvre.interfaces.Command;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author xeon
 */
public class MapStateChooserByCard implements Serializable{


    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MapStateChooserByCard.class.getName());
    
    public void setMapStateByCard(Card playingCard, Game game,  MapStateHandler handler)
    {
         switch (playingCard.getActionType()){
                case Card.MOVE_UNIT_ACTION:
                {
                Unit selectedUnit = game.getSelectedUnit();
                if(selectedUnit != null)
                    if(!game.positionCalculator.getMovePositionsByCard(playingCard).isEmpty())
                    {
                        handler.setStateByCard(playingCard, MapStateHandler.PICK_MOVE_POSITION_BY_CARD);
                    }   
                    else 
                    {
                        game.notifyAbout(EventType.CARD_THERE_IS_NO_ROOM_FOR_MOVE);
                        handler.setStateByCard(playingCard, MapStateHandler.NOSELECTION);
                    }
                else 
                    LOGGER.error("There is not unit selected");
                break;
                }
                case Card.PICK_UNIT_ACTION:    
                    if(!game.positionCalculator.getUnitsPositionToSelectByCard(playingCard).isEmpty())    
                            handler.setStateByCard(playingCard, MapStateHandler.PICK_UNIT_BY_CARD);
                        else 
                            showCannotPlayCardDialog();    
                    break;
                case Card.MULTIPLE_UNIT_PICK_ACTION:   
                    
                    handler.setStateByCard(playingCard, MapStateHandler.PICK_MULTIPLE_UNITS);
                break;
                
                case Card.NO_ACTION:
                    handler.setState(MapStateHandler.NOSELECTION);
                    break;
                }
                
        
    }
    
  
protected void showConfirmationCardDialog(CommandQueue cmdQueue, Command command, Game game){
         /*
        Confirmation dialog
        */
       // game.lockGUI();
        CustomDialog dialog = 
                new CustomDialog(CustomDialog.YES_NO_TYPE, 
                        "Are You sure to play that card? " ,
                        cmdQueue, game);
        try {
            dialog.setOkCommand(command);
            //dialog.setCancelCommand(moveUnit);
        } catch (Exception ex) {
            Logger.getLogger("Dialog has broken" + ex.toString());
        }
        dialog.setVisible(true);
        
    }
protected void showCannotPlayCardDialog(){
        /*
        Confirmation dialog
        */
        CustomDialog dialog = 
                new CustomDialog(CustomDialog.CONFIRMATION_TYPE, "You cannot play this card");
        dialog.setVisible(true);
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
          LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardHandler().resetFactory()");
        
        
    }
}
