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
    public enum Type { ASSAULT(true),VOLLEY(false),BOMBARD(false),AMBUSH(false), NO_TYPE(false), PURSUIT(false);
        private boolean advance;
        
        Type(boolean advance){
        this.advance = advance;
        }
        
        public boolean isAdvancementAfterCombat()
        {
            return advance;
        }
        
        @Override
        public String toString() {
            return name();  
            }
    } ;

    public enum State {COMBAT_NOT_INITIALIZED, INITIALIZING_COMBAT, WITHRDAW, PICK_DEFENSE_CARDS,
                        PICK_SUPPORT_UNIT, PICK_SUPPORT_CARDS, THROW_DICES,PURSUIT, 
                        DEFENDER_DECIDES, ATTACKER_DECIDES,
                        COMMITTED_ATTACK_CASUALITIES, END_COMBAT }
    
    public enum Outcome { DEFENDER_DECIDES,ATTACKER_DECIDES  , DEFFENDER_TAKES_HIT, 
    ATTACKER_TAKES_HIT,HIT_AND_RETREAT, ELIMINATE,  NO_EFFECT
    
    }
      
    Unit initAttackUnit, defendingUnit;
    Card supportingLeader;
    ArrayList<Unit> attackingUnits;     

    Combat.State state;
    Combat.Type combatType;
    
    private int defenceValue, attackValue, defenseBonus, attackBonus, leaderBonus;

    ArrayList<Dice> dices;
    Card initAttackingCard; 
    ArrayList<Card> supportCards, defenceCards;
    Terrain attackTerrain, defenseTerrain;
    
    ArrayList<Position> attackingPositions = new ArrayList<>();

    public Combat()
    {
        setState(State.COMBAT_NOT_INITIALIZED);
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
        
    private void calculateCombatValues()
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
        if(!initAttackingCard.getPlayingCardMode().equals(Combat.Type.BOMBARD)
                && !initAttackingCard.getPlayingCardMode().equals(Combat.Type.VOLLEY)
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
        if(defenseTerrain.isRedoubt())
           defenseBonus = defenseBonus - calculateRedoubtBonusModifier();
        attackBonus = attackTerrain.getAttackBonus(defenseTerrain);
    }
    
    private int calculateRedoubtBonusModifier()
    {
        for (Card supportCard:supportCards)
        {
            if(supportCard.getHQType() == Card.FRENCH_SAPPERS   
                || 
                 supportCard.getHQType() == Card.ROYAL_ENG     
                    )
                return 3;
        }
        return 0;
    }
    
    private void calculateSupportLeaderBonus() {
        leaderBonus =0;
        for (Card supportCard:supportCards)
        {
            if(supportCard.getType() == Card.LEADER)
                leaderBonus += supportCard.getLeaderCombat();
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
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
    calculateCombatValues();
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
        calculateCombatValues();
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
    

   public Outcome getOutcome()
   {
   switch(getType()){
   
       case ASSAULT : 
       case AMBUSH:    
           return getAssaultOutcome();
           
       case BOMBARD: return getBombardOutcome();
       
       case VOLLEY: return getBombardOutcome();
       default : return Outcome.NO_EFFECT;
   }
   }
    
    
    private Outcome getAssaultOutcome()
    {
        if(attackValue <  defenceValue) return Outcome.ATTACKER_TAKES_HIT;
        
        if(attackValue == defenceValue) return Outcome.NO_EFFECT; // if(defenceValue == attackValue) 
        
        if(attackValue > defenceValue && attackValue <  defenceValue * 2 ) return Outcome.DEFENDER_DECIDES;
        
        if(attackValue >= defenceValue * 2  && attackValue <  defenceValue * 3 ) return Outcome.ATTACKER_DECIDES;
        
        if(attackValue >= defenceValue * 3  && attackValue <  defenceValue * 4 ) return Outcome.HIT_AND_RETREAT;
        
         if(attackValue >= defenceValue * 4 ) return Outcome.ELIMINATE;
        
        return Outcome.NO_EFFECT;
    }
    public Outcome getBombardOutcome()
    {
        if(attackValue >  defenceValue) return Outcome.DEFFENDER_TAKES_HIT; 
        return Outcome.NO_EFFECT;   
        
    }
    
    public Outcome getPursuitOutcome(Card card)
    {
        int pursuitDiceResult = card.getDices().get(0).getResult();
        int leaderMaxPursuitModifier = 0;
         for (Card supportCard:supportCards)
            if(supportCard.getType() == Card.LEADER)
                if(leaderMaxPursuitModifier > supportCard.getLeaderPursuit() )
                    leaderMaxPursuitModifier = supportCard.getLeaderPursuit();
        //leader pursuit bonus is negative so it must be + not -
        if(pursuitDiceResult + leaderMaxPursuitModifier <= card.getUnitPursuit() )
           return Outcome.DEFFENDER_TAKES_HIT;
        
        else return Outcome.NO_EFFECT;
            
           
    }

    public void setAttackTerrain(Terrain attackTerrain) {
        this.attackTerrain = attackTerrain;
    }

    public void setDefenseTerrain(Terrain defenseTerrain) {
        this.defenseTerrain = defenseTerrain;
        calculateCombatValues();
    }
    public Type getType() {
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
        if( initAttackingCard.getType()!= Card.NO_CARD){
            combatType = initAttackingCard.getPlayingCardMode();
            calculateCombatValues();
        }
        else combatType = Combat.Type.NO_TYPE;
    
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
        ArrayList<Unit> returnUnits = new ArrayList<>();
        for(Unit attackingUnit : attackingUnits)
            if(attackingUnit.getID()!= -1)
                returnUnits.add(attackingUnit);
        return returnUnits;
    }
    
    public Terrain getAttackTerrain() {
        return attackTerrain;
    }

    public Terrain getDefenseTerrain() {
        return defenseTerrain;
    }
    
    public boolean isAttackerNotRequiredToAdvance(){
        /*
        If supporting card doesnt have "Not required to advance" feature then attacker have to advance
        */
        
        if(getType().equals(Combat.Type.AMBUSH)) 
            return true;
        
        else
        
        
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
                if(supportCards.contains(attackingUnit))
                    for(Card checkingCard : supportCards)
                    {
                        if(checkingCard.equals(attackingUnit))
                            if(checkingCard.canPursue())
                                return true;
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
        setState(State.COMBAT_NOT_INITIALIZED);
        setAttackingUnit(new Unit());
        setAttackTerrain(new Terrain());
        setInitAttackingCard(new Card());
        setDefendingUnit(new Unit());
        calculateCombatValues();
        clearAttackingPositions();
    }
    
    @Override
    public String toString(){
    
        return getType() + " " + getState() ;
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
    
    public int getNumberOfCommittedAttackCards()
    {
        int number=0;
        for(Card checkCard:getSupportCards())
            if(checkCard.getHQType() == Card.COMMITED_ATTACK)
                number++;
        return number;
    }
    
    public Card getTopCommittedAttackCard(){
    
        for(Card checkCard:getSupportCards())
            if(checkCard.getHQType() == Card.COMMITED_ATTACK)
                return checkCard;
            
        return new Card();
    
    }
    
    
}
