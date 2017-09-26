/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.CardCommandFactory;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Param;
import manouvre.game.Unit;
import manouvre.game.interfaces.Command;
import manouvre.state.CardStateHandler;
import manouvre.state.MapInputStateHandler;

/**
 *
 * @author piotr_grudzien
 */
public class ForceWithdraw implements Command{
    
    String playerName;
    Unit hitUnit;
    String log;

    public ForceWithdraw(String playerName, Unit hitUnit) {
        this.playerName = playerName;
        this.hitUnit = hitUnit;
      
    }
    
    @Override
    public void execute(Game game) {
            
      Unit unit = game.getUnitByName(hitUnit.getName());
      game.swapActivePlayer();
      game.getCombat().setState(Combat.WITHRDAW);
      
      game.mapInputHandler.setState(MapInputStateHandler.PICK_MOVE_POSITION);
      game.cardStateHandler.setState(CardStateHandler.NOSELECTION);
      unit.setRetriving(true);
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
