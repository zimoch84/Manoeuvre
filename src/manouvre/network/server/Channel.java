/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server;

import java.net.Socket;
import java.util.ArrayList;
import manouvre.game.Player;
import manouvre.network.client.SocketClient;

/**
 *
 * @author Piotr
 */
public class Channel {
    
    ArrayList<Socket> sockets;
    ArrayList<Player> players;
    
    String name, password;
    
    int ID;
    
    
    boolean locked;

    public Channel(String name, String password, Socket socket, Player player) {
        this.name = name;
        this.password = password;
        sockets = new ArrayList<>();
        players = new ArrayList<>();
        players.add(player);
        sockets.add(socket);
        
    }
    
    public void addSocket(Socket inSocket){
        
        if(!locked){
        this.sockets.add(inSocket);
        if (sockets.size() == 2) locked = true;
        }
        
        //players.add(inSocket.welcome.getPlayer());
        
    }
    
    @Override
    public boolean equals(Object o){
     Channel c = (Channel)    o;
   
     return this.name == c.name;
    }
    
    
    
    
    
    
    
}
