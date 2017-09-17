/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.io.Serializable;
import manouvre.game.Card;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.commands.CommandQueue;

/**
 *
 * @author xeon
 */
public class CardMultipleSelectionState implements CardInputState, Serializable{

    @Override
    public void handleInput(Card card, Game game,CommandQueue cmdQueue) {
        
        if(!card.isSelected()) 
        {   
            card.setSelected(true);
            if(game.getPhase() == Game.DISCARD)
            {
            game.getCurrentPlayer().getHand().selectionSeq.add(card);
            }
            else if(game.getPhase() == Game.COMBAT)
            {
                if(game.getCombat() != null)
                    if(game.getCombat().getState() == Combat.PICK_DEFENSE_CARDS)
                    {
                        game.getCurrentPlayer().getHand().selectionSeq.add(card);
                        card.actionOnSelection(game, cmdQueue);
                        
                    }
                    else if (game.getCombat().getState() == Combat.PICK_SUPPORTING_CARDS)
                    {
                        /*
                        TODO , picking leader triggers supporting mode
                        */
                        
                        game.getCurrentPlayer().getHand().selectionSeq.add(card);   
                        card.actionOnSelection(game, cmdQueue);
                    }
                        
                
                                
            }
                
            
        }
        else 
        {
            card.setSelected(false);
            if(game.getPhase() == Game.DISCARD)
            {
            
            game.getCurrentPlayer().getHand().selectionSeq.remove(card); 
            }
            
            else if(game.getPhase() == Game.COMBAT)
            {
                if(game.getCombat() != null)
                    if(game.getCombat().getState() == Combat.PICK_DEFENSE_CARDS)
                    {
                        game.getCurrentPlayer().getHand().selectionSeq.remove(card);
                        card.actionOnDeselection(game);
                    }
                    else if (game.getCombat().getState() == Combat.PICK_SUPPORTING_CARDS)
                    {
                        /*
                        TODO , picking leader triggers supporting mode
                        */
                        game.getCurrentPlayer().getHand().selectionSeq.remove(card);   
                    }
                        
                                
            }
     
        }       
        
        
    }

    @Override
    public String toString() {
        return "MULTIPLE_SELECTION";
    }
    
}
