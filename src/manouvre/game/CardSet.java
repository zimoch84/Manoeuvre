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
import manouvre.interfaces.CardInterface;
import manouvre.interfaces.CardSetInterface;


/**
 *
 * @author Bartosz
 */


public class CardSet extends Observable implements CardSetInterface, Serializable{
    
    private static final long serialVersionUID = 455321L;
    private int defCardSetSize=0;           
    private int nation;
    public String name;
    
    private ArrayList<Card> cardList = new ArrayList<Card>();
    
    /**
     * Establish object for CARD DECK 
     * @param size - size of the CardDeck
     * @param nation - number of the nation - for details see CardInterface
     * shuffleDeck - to made a Deck
     */
    public CardSet(int nation, String name){ 
        
        this.nation=nation;
        
        switch (name){
            case  "DRAW" : {
            makeDeck(); 
            this.name = "DRAW";
            this.defCardSetSize=60;
            break;
            }
            case "HAND" :
            {
            this.defCardSetSize=5;
            this.name = "HAND";
            break;
            }
            case "DISCARD":
            {
            this.defCardSetSize=60;
            this.name = "DISCARD";
            break;
            }
            case "TABLE":{
            this.defCardSetSize=8;
            this.name = "TABLE";
            break;
            }
            case   "TABLE_DEFENDING":
               this.defCardSetSize=8;
            this.name = "TABLE_DEFENDING";
            break;
            case "TEST" :
             this.defCardSetSize=60;
            this.name = "TABLE_DEFENDING";
            }
    }

    
    public boolean isAnyCardSelected() {
        for(Card selectedCard : cardList )
        {
            if(selectedCard.isSelected()) return true;
        }
        return false;
    }

    public ArrayList<Card> getSelectedCards()
    {
        ArrayList<Card> selectedCards = new ArrayList<>();
        
        for(Card selectedCard : cardList )
        {
            if(selectedCard.isSelected()) selectedCards.add(selectedCard);
        }
        return selectedCards;
 
    }
    public void unselectAllCards()
    {
         for(Card card : cardList )
             card.setSelected(false);
    }
    
    public void unselectMouseOverCard()
    {
         for(Card card : cardList )
             card.setMouseOverCard(false);
    }
    
    public void selectCard(Card cardToSelect){
    
        if(getCard(cardToSelect)!=null)
            getCard(cardToSelect).setSelected(true);
        
    }
    
    public void deselectCard(Card cardToDeSelect){
    
        if(getCard(cardToDeSelect)!=null)
            getCard(cardToDeSelect).setSelected(false);
        
    }
    
        

    //-----------------DECK------------------------------------------------
    /**
     * Get cards for specific nation out of all (480) 
     * @param range 
     */
    public void makeDeck() {   
        int cardID = 0;
        int range = 60;
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

        if(name.equals("DRAW")){
        
            if(size()< range) 
            {
                for(int i=0; i<size(); i++) 
                    moveCardTo(getLastCard(false), otherCardSet);
                /*
                Empty discard pile and set nightfall in game
                */
                notifyObservers("LAST_CARD_DRAWN");
                /*
                Make new clean deck and remove cards that are in hand already
                */
                makeDeck();
                
                /*
                Remove cards from deck that are in hand already
                */
                for(Card cardInHand: otherCardSet.getCardList()){
                    removeCard(cardInHand);
      
                }
            }
        }
        
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
        Card temp = getCardByPos(size()-1);
        if(remove)
            removeCard(getCardByPos(size()-1));
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

    //--------------GENERAL FOR ALL----------------------------
    @Override
    public void addCard(Card newCard){
        if (cardList.size()<defCardSetSize){  
            cardList.add(newCard);  
        }
        else{
           System.out.println("CardSet is Full. No more cards allowed");
        }          
    }  
   
    public void removeCard(Card removedCard){
         cardList.remove(removedCard);
        
     }
    
    @Override
    public Card getCardByPos(int cardPosition){
       return cardList.get(cardPosition);     
    }
    
    public Card getFirstCardByName(String name, boolean remove){
        for (int i=0; i<cardList.size(); i++){
            if(cardList.get(i).getCardName().equals(name)){
                Card card = cardList.get(i);
                if(remove)cardList.remove(i);
                return card;
            }
        }  
        return new Card();
    }
 
    public int getPositionInSet(Card card) {
        return cardList.indexOf(card);
     }
    
    public String getCardNameByPosInSet(int cardPosition){
       return cardList.get(cardPosition).getCardName();     
    }
    public int getCardTypeByPosInSet(int cardPosition){
       return cardList.get(cardPosition).getType();     
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
        return defCardSetSize;
    }
     
    public Card getCard(Card card){
        if(cardList.contains(card)){
              return  cardList.get(cardList.indexOf(card));
        }
        return null;
    }

    public ArrayList<Card> getCardList() {
        return cardList;
    }
    
    public boolean contains(Card card){
        if(getFirstCardByName(card.getCardName(), false).getType() == Card.NO_CARD)
            return false;
        else 
            return true;
    }
}
 
