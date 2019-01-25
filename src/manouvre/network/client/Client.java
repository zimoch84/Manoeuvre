/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.client;

import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.interfaces.Command;
import manouvre.network.core.Message;
import manouvre.network.server.GameRoom;

/**
 *
 * @author Piotr
 */
public interface Client {
    
    void openLoginWindow();
    void chatInActiveWindow(String chatMessage);
    void setRoomList(ArrayList<GameRoom> roomList);
    void createRoomWindow(Player player, int mode);
    void startGame(Game game);
    void setNation(Player.Nation nation);
    void executeCommand(Command command, Game game);
    
    void sendMessage(Message msg);
    
    void recconnect();
}
