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
import manouvre.gui.CustomDialogFactory;
import manouvre.gui.GameWindow;
import manouvre.interfaces.CardCommand;
import manouvre.interfaces.Command;
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

        ArrayList<Position> possibleMove = game.positionCalculator.getMovePositionsByCard(playingCard, selectedUnit);
        if(possibleMove.contains(pos))
        {
            MoveUnitCommand moveUnit = new MoveUnitCommand(game.getCurrentPlayer().getName() , selectedUnit,  pos);
            /*
            We attach move command to wrap it to postpone execution in card command
            */
            ccf.setAttachedCommand(moveUnit);
            Command cardCommand = ccf.createCardCommand(playingCard);
            /*
            Confirmation dialog
            */
            cmdQueue.storeAndExecuteAndSend(cardCommand);
            //CustomDialogFactory.showConfirmationCardDialog(cmdQueue, cardCommand, game);
            LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapInputStateHandler.NOSELECTION");
            handler.setState(MapStateHandler.NOSELECTION);
        }
               
    }

 @Override
    public String toString() {
        return MapStateHandler.PICK_MOVE_POSITION_BY_CARD;
    }
}