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

    
    public enum Set{DICE1d6,DICE1d8,DICE1d10,DICE2d6,DICE2d8,DICE2d10;
        public static Set getFromString(String dice){
            String diceStr = "DICE"+ dice;
        return valueOf(diceStr);
        }
    };

    public enum Type {D6, D8, D10;};

    Type type;
    int result;
  
    public Dice(Type type) {
        this.type = type;
        generateResult();
    }
  
    private void  generateResult()
    {
    switch(type){
        case D6:
            result = k6();
        break;
        case D8:
            result = k8();
         break;
        case D10:
            result = k10();
        break;
        }
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

    public Dice.Type getType() {
        return type;
    }

    public void setType(Dice.Type type) {
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
