/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.commands.CommandQueue;

/**
 *
 * @author xeon
 */
public class MapInputStateHandler {
    
    public final static int NOSELECTION = 0;
    public final static int PICK_ONE_UNIT = 1;
    public final static int PICK_MOVE_POSITION = 2;
    public final static int PICK_MULTIPLE_UNITS = 3;
    public final static int PICK_UNIT_BY_CARD = 4;
    public final static int PICK_MOVE_POSITION_BY_CARD = 5;
    public final static int CARD_PLAYING_STATE = 6;
       
    
    
    public MapState currentState, previousState;

    public MapInputStateHandler() {
       // this.arrayOfStates = new ArrayList();
        currentState = new MapPickAvalibleUnitState();
        //arrayofStates.add(currentState);O
    
    }
    
    public void setState(int nextState)
    {
        previousState = currentState;
        
        switch(nextState)
        {
            case NOSELECTION : currentState = new MapPickNoneState();
            break;
            
            case PICK_ONE_UNIT : currentState = new MapPickAvalibleUnitState();
            break;
            
            case CARD_PLAYING_STATE : currentState = new MapCardPlayingState();
            break;
            
            case PICK_UNIT_BY_CARD : currentState = new MapCardUnitPickState();
            break;
            
            case PICK_MOVE_POSITION_BY_CARD : currentState = new MapCardUnitMoveState();
            break;
            
           // case MULTIPLE_PICK : currentState = new CardMultipleSelectionState();
           // break;
            
            default: currentState = new MapPickNoneState();
        
        }
 
    }
   
    public void setPreviousState()
    {
        MapState copyCurrentState = currentState;
        
        if(previousState != null)
        { 
            currentState = previousState;
            previousState = copyCurrentState;
        }
     
    }
     
    public void handle(Position pos, Game game, CommandQueue cmdQueue)
    {
            currentState.handleInput(pos, game, cmdQueue, this);
    }
    
    
}
