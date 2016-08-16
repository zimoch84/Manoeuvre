package Game;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bartosz
 */




/*
8x40 Unit cards and 8x20 Head Quarter cards (HQ) each have:
    - color - that represants the nation
    - name
    - deckValue - that represents place in array of 8x60=460 cards
    - type - Unit or HQ 

Each Unit card has additional features:
    - attackValue
    - defenseValue
    - rangeValue
    - bombardValue
    - volleyValue
    - pursuitValue
    - withdrawValue

HQ cards are devided into two types:
    - hQleader card 8x1 unique card type
    - hQunit card 8x19 unique and recurrent card type 

Both type has common feature:
    - type = HQleader or hQunit

hQLeader cards has such features:
    - command
    - combat
    - rally
    - pursuit
    - grandBattery

hQunit cards has differant names and features:
    - names (11 cards):
        - ambush
        - committedAttack
        - forcedMarch
        - guerrilla
        - redoubt
        - regroup
        - sappers_engineers
        - skirmish
        - scout_spy
        - supply
        - withdraw
*/

public class Card {
    private String name;
    private int deckValue; //460 cards
    private int color; //8-colors representing nations
    private int type; //13-types: unit hQleader, hQUnit(11types)
    
    //unit cards
    private int attackValue;
    private int defenseValue;
    private int rangeValue;
    private int bombardValue;
    private int volleyValue;
    private int pursuitValue;
    private int withdrawValue;
    
    //hQleader cards
    private int command;
    private int combat;
    private int rally;
    private int pursuit;
    private int grandBattery;
   
    
    
    public Card (int deckValue, int faceValue, int suitValue){
     this.deckValue=deckValue;
 
    } 
    
    public int getDeckValue (){
        return deckValue;
    }
   
    }
    
    

