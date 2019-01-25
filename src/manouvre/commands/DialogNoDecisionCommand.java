/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import manouvre.events.EventType;
import manouvre.game.Game;
import manouvre.interfaces.Command;

/**
 *
 * @author Piotr
 */
public class DialogNoDecisionCommand implements Command{

    String playerName;
    Command setupCommand;
    
    
    public DialogNoDecisionCommand() {
 
    }

    @Override
    public void execute(Game game) {
        game.notifyAbout(EventType.DIALOG_NO_DECISION);
    }

    @Override
    public void undo(Game game) {
        
    }
    
    @Override
    public String logCommand(){
        return "No button pressed";
    
    }

    @Override
    public Type getType() {
        return Command.Type.END_SETUP;
    }
    
    
    
}
