/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;
import manouvre.events.EventType;
import manouvre.game.Game;

import manouvre.interfaces.Command;

/**
 *
 * @author Bartosz
 */
public class DrawCardCommand implements Command {
   
    int numberOfDrawnCards;
    String senderPlayerName;

    public DrawCardCommand(int numberOfDrawnCards, String senderPlayerName) {
       this.numberOfDrawnCards=numberOfDrawnCards;
       this.senderPlayerName=senderPlayerName;
       
       
    }
    
  

    @Override
    public void execute(Game game) {  

    game.getPlayerByName(senderPlayerName).getDrawPile().moveTopXCardsTo(
            numberOfDrawnCards,
            game.getPlayerByName(senderPlayerName).getHand()
            );
    //game.getPlayerByName(senderPlayerName).getHand().sortCard();
    game.getPlayerByName(senderPlayerName).setDraw(true);
    game.notifyAbout(EventType.CARDS_DRAWNED);
    }
    
    @Override
    public void undo(Game game){
      
    }
    
    @Override
    public String logCommand(){
        return new String(senderPlayerName + " has drawn " + numberOfDrawnCards + " cards");

    }

    @Override
    public Type getType() {
        return Command.Type.DRAW_CARD;
    }
}

