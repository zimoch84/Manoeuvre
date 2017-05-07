/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import manouvre.game.commands.CardCommands;
import manouvre.game.commands.RestoreUnitCommand;
import manouvre.game.commands.ThrowDiceCommand;
import manouvre.game.interfaces.CardCommandInterface;
import manouvre.game.interfaces.Command;
import manouvre.game.interfaces.DiceInterface;

/**
 * Class to serve for whole card flow in game.
 * @author Piotr
 */
public class CardCommandFactory extends Observable implements Serializable{
    
    Game game;
    Command attachedCommand;
    CardCommandInterface cardCommand;
    CardCommandInterface incomingCardCommand;

    /*
    Notify observer passed arg
    */
    public final static  String ATTACK_DIALOG = "ATTACK_DIALOG";
    public final static String CARD_DIALOG = "CARD_DIALOG";
    public final static String CARD_REJECTED = "CARD_REJECTED";
    public final static String CARD_NOT_REJECTED = "CARD_NOT_REJECTED";
    public final static String DEFENDING_CARDS_PLAYED = "DEFENDING_CARDS_PLAYED";
    public final static String OPPONENT_WITHDRAW = "OPPONENT_WITHDRAW";
    
    
    public final static String COMBAT_NO_RESULT = "COMBAT_NO_RESULT";
    public final static String COMBAT_DEFENDER_TAKES_HIT = "COMBAT_DEFENDER_TAKES_HIT";
    public final static String COMBAT_ATTACKER_TAKES_HIT = "COMBAT_ATTACKER_TAKES_HIT";
    public final static String COMBAT_DEFENDER_ELIMINATE = "COMBAT_ELIMINATE";
    public final static String COMBAT_ATTACKER_ELIMINATE = "COMBAT_ATTCACKER_ELIMINATE";
    public final static String COMBAT_ACCEPTED = "COMBAT_BOMBARD_ACCEPTED";
    
     
    Card playingCard, opponentCard;
    
    ArrayList<Card> pickedAttackingCards;
   
    ArrayList<Position> attackingPositions;
    ArrayList<Card> pickedDefendingCards=new ArrayList<>();
    ArrayList<Card> pickedSupportingCards=new ArrayList<>();
    
    Unit selectedUnit, attackedUnit;
    
    ArrayList<Dice> d6dices;
    ArrayList<Dice> d8dices;
    ArrayList<Dice> d10dices;
    
    
    boolean cancelCardMode;

    public boolean isCancelCardMode() {
        return cancelCardMode;
    }

    public void setCancelCardPopupMode(boolean cancelCardMode) {
        this.cancelCardMode = cancelCardMode;
    }
    
    public CardCommandFactory(Game game) {
        
        this.game = game;
        d6dices = new ArrayList<>();
        d8dices = new ArrayList<>();
        d10dices = new ArrayList<>(); 
       // fakeDices();
        
    }

    public ArrayList<Dice> getD6dices() {
        return d6dices;
    }

    public void setD6dices(ArrayList<Dice> d6dices) {
        this.d6dices = d6dices;
    }

    public ArrayList<Dice> getD8dices() {
        return d8dices;
    }

    public void setD8dices(ArrayList<Dice> d8dices) {
        this.d8dices = d8dices;
    }

    public ArrayList<Dice> getD10dices() {
        return d10dices;
    }

    public void setD10dices(ArrayList<Dice> d10dices) {
        this.d10dices = d10dices;
    }

    
    public ArrayList<Position> getAttackingPositions() {
        return attackingPositions;
    }

    public void setAttackingPositions(ArrayList<Position> attackingPositions) {
        this.attackingPositions = attackingPositions;
    }

    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(Unit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    public Command getAttachedCommand() {
        if(attachedCommand!=null)
        return attachedCommand;
        return (Command)null;
    }

    public void setAttachedCommand(Command attachedCommand) {
        this.attachedCommand = attachedCommand;
    }

    public Card getOpponentCard() {
        if(opponentCard!=null)
        return opponentCard;
        return (Card)null;
    }

    public void setOpponentCard(Card opponentCard) {
        this.opponentCard = opponentCard;
    }
    
    public void awakeObserver(){
         setChanged();
    }
    
    public Card getPlayingCard() {
        return playingCard;
    }

    public void setPlayingCard(Card playingCard) {
        this.playingCard = playingCard;
    }
    
    public void resetPlayingCard(){
        
        if(playingCard != null) 
            this.playingCard.setSelected(false) ;
       else 
             this.playingCard = null;
        
        game.getCurrentPlayer().setPlayingCard(false);
    }

    public Unit getAttackedUnit() {
        return attackedUnit;
    }

    public void setAttackedUnit(Unit attackedUnit) {
        this.attackedUnit = attackedUnit;
        setChanged();
    }
    
    
    
        /*
    Funtion to get current playing card
    */
    public Card getCurrentPlayedCard(){
        
        return playingCard;
    
    }
    
    public void calculateAttackingPositions(){
        ArrayList<Position> attackPossiblePositions;
        ArrayList<Position>  attackPositions  = new ArrayList<Position>();      
        
        

        if(playingCard.getPlayingCardMode()== Card.ASSAULT 
        || playingCard.getPlayingCardMode()== Card.VOLLEY)
            {
            attackPossiblePositions= game.getPossibleAssault(selectedUnit);
            for(Position checkPosition : attackPossiblePositions)
            {
                if(game.checkOpponentPlayerUnitAtPosition(checkPosition))
                    attackPositions.add(checkPosition);
            }

                setAttackingPositions(attackPositions);
            }
        else if (playingCard.getPlayingCardMode()== Card.BOMBARD) 
        {    
        {
            attackPossiblePositions= game.getLOS(selectedUnit, 2);
            for(Position checkPosition : attackPossiblePositions)
            {
                if(game.checkOpponentPlayerUnitAtPosition(checkPosition))
                    attackPositions.add(checkPosition);
            }

                setAttackingPositions(attackPositions);
            }
        }

   }   
            
    
    public void resetFactory()
    {
        setAttachedCommand(null);
        setAttackingPositions(null);
        resetPlayingCard();
        setSelectedUnit(null);
        //setD10dices(null);
       // setD8dices(null);
       // setD6dices(null);
        
        
    }
        
    /*
    Crate card command based on Card
    */
    public Command createCardCommand() {
    
    playingCard= getCurrentPlayedCard();
        
    switch (playingCard.getCardType() ) {
    
        case Card.HQCARD :
        {
            switch(playingCard.getHQType()){
            case Card.FORCED_MARCH : {
                setCardCommand( new CardCommands.ForcedMarchCommand(attachedCommand, playingCard, game.getCurrentPlayer().getName()) );
                return getCardCommand();
            }
            case Card.WITHDRAW : {
                /*
                We create it in 2 steps - first in attack dialog we choose withdraw action button which trigger another dialog window 
                when we have to choose where witdraw to.
                
                */
               
                setCardCommand( new CardCommands.WithrdawCommand(attachedCommand, playingCard, game.getCurrentPlayer().getName()) );
                return getCardCommand();
            }
            case Card.SUPPLY : {
                setCardCommand( new CardCommands.ForcedMarchCommand(attachedCommand, playingCard, game.getCurrentPlayer().getName()) );
                return getCardCommand();
            }
            default: {
                setCardCommand(new CardCommands.MoveToTableCommand(playingCard, game.getCurrentPlayer().getName()));
                return  getCardCommand();
            } //if any card selected temp
        }
        }  
        case Card.UNIT :
        {
            
            if(game.getPhase() == Game.COMBAT)
            return new CardCommands.AttackCommand(
                    getAttackedUnit(), 
                    playingCard,
                    game.getCurrentPlayer().getName(),
                    game.getSelectedUnit(), 
                    game.getMap().getTerrainAtPosition(game.getSelectedUnit().getPosition()), 
                    game.getMap().getTerrainAtPosition(getAttackedUnit().getPosition())
            );
            
            return new RestoreUnitCommand(game.getCurrentPlayer().getName(), game.getSelectedUnit(), playingCard);
            
            
        }    
            
            
    default: {
        setCardCommand(new CardCommands.MoveToTableCommand(playingCard, game.getCurrentPlayer().getName())) ;
        return  getCardCommand();
    } //if any card selected temp
    }
    }
    
    
    public Command resetFactoryCommand(){
        return new CardCommands.ResetCardFactory(game.getCurrentPlayer().getName());
    }
    
    public Command createRejectCardCommand(){
        return new CardCommands.RejectCardCommand(opponentCard, game.getCurrentPlayer().getName(), getIncomingCardCommand());
    }
    public Command createDoNotRejectCardCommand(){
        return new CardCommands.DoNotRejectCardCommand(opponentCard, game.getCurrentPlayer().getName());
    }
    
     public Command createMoveToHandCommand(CardSet cardSet, int numberOfChosenCards, boolean deleteCards){
        return new CardCommands.MoveToHandCommand(cardSet,numberOfChosenCards, game.getCurrentPlayer().getName(), deleteCards);
    }
     
    public Command createCleanTableCommand(){
        return new CardCommands.CleanTableCommand();
    } 
     
    public CardCommandInterface getCardCommand() {
        return cardCommand;
    }

    public void setCardCommand(CardCommandInterface cardCommand) {
        this.cardCommand = cardCommand;
    }

    public Command createThrowDiceCommand(){
    
        return new ThrowDiceCommand(game.getCurrentPlayer().getName(), getAttackingCards());
    }

    public ArrayList<Card> getAttackingCards() {
        return pickedAttackingCards;
    }

    public void setAttackingCards(ArrayList<Card> attackingCards) {
        this.pickedAttackingCards = attackingCards;
    }
     public void addPickedAttackingCard(Card card) {
        this.pickedAttackingCards.add(card);
    }
     public void removePickedAttackingCard(Card card) {
        this.pickedAttackingCards.remove(card);
    }

    void prepareDices(ArrayList<Card> attackingCards){
    
        for(Card checkCard: attackingCards )
        {
        switch(checkCard.getUnitAttack()){
        
            case DiceInterface.DICE1d6:{
                d6dices.add(new Dice(Dice.D6));
                break;
            }
            case DiceInterface.DICE2d6:{
                d6dices.add(new Dice(Dice.D6));
                d6dices.add(new Dice(Dice.D6));
                break;
            }
            case DiceInterface.DICE1d8:{
                d8dices.add(new Dice(Dice.D8));
                break;
            }
            case DiceInterface.DICE2d8:{
                d8dices.add(new Dice(Dice.D8));
                d8dices.add(new Dice(Dice.D8));
                break;
            }
            case DiceInterface.DICE1d10:{
                d10dices.add(new Dice(Dice.D8));
                break;
            }
            case DiceInterface.DICE2d10:{
                d10dices.add(new Dice(Dice.D10));
                d10dices.add(new Dice(Dice.D10));
                break;
            }
           
        }
        }
        
        for(Dice dice : d6dices){dice.setResult(6);}
        for(Dice dice : d8dices){dice.setResult(8);}
        for(Dice dice : d10dices){dice.setResult(10);}
   
        game.getCardCommandFactory().setD6dices(d6dices);
        game.getCardCommandFactory().setD8dices(d8dices);
        game.getCardCommandFactory().setD10dices(d10dices);
    }
    
    void fakeDices()
    {
    d6dices.add(new Dice(Dice.D6));
    d6dices.add(new Dice(Dice.D6));
    d6dices.add(new Dice(Dice.D6));
    d6dices.add(new Dice(Dice.D6));
    d10dices.add(new Dice(Dice.D10));
    d10dices.add(new Dice(Dice.D10));
    d8dices.add(new Dice(Dice.D8));
    d8dices.add(new Dice(Dice.D8));
    
    /*
        for(Dice dice : d6dices){dice.setResult(6);}
        for(Dice dice : d8dices){dice.setResult(8);}
        for(Dice dice : d10dices){dice.setResult(10);}
*/        
        for(Dice dice : d6dices){dice.generateResult();}
        for(Dice dice : d8dices){dice.generateResult();}
        for(Dice dice : d10dices){dice.generateResult();}
    }
    
     public CardCommandInterface getIncomingCardCommand() {
        return incomingCardCommand;
    }

    public void setIncomingCardCommand(CardCommandInterface incomingCardCommand) {
        this.incomingCardCommand = incomingCardCommand;
    }

    public ArrayList<Card> getPickedDefendingCards() {
        return pickedDefendingCards;
    }

    public void setPickedDefendingCards(ArrayList<Card> defendingCards) {
        this.pickedDefendingCards = defendingCards;
    }
    public void addPickedDefendingCard(Card cardToAdd) {
        this.pickedDefendingCards.add(cardToAdd);
    }
    public void clearDefendingCards() {
        this.pickedDefendingCards.clear();
    }
    public void removePickedDefendingCard(Card cardToRemove) {
        this.pickedDefendingCards.remove(cardToRemove);
    }

    public Command createMoveDefensiveCardsToTableCommand(ArrayList<Card> cards){
        return new CardCommands.MoveDefensiveCardsToTableCommand(cards, game.getCurrentPlayer().getName());
    }
    
    public Command createOutcomeCombatCommand(){
      
        Combat combat = game.getCombat();
        ThrowDiceCommand td = new ThrowDiceCommand(game.getCurrentPlayer().getName(), combat.getAttackCards());
        return new CardCommands.CombatOutcomeCommand(   game.getCurrentPlayer().getName(),combat, td    );
    }
    
    public ArrayList<Dice> getAllDices(){
        ArrayList<Dice> returnDices = new ArrayList<>();
    
        returnDices.addAll(d6dices);
        returnDices.addAll(d8dices);
        returnDices.addAll(d10dices);
    
    return returnDices;
    }
    
}
