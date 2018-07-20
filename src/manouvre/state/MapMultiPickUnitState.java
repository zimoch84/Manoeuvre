/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import manouvre.interfaces.MapState;
import java.io.Serializable;
import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.commands.CommandQueue;
import manouvre.game.Card;
import manouvre.game.Combat;
import manouvre.game.Unit;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author xeon
 */
public class MapMultiPickUnitState implements MapState, Serializable{
private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MapMultiPickUnitState.class.getName());     
    @Override
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue , MapStateHandler handler) {
       
          ArrayList<Position> avalaiblePositions =  game.positionCalculator.getCurrentPlayerAvalibleUnitToSelect();
                    
        if(avalaiblePositions.contains(pos))
        {
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
 

}
