/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import manouvre.interfaces.MapState;
import java.io.Serializable;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.commands.CommandQueue;

/**
 *
 * @author piotr_grudzien
 */
public class MapPickNoneState  implements MapState, Serializable{

    @Override
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue, MapStateHandler handler) {
       
        /*
        Do nothing
        */
    }
    
}
