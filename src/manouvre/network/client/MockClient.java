
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
        
        hostIn = new PipedInputStream();
        guestIn = new  PipedInputStream();
        
        hostOut = new PipedOutputStream();
        hostOut.connect(guestIn);
        hostOut.flush();
        
        guestOut = new PipedOutputStream();
        guestOut.connect(hostIn);
        guestOut.flush();
        
        hostClient = new HostClient(hostIn, hostOut);
        hostThread = new Thread(hostClient);
        hostThread.start();
       
        guestClient = new GuestClient(guestIn, guestOut);
        guestThread = new Thread(guestClient);
        guestThread.start();
    }
        public static void main(String[] args) throws IOException{
            
                MockClient mock = new MockClient();
                Message msgTest = new Message(Message.BYE, "fds", 0, "fsdf");
        
                mock.hostClient.send(msgTest);
                }
       }
    
    
    class HostClient implements ClientInterface , Runnable{


    PipedInputStream hostIn;
    PipedOutputStream hostOut;

    ObjectInputStream objectHostIn; 
    ObjectOutputStream objectHostOut;
    
    boolean keepRunning = true;
    public HostClient(PipedInputStream hostIn, PipedOutputStream hostOut) throws IOException {
        this.hostIn = hostIn;
        this.hostOut = hostOut;
        
        objectHostOut = new ObjectOutputStream(hostOut);
        objectHostOut.flush();
        objectHostIn = new ObjectInputStream(hostIn);
       
        
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

    PipedInputStream guestIn;
    PipedOutputStream guestOut;

    boolean keepRunning = true;
    
    ObjectInputStream objectGuestIn; 
    ObjectOutputStream objectGuestOut;
    public GuestClient(PipedInputStream guestIn, PipedOutputStream guestOut) throws IOException {
        this.guestIn = guestIn;
        this.guestOut = guestOut;
        
        objectGuestOut = new ObjectOutputStream(guestOut);
        objectGuestOut.flush();
        
        objectGuestIn = new ObjectInputStream(guestIn);
        
        
        
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

    


