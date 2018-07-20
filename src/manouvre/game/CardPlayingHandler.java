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
import manouvre.events.EventType;
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

    private MapStateHandler mapInputHandler;
    
    public CardPlayingHandler(Game game, MapStateHandler mapInputHandler) {
        this.game = game;
        this.mapInputHandler = mapInputHandler;
                
    }
    
     public boolean canBePlayed(Card card) {
        return canBePlayedByPhase(card);
    }
    
     public void actionOnSelection(Card card) {
           actionOnSelectionByPhase(card);
           mapInputHandler.setStateByCard(card, MapStateHandler.CARD_PLAYING_STATE);
    }
     public void actionOnDeselection(Card card) {
         actionOnDeselectionByPhase(card);
         mapInputHandler.setState(MapStateHandler.NOSELECTION);
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
                
                break;
            
            case Game.COMBAT:
                actionSelectionCardInCombat(card);
                break;
                
            case Game.RESTORATION:
                actionSelectionCardInRestoration(card);
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
    switch (card.getCardType()) {
        case Card.HQCARD:
                switch (card.getHQType()) {
                    case Card.WITHDRAW:
                        if (combat.getDefendingUnit() != null) 
                        {
                            combat.getDefendingUnit().setSelected(true);
                            combat.getDefendingUnit().setRetriving(true);
                            combat.setState(Combat.WITHRDAW);
                        }
                        break;
                case Card.GUERRILLAS:
                        cmdQueue.storeAndExecuteAndSend(ccf.createGuerrillaCardCommand(card));
                        break;
                
                case Card.SKIRMISH:
                        card.setSelected(true);
                        setPlayingCard(card);
                        game.notifyAbout(EventType.SKIRMISH_SELECTED);
                        
                }
        break;   
        case Card.UNIT:
            
            switch(combat.getState()){
                case Combat.COMBAT_NOT_INITIALIZED:
                    game.getCurrentPlayerUnitByName(card.getCardName()).setSelected(true);
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

                        combat.setAttackingUnit(game.getCurrentPlayerUnitByName(card.getCardName()));
                        combat.setInitAttackingCard(card);
                    }
                    /*
                    Notify to pick proper card mode
                     */ else if (playingModeCounter == 2) {
                        game.notifyAbout(EventType.VOLLEY_ASSAULT_DECISION);
                    }
                    /*
                    If we know what mode is playing we can calculate attacking positions
                     */
                    if (card.getPlayingCardMode() != null) {
                        game.getCombat().calculateAttackingPositions(game);
                    }
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
        switch (card.getCardType()) {
            case Card.UNIT:
                 game.getCurrentPlayerUnitByName(card.getCardName()).setSelected(true);  
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
                break;
            
            case Game.COMBAT:
                actionDeselectionCardInCombat(card);
                
            case Game.RESTORATION:
                actionDeselectionCardInRestoration(card);
        }
    }
    
    private void actionDeselectionCardInMove(Card card){
    
         switch (card.getHQType()) {
        case Card.FORCED_MARCH:
            game.getCurrentPlayer().getLastMovedUnit().setSelected(false);
            mapInputHandler.setState( MapStateHandler.NOSELECTION);
            break;
        case Card.SUPPLY:
            game.unselectAllUnits();
            mapInputHandler.setState( MapStateHandler.NOSELECTION);
            break;
               
        case Card.NO_CARD:
            break;
            
        }        
    }
    
    private void actionDeselectionCardInCombat(Card card){
    
    switch (card.getCardType()) {
        case Card.HQCARD:
            switch (card.getHQType()) {
                    case Card.WITHDRAW:
                            if (game.getCombat().getDefendingUnit() != null) {
                                game.getCombat().getDefendingUnit().setSelected(false);
                                game.getCombat().getDefendingUnit().setRetriving(false);
                                setPlayingCard(null);
                                LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapInputStateHandler.NOSELECTION");
                                mapInputHandler.setState(MapStateHandler.NOSELECTION);
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
            
                case Combat.COMBAT_NOT_INITIALIZED:
                    game.getCurrentPlayerUnitByName(card.getCardName()).setSelected(false);
                    int playingModeCounter = card.getPlayingPossibleCardModes().size();
                    if (playingModeCounter == 1) 
                    {
                        card.setPlayingCardMode(null);
                        game.getCombat().setState(Combat.COMBAT_NOT_INITIALIZED);
                        game.getCombat().setAttackingUnit(null);

                    } 
                    else if (playingModeCounter == 2) {
                        game.notifyAbout(EventType.VOLLEY_ASSAULT_DECISION_DESELECTION);
                    }
                    if (card.getPlayingCardMode() != null) {
                        game.getCombat().clearAttackingPosiotions();
                    }
                    break;
                case Combat.PICK_DEFENSE_CARDS:
                    game.getCombat().removeDefenceCard(card);
                    break;
            }
           
            break;
            case Card.LEADER:
                {
                    if (game.getPhase() == Game.COMBAT) {
                        if (game.getCombat().getState().equals(Combat.PICK_DEFENSE_CARDS)) {
                            game.getCombat().removeDefenceCard(card);
                        }
                    }
                    if (game.getCombat().getState().equals(Combat.PICK_SUPPORT_UNIT)) {
                        game.getCombat().setState(Combat.PICK_SUPPORT_CARDS);
                        game.getCombat().setSupportingLeader(null);
                    }
                    break;
                }
            case Card.NO_CARD:
                {
                    LOGGER.debug("B\u0142\u0105d wywoa\u0142ani pustej karty w metodzie actionOnDeselection");
                }
            
    }
    }

    private void actionDeselectionCardInRestoration(Card card){
    switch (card.getCardType()) {
            case Card.HQCARD:
                 switch (card.getHQType()) {
                    case Card.REDOUBDT:
                         mapInputHandler.setState(MapStateHandler.NOSELECTION);
                    break;
                    
                    case Card.SUPPLY:
                         mapInputHandler.setState(MapStateHandler.NOSELECTION);
                    break;
                }
            break;     
            case Card.UNIT:
                   
                     if (game.getPhase() == Game.RESTORATION) {
                        game.getCurrentPlayerUnitByName(card.getCardName()).setSelected(false);
                    }
            break;
            case Card.LEADER:
                mapInputHandler.setState(MapStateHandler.NOSELECTION);
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
                if(card.getCardType() == Card.UNIT)
                    return !game.getUnitByCard(card).isEliminated();
                if(card.getHQType() == Card.AMBUSH)
                    return !game.getCurrentPlayer().hasAttacked();
           case Combat.PICK_SUPPORT_CARDS:
               switch(card.getCardType()){
                case Card.HQCARD:
                    switch(card.getHQType()){
                        case Card.SKIRMISH:
                        case Card.COMMITED_ATTACK:
                        case Card.ROYAL_ENG:
                            return true;
                        default: return false;    
                    }
                case Card.LEADER:
                    return true;
                case Card.UNIT:
                    return checkIfSupportUnitMatchesCardWithAssaultMode(card, game);
                    
               }
           
           case Combat.PICK_DEFENSE_CARDS:
                switch(card.getCardType()){
                    case Card.HQCARD:
                        switch(card.getHQType()){
                            case Card.WITHDRAW:
                                return true;
                            default: return false;    
                        }
                    case Card.LEADER:
                        return true;
                    case Card.UNIT:
                        return game.getCombat().getDefendingUnit().equals(card);
                }
                
           case Combat.PICK_SUPPORT_UNIT:
               switch(card.getCardType()){
                case Card.LEADER:
                    return true;
                case Card.UNIT:
                    return checkIfSupportUnitMatchesCardWithAssaultMode(card, game);
               }
           default: return false;    
       }
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
    
        switch(card.getCardType()){
                case Card.HQCARD:
                    switch(card.getHQType()){
                        case Card.REDOUBDT:
                            return true;
                        case Card.SUPPLY:
                            checkIfAnyUnitInjured(game);
                        default: return false;    
                    }
                case Card.LEADER:
                    return checkIfAnyUnitInjured(game);
                case Card.UNIT:
                     return game.getUnitByCard(card).isInjured(); 
                default: 
                    return false;        
        }
    }
    
    private boolean checkIfAnyUnitInjured(Game game){
        
        for(int i=0 ; i < game.getCurrentPlayer().getArmy().length; i++){
            Unit currentUnit = game.getCurrentPlayer().getArmy()[i];
            if(currentUnit.injured)
                return true;
        }
        return false;
    
    }

    
   

    public Card getPlayingCard() {
        return playingCard;
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
    
    /*
    TODO czy to potrzebne?
    */
    
    public void resetCardHandler()
            
    {
    
    }
    
     public Card getOpponentCard() {
        return opponentCard;
    }

    public void setOpponentCard(Card opponentCard) {
        this.opponentCard = opponentCard;
    }

    private void handleCombatPhaseOnSelection(Game game, Card card){
   
                if(canBePlayed(card))
                card.setSelected(true);
                Combat combat = game.getCombat();
                
                switch(combat.getState()){
                
                    case Combat.PICK_DEFENSE_CARDS:
                       if(combat.getDefendingUnit() != null)
                        if(game.getUnit(combat.getDefendingUnit()).equals(card) )
                        {
                            card.setSelected(true);
                            combat.addDefenceCard(card);
                        }
                        else if (canBePlayed(card))
                            actionOnSelection(card);
                       
                    break;
                    
                    case Combat.PICK_SUPPORT_CARDS:
                        handleCombatPickSupportCardsOnSelection(card, game);
                        break;
                
                }
            }
                
    private void handleCombatPickSupportCardsOnSelection(Card card, Game game){
        if(card.getCardType() == Card.LEADER)
        {
            card.setSelected(true);
            setPlayingCard(card);
            game.notifyAbout(EventType.LEADER_SELECTED);
        }
        if(card.getCardType() == Card.UNIT)
        {
            if(game.getCombat().getSupportingLeader() == null)
            checkIfCardMatchesAttackingUnitAndAddSupport(game, card);
            /*
            We have leader selected and supporting unit picked
            */
            else 
            checkIfCardMatchesSupportingUnitAndAddSupport(game, card);    
       }
   }

    private void handleCombatPickSupportCardsOnDeSelection(Card card, Game game){
    
        if(card.getCardType() == Card.LEADER)
        {
            handleLeaderDeselected(game, card);
        }

        else if(card.getHQType() == Card.SKIRMISH)
        {
            card.setSelected(false);
            setPlayingCard(new Card());
            game.notifyAbout(EventType.SKIRMISH_DESELECTED);

        }
        if(card.getCardType() == Card.UNIT)
        {                   
            if(game.getCombat().getSupportingLeader() == null)
                    checkIfCardMatchesAttackingUnitAndRemoveSupport(game, card);
            else 
                    checkIfCardMatchesSupportingUnitAndRemoveSupport(game, card);
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

    private void handleCombatPhaseOnDeselection(Game game, Card card)
            {
                
                switch (game.getCombat().getState()) {
                    case Combat.PICK_DEFENSE_CARDS:
                        if(game.getCombat().getDefendingUnit() != null)
                            if(game.getUnit(game.getCombat().getDefendingUnit()).equals(card) )
                            {
                                game.getCombat().removeDefenceCard(card);
                            }
                            else if (canBePlayed(card))
                                actionOnDeselection(card);
                        break;
                    case Combat.PICK_SUPPORT_UNIT:
                        if(card.getCardType() == Card.LEADER)
                            handleLeaderDeselected(game, card);
                        break;
                    case Combat.PICK_SUPPORT_CARDS:   
                            handleCombatPickSupportCardsOnDeSelection(card, game);
                        break;
                }
}            
 private void handleLeaderDeselected(Game game, Card card) {
        /* We have to unselect supporting units equal to the support value */
        game.getCombat().resetSupport(game);
        LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().setPlayingCard(null)");
        setPlayingCard(new Card());

        game.notifyAbout(EventType.LEADER_DESELECTED);
 }
@Override
public void update(Observable o, Object arg) {


    System.out.println("Object attached to event()" +  o.getClass().toString() );

    String dialogType = (String) arg;

    LOGGER.debug(game.getCurrentPlayer().getName() + " Incoming Event: " + dialogType);

    switch(dialogType){
        case  EventType.CARD_MOVED_TO_TABLE:
         if(game.getCurrentPlayer().isActive())
             setOpponentCard(game.getOpponentPlayer().getTablePile().getLastCard(false));
        break;
        case EventType.CARD_ACCEPTED:
          if(game.getCurrentPlayer().isActive())
             setPlayingCard(new Card());
        break;
        case EventType.LEADER_FOR_COMBAT:
            game.getCombat().addSupportCard(getPlayingCard());
        break;
        case EventType.CARD_ASSAULT_MODE:
            playingCard.setPlayingCardMode(Card.ASSAULT);
            game.getCombat().setState(Combat.INITIALIZING_COMBAT);
            game.getCombat().setAttackingUnit(game.getUnitByCard(playingCard));
            game.getCombat().setInitAttackingCard(playingCard);
        break;
        case EventType.CARD_VOLLEY_MODE:
            playingCard.setPlayingCardMode(Card.VOLLEY);
            game.getCombat().setAttackingUnit(game.getUnitByCard(playingCard));
            game.getCombat().setInitAttackingCard(playingCard);
            game.getCombat().setState(Combat.INITIALIZING_COMBAT);
        break;
        case EventType.PICK_SUPPORT_UNIT:
            /*We have to select supporting units equal to the support value*/
            game.getCombat().setState(Combat.PICK_SUPPORT_UNIT);
            game.getCombat().setSupportingLeader(getPlayingCard());
        break;

        case EventType.REDOUBT_PLAYED:
            setPlayingCard(new Card());
            break;

        case EventType.CARD_DESELECTED:
            if(game.getCurrentPlayer().isActive())
             setPlayingCard(new Card());

    }
}
        
}
