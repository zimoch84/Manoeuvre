/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.interfaces.Command;

/**
 *
 * @author piotr_grudzien
 */
public class DontAdvanceUnitCommand implements Command  {
    
    String playerName;
    
    
    public DontAdvanceUnitCommand(String playerName) {
     this.playerName= playerName;
    }
    
    @Override
    public String logCommand(){
        return new String(playerName + " has not advanced after attack");  
    }

    @Override
    public void execute(Game game) {
        /*
        Ends combat
        */
        game.checkCommittedAttackandEndCombat();
    }

    @Override
    public void undo(Game game) {
        
    }

   @Override
    public String getType() {
        return Command.MOVE_UNIT;
    }
}
