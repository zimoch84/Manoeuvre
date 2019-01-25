/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.client;

import manouvre.network.core.Message;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.interfaces.ClientInterface;
import manouvre.gui.CommandLogger;
import manouvre.interfaces.Command;
import static java.lang.Thread.sleep;
import manouvre.commands.CommandQueue;
import static java.lang.Thread.sleep;

/**
 *
 * @author Piotr
 */
public class QueueClient  {
    
    ArrayList<Message> guestQueue, hostQueue;

    public HostClient hostClient;
    public GuestClient guestClient;
    Thread hostThread, guestThread;
    
    public CommandQueue cmdHost, cmdQuest;
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
                    cmdHost.storeAndExecute(executeCommand);
                   // clientGameHost.checkPopUps();
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
                cmdQuest.storeAndExecute(executeCommand);
                //clientGameGuest.checkPopUps();
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

