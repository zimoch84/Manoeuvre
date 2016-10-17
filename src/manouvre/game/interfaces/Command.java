/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.interfaces;

import manouvre.game.Game;

/**
 *
 * @author Piotr
 */
public interface Command {
    
    public void execute(Game game);
    public void undo(Game game);
}
