  /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import manouvre.game.Card;
import manouvre.game.Player;

/**
 *
 * @author Bartosz
 */
public class CardGUI {

    //-------- CARDS - BOTTOM OF THE SCREEN -----------------------------------
    //scale factor for Cards//Normally cards has 260x375 pixels
    
    Image imgFull;
    Image imgBackCover;
    Card card;

    public static final int CARD_WIDTH = 260;
    public static final int CARD_HEIGHT = 375;
    //scale factor for Cards//Normally cards has 260x375 pixels
    public static final float SCALE_FACTOR= 0.5f;
    public static final float SCALE_FACTOR_TABLE= 0.43f;
    
    public static final int  LIFTSELECTEDBY =20;//pixels if card selected
    
    public static final int WIDTH = Math.round(CardGUI.CARD_WIDTH * CardGUI.SCALE_FACTOR);
    public static final int HEIGHT = Math.round(CardGUI.CARD_HEIGHT * CardGUI.SCALE_FACTOR);
    
    public static final int WIDTH_TABLE = Math.round(CardGUI.CARD_WIDTH * CardGUI.SCALE_FACTOR_TABLE);
    public static final int HEIGHT_TABLE = Math.round(CardGUI.CARD_HEIGHT * CardGUI.SCALE_FACTOR_TABLE);
    public boolean mouseOverCard;
    /**
     * Gets the card image based on its ID in cards.csv 
     * @param ID    cardID
     * @see resources.cards
     */
    public CardGUI()
    {
        this.card = new Card();
    }
    
    public CardGUI(Card newCard) {
        this.card=newCard;    
        setImg();  
        setBackCoverImg();
    }
    
    private void setImg(){
        String filename = "resources/cards/" + card.getCardImgName();
        try {
            imgFull = ImageIO.read(
                getClass().getClassLoader().
                getResource(filename)
            );    
        } 
        catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getBackCoverImgName(){
        switch (card.getCardFlag()){ //check first card in the list for flag
            case BR:  
                return "BRMask.JPG";      
            case FR: 
                return "FRMask.JPG";    
            case RU: 
                return "RUMask.JPG";    
            case PR: 
                return "PRMask.JPG";    
            case AU: 
                return "AUMask.JPG";    
            case SP: 
                return "SPMask.JPG";    
            case OT: 
                return "OTMask.JPG";  
            case US: 
                return "USMask.JPG"; 
                
        }   
        return "FRNapoleon.JPG";
    }
   
    private void setBackCoverImg(){
        String filename = "resources/cards/" + getBackCoverImgName();
        try {
            imgBackCover = ImageIO.read(
                getClass().getClassLoader().
                getResource(filename)
            );    
        } 
        catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Image getImgBackCover() {
        return imgBackCover;
    }
    /**
     * Returns card image to be used in GUI
     * @return card image
     */
    public Image getImgFull() {
        return imgFull;
    }
    public Image getImgSmall(int frameCrop) {
        //Normally cards has 260x375 pixels
        int x=frameCrop;
        int y=frameCrop;
        int w=260-frameCrop*2;
        int h=375-frameCrop*2;
        
        Image imageSmall = cropImage(imgFull,x,y,w,h);
        return imageSmall;
    }
    private BufferedImage cropImage(Image img, int x, int y, int width, int height){
        BufferedImage buffImage = (BufferedImage)img;
        return buffImage.getSubimage(x, y, width, height);
    }

    public Player.Nation getFlag() {
        return card.getCardFlag();
    }
    public int getCardID() {
        return card.getCardID();
    }
     public int getCardType() {
        return card.getType();
    }
    
   public Card getCard(){
       return card;
   }
    
    @Override
    public String toString()
    {
    return card.toString();
    }
}
