/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.interfaces;

import manouvre.game.Game;

/**
 *
 * @author Bartosz
 */
public interface CardCommandInterface extends Command{
    public void cancel(Game game);
}
