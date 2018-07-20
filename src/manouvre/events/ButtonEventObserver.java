package manouvre.events;

import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.gui.GameWindow;
import manouvre.state.HandStateHandler;
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
    }
    @Override
    public void update(Observable o, Object arg) {
        String dialogType = (String) arg;
        LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);

        switch(dialogType){
        
            
            case EventType.SETUP_FINISHED:{
            
                if(game.getCurrentPlayer().isFinishedSetup())
                    buttonToNextPhaseMakeInvisible();
                break;
            
            }
                
            case EventType.PLAYER_MOVED:{
            
                buttonToNextPhaseSetText("End Move", game.getCurrentPlayer().isActive());
                break;
            }
          
            case EventType.CANCELLABLE_CARD_PLAYED: {
                /*
                Guirellas decision
                */
                if(game.getCurrentPlayer().isActive()){
                        buttonActionSetText("Accept Card", true);
                  
                }
                else 
                    {       
                        game.setInfoBarText("Opponnent can play Guirellas");
                        buttonActionMakeInvisible();
                    }
                break;
                }
            case EventType.THROW_DICE: {
                if( game.getCurrentPlayer().isActive() )
                   {
                       buttonActionSetText("Roll dices", true);
                   }
                
                else 
                {          
                game.setInfoBarText("Wait to see cannon balls");
                buttonActionMakeInvisible();
                }
            
            break;
            }
            case EventType.ASSAULT_BEGINS: {
                if( game.getCurrentPlayer().isActive() )
                   {
                       buttonActionSetText("Defend", true);
                   }
                
                else 
                {          
                game.setInfoBarText("Opponent is defending");
                buttonActionMakeInvisible();
                }
            
            break;
            }
            
            case EventType.DEFENDING_CARDS_PLAYED: {
                if( game.getCurrentPlayer().isActive() )
                   {
                       buttonActionSetText("Roll dices", true);
                   }
                
                else 
                {          
                game.setInfoBarText("Wait for attack!");
                buttonActionMakeInvisible();
                }
            
            break;
            }
            case EventType.LEADER_SELECTED:
           {
               if(game.getCurrentPlayer().isActive())
               {
                   if(!game.getPossibleSupportingUnits(game.getCombat().getDefendingUnit()).isEmpty())
                   {
                        buttonSetDecisionText("Command", "Combat Val");
                        game.setInfoBarText("Choose Leader playing mode");
                   }
                   else 
                   {
                        game.notifyAbout(EventType.LEADER_FOR_COMBAT);
                   }
               }       
               break;
           }
           
           case EventType.LEADER_DESELECTED:
           {
               if(game.getCurrentPlayer().isActive())
               {
                   game.setInfoBarText("");
                   decisionButtonsMakeInvisible();
              }       
               break;
           }
          case EventType.PICK_SUPPORT_UNIT: {
                if( game.getCurrentPlayer().isActive() )
                   {
                       buttonActionSetText("End picking", true);
                       game.setInfoBarText("Pick supporting units");
                   }
             break;
            }
         case EventType.DEFENDER_WITHDRAW:
           {
               if( game.getCurrentPlayer().isActive() )
               game.setInfoBarText("Pick position to withdraw");
               else {
                game.setInfoBarText("Opponent is thinking where to withdraw");
               }
               break;
           }  
          case EventType.PURSUIT: {
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
             break;
            } 
          
          case EventType.DEFENDER_DECIDES: {
                if( game.getCurrentPlayer().isActive() ){
                    game.setInfoBarText("Make decision");
                    buttonSetDecisionText("Withdraw", "Take Hit");
                }
                else 
                   game.setInfoBarText("Defender is choosing combat outcome"); 
                
             break;
            } 
          case EventType.ATTACKER_DECIDES:
           {
               if(game.getCurrentPlayer().isActive())
               {
                    game.setInfoBarText("Make decision");
                    buttonSetDecisionText("Withdraw", "Take Hit");
               }       
                else 
               {
                   game.setInfoBarText("Attacker is choosing combat outcome");
               }
               break;
           }
           case EventType.CARD_SELECTED:
           {
               if(game.getCurrentPlayer().isActive())
               {
                   if( game.getPhase() == Game.DISCARD)
                       if(!actionButton.isVisible())
                            buttonActionSetText("Discard", true);
               }       
               break;
           } 
           case EventType.CARD_DESELECTED:
           {
               if(game.getCurrentPlayer().isActive())
               {
                   if( game.getPhase() == Game.DISCARD)
                       if(!game.getCurrentPlayer().getHand().isAnyCardSelected())
                           buttonActionMakeInvisible();
               }       
               break;
           } 
           
           case EventType.NEXT_PHASE:
           {
               
            buttonNextPhaseSetText();
            actionButtonPhaseSetText();
            break;
           
           }
           case EventType.VOLLEY_ASSAULT_DECISION:
           {
               if(game.getCurrentPlayer().isActive())
               {
                   buttonSetDecisionText("Volley", "Assault");
                }       
               break;
           }
           
           case EventType.VOLLEY_ASSAULT_DECISION_DESELECTION:
               
           {
                decisionButtonsMakeInvisible();
                break;
           }
           
            case EventType.SKIRMISH_SELECTED:
               
           {
                 if(game.getCurrentPlayer().isActive())
               {
                   game.setInfoBarText("Move up to 2 spaces");
                   buttonActionSetText("Skirmish with no move", true);
              }  
           break;
           }
           
           case EventType.SKIRMISH_PLAYED:
               
           {
                 if(game.getCurrentPlayer().isActive())
               {
                   game.setInfoBarText("Move up to 2 spaces");
                   buttonActionSetText("Skirmish with no move", true);
              }  
                 else 
                  game.setInfoBarText("Opponent is moving up to 2 spaces");
           break;
           }
           
           
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
        actionButton.setVisible(true);
     }
     
     private void  buttonToNextPhaseSetText(String text, boolean isActive){
            
        toNextPhaseButton.setEnabled(isActive);
        toNextPhaseButton.setVisible(true);
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

     
     private void actionButtonPhaseSetText()
     {
      if(game.getCurrentPlayer().isActive()) 
               switch(game.getPhase()){
                
                case Game.DRAW:
                {
                  int handLimit = 5;
                  buttonActionSetText("Draw", (game.getCurrentPlayer().getHand().size()< handLimit) );
                  break;
                }
                case Game.RESTORATION:
                {
                 buttonActionSetText("Restore", false );
                 break;
                }
                default: {
                    buttonActionMakeInvisible();
                }
              }
     }
     
     
    private void buttonNextPhaseSetText() {
        {
            switch (game.getPhase()) {
                case Game.SETUP:
                    {
                        buttonToNextPhaseSetText("End setup", true);
                        break;
                    }
                case Game.DISCARD:
                    {
                        buttonToNextPhaseSetText("End discard", game.getCurrentPlayer().isActive());
                        break;
                    }
                case Game.DRAW:
                    {
                        buttonToNextPhaseSetText("End draw", (!game.getCurrentPlayer().hasDrawn() || 
                                         game.getCurrentPlayer().getHand().size() == 5) 
                                         && game.getCurrentPlayer().isActive());
                        break;
                    }
                case Game.MOVE:
                    {
                        buttonToNextPhaseSetText("End move", false
                        //game.getCurrentPlayer().hasMoved() && game.getCurrentPlayer().isActive() && (game.getCardHandler().getPlayingCard() == null)
                                                  );
                        break;
                    }
                case Game.COMBAT:
                    {
                        buttonToNextPhaseSetText("End combat", game.getCurrentPlayer().isActive()
                                //game.getCurrentPlayer().isActive() && ((game.getCombat() == null) ? true : game.getCombat().getState() == Combat.END_COMBAT)
                                  );
                        break;
                    }
                case Game.RESTORATION:
                    {
                        buttonToNextPhaseSetText("End turn",game.getCurrentPlayer().isActive());
                        break;
                    }
            }
        }
    }
             
    
}
