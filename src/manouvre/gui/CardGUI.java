/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import manouvre.game.Card;

//import java.awt.image.BufferedImage;

/**
 * Retrives card picture/image
 * @author Bartosz
 */
public class CardGUI {
    
    Image imgFull;
    boolean OverCard=false;
    boolean Selected=false;
    /**
     * Gets the card image based on its ID in cards.csv 
     * @param ID    cardID
     * @see resources.cards
     */
    public CardGUI(int ID) {
        Card card = new Card(ID);
        System.out.println(card.getCardName());
         System.out.println("resources\\cards\\"+card.getCardImg());
        
        try {
            imgFull = ImageIO.read(new File("resources\\cards\\"+card.getCardImg()));
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    /**
     * Gets the card image based on its object 
     * @param card    Card Object
     * @see game.Card.java
     */
    public CardGUI(Card card) {
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
