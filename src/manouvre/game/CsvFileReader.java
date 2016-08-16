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

package manouvre.game;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.csvreader.CsvReader;

public class CsvFileReader {
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

			
    public CsvFileReader(int chosenID) { 
        try {			
            CsvReader cards = new CsvReader("resources\\cards\\cards.csv", ';');//nie wiem jaka dac sciezke		
            cards.readHeaders();
            cards.readRecord();
            while (!"chosenID".equals(cards.get("CardID")))
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

    public String getCardFlag() {
        return CardFlag;
    }

    public String getCardImg() {
        return CardImg;
    }

    public String getCardType() {
        return CardType;
    }

    public String getUnitAttack() {
        return UnitAttack;
    }

    public String getUnitDeffense() {
        return UnitDeffense;
    }

    public String getUnitRange() {
        return UnitRange;
    }

    public String getUnitBombard() {
        return UnitBombard;
    }

    public String getUnitVolley() {
        return UnitVolley;
    }

    public String getUnitPursuit() {
        return UnitPursuit;
    }

    public String getUnitWithdraw() {
        return UnitWithdraw;
    }

    public String getLederCommand() {
        return LederCommand;
    }

    public String getLederCombat() {
        return LederCombat;
    }

    public String getLederRally() {
        return LederRally;
    }

    public String getLederPursuit() {
        return LederPursuit;
    }

    public String getLederGrandBatt() {
        return LederGrandBatt;
    }

    public String getUnitDescr() {
        return UnitDescr;
    }
	
}