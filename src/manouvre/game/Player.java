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
        hand.getAllCardsIDFromSet();
        // hand.getAllCardsIDFromSet();
    // System.out.println("cards left in hand"+hand.cardsLeftInSet());
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
