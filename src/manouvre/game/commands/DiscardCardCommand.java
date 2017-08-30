/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;
import java.util.ArrayList;
import manouvre.game.Card;

import manouvre.game.Game;
import manouvre.game.Param;

import manouvre.game.interfaces.Command;

/**
 *
 * @author Bartosz
 */
public class DiscardCardCommand implements Command {
   
    ArrayList<Card> selectionSeq;
    String senderPlayerName;
    
    public DiscardCardCommand(ArrayList<Card> selectionSeq, String senderPlayerName) {
       this.selectionSeq=selectionSeq;
       this.senderPlayerName=senderPlayerName;
    }
    
    @Override
    public void execute(Game game) {  
         
    for (int i=0; i<selectionSeq.size(); i++){   
           
        game.getPlayerByName(senderPlayerName).
            getHand().moveCardTo(
                    selectionSeq.get(i),  
                    game.getPlayerByName(senderPlayerName).
                            getDiscardPile()

           );
           }
    if(game.getPlayerByName(senderPlayerName).equals(game.getCurrentPlayer().getName() ))
        
    {
        /*
        Empty selecttion
        */
        game.getCurrentPlayer().getHand().selectionSeq.clear();
    }
   
    
       
    }
    
    @Override
    public void undo(Game game){
      
   
      
    }
    
    @Override
    public String logCommand(){
        return new String(senderPlayerName + " discarded " + selectionSeq.size() + " cards");
    
    }

    @Override
    public int getType() {
       return Param.DISCARD_CARD;
    }
    
}

