/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.Game;
import manouvre.game.Param;
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
        game.nextTurn();
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
       return new String(activePlayerName + " has ended his/her phase"  );
    }

    @Override
    public int getType() {
       return Param.END_TURN;
    }
    
}
