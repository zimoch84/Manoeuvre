package manouvre.network.client;

import java.io.*;
import java.net.*;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.game.interfaces.ClientInterface;
import manouvre.game.interfaces.FrameInterface;
import manouvre.gui.CommandLogger;
import manouvre.gui.CreateRoomWindow;
import manouvre.gui.GameWindow;
import manouvre.gui.LoginWindow;
import manouvre.gui.MainChatWindow;
import manouvre.gui.RoomWindow;
import manouvre.game.interfaces.Command;

public class SocketClient implements Runnable, ClientInterface{
    
    public static int port = 5002;
    public static String serverAddr= "localhost";
    /*
        Port nasluchujacy serwera =  Port socketa klienta 
        Port wynegocjowany z serwerem to localPort socketa klienta
        
        czyli socketClient.socket.Port = server.liteningsocket.port = server.socket.localport
        
        socketClient.socket.Localport = server.socket.port
                
        */
    public Socket socket;

       /*
    User GUI 
    */
    public GameWindow clientGame;
    public LoginWindow welcome;
    public MainChatWindow mainChat;
    public RoomWindow roomWindow;
    /*
    Active window is set from frames above
    */
    private FrameInterface activeWindow ;
    /*
    Player na poziomie socketu z założenia ma conajmniej swoją nazwę
    */
    public Player currentPlayer;
    /*
    Streamy do czytania i pisania w sockecie
    */
    public ObjectInputStream In;
    public ObjectOutputStream Out;

    CommandLogger commandLogger;
    
    boolean keepRunning = true;
    
    public SocketClient(LoginWindow frame) throws IOException{
        welcome = frame; 
        //this.serverAddr = "zimoch.insomnia247.nl";
        
        
        
        //socket = new Socket(InetAddress.getByName(serverAddr), port);
        socket = new Socket(frame.serverAddr, frame.port);   
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
        
        System.out.println("manouvre.network.client.SocketClient.<init>() : LocalPort " + socket.getLocalPort() + " Port " + socket.getPort()) ;
        
        
        
        
    }
    
        public SocketClient() throws IOException{
        socket = new Socket(InetAddress.getByName(serverAddr), port);
            
        Out = new ObjectOutputStream(socket.getOutputStream());
        Out.flush();
        In = new ObjectInputStream(socket.getInputStream());
        System.out.println("manouvre.network.client.SocketClient.<init>() : LocalPort " + socket.getLocalPort() + " Port " + socket.getPort()) ;
      
    }
    
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
 /*
    Klient w run nasłuchuje co otrzyma od serwera i steruje GUI
    
    */
    @Override
    public void run() {
        
        while(keepRunning){
            Message msg = null;
            try {
                msg = (Message) In.readObject();
                System.out.println("Incoming : "+msg.toString());
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
    
    @Override
    public void handle (Message msg){
    
    switch( msg.getMessageType() ){
            
                    case Message.LOGIN :
                 
                         if(msg.getContentP() == Message.OK){
                        
                             currentPlayer = welcome.getPlayer();
                             welcome.setVisible(false);
                            /*
                            Run chat window
                            */
                             java.awt.EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    try {
                                        mainChat = new MainChatWindow(SocketClient.this, currentPlayer);
                                        mainChat.setVisible(true);
                                        setActiveWindow(mainChat);
                                    } catch (IOException ex) {
                                        keepRunning = false;
                                        System.out.println("SocketClient.run() invoke MainChat IOException" + ex);
                                        ex.printStackTrace();
                                    }
                                }
                            }); 
                     }
                         break;
                    
                    case Message.CHAT:
                    
                        
                        mainChat.printOnChat(msg.sender + " : " + msg.content) ;

                        break;
                        
                    case Message.CHAT_IN_ROOM:
                    
                       /*
                        Either roomwindow chat or gamewindow chat
                        */
                        activeWindow.printOnChat(msg.sender + " : " + msg.content) ;

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
                        currentPlayer.setHost(true);
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                roomWindow = new RoomWindow(SocketClient.this, currentPlayer, CreateRoomWindow.AS_HOST);
                                roomWindow.setVisible(true);
                                /*
                                Set focus to this window do recive comunication to
                                */
                                setActiveWindow(roomWindow);
                            }
                        });
                     break;  
                   
                  case Message.JOIN_ROOM:
                      
                       if(msg.getContentP() == Message.OK)
                       {
                           Player hostPlayer = msg.getPlayer();
                          /*
                        Run room window
                        */
                             java.awt.EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                roomWindow = new RoomWindow(SocketClient.this, currentPlayer, CreateRoomWindow.AS_GUEST);
                                roomWindow.setVisible(true);
                                roomWindow.setHostPlayer(hostPlayer);
                                
                                setActiveWindow(roomWindow);
                                roomWindow.printOnChat("Player " +hostPlayer.getName() + " joined room" );
                            }
                        });
                       }
                      if(msg.getContentP() == Message.USER_JOINED_IN_ROOM)
                      {   
                           roomWindow.setGuestPlayer(msg.getPlayer());
                           roomWindow.printOnChat("Player " +msg.getPlayer().getName() + " joined room" );
                      }
                      break;
                  case Message.START_GAME:
                      if(msg.getContentP() == Message.OK)
                      {
                        commandLogger = new CommandLogger(clientGame);
                        Game game = msg.getGame();
                        System.out.println("SocketClient.run() Game: " + game.toString() );
                        System.out.println("SocketClient.run() currentPlayer: " + currentPlayer.toString() );
                       
                        Player hostPlayer = msg.hostPlayer;
                        Player guestPlayer = msg.guestPlayer;

                        /* Create and display the form */
                        java.awt.EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                try {
                                    if(currentPlayer.isHost() )
                                    { 
                                        clientGame = new GameWindow( game , SocketClient.this,  CreateRoomWindow.AS_HOST );
                                        setActiveWindow(clientGame);
                                    }
                                    else 
                                    { 
                                        clientGame = new GameWindow( game, SocketClient.this,  CreateRoomWindow.AS_GUEST );
                                        setActiveWindow(clientGame);
                                    }

                                    clientGame.setVisible(true);
                                    roomWindow.setVisible(false);
                                    mainChat.setVisible(false);
                                } catch (IOException  ex) {
                                        keepRunning = false;
                                        System.out.println("SocketClient.run() invoke GameWindow IOException" + ex);
                                        ex.printStackTrace();
                                }
                            }
                         });
                      }
                      break;
                   case Message.SET_NATION:
                       /*
                       Setting opponent choice of nation
                       */
                       roomWindow.setButtonFromNation(msg.getContentP());
                       break;
                      
                   case Message.COMMAND:
                        Command executeCommand = msg.getCommand();
                
                    /*
                    Executing command over game on socketClient
                    */
                    clientGame.cmdQueue.storeAndExecute(executeCommand);
                    
                  //  clientGame.checkPopUps();
                   
                    /*
                        Show command desc in console in game;
                        */
                    //commandLogger.log(executeCommand);
                    
                   // clientGame.repaint();
                    
                       break;
                  default:
                       System.out.println("manouvre.network.client.SocketClient.run() Unknown msg type" + msg.toString()) ;
              
                 }
    }
    
    @Override
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
    
    public void setActiveWindow(FrameInterface frame)
            
    {
            
        activeWindow = frame ;
//                        if(mainChat != null)
//                        {
//                            if(mainChat.isActive())
//                                activeWindow = mainChat;
//                        } 
//                        if(roomWindow != null)
//                        {
//                             if(roomWindow.isActive())
//                                activeWindow = roomWindow;
//                         }
//                        if(clientGame != null)
//                        {
//                            if(clientGame.isActive())
//                                activeWindow = clientGame;
//                        
//                        }  
                        
    }
    
}
