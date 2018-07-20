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

/**
 *
 * @author piotr_grudzien
 */
public class WithrdawCommand implements Command {

        Command moveUnitCommand;
        String senderPlayerName;
        String log;

        public WithrdawCommand(Command moveUnitCommand,  String senderPlayerName) {
            this.moveUnitCommand = moveUnitCommand;
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {
                       
            moveUnitCommand.execute(game);
            log = senderPlayerName +" withdrawn" ;
            
            game.getCombat().setState(Combat.PURSUIT);
            /*
            Attacking player chooses unit to pursuit
            */
            game.swapActivePlayer();
            game.notifyAbout(EventType.PURSUIT);
            
            if(game.getCurrentPlayer().isActive())
            {
               game.notifyAbout(EventType.PUSRUIT_AFTER_WITHDRAW);
            }
        }

        @Override
        public void undo(Game game) {
            moveUnitCommand.undo(game);
            game.getCurrentPlayer().setMoved(true);
        }

        @Override
        public String logCommand() {
            return log;
        }

        @Override
        public String getType() {
            return Command.PLAY_CARD;
        }

    }
