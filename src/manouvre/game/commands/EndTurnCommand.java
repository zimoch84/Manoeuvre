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
public class EndTurnCommand implements Command{

    
    String activePlayerName;
    int phase;
    public EndTurnCommand(String playerName) {
        activePlayerName = playerName;
    
    }
    
    @Override
    public void execute(Game game) {
        game.setPhase(game.getPhase() +1);
        game.setPhase(Game.DISCARD);
        game.getPlayerByName(activePlayerName).resetPlayer();
        game.swapActivePlayer();
    
        
    }

    @Override
    public void undo(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String logCommand() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getType() {
       return Command.END_TURN;
    }
    
}
