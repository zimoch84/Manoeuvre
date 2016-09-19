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

    
    
    Player hostPlayer;
    Player guestPlayer;
    
    
    String name, password;
    

    Game game;

   /*
        Port nasluchujacy serwera =  Port socketa klienta 
        Port wynegocjowany z serwerem to localPort socketa klienta
        
        czyli client.socket.Port = server.liteningsocket.port = server.socket.localport
        
        client.socket.Localport = server.socket.port
                
        */
    /**
     * hostSocketPortId = server.host.socket.localPort = host.socket.port
     * questSocketPortId = = server.quest.socket.localPort = quest.socket.port
    
    */
    
    int hostSocketPortId,

    /**
     * hostSocketPortId = server.host.socket.localPort = host.socket.port
 guestSocketPortId = = server.quest.socket.localPort = quest.socket.port
     */
    guestSocketPortId;

    public void setPassword(String password) {
        this.password = password;
    }
    
    int ID;
    
    
    boolean locked;

    
 
    
    public GameRoom(String name, String password, int hostSocketPortId, Player hostPlayer) {
        this.name = name;
        this.password = password;
      
               
        this.hostSocketPortId = hostSocketPortId;
        this.hostPlayer = hostPlayer;
        setLocked(false);
        
    }
     
    public void addPlayer(Player guestPlayer){
        
        
        if(!locked){
            this.guestPlayer= guestPlayer;
            setLocked(true);
            
        }
               
    }
    
    public ArrayList<Player> getPlayers() {
        
        ArrayList<Player> players = new ArrayList<>();
        
        players.add(hostPlayer);
        players.add(guestPlayer);
        
        return players;
        
    }

    public void setPlayers(ArrayList<Player> players) {
        
        if(players.size() == 2)
        {hostPlayer = players.get(0);
        guestPlayer = players.get(1);
        }
        
    }
    
    @Override
    public boolean equals(Object o){
     GameRoom c = (GameRoom)    o;
   
     return this.name == c.name;
        
     
    }
    
    @Override
    public String toString(){
    
        String out = "";
            
        out =   name + " hosted by : "  + getHostPlayer().getName() + " guest : " +
                (guestPlayer != null ? getGuestPlayer().getName() : "noone")
               ;
        return out;   
                       
//            if (password == null || password.equals("") )        
//              ;
//            else 
//             return out  +   " password protected";
    
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

    public int getGuestSocketPortId() {
        return guestSocketPortId;
    }

    public void setGuestSocketPortId(int guestSocketPortId) {
        this.guestSocketPortId = guestSocketPortId;
    }
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    
    public Player getHostPlayer() {
        return hostPlayer;
    }

    public void setHostPlayer(Player hostPlayer) {
        this.hostPlayer = hostPlayer;
    }

    public Player getGuestPlayer() {
       
            return guestPlayer;
        
    }

    public void setGuestPlayer(Player guestPlayer) {
        this.guestPlayer = guestPlayer;
    }

}
