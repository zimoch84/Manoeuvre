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
import manouvre.gui.CommandLogger;
import manouvre.gui.GameWindow;
import manouvre.network.client.Message;
import manouvre.game.interfaces.CommandInterface;
import static java.lang.Thread.sleep;

/**
 *
 * @author Piotr
 */
public class CommandQueue {
    
    ArrayList<CommandInterface> commands = new ArrayList<CommandInterface>();
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
    

    public void storeAndExecute(CommandInterface cmd) {
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
    
    public void storeAndExecuteWithoutLogs(CommandInterface cmd) {
      this.commands.add(cmd);
      cmd.execute(game);
      
      
      gameWindow.refreshAll();
      gameWindow.repaint();
     // cmd=null;//I thing we should delete commands  after completion
   }
    
    public void storeAndExecuteAndSend(CommandInterface cmd) {
        this.commands.add(cmd);
        
        commandLogger.log(cmd);
        
        gameWindow.refreshAll();
        gameWindow.repaint();
       
        

        Message message = new Message(Message.COMMAND, game.getCurrentPlayer().getName() , cmd.getType(), "IN_CHANNEL");
      
        message.setCommand(cmd);
        client.send(message);
       // cmd=null;//I thing we should delete commands after completion
        
        
      
   }
    public void undoCommand(CommandInterface cmd) {
    
         this.commands.add(cmd);
         cmd.undo(game);
         gameWindow.repaint();
         gameWindow.refreshAll();
    
    }
    
    public void undoLastCommand() {
    
         CommandInterface cmd = commands.get(commands.size()-1);
         cmd.undo(game);
         gameWindow.repaint();
         gameWindow.refreshAll();
         /*
         Remove last command
         */
         commands.remove(commands.size()-1);
    
    }

}
