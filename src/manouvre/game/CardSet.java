/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import manouvre.game.interfaces.CardInterface;

import manouvre.game.interfaces.CardSetInterface;


/**
 *
 * @author Bartosz
 */


public class CardSet implements CardSetInterface, Serializable{
    
    private static final long serialVersionUID = 455321L;
    private int cardSetSize=0;           // each army includes an Action Deck of 80 cards. 

 
    
    private int nation;
    private int cardID;
    
    boolean cardSelected;
    
    private Random randomGener = new Random();

    public ArrayList<Card> cardList = new ArrayList<Card>();

    /**
     * Establish object for CARD DECK 
     * @param size - size of the CardDeck
     * @param nation - number of the nation - for details see CardInterface
     * shuffleDeck - to made a Deck
     */
    public CardSet(int size, int nation){ 
        this.cardSetSize=size;
        this.nation=nation;
        makeDeck(size); 
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
        this.cardSetSize=60;
    }

    public boolean isCardSelected() {
        return cardSelected;
    }

    public void setCardSelected(boolean cardSelected) {
        this.cardSelected = cardSelected;
    }
    
    
    //-----------------DECK------------------------------------------------
    /**
     * Get cards for specific nation out of all (480) 
     * @param range 
     */
    public void makeDeck(int range) {   
        for (int i=0; i<range; i++){
        switch (nation){
            case CardInterface.BR:
                cardID=i; 
                break;
            case CardInterface.AU:  //1
                cardID=i+60;
                break;
            case CardInterface.FR:
                cardID=i+2*60;
                break;
            case CardInterface.OT:
                cardID=i+3*60;
                break;
            case CardInterface.PR:
                cardID=i+4*60;    
                break;
            case CardInterface.RU:
                cardID=i+5*60;
                break;
            case CardInterface.SP:
                cardID=i+6*60;  
                break;
            case CardInterface.US:
                cardID=i+7*60;   
                break;
        } 
        cardList.add(i, new Card(cardID));
        randomizeCards(cardList);
        }
     
    }
    //------------------HAND----------------------------------------
    /**
     * shuffle range of cards to this Set from another Set. F.ex. 6 cards to HAND from CARD DECK
     * @param range - number of the cards
     * @param otherCardSet - other set of Cards
     *  
     */
    public void addRandomCardsFromOtherSet(int range, CardSetInterface otherCardSet, boolean setPlayable){ //add the card from another Set (f.ex. Deck)
       Card randomCard;     
       for(int i=0; i<range; i++){
           randomCard=otherCardSet.dealRandomCardFromThisSet();
       if(setPlayable==true) randomCard.setPlayableInPhase(true);//false by 
       if (cardList.size()<cardSetSize){  //if it is possible to add the card  
           cardList.add(randomCard);  //add the card
       }

       }    
  
    }
    /**
     * Adds number of cards to this set, from the top of the sepecyfied other set
     * @param range - number of cards to be moved
     * @param otherCardSet - specyfy name of the other set
     * @param setPlayable  - set as playable TRUE or not playable FALSE
     */
    public void addCardsFromTheTopOfOtherSet(int range, CardSet otherCardSet, boolean setPlayable){
        Card temp;
        for(int i=0; i<range; i++){   
        temp=otherCardSet.lastCardFromThisSet(true);
        temp.setPlayableInPhase(setPlayable);    
       if (cardList.size()<cardSetSize){  //if it is possible to add the card  
           cardList.add(temp);  //add the card
       }
       else System.err.println("Too many cards in:"+cardList.getClass().getName()+", sent from: "+ otherCardSet.getClass().getName());
       }    
    }
    /**
     * Returns the last card from the stack
     * @param remove - if TRUE last card will be removed from the stack
     * @return 
     */
    public Card lastCardFromThisSet(boolean remove){
        int size=cardsLeftInSet();
        Card temp = getCardByPosInSet(size-1);
        if(remove)removeCardFromThisSet(getCardByPosInSet(size-1));
        return temp;
    }
    
    
    /**
     * Dealing a card to another set based on Object. F.ex. from HAND to USED CARDS
     * @param cardToDeal - card object to be given away
     * @param otherCardSet - where this card should go
     */
     public void dealCardToOtherSet(Card cardToDeal, CardSetInterface otherCardSet) {
        Card temp=cardList.get(cardList.indexOf(cardToDeal));
        otherCardSet.addCardToThisSet(temp);
        cardList.remove(cardToDeal);
        
    }
    public void dealCardToOtherSetByCardID(int cardIDToDeal, CardSetInterface otherCardSet) { 
        for (int i=0; i<cardList.size(); i++){
                if(cardList.get(i).getCardID()==cardIDToDeal){
                    Card temp=cardList.get(i);
                    otherCardSet.addCardToThisSet(temp);
                    cardList.remove(i);
                }
            }       
    }
    
     /**
     * Dealing a card to another set based on Object. F.ex. from HAND to USED CARDS
     * @param cardHandPos - position in the hand
     * @param otherCardSet - where this card should go
     */
     public void dealCardToOtherSetByHandPos(int cardHandPos, CardSetInterface otherCardSet) {
        Card temp=cardList.get(cardHandPos);
        otherCardSet.addCardToThisSet(temp);
        cardList.remove(cardHandPos);
        
    }
     
    
    //--------------GENERAL FOR ALL----------------------------
    public void addCardToThisSet(Card newCard){
        if (cardList.size()<cardSetSize){  //if it is possible to add the card  
           cardList.add(newCard);  //add the card
        }
        else{
           System.out.println("CardSet is Full. No more cards allowed");
        }          
    }  
    public Card dealRandomCardFromThisSet(){
         int randomCard=randomGener.nextInt(cardList.size());
         Card tempCard=cardList.get(randomCard);
         cardList.remove(randomCard);
         return  tempCard;  
     }
    
    public void removeCardFromThisSet(Card removedCard){
         cardList.remove(removedCard);
        
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
    
    public Card getCardByPosInSet(int cardPosition){
       return cardList.get(cardPosition);     
    }
    
    public Card getCardByName(String name){
        for (int i=0; i<cardList.size(); i++){
            if(cardList.get(i).getCardName().equals(name)){
                return cardList.get(i);
            }
        }  
        return cardList.get(0);
    }
     
    public String getCardNameByPosInSet(int cardPosition){
       return cardList.get(cardPosition).getCardName();     
    }
    public int getCardTypeByPosInSet(int cardPosition){
       return cardList.get(cardPosition).getCardType();     
    }
    
     public int getCardIDByPosInSet(int cardPosition){
       return cardList.get(cardPosition).getCardID();     
    }

    public int cardsLeftInSet() {
        return cardList.size();    
    }
    /**
     * Sorts the card in set by ID
     */
    public void sortCard(){
        Card temp;
        boolean sorted=false;
        int time=0; 
        
        while(!sorted){
           for (int i=0; i<(cardList.size()-1) ; i++){
               if (cardList.get(i).getCardID()>cardList.get(i+1).getCardID()){
                   temp=cardList.get(i);
                   cardList.set(i, cardList.get(i+1));
                   cardList.set(i+1, temp);
                  
                    time++;
                    i=-1; //if this was performed we have to be sure that the numbers are correct start from begining
                    if (time>500){    
                        sorted=true;
                        System.err.println("SORTING LOOP FAILURE");
                        break;
                    }//stop the loop if failure
               }
               else sorted=true;   
           }  
       }
       
     getAllCardsIDFromSet();
    }
    
    public void randomizeCards(ArrayList<Card> cardset){
        Collections.shuffle(cardset);
       }
       
  
    
 
     public int getPositionInSetByCardID(int cardID) {
        for (int i=0; i<cardList.size(); i++){
            if(cardList.get(i).getCardID()==cardID){
                return i;
            }
        }
        return 99;
     }
        
     public int getCardSetSize() {
        return cardSetSize;
    }

     public Card getSelectedCard(){
     
         if(isCardSelected())
               for(Card checkCard : cardList){
               
                   if(checkCard.isSelected())
                   {
                       return checkCard;
                   }
                   
               }
         return null;
              
     }
     
   }
 
