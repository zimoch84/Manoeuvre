/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import manouvre.game.Card;
import manouvre.game.Game;

/**
 *
 * @author xeon
 */
public class CardMultipleSelectionState implements CardInputState{

    @Override
    public void handleInput(Card card, Game game) {
        
        if(!card.isSelected()) 
        {
            card.setSelected(true);
            game.getCurrentPlayer().getHand().selectionSeq.add((Integer)card.getCardID());
        }
        else 
        {
            card.setSelected(false);
            game.getCurrentPlayer().getHand().selectionSeq.remove((Integer)card.getCardID()); 
     
        }       
        
        
    }
    
}
