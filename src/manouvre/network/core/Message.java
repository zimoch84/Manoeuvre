package manouvre.network.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.network.server.GameRoom;
import manouvre.interfaces.Command;
/**
 * 
 * @author Piotr
 */
public class Message implements Serializable{
    
    public enum Type{REQUEST,RESPONSE};
    
    public enum Result{OK, NOT_OK};
    
    public enum Target{INROOM, ALL, PRIVATE, SEVER};
    
    Type messageType2;
    Result response;
    Target target;
    
    UUID id;
    
    public final static int NOT_OK = 0;
    public final static int OK = 1;
    public final static int BAD_CHANNEL_NAME = 2;
    public final static int BAD_PASSWORD = 3;
    public final static int IS_ROOM_LOCKED = 4;
    public final static int ROOM_NOT_FOUND = 5; 
    public final static int USER_JOINED_IN_ROOM = 6; 
       
    /*
    Message types
    */    

    public final static int CREATE_ROOM = 10;
    public final static int JOIN_ROOM = 11;
    public final static int IN_ROOM_CHAT = 12;
    public final static int GET_ROOM_LIST = 13;
    public final static int LOGIN = 14;
    public final static int BYE = 15;
    public final static int SIGNOUT = 16;
    public final static int USER_LOGGED = 17;
    public final static int CHAT_IN_ROOM = 18; 
    public final static int START_GAME = 19; 
    public final static int SET_NATION = 20;
    public final static int COMMAND = 21;
    public final static int CHAT = 99;
    public final static int RECONNECT = 99;
  
    private static final long serialVersionUID = 1L;
    /**
     * type login, .bye, message, signup, create_room, room_list
     */
    public String type, sender, content, recipient;
    
    public User senderUser;
      
    public int messageType, contentP;

    ArrayList<Player> players;

    Game game;
  
    ArrayList<GameRoom> channelList; 
    
    public Player hostPlayer, guestPlayer;
    
    public Command command;

    public Message(Type type, String senderName, Result response)   {
          this.sender = senderName; 
          this.messageType2 = type;    
          this.response = response;
    }
    
    
    public Message(Type type, String from, Target to, Command content )   {
          this.sender = from; 
          this.target = to;
          this.messageType2 = type;    
          this.command = content;
    }
    
    public Message(Type type, User from, Target to, Command content )   {
      this.senderUser = from; 
      this.target = to;
      this.messageType2 = type;    
      this.command = content;
    }
    
     public Message(Type type, User from, Target to)   {
      this.senderUser = from; 
      this.target = to;
      this.messageType2 = type;    
    }
    
    
    
    public Message(String type, String sender, String content, String recipient){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
        channelList = new ArrayList<GameRoom> ();
         players = new ArrayList<>();
    }
    
    public Message(int type, String sender, int content, String recipient){
        this.messageType = type; this.sender = sender; this.contentP = content; this.recipient = recipient;
        channelList = new ArrayList<GameRoom> ();
        players = new ArrayList<>();
    }
    
    public Message(int type, String sender, String content, String recipient){
        channelList = new ArrayList<GameRoom> ();
        players = new ArrayList<>();
        this.messageType = type; this.sender = sender; this.content = content; this.recipient = recipient;
    }
    
    @Override
    public String toString(){
        return "{type='"+getType()+"', sender='"+sender+"', content='"+getContent()+"', recipient='"+recipient
                +   (game != null ? "Game:"+game.toString() : "")
                +"'}"
                ;
    }
    
    boolean isType(int messageType)
    {
        if(this.messageType == messageType)
            return true;
        else return false;
    }
    
    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

     public String getContent() {
        if(content == null) 
        {
            String out = null;
        
        
            switch(getContentP())
                {
                 case OK : out = "OK"; break;
                 case NOT_OK : out  = "NOT_OK"; break;
                 case BAD_CHANNEL_NAME : out  = "BAD_CHANNEL_NAME";break;
                 case BAD_PASSWORD : out = "BAD_PASSWORD" ;break;
                 case IS_ROOM_LOCKED: out = "IS_ROOM_LOCKED"; break;
                 
                 default : Integer.toString(getContentP());
                }            

         return out;
        }
        else  return content;
    }

    public String getRecipient() {
        return recipient;
    }

    public Target getTarget(){
        return target;
    }
    public User getSender() {
        return senderUser;
    }    
    public int getContentP() {
        return contentP;
    }

    public void setContentP(int contentP) {
        this.contentP = contentP;
    }
    
     public String getType() {
         if (type==null)
         {
             String out;
            
             
             switch (getMessageType())
             {
                    case CREATE_ROOM: out = "CREATE_ROOM"; break;
                    case JOIN_ROOM : out  = "JOIN_ROOM"; break;
                    case IN_ROOM_CHAT : out  = "IN_ROOM_CHAT";break;
                    case GET_ROOM_LIST : out  = "GET_ROOM_LIST";break;
                    case LOGIN : out  = "LOGIN";break;
                    case BYE : out  = "BYE" ;break;
                    case SIGNOUT : out  = "SIGNOUT";break;
                    case USER_LOGGED : out  = "USER_LOGGED" ;break;
                    case CHAT : out  = "CHAT"; break;
                    case CHAT_IN_ROOM : out  = "CHAT_IN_ROOM"; break;
                    case START_GAME : out  = "START_GAME"; break;
                    case SET_NATION : out  = "SET_NATION"; break;
                    case COMMAND: out  = "COMMAND "  + getCommand().getType()  ; break;
                    
                    
                    default: out = Integer.toString(getMessageType()) ;
                    
             }
          return out;   
         }
         else 
        return type;
    }
     
    public void setCommand(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }
      
    
   
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void addGameRoom(GameRoom gameRoom)
    {
        channelList.add(gameRoom);
    }
    
    public ArrayList<GameRoom> getChannelList() {
        return channelList;
    }

    public void setChannelList(ArrayList<GameRoom> channelList) {
        this.channelList = channelList;
    }
    
      public void setContent(String content) {
        this.content = content;
    }
    
    public Game getGame() {
        return game;
    }

    public void addPlayer(Player player)
    {
        players.add(player);
    }
    
    public Player getPlayer()
    {
        return  players.get(0);
    }
    
    
    public ArrayList<Player> getPlayers() {
        return players;
    }
    

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
