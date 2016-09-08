package manouvre.network.client;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import manouvre.game.Player;
import manouvre.gui.CreateRoomWindow;
import manouvre.gui.GameWindow;
import manouvre.gui.LoginWindow;
import manouvre.gui.MainChatWindow;
import manouvre.gui.RoomWindow;

public class SocketClient implements Runnable{
    
    public static int port = 5002;
    public static String serverAddr= "localhost";
    public Socket socket;
    public GameWindow ui;
    public LoginWindow welcome;
    public MainChatWindow mainChat;
    public RoomWindow roomWindow;
    
    /*
    Player na poziomie socketu z założenia ma conajmniej swoją nazwę
    */
    public Player player;
    
    public ObjectInputStream In;
    public ObjectOutputStream Out;
    
    
    public SocketClient(GameWindow frame) throws IOException{
        ui = frame; 
        //this.serverAddr = "zimoch.insomnia247.nl"; 
        socket = new Socket(InetAddress.getByName(serverAddr), port);
            
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
        
       
    }
    
    public SocketClient(LoginWindow frame) throws IOException{
        welcome = frame; 
        //this.serverAddr = "zimoch.insomnia247.nl"; 
        socket = new Socket(InetAddress.getByName(serverAddr), port);
            
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
      
    }
 
    @Override
    public void run() {
        boolean keepRunning = true;
        while(keepRunning){
            try {
                Message msg = (Message) In.readObject();
                System.out.println("Incoming : "+msg.toString());
                
                switch( msg.getMessageType() ){
            
                    case Message.LOGIN :
                 
                         if(msg.getContentP() == Message.OK){
                        
                             player = welcome.getPlayer();
                             welcome.setVisible(false);
                                /*
                                Run chat window
                                */
                                 java.awt.EventQueue.invokeLater(new Runnable() {
                                    public void run() {
                                        try {
                                            mainChat = new MainChatWindow(SocketClient.this, player);
                                            mainChat.setVisible(true);
                                        } catch (IOException ex) {
                                            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }); 
                         }
                         break;
                    
                    case Message.CHAT:
                    
                        /*
                        To repair - send to only active windows
                        */
                        
                    if(msg.recipient.equals(ui.getGame().getCurrentPlayer().getName())){
                        if(ui != null)
                             ui.printOnChat(msg.sender + " : " + msg.content) ;
                        
                    }
                    else{
                        if(ui != null)
                            ui.printOnChat(msg.sender +" : " + msg.content);
                    }   
                        
                        break;
                            
                            
                    case Message.GET_ROOM_LIST : 
                      /*
                        add channels to list
                        */
                    mainChat.setRoomList(msg.getChannelList());
                    
                      break;
                  
                    case Message.CREATE_ROOM:
                      
                       if(msg.getContentP() == Message.OK)
                      /*
                        Run room window
                        */
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                roomWindow = new RoomWindow(SocketClient.this, player, CreateRoomWindow.AS_HOST);
                                roomWindow.setVisible(true);
                            }
                        });
                     break;  
                   
                  case Message.JOIN_ROOM:
                      
                       if(msg.getContentP() == Message.OK)
                      /*
                        Run room window
                        */
                             java.awt.EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                roomWindow = new RoomWindow(SocketClient.this, welcome.getPlayer(), CreateRoomWindow.AS_GUEST);
                                roomWindow.setVisible(true);
                            }
                        });
                      break;
                  
                  default:
                       System.out.println("manouvre.network.client.SocketClient.run() Unknown msg type" + msg.toString()) ;
              
                 }
                
    
  
            }
            catch(Exception ex) {
                keepRunning = false;
                
                System.out.println("manouvre.network.client.SocketClient.run()" + ex);
                
                //ui.printOnChat("Aplication : Connection Failure \n" + ex.getMessage());
//                ui.jButton1.setEnabled(true); 
//                ui.jTextField1.setEditable(true); 
//                ui.jTextField2.setEditable(true);
//                ui.jButton4.setEnabled(false); 
//                ui.jButton5.setEnabled(false); 
//                ui.jButton5.setEnabled(false);
                
//                for(int i = 1; i < ui.model.size(); i++){
//                    ui.model.removeElementAt(i);
//                }
                
                //ui.clientThread.stop();
                
                System.out.println("Exception SocketClient run()");
                ex.printStackTrace();
            }
        }
    }
    
    public void send(Message msg){
        try {
            Out.writeObject(msg);
            Out.flush();
            System.out.println("Outgoing : "+msg.toString());
       } 
        catch (IOException ex) {
            System.out.println("Exception SocketClient send()");
        }
    }
    
    public void closeThread(Thread t){
        t = null;
    }
}
