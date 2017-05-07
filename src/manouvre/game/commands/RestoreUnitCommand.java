/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.Card;
import manouvre.game.Game;
import manouvre.game.Param;
import manouvre.game.Unit;
import manouvre.game.interfaces.Command;


public class RestoreUnitCommand implements Command {

    private static final long serialVersionUID = -1306760703066967345L;
    
    Card restorationCard ;
    Unit storedUnit;
    String playerName;

    public RestoreUnitCommand(String playerName, Unit unit,  Card restorationCard) {
	this.storedUnit = unit;
        this.restorationCard = restorationCard;
                this.playerName = playerName;
       
	}

    @Override
    public void execute(Game game) {
        
        
        game.getUnitByName(storedUnit.getName()).restoreUnit();
       
        game.unselectAllUnits();
                
                
        
        
    }
    
    @Override
    public void undo(Game game){
  
    }
    @Override
    public String logCommand(){
        return new String(playerName + " has has restored unit " + storedUnit.getName());  
    }

    @Override
    public int getType() {
        return Param.MOVE_UNIT;
    }
}
