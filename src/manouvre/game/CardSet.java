/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.util.Random;
import java.util.ArrayList;

import manouvre.game.interfaces.CardInterface;
import manouvre.game.interfaces.CardSetInterface;


/**
 *
 * @author Bartosz
 */


public class CardSet implements CardSetInterface{
    
    private int cardSetSize=0;           // each army includes an Action Deck of 80 cards. 
    private int nation;
    private int cardID;
   
    private Random randomGener = new Random();

    public ArrayList<CardInterface> cardList = new ArrayList<CardInterface>();

    /**
     * Establish object for CARD DECK 
     * @param size - size of the CardDeck
     * @param nation - number of the nation - for details see CardInterface
     * shuffleDeck - to made a Deck
     */
    public CardSet(int size, int nation){ 
        this.cardSetSize=size;
        this.nation=nation;
        System.out.println("211");
        shuffleDeck(size); 
    }
    /**
     * Establish object for HAND 
     */
    public CardSet(int size){
         this.cardSetSize=size; 
    } 
    /**
    * Establish empty object for USED CARDS 
    */
    public CardSet(){ 
        this.cardSetSize=80;
    }
    
    //-----------------DECK------------------------------------------------
    /**
     * shuffle all the possible cards (480) to get the right ones for specific nation
     * @param range 
     */
    public void shuffleDeck(int range) {   
         //System.out.println("range= " + range);
        for (int i=0; i<=range; i++){
        int randomCardID=randomGener.nextInt(range);
        switch (nation){
            case 0:
                cardID=randomCardID; 
                break;
            case 1:
                cardID=randomCardID+80;
                break;
            case 2:
                cardID=randomCardID+2*80;
                break;
            case 3:
                cardID=randomCardID+3*80;
                break;
            case 4:
                cardID=randomCardID+4*80;    
                break;
            case 5:
                cardID=randomCardID+5*80;
                break;
            case 6:
                cardID=randomCardID+6*80;  
                break;
            case 7:
                cardID=randomCardID+7*80;   
                break;
        }     
        cardList.add(i, new Card(cardID));
        }
     
    }
    //------------------HAND----------------------------------------
    /**
     * shuffle range of cards to this Set from another Set. F.ex. 6 cards to HAND from CARD DECK
     * @param range - number of the cards
     * @param otherCardSet - other set of Cards
     *  
     */
    public void addRandomCardsFromOtherSet(int range, CardSetInterface otherCardSet){ //add the card from another Set (f.ex. Deck)
       CardInterface randomCard;
       for(int i=0; i<=range; i++){
           randomCard=otherCardSet.dealRandomCardFromThisSet();
       if (cardList.size()<cardSetSize){  //if it is possible to add the card  
           cardList.add(randomCard);  //add the card
       }
       else{
           System.out.println("CardSet is Full. No more cards allowed");
       } 
       }
    }
    /**
     * Dealing a card to another set. F.ex. from HAND to USED CARDS
     * @param cardToDeal - card to be given away
     * @param otherCardSet - where this card should go
     */
     public void dealCardToOtherSet(CardInterface cardToDeal, CardSetInterface otherCardSet) {
        CardInterface temp=cardList.get(cardList.indexOf(cardToDeal));
        otherCardSet.addCardToThisSet(temp);
        cardList.remove(cardToDeal);
        
    }
    
    //--------------GENERAL FOR ALL----------------------------
    public void addCardToThisSet(CardInterface newCard){
        if (cardList.size()<cardSetSize){  //if it is possible to add the card  
           cardList.add(newCard);  //add the card
        }
        else{
           System.out.println("CardSet is Full. No more cards allowed");
        }          
    }  
    public CardInterface dealRandomCardFromThisSet(){
         int randomCard=randomGener.nextInt(cardList.size());
         CardInterface tempCard=cardList.get(randomCard);
         cardList.remove(randomCard);
         return  tempCard;  
     }
    
    public void getAllCardsNamesFromSet(){
    for(int i=0; i<cardList.size(); i++){
       System.out.println("Card " +i+": " + cardList.get(i).getCardName());     
    }
    }  
    public void getAllCardsIDFromSet(){
    for(int i=0; i<cardList.size(); i++){
       System.out.println("Card " +i+": " + cardList.get(i).getCardID());     
    }
    }  
    
    public CardInterface getCardByPosInSet(int cardPosition){
       return cardList.get(cardPosition);     
    }

    public int cardsLeftInSet() {
        return cardList.size();    
    }


    

   }
 
