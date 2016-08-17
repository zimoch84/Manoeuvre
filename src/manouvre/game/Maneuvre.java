/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import manouvre.game.interfaces.PositionInterface;

/**
 *
 * @author Piotr
 */
public class Maneuvre {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Unit unit = new Unit(59);
        System.out.println(unit.toString());
       
        unit.setPos(new Position(1, 3));
        
       for(PositionInterface pos :  unit.getPosition().getAdjencedPositions()   ) 
           
       {
           System.out.println(pos.toString());
       }
        
       
                
    }
    
}
