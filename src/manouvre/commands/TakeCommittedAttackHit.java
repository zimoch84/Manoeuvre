/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.events.EventType;
import manouvre.game.Card;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Unit;
import manouvre.interfaces.Command;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author piotr_grudzien
 */
public class TakeCommittedAttackHit implements Command{
    
    String playerName;
    Unit hitUnit;
    String log;
    Card committedAttackCard;
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(TakeCommittedAttackHit.class.getName());
    
    
    public TakeCommittedAttackHit(String playerName, Unit hitUnit, Card committedAttackCard) {
        this.playerName = playerName;
        this.hitUnit = hitUnit;
        this.committedAttackCard = committedAttackCard;
       
    }
    
    @Override
    public void execute(Game game) {
        Unit unit = game.getUnit(hitUnit);
        game.injureUnit(unit);
            if(  !unit.isEliminated())
                {
            game.notifyAbout(EventType.COMBAT_ATTACKER_TAKES_HIT);
            log = "Unit" +  unit.getName() + " take hit from Committed Attack";
                }
            else 
                {
             game.notifyAbout(EventType.COMBAT_ATTACKER_ELIMINATE);
            log = "Unit" +  unit.getName() + " took hit from Committed Attack and is eliminated" ;
                }
        game.getCombat().removeSupportCard(committedAttackCard);
        game.checkCommittedAttackandEndCombat();
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
