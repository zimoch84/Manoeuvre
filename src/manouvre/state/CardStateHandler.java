/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.io.Serializable;
import java.util.ArrayList;
import manouvre.game.Card;
import manouvre.game.Game;
import manouvre.commands.CommandQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author xeon
 */
public class CardStateHandler implements Serializable{
    
    public final static int NOSELECTION = 0;
    public final static int PICK_ONLY_ONE = 1;
    public final static int MULTIPLE_PICK = 2;
    
    Logger LOGGER;
    Game game;
    CommandQueue cmdQueue;
    
    ArrayList<CardInputState> arrayOfStates;
    private CardInputState currentState, previosState;

    public CardStateHandler(Game game, CommandQueue cmdQueue) {
        this.game = game;
        this.cmdQueue = cmdQueue;
        currentState = new CardsNoSelectionState();
        //arrayofStates.add(currentState);O
        LOGGER = LogManager.getLogger(CardStateHandler.class.getName());
    }
    
    public void setState(int nextState)
    {
        previosState = currentState;
        
        switch(nextState)
        {
            case NOSELECTION : currentState = new CardsNoSelectionState();
            break;
            
            case PICK_ONLY_ONE : currentState = new CardSingleSelectionState();
            break;
            
            case MULTIPLE_PICK : currentState = new CardMultipleSelectionState();
            break;
            
            default: currentState = new CardsNoSelectionState();
        
        }
        LOGGER.debug(game.getCurrentPlayer().getName() + " setState "  + currentState );
 
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
                
                case Game.SETUP : setState(CardStateHandler.NOSELECTION);
                break;
                
                case Game.DISCARD : setState(CardStateHandler.MULTIPLE_PICK);
                break;
                
                case Game.DRAW : setState(CardStateHandler.PICK_ONLY_ONE);
                break;
                
                case Game.MOVE : setState(CardStateHandler.PICK_ONLY_ONE);
                break;
                
                case Game.COMBAT : setState(CardStateHandler.PICK_ONLY_ONE);
                break;
                
                case Game.RESTORATION : setState(CardStateHandler.PICK_ONLY_ONE);
                break;
   
            }
          LOGGER.debug(game.getCurrentPlayer().getName() + " setInitStateForPhase " + currentState );
    }
    
    public void handle(Card card, Game game)
    {
        currentState.handleInput(card, game, cmdQueue);
        
    }
    
}
