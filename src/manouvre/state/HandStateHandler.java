/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import manouvre.game.Card;
import manouvre.game.Game;
import manouvre.commands.CommandQueue;
import manouvre.events.EventType;
import manouvre.game.CardCommandFactory;
import manouvre.game.CardPlayingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Piotr
 */
public class HandStateHandler implements Serializable, Observer{
    
    public final static int NOSELECTION = 0;
    public final static int PICK_ONLY_ONE = 1;
    public final static int MULTIPLE_PICK = 2;
    
    Logger LOGGER;
    Game game;
    CommandQueue cmdQueue;
    CardCommandFactory ccf;
    
    ArrayList<CardInputState> arrayOfStates;
    private CardInputState currentState, previosState;
    private CardPlayingHandler cardPlayingHandler;
    private MapStateHandler mapStateHandler;
    
    
    public HandStateHandler(Game game, CardPlayingHandler cardPlayingHandler, MapStateHandler mapStateHandler) {
    
        this.cardPlayingHandler = cardPlayingHandler;
        this.game = game;
        currentState = new CardsNoSelectionState();
        
        LOGGER = LogManager.getLogger(HandStateHandler.class.getName());
    }
    
    public void setState(int nextState)
    {
        previosState = currentState;
        
        switch(nextState)
        {
            case NOSELECTION : currentState = new CardsNoSelectionState();
            break;
            
            case PICK_ONLY_ONE : currentState = new CardSingleSelectionState(cardPlayingHandler, mapStateHandler);
            break;
            
            case MULTIPLE_PICK : currentState = new CardMultipleSelectionState(cardPlayingHandler);
            break;
            
            default: currentState = new CardsNoSelectionState();
        
        }
        LOGGER.debug(game.getCurrentPlayer().getName() + "previousState: " +  previosState +  " setState "  + currentState );
 
    }
   
    public void setPreviosState(int nextState)
    {
        CardInputState copyCurrentState = currentState;
        
        if(previosState != null)
        { 
            currentState = previosState;
            previosState = copyCurrentState;
        }
     
    }
   
    public void setInitStateForPhase (Game game){
    
       
            switch (game.getPhase())
            {
                
                case Game.SETUP : setState(HandStateHandler.NOSELECTION);
                break;
                
                case Game.DISCARD : setState(HandStateHandler.MULTIPLE_PICK);
                break;
                
                case Game.DRAW : setState(HandStateHandler.PICK_ONLY_ONE);
                break;
                
                case Game.MOVE : setState(HandStateHandler.PICK_ONLY_ONE);
                break;
                
                case Game.COMBAT : setState(HandStateHandler.PICK_ONLY_ONE);
                break;
                
                case Game.RESTORATION : setState(HandStateHandler.PICK_ONLY_ONE);
                break;
   
            }
          LOGGER.debug(game.getCurrentPlayer().getName() + "previousState: " +  previosState +  " setState "  + currentState  );
    }
    
    public void handle(Card card, Game game)
    {
        if(game.getCurrentPlayer().isActive())
                    currentState.handleInput(card, game, cmdQueue);
        
    }

    @Override
    public void update(Observable o, Object arg) {
        String dialogType = (String) arg;
        switch(dialogType){
        
        case EventType.NEXT_PHASE:
        case EventType.END_TURN:
            if(game.getCurrentPlayer().isActive())
                setInitStateForPhase(game);
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
            
        case EventType.CANCELLABLE_CARD_PLAYED: {
                /*
                Guirellas decision
                */
                if(game.getCurrentPlayer().isActive())
                    setState(HandStateHandler.PICK_ONLY_ONE);
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
                break;
         }
        case EventType.COMBAT_DEFENDER_WITHDRAW:
        {
            setState(HandStateHandler.NOSELECTION);
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;
        }
        
        case EventType.ASSAULT_BEGINS:
            if(game.getCurrentPlayer().isActive())
                setState(HandStateHandler.MULTIPLE_PICK);
            else 
                setState(HandStateHandler.NOSELECTION);
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;  
        
        case EventType.BOMBARD_BEGINS:
            setState(HandStateHandler.NOSELECTION);
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;
        
        case EventType.COMBAT_THROW_DICE:
            setState(HandStateHandler.NOSELECTION);
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;
        
        case EventType.DEFENDING_CARDS_PLAYED:
            if(game.getCurrentPlayer().isActive())
                setState(HandStateHandler.MULTIPLE_PICK);
            else 
                setState(HandStateHandler.NOSELECTION);
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;  
            
        case EventType.CARDS_DISCARDED: 
            if(game.getCurrentPlayer().isActive())
                 game.getCurrentPlayer().getHand().unselectAllCards();
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
        case EventType.LEADER_DESELECTED:
            if(game.getCurrentPlayer().isActive())
                    game.getCurrentPlayer().getHand().unselectAllCards();
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;    
        
        

    }
    }


    
    
    
}
