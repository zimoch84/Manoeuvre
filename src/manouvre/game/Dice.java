/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;
import java.io.Serializable;
import java.util.Random;
import manouvre.game.interfaces.DiceInterface;
/**
 *
 * @author Bartosz
 */
public class Dice implements Serializable{

   /* public Dice(String diceType) {
        this.diceType=diceType;
    }*/
    
    public static final int D6 = 0;
    public static final int D8 = 1;
    public static final int D10 = 3;
    
    
    int type;
    int result;

    public Dice(int type) {
    
    this.type = type;
    generateResult();
    
    }
      
    public void generateResult()
    {
    switch(type){
        
        case Dice.D6:
        {result = k6();
            break;
        }
        case Dice.D8:
        {
            result = k8();
            break;
        }
        case Dice.D10:
        {
            result = k10();
            break;
        }
      
    }
    
    }
            
    
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
 
    static public int k6(){
        int nextInt =  new Random().nextInt(6) + 1;
        return nextInt ;
    }
    static public int k2(){
        int nextInt =  new Random().nextInt(2) + 1;
        return nextInt ;
    }
    static public int k8(){
        int nextInt =  new Random().nextInt(8) + 1;
        return nextInt ;
    }
    static public int k10(){
        int nextInt =  new Random().nextInt(10) + 1 ;
        return nextInt ;
    }
    static public int dk6(){
        int nextInt =  new Random().nextInt(6) +  new Random().nextInt(6) + 2;
        return nextInt ;
    }
    
    static public int dk8(){
        int nextInt = new Random().nextInt(8) + new Random().nextInt(8) + 2;
        return nextInt ;
    }
    
    static public int dk10(){
        int nextInt = new Random().nextInt(10) + new Random().nextInt(10) + 2;
        return nextInt ;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
    
    
    
}
