/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.events;

import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import manouvre.game.Game;
import manouvre.gui.CommandLogger;
import manouvre.gui.CustomDialog;
import manouvre.state.HandStateHandler;
import manouvre.state.MapStateHandler;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author piotr_grudzien
 */
public class PanelsEventObserver implements Observer {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(PanelsEventObserver.class.getName());
    
    JPanel currentPlayerPanel, opponentPlayerPanel, infoPanel, tablePanel,phasePanel;
    Game game;
    
    CommandLogger cmdLogger;
    
    public PanelsEventObserver(Game game, JPanel currentPlayerPanel, JPanel opponentPlayerPanel, JPanel infoPanel, JPanel tablePanel, JPanel phasePanel) {
        this.currentPlayerPanel = currentPlayerPanel;
        this.opponentPlayerPanel = opponentPlayerPanel;
        this.infoPanel = infoPanel;
        this.tablePanel = tablePanel;
        this.phasePanel = phasePanel;
        this.game = game;
    }
    
    
    @Override
    public void update(Observable o, Object arg) {
        
        System.out.println("Object attached to event()" +  o.getClass().toString() );
        
        String dialogType = (String) arg;
        
        LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        
        switch(dialogType){
            case EventType.NEXT_PHASE:
            {
                game.setInfoBarText(null);
                break;
            }
            case EventType.PURSUIT:
            {
                if(game.getCurrentPlayer().isActive())
                    game.setInfoBarText("Pick puruit unit");
                else 
                    game.setInfoBarText("Opponent is picking puruit unit");
                break;
                
            }
            
            case EventType.BOMBARD_BEGINS:
                if(game.getCurrentPlayer().isActive())
                    game.setInfoBarText("Load cannon balls!");
                else 
                    game.setInfoBarText("Prepare for cannon balls!");
            break;
            
            case EventType.COMBAT_NO_RESULT:
           {
           
           game.setInfoBarText("Att: " + game.getCombat().getAttackValue()+
                   " vs Def: "+ game.getCombat().getDefenceValue() + " => No hit");
           break;
           }
           case EventType.COMBAT_DEFENDER_TAKES_HIT:
           {
           game.setInfoBarText(
                   "Att: " + game.getCombat().getAttackValue()
                           +" vs Def: "+ game.getCombat().getDefenceValue() +" => " 
                           +  "reduce defender unit");
           break;
           }
           
           case EventType.COMBAT_ATTACKER_TAKES_HIT:
           {
            game.setInfoBarText("Att: " + game.getCombat().getAttackValue()
                           +" vs Def: "+ game.getCombat().getDefenceValue() +" => " 
                           +  "reduce attacker unit");
           break;
           }
           case EventType.COMBAT_ATTACKER_ELIMINATE:
           {
           
            game.setInfoBarText("Att: " + game.getCombat().getAttackValue()
                           +" vs Def: "+ game.getCombat().getDefenceValue() +" => " 
                           +  "eliminate attacker unit");

           break;
           }
           case EventType.PUSRUIT_SUCCEDED:
           {
           
           game.setInfoBarText("Pursuit succeded");

           break;
           }
           
           case EventType.PUSRUIT_FAILED:
           {
           game.setInfoBarText("Pursuit failed");
           
           break;
           }
           
           case EventType.COMBAT_DEFENDER_ELIMINATE:
           {
           game.setInfoBarText("Att: " + game.getCombat().getAttackValue()
                           +" vs Def: "+ game.getCombat().getDefenceValue() +" => " 
                           +  "eliminate defender unit");
           
           break;
           }
           
           case EventType.ASSAULT_BEGINS:
           {
              game.setInfoBarText("Combat Begins");
           break;
           }
           
           case EventType.HOST_GAME_OVER:
               
           {
                
                   game.setInfoBarText("Game over! " + game.getGuestPlayer().getName() + " wins by killing more than 4 units!");
                      
                   CustomDialog cd = new CustomDialog(CustomDialog.CONFIRMATION_TYPE, 
                           "Game over" + game.getGuestPlayer().getName() + "wins by killing more than 4 units!");
                   cd.setVisible(true);
           break;
           }
           
           case EventType.GUEST_GAME_OVER:
           {
                
               game.setInfoBarText("Game over! " + game.getHostPlayer().getName() + "wins by killing more than 4 enemy units!");
                   CustomDialog cd = new CustomDialog(CustomDialog.CONFIRMATION_TYPE, 
                           "Game over! " + game.getHostPlayer().getName() + "wins by killing more than 4 enemy units!");
                   cd.setVisible(true);
           break;
           }    
           
           case EventType.CARD_THERE_IS_NO_ROOM_FOR_MOVE:
               game.setInfoBarText("There is no room for move");
               break;
                   
               
        
    }
    
    } 
    
}
