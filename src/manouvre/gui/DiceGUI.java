/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import manouvre.game.Dice;
import manouvre.game.Terrain;

/**
 *
 * @author Piotr_Grudzien
 */
public class DiceGUI {

    Dice dice;
    BufferedImage diceImage;
    final int D6SQUARE_WIDTH = 80;
    final int D6SQUARE_HEIGHT = 80;
    
    public DiceGUI(Dice dice) {
    
    this.dice = dice;
    setImage();
    }
    private void setImage(){
    
    BufferedImage bigImage ;
    BufferedImage cutImage;
    switch(dice.getType()){
    
        case Dice.D6:
        {
            try{
            String filename = "resources\\dices\\D6.png";
            bigImage = ImageIO.read(new File(filename));
            cutImage = bigImage.getSubimage((dice.getResult()-1)*D6SQUARE_WIDTH, 0, D6SQUARE_WIDTH, D6SQUARE_HEIGHT);
            diceImage = cutImage;
            }
            catch(IOException ioe){
                System.out.println("manouvre.gui.DiceGUI.setImage()");
            }
        }
    
    }    

    }
    
    BufferedImage getImage()
    {
    return diceImage;
    }
   
}
