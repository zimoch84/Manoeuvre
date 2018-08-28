/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observer;
import manouvre.commands.CommandQueue;
import manouvre.events.EventEmiter;
import manouvre.events.EventType;
import static manouvre.interfaces.PositionInterface.COLUMN_H;
import static manouvre.interfaces.PositionInterface.ROW_8;
import manouvre.gui.CreateRoomWindow;
import manouvre.network.server.UnoptimizedDeepCopy;
import manouvre.state.MapPickAvalibleUnitState;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Piotr
 */
public final class Game implements Serializable{
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MapPickAvalibleUnitState.class.getName());
    private static final long serialVersionUID = 42321L;
    
    /*Game phases
    */
    public static final int SETUP = -1;
    public static final int DISCARD = 0;
    public static final int DRAW = 1;
    public static final int MOVE = 2;
    public static final int COMBAT = 3;
    public static final int RESTORATION = 4;
    public static final int MAX_PHASES = 5;        
    
    private Map map;    
    
    private int turn;
    private int phase;
    
    private Player currentPlayer, opponentPlayer;
    private Player hostPlayer;
    private Player guestPlayer;
    
    boolean isServer=true; 
    boolean lockGUI=false;  
    
    boolean showOpponentHand = false;
    
    CardSet tablePile,tablePileDefPart;
    /*
    Describes and calculate combats
    */
    private Combat combat;
    public boolean freeMove = true;
    /*
    Move to gui
    */
    public boolean supressConfirmation = true;
    
    
    public PositionCalculator positionCalculator;
    
    CommandQueue cmdQueue;
    private String infoBarText;
    
    EventEmiter ee = new EventEmiter();
    
    public Game(ArrayList<Player> players) {
        this.hostPlayer = players.get(0);
        this.guestPlayer = players.get(1);
        
        hostPlayer.setHost(true);
        hostPlayer.setCards();  
        hostPlayer.generateUnits(); 
        guestPlayer.setHost(false);
        guestPlayer.setCards();  
        guestPlayer.generateUnits(); 
        this.tablePile=new CardSet(hostPlayer.getNation(), "TABLE");
        this.tablePileDefPart=new CardSet(hostPlayer.getNation(),"TABLE_DEFENDING");
        generateMap(); 
        
        placeUnitsOnMap(hostPlayer);
        placeUnitsOnMap(guestPlayer);
        
        setFirstPlayer();
        this.turn=1;
        setPhase(Game.SETUP);
             
        combat = new Combat();
        positionCalculator = new PositionCalculator(this);
        
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getOpponentPlayer() {
        return opponentPlayer;
    }

    public boolean showOpponentHand() {
        return showOpponentHand;
    }

    public void setShowOpponentHand(boolean showOpponentHand) {
        this.showOpponentHand = showOpponentHand;
    }

    public Player getPlayerByName(String playerName) {
      
        if (hostPlayer.getName().equals(playerName))
            return hostPlayer;
        if (guestPlayer.getName().equals(playerName))
            return guestPlayer;
        
        throw new NullPointerException(playerName) ;
        
       }
    
    public boolean isServer() {
        return isServer;
    }

    public Combat getCombat() {
        return combat;
    }

    public void setCombat(Combat combat) {
        this.combat = (Combat) UnoptimizedDeepCopy.copy (combat);
        if(combat!=null)
            if(combat.getAttackingUnit().getID()!= -1 )
                setUnit(combat.getAttackingUnit());
            if(combat.getDefendingUnit().getID()!= -1 )
                setUnit(combat.getDefendingUnit());
            
    }
    
    public  void setCurrentPlayer(int windowMode) {
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
      
    public ArrayList<Position> getPossibleAssault(Unit unit){
    
    ArrayList<Position> getOneSquarePositionsArray = getOneSquarePositions(unit.getPosition());
    
    ArrayList<Position> getPossibleAssaultArray = new  ArrayList<>();
    
        for(Position checkPositon: getOneSquarePositionsArray)
        {
            if(checkOpponentPlayerUnitAtPosition(checkPositon))
                   getPossibleAssaultArray.add(checkPositon);
        }
        return getPossibleAssaultArray;

    }
    
    public ArrayList<Position> getPossibleBombard(Unit unit){
                return getLOS(unit, 2);
    };
    
    public ArrayList<Position> getPossibleVolley(Unit unit){
            return getLOS(unit, 1);
    };
    public ArrayList<Position> getOneSquarePositions(Position unitPosition){
        ArrayList<Position> positions = new ArrayList<>();
          if (unitPosition.getX()-1  >= 0 ) {
                  positions.add(new Position(unitPosition.getX()-1, unitPosition.getY()));
          }
          if (unitPosition.getY()-1 >= 0){      
                  positions.add(new Position(unitPosition.getX(), unitPosition.getY()-1 ));
          }
          if (unitPosition.getY()+1 <= ROW_8){
                  positions.add(new Position(unitPosition.getX(), unitPosition.getY()+1));
          }
          if (unitPosition.getX()+1 <= COLUMN_H){
                  positions.add(new Position(unitPosition.getX()+1, unitPosition.getY()));
          }
        return positions;
    }
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
    
    public ArrayList<Position> getTwoSquareMovements(Position unitPosition){
    
    ArrayList<Position> moves = getOneSquareMovements(unitPosition);

    ArrayList<Position> tempMoves;       
    ArrayList<Position> tempMoves2 = new ArrayList<Position>();

    for(Position move : moves ){       
                if(! map.getTerrainAtXY(move.getX(), move.getY()).isEndsMove() ){              
                    tempMoves = getOneSquareMovements(move);                    
                        for(Position addPosition: tempMoves){                        
                            if (!moves.contains(addPosition) && !addPosition.equals(unitPosition))                                                                  
                                tempMoves2.add(addPosition);                                   
                        }     
                }                      
            }
     moves.addAll(tempMoves2);        
    
     return moves;
     
    }
    
    public void nextTurn(){
        turn++;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public String getInfoBarText() {
        return infoBarText;
    }
    
    public void setInfoBarText(String inText){
    
    this.infoBarText = inText;
    }
    
    
    public ArrayList<Position> getPossibleMovement(Unit unit){      
        ArrayList<Position> moves = new ArrayList<>();         
        /*
        get Infantry Moves
        */
        if (unit.getType() == Unit.INFANTRY)
        moves = getOneSquareMovements(unit.getPosition());
        /*
        If calvary do check of every infantry move considering Terrain.MARSH which ends move
        */
        else if(unit.getType() == Unit.CALVARY){       
        moves = getTwoSquareMovements(unit.getPosition());
        }       
        return moves; 
    };
    
    
    
    public ArrayList<Position> getLOS(Unit unit, int lenght){
        
        Position unitPosition = unit.getPosition();
        ArrayList<Position> los = getOneSquarePositions(unitPosition);
        
        ArrayList<Position> loscopy = (ArrayList<Position>) UnoptimizedDeepCopy.copy(los);
        ArrayList<Position> los2;
            /*
            If length = 1 then we have volley 
            */ 
            if(lenght == 1)
                    return loscopy;
            else 
            {
                 for(Iterator<Position> checkLOSPosition = loscopy.iterator(); checkLOSPosition.hasNext() ; ) {
                    /*  
                    If 1st square terrain blocks los then 2nd squara wont be visible
                    */
                     Position position = checkLOSPosition.next();
                 if(!map.getTerrainAtPosition(position).isBlockingLOS())   {
                     los2 = getOneSquarePositions(position);
                     los2.remove(unitPosition);

                     los.addAll(los2);
                 }
                 
                 }
                
                 
            }
         return los;           
    };  

    public ArrayList<Unit> getPossibleSupportingUnits(Unit defendingUnit, Unit attackingUnit){
       /*
        get attacking Units position that are adjenced to the defending one exept attacking one
        */
        ArrayList<Unit> possibleSupportUnits = new ArrayList<>();
        
        for(Position supportPosition:getOneSquarePositions(defendingUnit.getPosition()) )
        {
             if(getCurrentPlayerUnitAtPosition(supportPosition) != null)
                if(!attackingUnit.getPosition().equals(supportPosition))
                    possibleSupportUnits.add(getCurrentPlayerUnitAtPosition(supportPosition));
        }
        return possibleSupportUnits;
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
      return null;
             
    }
    
    public Unit getAdvancedUnit(String playerName){
     for (Unit unitSearch : getPlayerByName(playerName).getArmy()) {
            if (unitSearch.hasAdvanced()) {
                return unitSearch;
            }
        }
    return null;
    }
    
    public Unit getRetrievedUnit(String playerName){
     for (Unit unitSearch : getPlayerByName(playerName).getArmy()) {
            if (unitSearch.isRetriving()) {
                return unitSearch;
            }
        }
    return null;
    }
    
    /*
    Gets first selected unit  - in single selection mode its only one selected unit
    */
    
    public Unit getSelectedUnit(){
     for (Unit unitSearch : getCurrentPlayer().getArmy()) {
            if (unitSearch.isSelected()) {
                return unitSearch;
            }
        }
    return null;
    }
    
    public Unit getSelectedUnit(String playerName){
     for (Unit unitSearch : getPlayerByName(playerName).getArmy()) {
            if (unitSearch.isSelected()) {
                return unitSearch;
            }
        }
    return null;
    }
    
    /*
    In multiple selection mode 
    */
     public ArrayList<Unit> getSelectedUnits(){
         
         ArrayList<Unit> selectedUnits = new ArrayList<>();
         
         for (Unit unitSearch : getCurrentPlayer().getArmy()) {
            if (unitSearch.isSelected()) {
                selectedUnits.add(unitSearch);
           }
        }
        return selectedUnits;
    
    }
    
    
    public int getNumberOfSupportingUnit(){
        int numberOFSupUnits = 0;
        for (Unit unitSearch : getCurrentPlayer().getArmy()) {
            if (unitSearch.isSupporting()) {
                numberOFSupUnits ++;
            }
        }
        return numberOFSupUnits;
    
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
    
    public Unit getOpponentPlayerUnitAtPosition(Position position){
    
        for(Unit unitSearch: opponentPlayer.getArmy()){
        
            if(unitSearch.getPosition().equals(position))
            {
                return unitSearch;
              }

        }
       return null;

    }
    
    public boolean isCurrentPlayerUnitAtPosition(Position position){
    
        for(Unit unitSearch: currentPlayer.getNotKilledUnits()){
        
            if(unitSearch.getPosition().equals(position))
            {
                return true;
              }
        }
              
       return false;
        
     
    }
     public boolean checkCurrentPlayerUnitByName(String name){
    
        for(Unit unitSearch: currentPlayer.getNotKilledUnits()){
        
            if(unitSearch.getName().equals(name))
            {
                return true;
              }
        }
        return false;
     }
    
     public Unit getCurrentPlayerUnitByName(String name){
    
        for(Unit unitSearch: currentPlayer.getArmy()){
        
            if(unitSearch.getName().equals(name))
            {
                return unitSearch;
              }
            
        
        }
              
        return new Unit();
        
     
    }
     
    public void setUnit(Unit newUnit){
    
        Unit setUnit = getUnit(newUnit);
        if(setUnit.getID() != -1)
             setUnit = newUnit;
        
    }
     
     
    public Unit getUnit(Unit searchedUnit)
    {
        for(Unit unitSearch: currentPlayer.getArmy()){
            if(unitSearch.equals(searchedUnit)){
                return unitSearch;
            }
        }
        for(Unit unitSearch: opponentPlayer.getArmy()){
            if(unitSearch.equals(searchedUnit))
            {
                return unitSearch;
            }
        }
        return new Unit();
    
    }
    public Card getCardFromTable(Card searchedCard)
    {
     for(Card cardSearch: currentPlayer.getTablePile().getCardList()){
            if(cardSearch.equals(searchedCard))
            {
                return cardSearch;
              }
        }
    for(Card cardSearch: opponentPlayer.getTablePile().getCardList()){
            if(cardSearch.equals(searchedCard))
            {
                return cardSearch;
              }
        }
      return null;
    }

    public Unit getUnitByCard(Card card)
    {   
        for(Unit unitSearch: currentPlayer.getArmy())
             if(unitSearch.equals(card))
                return unitSearch;
        
        for(Unit unitSearch: opponentPlayer.getArmy())
            if(unitSearch.equals(card))
                return unitSearch;
        
        
        return new Unit();
        
    }
    
    
    public Unit getUnitByName(String name){
    
        for(Unit unitSearch: currentPlayer.getArmy()){
        
            if(unitSearch.getName().equals(name))
            {
                return unitSearch;
              }
    }
        
        for(Unit unitSearch: opponentPlayer.getArmy()){
        
            if(unitSearch.getName().equals(name))
            {
                return unitSearch;
              }
            
        
        }
              
        return null;
        
     
    }
     
    public Unit getUnitAtPosition(Position position)
    {
        if(isCurrentPlayerUnitAtPosition(position))
            return getCurrentPlayerUnitAtPosition(position);
            
        else if(checkOpponentPlayerUnitAtPosition(position))
            return getOpponentPlayerUnitAtPosition(position);
        else return null;
        
    }
    
     public boolean checkOpponentPlayerUnitAtPosition(Position position){
    
        for(Unit unitSearch: opponentPlayer.getNotKilledUnits()){
            if(unitSearch.getPosition().equals(position))
                return true;
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
    
    public void nextPhase(){
        if(getPhase()<Game.MAX_PHASES)setPhase(getPhase()+1);

    }
     public String getPhaseName(int phase){
      
        
           switch(phase){
           case Game.SETUP:
           {
           return "Setup";
           }    
           case Game.DISCARD:
           {
            return "Discard";
           }
           case Game.DRAW:
           {
             return "Draw";
           }
           case Game.MOVE:
           {
            return "Move";
            }
           case Game.COMBAT:
           {
            return "Combat";
           }
            case Game.RESTORATION:
           {
            return "Restoration";
           }
         
        
           }   
           return null;
      }

    public void setPhase(int phase) {
        this.phase = phase;
        
      
    } 
       
    void setFirstPlayer(){
        int result =  Dice.k2();
        
        if(result == 0)
        {
            getHostPlayer().setFirst(true);
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

    public void unselectAllUnits(){
     
    LOGGER.debug(getCurrentPlayer().getName() + " game.unselectAllUnits()" );
    
    for (Unit unit: getSelectedUnits()){
             unit.setSelected(false);
             getMap().unselectAllTerrains();
       }
       
    }
    
    public String toString(){
    
         return "Host Player:" + ( hostPlayer != null ? hostPlayer.toString() : "null") 
              + " Guest Player:"  + ( guestPlayer != null ? guestPlayer.toString() : "null")
                 + " Map: " +  map.toString();
            
    }

    public boolean isLocked() {
    return lockGUI;
    }

    public void lockGUI() {
    this.lockGUI = true;
    
    }
    public void unlockGUI(){
    this.lockGUI = false;
    }
    
     
     public boolean checkGameOver()
     {
         if(getHostPlayer().getUnitsKilled() > 4  )
             
         {
             notifyAbout(EventType.HOST_GAME_OVER);
             lockGUI();
             return true;
         }
         if(getGuestPlayer().getUnitsKilled() > 4  )
         {
            notifyAbout(EventType.GUEST_GAME_OVER);
            lockGUI();
             return true;
         }
         return false;
     }
     
    public void injureUnit(Unit unit){
       unit =getUnit(unit);
        
        if (!unit.isInjured())
            unit.injured = true;
        else 
        {  
            eliminateUnit(unit);
        }
     }
    
    public boolean checkIfAnyUnitInjured() {
        for (int i = 0; i < this.getCurrentPlayer().getArmy().length; i++) {
            Unit currentUnit = this.getCurrentPlayer().getArmy()[i];
            if (currentUnit.injured) {
                return true;
            }
        }
        return false;
    } 
    
    public ArrayList<Position> getInjuredUnitPositions(){
        ArrayList<Position> injuredUnitPositions = new ArrayList<>();
        
        for(Unit checkUnit : getCurrentPlayer().getArmy())
        {
            if(checkUnit.isInjured())
                  injuredUnitPositions.add(checkUnit.getPosition());
        }

        return injuredUnitPositions;
    }
    
    
    public void eliminateUnit(Unit unit){
         unit =getUnit(unit);
         unit.eliminated = true;
         getMap().getTerrainAtPosition(unit.getPosition()).setIsOccupiedByUnit(false);
               
     }
    void setPlayersScore(){
    
    }
    
    public void notifyAbout(Object eventType)
    {
        ee.notifyAbout(eventType);
    }
    
    public void addObserver(Observer observer){
        ee.addObserver(observer);
    }

    public void endCombat() {
        combat.setState(Combat.END_COMBAT);
        /*
        Clear advance flag after combat
         */
        for (Unit unit : getCurrentPlayer().getArmy()) {
            if (unit.hasAdvanced()) {
                unit.setAdvanced(false);
            }
            if (unit.isSupporting()) {
                unit.setSupporting(false);
            }
        }
        notifyAbout(EventType.END_COMBAT);
    }
    
}

