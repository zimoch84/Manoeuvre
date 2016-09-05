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

/**
 *
 * @author Bartosz
 */
public class CardGUI {
    Image imgFull;
    int OverCard=0;
    int Selected=0;
    Card card;
    
//    int posX;
//    int posY;
    
   
    /**
     * Gets the card image based on its ID in cards.csv 
     * @param ID    cardID
     * @see resources.cards
     */
    public CardGUI(Card newCard) {
        this.card=newCard;    
        setImg();        
    }
    
    private void setImg(){
        try {
            imgFull = ImageIO.read(new File("resources\\cards\\"+card.getCardImg()));
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
    
    public void setOverCard(int isOverCard) {
        this.OverCard = isOverCard;
    }
    
     public int isOverCard() {
        return OverCard;
    }

    public void setSelected(int isSelected) {
        this.Selected = isSelected;
    }
    
    public int isSelected() {
        return Selected;
    }

    public void printSelect(){
        System.out.println(card.getCardName()+" Card Selection Status:"+Selected);
        
    }
    
    public void printOverCard(){
        System.out.println("Mouse Over "+card.getCardName()+" Card Status: "+OverCard);
        
    }


    
}
