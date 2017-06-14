/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.awt.Graphics;
import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Position;

/**
 *
 * @author xeon
 */
public class PickAvalibleUnitState implements MapState{

    @Override
    public void handleInput(Position pos, Game game) {
        ArrayList<Position> avalaiblePositions =  game.getCurrentPlayerAvalibleUnitToSelect();
                    
        if(avalaiblePositions.contains(pos))
            
            game.getCurrentPlayer().getUnitByPosition(pos).setSelected(true);
            
            /*
            TODO stateHandler - move to next State - PickAvaliblePositionState to 
            move or attack or heal ect.
        */
            
        else
            game.unselectAllUnits();
            

    }

    @Override
    public void draw(Graphics g, Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
