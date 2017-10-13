/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import java.io.Serializable;
import manouvre.game.Card;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.commands.CommandQueue;
import manouvre.game.CardCommandFactory;
import manouvre.game.Unit;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author xeon
 */
public class CardMultipleSelectionState implements CardInputState, Serializable{
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(CardMultipleSelectionState.class.getName());  
    
    
    @Override
    public void handleInput(Card card, Game game,CommandQueue cmdQueue) {
        
        if(!card.isSelected()) 
        {   
            card.setSelected(true);
            if(game.getPhase() == Game.DISCARD)
            {
            game.getCurrentPlayer().getHand().selectionSeq.add(card);
            }
            else if(game.getPhase() == Game.COMBAT)
            {
                if(game.getCombat() != null)
                    if(game.getCombat().getState() == Combat.PICK_DEFENSE_CARDS)
                    {
                        if(game.getCombat().getDefendingUnit() != null)
                            if(game.getUnit(game.getCombat().getDefendingUnit()).equals(card) )
                            {
                                game.getCombat().addDefenceCard(card);
                                //game.getCurrentPlayer().getHand().selectionSeq.add(card);

                            }
                            else if (card.canBePlayed(game))
                                card.actionOnSelection(game, cmdQueue);
                    }
                    else if (game.getCombat().getState() == Combat.PICK_SUPPORTING_CARDS)
                    {
                        /*
                        TODO , picking leader triggers choose dialog support vs attack mode
                        */
                        if(card.getCardType() == Card.LEADER)
                        {
                            game.getCardCommandFactory().setPlayingCard(card);
                            game.getCardCommandFactory().awakeObserver();
                            game.getCardCommandFactory().notifyObservers(CardCommandFactory.LEADER_SELECTED);
                            
                        }
                        if(card.getCardType() == Card.UNIT)
                        {
                            if(game.getCombat().getSupportingLeader() == null)
                            {
                                /*
                                TODO can pick only attacker additional cards and only ASSAULT 
                                */
                            Unit attackingUnit = game.getUnit(game.getCombat().getAttackingUnit());
                            if(card.equals(attackingUnit))
                                
                            {
                                for(String cardMode:card.getPlayingPossibleCardModes() )
                                {
                                    if(cardMode == Card.ASSAULT)
                                    {
                                        game.getCombat().addSupportCard(card);    
                                        break;
                                    }
                                }
                            }   
                            }
                            else 
                            {
                            /*
                                TODO  You can pick any from supporting unit <--> cards to suppport
                                */
                            }
                        
                        }
                       }
            }
                
            
        }
        else 
        {
            card.setSelected(false);
            if(game.getPhase() == Game.DISCARD)
            {
            
            game.getCurrentPlayer().getHand().selectionSeq.remove(card); 
            }
            
            else if(game.getPhase() == Game.COMBAT)
            {
                if(game.getCombat() != null)
                    if(game.getCombat().getState() == Combat.PICK_DEFENSE_CARDS)
                    {
                       if(game.getCombat().getDefendingUnit() != null)
                            if(game.getUnit(game.getCombat().getDefendingUnit()).equals(card) )
                            {
                                game.getCombat().removeDefenceCard(card);
                                //game.getCurrentPlayer().getHand().selectionSeq.add(card);

                            }
                            else if (card.canBePlayed(game))
                                 card.actionOnDeselection(game);
                            
                       
                    }
                    else if (game.getCombat().getState() == Combat.PICK_SUPPORT_UNIT)
                    {
                        /*
                        TODO , picking leader triggers choose dialog support vs attack mode
                        */
                        if(card.getCardType() == Card.LEADER)
                        {
                        /*
                        We have to select supporting units equal to the support value
                        */
                        LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na MapInputStateHandler.NOSELECTION");
                        game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
                        
                        if(game.getCombat().getDefenceCards().contains(card))
                            game.getCombat().removeSupportCard(card);
                        
                        if(game.getCombat().getSupportingLeader().equals(card))
                            game.getCombat().setSupportingLeader(null);
                        
                            
                        game.getCardCommandFactory().setPlayingCard(null);
                            
                        game.getCombat().calculateCombatValues();
 
                        }
                    else if (game.getCombat().getState() == Combat.PICK_SUPPORTING_CARDS)   
                        if(card.getCardType() == Card.UNIT)
                        {
                            if(game.getCombat().getSupportingLeader() == null)
                            {
                                /*
                                TODO can pick only attacker additional cards and only ASSAULT 
                                
                                */
                           Unit attackingUnit = game.getUnit(game.getCombat().getAttackingUnit());
                            if(card.equals(attackingUnit))
                                
                            {
                                for(String cardMode:card.getPlayingPossibleCardModes() )
                                {
                                    if(cardMode == Card.ASSAULT)
                                    {
                                        game.getCombat().removeSupportCard(card);    
                                        break;
                                    }
                                }
                            }   
                            }
                            else 
                            {
                            /*
                                TODO  You can pick any from supporting unit <--> cards to suppport
                                */
                            }
                        }

                    }

            }
     
        }       
        
        
    }

    @Override
    public String toString() {
        return "MULTIPLE_SELECTION";
    }
    
}
