/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.events.EventType;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Unit;
import manouvre.interfaces.Command;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author piotr_grudzien
 */
public class TakeHitCommand implements Command{
    
    String playerName;
    Unit hitUnit;
    String log;
    boolean eliminate;

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(TakeHitCommand.class.getName());
    
    
    public TakeHitCommand(String playerName, Unit hitUnit, boolean eliminate) {
        this.playerName = playerName;
        this.hitUnit = hitUnit;
        this.eliminate = eliminate;
    }
    
    @Override
    public void execute(Game game) {
            
        Unit unit = game.getUnit(hitUnit);
        
        if(!eliminate) 
        {   
            game.injureUnit(unit);
            if(  !unit.isEliminated())
            {
            game.notifyAbout(EventType.COMBAT_DEFENDER_TAKES_HIT);
            log = "Combat ends with defending unit takes a hit";
            }
            else 
            {
             game.notifyAbout(EventType.COMBAT_DEFENDER_ELIMINATE);
             log = "Combat ends with defending unit takes a hit and is eliminated";
            }
        
        }
        else 
        {
         game.eliminateUnit(unit);
         game.notifyAbout(EventType.COMBAT_DEFENDER_ELIMINATE);
         log = "Combat ends with defending unit is eliminated";
        }
        
        LOGGER.debug(game.getCurrentPlayer().getName() + " unit.isEliminated() " + unit.isEliminated());
        LOGGER.debug(game.getCurrentPlayer().getName() + " game.getCombat().getState() " + game.getCombat().getState());
        if(unit.isEliminated())
        {
            if(game.getCombat().getState().equals(Combat.State.DEFENDER_DECIDES))
            {
                
                game.swapActivePlayer();
                LOGGER.debug(game.getCurrentPlayer().getName() + " swapActivePlayer " + game.getCurrentPlayer().isActive());
            }
                                
            game.checkConditionAndStartAdvancementOrEndCombat();
        }
        else 
        {
            if(game.getCombat().getState().equals(Combat.State.DEFENDER_DECIDES))
            {
                game.swapActivePlayer();
                LOGGER.debug(game.getCurrentPlayer().getName() + " swapActivePlayer " + game.getCurrentPlayer().isActive());
            }
            game.checkCommittedAttackandEndCombat();
        }    
        game.checkGameOver();
    }

    @Override
    public void undo(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String logCommand() {
       return log;
    }

    @Override
    public String getType() {
        return Command.TAKE_HIT;
    }
    
    
    
}
