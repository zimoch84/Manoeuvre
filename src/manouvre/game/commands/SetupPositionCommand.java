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
    String playerName;
  
    
    public SetupPositionCommand(String playerName, ArrayList<Unit> units ) {
    
        this.units = units;
        this.playerName = playerName;
        
    }
   
    @Override
    public void execute(Game game) {
       
        for(Unit searchUnit: units)
        {
            game.searchUnit(searchUnit).move(searchUnit.getPosition());
            
            /*
            To clear has moved and other flags
            */
            game.getCurrentPlayer().resetPlayer();
        }
        
        game.getPlayerByName(playerName).setFinishedSetup(true);
              
        
    }

    @Override
    public void undo(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 
    @Override
    public String logCommand(){
        return new String(playerName + " send his army setup position");
    
    }

    @Override
    public int getType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
