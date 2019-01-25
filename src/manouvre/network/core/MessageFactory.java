/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.core;

import manouvre.interfaces.Command;
import manouvre.network.server.commands.ChatCommand;

/**
 *
 * @author Piotr
 */
public class MessageFactory {

 private static MessageFactory instance;
    
    private MessageFactory(){}
    
    public static MessageFactory getInstance(){
        if(instance == null){
            instance = new MessageFactory();
        }
        return instance;
    }
    
public static Message createOKResponse(){
    return  new Message(Message.Type.RESPONSE, "SERVER", Message.Result.OK );
}
public static Message createNOTOKResponse(){
    return  new Message(Message.Type.RESPONSE, "SERVER", Message.Result.NOT_OK);

}

public static Message createChatMessage(User from, Message.Target to , String text){
    Message chatMessage = new Message(Message.Type.REQUEST, from, to);
    Command chatCommand = new ChatCommand(chatMessage, text);
    chatMessage.setCommand(chatCommand);
    return chatMessage;
}
    
}
