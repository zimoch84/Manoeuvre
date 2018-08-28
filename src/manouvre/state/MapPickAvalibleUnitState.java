/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import manouvre.interfaces.MapState;
import java.io.Serializable;
import java.util.ArrayList;
import manouvre.commands.AdvanceUnitCommand;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.commands.CommandQueue;
import manouvre.events.EventType;
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
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue, MapStateHandler handler) {
        ArrayList<Position> avalaiblePositions =  game.positionCalculator.getCurrentPlayerAvalibleUnitToSelect();
               
        if(avalaiblePositions.contains(pos))
            
        {
            if(handler.unitSelectionMode == MapStateHandler.PICK_ONE_UNIT)
            {
                
                game.unselectAllUnits();
            }
            
            Unit pickedUnit = game.getCurrentPlayerUnitAtPosition(pos);
            pickedUnit.setSelected(true);
        
            //if(game.getCardCommandFactory().getPlayingCard() == null) 
        
            //{
            if(game.getPhase() == Game.SETUP || game.getPhase() == Game.MOVE )
            {
                
                handler.setState(MapStateHandler.PICK_MOVE_POSITION);
            }   
            
            if(game.getPhase() == Game.COMBAT)
            {
            Combat combat = game.getCombat() ;
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
                            game.getCombat().getPursuitCards(pickedUnit)
                            );

                cmdQueue.storeAndExecuteAndSend(advanceCommand);

                game.notifyAbout(EventType.PICKED_ADVANCE);
            }
           
        }   
        }       
        /*
        We dont hit into matching position
        */
        else
        {   
            if(game.getPhase() == Game.SETUP || game.getPhase() == Game.MOVE )
            {
            
            game.unselectAllUnits();
            }
        }    
   }
    @Override
    public String toString() {
        return MapStateHandler.PICK_ONE_UNIT;
    }
    
    
}
