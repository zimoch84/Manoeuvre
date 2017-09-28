/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;


import java.io.Serializable;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.commands.CommandQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author xeon
 */
public class MapInputStateHandler implements Serializable{
    
    Logger LOGGER;
    Game game;
    
    public final static int NOSELECTION = 0;
    public final static int PICK_ONE_UNIT = 1;
    public final static int PICK_MOVE_POSITION = 2;
    public final static int PICK_MULTIPLE_UNITS = 3;
    public final static int PICK_UNIT_BY_CARD = 4;
    public final static int PICK_MOVE_POSITION_BY_CARD = 5;
    public final static int CARD_PLAYING_STATE = 6;
       
    /*
    Takes two valuse PICK_ONE_UNIT or PICK_MULTIPLE_UNITS
    default takes 1 unit.
    */
    public int unitSelectionMode = PICK_ONE_UNIT;
    
    
    
    public MapState currentState, previousState;

    public MapInputStateHandler(Game game) {
        
        this.game = game;
        LOGGER = LogManager.getLogger(MapInputStateHandler.class.getName());
        
       // this.arrayOfStates = new ArrayList();
        currentState = new MapPickAvalibleUnitState();
        //arrayofStates.add(currentState);O
    
    }
    
    public void setState(int nextState)
    {
      
        LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na " + getStateName(nextState) );
        previousState = currentState;
        
        switch(nextState)
        {
            case NOSELECTION : currentState = new MapPickNoneState();
            break;
            
            case PICK_ONE_UNIT : currentState = new MapPickAvalibleUnitState();
            break;
            
            case PICK_MOVE_POSITION : currentState = new MapPickUnitMovePositionState();
            break;
                       
            
            case CARD_PLAYING_STATE : currentState = new MapCardPlayingState();
            break;  
            
            case PICK_UNIT_BY_CARD : currentState = new MapCardPickUnitState();
            break;
            
            case PICK_MOVE_POSITION_BY_CARD : currentState = new MapCardMoveUnitState();
            break;
            
           // case MULTIPLE_PICK : currentState = new CardMultipleSelectionState();
           // break;
            
            default: currentState = new MapPickAvalibleUnitState();
            break;
        
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
     
    private String getStateName(int stateId){
    
    switch(stateId)
        {
            case NOSELECTION : return "NOSELECTION";
           
            
            case PICK_ONE_UNIT : return "PICK_ONE_UNIT";
            
            
            case PICK_MOVE_POSITION : return "PICK_MOVE_POSITION";
            
                       
            
            case CARD_PLAYING_STATE : return "CARD_PLAYING_STATE";
            
            
            case PICK_UNIT_BY_CARD : return "PICK_UNIT_BY_CARD";
            
            
            case PICK_MOVE_POSITION_BY_CARD : return "PICK_MOVE_POSITION_BY_CARD";
           
            
            default:  return "DEFAULT";
           
    }
    }
    
    public void handle(Position pos, Game game, CommandQueue cmdQueue)
    {
         currentState.handleInput(pos, game, cmdQueue, this);
    }
    
    
    public void setInitStateForPhase (Game game){
    
        LOGGER.debug(game.getCurrentPlayer().getName() + " setInitStateForPhase "  );
            switch (game.getPhase())
            {
                
                case Game.SETUP : setState(MapInputStateHandler.NOSELECTION);
                break;
                
                case Game.DISCARD : setState(MapInputStateHandler.NOSELECTION);
                break;
                
                case Game.MOVE : setState(MapInputStateHandler.PICK_ONE_UNIT);
                break;
                
                case Game.COMBAT : setState(MapInputStateHandler.NOSELECTION);
                break;
                
                case Game.RESTORATION : setState(MapInputStateHandler.NOSELECTION);
                break;
                
                                
                
                
            }
    
    }
    
}
