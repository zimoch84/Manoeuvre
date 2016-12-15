/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.Game;
import manouvre.game.interfaces.Command;

/**
 *
 * @author Piotr
 */
public class EndSetupCommand implements Command{

    String playerName;
    
    public EndSetupCommand(String playerName) {

    this.playerName = playerName;

    }

    @Override
    public void execute(Game game) {
        
        game.getPlayerByName(playerName).setFinishedSetup(true);
        /*
        If both players finished advance game phase
        */
        if (game.getCurrentPlayer().isFinishedSetup() && game.getOpponentPlayer().isFinishedSetup())
            {       game.nextPhase(); 
        
            }
        
        
        
    }

    @Override
    public void undo(Game game) {
        game.getPlayerByName(playerName).setFinishedSetup(false);
        
    }
    
    @Override
    public String toString(){
    return "END_SETUP_COMMAND";
    
    }
    
    @Override
    public String logCommand(){
        return new String(playerName + " has finished setup");
    
    }
    
    
    
}
