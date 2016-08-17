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
    CardSet hand;
    ArrayList<Unit> army;
    
    boolean active;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CardSet getHand() {
        return hand;
    }

    public void setHand(CardSet hand) {
        this.hand = hand;
    }

    public ArrayList<Unit> getArmy() {
        return army;
    }

    public void setArmy(ArrayList<Unit> army) {
        this.army = army;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    
    
    
    
    
    
    
}
