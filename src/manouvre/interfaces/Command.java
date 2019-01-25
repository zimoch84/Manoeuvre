/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.interfaces;

import java.io.Serializable;
import manouvre.game.Game;

/**
 *
 * @author Piotr
 */
public interface Command extends  Serializable{
    
    enum Type {REDOUBT ,SKIRMISH, THROW_DICE , END_TURN, MOVE_UNIT, COMBAT, 
    SETUP_POSITION, PLAY_CARD, NEXT_PHASE, END_SETUP, DISCARD_CARD, FORCE_WITHDRAW, 
    TAKE_HIT, DRAW_CARD, RESET_FACTORY, RESTORE_UNIT, CANCEL_ACTION,
    
    LOGIN, CHAT, CHAT_IN_ROOM, CREATE_ROOM, JOIN_ROOM, 
    GET_ROOM_LIST, BYE, START_GAME, SET_NATION, GAME_COMMAND, OK, ERROR
    }
    
    public void execute(Game game);
    public void undo(Game game);
    public String logCommand();
    public Type getType();
    
}
