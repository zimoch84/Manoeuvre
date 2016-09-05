/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.util.ArrayList;
import manouvre.gui.UnitGUI;

/**
 *  Nation
 *  int BR  = 0; //GreatBritain
    int FR  = 2; //France
    int RU  = 5; //Russland
    int PR  = 4; //Prussia
    int AU  = 1; //Austria
    int SP  = 6; //Spain
    int OT  = 3; //Ottoman
    int US  = 7; //USA
 * @author Piotr
 */
public class Player {
    
    String name;
    int nation; //for nation description see CardInterface

   
    CardSet hand;
    CardSet drawPile;
    CardSet discardPile;
    
    ArrayList<Unit> army;
    
    boolean active;

    public Player(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
 
    public void setNation(int nation) {
      //  System.out.println("Nation Set to:"+nation);
        this.nation = nation;
    }
    
    public int getNation() {
        return nation;
    }
    
    public void setCards() {
        this.drawPile = new CardSet(60,nation); 
        this.hand = new CardSet(5);     
             
        this.discardPile = new CardSet();

       
        hand.addRandomCardsFromOtherSet(5, drawPile);
        hand.sortCard();  
    }

    public void generateUnits(){
     
        army = new ArrayList<Unit>();
      for (int i=getNation()*8  ;i<getNation()*8+8;i++)
        {

            
            Unit unit =  new Unit(i+1);
          /*
            Pozycja tymczasowo - bedzie tworzona w setupie
            */
            unit.setPos(new Position (  i- ( getNation()*8)    ,1));
            army.add(   unit     ) ;      
                   
              
       
        }
    // System.out.println("Units Generated:");
    }
    
    
    public CardSet getDrawPile() {
        return drawPile;
    }
    
    public CardSet getHand() {
        return hand;
    }

     
    public CardSet getDiscardPile() {
        return discardPile;
    }


    public void setArmy(ArrayList<Unit> army) {
        this.army = army;
    }
    
    public ArrayList<Unit> getArmy() {
        return army;
    }


    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }


  
    
}
