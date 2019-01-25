/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.events.EventType;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.interfaces.Command;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Piotr
 */
public class NextPhaseCommand implements Command{
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(NextPhaseCommand.class.getName());     
    String activePlayerName;
    int phase;
    CardCommands.CleanTableCommand cleanTableCommand;
    public NextPhaseCommand(String playerName, int phase) {
        activePlayerName = playerName;
        this.phase = phase;
        cleanTableCommand = new CardCommands.CleanTableCommand(playerName);
    }
     public String getPhaseName(int phase){
      
           switch(phase){
           case Game.SETUP:
           {
           return "Setup";
           }    
           case Game.DISCARD:
           {
            return "Discard";
           }
           case Game.DRAW:
           {
             return "Draw";
           }
           case Game.MOVE:
           {
            return "Move";
            }
           case Game.COMBAT:
           {
            return "Combat";
           }
            case Game.RESTORATION:
           {
            return "Restoration";
           }
           }   
           return null;
      }
    
    
    @Override
    public void execute(Game game) {

        game.getMap().unselectAllTerrains();
        game.getPlayerByName(activePlayerName).resetPlayer();
        if(game.getPhase() == Game.COMBAT)
            game.setCombat(new Combat());
        cleanTableCommand.execute(game);
        game.nextPhase();
        
        
        
        game.notifyAbout(EventType.NEXT_PHASE);
    }

    @Override
    public void undo(Game game) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
    @Override
    public String logCommand(){
        return new String(activePlayerName + " moved to the next phase " + getPhaseName(phase) );
    
    }
    @Override
    public Type getType() {
       return Command.Type.NEXT_PHASE;
    }
    
}
