/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Unit;
import manouvre.game.interfaces.Command;

/**
 *
 * @author Piotr
 * Sets up player army posision 
 * Set flag setupFinished
 *       
 */
public class SetupPositionCommand implements Command{

    ArrayList<Unit> units;
    
  
    
    public SetupPositionCommand(ArrayList<Unit> units ) {
    
        this.units = units;
        
    }
   
    @Override
    public void execute(Game game) {
       
        for(Unit searchUnit: units)
        {
            game.searchUnit(searchUnit).move(searchUnit.getPos());
        }
        
        game.getOpponentPlayer().setFinishedSetup(true);
              
        
    }

    @Override
    public void undo(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
