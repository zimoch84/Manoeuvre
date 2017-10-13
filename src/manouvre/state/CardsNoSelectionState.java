/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.awt.Graphics;
import java.io.Serializable;
import manouvre.game.Card;
import manouvre.game.Game;
import manouvre.commands.CommandQueue;

/**
 *
 * @author xeon
 */
public class CardsNoSelectionState implements CardInputState, Serializable{

    @Override
    public void handleInput(Card card, Game game, CommandQueue cmdQueue) {
            
        /*
        Do nothing
        */
        
    }

    @Override
    public String toString() {
        return "NOSELECTION";
    }
    
}
