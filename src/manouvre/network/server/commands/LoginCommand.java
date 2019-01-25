/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server.commands;

import manouvre.game.Player;
import manouvre.network.core.MessageFactory;
import manouvre.network.core.User;
import manouvre.network.server.ClientServerThread;
import manouvre.network.server.GameServer;

/**
 *
 * @author Piotr
 */
public class LoginCommand extends ServerCommand{
    
    User loginPlayerName;
    String password;
    String log;
    
    public LoginCommand(User loginPlayerName, String password) {
        this.loginPlayerName = loginPlayerName;
        this.password = password;
    }

    @Override
    public void execute(GameServer server) {
        
        boolean testLogin = server.authorize(loginPlayerName, password);
        if(testLogin){
        ClientServerThread csThread = server.findUserThread(loginPlayerName);
        Player player = new Player(loginPlayerName);
        csThread.setPlayer(player);
        
        OKCommand ok = new OKCommand(loginPlayerName);
        ok.execute(server);
        
        //Announce command
       ChatCommand chat = new ChatCommand(MessageFactory.createChatMessage(log, log, ));
        
        
        
       
        
        }
        else 
        {
            NOTOKCommand notOk = new NOTOKCommand(loginPlayerName);
            notOk.execute(server);
        
        }

    }

    @Override
    public Type getType() {
        return Type.LOGIN;
    }

    @Override
    public String logCommand() {
        return log;
    }
    
}
