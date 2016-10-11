package manouvre.network.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.game.interfaces.CardInterface;
import manouvre.network.client.Message;

/**
 * 
 * @author Piotr
 * Wątek serwera posiada send(Message msg) i trzyma socket klienta 
 * i odpowiedzialna jest za komunikacje 1 klienta z serwerem
 */

class SimpleClientServerThread extends Thread { 
	
    public SimpleServer server = null;
    public Socket clientServerSocket = null;
    public int socketClientServerPortID = -1;
    public String username = "";
    public ObjectInputStream streamIn  =  null;
    public ObjectOutputStream streamOut = null;
    
    
    Player player;
    

    public SimpleClientServerThread(SimpleServer _server, Socket _socket){  
    	super();
        server = _server;
        clientServerSocket = _socket;
        socketClientServerPortID     = clientServerSocket.getPort();

    }
    
    /**
     *  Message( type,  sender,  content,  recipient)
    
    */
    public void send(Message msg){
        try {
            streamOut.writeObject(msg);
            streamOut.flush();
            System.out.println("send() "  + msg.toString() + " " +  "PortID: " + socketClientServerPortID);
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
    	System.out.println("\nServer Thread " + socketClientServerPortID + " running.");
        while (true){  
    	    try{  
                Message msg;
                msg= (Message) streamIn.readObject();
                
    	    	server.handle(socketClientServerPortID, msg);
            }
            catch(ClassCastException ioe){  
            	System.out.println(socketClientServerPortID + " ERROR reading: " + ioe.getMessage());
                ioe.printStackTrace();
                
               //
               // stop();
            }
            catch (SocketException ex){
                System.out.println("manouvre.network.SimpleClientServerThread.run()");
                
                server.destroyRoom( server.getRoomByPort(socketClientServerPortID)  );
                server.remove(socketClientServerPortID);
              
                
                
            }
            catch (IOException ex) {
                    System.out.println("manouvre.network.SimpleSimpleClientServerThread.run()" + ex);
                } 
            catch (ClassNotFoundException ex) {
                    System.out.println("manouvre.network.SimpleSimpleClientServerThread.run()" + ex);
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
 * Klasa serwera uruchamia i trzyma wątki SimpleClientServerThread i trzyma kanały
 * @author Piotr
 */


public class SimpleServer implements Runnable {
    /**
     * SimpleClientServerThread clients[];
    
    */
    public SimpleClientServerThread clients[];
    public ArrayList<GameRoom> channels;
    
    public ServerSocket serverSocket = null;
    public Thread       serverThread = null;
    public int clientCount = 0, port = 5002;
   

   public SimpleServer(ServerFrame frame){
       
        clients = new SimpleClientServerThread[50];
        
	try{  
            /*
            Create server clientServerSocket
            */
            channels = new ArrayList<>();
	    serverSocket = new ServerSocket(port);
            port = serverSocket.getLocalPort();
	   
            /*
            Invoke run();
            */
            start(); 
  
        }
	catch(IOException ioe){  
                  System.out.println("manouvre.network.SimpleServer.<init>()" + ioe); 
	}
    }
    
    
    public void run(){  
        /*
        Jezeli watek serwera istnieje 
        */
	while (serverThread != null){  
            try{  
                
             System.out.println("\nWaiting for a client ..."); 
                /*
                Funkcja servera accept zwraca clientServerSocket klienta;
                addThead dodaje do tablicy socketow 
                */
	        addThread(serverSocket.accept()); 
	    }
	    catch(Exception ioe){ 
                System.out.println("\nServer accept error: \n"); 
	    }

        } 
    }
	
    public void start(){  
     	if (serverThread == null){  
            serverThread = new Thread(this); 
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
    
      
    public SimpleClientServerThread findUserThread(String usr){
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
            SimpleClientServerThread toTerminate = clients[pos];
            System.err.println("\nRemoving client thread " + ID + " at " + pos);
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
	      	System.out.println("\nError closing thread: " + ioe); 
	    }
	    toTerminate.stop(); 
	}
    }
    /**
     *  @param  Socket - client clientServerSocket
 
 Tworzy na podstawie Socketa klienta obiekt SimpleClientServerThread i Dodaje do tablicy clients trzymających wątki klientów
       
    */
    private void addThread(Socket incomingClientSocket){  
	if (clientCount < clients.length){  
            System.out.println("\nClient accepted: " + incomingClientSocket);
            /*
            Tworzymy nowy obsługujący komunikacje Client <-> Server
            Obiekt posiada Socket klienta i Socket serwera
            */
	    clients[clientCount] = new SimpleClientServerThread(this, incomingClientSocket);
            /*
            Odpalamy nowy wątek dla klienta ,który będize obsługiwał komunikacje
            */
	    try{  
	      	clients[clientCount].open(); 
	        clients[clientCount].start();  
	        clientCount++; 
	    }
	    catch(IOException ioe){  
	      	System.out.println("\nError opening thread: " + ioe); 
	    } 
	}
	else{
            System.out.println("\nClient refused: maximum " + clients.length + " reached.");
	}
    }
     public void createRoom(String chuannelName, String password, SimpleClientServerThread thread){
        
         
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
     
  public synchronized void handle(int ID, Message msg) {
        System.out.println("manouvre.network.SocketServer.handle()" + msg.toString());
        System.out.println("\n" + msg.toString() + "\n");
        Message msgOut;
        GameRoom gameRoom;
        try {
            switch (msg.getMessageType()) {
                case Message.START_GAME:
                    /*
                    Searching for host Room
                     */
                    gameRoom = getRoomByPort(ID);
                    /*
                    Only Host can start game.
                    Message carry on info about players and their choices about army.
                     */
                    msgOut = new Message(Message.START_GAME, "SERVER", Message.OK, "ROOM: " + gameRoom.getName());
                    /*
                    Creating a game
                     */
                    ArrayList<Player> players = gameRoom.getPlayers();
                    //Assiging players
                    //ArrayList<Player> players = msg.getPlayers();
                    //Creating game - generate map, deal cards - setup army etc.
                    Game game = new Game(players);
                    //gameRoom.setGame(game);
                    //Setting msg to carry whole game
                    System.out.println("manouvre.network.ServerMessageHandler.handle() " + game.toString()); 
                    msgOut.setGame(game);
                    players.get(0).setArmy(game.hostPlayer.getArmy());
                    players.get(1).setArmy(game.guestPlayer.getArmy());
                    
                    msgOut.hostPlayer = players.get(0);
                    msgOut.guestPlayer = players.get(1);
                      
                    //Sending response to Host;
                    //announceInRoom(gameRoom, msgOut);
                    clients[findClient(gameRoom.getHostSocketPortId())].send(msgOut);
                    System.out.println("manouvre.network.ServerMessageHandler.handle() " + game.toString()); 
                    clients[findClient(gameRoom.getGuestSocketPortId())].send(msgOut);
                    System.out.println("manouvre.network.ServerMessageHandler.handle() " + game.toString()); 
                    break;
                case Message.SET_NATION:
                    /*
                    Searching for host Room
                     */
                    gameRoom = getRoomByPort(ID);
                    
                    /*
                    Setting Player Nation
                    */
                    gameRoom.getCurrentPlayer(ID).setNation(msg.getContentP());
                    /*
                    Sending message to opponent if room is full
                    */
                    if(gameRoom.isLocked())       
                    {
                        msgOut = new Message(Message.SET_NATION, "SERVER", msg.getContentP(), "ROOM: " + gameRoom.getName());
                        clients[findClient(gameRoom.getOpponentPortSocket(ID))].send(msgOut);
                    }
                    
                    break;
                default:
                    System.out.println("manouvre.network.ManouvreServer.handle() No type handled" + msg.getType());
            }
        } catch (NullPointerException ex) {
            System.out.println("manouvre.network.ManouvreServer.handle()" + ex.toString());
        }
        
    }   

}
