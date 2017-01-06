
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
public class MockClient extends Thread{


    public HostClient hostClient;
    public GuestClient guestClient;
    Thread hostThread, guestThread;
    
    PipedInputStream hostIn;
    PipedOutputStream hostOut;
    
    PipedInputStream guestIn;
    PipedOutputStream guestOut;
    
    ObjectInputStream objectHostIn; 
    ObjectOutputStream objectHostOut;
    
    ObjectInputStream objectGuestIn; 
    ObjectOutputStream objectGuestOut;
    
    public GameWindow clientGameHost;
    public GameWindow clientGameGuest;
    
    
    Thread mockThread;
    
    public MockClient() throws IOException, InterruptedException {
        
        hostOut = new PipedOutputStream();
        guestOut = new PipedOutputStream();
        
        hostIn = new PipedInputStream();
        guestIn = new  PipedInputStream();
        hostOut.connect(guestIn);
        guestOut.connect(hostIn);
        
        mockThread = new Thread(this);
        mockThread.start();  
        
                
        hostOut.flush();
        guestOut.flush();
        
        mockThread.join();
        System.out.println("mock Thread joined");      
        
        objectHostOut.flush();
        objectGuestOut.flush();
        
        
        hostClient = new HostClient(objectHostIn, objectHostOut);
        hostThread = new Thread(hostClient);
        hostThread.start();

        guestClient = new GuestClient(objectGuestIn, objectGuestOut);
        guestThread = new Thread(guestClient);
        guestThread.start();
        
        
    }
    
    @Override
    public void run() {
        try {
            System.out.println("Startig Mock  client");
            objectHostOut = new ObjectOutputStream(hostOut);
            objectGuestOut = new ObjectOutputStream(guestOut);
            objectHostIn  = new ObjectInputStream(hostIn);
            objectGuestIn = new ObjectInputStream(guestIn);
            
            
            System.out.println("Object counstructed Mock  client");
               
            
        } catch (IOException ex) {
            Logger.getLogger(MockClient.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
        public static void main(String[] args) throws IOException, InterruptedException{
            
                MockClient mock = new MockClient();
               // Message msgTest = new Message(Message.BYE, "fds", 0, "fsdf");
        
                //mock.hostClient.send(msgTest);
                }
       }
    
    
    class HostClient implements ClientInterface , Runnable{
    ObjectInputStream objectHostIn; 
    ObjectOutputStream objectHostOut;
    
    boolean keepRunning;
    public HostClient(ObjectInputStream objectHostIn, ObjectOutputStream objectHostOut) throws IOException {
        this.objectHostIn = objectHostIn;
        this.objectHostOut = objectHostOut;
        keepRunning = true;
    }   
    @Override
    synchronized public void send(Message msgOut) {
        try {
            objectHostOut.writeObject(msgOut);
            objectHostOut.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    @Override
    synchronized public void handle(Message msgIn) {
        
    switch( msgIn.getMessageType() ){
        case Message.BYE:
            System.out.println(msgIn.toString());
        break;
//        case Message.COMMAND:
//                        Command executeCommand = msgIn.getCommand();
//                    executeCommand.execute(clientGameHost.getGame());
//                    clientGameHost.repaint();
//                    break;
                  default:
                       System.out.println("Host Client" + msgIn.toString()) ;
              
                 }
    }

   @Override
    public void run() {
        System.out.println("Starting host client");
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
            finally {
                try {
                    objectHostIn.close();
                    objectHostOut.close();
                } catch (IOException ex) {
                    Logger.getLogger(MockClient.class.getName()).log(Level.SEVERE, null, ex);
                }
             }
     
            handle(msg);
            }
        }

}

class GuestClient implements ClientInterface , Runnable{

           boolean keepRunning = true;
    
    ObjectInputStream objectGuestIn; 
    ObjectOutputStream objectGuestOut;
    public GuestClient(ObjectInputStream objectGuestIn, ObjectOutputStream objectGuestOut) throws IOException {
        this.objectGuestIn = objectGuestIn;
        this.objectGuestOut = objectGuestOut;
      
        
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
//        case Message.COMMAND:
//                    Command executeCommand = msgIn.getCommand();
//                    Command.execute(clientGameGuest.getGame());
//                    clientGameGuest.repaint();
//                    
//                       break;
                  default:
                       System.out.println("Guest Client" + msgIn.toString()) ;
              
                 }
    }

    @Override
    public void run() {
        
      System.out.println("Starting guest client");  
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
                System.out.println("Guest Client IOException" + ex);
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

    


