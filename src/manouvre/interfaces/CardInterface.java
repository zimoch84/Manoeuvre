/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.interfaces;

import manouvre.game.Dice;

/**
 *
 * @author Piotr
 */
public interface CardInterface {

    /*
    Card Type
    */
    public static int UNIT=0;
    public static int HQCARD=1;
    public static int LEADER=2;
    public static int NO_CARD=99;
    
    /*
    Unit Types
    */
    public static int INFANTRY  = 0;
    public static int CALVARY  = 1;
    public static int ARTYLERRY  = 2;
    
    /*
    HQ card types
    */
   // public static int COMMANDER = 3; NOT IN USE
    public static int WITHDRAW = 4;
    public static int SUPPLY = 5;
    public static int GUERRILLAS = 6;
    public static int COMMITED_ATTACK = 7;
    public static int FORCED_MARCH = 8;
    public static int SKIRMISH = 9;
    public static int REDOUBDT = 10;
    public static int SCOUT = 11;
    public static int ROYAL_ENG = 12;
    public static int SPY = 13;
    public static int AMBUSH = 14;
    public static int REGROUP = 15;
    public static int FRENCH_SAPPERS = 16;

    /*
    Card attack types
    */
    public static String ASSAULT = "ASSAULT";
    public static String VOLLEY = "VOLLEY";
    public static String BOMBARD = "BOMBARD";
    public static String PURSUIT = "PURSUIT";
    public static String LEADER_SUPPORT = "LEADER_SUPPORT";
    
    public static String NO_TYPE = "NO_TYPE";
           
    public static int MOVE_UNIT_ACTION = 500;
    public static int PICK_UNIT_ACTION = 501;
    public static int MULTIPLE_UNIT_PICK_ACTION = 502;
    public static int NO_ACTION = 503;
    
    
    /*
    Return CardID 
    */  
    public int getCardID();
    /*
    Return card name string
    */
    public String getCardName(); 
    /*
    Returns card image String
    */
    public String getCardImgName();    
    /*
    Return type of card
    */
    public int getType();
    /*
    Returns unit attack value
    */
    public Dice.Set getUnitAttack();
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
    public Dice.Set getUnitBombard();
    /*
    Returns unit volley value
    */
    public Dice.Set getUnitVolley();
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
    public int getLederCommand();
    /*
    Returns leder combat value
    */
    public int getLeaderCombat();
    /*
    Returns leder rally value
    */
    public int getLeaderRally();
    /*
    Returns leder pursuit value
    */
    public int getLeaderPursuit();
    /*
    Returns leder grand battery value
    */
    public String getLeaderGrandBattery();
    /*
    Returns unit description string
    */
    public String getDescription();
    
    
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
    public boolean isNotRequredToAdvanceAfterAttack();   

}
