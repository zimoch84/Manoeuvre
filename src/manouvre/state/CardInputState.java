/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.awt.Graphics;
import manouvre.game.Card;
import manouvre.game.Game;


/**
 *
 * @author xeon
 */
public interface CardInputState {
    
    public void handleInput(Card pos,Game game);
    
}
