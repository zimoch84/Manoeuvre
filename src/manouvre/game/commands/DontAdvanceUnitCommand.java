/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.Game;
import manouvre.game.Param;
import manouvre.game.Position;
import manouvre.game.Unit;
import manouvre.game.interfaces.Command;

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
        
        
    }

    @Override
    public void undo(Game game) {
        
    }

   @Override
    public int getType() {
        return Param.MOVE_UNIT;
    }
}
