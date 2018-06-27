/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.events;

import java.util.Observable;
import java.util.Observer;
import javax.swing.JButton;
import manouvre.game.CardCommandFactory;
import manouvre.game.Game;
import manouvre.state.CardStateHandler;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author piotr_grudzien
 */
public class EventObserver  implements Observer  {

    private JButton actionButton;
    private JButton yesButton;
    private JButton noButton;
    private Game game;

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(EventObserver.class.getName());
    
    public EventObserver(Game game, JButton actionButton, JButton yesButton, JButton noButton) {
        
        
        this.game = game;
        this.actionButton = actionButton;
        this.yesButton = yesButton;
        this.noButton = noButton;
   
    }
    
    
    @Override
    public void update(Observable o, Object arg) {
        
     
        if(o instanceof CardCommandFactory){
        CardCommandFactory ccmdf = (CardCommandFactory) o;
        String dialogType = (String) arg;
        
        LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        
        switch(dialogType){
        
            case EventType.CARD_HAS_BEEN_PLAYED: {
                /*
                Guirellas decision
                */
                if(game.getCurrentPlayer().isActive()){
                    game.cardStateHandler.setState(CardStateHandler.PICK_ONLY_ONE);
                    if (game.getCardCommandFactory().getOpponentCard() != null)
                    {
                        buttonActionSetText("Accept Card", true);
                    }
                }
                else 
                {       game.setInfoBarText("Opponnent can play Guirellas");
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
                       /*
                       Leader is played for his attack Value
                       */
                       game.getCombat().addSupportCard(game.getCardCommandFactory().getPlayingCard());
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
            if(game.getCurrentPlayer().isActive()) 
               switch(game.getPhase()){
                case Game.DISCARD:
                {
                 buttonActionSetText("Discard", false);
                 break;
                }
                case Game.DRAW:
                {
                  buttonActionSetText("Draw", (game.getCurrentPlayer().getHand().size()< 5) );
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
     
     private void buttonActionMakeInvisible(){
     
            actionButton.setText("");
            actionButton.setEnabled(false);
            actionButton.setVisible(false);
     }
     
     private void decisionButtonsMakeInvisible()
     {
                    yesButton.setEnabled(false);
                   yesButton.setVisible(false);
                   noButton.setEnabled(false);
                   noButton.setEnabled(false);
     }
             
    
}
