/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Param;
import manouvre.game.Unit;
import manouvre.game.interfaces.Command;
import manouvre.state.CardStateHandler;
import manouvre.state.MapInputStateHandler;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author piotr_grudzien
 */
public class ForceWithdraw implements Command{
    
    String playerName;
    Unit hitUnit;
    String log;
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ForceWithdraw.class.getName());

    public ForceWithdraw(String playerName, Unit hitUnit) {
        this.playerName = playerName;
        this.hitUnit = hitUnit;
      
    }
    
    @Override
    public void execute(Game game) {
            
      Unit unit = game.getUnit(hitUnit);
    
      game.swapActivePlayer();
      game.getCombat().setState(Combat.WITHRDAW);
      
      if(game.getCurrentPlayer().isActive())
      {
          LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT ");
          game.mapInputHandler.setState(MapInputStateHandler.PICK_ONE_UNIT);
      }
      else 
      { 
          LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.NOSELECTION ");
          game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
      }
      LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na CardStateHandler.NOSELECTION ");
      game.cardStateHandler.setState(CardStateHandler.NOSELECTION);
     
      unit.setRetriving(true);
      unit.setSelected(true);
      log = "Attacker forced defender to withdraw";

    }

    @Override
    public void undo(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String logCommand() {
       return log;
    }

    @Override
    public int getType() {
        return Param.FORCE_WITHDRAW;
    }
    
    
    
}
