/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.io.Serializable;
import manouvre.game.Card;
import manouvre.game.Game;

/**
 *
 * @author xeon
 */
public class CardMultipleSelectionState implements CardInputState, Serializable{

    @Override
    public void handleInput(Card card, Game game) {
        
        if(!card.isSelected()) 
        {
            card.setSelected(true);
            game.getCurrentPlayer().getHand().selectionSeq.add(card);
        }
        else 
        {
            card.setSelected(false);
            game.getCurrentPlayer().getHand().selectionSeq.remove(card); 
     
        }       
        
        
    }

    @Override
    public String toString() {
        return "MULTIPLE_SELECTION";
    }
    
}
