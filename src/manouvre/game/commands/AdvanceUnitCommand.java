/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import java.util.ArrayList;
import manouvre.game.Card;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;

/**
 *
 * @author piotr_grudzien
 */
public class AdvanceUnitCommand extends MoveUnitCommand  {
    
     ArrayList<Card> pursuitCards;
     CardCommands.PursuitCommand pc;
    
    public AdvanceUnitCommand(String playerName, Unit unit, Position newPosition, ArrayList<Card> pursuitCards) {

        
        super(playerName, unit, newPosition);
                
        
        
        this.pursuitCards = pursuitCards;
        pc = new CardCommands.PursuitCommand(playerName, pursuitCards);
 
    }
    
    public void execute(Game game)  {
            
            /*
            Move un it
            */
            super.execute(game);
            /*
            Ends combat if card/unit has not pursuit mode
            */
            
            if(pursuitCards.size()> 0)
            {
            pc.execute(game);   
            }
            else    
            {  
            /*
            End Combat
            */
            game.getCombat().endCombat(game);
            game.unselectAllUnits();
            }
            
            
}
    
    @Override
    public String logCommand(){
        
        String log = playerName + " has advanced unit " + storedUnit.getName() + " to position " + newPosition.toString() + "/n";
        log +=  pc.logCommand();
     
        return log;
 
    }

}
