/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.interfaces;

/**
 *
 * @author Piotr
 */
public interface UnitInterface {
    
    /*
    Unit Types
    */
    public static int INFANTRY  = 0;
    public static int CALVARY  = 1;
    public static int ARTYLERRY  = 2;
    
    /*
    Return type of card
    */
    public int getType();
    
    public PositionInterface getPosition();
    
    public boolean isEliminated();
        
    public boolean isInjured();
    
    
}
