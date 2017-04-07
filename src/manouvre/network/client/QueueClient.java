/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.client;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.interfaces.ClientInterface;
import manouvre.game.interfaces.Command;
import manouvre.gui.CommandLogger;
import manouvre.gui.GameWindow;
import static java.lang.Thread.sleep;

/**
 *
 * @author Piotr
 */
public class QueueClient  {
    
    ArrayList<Message> guestQueue, hostQueue;

    public GameWindow clientGameHost, clientGameGuest;
    
    public HostClient hostClient;
    public GuestClient guestClient;
    Thread hostThread, guestThread;
    
    public CommandLogger commandLoggerHost, commandLoggerGuest;
    
    
    public QueueClient() {
        try {
            hostQueue = new ArrayList<>();
            guestQueue = new ArrayList<>();
            
            hostClient = new HostClient(hostQueue, guestQueue);
            hostThread = new Thread(hostClient);
            hostThread.start();
            
            guestClient = new GuestClient(hostQueue, guestQueue);
            guestThread = new Thread(guestClient);
            guestThread.start();
            
            
        } catch (IOException ex) {
            Logger.getLogger(QueueClient.class.getName()).log(Level.SEVERE, null, ex);
        }
     
    }
    public static void main (String args[]){
    QueueClient q = new QueueClient();
    
        q.hostClient.send(new Message("ds", "dassd", "oko", "dsas"));
    
        
        
    }
class HostClient implements ClientInterface , Runnable{
 ArrayList<Message> guestQueue, hostQueue;
 
    
    boolean keepRunning;
    public HostClient(ArrayList<Message> hostQueue, ArrayList<Message> guestQueue) throws IOException {
        this.guestQueue = guestQueue;
        this.hostQueue = hostQueue;
        keepRunning = true;
    } 
    
    @Override
    synchronized public void send(Message msgOut) {
            guestQueue.add(msgOut);
       
    }
    @Override
    synchronized public void handle(Message msgIn) {
        
    switch( msgIn.getMessageType() ){
        case Message.COMMAND:
            
            
                    Command executeCommand = msgIn.getCommand();
                    clientGameHost.cmd.storeAndExecute(executeCommand);
                    break;
        default:
             System.out.println("Host Client" + msgIn.toString()) ;
              
                 }
    }

   @Override
    public void run() {
        System.out.println("Starting host client");
        while(keepRunning){
            Message msg = null;
            if(!hostQueue.isEmpty()) 
            {
                msg = hostQueue.get(0);
                System.out.println("Host read" + msg.toString());
                handle(msg);
                hostQueue.remove(0);
            }
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(QueueClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }

}
class GuestClient implements ClientInterface , Runnable{

    ArrayList<Message> guestQueue, hostQueue;
    boolean keepRunning;
    
    public GuestClient(ArrayList<Message> hostQueue, ArrayList<Message> guestQueue) throws IOException {
        this.guestQueue = guestQueue;
        this.hostQueue = hostQueue;
        keepRunning = true;
        
    }

    @Override
    synchronized public void send(Message msgOut) {
            hostQueue.add(msgOut);
       
    }

    @Override
    synchronized public void handle(Message msgIn) {
         switch( msgIn.getMessageType() ){
            case Message.COMMAND:
                Command executeCommand = msgIn.getCommand();
                clientGameGuest.cmd.storeAndExecute(executeCommand);
                break;
            default:
                 System.out.println("Guest Client" + msgIn.toString()) ;

                 }
    }
    @Override
    public void run() {
        System.out.println("Starting guest client");
        while(keepRunning){
            Message msg = null;
            if(!guestQueue.isEmpty()) 
            {
                msg = guestQueue.get(0);
                System.out.println("Guest read" + msg.toString());
                handle(msg);
                guestQueue.remove(0);
            }
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(QueueClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        }
}
}

