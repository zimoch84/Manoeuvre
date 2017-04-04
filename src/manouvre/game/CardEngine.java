/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import manouvre.game.commands.CardCommands;
import manouvre.game.interfaces.Command;
import manouvre.gui.CardGUI;

/**
 * Class to serve for whole card flow in game.
 * @author Piotr
 */
public class CardEngine implements Serializable{
    
    Player player;
    Command attachedCommand, cardCommand;
     
    Card playingCard;

    public CardEngine(Player player) {
        this.player = player;
        
    }
    
    
    
    /*
    Getters and setters
    
    */

    public Command getAttachedCommand() {
        return attachedCommand;
    }

    public void setAttachedCommand(Command attachedCommand) {
        this.attachedCommand = attachedCommand;
    }

    public Card getPlayingCard() {
        return playingCard;
    }

    public void setPlayingCard(Card playingCard) {
        this.playingCard = playingCard;
    }
    
    
        /*
    Funtion to get current playing card
    */
    public Card getCurrentPlayedCard(){
    
        
        /*
        To do - return played card
        
        Temporaryly its Forced March
        
        */
        return new Card(3);
    
    }
    
    
    /*
    Crate card command based on Card
    */
    public Command createCardCommand() throws Exception{
    
    playingCard= getCurrentPlayedCard();
        
    switch (playingCard.getCardType() ) {
    
    
        case Card.FORCED_MARCH : return new CardCommands.ForcedMarchCommand(attachedCommand, playingCard) ; 
        default: throw new Exception("There is no such card type");
    }
    }
    
    
    
    
}
