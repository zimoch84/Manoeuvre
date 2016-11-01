/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;
import manouvre.game.interfaces.Command;


public class MoveUnitCommand implements Command {

    private static final long serialVersionUID = -1306760703066967345L;
    int x,y,lastx, lasty;
    Position newPosition, lastPosition;

    Unit storedUnit;

    public MoveUnitCommand(Unit unit,  Position newPosition) {
	this.storedUnit = unit;
        this.newPosition = newPosition;
       
	}

    @Override
    public void execute(Game game) {
        /*
        Searching reference to unit based on storeUnit.
        */
        Unit movingUnit =  game.searchUnit(storedUnit);
        /*
        Store last position to undo be passible
        */
        lastPosition = storedUnit.getPos() ;
        /*
        Move unit on game 
        */
        movingUnit.move(newPosition);
        /*
        Setting map on current game object to set occupation on it.
        */ 
        
        game.getMap().getTileAtIndex(lastPosition.getX(), lastPosition.getY()).setIsOccupiedByUnit(false);
        game.getMap().getTileAtIndex(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(true);
        
        
    }
    
    @Override
    public void undo(Game game){
      
    
        
      Unit movingUnit =  game.searchUnit(storedUnit);
      
      movingUnit.move(lastPosition);
        
      game.getMap().getTileAtIndex(lastPosition.getX(), lastPosition.getY()).setIsOccupiedByUnit(true);
      game.getMap().getTileAtIndex(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(false);
      
    }

}
