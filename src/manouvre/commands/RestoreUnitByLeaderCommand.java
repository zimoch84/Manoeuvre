/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import java.util.ArrayList;
import manouvre.events.EventType;
import manouvre.game.Card;
import manouvre.game.Dice;
import manouvre.game.Game;
import manouvre.game.Unit;
import manouvre.interfaces.Command;
import manouvre.network.server.UnoptimizedDeepCopy;


public class RestoreUnitByLeaderCommand implements Command {

    private static final long serialVersionUID = -1306760703066967345L;
    
    Card leaderCard ;
    ArrayList<Unit> healingUnits;
    String playerName;
    ArrayList<Dice> d6dices;
    CardCommands.MoveToTableCommand mtt;
    
    String log;

    public RestoreUnitByLeaderCommand(String playerName, ArrayList<Unit> healingUnits,  Card leaderCard) {
	this.leaderCard = leaderCard;
        this.playerName = playerName;
        
        ArrayList<Unit> unitsClone = (ArrayList<Unit>) UnoptimizedDeepCopy.copy (healingUnits);
        this.healingUnits = unitsClone;
        
        d6dices = new ArrayList<>();
        
        for(Unit healingUnit:this.healingUnits){
            Dice d6 = new Dice(Dice.Type.D6);
           d6dices.add(d6);
           healingUnit.setRestorationDice(d6);
        }
        mtt = new CardCommands.MoveToTableCommand(leaderCard, playerName);
	}

    @Override
    public void execute(Game game) {
        log = playerName + " attempted to restore " + healingUnits.size() + " units and successfully restored: ";
        mtt.execute(game);
        boolean successful = false;
        for(Unit restoreUnit:healingUnits){
            Unit unit = game.getUnit(restoreUnit);
            Dice restorationDice = restoreUnit.getRestorationDice();
            unit.setRestorationDice(restoreUnit.getRestorationDice());
            if( leaderCard.getLeaderRally()  >=  restorationDice.getResult()  )
            { 
                successful = true;
                unit.restoreUnit();
                log+= unit.getName() + " ";
            }
            if(successful)
                log+= "none units";
        }
        
        game.notifyAbout(EventType.RESTORATION_BY_LEADER);
        game.unselectAllUnits();
    }
    
    @Override
    public void undo(Game game){
  
    }
    @Override
    public String logCommand(){
        return log;
    }

    @Override
    public String getType() {
        return Command.RESTORE_UNIT;
    }
}
