/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import manouvre.events.EventType;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.interfaces.ClientInterface;
import manouvre.network.client.Message;
import manouvre.interfaces.Command;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Piotr
 */
public class CommandQueue extends Observable implements Observer{
    
    private ArrayList<Command> commands = new ArrayList<Command>();
    private Game game;
 
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(CommandQueue.class.getName());   
    ClientInterface client;
    
    public CommandQueue(Game game,  ClientInterface client) {
        this.game = game;
        this.client = client;
        
    }

    public void storeAndExecute(Command cmd) {
        this.commands.add(cmd);
        cmd.execute(game);
        setChanged();
        notifyObservers(cmd);
   }
    
    public void storeAndExecuteWithoutLogs(Command cmd) {
      this.commands.add(cmd);
      cmd.execute(game);
   }
    
    public void storeAndExecuteAndSend(Command cmd) {
        this.commands.add(cmd);
        cmd.execute(game); 

        Message message = new Message(Message.COMMAND, game.getCurrentPlayer().getName() , cmd.getType(), "IN_CHANNEL");
        message.setCommand(cmd);
        client.send(message);
        
        setChanged();   
        notifyObservers(cmd);
        
      
   }
    public void undoCommand(Command cmd) {
    
         this.commands.add(cmd);
         cmd.undo(game);
       
    }
    
    public void undoLastCommandBeforeGuirellas() {
    
        /*Last one command is guirella so need to move one more */
         Command cmd = commands.get(commands.size()-2);
         
         cmd.undo(game);

         /*
         Remove last command
         */
         commands.remove(commands.size()-2);
    
    }
    
    public Command getLastCommand()
    {
        return commands.get(commands.size() -1);
    }
    
    public Player getPlayer()
    {
        return game.getCurrentPlayer();
    }

    @Override
    public void update(Observable o, Object arg) {
       
        
        String dialogType = (String) arg;
        
        LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType); 
        
        switch(dialogType){
            case  EventType.GUIRELLA_PLAYED:
            {
                undoLastCommandBeforeGuirellas();
                break;
            }}
    }
    
    
    
}
