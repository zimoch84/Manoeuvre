/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;


import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.commands.CommandQueue;

/**
 *
 * @author Piotr Grudzień
 */
public interface MapState {
    
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue, MapInputStateHandler handler );
    
}
