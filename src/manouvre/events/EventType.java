/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.events;

/**
 *
 * @author piotr_grudzien
 */
public class EventType {

    
    public static final String SETUP_FINISHED = "SETUP_FINISHED";
    public static final String PLAYER_MOVED = "PLAYER_MOVED";
    
    public static final String ASSAULT_BEGINS = "ASSAULT_BEGINS";
    public static final String VOLLEY_BEGINS = "VOLLEY_BEGINS";
    public static final String BOMBARD_BEGINS = "BOMBARD_BEGINS";
    
    public static final String VOLLEY_ASSAULT_DECISION = "VOLLEY_ASSAULT_DECISION";
    public static final String VOLLEY_ASSAULT_DECISION_DESELECTION = "VOLLEY_ASSAULT_DECISION_DESELECTION";
    
    public static final String COMBAT_THROW_DICE = "COMBAT_THROW_DICE";
    public static final String COMBAT_DICE_ROLLED = "DICE_ROLLED"; 
    public static final String COMBAT_ATTACKER_DECIDES = "ATTACKER_DECIDES";
    public static final String COMBAT_DEFENDER_DECIDES = "DEFENDER_DECIDES";
    public static final String COMBAT_DEFENDER_WITHDRAW = "DEFENDER_WITHDRAW";

    public static final String COMBAT_DEFENDER_TAKES_HIT = "COMBAT_DEFENDER_TAKES_HIT";
    public static final String COMBAT_DEFENDER_ELIMINATE = "COMBAT_ELIMINATE";
    public static final String COMBAT_ATTACKER_ELIMINATE = "COMBAT_ATTACKER_ELIMINATE";
    public static final String COMBAT_NO_RESULT = "COMBAT_NO_RESULT";
    public static final String COMBAT_PUSRUIT_AFTER_RESOLUTION = "PUSRUIT_AFTER_COMBAT_RESOLUTION";
    public static final String COMBAT_ADVANCE_STARTED = "COMBAT_ADVANCE_STARTED";
    public static final String COMBAT_ATTACKER_TAKES_HIT = "COMBAT_ATTACKER_TAKES_HIT";
    public static final String PUSRUIT_SUCCEDED = "PUSRUIT_SUCCEDED";
    public static final String PUSRUIT_FAILED = "PUSRUIT_FAILED";
    public static final String PICK_COMMITTED_ATTACK_CASUALITIES = "PICK_COMMITTED_ATTACK_CASUALITIES";
    public static final String END_COMBAT = "END_COMBAT";
    
    public static final String HOST_GAME_OVER = "HOST_GAME_OVER";
    public static final String GUEST_GAME_OVER = "GUEST_GAME_OVER";
    
    public static final String SKIRMISH_DESELECTED = "SKIRMISH_DESELECTED";

    public static final String DEFENDING_CARDS_PLAYED = "DEFENDING_CARDS_PLAYED";
    public static final String CANCELLABLE_CARD_PLAYED = "CANCELLABLE_CARD_PLAYED";
   
    public static final String SKIRMISH_SELECTED = "SKIRMISH_SELECTED";
    public static final String SUPPLY_SELECTED = "SUPPLY_SELECTED";
    
    public static final String SKIRMISH_PLAYED = "SKIRMISH_PLAYED";
    public static final String GUIRELLA_PLAYED = "GUIRELLA_PLAYED";
    public static final String REDOUBT_PLAYED = "REDOUBT_PLAYED";
    
    
    public static final String LEADER_SELECTED = "LEADER_SELECTED";
    public static final String LEADER_DESELECTED = "LEADER_DESELECTED";
    public static final String LEADER_FOR_COMBAT = "LEADER_FOR_COMBAT";
    public static final String LEADER_FOR_COMMAND = "LEADER_FOR_COMMAND";
    public static final String LEADER_END_PICKING_SUPPORT = "LEADER_END_PICKING_SUPPORT";
    
    public static final String RESTORATION_BY_LEADER = "RESTORATION_BY_LEADER";

    public static final String PICK_SUPPORT_UNIT = "PICK_SUPPORT_UNIT";
    public static final String PUSRUIT_AFTER_WITHDRAW = "PUSRUIT_AFTER_WITHDRAW";
    
    public static final String CARD_ACCEPTED = "CARD_ACCEPTED";
    public static final String CARD_SELECTED = "CARD_SELECTED"; 
    public static final String CARD_DESELECTED = "CARD_DESELECTED";
    public static final String CARD_PLAYED = "CARD_PLAYED"; 
    public static final String CARD_ASSAULT_MODE = "CARD_ASSAULT_MODE"; 
    public static final String CARD_VOLLEY_MODE = "CARD_VOLLEY_MODE";
    
    public static final String CARD_THERE_IS_NO_ROOM_FOR_MOVE = "CARD_THERE_IS_NO_ROOM_FOR_MOVE";
        
    public static final String CARDS_DISCARDED = "CARDS_DISCARDED"; 
    public static final String CARDS_DRAWNED = "CARDS_DRAWNED"; 
    public static final String CARD_MOVED_TO_TABLE = "CARD_MOVED_TO_TABLE"; 
    public static final String TABLE_CLEANED = "TABLE_CLEANED"; 
    
    public static final String NEXT_PHASE = "NEXT_PHASE"; 
    public static final String END_TURN = "END_TURN"; 
     
    public static final String COMMAND_EXECUTED = "COMMAND_EXECUTED"; 
    public static final String COMMAND_CANCEL = "COMMAND_CANCEL"; 

    public static final String DIALOG_NO_DECISION = "DIALOG_NO_DECISION";
    
}
