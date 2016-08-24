/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.util.Random;

/**
 *
 * @author Bartosz
 */
public class Test {
   
    public static void main(String[] args) { 
        System.out.println("1");
        Random randomGenerator = new Random();
        System.out.println("2");
        CardSet BarteksDeck = new CardSet(80,0);
        System.out.println("3");
        CardSet BarteksHand = new CardSet(6);
        System.out.println("4");
        CardSet BarteksUsedCardsDeck = new CardSet();
        System.out.println("5");
        
        
        
        System.out.println("DECK:");
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
        BarteksUsedCardsDeck.getAllCardsIDFromSet();
        
         } 
        

          
          
          
    }

    

