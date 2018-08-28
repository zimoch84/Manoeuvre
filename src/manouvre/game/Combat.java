/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import manouvre.commands.CardCommands;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Piotr
 * Class to descrie flow and calculation of combat
 */
public class Combat implements Serializable{
     private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(CardCommands.class.getName());  
    /*
    Combat TYPE
    */
    public static final String ASSAULT = "ASSAULT";
    public static final String VOLLEY = "VOLLEY";
    public static final String BOMBARD = "BOMBARD";
    /*
    COmbat flow state
    */
    public static final String COMBAT_NOT_INITIALIZED= "COMBAT_NOT_INITIALIZED";
    public static final String INITIALIZING_COMBAT= "INITIALIZING_COMBAT";
    public static final String WITHRDAW= "WITHRDAW";
    public static final String PICK_DEFENSE_CARDS = "PICK_DEFENSE_CARDS";
    public static final String PICK_SUPPORT_UNIT= "PICK_SUPPORT_UNIT";
    public static final String PICK_SUPPORT_CARDS= "PICK_SUPPORT_CARDS";
    public static final String THROW_DICES= "THROW_DICES";
    public static final String PURSUIT= "PURSUIT";
    public static final String END_COMBAT= "END_COMBAT";
    
    /*
    Combat Outcome
    */
    public static final String DEFENDER_DECIDES= "DEFENDER_DECIDES";
    public static final String ATTACKER_DECIDES= "ATTACKER_DECIDES";
    public static final String DEFFENDER_TAKES_HIT="DEFFENDER_TAKES_HIT";
    public static final String ATTACKER_TAKES_HIT= "ATTACKER_TAKES_HIT";
    public static final String HIT_AND_RETREAT= "HIT_AND_RETREAT";
    public static final String ELIMINATE= "ELIMINATE";
    public static final String NO_EFFECT= "NO_EFFECT";

   
    Unit initAttackUnit, defendingUnit;
    Card supportingLeader;
    ArrayList<Unit> attackingUnits;     
    
    

    /*
    Descibe state of the combat 
    */
    String state;
    
    int defenceValue, attackValue, defenseBonus, attackBonus, leaderBonus;

    ArrayList<Dice> dices;
    Card initAttackingCard; 
    ArrayList<Card> supportCards, defenceCards;

   
    Terrain attackTerrain, defenseTerrain;
 
    String combatType;
    ArrayList<Position> attackingPositions = new ArrayList<>();

    public Combat()
    {
        setState(COMBAT_NOT_INITIALIZED);
        this.supportCards = new ArrayList<>();
        this.defenceCards = new ArrayList<>();
        this.dices = new ArrayList<>();
        this.attackingUnits = new ArrayList<>();
        this.attackTerrain = new Terrain();
        this.defenseTerrain = new Terrain();
        this.defendingUnit = new Unit();
        this.initAttackUnit = new Unit();
        this.initAttackingCard = new Card();
        
    
    }
    
    public Combat(Unit initAttackUnit, Card initAttackingCard,Terrain attackTerrain, Unit defendingUnit, Terrain defenseTerrain) {
        
    this.initAttackUnit = initAttackUnit;
    this.defendingUnit = defendingUnit;
    this.supportCards = new ArrayList<>();
    this.defenceCards = new ArrayList<>();
    this.dices = new ArrayList<>();
    this.attackingUnits = new ArrayList<>();
    this.initAttackingCard = initAttackingCard;
 
    this.attackingUnits.add(initAttackUnit);
    
    this.attackTerrain = attackTerrain;
    this.defenseTerrain = defenseTerrain;
    
    this.combatType = initAttackingCard.getPlayingCardMode();
    
    this.state= INITIALIZING_COMBAT;
    calculateBonuses();
    calculateCombatValues();

    }
        
    public void calculateCombatValues()
    {
        calculateBonuses();
        /*
        Defense value
        */
        defenceValue=0;
        defenceValue += defendingUnit.getCurrentStrenght();
        defenceValue += defenseBonus;
        
        for(Card checkCards : defenceCards)
            {
        defenceValue +=  checkCards.getUnitDefence();
        }

        /*
        Attack value
        */
        attackValue=0;
        //bomard do not get advantage of Unit Attack
        if(!initAttackingCard.getPlayingCardMode().equals(Card.BOMBARD)
                && !initAttackingCard.getPlayingCardMode().equals(Card.VOLLEY)
                ){
            
        attackValue = initAttackUnit.getCurrentStrenght();  
        
        }
        attackValue += attackBonus;
        calculateSupportLeaderBonus();
        attackValue += leaderBonus;
        /*
        Dices 
        */
        for(Dice dice : dices)
        {attackValue += dice.getResult();
        }
        /*
        Supporting units
        */
        for (Unit unit: attackingUnits)
        {
        if(unit.isSupporting())
            attackValue += unit.getCurrentStrenght();
        }
    }
    
    private void calculateBonuses(){
        defenseBonus=0;
        defenseBonus = defenseTerrain.getDefenceBonus();
        attackBonus = attackTerrain.getAttackBonus(defenseTerrain);
    
    }
    
    private void calculateSupportLeaderBonus() {
        leaderBonus =0;
        for (Card supportCard:supportCards)
        {
            if(supportCard.getCardType() == Card.LEADER)
                leaderBonus += supportCard.getLeaderCombat();
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public ArrayList<Dice> getDices() {
        return dices;
    }

    public Card getInitAttackingCard() {
        return initAttackingCard;
    }
    
    public void setDices(ArrayList<Dice> dices)
    {
    this.dices = dices;
    }
    public void addDefenceCard(Card defenceCard)
    {
        defenceCards.add(defenceCard);
        calculateCombatValues();
    }
    
    public void removeDefenceCard(Card defenceCard)
    {
        defenceCards.remove(defenceCard);
        calculateCombatValues();
    }
    
    public ArrayList<Card> getSupportCards() {
        return supportCards;
    }
    public void addSupportCard(Card attackCard)
    {
        supportCards.add(attackCard);
        calculateCombatValues();
    }
    
    public void addSupportLeader4Combat(Card leaderCard){
         addSupportCard(leaderCard);
    }
    
    public int getSupportUnitCount()
            
    {
        int number = 0;
        for (Unit unit:attackingUnits){
            if(unit.isSupporting()) number++;
        }
        return number;
    }
    
    public void removeSupportCard(Card attackCard)
    {
        supportCards.remove(attackCard);
        calculateCombatValues();
    }
    
    public void addSupportUnit(Unit supportUnit)
    {
        attackingUnits.add(supportUnit);
        calculateCombatValues();
    }
    
    public void removeSupportUnit(Unit supportUnit)
    {
        attackingUnits.remove(supportUnit);
        calculateCombatValues();
    }
    
    public void resetSupport()
    {
        setSupportingLeader(null);
        supportCards.clear();
        attackingUnits.clear();
        attackingUnits.add(initAttackUnit);
        calculateCombatValues();
        
    }
    
    
    public ArrayList<Card> getDefenceCards() {
        return defenceCards;
    }
     public void addDefenceCards(Card card) {
        defenceCards.add(card);
    }

    public void setDefenceCards(ArrayList<Card> defenceCards) {
        this.defenceCards = defenceCards;
    }

    public ArrayList<Unit> getSupportingUnits() {
        ArrayList<Unit> supportingUnits = new  ArrayList<>();
        for (Unit supportingUnit : getAttackingUnits())
            if(supportingUnit.isSupporting())
                supportingUnits.add(supportingUnit);
        
        return supportingUnits;
    }

    public int getDefenceValue() {
        return defenceValue;
    }

    public int getAttackValue() {
        return attackValue;
    }

    public int getLeaderBonus() {
        return leaderBonus;
    }
    

   public String getOutcome()
   {
   switch(getCombatType()){
   
       case Combat.ASSAULT : 
           return getAssaultOutcome();
           
       case Combat.BOMBARD: return getBombardOutcome();
       
       case Combat.VOLLEY: return getBombardOutcome();
       default : return NO_EFFECT;
   }
   }
    
    
    private String getAssaultOutcome()
    {
        if(attackValue <  defenceValue) return ATTACKER_TAKES_HIT;
        
        if(attackValue == defenceValue) return NO_EFFECT; // if(defenceValue == attackValue) 
        
        if(attackValue > defenceValue && attackValue <  defenceValue * 2 ) return DEFENDER_DECIDES;
        
        if(attackValue >= defenceValue * 2  && attackValue <  defenceValue * 3 ) return ATTACKER_DECIDES;
        
        if(attackValue >= defenceValue * 3  && attackValue <  defenceValue * 4 ) return HIT_AND_RETREAT;
        
         if(attackValue >= defenceValue * 4 ) return ELIMINATE;
        
        return NO_EFFECT;
    }
    public String getBombardOutcome()
    {
        if(attackValue >  defenceValue) return DEFFENDER_TAKES_HIT; 
        return NO_EFFECT;   
        
    }
    
    public String getPursuitOutcome(Card card)
    {
        if(card.getDices().get(0).getResult() <= card.getUnitPursuit() )
           return DEFFENDER_TAKES_HIT;
        
        else return NO_EFFECT;
            
           
    }

    public void setAttackTerrain(Terrain attackTerrain) {
        this.attackTerrain = attackTerrain;
    }

    public void setDefenseTerrain(Terrain defenseTerrain) {
        this.defenseTerrain = defenseTerrain;
    }
    public String getCombatType() {
        return combatType;
    }

    public Card getSupportingLeader() {
        return supportingLeader;
    }

    public void setSupportingLeader(Card supportingLeader) {
        this.supportingLeader = supportingLeader;
    }

    public void setAttackingUnit(Unit attackingUnit)
    {
        this.initAttackUnit = attackingUnit;
        calculateCombatValues();
    }

    public void setDefendingUnit(Unit defendingUnit) {
        this.defendingUnit = defendingUnit;
        calculateCombatValues();
    }
    

    public void setInitAttackingCard(Card initAttackingCard) {
        this.initAttackingCard = initAttackingCard;
        if( initAttackingCard.getCardType()!= Card.NO_CARD)
            combatType = initAttackingCard.getPlayingCardMode();
        else combatType = Card.NO_TYPE;
    
    }
    
    
    public Unit getAttackingUnit() {
        return initAttackUnit;
    }

    public Unit getDefendingUnit() {
        return defendingUnit;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public Card getInitAttackCard() {
        return initAttackingCard;
    }
    
    public ArrayList<Card> getAttackCards() {
    ArrayList<Card> attackCards = new  ArrayList<>();
    attackCards.add(initAttackingCard);
    attackCards.addAll(supportCards);
    return attackCards;
    }

    public ArrayList<Unit>getAttackingUnits(){

        return attackingUnits;
    }
    
    public Terrain getAttackTerrain() {
        return attackTerrain;
    }

    public Terrain getDefenseTerrain() {
        return defenseTerrain;
    }
    
    public boolean isAttackerNotRequiredToAdvance(){
        /*
        If supoprting card doesnt have "Not required to advance" feature then attacker have to advance
        */
        
        if(initAttackingCard.isNotRequredToAdvanceAfterAttack())
        {
            if(!supportCards.isEmpty())
            {
                for(Card attackCardinLoop :supportCards)
                {
                    /*
                    If supoprting card doesnt have "Not required to advance" feature then attacker have to advance
                    */
                    if(!attackCardinLoop.isNotRequredToAdvanceAfterAttack())
                        return false; 
                }
                /*
                If all of supporting cards have "Not requred to advance"
                */
                return true;
            }
            /*
            If attacker doesnt have supporting cards
            */
            else return true;
        } 
        
        return false;
        
        
    }
    /*
    Returns units that was involved in combat 
    */
    
    public ArrayList<Unit> getUnitThatCanAdvance(){
        
        ArrayList<Unit> advancenits = new ArrayList<>();
           
        advancenits.add(initAttackUnit);
        
        for(Unit attackUnit :attackingUnits)
                    {
        if(attackUnit.isSupporting()   ) ;
                   advancenits.add(attackUnit) ;
        }
        return advancenits;
    
    }
    
    public boolean canAttackerPursue(){
    
            /*
        Find unit that advanced and check if it is Calvary with proper Card
        */
        
        for(Unit attackingUnit: attackingUnits)
                
                if(attackingUnit.hasAdvanced())
                {
                    
                    if(supportCards.contains(attackingUnit))
                    {
                        for(Card checkingCard : supportCards)
                        {
                            if(checkingCard.equals(attackingUnit))
                            {
                                if(checkingCard.canPursue()){
                                    return true;
                                }
                            }
                                
                        }
                    }
                }
                    
       return false;
        
    }
public ArrayList<Card> getPursuitCards(Unit advancingUnit){
    ArrayList<Card> pursueCards = new ArrayList<>();
    ArrayList<Card> attackingCards = new ArrayList<>();
    attackingCards.add(initAttackingCard);
    attackingCards.addAll(supportCards);
    for(Card checkCard : attackingCards)
    {    
        if(checkCard.canPursue())
            if(checkCard.equals(advancingUnit))
                pursueCards.add(checkCard);
    }     
    return pursueCards;
}
    public void resetCombat()
    {
        setState(Combat.COMBAT_NOT_INITIALIZED);
        setAttackingUnit(new Unit());
        setAttackTerrain(new Terrain());
        setInitAttackingCard(new Card());
        setDefendingUnit(new Unit());
        calculateCombatValues();
        clearAttackingPositions();
    }
    
    @Override
    public String toString(){
    
        return getCombatType() + " " + getState() ;
    }


    public ArrayList<Position> getAttackingPositions() {
        return attackingPositions;
    }

    public void setAttackingPositions(ArrayList<Position> attackingPositions) {
        this.attackingPositions = attackingPositions;
    }

    public void clearAttackingPositions() {
        attackingPositions.clear();
    }
    
}
