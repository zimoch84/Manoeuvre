package Game;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bartosz
 */
public class Card {
    private int deckValue;
    private int faceValue;
    private int suitValue;
    
    public Card (int deckValue, int faceValue, int suitValue){
     this.deckValue=deckValue;
     this.faceValue=faceValue;
     this.suitValue=suitValue;   
    } 
    
    public int getDeckValue (){
        return deckValue;
    }
    public int getFaceValue (){
        return faceValue;
    }
    public int getSuitValue (){
        return suitValue;
    }
    
    
}
