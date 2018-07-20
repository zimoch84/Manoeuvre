/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui.panels;

import java.awt.Graphics;
import javax.swing.JTabbedPane;
import javax.swing.plaf.TabbedPaneUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 *
 * @author piotr_grudzien
 */
public class HandTabbedPane extends JTabbedPane {

    TabbedPaneUI paneUI;
    
    
    public HandTabbedPane() {
        super();
        paneUI = new BasicTabbedPaneUI(){
        @Override
        protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex){}
        };
        this.setUI(ui);
        
    }
    
    
    
}
