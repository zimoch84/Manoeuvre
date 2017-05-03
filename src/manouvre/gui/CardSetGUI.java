/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.util.ArrayList;
import manouvre.game.Card;
import manouvre.game.CardSet;

//import java.awt.image.BufferedImage;

/**
 * Retrives card picture/image
 * @author Bartosz
 */

public class CardSetGUI {
   
    
    ArrayList<CardGUI> cardListGui = new ArrayList<CardGUI>();  
    CardSet cardSet;  //decide which cardSet shall be processed
   // CardGUI backCover;

   
    
    
    public CardSetGUI (CardSet newCardSet){
       this.cardSet = newCardSet;
       reSet();
    }
    public CardSetGUI (ArrayList<Card> newCardSet){
       for(int i=0; i<newCardSet.size(); i++){
       cardListGui.add(i, new CardGUI(newCardSet.get(i)));
       }
    }
    
    public boolean isCardSelected(){
    
        return cardSet.isCardSelected();
        
    }
    
    public void reSet(){ 
       cardListGui.clear(); //clear the list
       for(int i=0; i<cardSet.cardsLeftInSet(); i++){
       cardListGui.add(i, new CardGUI(cardSet.getCardByPosInSet(i)));
       }
    }
    public void getAllCardsImg(){
        for(int i=0; i<cardListGui.size(); i++){
           System.out.println("Card " +i+": " + cardListGui.get(i).imgFull);     
        }
    }  
   
    public int cardsLeftInSet() {
        return cardListGui.size();    
    }
    public CardGUI getCardByPosInSet(int cardPosition){
       return cardListGui.get(cardPosition);     
    }
    
    public void removeCardBySetID(int CardSetIdToRemove){
       cardListGui.remove(CardSetIdToRemove); 
    }
    
    public int getCardIDByPosInSet(int CardSetId){
       return cardListGui.get(CardSetId).card.getCardID(); 
    }
    
    public int getPositionInSetByCardID(int cardID) {
        for (int i=0; i<cardListGui.size(); i++){
            if(cardListGui.get(i).getCardID()==cardID){
                return i;
            }
        }
        return 99;
     }
    
    public String getCardNameByPosInSet(int cardPosition){
       return cardListGui.get(cardPosition).card.getCardName();     
    }
    
       public CardSet getCardSet() {
        return cardSet;
    }

    public void setCardSet(CardSet cardSet) {
        this.cardSet = cardSet;
    } 
}
