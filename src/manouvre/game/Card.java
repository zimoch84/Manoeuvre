package manouvre.game;

import manouvre.interfaces.CardInterface;
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
import manouvre.commands.CommandQueue;
import manouvre.state.MapInputStateHandler;
import org.apache.logging.log4j.LogManager;


public class Card implements CardInterface, Serializable{
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Card.class.getName());
    
    private static final long serialVersionUID = 419321L;
    int countId=480;
    int countNation=8;
    int countCards=60;
    String chosenName;
    
    String CardID="";
    String CardName="";				
    String CardFlag="";
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
    String LederRally="";
    String LederPursuit="";                           
    String LederGrandBatt="";                          
    String CardDescr="";
    String EnableMove="";
    String CanBeCanceledFromCSV="";
 			
    boolean canceled=false;
    boolean canBeCanceled = false;
    
    boolean cardNotFound=false;
    boolean selected;
    boolean mouseOverCard;
    /*
    To indticate if card has been played
    */
    boolean played=false;
    /*
    Case if card is playing as an assoult or volley , bombard etc
    */
    String playingCardMode;
    
    boolean availableForDefance=false;
    boolean availableForSupport=false;
   
    ArrayList<Dice> dices;
    
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
            this.CardAttack = cards.get("UnitAttack");
            this.CardDefense = cards.get("UnitDeffense");
            this.CardRange = cards.get("UnitRange");
            this.CardBombard = cards.get("UnitBombard");
            this.CardVolley = cards.get("UnitVolley");
            this.CardPursuit = cards.get("UnitPursuit");
            this.CardWithdraw = cards.get("UnitWithdraw");
            this.LederCommand = cards.get("LederCommand");
            this.LeaderCombat = cards.get("LederCombat");
            this.LederRally = cards.get("LederRally");
            this.LederPursuit = cards.get("LederPursuit");
            this.LederGrandBatt = cards.get("LederGrandBatt");
            this.CardDescr = cards.get("UnitDescr");
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
            this.CardAttack = cards.get("UnitAttack");
            this.CardDefense = cards.get("UnitDeffense");
            this.CardRange = cards.get("UnitRange");
            this.CardBombard = cards.get("UnitBombard");
            this.CardVolley = cards.get("UnitVolley");
            this.CardPursuit = cards.get("UnitPursuit");
            this.CardWithdraw = cards.get("UnitWithdraw");
            this.LederCommand = cards.get("LederCommand");
            this.LeaderCombat = cards.get("LederCombat");
            this.LederRally = cards.get("LederRally");
            this.LederPursuit = cards.get("LederPursuit");
            this.LederGrandBatt = cards.get("LederGrandBatt");
            this.CardDescr = cards.get("UnitDescr");
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
     
    public int getActionType(int phase){
        
        if(getCardType() == Card.LEADER)
            return Card.MULTIPLE_PICK_ACTION;
        if(getHQType()== Card.FORCED_MARCH || 
           getHQType()== Card.WITHDRAW     
                )
            return Card.MOVE_ACTION;
        if(getHQType()== Card.SUPPLY) 
        {
          if(phase == Game.MOVE ){
            return Card.MOVE_ACTION;
          }
            else 
            return Card.PICK_ACTION;
         
        }
        if(getHQType()== Card.REDOUBDT ||
           getHQType()== Card.REGROUP ||
           getHQType()== Card.SKIRMISH ||     
           getHQType()== Card.AMBUSH ||
           getCardType() == Card.UNIT)
  
        {
            return Card.PICK_ACTION;
        }
        
     return 0;   
          
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
                return Card.UNIT;
            case "HqUnit":
                return Card.HQCARD;
            case "HqLeader":
                return Card.LEADER;
        }         
        return 99; //if else return wrong value
    }

    public int getUnitDiceValue(){
        switch(getPlayingCardMode()){
            case Card.BOMBARD :
            {
                return getUnitBombard();
            }
            case Card.ASSAULT : {
                return getUnitAttack();
            
            }
            case Card.VOLLEY :
            {
                return getUnitVolley();
            
            }
            case Card.PURSUIT :
            {
                return Dice.DICE1d6;
            
            }
            
           
            default: return getUnitAttack();
        }
            
    }
    
    public int getUnitAttack() { 
        if(!CardAttack.equals(""))
        return Dice.diceTypeToInt(CardAttack); //if else return wrong value
        else return 0;
    }

    public int getUnitDefence() {
        if(getCardType() == Card.UNIT)
            return Integer.parseInt(CardDefense);
        if(getCardType() == Card.LEADER)
           return getLeaderCombat();
        else return 0;
    }

    public int getUnitRange() {
         if(!CardRange.equals(""))
        return Integer.parseInt(CardRange);
         else return 0;
    }

    public int getUnitBombard() {
         if(!CardBombard.equals(""))
        return Dice.diceTypeToInt(CardBombard); 
         else return 99;
    }
  
    public int getUnitVolley() {
         if(!CardVolley.equals(""))
        return Dice.diceTypeToInt(CardVolley); 
         else return 99;
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

    public int getLeaderCombat() {
         if(!LeaderCombat.equals(""))
        return Integer.parseInt(LeaderCombat);
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
    public ArrayList<String> getPlayingPossibleCardModes() {
        
        ArrayList<String> cardPlayingModes = new ArrayList<String>();
       
        
        if(!this.CardAttack.equals("")) 
        {cardPlayingModes.add(Card.ASSAULT);
         
          }
        if(this.CardRange.equals("1") )
        {
        cardPlayingModes.add(Card.VOLLEY);
 
        }
        if(this.CardRange.equals("2") )
        {
        cardPlayingModes.add(Card.BOMBARD);
                }
 
        return cardPlayingModes;
    }

    public ArrayList<Dice> getDices() {
        return dices;
    }

    public void setDices(ArrayList<Dice> dices) {
        this.dices = dices;
    }

    
    
    
    public void setPlayingCardMode(String playingCardMode) {
        this.playingCardMode = playingCardMode;
    }

    public String getPlayingCardMode() {
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
    public boolean isNotRequredToAdvanceAfterAttack() {
       
        if (this.CardDescr.equals("Not required to advance") )
                return true;
        else return false;
    }
 
    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
    }

    public boolean hasPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }


    /*
    Check if card can be upped
    */
 
    public boolean getAvailableForPhase(Game game){
        int phase=game.getPhase();
        switch(phase){
            case Game.SETUP:
                 return false; 
           
            case Game.DISCARD:
                return true;
              
            case Game.DRAW:
                    if(getHQType() == Card.SCOUT ) 
                        return true;
                    else return false;
            case Game.MOVE:
                /*
                Not unit
                */
                    if(isHQCard()){
                    /*
                    And not these cards
                    */
                      if((getHQType() != Card.REDOUBDT 
                       && getHQType() != Card.REGROUP
                       && getHQType() != Card.SKIRMISH
                       && getHQType()  != Card.WITHDRAW
                              
                      ))
                        return true;
                    }
                    else return false;
              
            case Game.COMBAT: 
                {
                    if(game.getCombat() != null){
                        
                       /*
                        possible card types
                        */                 
                        if(getCardType() == Card.UNIT
                                ||
                                getCardType() == Card.LEADER
                                )
                            return true;
                        
                        
                        if(getCardType() == Card.HQCARD && 
                                (
                                getHQType() == Card.AMBUSH ||
                                getHQType() == Card.WITHDRAW ||
                                getHQType() == Card.SKIRMISH
                                )
                           )
                            return true;

                    }
                                        
                    else{
                         if(getCardType() == Card.UNIT
                                ||
                                getCardType() == Card.LEADER
                                )
                            return true;
                        
                        return getHQType() == Card.WITHDRAW 
                                
                                || getHQType() == Card.COMMITED_ATTACK
                                || getHQType() == Card.ROYAL_ENG;            
                    } 
              
                }
                break;
            case Game.RESTORATION:
                if(isHQCard())
                {
                    if(getHQType() == Card.REDOUBDT ||
                    getHQType() == Card.REGROUP)
                        return true;
                }
               else if(getCardType() == Card.HQCARD || getCardType() == Card.UNIT)
                    return true;
                
              else return false;
                   
        }
      return false;
           
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean canBePlayed(Game game) {
        
        int phase=game.getPhase();
        if(getAvailableForPhase(game))
        {
            switch (getCardType()) {
            
            case Card.LEADER:
            {
                
                
                switch(phase){
                    case Game.DISCARD : return true;
                    case Game.COMBAT :
                    {
                    if(game.getCombat() != null)
                        switch(game.getCombat().getState())
                        {
                            case Combat.PICK_DEFENSE_CARDS:
                                 return true;
                            case Combat.PICK_SUPPORT_CARDS:
                                 return true;
                            case Combat.PICK_SUPPORT_UNIT:
                                 return true;

                            default: return false;
                        }
                    break;    
                    }
                    case Game.RESTORATION :
                        return true;
                    
                    default: return false;
                }
            }
          
            case Card.HQCARD:
                if(game.getPhase() == Game.DISCARD)
                    return true;
                
                switch (getHQType())
                {
                    case Card.FORCED_MARCH :
                    if(game.getCurrentPlayer().hasMoved()) {
                        return true;
                    }
                    break;
                    case Card.SUPPLY :
                    if(game.getCurrentPlayer().hasMoved()) {
                    return true;
                    }
                    break;

                    case Card.GUERRILLAS :
                    if(
                            game.getCardCommandFactory().getOpponentCard().getHQType() == Card.FORCED_MARCH ||
                            game.getCardCommandFactory().getOpponentCard().getHQType() == Card.SUPPLY ||
                            game.getCardCommandFactory().getOpponentCard().getHQType() == Card.REDOUBDT
                            ) {
                    return true;
                    }
                    break;

                    case Card.WITHDRAW :
                    if(game.getCombat() != null) 
                        if(game.getCombat().getState() == Combat.PICK_DEFENSE_CARDS)
                            if(     
                                    (!game.getCombat().getCombatType().equals(Combat.BOMBARD))  
                                    &&
                                     !game.getCombat().getCombatType().equals(Combat.VOLLEY)
                                     )

                                return true;
                            break;                                               


                    case Card.REDOUBDT :
                    {
                        if(game.getPhase() == Game.RESTORATION)
                             return true;

                        break;


                    }

                    case Card.SKIRMISH :
                    {
                       if(game.getCombat() != null) 
                        return game.getCombat().getState() == Combat.PICK_SUPPORT_CARDS;

                    }
                }
                
                break;
            
            case Card.UNIT:
            
                if(game.getPhase() == Game.DISCARD)
                    return true;
                 
                if(!game.getUnitByName(CardName).isEliminated())
                    {
                        if(game.getPhase() == Game.COMBAT)
                        {
                            /*
                            Pick only living unit cards
                            */
                        if(game.getCombat() == null) {
                            return true;
                        }
                            
                        
                        else {
                            switch(game.getCombat().getState()){
                            case Combat.INITIALIZING_COMBAT:
                                return true;
                                
                            case Combat.PICK_DEFENSE_CARDS:
                                if(game.getCombat().getDefendingUnit().equals(this) )
                                       return true;
                                else 
                                       return false;
                            case Combat.PICK_SUPPORT_CARDS:
                            {
                                /*
                                Check if support unit matches card with assault mode
                                */
                                for(Unit supportUnit: game.getCombat().getSupportingUnits())
                                {
                                    if(supportUnit.equals(this)) 
                                    {
                                        for(String cardMode:getPlayingPossibleCardModes() )
                                            {
                                                if(cardMode == Card.ASSAULT) 
                                                    return true;
                                            }
                                    }
                                }
                                /*
                                Check if attack unit matches card with assault mode
                                */
                                if(game.getCombat().getAttackingUnit().equals(this))
                                {
                                     for(String cardMode:getPlayingPossibleCardModes() )
                                            {
                                                if(cardMode == Card.ASSAULT) 
                                                    return true;
                                            }
                                }   
                                return false;
                            }
                            case   Combat.END_COMBAT : 
                                return false;
                        }
                        }
                        }
                        else if(game.getPhase() == Game.RESTORATION)
                            return true;
                        /*
                        Card is playing outside Combat /Restoration / Discard
                        */
                        else
                            return false;
                    }
                /*
                Unit is elimitated
                */
                else return false;
                
            }
        }
        /*
        Is not avalaible for phase
        */
        return false;
    }
    
    public void actionOnSelection(Game game, CommandQueue cmdQueue){
    
    switch(getCardType()){
        case Card.HQCARD:
        {
            switch (getHQType()){
                case Card.FORCED_MARCH:
                {
                    if(game.getPhase() == Game.MOVE){
                    game.getCurrentPlayer().getLastMovedUnit().setSelected(true);
                    game.mapInputHandler.setState(MapInputStateHandler.PICK_MOVE_POSITION_BY_CARD);
                    
                    }
                    break;
                }
                
                case Card.WITHDRAW:
                {
                    if(game.getPhase() == Game.COMBAT && 
                            game.getCardCommandFactory().getAttackedUnit()!= null
                            ){
                    game.getCardCommandFactory().getAttackedUnit().setSelected(true);
                    game.getCardCommandFactory().getAttackedUnit().setRetriving(true);
                    game.getCardCommandFactory().setPlayingCard(this);
                    game.getCombat().setState(Combat.WITHRDAW);
                    //game.getCurrentPlayer().setActive(true);
                    LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na MapInputStateHandler.PICK_MOVE_POSITION_BY_CARD");
                    game.mapInputHandler.setState(MapInputStateHandler.PICK_MOVE_POSITION_BY_CARD);
                    }
                    break;
                }
                case Card.SUPPLY:
                {
                    break;
                }
                
                case Card.GUERRILLAS:
                {
                        game.getCardCommandFactory().setPlayingCard(this);
                        cmdQueue.storeAndExecuteAndSend(game.getCardCommandFactory().createGuerrillaCardCommand());
                    
                    break;
                }
                
                case Card.REDOUBDT:
                {
                        game.getCardCommandFactory().setPlayingCard(this);
                        LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT");
                        game.mapInputHandler.setState(MapInputStateHandler.PICK_UNIT_BY_CARD);
                    
                    break;
                }
                
            
            } break;
        }
        case Card.UNIT:
        {
                if(game.getPhase() == Game.COMBAT)
                    if(game.getCombat() != null)
                    {        if(game.getCombat().getDefendingUnit() != null)
                        {
                        game.getCombat().addDefenceCard(this);
                        }
                    }       
                    else {
                        game.getCurrentPlayerUnitByName(getCardName()).setSelected(true);
                            if(getPlayingCardMode()!= null)
                                game.getCardCommandFactory().calculateAttackingPositions(game.getSelectedUnit());
                    }
                else if (game.getPhase() == Game.RESTORATION)
                {
                game.getCurrentPlayerUnitByName(getCardName()).setSelected(true);
                }
                    
                
                break;
        } 
        
        case Card.LEADER:
        {
             if(game.getPhase() == Game.COMBAT)
                 if(game.getCombat() != null)
                     if(game.getCombat().getState() == Combat.PICK_DEFENSE_CARDS)
                     {
                        game.getCombat().addDefenceCard(this);
                     }
                     
                     if(game.getCombat().getState() == Combat.PICK_SUPPORT_CARDS)
                     {
                        game.getCombat().setState(Combat.PICK_SUPPORT_UNIT);
                        game.getCombat().setSupportingLeader(this);
                        
                     }
                break;
        } 
         
    }
    
    }
    public void actionOnDeselection(Game game){
    
        switch(getCardType()){
        case Card.HQCARD:
        {
            switch (getHQType()){
                case Card.FORCED_MARCH:
                {
                     if(game.getPhase() == Game.MOVE)
                     {
                            if(game.getCurrentPlayer().getLastMovedUnit()!= null)
                             game.getCurrentPlayer().getLastMovedUnit().setSelected(false);
                           
                     }
                    break;
                }
                case Card.WITHDRAW:
                {
                    if(game.getPhase() == Game.COMBAT && 
                            game.getCardCommandFactory().getAttackedUnit()!= null
                            ){
                    game.getCardCommandFactory().getAttackedUnit().setSelected(false);
                    game.getCardCommandFactory().getAttackedUnit().setRetriving(false);
                    game.getCardCommandFactory().setPlayingCard(null);
                    LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapInputStateHandler.NOSELECTION");
                    game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
                    }
                    break;
                }
                case Card.SUPPLY:
                {
                     game.unselectAllUnits();
                }
                
                case Card.REDOUBDT:
                {
                        LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na MapInputStateHandler.NOSELECTION");
                        game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
                    
                    break;
                }
            
            }
            break;
        }
        case Card.UNIT:
        {   
              if(game.getPhase() == Game.COMBAT)
                   if(game.getCombat() != null)
                   {  
                       if(game.getCombat().getDefendingUnit() != null)
                         {
                     game.getCombat().removeDefenceCard(this);
                         }
                    }
                    else
                        {
                         if(game.getCurrentPlayerUnitByName(getCardName())!= null)
                             game.getCurrentPlayerUnitByName(getCardName()).setSelected(false);
                        }
               else if (game.getPhase() == Game.RESTORATION)
                {
                game.getCurrentPlayerUnitByName(getCardName()).setSelected(false);
                }
              
                break;
        }
        case Card.LEADER:
        {
             if(game.getPhase() == Game.COMBAT)
                 if(game.getCombat() != null)
                     if(game.getCombat().getState() == Combat.PICK_DEFENSE_CARDS)
                     {
                        game.getCombat().removeDefenceCard(this);
                     }
                     
                     if(game.getCombat().getState() == Combat.PICK_SUPPORT_UNIT)
                     {
                        game.getCombat().setState(Combat.PICK_SUPPORT_CARDS);
                        game.getCombat().setSupportingLeader(null);
                        
                     }
                break;
        } 
    }
    
    }
    
    public boolean isCardNotFoundInNation() {
        return cardNotFound;
    }
    
    public void setCanBeCanceled(boolean canBeCanceled) {
        this.canBeCanceled = canBeCanceled;
    }
    
    public boolean isMouseOverCard() {
        return mouseOverCard;
    }

    public void setMouseOverCard(boolean mouseOverCard) {
        this.mouseOverCard = mouseOverCard;
    }
 
    @Override
    public String toString()
    {
    
    return ("ID [" + CardID + "] " + getCardName() + ( getPlayingCardMode()!=null  ?  " playing mode " + getPlayingCardMode() : "") 
            + ( isSelected() ?  " selected " : " deselected")
            )  ;
    
    }
    
    
    
}
