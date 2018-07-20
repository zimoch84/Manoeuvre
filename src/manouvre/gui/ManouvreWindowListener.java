/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import manouvre.game.Game;
import manouvre.interfaces.ClientInterface;
import manouvre.network.client.Message;



/**
 *
 * @author piotr_grudzien
 */
public class ManouvreWindowListener implements WindowListener{
        Game game;
        ClientInterface client;
        Thread clientThread;

        public ManouvreWindowListener(Game game, ClientInterface client, Thread clientThread) {
            this.game = game;
            this.client = client;
            this.clientThread = clientThread;
        }
     
        @Override public void windowOpened(WindowEvent e) {}
        @Override public void windowClosing(WindowEvent e) { 
            try{client.send(new Message("message",
                    game.getCurrentPlayer().getName(), ".bye", "SERVER"));
            clientThread.stop();  
            }
            catch(Exception ex){} 
        }
        @Override public void windowClosed(WindowEvent e) {}
        @Override public void windowIconified(WindowEvent e) {}
        @Override public void windowDeiconified(WindowEvent e) {}
        @Override public void windowActivated(WindowEvent e) {}
        @Override public void windowDeactivated(WindowEvent e) {}
}
