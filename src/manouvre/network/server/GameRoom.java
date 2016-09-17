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
    
    ArrayList<Player> players;

    
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
    
    int hostSocketPortId, questSocketPortId;

    public void setPassword(String password) {
        this.password = password;
    }
    
    int ID;
    
    
    boolean locked;

    
 
    
    public GameRoom(String name, String password, int hostSocketPortId, Player player) {
        this.name = name;
        this.password = password;
        
        players = new ArrayList<>();
        
        this.hostSocketPortId = hostSocketPortId;
        players.add(player);
        
    }
     
    public void addPlayer(Player inPlayer){
        
        if(!locked){
        this.players.add(inPlayer);
        if (players.size() == 2) locked = true;
        }
               
    }
    
    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
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
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
    
    

}
