/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import manouvre.game.Player;
import manouvre.network.core.Message;
import manouvre.network.core.User;
import org.apache.logging.log4j.LogManager;


/**
 * Klasa serwera uruchamia i trzyma wątki ClientServerThread i trzyma kanały
 * @author Piotr
 */
public class ClientServerThread extends Thread  { 
	
    public GameServer server = null;
    public Socket clientServerSocket = null;
    public int socketClientPortID = -1;
    public ObjectInputStream streamIn  =  null;
    public ObjectOutputStream streamOut = null;

    User user;
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ClientServerThread.class.getName()); 

    public ClientServerThread(GameServer server, Socket clientServerSocket){  
    	super();
        this.server = server;
        this.clientServerSocket = clientServerSocket ;
        socketClientPortID     = clientServerSocket.getPort();
    }

    public void send(Message msg){
        try {
            streamOut.writeObject(msg);
            streamOut.flush();
            LOGGER.info(new Date()   + ": send to " + getSocket().getInetAddress().toString() + ":" + getSocket().getPort() );
        } 
        catch (IOException ex) {
            LOGGER.error(new Date()   + ":" +   ex.getMessage());
        }
    }
    
    public int getSocketClientServerPortID(){  
	    return socketClientPortID;
    }
   
    public Socket getSocket() {
        return clientServerSocket;
    }

       
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    @SuppressWarnings("deprecation")
	public void run(){  
            
        server.notifyAbout("INFO", "Client Server Thread on port: " + socketClientPortID + " is  running");
        while (true){  
    	    try{  
                Message msg;
                msg= (Message) streamIn.readObject();
    	    	server.handler.handle(socketClientPortID, msg);
            }
            catch(ClassCastException ioe){  
                server.notifyAbout("ERROR"," ERROR reading: " + server.getStackTrace(ioe));
            }
            catch (SocketException ex){
                server.notifyAbout("ERROR", " ERROR reading: " + server.getStackTrace(ex));
                //server.destroyRoom( server.getRoomByPort(socketClientServerPortID)  );
                //server.remove(socketClientServerPortID);
            }
            catch (IOException ex) {
                 server.notifyAbout("ERROR", " ERROR reading: " + server.getStackTrace(ex));
                } 
            catch (ClassNotFoundException ex) {
                 server.notifyAbout("ERROR", " ERROR reading: " + server.getStackTrace(ex));
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

