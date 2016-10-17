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
    public static int SETUP = -1;
    public static int DISCARD = 0;
    public static int DRAW = 1;
    public static int MOVE = 2;
    public static int COMBAT = 3;
    public static int RESTORATION = 4;
       
    
    Map map;    

    //ArrayList<Unit> units = new ArrayList<>();

    int turn;
        
    Player currentPlayer;
    public Player hostPlayer;
    public Player guestPlayer;
    
    int phase; 

   
    
    
    /*
    GUI variables
    */
    MapGUI mapGui ;
    
  
    public Game(Player newPlayer) throws IOException {
        this.currentPlayer = newPlayer;
        
        //All this here is TEMP and should be deleted when using "correct" creation of Player (thru server)
       
        currentPlayer.setNation(1);  //TEMP
        currentPlayer.setCards();  //TEMP
        currentPlayer.generateUnits(); //TEMP
        
        
        //System.out.println("GameWindowKrutki");
      
        generateMap(); //TEMP
        placeUnitsOnMap(newPlayer);
        
        //-----------------------------------------
    }
    
    public Game(ArrayList<Player> players) {
        this.hostPlayer = players.get(0);
        this.guestPlayer = players.get(1);
        
        
        hostPlayer.setCards();  
        hostPlayer.generateUnits(); 
        
        guestPlayer.setCards();  
        guestPlayer.generateUnits(); 
                
        generateMap(); 
        
        placeUnitsOnMap(hostPlayer);
        placeUnitsOnMap(guestPlayer);
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

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    
    public void setCurrentPlayer(int windowMode) {
        if (windowMode == CreateRoomWindow.AS_HOST)
            currentPlayer = hostPlayer;
        else 
            currentPlayer = guestPlayer;
        
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
     
     void placeUnitsOnMap(Player player){
     
         for(Unit unit: player.getArmy())
         
             map.getTileAtIndex(unit.getPos().getX(), unit.getPos().getY()).setIsOccupiedByUnit(true);
         
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
      
    
    }
    
    public void moveUnit(Unit unit, Position newPosition){
    
      map.getTileAtIndex(unit.getPos().getX(), unit.getPos().getY()).setIsOccupiedByUnit(false);
      map.getTileAtIndex(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(true);

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
        //all playable in discard
        //Supply and Forced March in Move Phase
    for(int i=0; i<getCurrentPlayer().getHand().cardsLeftInSet(); i++){
        getCurrentPlayer().getHand().getCardByPosInSet(i).setPlayable(false);
        if (getPhase()==0){
           getCurrentPlayer().getHand().getCardByPosInSet(i).setPlayable(true);
        }
        if (getPhase()==1){
                getCurrentPlayer().getHand().getCardByPosInSet(i).setPlayable(false);  
        } 
        if (getPhase()==2){
            if(getCurrentPlayer().getHand().getCardByPosInSet(i).getCardName().equals("Supply")||getCurrentPlayer().getHand().getCardNameByPosInSet(i).equals("Forced March")){
                getCurrentPlayer().getHand().getCardByPosInSet(i).setPlayable(true); 
            }
        } 
        if (getPhase()==3){
           getCurrentPlayer().getHand().getCardByPosInSet(i).setPlayable(true);  
           }
        if (getPhase()==4){
            if(getCurrentPlayer().getHand().getCardNameByPosInSet(i).equals("Supply")||
                   getCurrentPlayer().getHand().getCardNameByPosInSet(i).equals("Regroup")||
                   getCurrentPlayer().getHand().getCardTypeByPosInSet(i)==0||
                   getCurrentPlayer().getHand().getCardTypeByPosInSet(i)==2)
            {
                getCurrentPlayer().getHand().getCardByPosInSet(i).setPlayable(true);  
            }
           }
        } 
     }
    
    public String toString(){
    
         return "Host Player:" + ( hostPlayer != null ? hostPlayer.toString() : "null") 
              + " Guest Player:"  + ( guestPlayer != null ? guestPlayer.toString() : "null")
                 + " Map: " +  map.toString();
            
    }
    
}

