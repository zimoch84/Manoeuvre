/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import manouvre.game.interfaces.Command;


public class MoveUnitCommand implements Command{

    int x,y,lastx, lasty;
    Position newPosition, lastPosition;

    Unit unit;
    Map map;
	
    public MoveUnitCommand(Unit unit, Map map,  Position newPosition) {
	this.unit = unit;
        this.newPosition = newPosition;
        this.map = map;

	}

    @Override
    public void execute() {
        
        lastPosition = unit.getPos() ;
        unit.move(newPosition);
         
      map.getTileAtIndex(lastPosition.getX(), lastPosition.getY()).setIsOccupiedByUnit(false);
      map.getTileAtIndex(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(true);
        
        
    }
    
    @Override
    public void undo(){
      
      unit.move(lastPosition);
        
      map.getTileAtIndex(lastPosition.getX(), lastPosition.getY()).setIsOccupiedByUnit(true);
      map.getTileAtIndex(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(false);
      
    }


}
