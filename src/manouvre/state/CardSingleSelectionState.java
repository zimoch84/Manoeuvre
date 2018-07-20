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
import manouvre.events.EventType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author xeon
 */
public class CardSingleSelectionState implements CardInputState, Serializable{

    private static final Logger LOGGER = LogManager.getLogger(CardSingleSelectionState.class.getName());
    
    CardPlayingHandler cardHandler;

    MapStateHandler mapStateHandler;

    public CardSingleSelectionState(CardPlayingHandler cardHandler, MapStateHandler mapStateHandler) {
        this.cardHandler = cardHandler;
        this.mapStateHandler = mapStateHandler;
    }
    
    @Override
    public void handleInput(Card card, Game game, CommandQueue cmdQueue) {
        if(!card.isSelected()) 
            actionOnSelection( card, game);
        else 
            actionOnDeselection(card,game);
    }
    private void actionOnDeselection(Card card, Game game)
    {
            if(cardHandler.canBePlayed(card))
            {
            card.setSelected(false); 
            cardHandler.actionOnDeselection(card);
            game.notifyAbout(EventType.CARD_DESELECTED);
            }
    }
    
    private void actionOnSelection(Card card, Game game){
            if(cardHandler.canBePlayed(card)){
                card.setSelected(true);
                cardHandler.setPlayingCard(card);
                cardHandler.actionOnSelection(card);
                keepOneSelectedCard(card, game);
                game.notifyAbout(EventType.CARD_SELECTED);
    }
    
    }

    private void keepOneSelectedCard(Card card, Game game){
       
        for(Card searchCard : game.getCurrentPlayer().getHand().getCardList() )
        {
            if(!searchCard.equals(card))
            {
                if(searchCard.isSelected())
                {
                    searchCard.setSelected(false);
                    actionOnDeselection(searchCard, game);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "SINGLE_SELECTION";
    }
   
    
    
}
