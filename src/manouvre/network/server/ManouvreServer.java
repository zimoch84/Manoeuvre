package manouvre.network.server;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.network.client.Message;
import manouvre.network.client.SocketClient;

/**
 * 
 * @author Piotr
 * Wątek serwera posiada send(Message msg) i trzyma socket klienta 
 * i odpowiedzialna jest za komunikacje 1 klienta z serwerem
 */

class ClientServerThread extends Thread { 
	
    public ManouvreServer server = null;
    public Socket clientServerSocket = null;
    public int socketClientServerPortID = -1;
    public String username = "";
    public ObjectInputStream streamIn  =  null;
    public ObjectOutputStream streamOut = null;
    public ServerFrame ui;
    
    

    
    
    Player player;
    

    public ClientServerThread(ManouvreServer _server, Socket _socket){  
    	super();
        server = _server;
        clientServerSocket = _socket;
        socketClientServerPortID     = clientServerSocket.getPort();
        ui = _server.ui;
    }
    
    /**
     *  Message( type,  sender,  content,  recipient)
    
    */
    public void send(Message msg){
        try {
            streamOut.writeObject(msg);
            streamOut.flush();
            System.out.println("manouvre.network.server.ClientServerThread.send() "  + msg.toString());
        } 
        catch (IOException ex) {
            System.out.println("Exception [SocketClient : send(...)]" + ex.toString());
        }
    }
    
    public int getSocketClientServerPortID(){  
	    return socketClientServerPortID;
    }
   
    public Socket getSocket() {
        return clientServerSocket;
    }

       
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
    
    @SuppressWarnings("deprecation")
	public void run(){  
    	ui.jTextArea1.append("\nServer Thread " + socketClientServerPortID + " running.");
        while (true){  
    	    try{  
                Message msg;
                msg= (Message) streamIn.readObject();
                
    	    	server.handler.handle(socketClientServerPortID, msg);
            }
            catch(ClassCastException ioe){  
            	System.out.println(socketClientServerPortID + " ERROR reading: " + ioe.getMessage());
                ioe.printStackTrace();
                
               //
               // stop();
            }
            catch (SocketException ex){
                System.out.println("manouvre.network.server.ClientServerThread.run()");
                
                server.destroyRoom( server.findGameRoom(socketClientServerPortID)  );
                server.remove(socketClientServerPortID);
              
                
                
            }
            catch (IOException ex) {
                    Logger.getLogger(ClientServerThread.class.getName()).log(Level.SEVERE, null, ex);
                } 
            catch (ClassNotFoundException ex) {
                    Logger.getLogger(ClientServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            
        }
    }
    /**
     * Otwiera komunikacje w sockecie
     * @throws IOException 
     */
    public void open() throws IOException {  
        streamOut = new ObjectOutputStream(clientServerSocket.getOutputStream());
        streamOut.flush();
        streamIn = new ObjectInputStream(clientServerSocket.getInputStream());
    }
    /**
     * Zamyka streamy w sockecie 
     * @throws IOException 
     */
    public void close() throws IOException {  
    	if (clientServerSocket != null)    clientServerSocket.close();
        if (streamIn != null)  streamIn.close();
        if (streamOut != null) streamOut.close();
    }
}


/**
 * Klasa serwera uruchamia i trzyma wątki ClientServerThread i trzyma kanały
 * @author Piotr
 */


public class ManouvreServer implements Runnable {
    /**
     * ClientServerThread clients[];
    
    */
    public ClientServerThread clients[];
    public ArrayList<GameRoom> channels;
    
    public ServerSocket server = null;
    public Thread       serverThread = null;
    public int clientCount = 0, port = 5002;
    public ServerFrame ui;
    public Database db;
    
    MessageHandler handler;
    
    

    public ManouvreServer(ServerFrame frame){
       
        clients = new ClientServerThread[50];
        ui = frame;
        db = new Database(ui.filePath);
        
        
        
	try{  
            /*
            Create server clientServerSocket
            */
            channels = new ArrayList<>();
	    server = new ServerSocket(port);
            port = server.getLocalPort();
	    ui.jTextArea1.append("Server startet. IP : " + InetAddress.getLocalHost() + ", Port : " + server.getLocalPort());
            /*
            Invoke run();
            */
            start(); 
            
            handler = new MessageHandler(this);
        }
	catch(IOException ioe){  
            ui.jTextArea1.append("Can not bind to port : " + port + "\nRetrying"); 
            ui.RetryStart();
	}
    }
    
    
    public void run(){  
        /*
        Jezeli watek serwera istnieje 
        */
	while (serverThread != null){  
            try{  
		ui.jTextArea1.append("\nWaiting for a client ..."); 
                /*
                Funkcja servera accept zwraca clientServerSocket klienta;
                addThead dodaje do tablicy socketow 
                */
	        addThread(server.accept()); 
	    }
	    catch(Exception ioe){ 
                ui.jTextArea1.append("\nServer accept error: \n");
                ui.RetryStart();
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
  
    public void Announce(String type, String sender, String content){
        Message msg = new Message(type, sender, content, "All");
        for(int i = 0; i < clientCount; i++){
            clients[i].send(msg);
        }
    }
    
    public void Announce(Message inMessage){
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
    
    public void SendUserList(String toWhom){
        for(int i = 0; i < clientCount; i++){
            findUserThread(toWhom).send(new Message("newuser", "SERVER", clients[i].username, toWhom));
        }
    }
    
      
    public ClientServerThread findUserThread(String usr){
        for(int i = 0; i < clientCount; i++){
            if(clients[i].username.equals(usr)){
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
            ui.jTextArea1.append("\nRemoving client thread " + ID + " at " + pos);
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
	      	ui.jTextArea1.append("\nError closing thread: " + ioe); 
	    }
	    toTerminate.stop(); 
	}
    }
    /**
     *  @param  Socket - client clientServerSocket
 
 Tworzy na podstawie Socketa klienta obiekt ClientServerThread i Dodaje do tablicy clients trzymających wątki klientów
       
    */
    private void addThread(Socket incomingClientSocket){  
	if (clientCount < clients.length){  
            ui.jTextArea1.append("\nClient accepted: " + incomingClientSocket);
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
	      	ui.jTextArea1.append("\nError opening thread: " + ioe); 
	    } 
	}
	else{
            ui.jTextArea1.append("\nClient refused: maximum " + clients.length + " reached.");
	}
    }
     public void createRoom(String chuannelName, String password, ClientServerThread thread){
        
         
         GameRoom channel = new GameRoom(chuannelName, password, thread.getSocket().getPort(), thread.getPlayer());
         
         
         channels.add(channel);
        
     }
     public boolean destroyRoom(GameRoom channel)
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
         }
         return remove;
     }
     
     
     public GameRoom findGameRoom(int socketPort){
     
     for(GameRoom room : getRooms())
         {
             if (room.getHostSocketPortId() == socketPort || room.getGuestSocketPortId() == socketPort )
             {
                 return room;
              }
         }
     return null;
     
     }
     
     
    public boolean isGameRoom(GameRoom ingameroom){
     
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
}
