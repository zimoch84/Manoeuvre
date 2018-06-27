/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;
import java.util.ArrayList;
import manouvre.events.EventType;
import manouvre.game.Card;
import manouvre.game.Game;
import manouvre.interfaces.Command;
import manouvre.network.server.UnoptimizedDeepCopy;

/**
 *
 * @author Bartosz
 */
public class DiscardCardCommand implements Command {
   
    ArrayList<Card> selectionSeq;
    String senderPlayerName;
    
    public DiscardCardCommand(ArrayList<Card> selectionSeq, String senderPlayerName) {
       this.selectionSeq=
                  (ArrayList<Card>) UnoptimizedDeepCopy.copy(selectionSeq);
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
    if(game.getPlayerByName(senderPlayerName).getName().equals(game.getCurrentPlayer().getName() ))
        
    {
        /*
        Empty selecttion
        */
        game.getCurrentPlayer().getHand().unselectAllCards();
    }
   

       
    }
    
    @Override
    public void undo(Game game){
      
   
      
    }
    
    @Override
    public String logCommand(){
        
        String out = new String(senderPlayerName + " discarded ");
        for (Card card :selectionSeq )
            out += card.toString() + ", "; 
         
        return out;
    
    }

    @Override
    public String getType() {
       return Command.DISCARD_CARD;
    }
    
}

