/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;
import java.io.IOException;
import manouvre.game.Game;

/**
 *
 * @author Bartosz
 */
public class GameGUI {
    
    Game game;
    MapGUI mapGui; 
    
    public GameGUI (Game newGame) throws IOException{
        this.game=newGame;
        this.mapGui = new MapGUI(game.getMap());
        
    }
   
    public Game getGame() {
        return game;
    }

    public MapGUI getMapGui() {
        return mapGui;
    }
    
}
