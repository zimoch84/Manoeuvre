/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
    * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import manouvre.game.Player;
import manouvre.interfaces.ClientInterface;
import manouvre.network.core.Message;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author piotr_grudzien
 */
public class ManouvreWindowListener implements WindowListener{
        Player player;
        ClientInterface client;
        Thread clientThread;
        private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(GameWindow.class.getName());
        public ManouvreWindowListener(Player player, ClientInterface client, Thread clientThread) {
            this.player = player;
            this.client = client;
            this.clientThread = clientThread;
        }
     
        @Override public void windowOpened(WindowEvent e) {}
        @Override public void windowClosing(WindowEvent e) {
        try{
            if( e.getSource().getClass().equals(MainChatWindow.class)||
                e.getSource().getClass().equals(GameWindow.class)){    
                
                client.send(new Message("message",player.getName(), ".bye", "SERVER"));
                if(clientThread!= null) 
                    clientThread.stop();  
            }
            
            else if  (e.getSource().getClass().equals(RoomWindow.class)  ){
                client.send(new Message("message",player.getName(), ".bye", "SERVER"));
            }
        }
        catch(Exception ex){
           LOGGER.error(ex.toString());
        } 
        }
        @Override public void windowClosed(WindowEvent e) {}
        @Override public void windowIconified(WindowEvent e) {}
        @Override public void windowDeiconified(WindowEvent e) {}
        @Override public void windowActivated(WindowEvent e) {}
        @Override public void windowDeactivated(WindowEvent e) {}
}
