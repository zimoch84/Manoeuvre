/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.io.Serializable;
import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.commands.CommandQueue;

/**
 *
 * @author xeon
 */
public class MapPickAvalibleUnitState implements MapState, Serializable{
    
    

    @Override
    public void handleInput(Position pos, Game game, CommandQueue cmdQueue, MapInputStateHandler handler) {
        ArrayList<Position> avalaiblePositions =  game.getCurrentPlayerAvalibleUnitToSelect();
                    
        if(avalaiblePositions.contains(pos))
            
        {
            game.getCurrentPlayer().getUnitByPosition(pos).setSelected(true);
            game.getMap().setUnitSelected(true);
          
        
            if(game.getCardCommandFactory().getPlayingCard() == null) 
        
            {
                if(game.getPhase() == Game.SETUP || game.getPhase() == Game.MOVE )
                    handler.setState(MapInputStateHandler.PICK_MOVE_POSITION);
            }
            
            else  
                
                handler.setState(MapInputStateHandler.PICK_UNIT_BY_CARD);
            
        }   
                 
            
            
        else
            game.unselectAllUnits();
            
            
   }
   
}
