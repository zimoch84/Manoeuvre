/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.ui;

import manouvre.game.Map;
import manouvre.game.interfaces.PositionInterface;

/**
 *
 * @author Piotr
 */
public class MapGUI {
    
    private Map map;
    
     public static final int BOARD_START_X = 0;
     public static final int BOARD_START_Y = 0;

     public static final int SQUARE_WIDTH = 205;
     public static final int SQUARE_HEIGHT = 205;

     public static final int PIECE_WIDTH = 205;
     public static final int PIECE_HEIGHT = 205;

     public static final int PIECES_START_X = BOARD_START_X + (int)(SQUARE_WIDTH/2.0 - PIECE_WIDTH/2.0);
     public static final int PIECES_START_Y = BOARD_START_Y + (int)(SQUARE_HEIGHT/2.0 - PIECE_HEIGHT/2.0);
    
    public void MapGUI(){
    
        this.map.generateMap();
    
    }
    
    
    
    
    
}
