/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server.commands;

import manouvre.game.Game;
import manouvre.interfaces.Command;
import manouvre.network.server.GameServer;

/**
 *
 * @author Piotr
 */
public abstract class ServerCommand implements Command{
 
    
    abstract void execute(GameServer server);

    @Override
    public void execute(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void undo(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    
}
