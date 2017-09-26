/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import manouvre.game.commands.CardCommands;
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
    public static final int ASSAULT = 202;
    public static final int VOLLEY = 203;
    public static final int BOMBARD = 204;
    
    /*
    COmbat flow state
    */
    public static final int COMBAT_NOT_INITIALIZED= -1;
    public static final int INITIALIZING_COMBAT= 0;
    public static final int WITHRDAW= 1;
    public static final int PICK_DEFENSE_CARDS= 2;
    public static final int PICK_SUPPORT_UNIT= 3;
    public static final int PICK_SUPPORTING_CARDS= 4;
    public static final int THROW_DICES= 5;
    public static final int DEFENDER_DECIDES= 6;
    public static final int ATTACKER_DECIDES= 7;
    public static final int PURSUIT= 8;
    public static final int END_COMBAT= 9;
    
    
    /*
    Outcome
    */
    public static final int DEFFENDER_TAKES_HIT=9;
    public static final int ATTACKER_TAKES_HIT= 10;
    public static final int HIT_AND_RETREAT= 13;
    public static final int ELIMINATE= 14;
    public static final int NO_EFFECT= 15;

   
    Unit initAttackUnit, defendingUnit;
    Card supportingLeader;
    ArrayList<Unit> attackingUnits;     
    
    

    /*
    Descibe state of the combat 
    */
    int state;
    
    int defenceValue, attackValue, defenseBonus, attackBonus;

    ArrayList<Dice> dices;
    Card attackCard; 
    ArrayList<Card> attackCards, defenceCards;
    Terrain attackTerrain, defenseTerrain;
 
    int combatType;

    public Combat(Unit initAttackUnit, Card attackCard,Terrain attackTerrain, Unit defendingUnit, Terrain defenseTerrain) {
        
    this.initAttackUnit = initAttackUnit;
    this.defendingUnit = defendingUnit;
    this.attackCards = new ArrayList<>();
    this.defenceCards = new ArrayList<>();
    this.dices = new ArrayList<>();
    this.attackingUnits = new ArrayList<>();
    this.attackCard = attackCard;
    this.attackCards.add(attackCard);
    this.attackingUnits.add(initAttackUnit);
    
    this.attackTerrain = attackTerrain;
    this.defenseTerrain = defenseTerrain;
    
    this.combatType = attackCard.getPlayingCardMode();
    
    this.state= INITIALIZING_COMBAT;
    //setDices(attackCards);
    calculateBonuses();
    calculateCombatValues();
    
    setState(INITIALIZING_COMBAT);
    }
    
    void calculateBonuses(){
        defenseBonus=0;
        defenseBonus = defenseTerrain.getDefenceBonus();
        attackBonus = attackTerrain.getAttackBonus(defenseTerrain);
    
    }
    
    public void calculateCombatValues()
            
    {
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
        if(attackCard.getPlayingCardMode()!=Card.BOMBARD){  
        attackValue = initAttackUnit.getCurrentStrenght();  }
        attackValue += attackBonus;
      
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public ArrayList<Dice> getDices() {
        return dices;
    }
    
    public void setDices(ArrayList<Dice> dices)
    {
    this.dices = dices;
    }

    public ArrayList<Card> getAttackCards() {
        return attackCards;
    }

    public void setAttackCards(ArrayList<Card> attackCards) {
        
        this.attackCards = attackCards;
        //setDices(attackCards);
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
    public void addAttackCard(Card attackCard)
    {
        attackCards.add(attackCard);
        calculateCombatValues();
    }
    
    public void removeAttackCard(Card attackCard)
    {
        attackCards.remove(attackCard);
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
        return attackingUnits;
    }

    public void setSupportingUnits(ArrayList<Unit> supportingUnits) {
        this.attackingUnits = supportingUnits;
    }

    public int getDefenceValue() {
        return defenceValue;
    }

    public void setDefenceValue(int defenceValue) {
        this.defenceValue = defenceValue;
    }

    public int getAttackValue() {
        return attackValue;
    }

    public void setAttackValue(int attackValue) {
        this.attackValue = attackValue;
    }

    public int getOutcome()
   {
   switch(getCombatType()){
   
       case Combat.ASSAULT : 
           return getAssaultOutcome();
           
       case Combat.BOMBARD: return getBombardOutcome();
       default : return 0;
   }
   }
    
    
    private int getAssaultOutcome()
    {
        if(attackValue <  defenceValue) return ATTACKER_TAKES_HIT;
        
        if(attackValue == defenceValue) return NO_EFFECT; // if(defenceValue == attackValue) 
        
        if(attackValue > defenceValue && attackValue <  defenceValue * 2 ) return DEFENDER_DECIDES;
        
        if(attackValue >= defenceValue * 2  && attackValue <  defenceValue * 3 ) return ATTACKER_DECIDES;
        
        if(attackValue >= defenceValue * 3  && attackValue <  defenceValue * 4 ) return HIT_AND_RETREAT;
        
         if(attackValue >= defenceValue * 4 ) return ELIMINATE;
        
        return NO_EFFECT;
    }
    public int getBombardOutcome()
    {
        if(attackValue >  defenceValue) return DEFFENDER_TAKES_HIT; 
        return NO_EFFECT;   
        
    }
    
    public int getPursuitOutcome(Card card, Dice dice)
    {
        if(dice.getResult() <= card.getUnitPursuit() )
           return DEFFENDER_TAKES_HIT;
        
        else return NO_EFFECT;
            
           
    }

    public int getCombatType() {
        return combatType;
    }

    public Card getSupportingLeader() {
        return supportingLeader;
    }

    public void setSupportingLeader(Card supportingLeader) {
        this.supportingLeader = supportingLeader;
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

    public Card getAttackCard() {
        return attackCard;
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
        
        if(attackCard.isNotRequredToAdvanceAfterAttack())
        {
            if(!attackCards.isEmpty())
            {
                for(Card attackCard :attackCards)
                {
                    /*
                    If supoprting card doesnt have "Not required to advance" feature then attacker have to advance
                    */
                    if(!attackCard.isNotRequredToAdvanceAfterAttack())
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
                    
                    if(attackCards.contains(attackingUnit))
                    {
                        for(Card checkingCard : attackCards)
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
    public ArrayList<Card> getPursuitCards(Game game){
        
        ArrayList<Card> pursueCards = new ArrayList<>();
           
          for(Unit attackingUnit: attackingUnits)
                
                if(game.getUnitByName(attackingUnit.getName()).hasAdvanced())
                {
                    
                    if(attackCards.contains(attackingUnit))
                    {
                        for(Card checkingCard : attackCards)
                        {
                            if(checkingCard.equals(attackingUnit))
                            {
                                if(checkingCard.canPursue()){
                                    pursueCards.add(checkingCard);
                                }
                            }
                                
                        }
                    }
                }
          
          return pursueCards;
                  
    }
    
    public void endCombat(Game game)
            
    {
        
        setState(END_COMBAT);
      
        /*
        Clear advance flag after combat
        */
        
        for(Unit unit: game.getCurrentPlayer().getArmy())
            
        {
            if(unit.hasAdvanced())
                unit.setAdvanced(false);
                
            if(unit.isSupporting())
                unit.setSupporting(false);
            
        }
        LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
        game.getCardCommandFactory().resetFactory();
    
    }
    
    
}
