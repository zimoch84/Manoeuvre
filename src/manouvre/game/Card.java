package manouvre.game;

import manouvre.interfaces.CardInterface;
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
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;


public class Card implements CardInterface, Serializable  {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Card.class.getName());
    
    private static final long serialVersionUID = 419321L;
    int countId=480;
    
    int countCards=60;
    
    String CardID="";
    String CardName="";				
    Player.Nation flag;
    String CardImg="";                            
    String CardType="";
    String CardAttack="";				
    String CardDefense ="";
    String CardRange="";                            
    String CardBombard="";
    String CardVolley="";				
    String CardPursuit="";
    String CardWithdraw="";                             
    String LederCommand="";
    String LeaderCombat="";				
    String LeaderRally="";
    String LeaderPursuit="";                           
    String LederGrandBatt="";                          
    String CardDescr="";
    String EnableMove="";
    String CanBeCanceledFromCSV="";
 			
    boolean canBeCanceled = false;
    
    boolean cardNotFound=false;
    boolean selected;
    /*
    To indticate if card has been played
    */
    boolean played=false;
    /*
    Case if card is playing as an assoult or volley , bombard etc
    */
    Combat.Type playingCardMode;
    
    ArrayList<Dice> dices;
    
    public Card ()
    {
        CardType = Card.NO_TYPE;
        playingCardMode = Combat.Type.NO_TYPE;
    }
    
    public Card (int chosenID) { 
        try {		
            String strChosenId=Integer.toString(chosenID);
            String filename = "resources/cards/cards.csv";
            InputStream csvResource   = getClass().getClassLoader().getResourceAsStream(filename);
            CsvReader cards = new CsvReader(csvResource, ';', Charset.forName("ISO-8859-1"));
            cards.readHeaders();
            cards.readRecord();
            setPlayingCardMode(Combat.Type.NO_TYPE);
            while (!strChosenId.equals(cards.get("CardID"))&&countId>0)
            {
                cards.readRecord();
                countId--;
            }
            this.CardID = cards.get("CardID");
            this.CardName = cards.get("CardName");
            this.flag = Player.Nation.valueOf(cards.get("CardFlag"));
            this.CardImg = cards.get("CardImg");
            this.CardType = cards.get("CardType");
            this.CardAttack = cards.get("UnitAttack");
            this.CardDefense = cards.get("UnitDeffense");
            this.CardRange = cards.get("UnitRange");
            this.CardBombard = cards.get("UnitBombard");
            this.CardVolley = cards.get("UnitVolley");
            this.CardPursuit = cards.get("UnitPursuit");
            this.CardWithdraw = cards.get("UnitWithdraw");
            this.LederCommand = cards.get("LederCommand");
            this.LeaderCombat = cards.get("LederCombat");
            this.LeaderRally = cards.get("LederRally");
            this.LeaderPursuit = cards.get("LederPursuit");
            this.LederGrandBatt = cards.get("LederGrandBatt");
            this.CardDescr = cards.get("UnitDescr");
            this.EnableMove = cards.get("EnableMove");
            this.CanBeCanceledFromCSV = cards.get("CanBeCancelled");
          
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
            
            if (countCards==0) cardNotFound=true; 
            
            this.CardID = cards.get("CardID");
            this.CardName = cards.get("CardName");
            this.flag = Player.Nation.valueOf(cards.get("CardFlag"));
            this.CardImg = cards.get("CardImg");
            this.CardType = cards.get("CardType");
            this.CardAttack = cards.get("UnitAttack");
            this.CardDefense = cards.get("UnitDeffense");
            this.CardRange = cards.get("UnitRange");
            this.CardBombard = cards.get("UnitBombard");
            this.CardVolley = cards.get("UnitVolley");
            this.CardPursuit = cards.get("UnitPursuit");
            this.CardWithdraw = cards.get("UnitWithdraw");
            this.LederCommand = cards.get("LederCommand");
            this.LeaderCombat = cards.get("LederCombat");
            this.LeaderRally = cards.get("LederRally");
            this.LeaderPursuit = cards.get("LederPursuit");
            this.LederGrandBatt = cards.get("LederGrandBatt");
            this.CardDescr = cards.get("UnitDescr");
            this.EnableMove = cards.get("EnableMove");
            
            cards.close();

            } catch (FileNotFoundException e) {
                    e.printStackTrace();
            } catch (IOException e) {
                    e.printStackTrace();
            }
         
     }
    
     @Override
     public boolean equals(Object in){
     
         if(in instanceof Card){
            Card cardIn = (Card) in;
            if (cardIn.CardID.equals(this.CardID)) return true;
         }
         if(in instanceof Unit){
            Unit unitIn = (Unit) in;
                if (unitIn.name.equals(this.CardName)) return true;
         }
         return false;
     }
    public int getCardID() {
        return Integer.parseInt(CardID);
    }

    public String getCardName() {
        return CardName;
    }

    public Player.Nation getCardFlag() {
        return flag;
    }

    public String getCardImgName() {
        return CardImg;
    }

    public int getType() {
        switch (CardType){
            case "Unit": 
                return Card.UNIT;
            case "HqUnit":
                return Card.HQCARD;
            case "HqLeader":
                return Card.LEADER;
            case Card.NO_TYPE:
                return Card.NO_CARD;
        }         
        return 99; //if else return wrong value
    }

    public Dice.Set getUnitDiceValue(){
        
        if(getHQType() == Card.COMMITED_ATTACK)
            return Dice.Set.DICE2d6;
        
        if(getHQType() == Card.AMBUSH)
            return Dice.Set.DICE1d10;
        
        switch(getPlayingCardMode()){
            case BOMBARD :
                return getUnitBombard();
            case ASSAULT : 
                return getUnitAttack();
            case VOLLEY :
                return getUnitVolley();
            case PURSUIT :
                return Dice.Set.DICE1d6;
            default: return getUnitAttack();
        }
            
    }
    
    public int getUnitDefence() {
        if(getType() == Card.UNIT)
            return Integer.parseInt(CardDefense);
        if(getType() == Card.LEADER)
           return getLeaderCombat();
        else return 0;
    }

    public int getUnitRange() {
         if(!CardRange.equals(""))
        return Integer.parseInt(CardRange);
         else return 0;
    }

    public Dice.Set getUnitBombard() {
        return Dice.Set.getFromString(CardBombard); 
    }
  
    public Dice.Set getUnitVolley() {
         return Dice.Set.getFromString(CardVolley); 
    }
    
    public Dice.Set getUnitAttack() { 
        return Dice.Set.getFromString(CardAttack); 
    }
      
    public boolean canPursue(){
        if(!CardPursuit.equals("")) 
            return true;
            else return false;
    }
    
    public int getUnitPursuit() {
         if(!CardPursuit.equals(""))
        return Integer.parseInt(CardPursuit);
         else return 99;
    }

    public int getUnitWithdraw() {
         if(!CardWithdraw.equals(""))
        return Integer.parseInt(CardWithdraw);
         else return 99;
    }

    public int getLederCommand() {
         if(!LederCommand.equals(""))
            return Integer.parseInt(LederCommand);
         else return 99;
    }

    @Override
    public int getLeaderCombat() {
         if(!LeaderCombat.equals(""))
        return Integer.parseInt(LeaderCombat);
         else return 99;
    }

    @Override
    public int getLeaderRally() {
         if(!LeaderRally.equals(""))
        return Integer.parseInt(LeaderRally);
         else return 0;
    }

    @Override
    public int getLeaderPursuit() {
         if(!LeaderPursuit.equals(""))
        return Integer.parseInt(LeaderPursuit);
         else return 0;
    }

    @Override
    public String getLeaderGrandBattery() { //only Napoleon
         if(!LederGrandBatt.equals(""))
        return LederGrandBatt;
         else return "0";
    }

    @Override
    public String getDescription() {
         if(!CardDescr.equals(""))
        return CardDescr;
         else return "99";
    }
    public boolean getEnableMove() {
         if(!EnableMove.equals(""))
            if(Integer.parseInt(EnableMove)==1)
            return true;
         else return false;
         return false;
    }
    
    public boolean isCancelable(Game game) {
        
        switch(getType()){
            case Card.UNIT:
                if(game.getPhase() == Game.RESTORATION)
                    return true;
            break;   
            case Card.HQCARD:
                switch(getHQType()){
                    case Card.SUPPLY:
                    case Card.FORCED_MARCH:
                    case Card.REGROUP:
                        return true;
                }
            break;
        }
        return false;
    }
    /*
    There are 3 combination here 
    ASSAULT
    ASSAULT/VOLLEY
    BOMBARD
    */
    public ArrayList<Combat.Type> getPlayingPossibleCardModes() {
        
        ArrayList<Combat.Type> cardPlayingModes = new ArrayList<Combat.Type>();
        if(!this.CardAttack.equals(""))
            cardPlayingModes.add(Combat.Type.ASSAULT);
        if(this.CardRange.equals("1") )
            cardPlayingModes.add(Combat.Type.VOLLEY);
        if(this.CardRange.equals("2") )
            cardPlayingModes.add(Combat.Type.BOMBARD);
 
        return cardPlayingModes;
    }

    public ArrayList<Dice> getDices() {
        return dices;
    }

    public void setDices(ArrayList<Dice> dices) {
        this.dices = dices;
    }
    
    final public void setPlayingCardMode(Combat.Type playingCardMode) {
        this.playingCardMode = playingCardMode;
    }

    public Combat.Type getPlayingCardMode() {
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
                return CardInterface.SKIRMISH;
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
            case "French Sappers":
                return CardInterface.FRENCH_SAPPERS;
        }      
        return Card.NO_CARD;
    }
 

    @Override
    public boolean isHQCard() {
        if (getType()== Card.HQCARD)
            return true; 
            else return false;
    }

    @Override
    public boolean isNotRequredToAdvanceAfterAttack() {
       
        if (this.CardDescr.equals("Not required to advance") )
                return true;
        else return false;
    }
    public boolean hasPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isCardNotFoundInNation() {
        return cardNotFound;
    }

    @Override
    public String toString()
    {
    
    return ("ID [" + CardID + "] " + getCardName() + ( getPlayingCardMode()!=null  ?  " playing mode " + getPlayingCardMode() : "") 
            + ( isSelected() ?  " selected " : " deselected")
            )  ;
    
    }
    
}
