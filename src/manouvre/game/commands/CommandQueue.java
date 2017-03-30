/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import java.util.ArrayList;
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
    
    public CommandQueue(Game game, CommandLogger commandLogger, GameWindow gameWindow , ClientInterface client) {
        this.game = game;
        this.commandLogger = commandLogger;
        this.gameWindow = gameWindow;
        this.client = client;
    }
    

    public void storeAndExecute(Command cmd) {
      this.commands.add(cmd);
      cmd.execute(game);
      commandLogger.log(cmd);
      
      gameWindow.repaint();
      gameWindow.refreshAll();
      
      
   }
    
    public void storeAndExecuteWithoutLogs(Command cmd) {
      this.commands.add(cmd);
      cmd.execute(game);
      
      gameWindow.repaint();
      gameWindow.refreshAll();
      
      
   }
    
    public void storeAndExecuteAndSend(Command cmd) {
        this.commands.add(cmd);
        cmd.execute(game);
        commandLogger.log(cmd);
        gameWindow.repaint();
        gameWindow.refreshAll();

        Message message = new Message(Message.COMMAND, game.getCurrentPlayer().getName() , cmd.getType(), "IN_CHANNEL");
        message.setCommand(cmd);
        client.send(message);
      
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
