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
    
    Image imgFull;
    boolean OverCard=false;
    boolean Selected=false;
    CardSet cardSet;
    public ArrayList<CardSet> cardListGui = new ArrayList<CardSet>();

    /**
     * Gets the card image based on its ID in cards.csv 
     * @param ID    cardID
     * @see resources.cards
     */
    public CardSetGUI(CardSet newCardSet) {
        this.cardSet=newCardSet;       
        try {
            imgFull = ImageIO.read(new File("resources\\cards\\"+card.getCardImg()));
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void setImg(){
        for (int i=0; i<=cardSet.cardsLeftInSet(); i++){
            
        }
    }
    
    
    /**
     * Gets the card image based on its object 
     * @param card    Card Object
     * @see game.Card.java
     */
    public CardSetGUI(Card card) {
        try {
            imgFull = ImageIO.read(new File("resources\\cards\\"+  card.getCardImg()   ));
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }                   
    }   
    /**
     * Returns card image to be used in GUI
     * @return card image
     */
    public Image getImgFull() {
        return imgFull;
    }
    
    public void setOverCard(boolean isOverCard) {
        this.OverCard = isOverCard;
    }
     public boolean isOverCard() {
        return OverCard;
    }

    public void setSelected(boolean isSelected) {
        this.Selected = isSelected;
    }
    
    public boolean isSelected() {
        return Selected;
    }

    public void printSelect(){
        System.out.println("Card Selection Status:"+Selected);
        
    }
    
    public void printOverCard(){
        System.out.println("Mouse Over Card Status: "+OverCard);
        
    }


     
    
}
