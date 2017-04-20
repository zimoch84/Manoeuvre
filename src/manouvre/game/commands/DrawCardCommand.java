/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;
import manouvre.game.Game;
import manouvre.game.Param;

import manouvre.game.interfaces.Command;

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
         if(!game.isServer())
         game.getCurrentPlayer().getName(); //temp just for check where we are
            game.getPlayerByName(senderPlayerName).getHand().addCardsFromTheTopOfOtherSet(
                    numberOfDrawnCards, game.getPlayerByName(senderPlayerName).getDrawPile(), false, true);
            game.getPlayerByName(senderPlayerName).getHand().sortCard();
            game.getPlayerByName(senderPlayerName).setDraw(true);
            
     
    }
    
    @Override
    public void undo(Game game){
      
    }
    
    @Override
    public String logCommand(){
        return new String(senderPlayerName + " has drawn " + numberOfDrawnCards + " cards");

    }

    @Override
    public int getType() {
        return Param.DRAW_CARD;
    }
}

