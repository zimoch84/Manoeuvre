/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

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
    public NextPhaseCommand(String playerName, int phase) {
        activePlayerName = playerName;
        this.phase = phase;
    
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
        game.setInfoBarText(null);
        game.getPlayerByName(activePlayerName).resetPlayer();
        game.getCardCommandFactory().createCleanTableCommand().execute(game);

        LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
        game.getCardCommandFactory().resetFactory();
        if(game.getCombat() != null)
            game.setCombat(null);
        game.nextPhase();
        
       
        
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
    public String getType() {
       return Command.NEXT_PHASE;
    }
    
}
