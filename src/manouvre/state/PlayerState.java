/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import manouvre.game.CardPlayingHandler;
import java.util.Observable;
import java.util.Observer;
import manouvre.commands.CommandQueue;
import manouvre.events.EventType;
import manouvre.game.CardCommandFactory;
import manouvre.game.Game;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author piotr_grudzien
 */
public class PlayerState implements Observer{
    
    private Game game;
    private CommandQueue cmdQueue;
    public  CardCommandFactory cardCommandFactory;
    
    public  HandStateHandler cardStateHandler;
    public  MapStateHandler mapStateHandler;
    public  CardPlayingHandler cardPlayingHandler;
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(PlayerState.class.getName());  
    

    public PlayerState(Game game, CommandQueue cmdQueue) {
        this.game = game;
        this.cmdQueue = cmdQueue;
        cardCommandFactory = new CardCommandFactory(game);
        
        
        mapStateHandler = new MapStateHandler(game, this);
        cardPlayingHandler = new CardPlayingHandler(game, mapStateHandler);
        cardStateHandler = new HandStateHandler(game, cardPlayingHandler, mapStateHandler);
        
    }

    @Override
    public void update(Observable o, Object arg) {
        
        System.out.println("Object attached to event()" +  o.getClass().toString() );
        
        String dialogType = (String) arg;
        
        LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        
        switch(dialogType){
            
            case EventType.SETUP_FINISHED:
            {
                if(game.getCurrentPlayer().isFinishedSetup()){
                mapStateHandler.setState(MapStateHandler.NOSELECTION);
                }
                break;
            }
            
            case EventType.NEXT_PHASE: case EventType.END_TURN:{
                
                cmdQueue.storeAndExecuteAndSend(cardCommandFactory.createCleanTableCommand());
                break;
            }
        
            case EventType.CARDS_DISCARDED: {
                
                if(game.getCurrentPlayer().isActive())
                     game.getCurrentPlayer().getHand().unselectAllCards();
                break;
            }

            default: 
            {
               LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: Brak obsługi");
            }
            }
        
        
    
        
    }
    
    
    
    
    
    
    
   
    
    
    
    
}
