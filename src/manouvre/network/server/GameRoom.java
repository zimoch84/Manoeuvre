/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server;

import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.network.client.SocketClient;

/**
 *
 * @author Piotr
 */
public class GameRoom implements Serializable {
    
    transient ArrayList<Socket> sockets;

    ArrayList<Player> players;
    
    String name, password;

    Game game;

    
    
    int hostSocketPortId, questSocketPortId;

    public void setPassword(String password) {
        this.password = password;
    }
    
    int ID;
    
    
    boolean locked;

    

    public GameRoom(String name, String password, Socket socket, Player player) {
        this.name = name;
        this.password = password;
        sockets = new ArrayList<>();
        players = new ArrayList<>();
        
        hostSocketPortId = socket.getPort();
        
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
    public ArrayList<Socket> getSockets() {
        return sockets;
    }
    
    public void addPlayer(Player inPlayer){
        
        if(!locked){
        this.players.add(inPlayer);
        if (players.size() == 2) locked = true;
        }
        
        //players.add(inSocket.welcome.getPlayer());
        
    }
    
    @Override
    public boolean equals(Object o){
     GameRoom c = (GameRoom)    o;
   
     return this.name == c.name;
    }
    
    @Override
    public String toString(){
    
        String out = "";
        if(players.size() > 0 )
            
            out = out +  name + " hosted by : " ; 
            for (Player player: players)
            out = out + player.getName() + " ";
            
            if (password == null || password.equals("") )        
             return out ;
            else 
             return out  +   " password protected";
    
    }
    
     public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }
   
    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
 
    
    public int getHostSocketPortId() {
        return hostSocketPortId;
    }

    public void setHostSocketPortId(int hostSocketPortId) {
        this.hostSocketPortId = hostSocketPortId;
    }

    public int getQuestSocketPortId() {
        return questSocketPortId;
    }

    public void setQuestSocketPortId(int questSocketPortId) {
        this.questSocketPortId = questSocketPortId;
    }
    
    
    
    
    
    
}
