/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.io.Serializable;
import manouvre.game.Card;
import manouvre.game.CardSet;
import manouvre.game.Game;
import manouvre.commands.CommandQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author xeon
 */
public class CardSingleSelectionState implements CardInputState, Serializable{

    private static final Logger LOGGER = LogManager.getLogger(CardSingleSelectionState.class.getName());
    
    
    @Override
    public void handleInput(Card card, Game game, CommandQueue cmdQueue) {
        
        if(!card.isSelected()) 
        {
            LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapInputStateHandler.CARD_PLAYING_STATE") ;
            game.mapInputHandler.setState(MapInputStateHandler.CARD_PLAYING_STATE);
            
            if(card.canBePlayed(game)){
                card.setSelected(true);
                game.getCardCommandFactory().setPlayingCard(card);
                triggerCardActionOnSelection(card, game, cmdQueue);
                keepOneSelectedCard(card, game);
            }
        }

        else 
        {   
            if(card.canBePlayed(game)){
            card.setSelected(false); 
            triggerCardActionOnDeSelection(card, game);
            }
            
        }    
            
        
    }
    
     private void triggerCardActionOnSelection(Card playingCard, Game game, CommandQueue cmdQueue){
    
         if(playingCard.canBePlayed(game))
            {
                /*
            If card have only 1 attacking mode set it here to avoid custom dialog
            If card have 2 attacking mode then later we'll ask user about which mode he choses
            */
                if(playingCard.getCardType() == Card.UNIT)
                {
                    if(playingCard.getPlayingPossibleCardModes().size() == 1 )
                        playingCard.setPlayingCardMode(playingCard.getPlayingPossibleCardModes().get(0));
                }

            /*
            Trigger action on selection
            */
            playingCard.actionOnSelection(game, cmdQueue);
            }
            
    
    }
    
    private void triggerCardActionOnDeSelection(Card playingCard, Game game){
        if(playingCard.canBePlayed(game)) { 
            /*
            Trigger action on selection
            */
            playingCard.actionOnDeselection(game);
            LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
            game.getCardCommandFactory().resetFactory();
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
                    searchCard.actionOnDeselection(game);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "SINGLE_SELECTION";
    }
   
    
    
}
