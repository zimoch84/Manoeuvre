/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observer;
import manouvre.commands.CommandQueue;
import manouvre.events.EventEmiter;
import manouvre.events.EventType;
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
    public boolean freeMove = false;
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
            for(Unit supportingUnit:combat.getSupportingUnits())
            {
                setUnit(supportingUnit);
            }
            
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
    

    public ArrayList<Unit> getPossibleSupportingUnits(Unit defendingUnit, Unit attackingUnit){
       /*
        get attacking Units position that are adjenced to the defending one exept attacking one
        */
        ArrayList<Unit> possibleSupportUnits = new ArrayList<>();
        
        for(Position supportPosition:positionCalculator.getOneSquarePositions(defendingUnit.getPosition()) )
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
     
    private void placeUnitsOnMap(Player player){
     
         for(Unit unit: player.getArmy())
         
             map.getTerrainAtXY(unit.getPosition().getX(), unit.getPosition().getY()).setIsOccupiedByUnit(true);
         
     }
    
    /*
    Gets first selected unit  - in single selection mode its only one selected unit
    */
    
    public Unit getSelectedUnit(){
     for (Unit unitSearch : getCurrentPlayer().getNotKilledUnits()) {
            if (unitSearch.isSelected()) {
                return unitSearch;
            }
        }
    return new Unit();
    }
      
    public Unit getCurrentPlayerUnitAtPosition(Position position){
        for(Unit unitSearch: currentPlayer.getArmy()){
            if(unitSearch.getPosition().equals(position))
                return unitSearch;
       }
       return null;
    }
    
    public Unit getOpponentPlayerUnitAtPosition(Position position){
        for(Unit unitSearch: opponentPlayer.getArmy()){
            if(unitSearch.getPosition().equals(position))
                return unitSearch;
            }
       return null;
    }
    
    public boolean isCurrentPlayerUnitAtPosition(Position position){
        for(Unit unitSearch: currentPlayer.getNotKilledUnits()){
            if(unitSearch.getPosition().equals(position))
                return true;
            }
       return false;
    }
    public boolean checkCurrentPlayerUnitByCard(Card card){
    
        for(Unit unitSearch: currentPlayer.getNotKilledUnits()){
            if(unitSearch.equals(card))
                     return true;
            }
            return false;
    }
    public Unit getCurrentPlayerUnitByCard(Card card){
        for(Unit unitSearch: currentPlayer.getNotKilledUnits()){
            if(unitSearch.equals(card))
                return unitSearch;
        }
        return new Unit();
    }
     
    public void setUnit(Unit newUnit){
    
        getCurrentPlayer().setUnit(newUnit);
        getOpponentPlayer().setUnit(newUnit);
        
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
      
    public int getPhase() {
        return phase;
    }
    
    public void nextPhase(){
        if(getPhase()<Game.MAX_PHASES)
            setPhase(getPhase()+1);

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
     /*
    In multiple selection mode 
    */
    public ArrayList<Unit> getSelectedUnits(){
        ArrayList<Unit> selectedUnits = new ArrayList<>();
        for (Unit unitSearch : getCurrentPlayer().getNotKilledUnits()) {
            if (unitSearch.isSelected()) 
                selectedUnits.add(unitSearch);
            }
        return selectedUnits;
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
    
    public boolean checkIfCurrentPlayerHasAnyUnitInjured() {
        for(Unit checkUnit:getCurrentPlayer().getNotKilledUnits())
            if(checkUnit.isInjured() ) {
                return true;
            }
        return false;
    } 
   
    public ArrayList<Unit> getCurrentPlayerInjuredUnits(){
        ArrayList<Unit> injuredUnits = new ArrayList<>();
        for(Unit checkUnit : getCurrentPlayer().getNotKilledUnits())
        {
            if(checkUnit.isInjured())
                  injuredUnits.add(checkUnit);
        }
        return injuredUnits;
    }
    public void eliminateUnit(Unit unit){
         unit =getUnit(unit);
         unit.eliminated = true;
         Terrain terrain = getMap().getTerrainAtPosition(unit.getPosition());
         terrain.setIsOccupiedByUnit(false);
         if(terrain.isRedoubt())
             terrain.setRedoubt(false);
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

    public void checkCommittedAttackandEndCombat() {
        
        int numberOfComCards =  combat.getNumberOfCommittedAttackCards();
        if(numberOfComCards==0)
            endCombat();
        else 
        {
            int numberOfAttackingUnits = combat.getAttackingUnits().size();
            
            if(numberOfComCards == numberOfAttackingUnits){
                for(Unit attackingUnit : combat.getAttackingUnits())
                    injureUnit(getUnit(attackingUnit)); 
                
                endCombat();     
                }
            else{
                combat.setState(Combat.State.COMMITTED_ATTACK_CASUALITIES);
                notifyAbout(EventType.PICK_COMMITTED_ATTACK_CASUALITIES);
            }
        }
    }
    
    public void checkConditionAndStartAdvancementOrEndCombat(){
        if(getCombat().getType().isAdvancementAfterCombat()){
            getCombat().setState(Combat.State.PURSUIT);
            notifyAbout(EventType.COMBAT_ADVANCE_STARTED);
         }
        else checkCommittedAttackandEndCombat();
    }
    
    public void endCombat() {
        
        combat.setState(Combat.State.END_COMBAT);
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

