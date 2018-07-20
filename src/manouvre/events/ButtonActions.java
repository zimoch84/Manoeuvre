/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.events;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import javax.swing.JButton;
import manouvre.commands.CardCommands;
import manouvre.commands.CommandQueue;
import manouvre.commands.DontAdvanceUnitCommand;
import manouvre.commands.EndSetupCommand;
import manouvre.commands.EndTurnCommand;
import manouvre.commands.ForceWithdraw;
import manouvre.commands.NextPhaseCommand;
import manouvre.commands.SetupPositionCommand;
import manouvre.commands.TakeHitCommand;
import manouvre.game.Card;
import manouvre.game.CardCommandFactory;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Unit;
import manouvre.gui.CustomDialog;
import manouvre.interfaces.Command;
import manouvre.state.HandStateHandler;
import manouvre.state.MapStateHandler;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author piotr_grudzien
 */
public class ButtonActions  extends Observable{
    
    private JButton actionButton;
    private JButton buttonYes;
    private JButton buttonNo;
    
    private Game game;
    
    private CommandQueue cmdQueue;
    CardCommandFactory ccf;

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ButtonActions.class.getName());

    public ButtonActions(JButton actionButton, JButton yesButton, JButton noButton, Game game, CommandQueue cmdQueue, CardCommandFactory ccf) {
        this.actionButton = actionButton;
        this.buttonYes = yesButton;
        this.buttonNo = noButton;
        this.game = game;
        this.cmdQueue = cmdQueue;
        this.ccf = ccf;
    }

    /*
    Take hit button
     */
    public void buttonNoActionPerformed(ActionEvent evt) {
        switch (buttonNo.getText()) {
            case "Take Hit":
                {
                    TakeHitCommand th = new TakeHitCommand(game.getCurrentPlayer().getName(), game.getCombat().getDefendingUnit(), false);
                    cmdQueue.storeAndExecuteAndSend(th);
                    break;
                }
        /*Add value of leader as supporting unit*/
            case "Combat Val":
                {
                    game.notifyAbout(EventType.LEADER_FOR_COMBAT);
                    break;
                }
            case "Assault":
                {
                    game.notifyAbout(EventType.CARD_ASSAULT_MODE);
                    break;
                }
        }
        buttonDecisionDisappear();
    }

    public void buttonYesActionPerformed(ActionEvent evt) {
        switch (buttonYes.getText()) {
            case "Withdraw":
                    /*
                    If we have where to retreat
                     */
                    if (game.positionCalculator.getRetreatPositions(game.getUnit(game.getCombat().getDefendingUnit())).size() > 0) 
                    {
                        /*
                        Defending player
                         */
                        if (!game.getCurrentPlayer().hasAttacked()) {

                            game.getUnit(game.getCombat().getDefendingUnit()).setRetriving(true);
                            game.getUnit(game.getCombat().getDefendingUnit()).setSelected(true);
                            game.getCombat().setState(Combat.WITHRDAW);
                            game.notifyAbout(EventType.DEFENDER_WITHDRAW);
                        }
                        /*
                        Attacking player
                         */ 
                        else {
                            /*Force withdraw command*/
                            ForceWithdraw fw = new ForceWithdraw(game.getCurrentPlayer().getName(), game.getCombat().getDefendingUnit());
                            cmdQueue.storeAndExecuteAndSend(fw);
                        }
                    } 
                    /*dont have position to withdraw */
                    else {
                        TakeHitCommand th = new TakeHitCommand(game.getCurrentPlayer().getName(), game.getCombat().getDefendingUnit(), true);
                        cmdQueue.storeAndExecuteAndSend(th);
                    }
                    break;
            /*
            Playing leader for his command attribute
             */
            case "Command":
                    game.notifyAbout(EventType.PICK_SUPPORT_UNIT);
            break;
            case "Volley":
                game.notifyAbout(EventType.CARD_VOLLEY_MODE);
            break;
        }
        buttonDecisionDisappear();
    }

    public void buttonDecisionDisappear() {
        buttonYes.setVisible(false);
        buttonNo.setVisible(false);
        buttonYes.setEnabled(false);
        buttonNo.setEnabled(false);
        
    }

    public void buttonActionPerformed(ActionEvent evt) {
        /*
        Play action based on current button description
         */
        switch (actionButton.getText()) {
            case "Play Card":
                {
                    //TODO remove this option
                    //gameGui.playSelectedCard();
                   
                    break;
                }
            case "Undo":
                cmdQueue.undoLastCommand();
                break;
            case "Accept Card":
                cmdQueue.storeAndExecuteAndSend(ccf.createDoNotRejectCardCommand());
                break;
            case "Not requre to adv.":
                {
                    DontAdvanceUnitCommand notReq2AdvCommand = new DontAdvanceUnitCommand(game.getCurrentPlayer().getName());
                    cmdQueue.storeAndExecuteAndSend(notReq2AdvCommand);
                    break;
                }
            case "Defend":
                {
                    Command defendCommand = new CardCommands.DefendCommand(game.getCurrentPlayer().getName(), game.getCombat());
                    cmdQueue.storeAndExecuteAndSend(defendCommand);
                    break;
                }
            case "Roll dices":
                {
                    Command combatOutcome = ccf.createOutcomeCombatCommand();
                    cmdQueue.storeAndExecuteAndSend(combatOutcome);
                    break;
                }
            case "End picking":
                {
                    game.getCombat().setState(Combat.PICK_SUPPORT_CARDS);
                    game.notifyAbout(EventType.LEADER_END_PICKING_SUPPORT);
                    actionButton.setText("Roll dices");
                    break;
                }
            case "Discard":
                {
                    cmdQueue.storeAndExecuteAndSend(ccf.createDiscardCommand());
                    break;
                }
            case "Draw":
                {
                    cmdQueue.storeAndExecuteAndSend(ccf.createDrawCommand());
                    break;
                }
            default:
                {
                    LOGGER.debug(game.getCurrentPlayer().getName() + "Akcja buttona bez obslugi");
                }
        }
        buttonActionMakeInvisible();
    }




    public void buttonToNextPhaseActionPerformed(ActionEvent evt) {
        /*
        IF setup then ask for confirmation
         */
        switch (game.getPhase()) {
            case Game.SETUP:
                /*
                Validate setup army posiotion
                */
                Unit badPlacedUnit = game.validateArmySetup(game.getCurrentPlayer());
                if (badPlacedUnit == null) {
                    /*
                    Setting end setup flag and after confirmation dialog
                    */
                    SetupPositionCommand setupCommand = new SetupPositionCommand(game.getCurrentPlayer().getName(), new ArrayList<Unit>(Arrays.asList(game.getCurrentPlayer().getArmy())));
                    EndSetupCommand endSetupCommand = new EndSetupCommand(game.getCurrentPlayer().getName(), setupCommand);
                    CustomDialog dialog = new CustomDialog(CustomDialog.YES_NO_TYPE, "Are You sure to end setup?", cmdQueue, game);
                    dialog.setOkCommand(endSetupCommand);
                } 
                else {
                    CustomDialog dialog = new CustomDialog(CustomDialog.CONFIRMATION_TYPE, "Unit " + badPlacedUnit.getName() + " is placed wrong", cmdQueue, game);
                }   break;
            case Game.DISCARD:
                {
                    if (game.getCurrentPlayer().getHand().size() < 5 ) {
                        cmdQueue.storeAndExecuteAndSend(ccf.createDrawCommand());
                    }       
                    Command nextPhaseCommand = new NextPhaseCommand(game.getCurrentPlayer().getName(), game.getPhase() + 1);
                    cmdQueue.storeAndExecuteAndSend(nextPhaseCommand);
                    break;
                }
            case Game.DRAW:
                if (game.getCurrentPlayer().getHand().size() < 5) {
                    Command drawCommand = ccf.createDrawCommand();
                    cmdQueue.storeAndExecuteAndSend(drawCommand);
                }
                
                Command nextPhaseCommand = new NextPhaseCommand(game.getCurrentPlayer().getName(), game.getPhase() + 1);
                cmdQueue.storeAndExecuteAndSend(nextPhaseCommand);
                break;
            
            case Game.MOVE:{
                  cmdQueue.storeAndExecuteAndSend(new NextPhaseCommand(game.getCurrentPlayer().getName(), game.getPhase() + 1));
                  break;
            }  
            case Game.COMBAT:
            {
                 cmdQueue.storeAndExecuteAndSend(new NextPhaseCommand(game.getCurrentPlayer().getName(), game.getPhase() + 1));
                 break;
            }
                
            case Game.RESTORATION:
                Command endTurnCommand = new EndTurnCommand(game.getCurrentPlayer().getName());
                cmdQueue.storeAndExecuteAndSend(endTurnCommand);
                break;
            default:
                {
                    LOGGER.error("There is no such phase " + game.getPhase());
                    break;
                }
        }
        
    }
    
    private void buttonActionMakeInvisible(){
     
        actionButton.setText("");
        actionButton.setEnabled(false);
        actionButton.setVisible(false);
     }
    
}
