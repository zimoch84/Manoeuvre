/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

/**
 *
 * @author Bartosz
 */
public class Test {
    
    public static void main(String[] args) { 
        
        Card Card = new Card(61);
        int name=Card.getUnitDeffense();
        
        System.out.println("ID"+61+"Deffense:" + name);
        
        Card Card1 = new Card(245);
       
        System.out.println("ID"+245+"UnitBombard:" + Card1.getUnitBombard());
        
        CardSet Bartek= new CardSet(6, 0);
        Bartek.getAllCards();
        
        
    }

    
}
