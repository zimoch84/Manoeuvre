/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.interfaces.ClientInterface;
import manouvre.gui.CommandLogger;
import manouvre.gui.GameWindow;
import manouvre.network.client.Message;
import manouvre.interfaces.Command;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Piotr
 */
public class CommandQueue {
    
    ArrayList<Command> commands = new ArrayList<Command>();
    Game game;
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(CommandQueue.class.getName());   
    
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
        
        LOGGER.debug(game.getCurrentPlayer().getName() + " wykonano command " + cmd.logCommand());
        commandLogger.log(cmd);

        
        gameWindow.refreshAll();
        gameWindow.repaint();
      
   }
    
    public void storeAndExecuteWithoutLogs(Command cmd) {
      this.commands.add(cmd);
      cmd.execute(game);
      
      LOGGER.debug(game.getCurrentPlayer().getName() + " wykonano command " + cmd.logCommand()
      );
      gameWindow.refreshAll();
      gameWindow.repaint();
     // cmd=null;//I think we should delete commands  after completion
   }
    
    public void storeAndExecuteAndSend(Command cmd) {
        this.commands.add(cmd);
        cmd.execute(game); //execute locally
        commandLogger.log(cmd);
        
        LOGGER.debug(game.getCurrentPlayer().getName() + " wykonano command " + cmd.logCommand());
        
        gameWindow.refreshAll();
        gameWindow.repaint();
       
        

        Message message = new Message(Message.COMMAND, game.getCurrentPlayer().getName() , cmd.getType(), "IN_CHANNEL");
      
        message.setCommand(cmd);
        client.send(message);
       // cmd=null;//I think we should delete commands after completion
        
        
      
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
