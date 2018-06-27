/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import manouvre.commands.CardCommands;
import manouvre.commands.DiscardCardCommand;
import manouvre.commands.DrawCardCommand;
import manouvre.commands.RestoreUnitCommand;
import manouvre.interfaces.CardCommandInterface;
import manouvre.interfaces.Command;


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
    
    
    
    
    Card playingCard, opponentCard;
    /*
    TODO remove?
    */
    ArrayList<Card> opponentCards=new ArrayList<>();  //oponent attacking cards
    ArrayList<Card> supportAttackCards=new ArrayList<>(); //current player attacking cards
   
    ArrayList<Position> attackingPositions;
   
    
    Unit selectedUnit, attackedUnit;
    
    ArrayList<Dice> d6dices;
    ArrayList<Dice> d8dices;
    ArrayList<Dice> d10dices;
    
    
    boolean cancelCardMode;
    int minFromDices=0;
    
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
        //fakeDices();
        
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
        this.opponentCards.add(opponentCard);
    }
    
    public void notifyAbout(Object eventType)
    {
        setChanged();
        notifyObservers(eventType);
    }
    
    public Card getPlayingCard() {
        return playingCard;
    }

    public void setPlayingCard(Card playingCard) {
        this.playingCard = playingCard;
    }
    
    public void resetPlayingCard(){
        if(playingCard != null) 
        {
            playingCard.actionOnDeselection(game);
            if(playingCard != null)
                this.playingCard.setSelected(false) ;
            this.playingCard = null;
            
        }
        
    }

    public Unit getAttackedUnit() {
        return attackedUnit;
    }

    public void setAttackedUnit(Unit attackedUnit) {
        this.attackedUnit = attackedUnit;
        setChanged();
    }
   
    public void calculateAttackingPositions(Unit attackingUnit){
        ArrayList<Position> attackPossiblePositions;
        ArrayList<Position>  attackPositions  = new ArrayList<Position>();      
        
        
        if(playingCard.getPlayingCardMode().equals(Card.ASSAULT) 
        || playingCard.getPlayingCardMode().equals(Card.VOLLEY))
            {
            attackPossiblePositions= game.getPossibleAssault(attackingUnit);
            for(Position checkPosition : attackPossiblePositions)
            {
                if(game.checkOpponentPlayerUnitAtPosition(checkPosition))
                    attackPositions.add(checkPosition);
            }

                setAttackingPositions(attackPositions);
            }
        else if (playingCard.getPlayingCardMode().equals(Card.BOMBARD)) 
        {    
        {
            attackPossiblePositions= game.getLOS(attackingUnit, 2);
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
        setOpponentCard(null);
        setD10dices(null);
        setD8dices(null);
        setD6dices(null);
        
        
    }
    
    
        
    /*
    Crate card command based on Card
    */
    public Command createCardCommand() {
    
    if(playingCard != null)
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
                case Card.SKIRMISH : {
                    setCardCommand( new CardCommands.SkirmishCommand(game.getCurrentPlayer().getName(),
                            game.getOpponentPlayer().getName(), playingCard, attachedCommand ) );
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
            else  if(game.getPhase() == Game.RESTORATION)
            {
                Command restoreCommand = new RestoreUnitCommand(game.getCurrentPlayer().getName(),
                        game.getSelectedUnit(),
                        playingCard);
                return restoreCommand;
                
            }
            
        }    
            
            
    default: {
        setCardCommand(new CardCommands.MoveToTableCommand(playingCard, game.getCurrentPlayer().getName())) ;
        return  getCardCommand();
    } //if any card selected temp
    }
   /*
    If we dont have command
    */
    throw new UnsupportedOperationException("we dont have command"); //To change body of generated methods, choose Tools | Templates.
    
    }
    
    public Command createDrawCommand(){
    
        int cardsToDraw = 5 - game.getCurrentPlayer().getHand().size();
        DrawCardCommand drawCard = new DrawCardCommand(cardsToDraw, game.getCurrentPlayer().getName());
        return  drawCard;
    }
    
    
    public Command createDiscardCommand(){
    
        return  new DiscardCardCommand(game.getCurrentPlayer().getHand().getSelectedCards(), game.getCurrentPlayer().getName());
    }
    
    public Command resetFactoryCommand(){
        return new CardCommands.ResetCardFactory(game.getCurrentPlayer().getName());
    }
    
    public Command createGuerrillaCardCommand(){
        return new CardCommands.GuerrillaCardCommand(getPlayingCard(), game.getCurrentPlayer().getName(), getIncomingCardCommand());
    }
    public Command createDoNotRejectCardCommand(){
        return new CardCommands.DoNotRejectCardCommand(opponentCard, game.getCurrentPlayer().getName());
    }
    
     public Command createMoveToHandCommand(CardSet cardSet, int numberOfChosenCards, boolean deleteCards){
        return new CardCommands.MoveToHandCommand(cardSet,numberOfChosenCards, game.getCurrentPlayer().getName(), deleteCards);
    }
     
    public Command createCleanTableCommand(){
        return new CardCommands.CleanTableCommand(game.getCurrentPlayer().getName());
    } 
     
    public CardCommandInterface getCardCommand() {
        return cardCommand;
    }

    public void setCardCommand(CardCommandInterface cardCommand) {
        this.cardCommand = cardCommand;
    }

    public ArrayList<Card> getAttackingCards() {
        return supportAttackCards;
    }

    public void setAttackingCards(ArrayList<Card> attackingCards) {
        this.supportAttackCards = attackingCards;
    }
     public void addPickedAttackingCard(Card card) {
        this.supportAttackCards.add(card);
    }
     public void removePickedAttackingCard(Card card) {
        this.supportAttackCards.remove(card);
    }
       /*
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
    
      
        for(Dice dice : d6dices){dice.generateResult();}
        for(Dice dice : d8dices){dice.generateResult();}
        for(Dice dice : d10dices){dice.generateResult();}
    }
    */
     public CardCommandInterface getIncomingCardCommand() {
        return incomingCardCommand;
    }

    public void setIncomingCardCommand(CardCommandInterface incomingCardCommand) {
        this.incomingCardCommand = incomingCardCommand;
    }

    
    public Command createOutcomeCombatCommand(){
      
        Combat combat = game.getCombat();
       
        return new CardCommands.CombatOutcomeCommand(   game.getCurrentPlayer().getName(),combat);
    }
    
    public ArrayList<Dice> getAllDices(){
        ArrayList<Dice> returnDices = new ArrayList<>();
    
        returnDices.addAll(d6dices);
        returnDices.addAll(d8dices);
        returnDices.addAll(d10dices);
    
    return returnDices;
    }
    
}
