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
    flags
    */
    
    public static int BR  = 0; //GreatBritain
    public static int FR  = 1; //France
    public static int RU  = 2; //Russland
    public static int PR  = 3; //Prussia
    public static int AU  = 4; //Austria
    public static int SP  = 5; //Spain
    public static int OT  = 6; //Ottoman
    public static int US  = 7; // USA
    
    /*
    Card Type
    */
    public static int UNIT=0;
    public static int HQUNIT=0;
    public static int HQLEADER=0;
    
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
    
    
    public static int HQCARD  = 6;
    
    /*
    Return CardID 
    */  
    public int getCardID();
    /*
    Return card name string
    */
    public String getCardName(); 
    /*
    Returns flag of card
    */
    public int getCardFlag(); 
    /*
    Returns card image String
    */
    public String getCardImg();    
    /*
    Return type of card
    */
    public int getCardType();
    /*
    Returns unit attack value
    */
    public int getUnitAttack();
    /*
    Returns unit defence value
    */
    public int getUnitDefence();  
    /*
    Returns unit range value
    */
    public int getUnitRange();   
    /*
    Returns unit bombard value
    */
    public int getUnitBombard();
    /*
    Returns unit volley value
    */
    public int getUnitVolley();
    /*
    Returns unit pursuit value
    */
    public int getUnitPursuit();
    /*
    Returns unit withdraw value
    */
    public int getUnitWithdraw();
    /*
    Returns leder command string
    */
    public String getLederCommand();
    /*
    Returns leder combat value
    */
    public int getLederCombat();
    /*
    Returns leder rally value
    */
    public int getLederRally();
    /*
    Returns leder pursuit value
    */
    public int getLederPursuit();
    /*
    Returns leder grand battery value
    */
    public String getLederGrandBatt();
    /*
    Returns unit description string
    */
    public String getUnitDescr();
    
    
    //--------------------------------------
    /*
    Checks if cards is HQ Card
    */
    public boolean isHQCard();
     /*
    Return type of card
    */
    public int getHQType();   
    /*
    Is required to advance
    */
    public boolean isRequredToAdvanceAfterAttack();   
    /*
    checks if Leader Rally successfully resolve
    true - test passed
    
    */
    public boolean checkRally(int diceThrow);
    
    /*  
    is card cancelled by GUERRILLAS
    */
    
    public boolean isCancelled();
    
    /**  
    set card is cancelled by GUERRILLAS
    */
    public void setCancelled();
    
    
    
    
}
