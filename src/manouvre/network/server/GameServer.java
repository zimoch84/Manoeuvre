package manouvre.network.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Observable;
import manouvre.interfaces.Command;
import manouvre.network.core.Message;
import org.apache.logging.log4j.LogManager;
import manouvre.network.core.MessageFactory;
import manouvre.network.core.User;
/**
 * 
 * @author Piotr
 * Wątek serwera posiada send(Message msg) i trzyma socket klienta 
 * i odpowiedzialna jest za komunikacje 1 klienta z serwerem
 */



public class GameServer extends Observable implements Runnable{
    /**
     * ClientServerThread clients[];
    */
    public ClientServerThread clients[];
    public ArrayList<GameRoom> channels;
    
    private  ServerSocket serverListeningSocket = null;
    public Thread       serverThread = null;
    public int clientCount = 0, port = 5002;
    public ServerFrame ui;
    public AuthorizitionControl userDatabase;
    public ServerLogger logger;
    
    ServerMessageHandler handler;
    
    private MessageFactory mf;
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(GameServer.class.getName());  

    public GameServer(ServerFrame frame){
       
        clients = new ClientServerThread[50];
        ui = frame;
        userDatabase = new FileDatabase(ui.filePath);
        logger = new ServerLogger(ui.loggerTextArea);
        this.mf = manouvre.network.core.MessageFactory.getInstance();
        addObserver(logger);
        addObserver(ui);
        
	try{  
            /*
            Create server clientServerSocket
            */
            channels = new ArrayList<>();
	    serverListeningSocket = new ServerSocket(port);
            port = serverListeningSocket.getLocalPort();
            
            notifyAbout("INFO", "Server startet. IP : " + InetAddress.getLocalHost() + ", Port : " + serverListeningSocket.getLocalPort());

            start(); 
            
            handler = new ServerMessageHandler(this);
        }
	catch(IOException ioe){  
            notifyAbout("ERROR", "Can not bind to port : " + port); 
	}
    }
    
    public MessageFactory getMessageFactory(){
        return mf;
    
    }
    
    public void run(){  
        /*
        Jezeli watek serwera istnieje 
        */
	while (serverThread != null){  
            try{  
		notifyAbout("INFO", "Waiting for a client ..."); 
                /*
                Funkcja servera accept zwraca clientServerSocket klienta;
                addThead dodaje do tablicy socketow 
                */
                Socket connectionSocket =  serverListeningSocket.accept();
	        addThread(connectionSocket); 
	    }
	    catch(Exception ioe){ 
                notifyAbout("ERROR","Server accept error: " + getStackTrace(ioe));
	    }

        } 
    }
	
    public void start(){  
        /*
        Klasa runnable nie odpala automatycznie run trzeba stworzyc watek zeby odpalił run() klasy Runnable
        */
        
    	if (serverThread == null){  
            serverThread = new Thread(this); 
            /*
            Odpala ManouvreServer.run(); i tworzy wątek
            */
	    serverThread.start();
	}
    }
    
    @SuppressWarnings("deprecation")
    public void stop(){  
        if (serverThread != null){  
            serverThread.stop(); 
	    serverThread = null;
	}
    }
    /**
     * 
     * @param ID
     * @return clientServerSocketPortid to find thread
     */
    protected int findClient(int ID){  
    	for (int i = 0; i < clientCount; i++){
        	if (clients[i].getSocketClientServerPortID() == ID){
                    return i;
                }
	}
	return -1;
    }
  
    public void announce(String type, String sender, String content){
        Message msg = new Message(type, sender, content, "All");
        for(int i = 0; i < clientCount; i++){
            clients[i].send(msg);
        }
    }
    
    public void announce(Message inMessage){
       for(int i = 0; i < clientCount; i++){
            clients[i].send(inMessage);
        }
    }
    
    public void announceInRoom(GameRoom room , Message msg){
    
        if (channels.contains(room)) 
        {
           clients[findClient(room.getHostSocketPortId())].send(msg);
            if(room.guestSocketPortId > 0)
                  clients[findClient(room.getGuestSocketPortId())].send(msg);
           
        }
            
    }
    
    public GameRoom getRoomByPort(int socketPort){
     
     for(GameRoom room : getRooms())
         {
             if (room.getHostSocketPortId() == socketPort || room.getGuestSocketPortId() == socketPort )
             {
                 return room;
              }
         }
     return null;
     
     }
     
     public boolean isUserInGameRoom(User user){
        return false;
         
     }
    
    public void sendUserList(User toWhom){
        for(int i = 0; i < clientCount; i++){
           findUserThread(toWhom).send
                (new Message(Message.Type.RESPONSE, "SERVER", Message.Target.PRIVATE, null));
        }
    }
    
      
    public ClientServerThread findUserThread(User usr){
        for(int i = 0; i < clientCount; i++){
            if(clients[i].getUser().equals(usr)){
                return clients[i];
            }
        }
        return null;
    }
    
    public ArrayList<GameRoom> getRooms(){
    return channels;
    }
	
    @SuppressWarnings("deprecation")
    public synchronized void remove(int ID){  
    int pos = findClient(ID);
        if (pos >= 0){  
            ClientServerThread toTerminate = clients[pos];
            notifyAbout("EROR","Removing client thread " + ID + " at " + pos);
	    
            if (pos < clientCount-1){
                for (int i = pos+1; i < clientCount; i++){
                    clients[i-1] = clients[i];
	        }
	    }
	    clientCount--;
	    try{  
	      	toTerminate.close(); 
	    }
	    catch(IOException ioe){  
	      	notifyAbout("EROR","Error closing thread: " + ioe); 
	    }
	    toTerminate.stop(); 
	}
    }
    /**
     *  @param  Socket - client clientServerSocket
 
    Tworzy na podstawie Socketa klienta obiekt ClientServerThread i Dodaje do tablicy clients trzymających wątki klientów
       
    */
    private synchronized void addThread(Socket incomingClientSocket){  
	if (clientCount < clients.length){  
            
            notifyAbout("INFO","Client accepted: " + incomingClientSocket);
            /*
            Tworzymy nowy obsługujący komunikacje Client <-> Server
            Obiekt posiada Socket klienta i Socket serwera
            */
	    clients[clientCount] = new ClientServerThread(this, incomingClientSocket);
            /*
            Odpalamy nowy wątek dla klienta ,który będize obsługiwał komunikacje
            */
	    try{  
	      	clients[clientCount].open(); 
	        clients[clientCount].start();  
	        clientCount++; 
	    }
	    catch(IOException ioe){  
	      	notifyAbout("ERROR","Error opening thread: " + ioe); 
	    } 
	}
	else{
            notifyAbout("INFO","Client refused: maximum " + clients.length + " reached.");
	}
    }
     public synchronized void createRoom(String channelName, String password, ClientServerThread thread){
        
         GameRoom channel = new GameRoom(channelName, password, thread.getSocket().getPort(), thread.getUser());
         channels.add(channel);
         
         notifyAbout("INFO", "channel created " + channel.toString());
        
     }
     public synchronized boolean destroyRoom(GameRoom channel)
     {
         
         boolean remove= false;
         for(GameRoom room : getRooms())
         {
             if (room.getHostSocketPortId() == channel.getHostSocketPortId())
             {
                 /*
                    TODO 
                 send to all GAME_ROOM_DESTROYED
                     */                 
                 remove = channels.remove(channel);
                 break;
             }
         }
         if(channels.contains(channel)) 
         {
              remove = channels.remove(channel);
              notifyAbout("INFO", "channel destroyed " + channel.toString());
              
         }
         return remove;
     }
     
     
  
     
    public boolean isRoomExistsOnServer(GameRoom ingameroom){
     
     for(GameRoom room : getRooms())
         {
             if (room.getHostSocketPortId() == ingameroom.getHostSocketPortId())
             {
                 return true;
              }
         }
     return false;
     
     }
     
     
     public static String getStackTrace(final Throwable throwable) {
     final StringWriter sw = new StringWriter();
     final PrintWriter pw = new PrintWriter(sw, true);
     throwable.printStackTrace(pw);
     return sw.getBuffer().toString();
    }       

    public void notifyAbout(String logLevel, String text) {
        
        setChanged();
        notifyObservers(logLevel + " "+ text );
        super.notifyObservers(); 
    }

   public boolean authorize (User user, String password){
        return userDatabase.authorize(user, password);
   }
    
    
}
