/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Image;
import manouvre.game.Unit;

/**
 *
 * @author Piotr
 */
public class UnitGUI extends Unit {
    
    Image img;

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }
    
    public UnitGUI(int ID) {
        super(ID);
        
        
    }
    
}
