package manouvre.game;

import manouvre.game.interfaces.CardInterface;
//import manouvre.game.interfaces.DiceInterface;
import manouvre.game.Dice;

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



import java.io.FileNotFoundException;
import java.io.IOException;

import com.csvreader.CsvReader;

public class Card implements CardInterface{
    int chosenID;
    String CardName="";				
    String CardFlag="";
    String CardImg="";                            
    String CardType="";
    String UnitAttack="";				
    String UnitDeffense ="";
    String UnitRange="";                            
    String UnitBombard="";
    String UnitVolley="";				
    String UnitPursuit="";
    String UnitWithdraw="";                             
    String LederCommand="";
    String LederCombat="";				
    String LederRally="";
    String LederPursuit="";                           
    String LederGrandBatt="";                          
    String UnitDescr="";
			
    public Card (int chosenID) { 
        try {		
            String strChosenId=Integer.toString(chosenID);
            CsvReader cards = new CsvReader("resources\\cards\\cards.csv", ';');		
            cards.readHeaders();
            cards.readRecord();
            while (!strChosenId.equals(cards.get("CardID")))
            {
                cards.readRecord();
            }
            this.CardName = cards.get("CardName");
            this.CardFlag = cards.get("CardFlag");
            this.CardImg = cards.get("CardImg");
            this.CardType = cards.get("CardType");
            this.UnitAttack = cards.get("UnitAttack");
            this.UnitDeffense = cards.get("UnitDeffense");
            this.UnitRange = cards.get("UnitRange");
            this.UnitBombard = cards.get("UnitBombard");
            this.UnitVolley = cards.get("UnitVolley");
            this.UnitPursuit = cards.get("UnitPursuit");
            this.UnitWithdraw = cards.get("UnitWithdraw");
            this.LederCommand = cards.get("LederCommand");
            this.LederCombat = cards.get("LederCombat");
            this.LederRally = cards.get("LederRally");
            this.LederPursuit = cards.get("LederPursuit");
            this.LederGrandBatt = cards.get("LederGrandBatt");
            this.UnitDescr = cards.get("UnitDescr");

          // System.out.println(chosenID + ":" + CardName + ", " + CardFlag + CardImg + ", " + CardType + UnitAttack + ", " + UnitDeffense + UnitRange + ", " + UnitBombard + UnitVolley + ", " + UnitPursuit + ", " + UnitWithdraw + ", " + LederCommand + ", " + LederCombat + ", " + LederRally + ", " + LederPursuit + ", " + LederGrandBatt + "," + UnitDescr);

            cards.close();

            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
    }
    
    public int getChosenID() {
        return chosenID;
    }

    public String getCardName() {
        return CardName;
    }

    public int getCardFlag() {
        switch (CardFlag){
            case "BR": 
                return 0;
            case "FR":
                return 1;
            case "RU":
                return 2;
            case "PR":
                return 3;
            case "AU":
                return 4;
            case "SP":
                return 5;
            case "OT":
                return 6;
        }          
        return 99; //if else return wrong value
    }

    public String getCardImg() {
        return CardImg;
    }

    public String getCardType() {
        return CardType;
    }

    public int getUnitAttack() {    
        return Dice.diceTypeToInt(UnitAttack); //if else return wrong value
    }

    public int getUnitDeffense() {
        int strToInt=Integer.parseInt(UnitDeffense);
        return strToInt;
    }

    public int getUnitRange() {
        int strToInt=Integer.parseInt(UnitRange);
        return strToInt;
    }

    public int getUnitBombard() {
        return Dice.diceTypeToInt(UnitBombard); 
    }
  
    public int getUnitVolley() {
        return Dice.diceTypeToInt(UnitVolley); 
    }

    public int getUnitPursuit() {
        int strToInt=Integer.parseInt(UnitPursuit);
        return strToInt;
    }

    public int getUnitWithdraw() {
        int strToInt=Integer.parseInt(UnitWithdraw);
        return strToInt;
    }

    public String getLederCommand() {
        return LederCommand;
    }

    public int getLederCombat() {
        int strToInt=Integer.parseInt(LederCombat);
        return strToInt;
    }

    public int getLederRally() {
        int strToInt=Integer.parseInt(LederRally);
        return strToInt;
    }

    public int getLederPursuit() {
        int strToInt=Integer.parseInt(LederPursuit);
        return strToInt;
    }

    public String getLederGrandBatt() { //only Napoleon
        return LederGrandBatt;
    }

    public String getUnitDescr() {
        return UnitDescr;
    }

    @Override
    public int getType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getHQType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getColor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getDefence() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isHQCard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
	
}


    
    
    
    
    
    
    
    
   