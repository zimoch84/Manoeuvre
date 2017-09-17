/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.CardCommandFactory;
import manouvre.game.Game;
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

    public TakeHitCommand(String playerName, Unit hitUnit) {
        this.playerName = playerName;
        this.hitUnit = hitUnit;
    }
    
    @Override
    public void execute(Game game) {
            
        Unit unit = game.getUnitByName(hitUnit.getName());
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
        
        game.swapActivePlayer();
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
