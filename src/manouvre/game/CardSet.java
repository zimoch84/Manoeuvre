/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Observable;
import manouvre.game.interfaces.CardInterface;

import manouvre.game.interfaces.CardSetInterface;


/**
 *
 * @author Bartosz
 */


public class CardSet extends Observable implements CardSetInterface, Serializable{
    
    private static final long serialVersionUID = 455321L;
    private int cardSetSize=0;           // each army includes an Action Deck of 60 cards. 
 
    private int nation;
    boolean cardSelected;
    public String name;
    
    public ArrayList<Card> cardList = new ArrayList<Card>();
    public ArrayList<Card> selectionSeq = new ArrayList<Card>();

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
        this.name = "DRAW";
        
    }
    /**
     * Establish object for HAND 
     */
    public CardSet(int size){
         this.cardSetSize=size; 
         this.name = "HAND";
        
    } 
    /**
    * Establish empty object for USED CARDS 
    */
    public CardSet(String name){ 
        this.cardSetSize=60;
        this.name = name;
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
        int cardID = 0;
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
                cardList.add(new Card(cardID));
            }   
        randomizeCards();
     
    }
    //------------------HAND----------------------------------------
    /**
     * Adds number of cards to this set, from the top of the sepecyfied other set
     * @param range - number of cards to be moved
     * @param otherCardSet - specyfy name of the other set
     * @param setPlayable  - set as playable TRUE or not playable FALSE
     */
    public void moveTopXCardsTo(int range, CardSet otherCardSet){

    for(int i=0; i<range; i++)  
        moveCardTo(getLastCard(false), otherCardSet);
    }

     
     public void clear(int range){ //remove first "range" cards set
          for(int i=0; i<range; i++){
             cardList.remove(0); 
          }
      }
      public void clear(){ 
        cardList.clear(); 
      }
    /**
     * Returns the last card from the stack
     * @param remove - if TRUE last card will be removed from the stack
     * @return 
     */
    public Card getLastCard(boolean remove){
        if(size()>0){
        Card temp = getCardByPosInSet(size()-1);
        if(remove)
            removeCardFromThisSet(getCardByPosInSet(size()-1));
        return temp;
        }
     return null;
    }
    /**
     * Dealing a card to another set based on Object. F.ex. from HAND to USED CARDS
     * @param cardToDeal - card object to be given away
     * @param otherCardSet - where this card should go
     */
    public void moveCardTo(Card card, CardSetInterface otherSet)
    {
     if(cardList.size() > 0 )   
     {   
         if(cardList.contains(card))
            {
                cardList.remove(card);
                otherSet.addCard(card);
                
                if(cardList.size() == 0 && name.equals("DRAW") )   
                    notifyObservers("LAST_CARD_DRAWN");
                
            }
     }
     else 
         notifyObservers("LAST_CARD_DRAWN");
    }
    public Card drawCardFromSet(Card card){
        if(cardList.size()>0&&cardList.contains(card)){
        Card t_card=cardList.get(cardList.indexOf(card));
        cardList.remove(card);
        return t_card;
        }
        return (Card)null;
     }
    
    //--------------GENERAL FOR ALL----------------------------
    @Override
    public void addCard(Card newCard){
        if (cardList.size()<cardSetSize){  //if it is possible to add the card  
            cardList.add(newCard);  //add the card
        }
        else{
           System.out.println("CardSet is Full. No more cards allowed");
        }          
    }  
   
    public void removeCardFromThisSet(Card removedCard){
         cardList.remove(removedCard);
        
     }
    /*
    TODO:Remove this
    */
    
    public ArrayList<String> getAllCardsNamesFromSet(){
        ArrayList<String> cardNames = new ArrayList<>();
    for(int i=0; i<cardList.size(); i++){
       System.out.println("Card " +i+": " + cardList.get(i).getCardName()); 
       cardNames.add(cardList.get(i).getCardName());
    }
    return cardNames;
    }  
        
    public Card getCardByPosInSet(int cardPosition){
       return cardList.get(cardPosition);     
    }
    
    public Card getCardByName(String name, boolean remove){
        for (int i=0; i<cardList.size(); i++){
            if(cardList.get(i).getCardName().equals(name)){
                Card card = cardList.get(i);
                if(remove)cardList.remove(i);
                return card;
            }
        }  
        return (Card)null;
    }
     
    public Card getCardByType(int type){
        for (int i=0; i<cardList.size(); i++){
            if(cardList.get(i).getHQType() == type){
                Card card = cardList.get(i);
                return card;
            }
        }  
        return (Card)null;
    }
    
    public int getPositionInSet(Card card) {
        return cardList.indexOf(card);
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

    @Override
    public int size() {
        return cardList.size();    
    }
    /**
     * Sorts the card in set by ID
     */
    public void sortCard(){
        cardList.sort( 
        new Comparator<Card>() {
        @Override
        public int compare(Card card1, Card card2){
            if (card1.getCardID() > card2.getCardID()) 
                return 0 ;
            else return 1 ;
        }
        }
        );
        }
    
    private void randomizeCards(){
        Collections.shuffle(cardList);
    }
  
       
    public int getCardSetSize() {
        return cardSetSize;
    }
     
     public Card getCardFromSetByID(int cardID){
          for (int i=0; i<cardList.size(); i++){
            if(cardList.get(i).getCardID()==cardID){
                return cardList.get(i);
            }
        }
        return (Card)null;
     
     }
     
    public Card getCard(Card card){
        if(cardList.contains(card)){
              return  cardList.get(cardList.indexOf(card));
        }
        return null;
    }
     
   }
 
