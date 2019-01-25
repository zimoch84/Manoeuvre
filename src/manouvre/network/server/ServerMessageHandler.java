/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server;

import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.network.core.Message;
import manouvre.interfaces.Command;
import manouvre.network.core.User;

/**
 *
 * @author Piotr_Grudzien
 */
public class ServerMessageHandler {
    
    GameServer server;
    
    private ClientServerThread clients[];
    private ArrayList<GameRoom> gameRooms;
    private ServerFrame ui;
    private AuthorizitionControl db ;
    
    public ServerMessageHandler(GameServer server){
       this.server = server;
       clients = server.clients;
       gameRooms = server.channels;
       db = server.userDatabase;
       ui = server.ui;
       
    }

    public synchronized void handle(int portID, Message msg) {
        
        server.notifyAbout("INFO", "Message handle : " +  msg.toString());
        
        Message msgOut;
        GameRoom gameRoom;
        try {
            switch (msg.getMessageType()) {
                case Message.LOGIN:
                    if (server.findUserThread(msg.getSender()) == null) {
                        if (db.authorize(msg.getSender(), msg.content)) {
                            clients[server.findClient(portID)].user =msg.getSender();
                            clients[server.findClient(portID)].setUser(msg.getSender());
                            clients[server.findClient(portID)].send(new Message(Message.LOGIN, "SERVER", Message.OK, msg.sender));
                            server.announce(new Message(Message.USER_LOGGED, "SERVER", Message.OK, msg.sender));
                            //SendUserList(msg.sender);
                        } else {
                            clients[server.findClient(portID)].send(new Message(Message.LOGIN, "SERVER", Message.NOT_OK, msg.sender));
                        }
                    } else {
                        clients[server.findClient(portID)].send(new Message(Message.LOGIN, "SERVER", Message.NOT_OK, msg.sender));
                    }
                    break;
                case Message.CHAT:
                    if (msg.recipient.equals("All")) {
                        server.announce(new Message(Message.CHAT, msg.sender, msg.content, "SERVER"));
                    } else {
                        server.findUserThread(new User(msg.recipient)).send(new Message(Message.CHAT, msg.sender, msg.content, msg.recipient));
                        clients[server.findClient(portID)].send(new Message(Message.CHAT, msg.sender, msg.content, msg.recipient));
                    }
                    break;
                case Message.CHAT_IN_ROOM:
                    server.announceInRoom(server.getRoomByPort(portID), msg);
                    // clients[server.findClient(portID)].send(new Message(Message.CHAT, msg.sender, msg.content, msg.recipient));
                    break;
                case Message.CREATE_ROOM:
                    String[] parts = msg.content.split("|");
                    String name = parts[0];
                    String password = parts[1];
                    GameRoom newRoom = msg.getChannelList().get(0);
                    Player hostPlayer = newRoom.getHostPlayer();
                    //Predefine choice if player would choose nothing
                    hostPlayer.setNation(Player.Nation.AU);
                    
                    /*
                    Check if socket client localport  = server port
                     */
                    if (portID == newRoom.getHostSocketPortId()) {
                        this.gameRooms.add(newRoom);
                    }
                    /*
                    Wysylamy do klienta ze udalo sie dodac kanal
                     */
                    clients[server.findClient(portID)].send(new Message(Message.CREATE_ROOM, "SERVER", Message.OK, msg.sender));
                    /*
                    Wysylamy do wszystkich userow liste kanalow
                     */
                    msgOut = new Message(Message.GET_ROOM_LIST, "SERVER", "serverserver.Announce", "All");
                    msgOut.setChannelList(server.getRooms());
                    msgOut.setContent("Channels size " + Integer.toString(server.getRooms().size()));
                    server.announce(msgOut);
                    break;
                case Message.JOIN_ROOM:
                    /*
                    Szukamy pokoju
                     */
                    GameRoom requestRoom = msg.getChannelList().get(0);
                    /*
                    If room exists
                     */
                    if (server.isRoomExistsOnServer(requestRoom)) {
                        GameRoom room = server.getRoomByPort(requestRoom.getHostSocketPortId());
                        if (!room.isLocked()) {
                            room.setGuestSocketPortId(portID);
                            Player guestPlayer = msg.getPlayer();
                            //Predefine choice if player would choose nothing                     
                            guestPlayer.setNation(Player.Nation.US);
                            room.setGuestPlayer(guestPlayer);
                            System.out.println("manouvre.network.server.ManouvreServer.handle() " + msg.getPlayer().getName() + " has joined room");
                            /*
                            Send to guest player - OK  and host player object
                             */
                            msgOut = new Message(Message.JOIN_ROOM, "SERVER", Message.OK, msg.sender);
                            msgOut.addPlayer(room.getHostPlayer());
                            clients[server.findClient(portID)].send(msgOut);
                            //Send message to host of the room and guest player object
                            Message msgToHost = new Message(Message.JOIN_ROOM, "SERVER", Message.USER_JOINED_IN_ROOM, room.getHostPlayer().getName());
                            msgToHost.addPlayer(room.getGuestPlayer());
                            clients[server.findClient(room.getHostSocketPortId())].send(msgToHost);
                        } else {
                            msgOut = new Message(Message.JOIN_ROOM, "SERVER", Message.IS_ROOM_LOCKED, msg.sender);
                            clients[server.findClient(portID)].send(msgOut);
                        }
                    } else {
                        msgOut = new Message(Message.JOIN_ROOM, "SERVER", Message.ROOM_NOT_FOUND, msg.sender);
                        clients[server.findClient(portID)].send(msgOut);
                    }
                    break;
                case Message.GET_ROOM_LIST:
                    /*
                    Wysylamy do wszystkich userow liste kanalow
                     */
                    msgOut = new Message(Message.GET_ROOM_LIST, "SERVER", "serverResponse", msg.sender);
                    msgOut.setChannelList(server.getRooms());
                    msgOut.setContent("Channels size " + Integer.toString(server.getRooms().size()));
                    clients[server.findClient(portID)].send(msgOut);
                    break;
                case Message.BYE:
                    server.announce("signout", "SERVER", msg.sender);
                    server.remove(portID);
                    break;
                case Message.START_GAME:
                    /*
                    Searching for host Room
                     */
                    gameRoom = server.getRoomByPort(portID);
                    /*
                    Only Host can start game.
                    Message carry on info about players and their choices about army.
                     */
                    msgOut = new Message(Message.START_GAME, "SERVER", Message.OK, "ROOM: " + gameRoom.getName());
                    /*
                    Creating a game
                     */
                    ArrayList<Player> players = gameRoom.getPlayers();
                    
                 
                    ArrayList<Player> players2 = ( ArrayList<Player> ) UnoptimizedDeepCopy.copy (players); 
                    //Assiging players
                    //ArrayList<Player> players = msg.getPlayers();
                    //Creating game - generate map, deal cards - setup army etc.
                    Game game = new Game(players2);
                    /*
                    To avioid multi-threading problems with objecs
                    */
                    Game game2 = (Game) UnoptimizedDeepCopy.copy (game);
                    
                    gameRoom.setGame(game);
                    //Setting msg to carry whole game
                    server.notifyAbout("INFO", "game started " + game2.toString());
                    
                    msgOut.setGame(game2);
                    players.get(0).setArmy(game2.getHostPlayer().getArmy());
                    players.get(1).setArmy(game2.getGuestPlayer().getArmy());
                    
                    msgOut.hostPlayer = players2.get(0);
                    msgOut.guestPlayer = players2.get(1);
                      
                    //Sending response to Host;
                    //server.announceInRoom(gameRoom, msgOut);
                    clients[server.findClient(gameRoom.getHostSocketPortId())].send(msgOut);
                    
                    clients[server.findClient(gameRoom.getGuestSocketPortId())].send(msgOut);
                 
                    break;
                case Message.SET_NATION:
                    /*
                    Searching for host Room
                     */
                    gameRoom = server.getRoomByPort(portID);
                    
                    /*
                    Setting Player Nation
                    */
                    gameRoom.getCurrentPlayer(portID).setNation(Player.Nation.fromValue(msg.getContentP()));
                    /*
                    Sending message to opponent if room is full
                    */
                    if(gameRoom.isLocked())       
                    {
                        msgOut = new Message(Message.SET_NATION, "SERVER", msg.getContentP(), "ROOM: " + gameRoom.getName());
                        clients[server.findClient(gameRoom.getOpponentPortSocket(portID))].send(msgOut);
                    }
                    
                    break;
                case Message.COMMAND:
                    /*
                    Searching for host Room
                     */
                    gameRoom = server.getRoomByPort(portID);
                    
                    Command executeCommand = msg.getCommand();
                    
                    /*
                    Executing command over game on server
                    */
                    //executeCommand.execute(gameRoom.getGame());
                    
                    /*
                    Send command to opponent with the same message
                    */
                    Message messageOut = (Message) UnoptimizedDeepCopy.copy (msg);
                    
                    clients[server.findClient(gameRoom.getOpponentPortSocket(portID))].send(messageOut);
                    server.notifyAbout("INFO", "Sending command " + executeCommand);
                    break;
                                        
                    
                default:
                    server.notifyAbout("INFO","manouvre.network.server.ManouvreServer.handle() No type handled" + msg.getType());
            }
        } 
        
        catch (NullPointerException ex) {
           server.notifyAbout("INFO", server.getStackTrace(ex));
        }
       
    }
    
}
