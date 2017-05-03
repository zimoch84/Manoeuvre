/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.util.ArrayList;

/**
 *
 * @author Piotr
 * Class to descrie flow and calculation of combat
 */
public class Combat {
    
    /*
    COmbat flow
    */
    public static final int INITIALIZING_COMBAT= 0;
    public static final int PICK_DEFENSE_CARDS= 1;
    public static final int PICK_SUPPORT_UNIT= 2;
    public static final int PLAY_SUPPORTING_CARDS= 3;
    public static final int THROW_DICES= 4;
    
    /*
    Outcome
    */
    public static final int ATTACKER_TAKES_HIT= 10;
    public static final int DEFENDER_CHOSES= 11;
    public static final int ATTACKER_CHOSES= 12;
    public static final int HIT_AND_RETREAT= 13;
    public static final int ELIMINATE= 14;
    public static final int NO_EFFECT= 15;

    
    Unit attackingUnit, defendingUnit;


    int state;
    
    int defenceValue, attackValue, defenseBonus, attackBonus;
    
    ArrayList<Dice> dices;
    
    Card attackCard; 
    ArrayList<Card> attackCards, defenseCards;
    Terrain attackTerrain, defenseTerrain;
    
    ArrayList<Unit> supportingUnits;
    

    public Combat(int combatType, Unit attackingUnit, Card attackCard,Terrain attackTerrain, Unit defendingUnit, Terrain defenseTerrain) {
        
    this.attackingUnit = attackingUnit;
    this.defendingUnit = defendingUnit;
    this.attackCards = new ArrayList<>();
    this.defenseCards = new ArrayList<>();
    this.dices = new ArrayList<>();
    this.supportingUnits = new ArrayList<>();
    this.attackCard = attackCard;
    this.attackCards.add(attackCard);
    
    this.attackTerrain = attackTerrain;
    this.defenseTerrain = defenseTerrain;
    
    this.state= INITIALIZING_COMBAT;
    
    calculateBonuses();
    calculateCombatValues();
    
    setState(INITIALIZING_COMBAT);
    }
    
    void calculateBonuses(){
    
        defenseBonus = defenseTerrain.getDefenceBonus();
        attackBonus = attackTerrain.getAttackBonus(defenseTerrain);
    
    }
    
    public void calculateCombatValues()
            
    {
        /*
        Defense value
        */
        defenceValue += defendingUnit.getCurrentStrenght();
        defenceValue += defenseBonus;
        for(Card checkCards : defenseCards)
            {
        defenceValue +=  checkCards.getUnitDefence();
        }

        /*
        Attack value
        */
        attackValue += attackingUnit.getCurrentStrenght();
        attackValue += attackBonus;
        
        /*
        Dices 
        */
        for(Dice dice : dices)
        {
        attackValue += dice.getResult();
        }
        /*
        Supporting units
        */
        for (Unit unit: supportingUnits)
        {
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

    public void setDices(ArrayList<Dice> dices) {
        this.dices = dices;
    }

    public ArrayList<Card> getAttackCards() {
        return attackCards;
    }

    public void setAttackCards(ArrayList<Card> attackCards) {
        this.attackCards = attackCards;
    }

    public ArrayList<Unit> getSupportingUnits() {
        return supportingUnits;
    }

    public void setSupportingUnits(ArrayList<Unit> supportingUnits) {
        this.supportingUnits = supportingUnits;
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
        if(attackValue <  defenceValue) return ATTACKER_TAKES_HIT;
        
        if(defenceValue == attackValue) return NO_EFFECT;
        
        if(attackValue > defenceValue && attackValue <  defenceValue * 2 ) return DEFENDER_CHOSES;
        
        if(attackValue >= defenceValue * 2  && attackValue <  defenceValue * 3 ) return ATTACKER_CHOSES;
        
        if(attackValue >= defenceValue * 3  && attackValue <  defenceValue * 4 ) return HIT_AND_RETREAT;
        
        if(attackValue >= defenceValue * 4 ) return ELIMINATE;
        
        return NO_EFFECT;
    }
    
}
