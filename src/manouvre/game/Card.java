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
import java.io.Serializable;
import manouvre.game.interfaces.Command;

public class Card implements CardInterface, Serializable{
    
    private static final long serialVersionUID = 419321L;
    int chosenID;
    String CardID="";
    String CardName="";				
    String CardFlag="";
    String CardImg="";                            
    String CardType="";
    String UnitAttack="";				
    String UnitDefense ="";
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
 			
    boolean canceled=false;
    boolean playable=false;
 
    
    
   
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
            this.CardID = cards.get("CardID");
            this.CardName = cards.get("CardName");
            this.CardFlag = cards.get("CardFlag");
            this.CardImg = cards.get("CardImg");
            this.CardType = cards.get("CardType");
            this.UnitAttack = cards.get("UnitAttack");
            this.UnitDefense = cards.get("UnitDeffense");
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
    
    public int getCardID() {
        return Integer.parseInt(CardID);
    }

    public String getCardName() {
        return CardName;
    }

    public int getCardFlag() {
        switch (CardFlag){
            case "BR": 
                return CardInterface.BR;
            case "FR":
                return CardInterface.FR;
            case "RU":
                return CardInterface.RU;
            case "PR":
                return CardInterface.PR;
            case "AU":
                return CardInterface.AU;
            case "SP":
                return CardInterface.SP;
            case "OT":
                return CardInterface.OT;
        }          
        return 99; //if else return wrong value
    }

    public String getCardImg() {
        return CardImg;
    }

    public int getCardType() {
         switch (CardType){
            case "Unit": 
                return CardInterface.UNIT;
            case "HqUnit":
                return CardInterface.HQUNIT;
            case "HqLeader":
                return CardInterface.HQLEADER;
           
        }          
        return 99; //if else return wrong value
    }

    public int getUnitAttack() {    
        return Dice.diceTypeToInt(UnitAttack); //if else return wrong value
    }

    public int getUnitDefence() {
        return Integer.parseInt(UnitDefense);
    }

    public int getUnitRange() {
        return Integer.parseInt(UnitRange);
    }

    public int getUnitBombard() {
        return Dice.diceTypeToInt(UnitBombard); 
    }
  
    public int getUnitVolley() {
        return Dice.diceTypeToInt(UnitVolley); 
    }

    public int getUnitPursuit() {
        return Integer.parseInt(UnitPursuit);
    }

    public int getUnitWithdraw() {
        return Integer.parseInt(UnitWithdraw);
    }

    public String getLederCommand() {
        return LederCommand;
    }

    public int getLederCombat() {
        return Integer.parseInt(LederCombat);
    }

    public int getLederRally() {
        return Integer.parseInt(LederRally);
    }

    public int getLederPursuit() {
        return Integer.parseInt(LederPursuit);
    }

    public String getLederGrandBatt() { //only Napoleon
        return LederGrandBatt;
    }

    public String getUnitDescr() {
        return UnitDescr;
    }


    @Override
    public int getHQType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
 

    @Override
    public boolean isHQCard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isRequredToAdvanceAfterAttack() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean checkRally(int diceThrow) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCancelled() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }
    
    public boolean isPlayable() {
        return playable;
    }
    
    public void setAvailableForPhase(int phase){
        switch(phase){
            case Game.SETUP:
                 this.setPlayable(false);
                 break;
            case Game.DISCARD:
                this.setPlayable(true);
                break;
            case Game.DRAW:
                this.setPlayable(false);
                break;
            case Game.MOVE:
                if(this.CardName.equals("Supply")||
                        this.CardName.equals("Forced March"))
                    
                   this.setPlayable(true);
                break;
            case Game.COMBAT:
                this.setPlayable(true);//btestfalse 
                break;
            case Game.RESTORATION:
                if(this.CardName.equals("Supply")||
                        this.CardName.equals("Regroup"))
                       // this.getCardType()==CardInterface.UNIT||
                       // this.getCardType()==CardInterface.HQLEADER) btestfalse
                    
                   this.setPlayable(true);
                break;
                
        }
       // if(this.CardName.equals("Supply"))
            
    }

    @Override
    public boolean canBePlayed(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
