/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.util.ArrayList;

/**
 *
 * @author xeon
 */
public class CardStateHandler {
    
    public final static int NOSELECTION = 0;
    public final static int PICK_ONLY_ONE = 1;
    public final static int MULTIPLE_PICK = 2;
    
    ArrayList<CardInputState> arrayOfStates;
    public CardInputState currentState, previosState;

    public CardStateHandler() {
       // this.arrayOfStates = new ArrayList();
        currentState = new CardsNoSelectionState();
        //arrayofStates.add(currentState);O
    
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
     
    
}
