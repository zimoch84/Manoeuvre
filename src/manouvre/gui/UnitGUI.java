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
public class UnitGUI  {
    
    BufferedImage imgFull;
    BufferedImage imgReduced;
    
    Unit unit;
    
    public BufferedImage getImg() {
        if(unit.isInjured())
                return imgReduced;
        else return imgFull;
                        
    }

    public UnitGUI(int ID) {
        unit = new Unit(ID);
        try {
            imgFull = ImageIO.read(new File("resources\\units\\"+  unit.getImageFullName()   ));
            imgReduced = ImageIO.read(new File("resources\\units\\"+  unit.getImageReducedName()   ));
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public UnitGUI(Unit unit) {
        this.unit = unit;
        try {
            imgFull = ImageIO.read(new File("resources\\units\\"+  unit.getImageFullName()   ));
            imgReduced = ImageIO.read(new File("resources\\units\\"+  unit.getImageReducedName()   ));
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
        try {
               imgFull = ImageIO.read(new File("resources\\units\\"+  unit.getImageFullName()   ));
               imgReduced = ImageIO.read(new File("resources\\units\\"+  unit.getImageReducedName()   ));
           } catch (IOException ex) {
               Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
           }   
        
    }
    
        
}
