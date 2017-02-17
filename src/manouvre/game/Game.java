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
import manouvre.gui.UnitGUI;

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
    int phase;
    
    private Player currentPlayer, opponentPlayer;
    private Player hostPlayer;
    private Player guestPlayer;
    boolean isServer=true;  //if this will not change game is set on Server
    

  
    public Game(Player newPlayer) throws IOException {
        this.currentPlayer = newPlayer;
        
        turn = 1;
        
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
        
        setFirstPlayer();
        this.turn=1;
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
      
        if (hostPlayer.getName().equals(playerName))
            return hostPlayer;
        if (guestPlayer.getName().equals(playerName))
            return guestPlayer;
        
        throw new NullPointerException() ;
        
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
    public ArrayList<Position> getOneSquareMovements(Position unitPosition){
        
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
    
    public void nextTurn(){
        turn++;
        }
    
    public ArrayList<Position> getPossibleMovement(Unit unit){      
        ArrayList<Position> moves;         
        /*
        get Infantry Moves
        */
        moves = getOneSquareMovements(unit.getPosition());
       
        /*
        If calvary do check of every infantry move considering Terrain.MARSH which ends move
        */
        if(unit.type == Unit.CALVARY){       
        ArrayList<Position> tempMoves;       
        ArrayList<Position> tempMoves2 = new ArrayList<Position>();
                    
        for(Position move : moves ){       
                if(! map.getTerrainAtXY(move.getX(), move.getY()).isEndsMove() ){              
                    tempMoves = getOneSquareMovements(move);                    
                        for(Position addPosition: tempMoves){                        
                            if (!moves.contains(addPosition) && !addPosition.equals(unit.getPosition()))                                                                  
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
        /*
        A Retreat result will force the affected unit to vacate its current square. A unit
        that Retreats is moved one square away from its current location. The choice of
        the direction of the Retreat must be directly towards the unit’s starting edge of
        the battlefield. If that square is blocked (by either friendly or enemy units), then
        the Retreat must be to either flank square, retreating player’s choice. A flank
        square is one not toward either side’s Starting Edge. If all three of these squares
        are blocked or are the map edge, then and only then the unit may retreat towards
        the enemy’s Starting Edge. If all four squares are blocked or are the map edge,
        then the unit may not retreat and is Eliminated instead.
        */
        ArrayList<Position> possibleMovements = getOneSquareMovements(unit.getPosition());
        /*
        If there is no room for movement return null
        */
        if (possibleMovements.isEmpty() ) return new ArrayList<>();
        
        ArrayList<Position> retreatMovements = new ArrayList<>();
        /*
        If player is a host we move increasing y else we decrease y 
        */
        int deltaMove =  unit.getOwner().isHost() ?  -1  : 1;
        /*
        Checking possible retreat to unit starting edge
        */
            for(Position checkRetreatPos : possibleMovements)
            {/*
                If we have space to move back
                */
             if(checkRetreatPos.getY() == unit.getPosition().getY() + deltaMove)  {
                 
                 retreatMovements.add(checkRetreatPos);
                 return retreatMovements;
             }
            }
            for(Position checkRetreatPos : possibleMovements)
            {
            /*
            If we have space to move aside
            */
             if(checkRetreatPos.getX() == unit.getPosition().getX() + 1
                     ||
                     checkRetreatPos.getX() == unit.getPosition().getX() - 1 )
             {
                 
                 retreatMovements.add(checkRetreatPos);
                
             }
            }
             /*
             If we have side way movements return them
             */
             if (!retreatMovements.isEmpty())  return retreatMovements;
             /*
            if we have possibleMovements not epmty and none of above is true then 
            possiblemovements contains final move up way
             */
             else return possibleMovements;
             
    }
    
    
    public ArrayList<Position> getSetupPossibleMovement()
    {     
        ArrayList<Position> moves;
        moves = new ArrayList<>();
                 
        
        int maxRow = getCurrentPlayer().isHost() ? Position.ROW_6 : Position.ROW_1;
            
        
        for(Terrain terrains: getMap().getTerrainz()){
        
            if(currentPlayer.isHost())
            {
                if(terrains.getPosition().getY() < Position.ROW_7 && terrains.isPassable()) 
                {
                    moves.add(terrains.getPosition());
                }
      
            }
            else 
                
                if(terrains.getPosition().getY() > Position.ROW_1 && terrains.isPassable() ) 
                {
                    moves.add(terrains.getPosition());
                }
                
                
        
        }
        
        return moves;
    }
    
    
    public void generateMap(){
        this.map = new Map();
             }
     public Map getMap() {
        return map;
        
    }
     
     void placeUnitsOnMap(Player player){
     
         for(Unit unit: player.getArmy())
         
             map.getTerrainAtXY(unit.getPosition().getX(), unit.getPosition().getY()).setIsOccupiedByUnit(true);
         
     }
    /**
     * Places unit on map
     * @param player
     * @param unit
     * @param placePosition 
     *
     */
    public void placeUnit(Player player, Unit unit){
    
      map.getTerrainAtXY(unit.getPosition().getX(), unit.getPosition().getY()).setIsOccupiedByUnit(true);
      
    
    }
    
    public void moveUnit(Unit unit, Position newPosition){
    
      map.getTerrainAtXY(unit.getPosition().getX(), unit.getPosition().getY()).setIsOccupiedByUnit(false);
      map.getTerrainAtXY(newPosition.getX(), newPosition.getY()).setIsOccupiedByUnit(true);

      getCurrentPlayerUnitAtPosition(unit.getPosition()).setPosition(newPosition);
      
      
          
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
    
    public Unit getSelectedUnit(){
     for (Unit unitSearch : getCurrentPlayer().getArmy()) {
            if (unitSearch.isSelected()) {
                return unitSearch;
            }
        }
        return null;
    
    }
    
    public Unit getCurrentPlayerUnitAtPosition(Position position){
    
        for(Unit unitSearch: currentPlayer.getArmy()){
        
            if(unitSearch.getPosition().equals(position))
            {
                return unitSearch;
              }
            
        
        }
              
        return null;
        
     
    }
    
    public boolean checkCurrentPlayerUnitAtPosition(Position position){
    
        for(Unit unitSearch: currentPlayer.getArmy()){
        
            if(unitSearch.getPosition().equals(position))
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
        
        else 
            /*
            Next turn
            */
        {
            turn++;
            getCurrentPlayer().resetPlayer();
            swapActivePlayer();
            phase=0;
        }
        setCardsInHandAsPlayableDueToPhase();
        
    } 
    
    void setFirstPlayer(){
    
    
        int result =  Dice.k2();
        
        if(result == 0)
        {getHostPlayer().setFirst(true);
         getHostPlayer().setActive(true);
            getGuestPlayer().setFirst(false);
            getGuestPlayer().setActive(false);
        
        }
        else 
        {
             getHostPlayer().setFirst(false);
             getHostPlayer().setActive(false);
                getGuestPlayer().setFirst(true);
                getGuestPlayer().setActive(true);
        }
    
    }
   
    public void setCardsInHandAsPlayableDueToPhase(){
        for(int i=0; i<getCurrentPlayer().getHand().cardsLeftInSet(); i++){
            currentPlayer.setHandPlayableByPhaseAndPosition(i, phase);    
         }
    }
    
    /*
    Returns true if starting position of army is OK.
    */
    public Unit validateArmySetup(Player checkPlayer){
    
        for(Unit unit:checkPlayer.getArmy()){
            /*
            Check if unit is on lake
            */
            if(!getMap().getTerrainAtPosition(unit.getPosition()).isTerrainPassable()) 
                return unit;    
            /*
            Check if unit fits in first 2 rows
            */
            if(getCurrentPlayer().isHost() && unit.getPosition().getY() > Position.COLUMN_B
                    ||
                 !getCurrentPlayer().isHost()   && unit.getPosition().getY() < Position.COLUMN_G
           )
                return unit;
           }
        
        return null;
    }
    
    
    public void swapActivePlayer(){
    
        if(getCurrentPlayer().isActive())
        {
        getCurrentPlayer().setActive(false);
        getOpponentPlayer().setActive(true);
        }
        else {
        getCurrentPlayer().setActive(true);
        getOpponentPlayer().setActive(false);
        }
            
            
        
    
    }
    
   
    
    public String toString(){
    
         return "Host Player:" + ( hostPlayer != null ? hostPlayer.toString() : "null") 
              + " Guest Player:"  + ( guestPlayer != null ? guestPlayer.toString() : "null")
                 + " Map: " +  map.toString();
            
    }
    
    
    
}

