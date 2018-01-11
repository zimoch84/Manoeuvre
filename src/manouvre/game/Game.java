/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import manouvre.commands.CommandQueue;
import static manouvre.interfaces.PositionInterface.COLUMN_H;
import static manouvre.interfaces.PositionInterface.ROW_8;
import manouvre.gui.CreateRoomWindow;
import manouvre.network.server.UnoptimizedDeepCopy;
import manouvre.state.CardStateHandler;
import manouvre.state.MapInputStateHandler;

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
    public static final int MAX_PHASES = 5; //there is max 6 phases -> move this value up if new phase come
       
    
    Map map;    
    

    //ArrayList<Unit> units = new ArrayList<>();

    int turn;
    int phase;
    
    private Player currentPlayer, opponentPlayer;
    private Player hostPlayer;
    private Player guestPlayer;
    boolean isServer=true;  //if this will not change game is set on Server
    boolean lockGUI=false;  
    
    boolean showOpponentHand = false;
    CardCommandFactory cardCommandFactory;
    
    CardSet tablePile,tablePileDefPart;
    /*
    Describes and calculate combats
    */
    Combat combat;
    public boolean freeMove = true;
    
    public boolean supressConfirmation = true;
    
    public MapInputStateHandler mapInputHandler;
    public CardStateHandler cardStateHandler;
    
    CommandQueue cmdQueue;
    private String infoBarText;
    
    public Game(ArrayList<Player> players) {
        this.hostPlayer = players.get(0);
        this.guestPlayer = players.get(1);
        
        hostPlayer.setHost(true);
        hostPlayer.setCards();  
        hostPlayer.generateUnits(); 
        guestPlayer.setHost(false);
        guestPlayer.setCards();  
        guestPlayer.generateUnits(); 
        cardCommandFactory = new CardCommandFactory(this);
        this.tablePile=new CardSet(hostPlayer.getNation(), "TABLE");
        this.tablePileDefPart=new CardSet(hostPlayer.getNation(),"TABLE_DEFENDING");
        generateMap(); 
        
        placeUnitsOnMap(hostPlayer);
        placeUnitsOnMap(guestPlayer);
        
        setFirstPlayer();
        this.turn=1;
        setPhase(Game.SETUP);
        
        /*
        Create handlers
        */
        mapInputHandler = new MapInputStateHandler(this);
        
        
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
        
        throw new NullPointerException() ;
        
       }
    

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        isServer=false;
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
            combat.linkObjects(this);
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

    public CommandQueue getCmdQueue() {
        return cmdQueue;
    }

    public void setCmdQueue(CommandQueue cmdQueue) {
        this.cmdQueue = cmdQueue;
        cardStateHandler = new CardStateHandler(this, this.cmdQueue);
        
    }
    
    public ArrayList<Position> getPossibleAssault(Unit unit){
    
    ArrayList<Position> getOneSquarePositionsArray = getOneSquarePositions(unit.getPosition());
    
    ArrayList<Position> getPossinleAssaultArray = new  ArrayList<>();
    
    for(Position checkPositon: getOneSquarePositionsArray)
    {
        if(checkOpponentPlayerUnitAtPosition(checkPositon))
               getPossinleAssaultArray.add(checkPositon);
    }
    return getPossinleAssaultArray;
    
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
        if(getCurrentPlayer().isActive())
            mapInputHandler.setInitStateForPhase(this);
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
    
    public ArrayList<Position> getCurrentPlayerNotMovedUnits()
    { 
        ArrayList<Position> units = new ArrayList<>();         
        for(Unit unitSearch: currentPlayer.getArmy()){
             if(!unitSearch.hasMoved())
            {
                units.add(unitSearch.getPosition());
            }
         }
        return units;
    }
    
    
    public ArrayList<Position> getCurrentPlayerInjuredUnitPositions()
    { 
        ArrayList<Position> units = new ArrayList<>();         
        for(Unit unitSearch: currentPlayer.getArmy()){
             if(unitSearch.isInjured())
            {
                units.add(unitSearch.getPosition());
            }
         }
        return units;
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

    public ArrayList<Unit> getPossibleSupportingUnits(Unit defendingUnit){
        
       /*
        get attacking Units position that are adjenced to the defending one
        */
        ArrayList<Unit> possibleSupportUnits = new ArrayList<>();
        
        for(Position supportPosition:getOneSquarePositions(defendingUnit.getPosition()) )
        {
         if(getCurrentPlayerUnitAtPosition(supportPosition) != null)
            {
           possibleSupportUnits.add(getCurrentPlayerUnitAtPosition(supportPosition));
            }
        }
        return possibleSupportUnits;
    }
    
    /*
    Get unit positions that  can join attack , this is for selection purpose only
    */
    public ArrayList<Position> getPossibleSupportingUnitsPositions(Unit defendingUnit){
    
        /*
        get attacking Units position that are adjenced to the defending one
        */
        ArrayList<Position> possiblePositions = getOneSquarePositions(defendingUnit.getPosition());
        ArrayList<Position> supportingPositions = new ArrayList<>();
        Position atackingPosition = getCombat().getAttackingUnit().getPosition();
        for( Position checkPosition: possiblePositions)
        {
            
            if(checkCurrentPlayerUnitAtPosition(checkPosition) && !checkPosition.equals(atackingPosition))
            {
                supportingPositions.add(checkPosition);
            }
     
        }
        
        return supportingPositions;
        
        

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
    
    public boolean checkCurrentPlayerUnitAtPosition(Position position){
    
        for(Unit unitSearch: currentPlayer.getArmy()){
        
            if(unitSearch.getPosition().equals(position))
            {
                return true;
              }
        }
              
       return false;
        
     
    }
     public boolean checkCurrentPlayerUnitByName(String name){
    
        for(Unit unitSearch: currentPlayer.getArmy()){
        
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
              
        return null;
        
     
    }
     
    public ArrayList<Position> getCurrentPlayerAvalibleUnitToSelect(){
    
     switch (getPhase()){
        
            case Game.SETUP :
            {
           
                return getCurrentPlayer().getArmyPositions();
                
            }
            
            case Game.DISCARD :
                
                return null;
                
            case Game.MOVE:
                return getCurrentPlayer().getArmyPositions();
               
            case Game.COMBAT:
                 
                if(getCombat() == null)
                
                return getCurrentPlayer().getArmyPositions();
                
                else 
                {
                    ArrayList<Position> possiblePositions = new ArrayList<>();
                    if(getCombat().getState() == Combat.WITHRDAW)
                    {
                        if(getCurrentPlayer().hasAttacked())
                        {
                            possiblePositions.add(getCombat().getAttackingUnit().getPosition());
                            return possiblePositions;
                        }
                        else
                        {
                            possiblePositions.add(getCombat().getDefendingUnit().getPosition());
                            return possiblePositions;
                        }   
                    
                    }
                    
                    if(getCombat().getState() == Combat.PURSUIT)
                    {
                        
                        for(Unit pursueUnit: getCombat().getUnitThatCanAdvance())
                        {
                            possiblePositions.add(pursueUnit.getPosition());
                        
                        }
                        return possiblePositions;
                        
                    }
                    
                   if(getCombat().getState() == Combat.PICK_SUPPORT_UNIT)
                   {
                       return getPossibleSupportingUnitsPositions(getCombat().getDefendingUnit());
                   }
                        
                        
                }
                break;
                /*
                TODO implement many more cases with combat mode
                */
            
            case Game.RESTORATION:
                
                return null;
                
                /*
                TODO create funcion to get Units selected by card
                */
          
        }
        
     return null;
        
    }
    public ArrayList<Position> getCurrentPlayerAvalibleMoveUnitPositions(){
    
    Unit selectedUnit;    
        
     if(freeMove) 
            return getSetupPossibleMovement();
        
     switch (getPhase()){
        
            case Game.SETUP :
            {
           
                return getSetupPossibleMovement();
                
            }
            
            case Game.DISCARD :
                
                return null;
                
            case Game.MOVE:
                selectedUnit = getSelectedUnit();
                ArrayList<Position> movePositions;
                return getPossibleMovement(selectedUnit);
                
            case Game.COMBAT:
                   
                
                if(getCombat()!= null)
                {  
                    switch( getCombat().getState() ){
                    
                        case Combat.WITHRDAW :
                        {
                        if(getUnit(getCombat().getDefendingUnit()).isRetriving())
                          
                            return getRetreatPositions(getUnit(getCombat().getDefendingUnit()));
                        }
                    }
                }

                return null;
                /*
                TODO implement many more cases with combat mode
                */
            
            case Game.RESTORATION:
                
                return null;
                
                /*
                TODO create funcion to get Units selected by card
                */
          
        }
        
     return null;
        
    }
    /*
    Returns reference to searched Unit
    */
    
    public Unit getUnit(Unit searchedUnit)
    {
     for(Unit unitSearch: currentPlayer.getArmy()){
        
            if(unitSearch.equals(searchedUnit))
            {
                return unitSearch;
              }
        }
        
        for(Unit unitSearch: opponentPlayer.getArmy()){
        
            if(unitSearch.equals(searchedUnit))
            {
                return unitSearch;
              }
        }
      return null;
    
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
    
    
    public void setUnit(Unit unit)
    {
        Unit setUnit = getUnit(unit);
        
        if(setUnit!= null)
        {
            setUnit = unit;
        }   
    
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
        if(checkCurrentPlayerUnitAtPosition(position))
            return getCurrentPlayerUnitAtPosition(position);
            
        else if(checkCurrentPlayerUnitAtPosition(position))
            return getOpponentPlayerUnitAtPosition(position);
        
        else return null;
        
    }
    
     public boolean checkOpponentPlayerUnitAtPosition(Position position){
    
        for(Unit unitSearch: opponentPlayer.getArmy()){
        
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
    
    public void nextPhase(){
        if(getPhase()<Game.MAX_PHASES)setPhase(getPhase()+1);
        
        /*
        Initialize states
        */
        if(getCurrentPlayer().isActive())
        {   
            mapInputHandler.setInitStateForPhase(this);
            cardStateHandler.setInitStateForPhase(this);
        }
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
        
         /*
        Initialize states
        */
        if(getCurrentPlayer() != null)
            if(getCurrentPlayer().isActive())
            {
                mapInputHandler.setInitStateForPhase(this);
                cardStateHandler.setInitStateForPhase(this);
            }
 
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

    public CardCommandFactory getCardCommandFactory() {
        return cardCommandFactory;
    }

    public void unselectAllUnits(){
    
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
    
     public void setLockGUIByPhase()
    {
        if(getCombat()==null){
                if(getPhase()== Game.SETUP && getCurrentPlayer().isFinishedSetup() && !getOpponentPlayer().isFinishedSetup() )
                    lockGUI();
                else if(getPhase()!= Game.SETUP && !getCurrentPlayer().isActive())
                    lockGUI();            
                else 
                    unlockGUI();
        }
        

    }   
     
     boolean isGameOver()
     {
     
         if(getHostPlayer().getUnitsKilled() > 4  )
             
         {getCardCommandFactory().notifyObservers(CardCommandFactory.HOST_GAME_OVER);
             return true;
         }
         if(getGuestPlayer().getUnitsKilled() > 4  )
         {
            getCardCommandFactory().notifyObservers(CardCommandFactory.GUEST_GAME_OVER);
             return true;
         }
         
         return false;
     
     }
}

