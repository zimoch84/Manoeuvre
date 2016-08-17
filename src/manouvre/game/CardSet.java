/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;
import manouvre.game.interfaces.DeckInterface;

import java.util.Random;
import java.util.ArrayList;

import manouvre.game.interfaces.CardInterface;


/**
 *
 * @author Bartosz
 */


public class CardSet implements DeckInterface{
    
    private int cardSetSize;           // each army includes an Action Deck of 80 cards. 
    private int nation;
    private int cardID;
   
    private Random randomGener = new Random();

    ArrayList<Integer> list = new ArrayList<Integer>();

    public CardSet(int size, int nation){ //for nation description see CardInterface
        this.cardSetSize=size;
        this.nation=nation;
        shuffle();
    }
   
    public void shuffle() {
        for(int i=0; i<cardSetSize; i++){
            int randomCard=randomGener.nextInt(80);
        switch (nation){
            case 0:
                cardID=randomCard; 
                break;
            case 1:
                cardID=randomCard+80;
                break;
            case 2:
                cardID=randomCard+2*80;
                break;
            case 3:
                cardID=randomCard+3*80;
                break;
            case 4:
                cardID=randomCard+4*80;    
                break;
            case 5:
                cardID=randomCard+5*80;
                break;
            case 6:
                cardID=randomCard+6*80;  
                break;
            case 7:
                cardID=randomCard+7*80;   
                break;
        }     
            list.add(i, cardID);      
        }
    }

    public void getAllCards(){
    for(int i : list){
       System.out.println(i);     
    }
    }
    
    @Override
    public int cardsLeft() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CardInterface dealCard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
  
    
}
