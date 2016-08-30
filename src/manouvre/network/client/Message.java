package manouvre.network.client;

import java.io.Serializable;
import java.util.ArrayList;
/**
 * 
 * @author Piotr
 */
public class Message implements Serializable{
    
    private static final long serialVersionUID = 1L;
    /**
     * type login, .bye, message, signup, create_room, room_list
     */
    public String type, sender, content, recipient;
    
    
    ArrayList<String> channelList; 

      
    public Message(String type, String sender, String content, String recipient){
        this.type = type; this.sender = sender; this.content = content; this.recipient = recipient;
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
    
}
