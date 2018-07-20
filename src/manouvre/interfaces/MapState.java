/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.interfaces;


import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.commands.CommandQueue;
import manouvre.state.MapStateHandler;

/**
 *
 * @author Piotr Grudzień
 */
public interface MapState {
    
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue, MapStateHandler handler );
    
}
