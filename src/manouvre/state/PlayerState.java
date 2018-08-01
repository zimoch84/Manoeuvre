/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import manouvre.game.CardPlayingHandler;
import manouvre.commands.CommandQueue;
import manouvre.game.CardCommandFactory;
import manouvre.game.Game;

/**
 *
 * @author piotr_grudzien
 */
public class PlayerState{
    
    public  CardCommandFactory cardCommandFactory;
    public  HandStateHandler cardStateHandler;
    public  MapStateHandler mapStateHandler;
    public  CardPlayingHandler cardPlayingHandler;
    
    public PlayerState(Game game) {
        cardCommandFactory = new CardCommandFactory(game);
        mapStateHandler = new MapStateHandler(game, cardCommandFactory);
        cardPlayingHandler = new CardPlayingHandler(game, mapStateHandler);
        cardStateHandler = new HandStateHandler(game, cardPlayingHandler, mapStateHandler);
        
        game.addObserver(mapStateHandler);
        game.addObserver(cardStateHandler);
        game.addObserver(cardPlayingHandler);
        
    }
   
}
