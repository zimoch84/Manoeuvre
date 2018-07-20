/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.events.EventType;
import manouvre.game.Game;
import manouvre.interfaces.Command;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Piotr
 */
public class EndTurnCommand implements Command{

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(EndTurnCommand.class.getName());  
    String activePlayerName;
    int phase;
    public EndTurnCommand(String playerName) {
        activePlayerName = playerName;
    
    }
    
    @Override
    public void execute(Game game) {
        
        game.swapActivePlayer();
        LOGGER.debug(game.getCurrentPlayer().getName() + " swapActivePlayer " + game.getCurrentPlayer().isActive());
        game.nextTurn();
        game.setPhase(Game.DISCARD);
        game.getMap().unselectAllTerrains();
        game.getPlayerByName(activePlayerName).resetPlayer();
        game.notifyAbout(EventType.END_TURN);
        
    }

    @Override
    public void undo(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String logCommand() {
       return new String(activePlayerName + " has ended his/her phase"  );
    }

    @Override
    public String getType() {
       return Command.END_TURN;
    }
    
}
