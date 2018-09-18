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
import manouvre.gui.CustomDialogFactory;
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
        
        game.addObserver(this);
    }
    
    
    @Override
    public void update(Observable o, Object arg) {
        
        String dialogType = (String) arg;
        
        switch(dialogType){
            case EventType.NEXT_PHASE:
                game.setInfoBarText(null);
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
                break;

            case EventType.COMBAT_PURSUIT_STARTED:
                if(game.getCurrentPlayer().isActive())
                    game.setInfoBarText("Pick puruit unit");
                else 
                    game.setInfoBarText("Opponent is picking puruit unit");
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
                break;

            case EventType.BOMBARD_BEGINS:
                if(game.getCurrentPlayer().isActive())
                    game.setInfoBarText("Load cannon balls!");
                else 
                    game.setInfoBarText("Prepare for cannon balls!");
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;
            
            case EventType.COMBAT_NO_RESULT:
               game.setInfoBarText("Att: " + game.getCombat().getAttackValue()+
                   " vs Def: "+ game.getCombat().getDefenceValue() + " => No hit");
               LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;
            case EventType.COMBAT_DEFENDER_TAKES_HIT:
               game.setInfoBarText(
                   "Att: " + game.getCombat().getAttackValue()
                           +" vs Def: "+ game.getCombat().getDefenceValue() +" => " 
                           +  "reduce defender unit");
               LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;

            case EventType.COMBAT_ATTACKER_TAKES_HIT:
                 game.setInfoBarText("Att: " + game.getCombat().getAttackValue()
                            +" vs Def: "+ game.getCombat().getDefenceValue() +" => " 
                            +  "reduce attacker unit");
                 LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;
            case EventType.COMBAT_ATTACKER_ELIMINATE:
                 game.setInfoBarText("Att: " + game.getCombat().getAttackValue()
                            +" vs Def: "+ game.getCombat().getDefenceValue() +" => " 
                            +  "eliminate attacker unit");
                 LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;

            case EventType.PUSRUIT_SUCCEDED:
                game.setInfoBarText("Pursuit succeded");
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;

            case EventType.PUSRUIT_FAILED:
                game.setInfoBarText("Pursuit failed");
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;

            case EventType.COMBAT_DEFENDER_ELIMINATE:
                game.setInfoBarText("Att: " + game.getCombat().getAttackValue()
                            +" vs Def: "+ game.getCombat().getDefenceValue() +" => " 
                            +  "eliminate defender unit");
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);

            break;
            
            case EventType.END_COMBAT:
                 //game.setInfoBarText("Combat Ended");
            break;    

            case EventType.ASSAULT_BEGINS:
                 game.setInfoBarText("Combat begins");
                 LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;

            case EventType.HOST_GAME_OVER:
            {
                game.setInfoBarText("Game over! " + game.getGuestPlayer().getName() + " wins by killing more than 4 units!");
                CustomDialogFactory.showConfirmationDialog("Game over" + game.getGuestPlayer().getName() + "wins by killing more than 4 units!");
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;
            }

            case EventType.GUEST_GAME_OVER:
                game.setInfoBarText("Game over! " + game.getHostPlayer().getName() + "wins by killing more than 4 enemy units!");
                CustomDialogFactory.showConfirmationDialog("Game over! " + game.getHostPlayer().getName() + "wins by killing more than 4 enemy units!");
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;

            case EventType.CARD_THERE_IS_NO_ROOM_FOR_MOVE:
                game.setInfoBarText("There is no room for move");
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
                break;
                
            case EventType.GUIRELLA_PLAYED:
                game.setInfoBarText("Guirellas has been played");
            break;   
            
            case EventType.PICK_COMMITTED_ATTACK_CASUALITIES:
                 game.setInfoBarText("Pick committed attack casualities");
            break;     
        
    }
    } 
    
}
