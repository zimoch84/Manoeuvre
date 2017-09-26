/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.io.Serializable;
import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;
import manouvre.game.commands.CardCommands;
import manouvre.game.commands.CommandQueue;
import manouvre.game.commands.MoveUnitCommand;
import manouvre.gui.CustomDialog;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author xeon
 */
public class MapPickUnitMovePositionState implements MapState, Serializable{
private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MapPickAvalibleUnitState.class.getName());   

    @Override
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue , MapInputStateHandler handler) {
       
        Unit selectedUnit = game.getSelectedUnit();
        
        
        if(game.getCurrentPlayer().hasMoved() && ! game.freeMove){
        CustomDialog cd = new CustomDialog(CustomDialog.CONFIRMATION_TYPE, "You have moved already, \n play card or proceed to next phase");
        cd.setVisible(true);
        
        LOGGER.debug(game.getCurrentPlayer().getName() + " game.unselectAllUnits()" );
        game.unselectAllUnits();
        LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT" );
        handler.setState(MapInputStateHandler.PICK_ONE_UNIT);
        return;
        }
        
        
        ArrayList<Position> avalaiblePositions =  game.getCurrentPlayerAvalibleMoveUnitPositions();

        if(avalaiblePositions.contains(pos))
        {
           MoveUnitCommand moveUnit = new MoveUnitCommand(game.getCurrentPlayer().getName() , selectedUnit,  pos);
            /*
             If we done play card and we are not in setup
           */
            if(game.getPhase() == Game.MOVE )
            {        
            cmdQueue.storeAndExecuteAndSend(moveUnit);
            LOGGER.debug(game.getCurrentPlayer().getName() + " game.unselectAllUnits()" );
            game.unselectAllUnits(); 
            LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT" );
            handler.setState(MapInputStateHandler.PICK_ONE_UNIT);
            }
            /*
            If we dont play card or we are in setup
            */
            if(game.getPhase() == Game.SETUP)
            {   
            /*
            Just execute on client
            */
            cmdQueue.storeAndExecute(moveUnit);
            LOGGER.debug(game.getCurrentPlayer().getName() + " game.unselectAllUnits()" );
            game.unselectAllUnits();  
            LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT" );
            handler.setState(MapInputStateHandler.PICK_ONE_UNIT);
            }
            if(game.getPhase() == Game.COMBAT)
            {
                if(selectedUnit.isRetriving()){
                    
                    
                    CardCommands.WithrdawCommand withdrawCommand = new CardCommands.WithrdawCommand(
                            moveUnit , game.getCardCommandFactory().getPlayingCard(), game.getCurrentPlayer().getName() );

                    cmdQueue.storeAndExecuteAndSend(withdrawCommand);

                    LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.NOSELECTION" );
                    handler.setState(MapInputStateHandler.NOSELECTION);
                }
    
            }
        }
            
        else
        {
            if(game.getPhase() != Game.COMBAT)
            {
            LOGGER.debug(game.getCurrentPlayer().getName() + " game.unselectAllUnits()" );
            game.unselectAllUnits();
            LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT" );
            handler.setState(MapInputStateHandler.PICK_ONE_UNIT);
            }
            
            else 
            {
            //Do nothing
            }
        }
            
            

    }

    
}
