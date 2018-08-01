/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;

import manouvre.commands.CardCommands;
import manouvre.commands.DiscardCardCommand;
import manouvre.commands.DrawCardCommand;
import manouvre.commands.RestoreUnitCommand;
import manouvre.interfaces.Command;
import manouvre.interfaces.CardCommand;


/**
 * Class to serve for whole card flow in game.
 * @author Piotr
 */
public class CardCommandFactory implements Serializable{
    
    Game game;
    Command attachedCommand;
    CardCommand cardCommand;
    CardCommand incomingCardCommand;
    /*
    Notify observer passed arg
    */
    Card opponentCard;
    /*
    TODO remove?
    */
    ArrayList<Card> opponentCards=new ArrayList<>();  //oponent attacking cards
    ArrayList<Card> supportAttackCards=new ArrayList<>(); //current player attacking cards
    

    int minFromDices=0;

    public CardCommandFactory(Game game) {
        this.game = game;
    }
    
    
    public Command getAttachedCommand() {
        if(attachedCommand!=null)
        return attachedCommand;
        return (Command)null;
    }

    public void setAttachedCommand(Command attachedCommand) {
        this.attachedCommand = attachedCommand;
    }

    public void resetFactory()
    {
        setAttachedCommand(null);
    }
    /*
    Crate card command based on Card
    */
    public Command createCardCommand(Card playingCard) {
    
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
                    setCardCommand( new CardCommands.WithrdawCommandByCard(attachedCommand, playingCard, game.getCurrentPlayer().getName()) );
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
                    game.getCurrentPlayer().getName(),
                    game.getCombat()
                    
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
    
    public Command createGuerrillaCardCommand(Card playingCard){
        return new CardCommands.GuerrillaCardCommand(playingCard, game.getCurrentPlayer().getName());
    }
    public Command createDoNotRejectCardCommand(){
        return new CardCommands.AcceptCardCommand(opponentCard, game.getCurrentPlayer().getName());
    }
    
    public Command createCleanTableCommand(){
        return new CardCommands.CleanTableCommand(game.getCurrentPlayer().getName());
    } 
     
    public CardCommand getCardCommand() {
        return cardCommand;
    }

    public void setCardCommand(CardCommand cardCommand) {
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
     
     public CardCommand getIncomingCardCommand() {
        return incomingCardCommand;
    }

    public void setIncomingCardCommand(CardCommand incomingCardCommand) {
        this.incomingCardCommand = incomingCardCommand;
    }

    
    
    public Command createOutcomeCombatCommand(){
      
        Combat combat = game.getCombat();
       
        return new CardCommands.CombatOutcomeCommand(   game.getCurrentPlayer().getName(),combat);
    }
    
}
