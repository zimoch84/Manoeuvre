package manouvre.events;

import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import manouvre.game.Game;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author piotr_grudzien
 */
public class ButtonEventObserver  implements Observer  {

private JButton actionButton;
private JButton yesButton;
private JButton noButton;
private JButton toNextPhaseButton;

private Game game;

private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(ButtonEventObserver.class.getName());

public ButtonEventObserver(Game game, JButton actionButton, JButton yesButton, JButton noButton, JButton toNextPhaseButton) {
    this.game = game;
    this.actionButton = actionButton;
    this.yesButton = yesButton;
    this.noButton = noButton;
    this.toNextPhaseButton = toNextPhaseButton;

    game.addObserver(this);
}
@Override
public void update(Observable o, Object arg) {
    String dialogType = (String) arg;
    switch(dialogType){
        case EventType.SETUP_FINISHED:
            if(game.getCurrentPlayer().isFinishedSetup())
                buttonToNextPhaseMakeInvisible();
                LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;
        case EventType.PLAYER_MOVED:
            buttonToNextPhaseSetText("End Move", game.getCurrentPlayer().isActive());
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;
        case EventType.CANCELLABLE_CARD_PLAYED: 
            /*Guirellas decision*/
            if(game.getCurrentPlayer().isActive())
            {
                buttonActionSetText("Accept Card", true);
                buttonToNextPhaseMakeInvisible();
            }
            else 
                {       
                game.setInfoBarText("Opponnent can play Guirellas");
                buttonActionMakeInvisible();
                }
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
            break;
        case EventType.COMBAT_THROW_DICE: 
            if( game.getCurrentPlayer().isActive() )
                buttonActionSetText("Roll dices", true);
            else 
            {          
                game.setInfoBarText("Wait to see cannon balls");
                buttonActionMakeInvisible();
            }
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
        case EventType.ASSAULT_BEGINS: 
            if( game.getCurrentPlayer().isActive() )
                   buttonActionSetText("Defend", true);
            else 
            {          
                game.setInfoBarText("Opponent is defending");
                buttonActionMakeInvisible();
                buttonToNextPhaseMakeInvisible();
            }
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
        case EventType.BOMBARD_BEGINS: 
            if( game.getCurrentPlayer().isActive() )
                buttonToNextPhaseMakeInvisible();
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
        case EventType.DEFENDING_CARDS_PLAYED: 
            if( game.getCurrentPlayer().isActive() )
                   buttonActionSetText("Roll dices", true);
            else 
            {          
                game.setInfoBarText("Wait for attack!");
                buttonActionMakeInvisible();
            }
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
        case EventType.LEADER_SELECTED:
           if(game.getCurrentPlayer().isActive())
            {
                    buttonSetDecisionText("Command", "Combat Val");
                    game.setInfoBarText("Choose Leader playing mode");
            }     
           LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;

       case EventType.LEADER_DESELECTED:
           if(game.getCurrentPlayer().isActive())
           {
               game.setInfoBarText("");
               decisionButtonsMakeInvisible();
               buttonActionSetText("Roll dices", true);
          }       
           LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
           break;
      case EventType.PICK_SUPPORT_UNIT: {
            if( game.getCurrentPlayer().isActive() )
               {
                   buttonActionSetText("End picking", true);
                   game.setInfoBarText("Pick supporting units");
               }
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
         break;
        }
    case EventType.COMBAT_DEFENDER_WITHDRAW:
        if( game.getCurrentPlayer().isActive() )
        game.setInfoBarText("Pick position to withdraw");
        else {
         game.setInfoBarText("Opponent is thinking where to withdraw");
        }
        LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
    break;
    case EventType.COMBAT_PURSUIT_STARTED: 
          if( game.getCurrentPlayer().isActive() ){
              if(game.getCombat().isAttackerNotRequiredToAdvance())
             {
                 buttonActionSetText("Not requre to adv.", true);
             }
              else 
              {
                  game.setInfoBarText("Pick Pursuit Unit");
              }
          }
          else 
             game.setInfoBarText("Opponent is picking pursuit unit"); 
          LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
    break;
      case EventType.COMBAT_DEFENDER_DECIDES: 
            if( game.getCurrentPlayer().isActive() ){
                game.setInfoBarText("Make decision");
                buttonSetDecisionText("Withdraw", "Take Hit");
            }
            else 
               game.setInfoBarText("Defender is choosing combat outcome"); 
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
         break;
      case EventType.COMBAT_ATTACKER_DECIDES:
           if(game.getCurrentPlayer().isActive())
           {
                game.setInfoBarText("Make decision");
                buttonSetDecisionText("Withdraw", "Take Hit");
           }       
            else 
           {
               game.setInfoBarText("Attacker is choosing combat outcome");
           }
           LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
           break;
       case EventType.CARD_SELECTED:
           if(game.getCurrentPlayer().isActive())
           {
               if( game.getPhase() == Game.DISCARD)
                   if(!actionButton.isVisible())
                        buttonActionSetText("Discard", true);
           }
           LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
           break;
       case EventType.CARD_DESELECTED:
           if(game.getCurrentPlayer().isActive())
           {
               if( game.getPhase() == Game.DISCARD)
                   if(!game.getCurrentPlayer().getHand().isAnyCardSelected())
                       buttonActionMakeInvisible();
           } 
           LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
           break;
       case EventType.NEXT_PHASE:
       case EventType.END_TURN:
        buttonNextPhaseSetText();
        actionButtonPhaseSetText();
        LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
       case EventType.VOLLEY_ASSAULT_DECISION:
           if(game.getCurrentPlayer().isActive())
               buttonSetDecisionText("Volley", "Assault");
           LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
           break;

       case EventType.VOLLEY_ASSAULT_DECISION_DESELECTION:
            decisionButtonsMakeInvisible();
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);    
            break;

        case EventType.SKIRMISH_SELECTED:
            if(game.getCurrentPlayer().isActive())
               {
               game.setInfoBarText("Move up to 2 spaces");
               buttonActionSetText("Skirmish with no move", true);
              }  
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);    
       break;

       case EventType.SKIRMISH_PLAYED:
            buttonActionMakeInvisible();
       break;
       
       case EventType.END_COMBAT:
           buttonNextPhaseSetText();
       break;
       case EventType.LEADER_END_PICKING_SUPPORT:
           buttonActionSetText("Roll dices", true);
       break;
       
           
           
}
}
private void buttonSetDecisionText(String yesOption, String noOption)
{
    yesButton.setText(yesOption);
    noButton.setText(noOption);
    yesButton.setVisible(true);
    noButton.setVisible(true);
    yesButton.setEnabled(true);
    noButton.setEnabled(true);
}
 private void  buttonActionSetText(String text, boolean isActive){

    actionButton.setText(text);
    actionButton.setEnabled(isActive);
    actionButton.setVisible(isActive);
 }

 private void  buttonToNextPhaseSetText(String text, boolean isActive){

    toNextPhaseButton.setEnabled(isActive);
    toNextPhaseButton.setVisible(isActive);
    toNextPhaseButton.setText(text);
 }


 private void buttonActionMakeInvisible(){

    actionButton.setText("");
    actionButton.setEnabled(false);
    actionButton.setVisible(false);
 }

private void buttonToNextPhaseMakeInvisible(){

    toNextPhaseButton.setText("");
    toNextPhaseButton.setEnabled(false);
    toNextPhaseButton.setVisible(false);
 }


private void decisionButtonsMakeInvisible()
{
   yesButton.setEnabled(false);
   yesButton.setVisible(false);
   noButton.setEnabled(false);
   noButton.setVisible(false);
}


private void actionButtonPhaseSetText(){

 if(game.getCurrentPlayer().isActive()) 
   switch(game.getPhase()){
       case Game.DRAW:
         int handLimit = 5;
         buttonActionSetText("Draw", (game.getCurrentPlayer().getHand().size()< handLimit) );
         break;
       case Game.RESTORATION:
        buttonActionSetText("Restore", false );
        break;
       default: 
           buttonActionMakeInvisible();
     }
     }
private void buttonNextPhaseSetText() {
        switch (game.getPhase()) {
            case Game.SETUP:
                    buttonToNextPhaseSetText("End setup", true);
                    break;
            case Game.DISCARD:
                    buttonToNextPhaseSetText("End discard", game.getCurrentPlayer().isActive());
                    break;
            case Game.DRAW:
                    buttonToNextPhaseSetText("End draw", (!game.getCurrentPlayer().hasDrawn() || 
                                     game.getCurrentPlayer().getHand().size() == 5) 
                                     && game.getCurrentPlayer().isActive());
                    break;
            case Game.MOVE:
                    buttonToNextPhaseSetText("End move", false);
                    break;
            case Game.COMBAT:
                    buttonToNextPhaseSetText("End combat", game.getCurrentPlayer().isActive()
                              );
                    break;
            case Game.RESTORATION:
                    buttonToNextPhaseSetText("End turn",game.getCurrentPlayer().isActive());
                    break;
        }
}
}
