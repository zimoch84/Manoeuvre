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
//someth



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
import java.util.ArrayList;


public class Card implements CardInterface, Serializable{
    
    private static final long serialVersionUID = 419321L;
    int countId=480;
    int countNation=8;
    int countCards=60;
    int chosenID;
    String chosenName;
    
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
    String EnableMove="";
    String CanBeCanceledFromCSV="";
 			
    boolean canceled=false;
    boolean canBeCanceled = false;

    
    boolean playable=false;
    boolean cardNotFound=false;

    boolean justToTry;
 
    boolean selected;
    /*
    Case if card is playing as an assoult or volley , bombard etc
    */
    int playingCardMode;
    
   
    public Card (int chosenID) { 
        try {		
            String strChosenId=Integer.toString(chosenID);
            CsvReader cards = new CsvReader("resources\\cards\\cards.csv", ';');		
            cards.readHeaders();
            cards.readRecord();
            while (!strChosenId.equals(cards.get("CardID"))&&countId>0)
            {
                cards.readRecord();
                countId--;
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
            this.EnableMove = cards.get("EnableMove");
            this.CanBeCanceledFromCSV = cards.get("CanBeCancelled");
            setCanBeCanceled(getCanBeCanceledFromCsv());

            cards.close();

            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
    }
    
     public Card (String chosenName, String flag){
          try {		
            CsvReader cards = new CsvReader("resources\\cards\\cards.csv", ';');		
            cards.readHeaders();
            cards.readRecord();
            while (!flag.equals(cards.get("CardFlag"))&&countId>0){
                cards.readRecord();
                countId--;
            }
            
            while (!chosenName.equals(cards.get("CardName"))&&countCards>0)
            {
               
                cards.readRecord();
                countCards--;

            }
            //countNation--;
            if (countCards==0) cardNotFound=true; 
            
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
            this.EnableMove = cards.get("EnableMove");
            this.CanBeCanceledFromCSV = cards.get("CanBeCancelled");
            setCanBeCanceled(getCanBeCanceledFromCsv());
            
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
                return CardInterface.HQCARD;
            case "HqLeader":
                return CardInterface.HQLEADER;
        }         
        return 99; //if else return wrong value
    }

    public int getUnitAttack() { 
        if(!UnitAttack.equals(""))
        return Dice.diceTypeToInt(UnitAttack); //if else return wrong value
        else return 0;
    }

    public int getUnitDefence() {
        if(!UnitDefense.equals(""))
        return Integer.parseInt(UnitDefense);
        else return 99;
    }

    public int getUnitRange() {
         if(!UnitRange.equals(""))
        return Integer.parseInt(UnitRange);
         else return 0;
    }

    public int getUnitBombard() {
         if(!UnitBombard.equals(""))
        return Dice.diceTypeToInt(UnitBombard); 
         else return 99;
    }
  
    public int getUnitVolley() {
         if(!UnitVolley.equals(""))
        return Dice.diceTypeToInt(UnitVolley); 
         else return 99;
    }

    public int getUnitPursuit() {
         if(!UnitPursuit.equals(""))
        return Integer.parseInt(UnitPursuit);
         else return 99;
    }

    public int getUnitWithdraw() {
         if(!UnitWithdraw.equals(""))
        return Integer.parseInt(UnitWithdraw);
         else return 99;
    }

    public String getLederCommand() {
         if(!LederCommand.equals(""))
        return LederCommand;
         else return "99";
    }

    public int getLederCombat() {
         if(!LederCombat.equals(""))
        return Integer.parseInt(LederCombat);
         else return 99;
    }

    public int getLederRally() {
         if(!LederRally.equals(""))
        return Integer.parseInt(LederRally);
         else return 99;
    }

    public int getLederPursuit() {
         if(!LederPursuit.equals(""))
        return Integer.parseInt(LederPursuit);
         else return 99;
    }

    public String getLederGrandBatt() { //only Napoleon
         if(!LederGrandBatt.equals(""))
        return LederGrandBatt;
         else return "99";
    }

    public String getUnitDescr() {
         if(!UnitDescr.equals(""))
        return UnitDescr;
         else return "99";
    }
    public boolean getEnableMove() {
         if(!EnableMove.equals(""))
            if(Integer.parseInt(EnableMove)==1)
            return true;
         else return false;
         return false;
    }
    
    public boolean getCanBeCancelled() {
         return canBeCanceled;
    }
     public boolean getCanBeCanceledFromCsv() {
         if(!CanBeCanceledFromCSV.equals(""))
            if(Integer.parseInt(CanBeCanceledFromCSV)==1)
            return true;
         else return false;
         return false;
    }
      
    /*
    There are 3 combination here 
    ASSAULT
    ASSAULT/VOLLEY
    BOMBARD
    */
    public ArrayList<Integer> getPlayingPossibleCardModes() {
        
        ArrayList<Integer> cardPlayingModes = new ArrayList<Integer>();
       
        
        if(!this.UnitAttack.equals("0")) 
        {cardPlayingModes.add(Card.ASSAULT);
         
          }
        if(this.UnitRange.equals("1") )
        {
        cardPlayingModes.add(Card.VOLLEY);
 
        }
        if(this.UnitRange.equals("2") )
        {
        cardPlayingModes.add(Card.BOMBARD);
                }
 
        return cardPlayingModes;
    }

    
    public void setPlayingCardMode(int playingCardMode) {
        this.playingCardMode = playingCardMode;
    }

    public int getPlayingCardMode() {
        return playingCardMode;
    }
    
    
    
    @Override
    public int getHQType() {

        switch (CardName){
            case "Committed Attack": 
                return CardInterface.COMMITED_ATTACK;
            case "Forced March":
                return CardInterface.FORCED_MARCH;
            case "Redoubt":
                return CardInterface.REDOUBDT;
            case "Royal Engineers": 
                return CardInterface.ROYAL_ENG;
            case "Skirmish":
                return CardInterface.SKIRMICH;
            case "Spy":
                return CardInterface.SPY;
            case "Supply": 
                return CardInterface.SUPPLY;
            case "Withdraw":
                return CardInterface.WITHDRAW;
            case "Ambush":
                return CardInterface.AMBUSH;
            case "Guerrillas": 
                return CardInterface.GUERRILLAS;
            case "Regroup":
                return CardInterface.REGROUP;
            case "Scout":
                return CardInterface.SCOUT;
        }      
        return 99;
    }
 

    @Override
    public boolean isHQCard() {
        if (getCardType()== Card.HQCARD)
            return true; 
            else return false;
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

    public void setPlayableInPhase(boolean playable) {
        this.playable = playable;
    }
    
    public boolean isPlayableInPhase() {
        return playable;
    }
    
    public void setAvailableForPhase(int phase,  Unit[] army){
        switch(phase){
            case Game.SETUP:
                 this.setPlayableInPhase(false);
                 break;
            case Game.DISCARD:
                this.setPlayableInPhase(true);
                break;
            case Game.DRAW:
                this.setPlayableInPhase(false);
                break;
            case Game.MOVE:
                if(this.CardName.equals("Supply")||
                        this.CardName.equals("Forced March"))
                    
                   this.setPlayableInPhase(true);
                break;
            case Game.COMBAT:
                for(int i=0; i<army.length;i++){
                     if(this.CardName.equals(army[i].getName()))//find if there is unit with the card name
                         this.setPlayableInPhase(true);//btestfalse 
                }
                break;
            case Game.RESTORATION:
                if(this.CardName.equals("Supply")||
                        this.CardName.equals("Regroup"))
                       // this.getCardType()==CardInterface.UNIT||
                       // thi-s.getCardType()==CardInterface.HQLEADER) btestfalse
                    
                   this.setPlayableInPhase(true);
                break;
                
        }
       // if(this.CardName.equals("Supply"))
            
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    
    
    @Override
    public boolean canBePlayed(Game game) {
        
        if(isPlayableInPhase())
        {
            switch (getCardType())
            {
                case Card.FORCED_MARCH :
                if(game.getCurrentPlayer().hasMoved()) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    public boolean isCardNotFoundInNation() {
        return cardNotFound;
    }
    
    public boolean isCanBeCanceled() {
        return canBeCanceled;
    }

    public void setCanBeCanceled(boolean canBeCanceled) {
        this.canBeCanceled = canBeCanceled;
    }
    
    @Override
    public boolean equals(Object in){
    
        Card p = (Card) in;
        if(this.getCardID()==p.getCardID()) return true;
        
        else return false;
    }
}
