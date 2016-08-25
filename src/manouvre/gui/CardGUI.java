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
import manouvre.game.Unit;
//import java.awt.image.BufferedImage;
/**
 *
 * @author Bartosz
 */
public class CardGUI {
    
    Image imgFull;
    
    public CardGUI(int ID) {
        Card card = new Card(ID);
        try {
            imgFull = ImageIO.read(new File("resources\\units\\"+card.getCardName()));
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public CardGUI(Card card) {
        try {
            imgFull = ImageIO.read(new File("resources\\units\\"+  card.getCardName()   ));
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }                   
    }   
    
    public Image getImgFull() {
        return imgFull;
    }
    

     
    
}
