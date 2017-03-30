/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.Card;
import manouvre.game.Game;
import manouvre.game.Param;
import manouvre.game.interfaces.Command;

/**
 *
 * @author Piotr
 */
public class CardCommands {
    
public static class MoveToTableCommand implements Command{    
            
    
        Card card;

        public MoveToTableCommand(Card card) {
            this.card = card;
        }
     
        @Override
        public void execute(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }
    }    
 
public static class ForcedMarchCommand implements Command {

        Command moveUnitCommand;
        Card card;
        Command moveToTableCommand;
    
        public ForcedMarchCommand(Command moveUnitCommand, Card card) {
            this.moveUnitCommand = moveUnitCommand;
            this.card = card;
            this.moveToTableCommand = new CardCommands.MoveToTableCommand(card);
            
            
        }

        @Override
        public void execute(Game game) {
          
            moveToTableCommand.execute(game);
            moveUnitCommand.execute(game);
        }

        @Override
        public void undo(Game game) {
            
            moveUnitCommand.undo(game);
            
        }

        @Override
        public String logCommand() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }
        
        



}    
   
}


