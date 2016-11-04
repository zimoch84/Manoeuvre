/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import static manouvre.game.interfaces.PositionInterface.COLUMN_H;
import static manouvre.game.interfaces.PositionInterface.ROW_8;
import manouvre.gui.CreateRoomWindow;
import manouvre.gui.MapGUI;

/**
 *
 * @author Piotr
 */
public final class Game implements Serializable{
    
    private static final long serialVersionUID = 42321L;
    /*
    Game phases
    */
    public static final int SETUP = -1;
    public static final int DISCARD = 0;
    public static final int DRAW = 1;
    public static final int MOVE = 2;
    public static final int COMBAT = 3;
    public static final int RESTORATION = 4;
       
    
    Map map;    

    //ArrayList<Unit> units = new ArrayList<>();

    int turn;
        
    private Player currentPlayer, opponentPlayer;
    private Player hostPlayer;
    private Player guestPlayer;
    boolean isServer=true;  //if this will not change game is set on Server
    int phase; 

  
    public Game(Player newPlayer) throws IOException {
        this.currentPlayer = newPlayer;
        
        //All this here is TEMP and should be deleted when using "correct" creation of Player (thru server)
       
        currentPlayer.setNation(1);  //TEMP
        currentPlayer.setCards();  //TEMP
        currentPlayer.generateUnits(); //TEMP
        
      
      
        generateMap(); //TEMP
        placeUnitsOnMap(newPlayer);
        
        //-----------------------------------------
    }
    
    public Game(ArrayList<Player> players) {
        this.hostPlayer = players.get(0);
        this.guestPlayer = players.get(1);
        
        hostPlayer.setHost(true);
        hostPlayer.setCards();  
        hostPlayer.generateUnits(); 
        
        guestPlayer.setHost(false);
        guestPlayer.setCards();  
        guestPlayer.generateUnits(); 
                
        generateMap(); 
        
        placeUnitsOnMap(hostPlayer);
        placeUnitsOnMap(guestPlayer);
        
        setPhase(Game.SETUP);
    }
     
    
    public ArrayList<Player> getPlayers() {
        
        ArrayList<Player> players = new ArrayList<>();
        
        players.add(hostPlayer);
        players.add(guestPlayer);
        
        return players;
        
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOpponentPlayer() {
        return opponentPlayer;
    }
    
    public Player getPlayerByName(String playerName) {
        Player tempPlayer=new Player("EMPTY PLAYER");
        if (hostPlayer.getName().equals(playerName))
            tempPlayer=hostPlayer;
        if (guestPlayer.getName().equals(playerName))
            tempPlayer=guestPlayer;
        return tempPlayer;
    }
    

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        isServer=false;
    }

    public boolean isServer() {
        return isServer;
    }
    
    
    
    public void setCurrentPlayer(int windowMode) {
        switch(windowMode)
        {
            case CreateRoomWindow.AS_HOST:
                currentPlayer = hostPlayer;
                opponentPlayer = guestPlayer;
                break;
            case CreateRoomWindow.AS_GUEST:
                currentPlayer = guestPlayer;
                opponentPlayer = hostPlayer; 
                break;
        }       
    }

    public Player getHostPlayer() {
        return hostPlayer;
    }

    public Player getGuestPlayer() {
        return guestPlayer;
    }
    
    

    
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
                
              if (map.getTerrainAtXY(unitPosition.getX()-1, unitPosition.getY()).isPassable()
                      )
                        moves.add(new Position(unitPosition.getX()-1, unitPosition.getY()));
          }
          if (unitPosition.getY()-1 >= 0){      
              if (map.getTerrainAtXY(unitPosition.getX(), unitPosition.getY()-1).isPassable()
                      
                      )
                moves.add(new Position(unitPosition.getX(), unitPosition.getY()-1 ));
          }
          if (unitPosition.getY()+1 <= ROW_8){
              if (map.getTerrainAtXY(unitPosition.getX(), unitPosition.getY()+1).isPassable()
                     
                      )
                                moves.add(new Position(unitPosition.getX(), unitPosition.getY()+1));
          }
          if (unitPosition.getX()+1 <= COLUMN_H){
              if (map.getTerrainAtXY(unitPosition.getX()+1, unitPosition.getY()).isPassable()
                     
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
                if(! map.getTerrainAtXY(move.getX(), move.getY()).isEndsMove() ){              
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
     
     void placeUnitsOnMap(Player player){
     
         for(Unit unit: player.getArmy())
         
             map.getTerrainAtXY(unit.getPos().getX(), unit.getPos().getY()).setIsOccupiedByUnit(true);
         
     }
    /**
     * Places unit on map
     * @param player
     * @param unit
     * @param placePosition 
     *
     */
    public void placeUnit(Player player, Unit unit){
    
      map.getTerrainAtXY(unit.getPos().getX(), unit.getPos().getY()).setIsOccupiedByUnit(true);
      
    
    }
    
    public void moveUnit(Unit unit, Position newPosition){
    
      map.getTerrainAtXY(unit.getPos().getX(), unit.getPos().getY()).setIsOccupiedByUnit(false);
      map.getTerrainAtXY(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(true);

      getCurrentPlayerUnitAtPosition(unit.getPos()).setPos(newPosition);
      
      
          
    }
    
    public Unit searchUnit(Unit unit){
    
        for(Unit unitSearch : hostPlayer.getArmy())
        {if(unitSearch.equals(unit))
            {
                return unitSearch;
              }
        }
        for(Unit unitSearch : guestPlayer.getArmy())
        {   if(unitSearch.equals(unit))
            {
                return unitSearch;
              }
        }
       throw new NullPointerException() ;
             
    }
    
    public Unit getCurrentPlayerUnitAtPosition(Position position){
    
        for(Unit unitSearch: currentPlayer.getArmy()){
        
            if(unitSearch.getPos().equals(position))
            {
                return unitSearch;
              }
            
        
        }
              
        return null;
        
     
    }
    
    public boolean checkCurrentPlayerUnitAtPosition(Position position){
    
        for(Unit unitSearch: currentPlayer.getArmy()){
        
            if(unitSearch.getPos().equals(position))
            {
                return true;
              }
            
        
        }
              
        return false;
        
     
    }
    //-----------phases-----------
//   SETUP = -1    
//   DISCARD = 0;
//   DRAW = 1;
//   MOVE = 2;
//   COMBAT = 3;
//   RESTORATION = 4;
       
    public int getPhase() {
        return phase;
    }

    public void setPhase(int phase) {
        this.phase = phase;
    } 
    public void nextPhase() {
          
        if(phase<4) phase++;
        else phase=0;
        setCardsInHandAsPlayableDueToPhase();
    } 
   
    public void setCardsInHandAsPlayableDueToPhase(){
        for(int i=0; i<getCurrentPlayer().getHand().cardsLeftInSet(); i++){
            currentPlayer.setHandPlayableByPhaseAndPosition(i, phase);    
         }
    }
    
    public String toString(){
    
         return "Host Player:" + ( hostPlayer != null ? hostPlayer.toString() : "null") 
              + " Guest Player:"  + ( guestPlayer != null ? guestPlayer.toString() : "null")
                 + " Map: " +  map.toString();
            
    }
    
}

