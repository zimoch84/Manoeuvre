/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

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
     String log;
     
    public AdvanceUnitCommand(String playerName, Unit unit, Position newPosition, ArrayList<Card> pursuitCards) {

        
        super(playerName, unit, newPosition);
        this.pursuitCards = pursuitCards;
        pc = new CardCommands.PursuitCommand(playerName, pursuitCards);
 
    }
    
    public void execute(Game game)  {
            
            /*
            Move unit
            */
            super.execute(game);
            log = playerName + " has advanced unit " + storedUnit.getName() + " to position " + newPosition.toString();
            /*
            Ends combat if card/unit has not pursuit mode
            */
            if(!game.getUnit(game.getCombat().getDefendingUnit()).isEliminated())
            {
                if(pursuitCards.size()> 0)
                {
                pc.execute(game); 
                log +=  pc.logCommand();
                }
            }    
            /*
            End Combat
            */
            game.checkCommittedAttackandEndCombat();
            game.unselectAllUnits();
}
    @Override
    public String logCommand(){
        return log;
    }

}
