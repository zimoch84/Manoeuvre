/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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

    public CardSetGUI (CardSet newCardSet){
       this.cardSet = newCardSet;
       reSet();
    }
    
    public void reSet(){ 
       cardListGui.clear(); //clear the list
     //  System.out.println("cardSet.cardsLeftInSet():"+cardSet.cardsLeftInSet());
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
    
    public int getCardIDBySetID(int CardSetId){
       return cardListGui.get(CardSetId).card.getCardID(); 
    }
     
    
}
