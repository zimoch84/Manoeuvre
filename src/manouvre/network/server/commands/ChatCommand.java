/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server.commands;

import manouvre.network.core.Message;
import manouvre.network.server.ClientServerThread;
import manouvre.network.server.GameServer;

/**
 *
 * @author Piotr
 */
public class ChatCommand extends ServerCommand{
    
    String loginPlayerName;
    Message message;
    ClientServerThread csTherad;
    String log, text;
    
    public ChatCommand(Message message, String text) {
        this.message = message;
        this.text = text;
    }

    @Override
    public void execute(GameServer server) {
        
       switch (message.getTarget()) {
       case ALL:
           server.announce(message);
           break;
       case INROOM:
           server.announceInRoom(server.findUserRoom(message.getSender()) , message);
           break;
       case PRIVATE:
           break;
       case SEVER:    
           break;
           
       
       
       }

       log = "Sever announce message: " + message.getContent();
     }

    @Override
    public Type getType() {
        return Type.CHAT;
    
    }

    @Override
    public String logCommand() {
       return log;
        
    }
        
    
}
