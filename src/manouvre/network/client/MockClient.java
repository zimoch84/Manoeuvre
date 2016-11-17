
package manouvre.network.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.interfaces.ClientInterface;
import manouvre.game.interfaces.Command;
import manouvre.gui.GameWindow;

/**
 *
 * @author Piotr
 */
public class MockClient {


    public HostClient hostClient;
    public GuestClient guestClient;
    Thread hostThread, guestThread;
    
    PipedInputStream hostIn;
    PipedOutputStream hostOut;
    
    PipedInputStream guestIn;
    PipedOutputStream guestOut;
    
    public GameWindow clientGameHost;
    public GameWindow clientGameGuest;
    
    public MockClient() throws IOException {
        
        
        
        hostOut = new PipedOutputStream();
        guestOut = new PipedOutputStream();
       
        hostIn = new PipedInputStream(guestOut);
        guestIn = new  PipedInputStream(hostOut);
        
        
        hostClient = new HostClient(hostIn, hostOut);
        hostThread = new Thread(hostClient);
        hostThread.start();
    
        
        guestClient = new GuestClient(guestIn, guestOut);
        guestThread = new Thread(guestClient);
        guestThread.start();
    
    }
    
    class HostClient implements ClientInterface , Runnable{


    PipedInputStream hostIn;
    PipedOutputStream hostOut;

    ObjectInputStream objectHostIn; 
    ObjectOutputStream objectHostOut;
    
    boolean keepRunning;
    public HostClient(PipedInputStream hostIn, PipedOutputStream hostOut) {
        this.hostIn = hostIn;
        this.hostOut = hostOut;
    }
    

    @Override
    synchronized public void send(Message msgOut) {
        try {
            objectHostOut.writeObject(msgOut);
            objectHostOut.flush();
        } catch (IOException ex) {
            Logger.getLogger(HostClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    synchronized public void handle(Message msgIn) {
        
    switch( msgIn.getMessageType() ){
        case Message.COMMAND:
                        Command executeCommand = msgIn.getCommand();
                    executeCommand.execute(clientGameHost.getGame());
                    clientGameHost.repaint();
                    break;
                  default:
                       System.out.println("Host Client" + msgIn.toString()) ;
              
                 }
        
       
    }

   @Override
    public void run() {
        
        while(keepRunning){
            Message msg = null;
            try {
                msg = (Message) objectHostIn.readObject();
                System.out.println("Incoming : "+msg.toString());
                }
            catch(ClassCastException ex) {
                keepRunning = false;
                System.out.println(" ClassCastException " + ex);
            } 
            catch (IOException ex) {  
                keepRunning = false;
                System.out.println("IOException" + ex);
            } 
            catch (ClassNotFoundException ex) {
                keepRunning = false;
                System.out.println("ClassNotFoundException" + ex);
            }
       
            handle(msg);
            }

        }


}

class GuestClient implements ClientInterface , Runnable{

    PipedInputStream guestIn;
    PipedOutputStream guestOut;

    boolean keepRunning = true;
    
    ObjectInputStream objectGuestIn; 
    ObjectOutputStream objectGuestOut;
    public GuestClient(PipedInputStream guestIn, PipedOutputStream guestOut) throws IOException {
        this.guestIn = guestIn;
        this.guestOut = guestOut;
        
        objectGuestIn = new ObjectInputStream(guestIn);
        objectGuestOut = new ObjectOutputStream(guestOut);
        
    }

   @Override
   synchronized public void send(Message msgOut) {
        try {
            objectGuestOut.writeObject(msgOut);
            objectGuestOut.flush();
        } catch (IOException ex) {
            Logger.getLogger(HostClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    synchronized public void handle(Message msgIn) {
         switch( msgIn.getMessageType() ){
        case Message.COMMAND:
                        Command executeCommand = msgIn.getCommand();
                    
                    /*
                    Executing command over game on server
                    */
                    executeCommand.execute(clientGameGuest.getGame());
                    clientGameGuest.repaint();
                    
                       break;
                  default:
                       System.out.println("Guest Client" + msgIn.toString()) ;
              
                 }
    }

    @Override
    public void run() {
        
        
        while(keepRunning){
            Message msg = null;
            try {
                msg = (Message) objectGuestIn.readObject();
                System.out.println("Guest Client Incoming : "+msg.toString());
                }
            catch(ClassCastException ex) {
                keepRunning = false;
                System.out.println("SocketClient.run() ClassCastException " + ex);
                ex.printStackTrace();
            } 
            catch (IOException ex) {  
                keepRunning = false;
                System.out.println("SocketClient.run() IOException" + ex);
                ex.printStackTrace();
            } 
            catch (ClassNotFoundException ex) {
                keepRunning = false;
                System.out.println("SocketClient.run() ClassNotFoundException" + ex);
                ex.printStackTrace();
            }
            
            handle(msg);
   
            }

        }



}

    
}

