/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JTextArea;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Piotr
 */
public class ServerLogger implements Observer {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ServerLogger.class.getName()); 
    JTextArea uiLog; 
    public ServerLogger(JTextArea uiLog)    {
        this.uiLog = uiLog;
    }

    @Override
    public void update(Observable o, Object arg) {
        
     boolean isInfo  = arg.toString().contains("INFO");
     boolean iserror = arg.toString().contains("ERROR");
     String text = new Date() + ":"  +(String) arg.toString() + "\n";
     
     if(isInfo)
     LOGGER.info(text);
     else if(iserror)
     LOGGER.error(text);  
     else 
     LOGGER.debug(text); 
     
     uiLog.append(text);
        
    }
    
    
    
}
