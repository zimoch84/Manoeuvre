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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author xeon
 */
public class CardSingleSelectionState implements CardInputState, Serializable{

    private static final Logger LOGGER = LogManager.getLogger(CardSingleSelectionState.class.getName());
    
    
    @Override
    public void handleInput(Card card, Game game) {
        
        if(!card.isSelected()) 
        {
            LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapInputStateHandler.CARD_PLAYING_STATE") ;
            game.mapInputHandler.setState(MapInputStateHandler.CARD_PLAYING_STATE);
            
            if(card.canBePlayed(game)){
                card.setSelected(true);
                game.getCardCommandFactory().setPlayingCard(card);
                triggerCardActionOnSelection(card, game);
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
    
     private void triggerCardActionOnSelection(Card playingCard, Game game){
    
         if(playingCard.canBePlayed(game))
            {
            /*
            Trigger action on selection
            */
            playingCard.actionOnSelection(game);
            
            /*
            If card have only 1 attacking mode set it here to avoid custom dialog
            If card have 2 attacking mode then later we'll ask user about which mode he choses
            */
                if(playingCard.getCardType() == Card.UNIT)
                {
                    if(playingCard.getPlayingPossibleCardModes().size() == 1 )
                        playingCard.setPlayingCardMode(playingCard.getPlayingPossibleCardModes().get(0));
                }
            
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
        CardSet hand = game.getCurrentPlayer().getHand();
        
        for (int i=0; i<game.getCurrentPlayer().getHand().size(); i++){ 
            hand.getCardByPosInSet(i).setSelected(false);
        }
        game.getCurrentPlayer().getHand().selectionSeq.clear();
        if(hand.getCard(card)!=null){
        hand.getCard(card).setSelected(true);
        game.getCurrentPlayer().getHand().selectionSeq.add(card);
        }
        else System.err.println("CARD IS NOT SELECTED - check GameGui.java method: keepOneSelectedCard");
    }

    @Override
    public String toString() {
        return "SINGLE_SELECTION";
    }
   
    
    
}
