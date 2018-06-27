/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.events.EventType;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Unit;
import manouvre.interfaces.Command;
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
      LOGGER.debug(game.getCurrentPlayer().getName() + " swapActivePlayer " + game.getCurrentPlayer().isActive());
      game.getCombat().setState(Combat.WITHRDAW);
      game.getCardCommandFactory().notifyObservers(EventType.DEFENDER_WITHDRAW);
      
      if(game.getCurrentPlayer().isActive())
      {
          LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_MOVE_POSITION ");
          game.mapInputHandler.setState(MapInputStateHandler.PICK_MOVE_POSITION);
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
    public String getType() {
        return Command.FORCE_WITHDRAW;
    }
    
    
    
}
