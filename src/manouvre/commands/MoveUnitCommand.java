/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.events.EventType;
import manouvre.game.Game;
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
        Unit movingUnit =  game.getUnit(storedUnit);
        /*
        Move unit on game 
        */
        movingUnit.move(newPosition);
        
        game.getPlayerByName(playerName).setLastMovedUnit(movingUnit);
        /*
        Setting map on current game object to set occupation on it.
        */ 
        game.getMap().getTerrainAtXY(lastPosition.getX(), lastPosition.getY()).setIsOccupiedByUnit(false);
        game.getMap().getTerrainAtXY(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(true);
        if(game.getMap().getTerrainAtXY(lastPosition.getX(), lastPosition.getY()).isRedoubt())
            game.getMap().getTerrainAtXY(lastPosition.getX(), lastPosition.getY()).setRedoubt(false);
        game.unselectAllUnits();
        
        if (game.getPhase()== Game.MOVE && !game.getPlayerByName(playerName).hasMoved() )
        {
            game.getPlayerByName(playerName).setMoved(true);
            game.notifyAbout(EventType.PLAYER_MOVED);
        }
        
    }
    
    @Override
    public void undo(Game game){
      Unit movingUnit =  game.getUnit(storedUnit);
      movingUnit.move(lastPosition);
      game.getMap().getTerrainAtXY(lastPosition.getX(), lastPosition.getY()).setIsOccupiedByUnit(true);
      game.getMap().getTerrainAtXY(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(false);
      game.getPlayerByName(playerName).setMoved(false);  
      
      
    }
    @Override
    public String logCommand(){
        return new String(playerName + " has moved unit " + storedUnit.getName() + " to position " + newPosition.toString());  
    }

    @Override
    public Type getType() {
        return Command.Type.MOVE_UNIT;
    }
}
