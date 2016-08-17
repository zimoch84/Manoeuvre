/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.util.ArrayList;

/**
 *
 * @author Piotr
 */
public class Game {
    
    Map map;

   
    Player playerOne;
    Player playerTwo;
    
    int phase; 
    
    
    public ArrayList<Position> getPossibleBombard(Unit unit){
    return null;
    };
    
    public ArrayList<Position> getPossibleVolley(Unit unit){
        return null;
        
    };
    
    public ArrayList<Position> getPossibleMovement(Unit unit){
    return null;
    };
    
    public ArrayList<Position> getPossibleSupportingUnits(Unit unit){
    return null;
    };
    
    public ArrayList<Position> getRetreatPositions(Unit unit){
    return null;
    };
    
    
    public void generateMap(){
        this.map = new Map();
    }
     public Map getMap() {
        return map;
        
    }
    
    
}
