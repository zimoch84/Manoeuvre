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

/**
 *
 * @author Piotr_Grudzien
 */
public class DiceGUI {

    Dice dice;
    BufferedImage diceImage;
    static final int D6SQUARE_WIDTH = 80;
    static final int D6SQUARE_HEIGHT = 80;
    static final int D8SQUARE_WIDTH = 84;
    static final int D8SQUARE_HEIGHT = 80;
    static final int D10SQUARE_WIDTH = 100;
    static final int D10SQUARE_HEIGHT = 100;
    
    
    static final float SCALE_FACTOR_D6 = 0.65f;
    static final float SCALE_FACTOR_D8 = 0.65f;
    static final float SCALE_FACTOR_D10 = 0.6f;
    
    
    public DiceGUI(Dice dice) {
    
    this.dice = dice;
    setImage();
    }
    private void setImage(){
    
    BufferedImage bigImage ;
    BufferedImage cutImage;
    switch(dice.getType()){
    
        case D6:
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
            break;
        }
        
        case D8:
        {
            try{
            String filename = "resources\\dices\\D8.png";
            bigImage = ImageIO.read(new File(filename));
            switch(dice.getResult()){
            case 1: cutImage = bigImage.getSubimage((10)*D8SQUARE_WIDTH, 0, D8SQUARE_WIDTH, D8SQUARE_HEIGHT); break;
            case 2: cutImage = bigImage.getSubimage((6)*D8SQUARE_WIDTH, 0, D8SQUARE_WIDTH, D8SQUARE_HEIGHT);break;
            case 3: cutImage = bigImage.getSubimage((8)*D8SQUARE_WIDTH, 0, D8SQUARE_WIDTH, D8SQUARE_HEIGHT);break;
            case 4: cutImage = bigImage.getSubimage((0)*D8SQUARE_WIDTH, 0, D8SQUARE_WIDTH, D8SQUARE_HEIGHT);break;
            case 5: cutImage = bigImage.getSubimage((14)*D8SQUARE_WIDTH, 0, D8SQUARE_WIDTH, D8SQUARE_HEIGHT);break;
            case 6: cutImage = bigImage.getSubimage((2)*D8SQUARE_WIDTH, 0, D8SQUARE_WIDTH, D8SQUARE_HEIGHT);break;
            case 7: cutImage = bigImage.getSubimage((16)*D8SQUARE_WIDTH, 0, D8SQUARE_WIDTH, D8SQUARE_HEIGHT);break;
            case 8: cutImage = bigImage.getSubimage((4)*D8SQUARE_WIDTH, 0, D8SQUARE_WIDTH, D8SQUARE_HEIGHT);break;
            default : cutImage = null;
    
            }
            diceImage = cutImage;
            }
            catch(IOException ioe){
                System.out.println("manouvre.gui.DiceGUI.setImage()");
            }
            break;
        }
        
        case D10:
        {
             try{
            String filename = "resources\\dices\\D10.png";
            bigImage = ImageIO.read(new File(filename));
            switch(dice.getResult()){
            case 1: cutImage = bigImage.getSubimage((0)*D10SQUARE_WIDTH, 0*D10SQUARE_HEIGHT , D10SQUARE_WIDTH, D10SQUARE_HEIGHT); break;
            case 2: cutImage = bigImage.getSubimage((1)*D10SQUARE_WIDTH, 0* D10SQUARE_HEIGHT, D10SQUARE_WIDTH, D10SQUARE_HEIGHT);break;
            case 3: cutImage = bigImage.getSubimage((2)*D10SQUARE_WIDTH, 0* D10SQUARE_HEIGHT, D10SQUARE_WIDTH, D10SQUARE_HEIGHT);break;
            case 4: cutImage = bigImage.getSubimage((3)*D10SQUARE_WIDTH, 0* D10SQUARE_HEIGHT, D10SQUARE_WIDTH, D10SQUARE_HEIGHT);break;
            case 5: cutImage = bigImage.getSubimage((0)*D10SQUARE_WIDTH, 1* D10SQUARE_HEIGHT, D10SQUARE_WIDTH, D10SQUARE_HEIGHT);break;
            case 6: cutImage = bigImage.getSubimage((1)*D10SQUARE_WIDTH, 1*D10SQUARE_HEIGHT, D10SQUARE_WIDTH, D10SQUARE_HEIGHT);break;
            case 7: cutImage = bigImage.getSubimage((2)*D10SQUARE_WIDTH, 1*D10SQUARE_HEIGHT, D10SQUARE_WIDTH, D10SQUARE_HEIGHT);break;
            case 8: cutImage = bigImage.getSubimage((3)*D10SQUARE_WIDTH, 1*D10SQUARE_HEIGHT, D10SQUARE_WIDTH, D10SQUARE_HEIGHT);break;
            case 9: cutImage = bigImage.getSubimage((0)*D10SQUARE_WIDTH, 2*D10SQUARE_HEIGHT, D10SQUARE_WIDTH, D10SQUARE_HEIGHT);break;
            case 10: cutImage = bigImage.getSubimage((1)*D10SQUARE_WIDTH, 2*D10SQUARE_HEIGHT, D10SQUARE_WIDTH, D10SQUARE_HEIGHT);break;
            
            default : cutImage = null;
    
            }
            diceImage = cutImage;
            }
            catch(IOException ioe){
                System.out.println("manouvre.gui.DiceGUI.setImage()");
            }
             break;
        }
    
    }    

    }
    
    BufferedImage getImage()
    {
    return diceImage;
    }
   
}
