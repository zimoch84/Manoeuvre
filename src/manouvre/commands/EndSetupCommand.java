/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.events.EventType;
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
        setupCommand.execute(game);
        game.notifyAbout(EventType.SETUP_FINISHED);
        
        /*
        If both players finished advance game phase
        */
        if (game.getCurrentPlayer().isFinishedSetup() && game.getOpponentPlayer().isFinishedSetup())
            {     
               Command nextPhaseCommand = new NextPhaseCommand(game.getCurrentPlayer().getName(), game.getPhase() + 1);
               nextPhaseCommand.execute(game);
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

    @Override
    public Type getType() {
        return Command.Type.END_SETUP;
    }
    
    
    
}
