/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.interfaces;

import java.util.ArrayList;

/**
 *
 * @author Piotr
 */
public interface PositionInterface {
    
   
    	//eight rows (called ranks and denoted with numbers 1 to 8)
	//and eight columns (called files and denoted with letters a to h) of squares.
	
        //private int row;
	public static final int ROW_1 = 0;
	public static final int ROW_2 = 1;
	public static final int ROW_3 = 2;
	public static final int ROW_4 = 3;
	public static final int ROW_5 = 4;
	public static final int ROW_6 = 5;
	public static final int ROW_7 = 6;
	public static final int ROW_8 = 7;
	
	//private int column;
	
	public static final int COLUMN_A = 0;
	public static final int COLUMN_B = 1;
	public static final int COLUMN_C = 2;
	public static final int COLUMN_D = 3;
	public static final int COLUMN_E = 4;
	public static final int COLUMN_F = 5;
	public static final int COLUMN_G = 6;
	public static final int COLUMN_H = 7;
	
        
        abstract public int convertColumnToMouseX(int column);
	
	/**
	 * convert logical row into y coordinate
	 * @param row
	 * @return y coordinate for row
	 */
	abstract public int convertRowToMouseY(int row);
        
        
        /*
         Gets adjenced Posiotions
        */
        
        abstract public ArrayList<? extends PositionInterface> getAdjencedPositions();
        
        
        
	
    
}
