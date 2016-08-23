/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.util.ArrayList;
import static manouvre.game.interfaces.PositionInterface.COLUMN_H;
import static manouvre.game.interfaces.PositionInterface.ROW_8;

/**
 *
 * @author Piotr
 */
public class Game {
    
    /*
    Game phases
    */
    public static int DISCARD = 0;
    public static int DRAW = 1;
    public static int MOVE = 2;
    public static int COMBAT = 3;
    public static int RESTORATION = 4;
       
    
    Map map;

    ArrayList<Unit> units;

    int turn;
    
    public Game(ArrayList<Unit> units) {
        this.units = units;
    }
    public Game() {
        units = new ArrayList<>();
    }

    
    
    Player playerOne;
    Player playerTwo;
    
    int phase; 
    
    
    public ArrayList<Position> getPossibleBombard(Unit unit){
    return null;
    };
    
    public ArrayList<Position> getPossibleVolley(Unit unit){
        return null;
        
    };
     /**
         Firstly get adjenced tiles then check on terrain restrictions then check if another tile is occupied
     * @param unit
     * @return Position
     */ 
    private ArrayList<Position> getPossibleMovements(Position unitPosition){
        
        ArrayList<Position> moves;
        moves = new ArrayList<>();
            /*
            Firstly get adjenced tiles then check on terrain restrictions then check if another tile is occupied
            */ 
          if (unitPosition.getX()-1  >= 0 ) {
                
              if (map.getTileAtIndex(unitPosition.getX()-1, unitPosition.getY()).isPassable()
                      )
                        moves.add(new Position(unitPosition.getX()-1, unitPosition.getY()));
          }
          if (unitPosition.getY()-1 >= 0){      
              if (map.getTileAtIndex(unitPosition.getX(), unitPosition.getY()-1).isPassable()
                      
                      )
                moves.add(new Position(unitPosition.getX(), unitPosition.getY()-1 ));
          }
          if (unitPosition.getY()+1 <= ROW_8){
              if (map.getTileAtIndex(unitPosition.getX(), unitPosition.getY()+1).isPassable()
                     
                      )
                                moves.add(new Position(unitPosition.getX(), unitPosition.getY()+1));
          }
          if (unitPosition.getX()+1 <= COLUMN_H){
              if (map.getTileAtIndex(unitPosition.getX()+1, unitPosition.getY()).isPassable()
                     
                      )
              moves.add(new Position(unitPosition.getX()+1, unitPosition.getY()));
          }
            
        
        
        return moves;
    }
    public ArrayList<Position> getPossibleMovement(Unit unit){
        
        ArrayList<Position> moves;
          
        /*
        get Infantry Moves
        */
        moves = getPossibleMovements(unit.getPos());
       
        /*
        If calvary do check of every infantry move considering Terrain.MARSH which ends move
        */
        if(unit.type == Unit.CALVARY){
        
        ArrayList<Position> tempMoves;
        
        ArrayList<Position> tempMoves2 = new ArrayList<Position>();
        
            
        for(Position move : moves ){
        
                if(! map.getTileAtIndex(move.getX(), move.getY()).isEndsMove() ){
                
                    tempMoves = getPossibleMovements(move);
                    
                        for(Position addPosition: tempMoves){
                        
                            if (!moves.contains(addPosition) && !addPosition.equals(unit.getPos()))
                                    
                                
                                tempMoves2.add(addPosition);
                                    
                        }
      
                }            
            
            }
        moves.addAll(tempMoves2);
        
        }
        
        return moves;
        
        
    };
    
    public ArrayList<Position> getPossibleSupportingUnits(Unit unit){
    return null;
    };
    
    public ArrayList<Position> getRetreatPositions(Unit unit){
    return null;
    };
    
    
    public void generateMap(){
        this.map = new Map();
    }
     public Map getMap() {
        return map;
        
    }
    /**
     * Places unit on map
     * @param player
     * @param unit
     * @param placePosition 
     *
     */
    public void placeUnit(Player player, Unit unit){
    
      map.getTileAtIndex(unit.getPos().getX(), unit.getPos().getY()).setIsOccupiedByUnit(true);
      units.add(unit);
    
    }
    
    public void moveUnit(Unit unit, Position newPosition){
    
      map.getTileAtIndex(unit.getPos().getX(), unit.getPos().getY()).setIsOccupiedByUnit(false);
      map.getTileAtIndex(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(true);

      unit.setPos(newPosition);
      ;
          
    }
    
    public Unit getUnitAtPosition(Position position){
    
        for(Unit unitSearch: units){
        
            if(unitSearch.getPos().equals(position))
            {
                return unitSearch;
              }
            
        
        }
              
        return null;
        
     
    }
    
    public boolean checkUnitAtPosition(Position position){
    
        for(Unit unitSearch: units){
        
            if(unitSearch.getPos().equals(position))
            {
                return true;
              }
            
        
        }
              
        return false;
        
     
    }
    
        
    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }
}

