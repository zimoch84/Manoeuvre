/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;
import manouvre.game.interfaces.DiceInterface;
/**
 *
 * @author Bartosz
 */
public class Dice {

   /* public Dice(String diceType) {
        this.diceType=diceType;
    }*/
    
    public static int diceTypeToInt(String diceType){
        switch (diceType){
            case "":
                return 98; //if No Value
            case "1d6": 
                return DiceInterface.DICE1d6;
            case "1d8":
                return DiceInterface.DICE1d8;
            case "1d10":
                return DiceInterface.DICE1d10;
            case "2d6":
                return DiceInterface.DICE2d6;
            case "2d8":
                return DiceInterface.DICE2d8;
            case "2d10":
                return DiceInterface.DICE2d10; 
        }     
        return 99; //if another value
    }
    
}
