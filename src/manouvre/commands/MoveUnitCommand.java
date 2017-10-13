/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.game.Game;
import manouvre.game.Param;
import manouvre.game.Position;
import manouvre.game.Unit;
import manouvre.interfaces.Command;


public class MoveUnitCommand implements Command {

    private static final long serialVersionUID = -1306760703066967345L;
    int x,y,lastx, lasty;
    Position newPosition, lastPosition;

    Unit storedUnit;
    
    String playerName;

    public MoveUnitCommand(String playerName, Unit unit,  Position newPosition) {
	this.storedUnit = unit;
        this.newPosition = newPosition;
        this.lastPosition = new Position (storedUnit.getPosition().getX(), storedUnit.getPosition().getY()) ;
        this.playerName = playerName;
       
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
       // lastPosition = new Position (storedUnit.getPosition().getX(), storedUnit.getPosition().getY()) ;
        /*
        Move unit on game 
        */
        movingUnit.move(newPosition);
        /*
        Setting map on current game object to set occupation on it.
        */ 
        
        game.getMap().getTerrainAtXY(lastPosition.getX(), lastPosition.getY()).setIsOccupiedByUnit(false);
        game.getMap().getTerrainAtXY(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(true);
        if(game.getMap().getTerrainAtXY(lastPosition.getX(), lastPosition.getY()).isRedoubt())
            game.getMap().getTerrainAtXY(lastPosition.getX(), lastPosition.getY()).setRedoubt(false);
        
        game.unselectAllUnits();
                
                
        if (game.getPhase()!=Game.SETUP)
        {
            game.getPlayerByName(playerName).setMoved(true);
        }
        
        
    }
    
    @Override
    public void undo(Game game){
      
    
        
      Unit movingUnit =  game.searchUnit(storedUnit);
      
      movingUnit.move(lastPosition);
        
      game.getMap().getTerrainAtXY(lastPosition.getX(), lastPosition.getY()).setIsOccupiedByUnit(true);
      game.getMap().getTerrainAtXY(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(false);
      
      game.getPlayerByName(playerName).setMoved(false);  //btestfalse undo should give setMoved(false) but it doesnt work with Force March and reject funcion. Not possible to end Move Phase
      
      
    }
    @Override
    public String logCommand(){
        return new String(playerName + " has moved unit " + storedUnit.getName() + " to position " + newPosition.toString());  
    }

    @Override
    public int getType() {
        return Param.MOVE_UNIT;
    }
}
