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

    public static final String REDOUBT = "REDOUBT";
    public static final String SKIRMISH = "SKIRMISH";
    public static final String THROW_DICE = "THROW_DICE";
    public static final String END_TURN = "END_TURN";
    public static final String MOVE_UNIT = "MOVE_UNIT";
    public static final String COMBAT = "COMBAT";
    public static final String SETUP_POSITION = "SETUP_POSITION";
    public static final String PLAY_CARD = "PLAY_CARD";
    public static final String NEXT_PHASE = "NEXT_PHASE";
    public static final String END_SETUP = "END_SETUP";
    public static final String DISCARD_CARD = "DISCARD_CARD";
    public static final String FORCE_WITHDRAW = "FORCE_WITHDRAW";
    public static final String TAKE_HIT = "TAKE_HIT";
    public static final String DRAW_CARD = "DRAW_CARD";
    public static final String RESET_FACTORY = "RESET_FACTORY";
    public static final String RESTORE_UNIT = "RESTORE_UNIT";
    public static final String CANCEL_ACTION = "CANCEL_ACTION";
    

      
    
    public void execute(Game game);
    public void undo(Game game);
    public String logCommand();
    public String getType();
    
}
