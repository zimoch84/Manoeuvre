/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.util.ArrayList;
import manouvre.game.interfaces.DiceInterface;

/**
 *
 * @author Piotr
 * Class to descrie flow and calculation of combat
 */
public class Combat {
    
    /*
    Combat TYPE
    */
    public static final int ASSAULT = 202;
    public static final int VOLLEY = 203;
    public static final int BOMBARD = 204;
    
    /*
    COmbat flow
    */
    public static final int INITIALIZING_COMBAT= 0;
    public static final int PICK_DEFENSE_CARDS= 1;
    public static final int PICK_SUPPORT_UNIT= 2;
    public static final int PICK_SUPPORTING_CARDS= 3;
    public static final int THROW_DICES= 4;
    public static final int WAIT_FOR_OPPONENT= 5;
    
    /*
    Outcome
    */
    public static final int DEFFENDER_TAKES_HIT=9;
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
    ArrayList<Card> attackCards, defenceCards;
    Terrain attackTerrain, defenseTerrain;
    
    ArrayList<Unit> supportingUnits;
    int combatType;
    

    public Combat(int combatType, Unit attackingUnit, Card attackCard,Terrain attackTerrain, Unit defendingUnit, Terrain defenseTerrain) {
        
    this.attackingUnit = attackingUnit;
    this.defendingUnit = defendingUnit;
    this.attackCards = new ArrayList<>();
    this.defenceCards = new ArrayList<>();
    this.dices = new ArrayList<>();
    this.supportingUnits = new ArrayList<>();
    this.attackCard = attackCard;
    this.attackCards.add(attackCard);
    
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
        if(!attackCard.getPlayiningMode().equals("BOMBARD")){  //bomard do not get advantage of Unit Attack
        attackValue = attackingUnit.getCurrentStrenght();  }
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

    public void setDices(ArrayList<Dice> dices)
    {
    this.dices = dices;
    }
//    public void setDices(ArrayList<Card> cards) {
//    for(Card checkCard: cards)
//        {
//        switch(checkCard.getUnitAttack()){ //if attack
//            case DiceInterface.DICE1d6:{
//                dices.add(new Dice(Dice.D6));
//                break;
//            }
//            case DiceInterface.DICE2d6:{
//                dices.add(new Dice(Dice.D6));
//                dices.add(new Dice(Dice.D6));
//                break;
//            }
//            case DiceInterface.DICE1d8:{
//                dices.add(new Dice(Dice.D8));
//                break;
//            }
//            case DiceInterface.DICE2d8:{
//                dices.add(new Dice(Dice.D8));
//                dices.add(new Dice(Dice.D8));
//                break;
//            }
//            case DiceInterface.DICE1d10:{
//                dices.add(new Dice(Dice.D8));
//                break;
//            }
//            case DiceInterface.DICE2d10:{
//                dices.add(new Dice(Dice.D10));
//                dices.add(new Dice(Dice.D10));
//                break;
//            }
//            case 99:{                              // if card has no attack- BOMBARD
//                switch(checkCard.getUnitBombard()){
//                    case DiceInterface.DICE1d6:{
//                        dices.add(new Dice(Dice.D6));
//                        break;
//                    }
//                    case DiceInterface.DICE2d6:{
//                        dices.add(new Dice(Dice.D6));
//                        dices.add(new Dice(Dice.D6));
//                        break;
//                    }
//                    case DiceInterface.DICE1d8:{
//                        dices.add(new Dice(Dice.D8));
//                        break;
//                    }
//                    case DiceInterface.DICE2d8:{
//                        dices.add(new Dice(Dice.D8));
//                        dices.add(new Dice(Dice.D8));
//                        break;
//                    }
//                    case DiceInterface.DICE1d10:{
//                        dices.add(new Dice(Dice.D8));
//                        break;
//                    }
//                    case DiceInterface.DICE2d10:{
//                        dices.add(new Dice(Dice.D10));
//                        dices.add(new Dice(Dice.D10));
//                        break;
//                    }
//                }
//            break;    
//            }    
//        }
//    }
//        
//        this.dices = dices;
//    }
    public ArrayList<Card> getAttackCards() {
        return attackCards;
    }

    public void setAttackCards(ArrayList<Card> attackCards) {
        
        this.attackCards = attackCards;
        //setDices(attackCards);
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
   switch(getCombatType()){
   
       case Combat.ASSAULT : 
           return getAssaultOutcome();
           
       case Combat.BOMBARD: return getBombardOutcome();
       default : return 0;
   }
   }
    
    
    private int getAssaultOutcome()
    {
//        if(attackValue <  defenceValue) return ATTACKER_TAKES_HIT;
        
        if(attackValue < defenceValue) return NO_EFFECT; // if(defenceValue == attackValue) 
        
//        if(attackValue > defenceValue && attackValue <  defenceValue * 2 ) return DEFENDER_CHOSES;
//        
//        if(attackValue >= defenceValue * 2  && attackValue <  defenceValue * 3 ) return ATTACKER_CHOSES;
//        
//        if(attackValue >= defenceValue * 3  && attackValue <  defenceValue * 4 ) return HIT_AND_RETREAT;
        
        if(attackValue >= defenceValue ) return ELIMINATE; //  if(attackValue >= defenceValue * 4 ) return ELIMINATE;
        
        return NO_EFFECT;
    }
    public int getBombardOutcome()
    {
        if(attackValue >  defenceValue) return ELIMINATE; //if(attackValue >  defenceValue) return DEFFENDER_TAKES_HIT;
        return NO_EFFECT;   
        
    }

    public int getCombatType() {
        return combatType;
    }

    public Unit getAttackingUnit() {
        return attackingUnit;
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
    
    
    
    
}
