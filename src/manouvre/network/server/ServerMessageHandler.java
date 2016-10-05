/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.network.server;

import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.game.interfaces.CardInterface;
import manouvre.network.client.Message;

/**
 *
 * @author Piotr_Grudzien
 */
public class ServerMessageHandler {
    
    ManouvreServer server;
    
    private ClientServerThread clients[];
    private ArrayList<GameRoom> channels;
    private ServerFrame ui;
    private Database db ;
    
    public ServerMessageHandler(ManouvreServer server){
       this.server = server;
       clients = server.clients;
       channels = server.channels;
       db = server.db;
       ui = server.ui;
       
    }

    public synchronized void handle(int ID, Message msg) {
        System.out.println("manouvre.network.server.SocketServer.handle()" + msg.toString());
        ui.jTextArea1.append("\n" + msg.toString() + "\n");
        Message msgOut;
        GameRoom gameRoom;
        try {
            switch (msg.getMessageType()) {
                case Message.LOGIN:
                    if (server.findUserThread(msg.sender) == null) {
                        if (db.checkLogin(msg.sender, msg.content)) {
                            clients[server.findClient(ID)].username = msg.sender;
                            clients[server.findClient(ID)].setPlayer(new Player(msg.sender));
                            clients[server.findClient(ID)].send(new Message(Message.LOGIN, "SERVER", Message.OK, msg.sender));
                            server.Announce(new Message(Message.USER_LOGGED, "SERVER", Message.OK, msg.sender));
                            //SendUserList(msg.sender);
                        } else {
                            clients[server.findClient(ID)].send(new Message(Message.LOGIN, "SERVER", Message.NOT_OK, msg.sender));
                        }
                    } else {
                        clients[server.findClient(ID)].send(new Message(Message.LOGIN, "SERVER", Message.NOT_OK, msg.sender));
                    }
                    break;
                case Message.CHAT:
                    if (msg.recipient.equals("All")) {
                        server.Announce(new Message(Message.CHAT, msg.sender, msg.content, "SERVER"));
                    } else {
                        server.findUserThread(msg.recipient).send(new Message(Message.CHAT, msg.sender, msg.content, msg.recipient));
                        clients[server.findClient(ID)].send(new Message(Message.CHAT, msg.sender, msg.content, msg.recipient));
                    }
                    break;
                case Message.CHAT_IN_ROOM:
                    server.announceInRoom(server.getRoomByPort(ID), msg);
                    // clients[server.findClient(ID)].send(new Message(Message.CHAT, msg.sender, msg.content, msg.recipient));
                    break;
                case Message.CREATE_ROOM:
                    String[] parts = msg.content.split("|");
                    String name = parts[0];
                    String password = parts[1];
                    GameRoom newRoom = msg.getChannelList().get(0);
                    Player hostPlayer = newRoom.getHostPlayer();
                    //Predefine choice if player would choose nothing
                    hostPlayer.setNation(CardInterface.AU);
                    
                    /*
                    Check if socket client localport  = server port
                     */
                    if (ID == newRoom.getHostSocketPortId()) {
                        this.channels.add(newRoom);
                    }
                    /*
                    Wysylamy do klienta ze udalo sie dodac kanal
                     */
                    clients[server.findClient(ID)].send(new Message(Message.CREATE_ROOM, "SERVER", Message.OK, msg.sender));
                    /*
                    Wysylamy do wszystkich userow liste kanalow
                     */
                    msgOut = new Message(Message.GET_ROOM_LIST, "SERVER", "serverserver.Announce", "All");
                    msgOut.setChannelList(server.getRooms());
                    msgOut.setContent("Channels size " + Integer.toString(server.getRooms().size()));
                    server.Announce(msgOut);
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
                            room.setGuestSocketPortId(ID);
                            Player guestPlayer = msg.getPlayer();
                            //Predefine choice if player would choose nothing                     
                            guestPlayer.setNation(CardInterface.US);
                            room.setGuestPlayer(guestPlayer);
                            System.out.println("manouvre.network.server.ManouvreServer.handle() " + msg.getPlayer().getName() + " has joined room");
                            /*
                            Send to guest player - OK  and host player object
                             */
                            msgOut = new Message(Message.JOIN_ROOM, "SERVER", Message.OK, msg.sender);
                            msgOut.addPlayer(room.getHostPlayer());
                            clients[server.findClient(ID)].send(msgOut);
                            //Send message to host of the room and guest player object
                            Message msgToHost = new Message(Message.JOIN_ROOM, "SERVER", Message.USER_JOINED_IN_ROOM, room.getHostPlayer().getName());
                            msgToHost.addPlayer(room.getGuestPlayer());
                            clients[server.findClient(room.getHostSocketPortId())].send(msgToHost);
                        } else {
                            msgOut = new Message(Message.JOIN_ROOM, "SERVER", Message.IS_ROOM_LOCKED, msg.sender);
                            clients[server.findClient(ID)].send(msgOut);
                        }
                    } else {
                        msgOut = new Message(Message.JOIN_ROOM, "SERVER", Message.ROOM_NOT_FOUND, msg.sender);
                        clients[server.findClient(ID)].send(msgOut);
                    }
                    break;
                case Message.GET_ROOM_LIST:
                    /*
                    Wysylamy do wszystkich userow liste kanalow
                     */
                    msgOut = new Message(Message.GET_ROOM_LIST, "SERVER", "serverResponse", msg.sender);
                    msgOut.setChannelList(server.getRooms());
                    msgOut.setContent("Channels size " + Integer.toString(server.getRooms().size()));
                    clients[server.findClient(ID)].send(msgOut);
                    break;
                case Message.BYE:
                    server.Announce("signout", "SERVER", msg.sender);
                    server.remove(ID);
                    break;
                case Message.START_GAME:
                    /*
                    Searching for host Room
                     */
                    gameRoom = server.getRoomByPort(ID);
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
                    
                    Game game2 = (Game) UnoptimizedDeepCopy.copy (game);
                    
                    //gameRoom.setGame(game);
                    //Setting msg to carry whole game
                    System.out.println("manouvre.network.server.ServerMessageHandler.handle() " + game2.toString()); 
                    msgOut.setGame(game2);
                    players.get(0).setArmy(game2.hostPlayer.getArmy());
                    players.get(1).setArmy(game2.guestPlayer.getArmy());
                    
                    msgOut.hostPlayer = players2.get(0);
                    msgOut.guestPlayer = players2.get(1);
                      
                    //Sending response to Host;
                    //server.announceInRoom(gameRoom, msgOut);
                    clients[server.findClient(gameRoom.getHostSocketPortId())].send(msgOut);
                    System.out.println("manouvre.network.server.ServerMessageHandler.handle() " + game.toString()); 
                    clients[server.findClient(gameRoom.getGuestSocketPortId())].send(msgOut);
                    System.out.println("manouvre.network.server.ServerMessageHandler.handle() " + game.toString()); 
                    break;
                case Message.SET_NATION:
                    /*
                    Searching for host Room
                     */
                    gameRoom = server.getRoomByPort(ID);
                    
                    /*
                    Setting Player Nation
                    */
                    gameRoom.getCurrentPlayer(ID).setNation(msg.getContentP());
                    /*
                    Sending message to opponent if room is full
                    */
                    if(gameRoom.isLocked())       
                    {
                        msgOut = new Message(Message.SET_NATION, "SERVER", msg.getContentP(), "ROOM: " + gameRoom.getName());
                        clients[server.findClient(gameRoom.getOpponentPortSocket(ID))].send(msgOut);
                    }
                    
                    break;
                default:
                    System.out.println("manouvre.network.server.ManouvreServer.handle() No type handled" + msg.getType());
            }
        } catch (NullPointerException ex) {
            System.out.println("manouvre.network.server.ManouvreServer.handle()" + ex.toString());
        }
        //        if (msg.content.equals(".bye")){
        //            server.Announce("signout", "SERVER", msg.sender);
        //            server.remove(ID);
        //	}
        //	else{
        //            if(msg.getType().equals("login")){
        //                if(server.findUserThread(msg.sender) == null){
        //                    if(db.checkLogin(msg.sender, msg.content)){
        //                        clients[server.findClient(ID)].username = msg.sender;
        //                        clients[server.findClient(ID)].send(new Message("login", "SERVER", "TRUE", msg.sender));
        //                        server.Announce("newuser", "SERVER", msg.sender);
        //                        SendUserList(msg.sender);
        //                    }
        //                    else{
        //                        clients[server.findClient(ID)].send(new Message("login", "SERVER", "FALSE", msg.sender));
        //                    }
        //                }
        //                else{
        //                    clients[server.findClient(ID)].send(new Message("login", "SERVER", "FALSE", msg.sender));
        //                }
        //            }
        //            else if(msg.getType().equals("message")){
        //                if(msg.recipient.equals("All")){
        //                    server.Announce("message", msg.sender, msg.content);
        //                }
        //                else{
        //                    server.findUserThread(msg.recipient).send(new Message(msg.type, msg.sender, msg.content, msg.recipient));
        //                    clients[server.findClient(ID)].send(new Message(msg.type, msg.sender, msg.content, msg.recipient));
        //                }
        //            }
        //            else if(msg.getType().equals("test")){
        //                clients[server.findClient(ID)].send(new Message("test", "SERVER", "OK", msg.sender));
        //            }
        //
        //            else if(msg.getType().equals("join_room")){
        //
        //                String[] parts = msg.content.split("|");
        //                String name = parts[0];
        //                String password = parts[1];
        //
        //                /*
        //                Szukaj czy jest taki pokoj i dodajemy usera (socket)
        //                */
        //
        //                for(GameRoom checkRoom : server.getRooms())
        //
        //                {
        //                    if(checkRoom.getName().equals(name) && checkRoom.getPassword().equals(password) && !checkRoom.isLocked())
        //                    {
        //                        checkRoom.addSocket(this.clients[server.findClient(ID)].getSocket());
        //                        checkRoom.addPlayer(this.clients[server.findClient(ID)].getPlayer());
        //
        //                        clients[server.findClient(ID)].send (new Message (Message.JOIN_ROOM,"SERVER", Message.OK, msg.sender)  );
        //
        //                        server.announceInRoom(checkRoom, new Message (Message.IN_ROOM_CHAT, "SERVER", "Player " + msg.sender + " joined the room", "All") );
        //                        break;
        //                    }
        //                    else if (!checkRoom.getName().equals(name))
        //                    {
        //                        clients[server.findClient(ID)].send(new Message (Message.JOIN_ROOM, "SERVER", Message.BAD_CHANNEL_NAME, msg.sender));
        //                        break;
        //                    }
        //                    else if (checkRoom.getName().equals(name) && !checkRoom.getPassword().equals(password))
        //                    {
        //                        clients[server.findClient(ID)].send(new Message (Message.JOIN_ROOM, "SERVER", Message.BAD_PASSWORD, msg.sender));
        //                        break;
        //                    }
        //                    else if (checkRoom.isLocked())
        //                        clients[server.findClient(ID)].send(new Message (Message.JOIN_ROOM, "SERVER", Message.IS_ROOM_LOCKED, msg.sender));
        //                    else {
        //                        System.out.println("manouvre.network.server.ManouvreServer.handler() Something goes wrong" );
        //
        //                    }
        //
        //                }
        //
        //                GameRoom newRoom = new GameRoom( name, password, clients[server.findClient(ID)].clientServerSocket, new Player (msg.sender) );
        //                this.channels.add(newRoom);
        //
        //                /*
        //                Wysylamy do klienta ze udalo sie doda kanal
        //                */
        //                clients[server.findClient(ID)].send(new Message(Message.CREATE_ROOM, "SERVER", Message.OK, msg.sender));
        //
        //
        //                /*
        //                Wysylamy do wszystkich liste kanalow
        //                */
        //                msgOut = new Message("room_list", "inClass", "SERVER", "All");
        //                msgOut.setChannelList(server.getRooms());
        //                server.Announce(msgOut);
        //
        //
        //
        //
        //
        //
        //            }
        //
        //
        //            else if(msg.getType().equals("signup")){
        //                if(server.findUserThread(msg.sender) == null){
        //                    if(!db.userExists(msg.sender)){
        //                        db.addUser(msg.sender, msg.content);
        //                        clients[server.findClient(ID)].username = msg.sender;
        //                        clients[server.findClient(ID)].send(new Message("signup", "SERVER", "TRUE", msg.sender));
        //                        clients[server.findClient(ID)].send(new Message("login", "SERVER", "TRUE", msg.sender));
        //                        server.Announce("newuser", "SERVER", msg.sender);
        //                        SendUserList(msg.sender);
        //                    }
        //                    else{
        //                        clients[server.findClient(ID)].send(new Message("signup", "SERVER", "FALSE", msg.sender));
        //                    }
        //                }
        //                else{
        //                    clients[server.findClient(ID)].send(new Message("signup", "SERVER", "FALSE", msg.sender));
        //                }
        //            }
        //
        //	}
    }
    
}
