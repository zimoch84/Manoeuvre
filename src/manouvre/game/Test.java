/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.gui.GameWindow;

/**
 *
 * @author Bartosz
 */
public class Test {
   
    public static void main(String[] args) { 
       // GameWindow.main(null);
        System.out.println("I am in Test.java!");
        Random randomGenerator = new Random();
       
        CardSet BarteksDeck = new CardSet(80,0);
       
        CardSet BarteksHand = new CardSet(6);
        
        CardSet BarteksUsedCardsDeck = new CardSet();
       
        BarteksHand.addRandomCardsFromOtherSet(6, BarteksDeck);
        
        
      /*  System.out.println("DECK:");
        BarteksDeck.getAllCardsIDFromSet();
        System.out.println("HAND:");
        BarteksHand.getAllCardsIDFromSet();
        System.out.println("USED:");
        BarteksUsedCardsDeck.getAllCardsIDFromSet();
        
        System.out.println("add 6 RandomCardsFromOtherSet :");
        BarteksHand.addRandomCardsFromOtherSet(6, BarteksDeck);
         
        System.out.println("DECK:");
        BarteksDeck.getAllCardsIDFromSet();
        System.out.println("HAND:");
        BarteksHand.getAllCardsIDFromSet();
        System.out.println("USED:");
        BarteksUsedCardsDeck.getAllCardsIDFromSet();
        
         System.out.println("Deal 3rd card:");
        BarteksHand.dealCardByPosInSetToOtherSet(3, BarteksUsedCardsDeck);
         
        System.out.println("DECK:");
        BarteksDeck.getAllCardsIDFromSet();
        System.out.println("HAND:");
        BarteksHand.getAllCardsIDFromSet();
        System.out.println("USED:");
        BarteksUsedCardsDeck.getAllCardsIDFromSet();*/
        
         } 
        

          
          
          
    }

    

