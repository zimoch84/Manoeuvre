package manouvre.network.server;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.Player;
import manouvre.network.client.Message;

/**
 * 
 * @author Piotr
 * Wątek serwera posiada send(Message msg) 
 */

class ClientServerThread extends Thread { 
	
    public ManouvreServer server = null;
    public Socket clientServerSocket = null;
    public int socketClientServerPortID = -1;
    public String username = "";
    public ObjectInputStream streamIn  =  null;
    public ObjectOutputStream streamOut = null;
    public ServerFrame ui;
    

    public ClientServerThread(ManouvreServer _server, Socket _socket){  
    	super();
        server = _server;
        clientServerSocket = _socket;
        socketClientServerPortID     = clientServerSocket.getPort();
        ui = _server.ui;
    }
    
    public void send(Message msg){
        try {
            streamOut.writeObject(msg);
            streamOut.flush();
        } 
        catch (IOException ex) {
            System.out.println("Exception [SocketClient : send(...)]");
        }
    }
    
    public int getSocketClientServerPortID(){  
	    return socketClientServerPortID;
    }
   
    public Socket getSocket() {
        return clientServerSocket;
    }

       
    @SuppressWarnings("deprecation")
	public void run(){  
    	ui.jTextArea1.append("\nServer Thread " + socketClientServerPortID + " running.");
        while (true){  
    	    try{  
                Message msg;
                msg= (Message) streamIn.readObject();
                
    	    	server.handle(socketClientServerPortID, msg);
            }
            catch(ClassCastException ioe){  
            	System.out.println(socketClientServerPortID + " ERROR reading: " + ioe.getMessage());
                ioe.printStackTrace();
                
               //server.remove(socketClientServerPortID);
               // stop();
            }   catch (IOException ex) {
                    Logger.getLogger(ClientServerThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
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
 * Klasa serwera uruchamia wątki ClientServerThread
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
    private int findClient(int ID){  
    	for (int i = 0; i < clientCount; i++){
        	if (clients[i].getSocketClientServerPortID() == ID){
                    return i;
                }
	}
	return -1;
    }
	
    public synchronized void handle(int ID, Message msg){  
	
        System.out.println("manouvre.network.server.SocketServer.handle()" + msg.toString());
        
        ui.jTextArea1.append("\n"+msg.toString()+"\n" );
        
        
        if (msg.content.equals(".bye")){
            Announce("signout", "SERVER", msg.sender);
            remove(ID); 
	}
	else{
            if(msg.type.equals("login")){
                if(findUserThread(msg.sender) == null){
                    if(db.checkLogin(msg.sender, msg.content)){
                        clients[findClient(ID)].username = msg.sender;
                        clients[findClient(ID)].send(new Message("login", "SERVER", "TRUE", msg.sender));
                        Announce("newuser", "SERVER", msg.sender);
                        SendUserList(msg.sender);
                    }
                    else{
                        clients[findClient(ID)].send(new Message("login", "SERVER", "FALSE", msg.sender));
                    } 
                }
                else{
                    clients[findClient(ID)].send(new Message("login", "SERVER", "FALSE", msg.sender));
                }
            }
            else if(msg.type.equals("message")){
                if(msg.recipient.equals("All")){
                    Announce("message", msg.sender, msg.content);
                }
                else{
                    findUserThread(msg.recipient).send(new Message(msg.type, msg.sender, msg.content, msg.recipient));
                    clients[findClient(ID)].send(new Message(msg.type, msg.sender, msg.content, msg.recipient));
                }
            }
            else if(msg.type.equals("test")){
                clients[findClient(ID)].send(new Message("test", "SERVER", "OK", msg.sender));
            }
            else if(msg.type.equals("create_room")){
                
                String[] parts = msg.content.split("|");
                String name = parts[0];
                String password = parts[1];
                GameRoom newRoom = new GameRoom( name, password, clients[findClient(ID)].clientServerSocket, new Player (msg.sender) );
                this.channels.add(newRoom);
           
                /*
                Wysylamy do klienta ze udalo sie doda kanal
                */
                clients[findClient(ID)].send(new Message("create_channel", "SERVER", "OK", msg.sender));
                
                
                /*
                Wysylamy do wszystkich liste kanalow
                */
                ArrayList<String> channelList = new ArrayList<>();
                for (GameRoom room: getRooms()){
                channelList.add(room.toString());
                }
                
                Message msgOut = new Message("room_list", "inClass", "SERVER", "All");
                msgOut.setChannelList(channelList);
                Announce(msgOut);
                
                
                
                
                
                
            }
            
            else if(msg.type.equals("join_room")){
                
                String[] parts = msg.content.split("|");
                String name = parts[0];
                String password = parts[1];
                
                /*
                Szukaj czy jest taki pokoj i dodajemy usera (socket) 
                */
                
                for(GameRoom checkRoom : getRooms())
                    
                {
                    if(checkRoom.getName().equals(name) && checkRoom.getPassword().equals(password) )
                    {
                        checkRoom.addSocket(this.clients[findClient(ID)].getSocket());
                        
                        checkRoom.addPlayer(new Player(msg.sender));
                                
                    }
                
                }
                
                GameRoom newRoom = new GameRoom( name, password, clients[findClient(ID)].clientServerSocket, new Player (msg.sender) );
                this.channels.add(newRoom);
           
                /*
                Wysylamy do klienta ze udalo sie doda kanal
                */
                clients[findClient(ID)].send(new Message("create_channel", "SERVER", "OK", msg.sender));
                
                
                /*
                Wysylamy do wszystkich liste kanalow
                */
                ArrayList<String> channelList = new ArrayList<>();
                for (GameRoom room: getRooms()){
                channelList.add(room.toString());
                }
                
                Message msgOut = new Message("room_list", "inClass", "SERVER", "All");
                msgOut.setChannelList(channelList);
                Announce(msgOut);
                
                
                
                
                
                
            }
            
            
            else if(msg.type.equals("signup")){
                if(findUserThread(msg.sender) == null){
                    if(!db.userExists(msg.sender)){
                        db.addUser(msg.sender, msg.content);
                        clients[findClient(ID)].username = msg.sender;
                        clients[findClient(ID)].send(new Message("signup", "SERVER", "TRUE", msg.sender));
                        clients[findClient(ID)].send(new Message("login", "SERVER", "TRUE", msg.sender));
                        Announce("newuser", "SERVER", msg.sender);
                        SendUserList(msg.sender);
                    }
                    else{
                        clients[findClient(ID)].send(new Message("signup", "SERVER", "FALSE", msg.sender));
                    }
                }
                else{
                    clients[findClient(ID)].send(new Message("signup", "SERVER", "FALSE", msg.sender));
                }
            }
            else if(msg.type.equals("upload_req")){
                if(msg.recipient.equals("All")){
                    clients[findClient(ID)].send(new Message("message", "SERVER", "Uploading to 'All' forbidden", msg.sender));
                }
                else{
                    findUserThread(msg.recipient).send(new Message("upload_req", msg.sender, msg.content, msg.recipient));
                }
            }
            else if(msg.type.equals("upload_res")){
                if(!msg.content.equals("NO")){
                    String IP = findUserThread(msg.sender).clientServerSocket.getInetAddress().getHostAddress();
                    findUserThread(msg.recipient).send(new Message("upload_res", IP, msg.content, msg.recipient));
                }
                else{
                    findUserThread(msg.recipient).send(new Message("upload_res", msg.sender, msg.content, msg.recipient));
                }
            }
	}
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
     public void createChannel(String username, String password, ClientServerThread thread){
         GameRoom channel = new GameRoom(username, password, thread.getSocket(), new Player(username));
         channels.add(channel);
        
     }
     public boolean destroyChannel(GameRoom channel)
     {
         boolean remove= false;
         if(channels.contains(channel)) 
         {
              remove = channels.remove(channel);
         }
         return remove;
     }
     
    public static String getStackTrace(final Throwable throwable) {
     final StringWriter sw = new StringWriter();
     final PrintWriter pw = new PrintWriter(sw, true);
     throwable.printStackTrace(pw);
     return sw.getBuffer().toString();
}
}
