/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.util.Observable;
import java.util.Observer;
import manouvre.commands.CommandQueue;
import manouvre.interfaces.FrameInterface;
import manouvre.interfaces.Command;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Piotr
 */
public class CommandLogger implements Observer {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(CommandLogger.class.getName());   
    
    FrameInterface frame;
    
    public CommandLogger(FrameInterface frame) 
    
    {
    this.frame = frame;
    
    }
      
    public void log(Command command){
            
    frame.printOnChat(command.logCommand());
     
        
    }

    @Override
    public void update(Observable o, Object arg) {
       
        if(o instanceof CommandQueue)
        {
           CommandQueue cmdQueue = (CommandQueue) o;
           
        frame.printOnChat(cmdQueue.getLastCommand().logCommand());
        LOGGER.debug(cmdQueue.getPlayer().getName() + " wykonano command " + cmdQueue.getLastCommand().logCommand());
        }
       
       
    }
    
    
    
}
