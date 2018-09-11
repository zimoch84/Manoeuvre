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
    
    private Game game;
    private Command attachedCommand;
    private CardCommand cardCommand;


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
    public Command createHQCardCommand(Card playingCard, Unit movedUnit) {
    
    if(playingCard != null)
    switch (playingCard.getType() ) {
        case Card.HQCARD :
            switch(playingCard.getHQType()){
                case Card.FORCED_MARCH : 
                    setCardCommand( new CardCommands.ForcedMarchCommand(attachedCommand, movedUnit, playingCard, game.getCurrentPlayer().getName()) );
                    return getCardCommand();
               
                case Card.WITHDRAW : 
                    /*
                    We create it in 2 steps - first in attack dialog we choose withdraw action button which trigger another dialog window 
                    when we have to choose where witdraw to.
                    */
                    setCardCommand( new CardCommands.WithrdawCommandByCard(attachedCommand, playingCard, game.getCurrentPlayer().getName()) );
                    return getCardCommand();
                
                case Card.SUPPLY : 
                    setCardCommand( new CardCommands.ForcedMarchCommand(attachedCommand, movedUnit, playingCard, game.getCurrentPlayer().getName()) );
                    return getCardCommand();
                
                case Card.SKIRMISH : 
                    setCardCommand( new CardCommands.SkirmishCommand(game.getCurrentPlayer().getName(),
                            game.getOpponentPlayer().getName(), playingCard, movedUnit, attachedCommand ) );
                    return getCardCommand();
                
                
                default: 
                    setCardCommand(new CardCommands.MoveToTableCommand(playingCard, game.getCurrentPlayer().getName()));
                    return  getCardCommand();
                
            }
                  
        default: 
            setCardCommand(new CardCommands.MoveToTableCommand(playingCard, game.getCurrentPlayer().getName())) ;
            return  getCardCommand();
        }
    
     return  getCardCommand();
    }
    public Command createRestorationCommand(Card card, Unit unit){
    
     Command restoreCommand = new RestoreUnitCommand(
             game.getCurrentPlayer().getName(),
                        unit,
                        card);
                return restoreCommand;
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
    public Command createAcceptCardCommand(){
        Card opponentCard = game.getOpponentPlayer().getTablePile().getLastCard(false);
        
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

    public Command createOutcomeCombatCommand(){
        Combat combat = game.getCombat();
            return new CardCommands.CombatOutcomeCommand(  game.getCurrentPlayer().getName(),combat);
    }
    
}
