/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.io.Serializable;
import java.util.ArrayList;
import manouvre.commands.AdvanceUnitCommand;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.commands.CommandQueue;
import manouvre.game.CardCommandFactory;
import manouvre.game.Combat;
import manouvre.game.Unit;
import manouvre.interfaces.Command;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author xeon
 */
public class MapPickAvalibleUnitState implements MapState, Serializable{
    
    
     private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MapPickAvalibleUnitState.class.getName());
     
    @Override
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue, MapInputStateHandler handler) {
        ArrayList<Position> avalaiblePositions =  game.getCurrentPlayerAvalibleUnitToSelect();
                    
        if(avalaiblePositions.contains(pos))
            
        {
            if(handler.unitSelectionMode == MapInputStateHandler.PICK_ONE_UNIT)
            {
                LOGGER.debug(game.getCurrentPlayer().getName() + "game.unselectAllUnits()" );
                game.unselectAllUnits();
            }
            
            Unit pickedUnit = game.getCurrentPlayerUnitAtPosition(pos);
            pickedUnit.setSelected(true);
        
            //if(game.getCardCommandFactory().getPlayingCard() == null) 
        
            //{
            if(game.getPhase() == Game.SETUP || game.getPhase() == Game.MOVE )
            {
                LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_MOVE_POSITION" );
                handler.setState(MapInputStateHandler.PICK_MOVE_POSITION);
            }   
            
            if(game.getPhase() == Game.COMBAT)
            {
            Combat combat = game.getCombat() ;
            if(combat != null)
                /*
                If its withdraw then select attacking unit position
                */
                if(combat.getState() == Combat.PURSUIT) 
                {
                    /*
                    Chosen unit to advance
                    */
                    pickedUnit.setAdvanced(true);
                    Command advanceCommand = 
                        new AdvanceUnitCommand(game.getCurrentPlayer().getName(),
                                pickedUnit , 
                                game.getCombat().getDefendingUnit().getPosition(),
                                game.getCombat().getPursuitCards(game)
                                );

                    cmdQueue.storeAndExecuteAndSend(advanceCommand);
                    game.getCardCommandFactory().awakeObserver();
                    game.getCardCommandFactory().notifyObservers(CardCommandFactory.PICKED_ADVANCE);
            }
            /* }
            else  
            {
                LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_UNIT_BY_CARD" );
                handler.setState(MapInputStateHandler.PICK_UNIT_BY_CARD);
            }
            */
        }   
        }       
        /*
        We dont hit into matching position
        */
        else
        {   
            if(game.getPhase() == Game.SETUP || game.getPhase() == Game.MOVE )
            {
            LOGGER.debug(game.getCurrentPlayer().getName() + " game.unselectAllUnits()" );
            game.unselectAllUnits();
            }
        }    
   }
   
}
