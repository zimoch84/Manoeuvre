/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;
import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author Bartosz
 */
public class Dice implements Serializable{

    /*
    Possible dices on cards
    */
    public static final int DICE1d6 = 6;
    public static final int DICE1d8 = 8;
    public static final int DICE1d10 = 10;
    public static final int DICE2d6 = 12;
    public static final int DICE2d8 = 16;
    public static final int DICE2d10 = 20;
    
    public static final int D6 = 0;
    public static final int D8 = 1;
    public static final int D10 = 3;
    
    
    int type;
    int result;
    int max;


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
    
    public int getMax(){
        switch(type){
        
        case Dice.D6:
        {
            max = 6;
            break;
        }
        case Dice.D8:
        {
            max = 8;
            break;
        }
        case Dice.D10:
        {
            max = 10;
            break;
        }
      
    }
        return max;
    }
    
    public static int diceTypeToInt(String diceType){
        switch (diceType){
            case "":
                return 98; //if No Value
            case "1d6": 
                return Dice.DICE1d6;
            case "1d8":
                return Dice.DICE1d8;
            case "1d10":
                return Dice.DICE1d10;
            case "2d6":
                return 12;//Dice.DICE2d6;
            case "2d8":
                return Dice.DICE2d8;
            case "2d10":
                return Dice.DICE2d10; 
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

    @Override
    public String toString(){
        String out;
       
        switch (getType())
        {
            case D6: out="D6-> " + Integer.toString(result);
            return out;
            case D8: out="D8-> " + Integer.toString(result);
            return out;
            case D10: out="D10-> " + Integer.toString(result);
            return out;
        }
        return null;
        
    }
}
