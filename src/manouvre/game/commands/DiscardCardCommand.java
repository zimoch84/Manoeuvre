/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;
import java.util.ArrayList;

import manouvre.game.Game;

import manouvre.game.interfaces.Command;

/**
 *
 * @author Bartosz
 */
public class DiscardCardCommand implements Command {
   
    ArrayList<Integer> selectionSeq;

    public DiscardCardCommand(ArrayList<Integer> selectionSeq) {
       
       this.selectionSeq=selectionSeq;
        
    }
    
    @Override
    public void execute(Game game) {  //fing opponent and draw the card
        
           for (int i=0; i<selectionSeq.size(); i++){   
           game.getOpponentPlayer().getHand().dealCardToOtherSetByCardID(selectionSeq.get(i),  game.getOpponentPlayer().getDiscardPile());
           }//check what happen wieht dealCardToOtherSetByCardID   btestfalse
       
    }
    
    @Override
    public void undo(Game game){
      
   
      
    }
    
}

