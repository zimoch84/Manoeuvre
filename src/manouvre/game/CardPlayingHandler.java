/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import manouvre.commands.CommandQueue;
import manouvre.commands.RestoreUnitByLeaderCommand;
import manouvre.events.EventType;
import manouvre.gui.CustomDialogFactory;
import manouvre.interfaces.Command;
import manouvre.state.MapStateHandler;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author piotr_grudzien
 */
public class CardPlayingHandler extends Observable implements  Serializable, Observer  {
    
    Game game;
    CommandQueue cmdQueue;
    CardCommandFactory ccf;
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(CardPlayingHandler.class.getName()); 

    private Card playingCard;
    private Card opponentCard;

    private MapStateHandler mapStateHandler;
    
    public CardPlayingHandler(Game game, MapStateHandler mapStateHandler) {
        this.game = game;
        this.mapStateHandler = mapStateHandler;
        this.playingCard = new Card();
                
    }
    
    public boolean canBePlayed(Card card) {
        return canBePlayedByPhase(card);
    }
    
    public void actionOnSelection(Card card) {
               actionOnSelectionByPhase(card);
           
    }
    public void actionOnDeselection(Card card) {
         actionOnDeselectionByPhase(card);
         
    }
    
    private void actionOnSelectionByPhase(Card card){
    
        switch(game.getPhase()){
        
            case Game.SETUP:
                break;
                        
            case Game.DISCARD:    
                card.setSelected(true);
                game.notifyAbout(EventType.CARD_SELECTED);
                break;
            case Game.DRAW:
                //TODO handle ScountCard;
              break;
            
            case Game.MOVE:
                actionSelectionCardInMove(card);
                mapStateHandler.setStateByCard(card, MapStateHandler.CHOOSE_BY_CARD);
                break;
            
            case Game.COMBAT:
                actionSelectionCardInCombat(card);
                mapStateHandler.setStateByCard(card, MapStateHandler.CHOOSE_BY_CARD);
                break;
                
            case Game.RESTORATION:
                actionSelectionCardInRestoration(card);
                mapStateHandler.setStateByCard(card, MapStateHandler.CHOOSE_BY_CARD);
                break;
                
        }
    
    }
    
    private void actionSelectionCardInMove(Card card){
    
        switch (card.getHQType()) {
        case Card.FORCED_MARCH:
            game.getCurrentPlayer().getLastMovedUnit().setSelected(true);
           
            break;
        case Card.SUPPLY:
        case Card.NO_CARD:
            break;
            
        }        
    
    }
    
    private void actionSelectionCardInCombat(Card card){
    
    Combat combat = game.getCombat();
    switch (card.getType()) {
        case Card.HQCARD:
                
                switch (card.getHQType()) {
                case Card.WITHDRAW:
                        if (combat.getDefendingUnit() != null) 
                        {
                            game.getUnit(combat.getDefendingUnit()).setSelected(true);
                            game.getUnit(combat.getDefendingUnit()).setRetriving(true);
                            combat.setState(Combat.WITHRDAW);
                        }
                        break;
                case Card.GUERRILLAS:
                        cmdQueue.storeAndExecuteAndSend(ccf.createGuerrillaCardCommand(card));
                        break;
                
                case Card.SKIRMISH:
                        handleCombatPickSupportCardsOnSelection(card, game);
                        break;
                }
        break;   
        case Card.UNIT:
            
            switch(combat.getState()){
                case Combat.COMBAT_NOT_INITIALIZED:
                    handleCombatInitializationOnSelection(card, game);
                    break;
                    
                case Combat.PICK_DEFENSE_CARDS:
                    
                    if(game.getUnit(combat.getDefendingUnit()).equals(card) )
                        combat.addDefenceCard(card);
                    break;
                   
                case Combat.PICK_SUPPORT_CARDS:
                        handleCombatPickSupportCardsOnSelection(card, game);
                        break;  
                        
            }
            break;
                
            case Card.LEADER:
                switch(combat.getState()){
                    case Combat.PICK_DEFENSE_CARDS:
                        combat.addDefenceCard(card);
                        break;
                    case Combat.PICK_SUPPORT_CARDS:
                        handleCombatPickSupportCardsOnSelection(card, game);
                        break;    
                    }
             break;
                
    }
    }
    
    private void actionSelectionCardInRestoration(Card card){
        switch (card.getType()) {
            case Card.UNIT:
                 game.getCurrentPlayerUnitByCard(card).setSelected(true);  
            break;
            case Card.HQCARD:
                switch(card.getHQType()){
                    case Card.SUPPLY:
                        setPlayingCard(card);
                    break;    
                }
            case Card.LEADER:
                ArrayList<Unit> injuredUnits = game.getCurrentPlayerInjuredUnits();
                Command restoreByLeader = new RestoreUnitByLeaderCommand(game.getCurrentPlayer().getName(), injuredUnits, card);
                CustomDialogFactory.showSureToPlayCardDialog(cmdQueue, restoreByLeader, game);
                break;
    }
    }
  
    private void actionOnDeselectionByPhase(Card card){
    switch(game.getPhase()){
        
            case Game.SETUP:
                break;
                        
            case Game.DISCARD:    
                card.setSelected(false);
                game.notifyAbout(EventType.CARD_DESELECTED);
                break;
            case Game.DRAW:
                //TODO handle ScountCard;
                break;
            
            case Game.MOVE:
                actionDeselectionCardInMove(card);
                mapStateHandler.setState(MapStateHandler.NOSELECTION);
                break;
            
            case Game.COMBAT:
                actionDeselectionCardInCombat(card);
                mapStateHandler.setState(MapStateHandler.NOSELECTION);
                break; 
            case Game.RESTORATION:
                actionDeselectionCardInRestoration(card);
                mapStateHandler.setState(MapStateHandler.NOSELECTION);
                break;
        }
    }
    
    private void actionDeselectionCardInMove(Card card){
    
         switch (card.getHQType()) {
        case Card.FORCED_MARCH:
            game.getCurrentPlayer().getLastMovedUnit().setSelected(false);
            mapStateHandler.setState( MapStateHandler.NOSELECTION);
            break;
        case Card.SUPPLY:
            game.unselectAllUnits();
            mapStateHandler.setState( MapStateHandler.NOSELECTION);
            break;
               
        case Card.NO_CARD:
            break;
            
        }        
    }
    
    private void actionDeselectionCardInCombat(Card card){
    
        
    switch (card.getType()) {
        case Card.HQCARD:
            switch (card.getHQType()) {
                    case Card.WITHDRAW:
                            if (game.getCombat().getDefendingUnit() != null) {
                                game.getUnit(game.getCombat().getDefendingUnit()).setSelected(false);
                                game.getUnit(game.getCombat().getDefendingUnit()).setRetriving(false);
                                setPlayingCard(null);
                                game.getCombat().setState(Combat.PICK_DEFENSE_CARDS);
                            }
                            break;
                        case Card.SKIRMISH:
                            card.setSelected(false);
                            setPlayingCard(new Card());
                            game.notifyAbout(EventType.SKIRMISH_DESELECTED);
                        break;
            
            }
        break;
        case Card.UNIT:
            switch(game.getCombat().getState()){
                case Combat.INITIALIZING_COMBAT:
                    handleCombatInitializationOnDeselection(card, game);
                break;
                case Combat.PICK_DEFENSE_CARDS:
                    game.getCombat().removeDefenceCard(card);
                break;
    
                case Combat.PICK_SUPPORT_CARDS:   
                    handleCombatPickSupportCardsOnDeselection(card, game);
                break;    
                }
            break;
            case Card.LEADER:
                switch(game.getCombat().getState()){
                     case Combat.PICK_DEFENSE_CARDS:
                         game.getCombat().removeDefenceCard(card);
                     break;    

                     case Combat.PICK_SUPPORT_CARDS:
                         handleCombatPickSupportCardsOnDeselection(card, game);
                     break;

                     case Combat.PICK_SUPPORT_UNIT:
                         handleLeaderDeselectedInCombat(card, game);
                     break;    
                 }
            break;
            case Card.NO_CARD:
                 LOGGER.debug("Blad wywolania pustej karty w metodzie actionOnDeselection");
            break;     
            
    }
    }

    private void actionDeselectionCardInRestoration(Card card){
    switch (card.getType()) {
            case Card.HQCARD:
                 switch (card.getHQType()) {
                    case Card.REDOUBDT:
                         mapStateHandler.setState(MapStateHandler.NOSELECTION);
                    break;
                    
                    case Card.SUPPLY:
                         mapStateHandler.setState(MapStateHandler.NOSELECTION);
                    break;
                }
            break;     
            case Card.UNIT:
                   
                     if (game.getPhase() == Game.RESTORATION) {
                        game.getCurrentPlayerUnitByCard(card).setSelected(false);
                    }
            break;
            case Card.LEADER:
                mapStateHandler.setState(MapStateHandler.NOSELECTION);
                break;
     }
    }
    
    private boolean canBePlayedByPhase(Card card){
    
        switch(game.getPhase())
        {
            case Game.SETUP:
                 return false; 
            case Game.DISCARD:
                return true;
            case Game.DRAW:
                if(card.getHQType() == Card.SCOUT ) 
                       return true;
                   else return false;
            case Game.MOVE:
                return canCardBePlayedInMove(card);
            
            case Game.COMBAT:
                return canCardBePlayedInCombat(card);
                
            case Game.RESTORATION:
               return canCardBePlayedInRestoration(card);
                
        }
        return false;
    }
    
    private boolean canCardBePlayedInMove(Card card){
    if(card.isHQCard())
        switch(card.getHQType())
        {
          case Card.FORCED_MARCH:
          case Card.SUPPLY:
              return game.getCurrentPlayer().hasMoved();
                        
          case Card.GUERRILLAS:
              Card opponentPlayedCard = game.getOpponentPlayer().getTablePile().getLastCard(false);
                return opponentPlayedCard.getHQType() == Card.FORCED_MARCH 
                        || opponentPlayedCard.getHQType() == Card.SUPPLY
                        || opponentPlayedCard.getHQType() == Card.REDOUBDT;
              
          default: return false;

        }
      else return false;
    }       
    
     private boolean canCardBePlayedInCombat(Card card){
       
           switch(game.getCombat().getState())  
       {
           case Combat.COMBAT_NOT_INITIALIZED:
           case Combat.INITIALIZING_COMBAT:    
                if(card.getType() == Card.UNIT)
                {
                    if(!card.getPlayingCardMode().equals(Card.NO_TYPE))
                    {
                        boolean isNotEliminated =  !game.getUnitByCard(card).isEliminated();                   
                        //boolean isThereValidTargets = !game.positionCalculator.getUnitsPositionToSelectByCard(card).isEmpty();
                        return isNotEliminated;
                    }
                    else 
                    {
                        boolean isNotEliminated =  !game.getUnitByCard(card).isEliminated();                   
                        return isNotEliminated;
                    }
                        
                }    
                if(card.getHQType() == Card.AMBUSH)
                    return !game.getCurrentPlayer().hasAttacked();
                
           break;     
           case Combat.PICK_SUPPORT_CARDS:
               switch(card.getType()){
                case Card.HQCARD:
                    switch(card.getHQType()){
                        case Card.SKIRMISH:
                        case Card.COMMITED_ATTACK:
                        case Card.ROYAL_ENG:
                        case Card.FRENCH_SAPPERS:
                            return true;
                        default: return false;    
                    }
                case Card.LEADER:
                    return true;
                case Card.UNIT:
                    return checkIfSupportUnitMatchesCardWithAssaultMode(card, game);
                    
               }
           break;
           case Combat.WITHRDAW:
           case Combat.PICK_DEFENSE_CARDS:
                switch(card.getType()){
                    case Card.HQCARD:
                        switch(card.getHQType()){
                            case Card.WITHDRAW:
                                boolean canBePlayed = !game.positionCalculator.getRetreatPositions(game.getCombat().getDefendingUnit()).isEmpty();
                                if(!canBePlayed)
                                      CustomDialogFactory.showThereIsNoRoomToWithdraw();
                                return canBePlayed;
                            default: return false;    
                        }
                    case Card.LEADER:
                        return true;
                    case Card.UNIT:
                        return game.getCombat().getDefendingUnit().equals(card);
                }
           break;    
           case Combat.PICK_SUPPORT_UNIT:
               switch(card.getType()){
                case Card.LEADER:
                    return true;
                case Card.UNIT:
                    return false;
               }
           break;    
           default: return false;    
       }
       return false;    
    }
    private boolean checkIfSupportUnitMatchesCardWithAssaultMode(Card card, Game game){
        /*
        Check if support unit matches card with assault mode
         */
        for (Unit supportUnit : game.getCombat().getSupportingUnits()) {
            if (supportUnit.equals(card)) {
                for (String cardMode : card.getPlayingPossibleCardModes()) {
                    if (cardMode == Card.ASSAULT) {
                        return true;
                    }
                }
            }
        }
        /*
        Check if attack unit matches card with assault mode
         */
        if (game.getCombat().getAttackingUnit().equals(card)) {
            for (String cardMode : card.getPlayingPossibleCardModes()) {
                if (cardMode == Card.ASSAULT) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean canCardBePlayedInRestoration(Card card){
    
        switch(card.getType()){
                case Card.HQCARD:
                    switch(card.getHQType()){
                        case Card.REDOUBDT:
                            return true;
                        case Card.SUPPLY:
                            return game.checkIfCurrentPlayerHasAnyUnitInjured();
                        default: return false;    
                    }
                case Card.LEADER:
                    return game.checkIfCurrentPlayerHasAnyUnitInjured();
                case Card.UNIT:
                     return game.getUnitByCard(card).isInjured(); 
                default: 
                    return false;        
        }
    }

 

    private void handleCombatInitializationOnSelection(Card card , Game game){
    
            Unit attackingUnit = game.getUnitByCard(card);
            attackingUnit.setSelected(true);
            Combat combat = game.getCombat();
            /*
            Volley Decision
             */
            int playingModeCounter = card.getPlayingPossibleCardModes().size();
            /*
            Set bombard or asssault
             */
            if (playingModeCounter == 1) {
                card.setPlayingCardMode(card.getPlayingPossibleCardModes().get(0));
                combat.setState(Combat.INITIALIZING_COMBAT);
                Terrain attackingTerrain = game.getMap().getTerrainAtPosition(attackingUnit.getPosition());

                combat.setAttackingUnit(attackingUnit);
                combat.setAttackTerrain(attackingTerrain);
                combat.setInitAttackingCard(card);
                combat.calculateCombatValues();
                ArrayList<Position> attackingPositions = game.positionCalculator.getAttackingPositions(card, attackingUnit);
                combat.setAttackingPositions(attackingPositions);

            }
            /*
            Notify to pick proper card mode
             */ else if (playingModeCounter == 2) {
                game.notifyAbout(EventType.VOLLEY_ASSAULT_DECISION);
            }
            /*
            If we know what mode is playing we can calculate attacking positions
             */
            
    
    }
    
    private void handleCombatInitializationOnDeselection(Card card, Game game){
        game.getUnitByCard(card).setSelected(false);
        int playingModeCounter = card.getPlayingPossibleCardModes().size();
        if (playingModeCounter == 2) {
            game.notifyAbout(EventType.VOLLEY_ASSAULT_DECISION_DESELECTION);
        }
        
        card.setPlayingCardMode(Card.NO_TYPE);
        game.getCombat().resetCombat(); 
       
    }
                
    private void handleCombatPickSupportCardsOnSelection(Card card, Game game){
        
        switch(card.getType()){
        case  Card.LEADER:
            card.setSelected(true);
            setPlayingCard(card);
            Combat combat = game.getCombat();
            Unit attackingUnit = combat.getAttackingUnit();
            Unit defendingUnit = combat.getDefendingUnit();
            /*If there is no possible supporting units -add just leader combat value to support combat*/
            if(game.getPossibleSupportingUnits(defendingUnit, attackingUnit).isEmpty()){
                
                game.getCombat().addSupportCard(card);
               
                }
            else
                game.notifyAbout(EventType.LEADER_SELECTED);
        
        break;
        case Card.UNIT:
            if(game.getCombat().getSupportingLeader() == null)
                checkIfCardMatchesAttackingUnitAndAddSupport(game, card);
            /*
            We have leader selected and supporting unit picked
            */
            else 
                checkIfCardMatchesSupportingUnitAndAddSupport(game, card);  
        break;
        
        case Card.HQCARD:
            switch(card.getHQType()){
            
                case Card.FRENCH_SAPPERS:
                case Card.ROYAL_ENG:    
                    game.getCombat().addSupportCard(card);
                break;  
                case Card.SKIRMISH:
                    card.setSelected(true);
                    setPlayingCard(card);
                    game.notifyAbout(EventType.SKIRMISH_SELECTED);
                default:
                    System.err.println("manouvre.game.CardPlayingHandler.handleCombatPickSupportCardsOnSelection() Nie ma obsługi tej karty"  + card.getCardName());
            }
            
        break;    
       }
   }

    private void handleCombatPickSupportCardsOnDeselection(Card card, Game game){
        switch(card.getType()){
            case Card.LEADER:
        
                handleLeaderDeselectedInCombat(card, game);
            break;
            
            case Card.HQCARD:
                switch(card.getHQType()){
                    
                    case Card.SKIRMISH :
                        card.setSelected(false);
                        setPlayingCard(new Card());
                        game.notifyAbout(EventType.SKIRMISH_DESELECTED);
                    break;
                    case Card.ROYAL_ENG:
                    case Card.FRENCH_SAPPERS:
                        game.getCombat().removeSupportCard(card);
                    break;    
                    
                }
            break;
            case Card.UNIT:
                if(game.getCombat().getSupportingLeader() == null)
                    checkIfCardMatchesAttackingUnitAndRemoveSupport(game, card);
                else 
                    checkIfCardMatchesSupportingUnitAndRemoveSupport(game, card);
            break;    
        }    
    }
    
    private void checkIfCardMatchesAttackingUnitAndAddSupport(Game game, Card card)
    {
            Unit attackingUnit = game.getUnit(game.getCombat().getAttackingUnit());
            if(card.equals(attackingUnit))
            {
                for(String cardMode:card.getPlayingPossibleCardModes() )
                {
                    if(cardMode == Card.ASSAULT)
                    {
                        card.setPlayingCardMode(Card.ASSAULT);
                        game.getCombat().addSupportCard(card); 
                        card.setSelected(true);
                        break;
                    }
                }
            }   
     }
     private void checkIfCardMatchesAttackingUnitAndRemoveSupport(Game game, Card card)
    {
            Unit attackingUnit = game.getUnit(game.getCombat().getAttackingUnit());
            if(card.equals(attackingUnit))
            {
                for(String cardMode:card.getPlayingPossibleCardModes() )
                {
                    if(cardMode == Card.ASSAULT)
                    {
                        card.setPlayingCardMode(Card.ASSAULT);
                        game.getCombat().removeSupportCard(card); 
                        card.setSelected(false);
                        break;
                    }
                }
            }   
     }
    
    private void checkIfCardMatchesSupportingUnitAndAddSupport(Game game, Card card){
            ArrayList<Unit> supportingUnits = game.getCombat().getAttackingUnits();
             for(Unit unit:supportingUnits){
                 if(card.equals(unit))
                 {
                     for(String cardMode:card.getPlayingPossibleCardModes() )
                     {
                         if(cardMode == Card.ASSAULT)
                         {
                             card.setPlayingCardMode(Card.ASSAULT);
                             game.getCombat().addSupportCard(card); 
                             break;
                         }
                     }
                 } 
             }
    }
    
    private void checkIfCardMatchesSupportingUnitAndRemoveSupport(Game game, Card card){
            ArrayList<Unit> supportingUnits = game.getCombat().getAttackingUnits();
             for(Unit unit:supportingUnits){
                 if(card.equals(unit))
                 {
                     for(String cardMode:card.getPlayingPossibleCardModes() )
                     {
                         if(cardMode == Card.ASSAULT)
                         {
                             card.setPlayingCardMode(Card.ASSAULT);
                             game.getCombat().removeSupportCard(card); 
                             break;
                         }
                     }
                 } 
             }
    }

        
 private void handleLeaderDeselectedInCombat(Card card, Game game) {
    Combat combat = game.getCombat();
    if(!combat.getSupportingUnits().isEmpty())
    {
        for(Unit supportingUnit: combat.getSupportingUnits())
        {
            game.getUnit(supportingUnit).setSupporting(false);
        }
        combat.resetSupport();
    }
    if(combat.getSupportCards().contains(card))
        combat.removeSupportCard(card);
    
    combat.setState(Combat.PICK_SUPPORT_CARDS);
    game.notifyAbout(EventType.LEADER_DESELECTED);
 }
    public Card getPlayingCard() {
        if(playingCard!=null)
        return playingCard;
        else return new Card();
    }

    public void setPlayingCard(Card playingCard) {
        this.playingCard = playingCard;
    }

    public void resetPlayingCard(Card card) {
        if (playingCard != null) {
            actionOnDeselection(card);
            if (playingCard != null) {
                playingCard.setSelected(false);
            }
            playingCard = null;
        }
    }
    
    public Card getOpponentCard() {
        return opponentCard;
    }
    //TODO czy to potrzebne?
    public void setOpponentCard(Card opponentCard) {
        this.opponentCard = opponentCard;
    }
@Override
public void update(Observable o, Object arg) {

    String dialogType = (String) arg;

    switch(dialogType){
        case  EventType.CARD_MOVED_TO_TABLE:
            if(game.getCurrentPlayer().isActive())
                setOpponentCard(game.getOpponentPlayer().getTablePile().getLastCard(false));
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
        case EventType.CARD_ACCEPTED:
            if(game.getCurrentPlayer().isActive())
                setPlayingCard(new Card());
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);  
        break;
        case EventType.LEADER_FOR_COMBAT:
            game.getCombat().addSupportLeader4Combat(playingCard);
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
        case EventType.CARD_ASSAULT_MODE:
            playingCard.setPlayingCardMode(Card.ASSAULT);
            game.getCombat().setAttackingUnit(game.getUnitByCard(playingCard));
            game.getCombat().setInitAttackingCard(playingCard);
            game.getCombat().setAttackingPositions(game.positionCalculator.getAttackingPositions(playingCard, game.getUnitByCard(playingCard)));
            game.getCombat().setState(Combat.INITIALIZING_COMBAT);
            mapStateHandler.setStateByCard(playingCard, MapStateHandler.CHOOSE_BY_CARD);
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
        case EventType.CARD_VOLLEY_MODE:
            playingCard.setPlayingCardMode(Card.VOLLEY);
            game.getCombat().setAttackingUnit(game.getUnitByCard(playingCard));
            game.getCombat().setInitAttackingCard(playingCard);
            game.getCombat().setAttackingPositions(game.positionCalculator.getAttackingPositions(playingCard, game.getUnitByCard(playingCard)));
            game.getCombat().setState(Combat.INITIALIZING_COMBAT);
            mapStateHandler.setStateByCard(playingCard, MapStateHandler.CHOOSE_BY_CARD);
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
        case EventType.PICK_SUPPORT_UNIT:
            /*We have to select supporting units equal to the support value*/
            game.getCombat().setState(Combat.PICK_SUPPORT_UNIT);
            game.getCombat().setSupportingLeader(getPlayingCard());
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;

        /*
        TODO nie potrzebuje tego?
        */
        case EventType.REDOUBT_PLAYED:
            setPlayingCard(new Card());
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;

        case EventType.CARD_DESELECTED:
        case EventType.NEXT_PHASE:
        case EventType.END_TURN:    
            if(game.getCurrentPlayer().isActive())
             setPlayingCard(new Card());
            LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);
        break;
        
        case EventType.SUPPLY_SELECTED:
                mapStateHandler.setStateByCard(playingCard,MapStateHandler.PICK_MOVE_POSITION_BY_CARD);
        break;
         
        case EventType.SKIRMISH_PLAYED:
            game.getCurrentPlayer().getHand().unselectAllCards();
        break;
        
        case EventType.END_COMBAT:
            setPlayingCard(new Card());
        break;
        
    }
}
        
}
