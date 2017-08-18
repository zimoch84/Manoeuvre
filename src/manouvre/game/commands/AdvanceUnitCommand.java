/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;

/**
 *
 * @author piotr_grudzien
 */
public class AdvanceUnitCommand extends MoveUnitCommand  {
    
    
    
    public AdvanceUnitCommand(String playerName, Unit unit, Position newPosition) {
        super(playerName, unit, newPosition);
    
    }
    
    public void execute(Game game)  {
            
            super.execute(game);
            /*
            Ends combat
            */
            game.setCombat(null);
            /*
            Clear retrieving and advancement
            */
            if(game.getCurrentPlayer().getName().equals(playerName))
                if(game.getAdvancedUnit() != null)
                   game.getAdvancedUnit().setAdvanced(false);
            else
                if(game.getRetrievedUnit() != null) 
                   game.getRetrievedUnit().setRetriving(false);
}
    
    @Override
    public String logCommand(){
        return new String(playerName + " has advanced unit" + storedUnit.getName() + " to position " + newPosition.toString());  
    }

}
