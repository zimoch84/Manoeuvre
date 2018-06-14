/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;


import java.io.Serializable;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.commands.CommandQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author xeon
 */
public class MapInputStateHandler implements Serializable{
    
    Logger LOGGER;
    Game game;
    
    public final static String NOSELECTION = "NOSELECTION";
    public final static String PICK_ONE_UNIT = "PICK_ONE_UNIT";
    public final static String PICK_MOVE_POSITION = "PICK_MOVE_POSITION";
    public final static String PICK_MULTIPLE_UNITS = "PICK_MULTIPLE_UNITS";
    public final static String PICK_UNIT_BY_CARD = "PICK_UNIT_BY_CARD";
    public final static String PICK_MOVE_POSITION_BY_CARD = "PICK_MOVE_POSITION_BY_CARD";
    public final static String CARD_PLAYING_STATE = "CARD_PLAYING_STATE";
       
    public String unitSelectionMode = PICK_ONE_UNIT;
    
    public MapState currentState, previousState;

    public MapInputStateHandler(Game game) {
        this.game = game;
        LOGGER = LogManager.getLogger(MapInputStateHandler.class.getName());
        currentState = new MapPickAvalibleUnitState();
    }
    
    public void setState(String nextState)
    {
        LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu z:" + currentState + " na: "   + nextState );
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
            
            case PICK_MULTIPLE_UNITS : currentState = new MapMultiPickUnitState();
            break;
            
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
