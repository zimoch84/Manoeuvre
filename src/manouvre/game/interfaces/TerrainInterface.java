/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.interfaces;

/**
 *
 * @author Piotr
 */
public interface TerrainInterface {
    
    /*
    Tile Types
    */
    public static int CLEAR  = 0;
    public static int HILL  = 1;
    public static int FOREST  = 2;
    public static int MARSH  = 3;
    public static int FIELDS  = 4;
    public static int LAKE  = 5;
    
    
    /*
    Return type of tile
    */
    public int getType();
    
    /*
    Return defenceBonus
    */
    
    public int getDefenceBonus();
    
    /*
    Returns if its blocking Line of sight
    */
    public boolean isBlockingLOS();
    
    /*
    Returns if is passable
    */
            
    public boolean isPassable();
    
    
}
