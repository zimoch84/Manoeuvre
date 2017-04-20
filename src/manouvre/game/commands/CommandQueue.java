/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.Game;
import manouvre.game.interfaces.ClientInterface;
import manouvre.game.interfaces.Command;
import manouvre.gui.CommandLogger;
import manouvre.gui.GameWindow;
import manouvre.network.client.Message;

/**
 *
 * @author Piotr
 */
public class CommandQueue {
    
    ArrayList<Command> commands = new ArrayList<Command>();
    Game game;
    
    CommandLogger commandLogger;
    GameWindow gameWindow;
    
    ClientInterface client;
    
    public CommandQueue(Game game, CommandLogger commandLogger, GameWindow gameWindow , ClientInterface clientInterface) {
        this.game = game;
        this.commandLogger = commandLogger;
        this.gameWindow = gameWindow;
        this.client = clientInterface;
    }
    

    public void storeAndExecute(Command cmd) {
      this.commands.add(cmd);
      cmd.execute(game);
      commandLogger.log(cmd);
      
      
      gameWindow.refreshAll();
      gameWindow.repaint();
        
      System.err.println("CommandQueue.storeAndExecute() " + cmd.toString() + " "
              + game.getCurrentPlayer().getName() + " Phase" + 
              game.getPhaseName(game.getPhase()));
     
     // cmd=null;//I thing we should delete commands after completion
      
   }
    
    public void storeAndExecuteWithoutLogs(Command cmd) {
      this.commands.add(cmd);
      cmd.execute(game);
      
      
      gameWindow.refreshAll();
      gameWindow.repaint();
     // cmd=null;//I thing we should delete commands  after completion
   }
    
    public void storeAndExecuteAndSend(Command cmd) {
        this.commands.add(cmd);
        cmd.execute(game); //execute locally
        commandLogger.log(cmd);
        
        gameWindow.refreshAll();
        gameWindow.repaint();
       
        

        Message message = new Message(Message.COMMAND, game.getCurrentPlayer().getName() , cmd.getType(), "IN_CHANNEL");
      
        message.setCommand(cmd);
        client.send(message);
       // cmd=null;//I thing we should delete commands after completion
        
        
      
   }
    public void undoCommand(Command cmd) {
    
         this.commands.add(cmd);
         cmd.undo(game);
         gameWindow.repaint();
         gameWindow.refreshAll();
    
    }
    
    public void undoLastCommand() {
    
         Command cmd = commands.get(commands.size()-1);
         cmd.undo(game);
         gameWindow.repaint();
         gameWindow.refreshAll();
         /*
         Remove last command
         */
         commands.remove(commands.size()-1);
    
    }


}
