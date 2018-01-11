/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.game.Game;
import manouvre.interfaces.Command;

/**
 *
 * @author Piotr
 */
public class EndSetupCommand implements Command{

    String playerName;
    Command setupCommand;
    
    public EndSetupCommand(String playerName, SetupPositionCommand setupCommand) {

    this.playerName = playerName;
    this.setupCommand = setupCommand;

    }

    @Override
    public void execute(Game game) {
        
        game.getPlayerByName(playerName).setFinishedSetup(true);
        game.getPlayerByName(playerName).resetPlayer();
        
        /*
        If both players finished advance game phase
        */
        if (game.getCurrentPlayer().isFinishedSetup() && game.getOpponentPlayer().isFinishedSetup())
            {     
                game.setPhase(game.getPhase()+1);
                
               
            }
        
        setupCommand.execute(game);
        
        
        
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

    @Override
    public String getType() {
        return Command.END_SETUP;
    }
    
    
    
}
