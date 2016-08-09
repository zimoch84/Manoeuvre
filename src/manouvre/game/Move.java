/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import manouvre.game.interfaces.MoveInterface;
import manouvre.game.interfaces.UnitInterface;

public class Move implements MoveInterface{
	public int sourceRow;
	public int sourceColumn;
	public int targetRow;
	public int targetColumn;
	
        public UnitInterface capturedPiece;
	
	public Move(UnitInterface unit, int sourceRow, int sourceColumn, int targetRow, int targetColumn) {
		
                this.capturedPiece = unit;          
                this.sourceRow = sourceRow;
		this.sourceColumn = sourceColumn;
		this.targetRow = targetRow;
		this.targetColumn = targetColumn;
	}
	
        


}
