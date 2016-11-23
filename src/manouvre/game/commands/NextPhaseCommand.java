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
public class NextPhaseCommand implements Command{

    String activePlayerName;
    int phase;
    public NextPhaseCommand(String playerName, int phase) {
        activePlayerName = playerName;
    
    }
    
    
    
    @Override
    public void execute(Game game) {
        game.nextPhase();
        
    }

    @Override
    public void undo(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
