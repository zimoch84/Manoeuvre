/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server.commands;

import manouvre.network.core.Message;
import manouvre.network.core.MessageFactory;
import manouvre.network.core.User;
import manouvre.network.server.ClientServerThread;
import manouvre.network.server.GameServer;

/**
 *
 * @author Piotr
 */
public class NOTOKCommand extends ServerCommand{
    
    User loginPlayerName;
    String password;
    ClientServerThread csTherad;
    String log;
    
    public NOTOKCommand( User loginPlayerName) {
        this.loginPlayerName = loginPlayerName;
    }
    @Override
    public void execute(GameServer server) {
        
        ClientServerThread csThread = server.findUserThread(loginPlayerName);
        Message response = MessageFactory.createNOTOKResponse();
        csThread.send(response);
    }

    @Override
    public Type getType() {
        return Type.OK;
    
    }

    @Override
    public String logCommand() {
       return log;
        
    }
        
    
}
