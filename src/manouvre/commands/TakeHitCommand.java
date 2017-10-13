/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.game.CardCommandFactory;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Param;
import manouvre.game.Unit;
import manouvre.interfaces.Command;
import manouvre.state.MapInputStateHandler;
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
            
        Unit unit = game.getUnitByName(hitUnit.getName());
        
        if(!eliminate) 
        {   
            unit.takeHit(game);
            if(  !unit.isEliminated())
            {
            game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_TAKES_HIT);
            log = "Combat ends with defending unit takes a hit";
            }
            else 
            {
             game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
             log = "Combat ends with defending unit takes a hit and is eliminated";
            }
        
        }
        else 
        {
         unit.eliminate(game);
         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
         log = "Combat ends with defending unit is eliminated";
        }
        
        if(unit.isEliminated())
        {
            if(game.getCombat().getState() == Combat.DEFENDER_DECIDES)
            {
                game.swapActivePlayer();
            }
            game.getCombat().setState(Combat.PURSUIT);

            if(game.getCurrentPlayer().isActive())
            {
                LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT ");
                game.mapInputHandler.setState(MapInputStateHandler.PICK_ONE_UNIT);
            }
            else 
            {
                LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.NOSELECTION ");
                game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
            }
        }
        else 
        {
            game.getCombat().endCombat(game);
            LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.NOSELECTION ");
            game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
        }    
            
        
        
        
        
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
    public int getType() {
        return Param.TAKE_HIT;
    }
    
    
    
}
