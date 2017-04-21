/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import manouvre.game.interfaces.FrameInterface;
import manouvre.game.interfaces.Command;

/**
 *
 * @author Piotr
 */
public class CommandLogger {

    FrameInterface frame;
    
    public CommandLogger(FrameInterface frame) 
    
    {
    this.frame = frame;
    
    }
      
    public void log(Command command){
                  
        frame.printOnChat(command.logCommand());
       
        
    }
    
}
