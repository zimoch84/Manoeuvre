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
import manouvre.game.Unit;

/**
 *
 * @author Piotr
 */
public class UnitGUI extends Unit {
    
    Image imgFull;
    Image imgReduced;

    public Image getImg() {
        if(isInjured())
                return imgReduced;
        else return imgFull;
                        
    }

    
    public UnitGUI(int ID) {
        super(ID);
        try {
            imgFull = ImageIO.read(new File("resources\\units\\"+  getImageFullName()   ));
            imgReduced = ImageIO.read(new File("resources\\units\\"+  getImageReducedName()   ));
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
