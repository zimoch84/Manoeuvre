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

/**
 *
 * @author piotr_grudzien
 */
public class TakeHitCommand implements Command{
    
    String playerName;
    Unit hitUnit;
    String log;
    boolean eliminate;

    public TakeHitCommand(String playerName, Unit hitUnit, boolean eliminate) {
        this.playerName = playerName;
        this.hitUnit = hitUnit;
        this.eliminate = eliminate;
    }
    
    @Override
    public void execute(Game game) {
            
        Unit unit = game.getUnitByName(hitUnit.getName());
        
        if(!eliminate) 
        {   
            unit.takeHit();
            if(  !unit.isEliminated())
            {
            game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_TAKES_HIT);
            log = "Combat ends with defending unit takes a hit";
            }
            else 
            {
             game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
             log = "Combat ends with defending unit takes a hit and is eliminated";
            }
        
        }
        else 
        {
         unit.eliminate();
         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
         log = "Combat ends with defending unit is eliminated";
        }
   
        if(game.getCombat().getState() == Combat.DEFENDER_DECIDES)
        {
            game.getCombat().setState(Combat.END_COMBAT);
            game.swapActivePlayer();
        }
        if(game.getCombat().getState() == Combat.ATTACKER_DECIDES)
        {
            
            game.getCombat().setState(Combat.END_COMBAT);
              }
    }

    @Override
    public void undo(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String logCommand() {
       return log;
    }

    @Override
    public int getType() {
        return Param.TAKE_HIT;
    }
    
    
    
}
