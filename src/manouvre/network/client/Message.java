package manouvre.network.client;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 
 * @author Piotr
 */
public class Message implements Serializable{
    
    /*
    Set of messages used in protocol
    */
    public static int NOT_OK = 0;
    public static int OK = 1;
    public static int BAD_CHANNEL_NAME = 2;
    public static int BAD_PASSWORD = 3;
    public static int IS_ROOM_LOCKED = 4;
    
    
    
    /*
    Message types
    */
    
    public static int CREATE_ROOM = 10;
    public static int JOIN_ROOM = 11;
    public static int IN_ROOM_CHAT = 12;
    
    
    
    
    private static final long serialVersionUID = 1L;
    /**
     * type login, .bye, message, signup, create_room, room_list
     */
    public String type, sender, content, recipient;
    
    int messageType, contentP;
    
    ArrayList<String> channelList; 

      
    public Message(String type, String sender, String content, String recipient){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
    }
    
    public Message(int type, String sender, int content, String recipient){
        this.messageType = type; this.sender = sender; this.contentP = content; this.recipient = recipient;
    }
    
    public Message(int type, String sender, String content, String recipient){
        this.messageType = type; this.sender = sender; this.content = content; this.recipient = recipient;
    }
    
    @Override
    public String toString(){
        return "{type='"+type+"', sender='"+sender+"', content='"+content+"', recipient='"+recipient+"'}";
    }
    
    public ArrayList<String> getChannelList() {
        return channelList;
    }

    public void setChannelList(ArrayList<String> channelList) {
        this.channelList = channelList;
    }
    
    boolean isType(int messageType)
    {
        if(this.messageType == messageType)
            return true;
        else return false;
    }
    
}
