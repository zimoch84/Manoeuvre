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
public interface CardInterface {
    
    /*
    colors
    */
    
    public static int RED  = 0;
    public static int BLUE  = 1;
    public static int YELLOW  = 2;
    public static int GREY  = 3;
    public static int GREEN  = 4;
    public static int LIGHTBLUE  = 5;
    public static int LIGHTGREEN  = 6;
    
    /*
    Unit Types
    */
    public static int INFANTRY  = 0;
    public static int CALVARY  = 1;
    public static int ARTYLERRY  = 2;
    
    /*
    HQ card types
    */
    public static int COMMANDER = 3;
    public static int WITHDRAW = 4;
    public static int SUPPLY = 5;
    public static int GUERRILLAS = 6;
    public static int COMMITED_ATTACK = 7;
    public static int FORCED_MARCH = 8;
    public static int SKIRMICH = 9;
    public static int REDOUBDT = 10;
    public static int SCOUT = 11;
    
    
    public static int HQCARD  = 4;
    
    /*
    Return type of card
    */
    public int getType();
    
    /*
    Return type of card
    */
    public int getHQType();
    
    
    /*
    Returns color of card
    */
    
    public int getColor();
    
    /*
    Returns defence value
    */
    public int getDefence();
    
    /*
    Checks if cards is HQ Card
    */
    public boolean isHQCard();
    
    
    
    
    
}
