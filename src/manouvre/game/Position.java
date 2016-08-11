/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import manouvre.game.interfaces.PositionInterface;
import manouvre.ui.MapGUI;

/**
 *
 * @author Piotr
 */
public class Position implements PositionInterface{

    @Override
    public int[][] getRelativePosition() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
     public  int convertColumnToX(int column){
		return MapGUI.PIECES_START_X + MapGUI.SQUARE_WIDTH * column;
	}
	
	/**
	 * convert logical row into y coordinate
	 * @param row
	 * @return y coordinate for row
	 */
	public  int convertRowToY(int row){
		return MapGUI.PIECES_START_Y + MapGUI.SQUARE_HEIGHT * (PositionInterface.ROW_8 - row);
	}
        
        
}
