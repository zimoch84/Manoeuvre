/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import manouvre.game.CardPlayingHandler;
import java.io.Serializable;
import manouvre.game.Card;
import manouvre.game.Game;
import manouvre.commands.CommandQueue;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author xeon
 */
public class CardMultipleSelectionState implements CardInputState, Serializable{
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(CardMultipleSelectionState.class.getName());  
    
    CardPlayingHandler cardHandler;


    public CardMultipleSelectionState(CardPlayingHandler cardHandler) {
        this.cardHandler = cardHandler;
      
    }
    
    @Override
    public void handleInput(Card card, Game game,CommandQueue cmdQueue) {
       
        if(!card.isSelected()) 
           actionOnSelection(card);
        else 
           actionOnDeselection(card);
    }
    
    private void actionOnSelection( Card card)
    {
        if(cardHandler.canBePlayed(card)){
           card.setSelected(true);
           cardHandler.actionOnSelection(card);
        }
    }
    
   private void actionOnDeselection(Card card){
           card.setSelected(false);
           cardHandler.actionOnDeselection(card);
    } 
    
    @Override
    public String toString() {
        return "MULTIPLE_SELECTION";
    }
   }

   

