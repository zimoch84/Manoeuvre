/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;
import manouvre.game.Game;

import manouvre.game.interfaces.Command;

/**
 *
 * @author Bartosz
 */
public class DrawCardCommand implements Command {
   
    int numberOfDrawnCards;

    public DrawCardCommand(int numberOfDrawnCards) {
       this.numberOfDrawnCards=numberOfDrawnCards;

        
    }
    
  

    @Override
    public void execute(Game game) {  //fing opponent and draw the card
        
            game.getOpponentPlayer().getHand().addCardsFromTheTopOfOtherSet(numberOfDrawnCards, game.getOpponentPlayer().getDrawPile(), false);
            game.getOpponentPlayer().getHand().sortCard();
       
    }
    
    @Override
    public void undo(Game game){
      
   
      
    }
    
}

