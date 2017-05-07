/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import java.util.ArrayList;
import java.util.Arrays;
import manouvre.game.Card;
import manouvre.game.CardCommandFactory;
import manouvre.game.CardSet;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Param;
import manouvre.game.Position;
import manouvre.game.Terrain;
import manouvre.game.Unit;
import manouvre.game.interfaces.CardCommandInterface;
import manouvre.game.interfaces.Command;
import manouvre.gui.CustomDialog;

/**
 *
 * @author Piotr
 */
public class CardCommands {

    public static class MoveToTableCommand implements CardCommandInterface {

        Card card;
        String senderPlayerName;

        public MoveToTableCommand(Card card, String senderPlayerName) {
            this.card = card;
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {
                Card movingCard = game.getPlayerByName(senderPlayerName).getHand().getCardByCard(card);
                game.getTablePile().addCardToThisSet(movingCard);// Put cards on own table
                game.getPlayerByName(senderPlayerName).getHand().drawCardFromSet(movingCard);//remove cards from own hand
                
                if (game.getCurrentPlayer().getName().equals(senderPlayerName)) {
                    //do nothing
                } else {
                    game.getCardCommandFactory().setOpponentCard(movingCard);
                }
                //repaint is made by CommandQueue
            }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            if (card.getCanBeCancelled()) {
                return card.getCardName() + " cart moved to the table " + senderPlayerName + " have to wait for acceptance";
                }
            return card.getCardName() + " cart moved to the table";
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }

        @Override
        public void cancel(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public static class RejectCardCommand implements CardCommandInterface {

        Card card;
        String senderPlayerName;
        CardCommandInterface incomingCardCommand;

        public RejectCardCommand(Card card, String senderPlayerName, CardCommandInterface incomingCardCommand) {
            this.card = card;
            this.senderPlayerName = senderPlayerName;
            this.incomingCardCommand = incomingCardCommand;
        }

        @Override
        public void execute(Game game) {
            if (game.getCurrentPlayer().getName().equals(senderPlayerName)) { //separate action for each player
                //do nothing special
            } else {
                game.getCardCommandFactory().awakeObserver();
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.CARD_REJECTED);
            }
            //all players do the same
            game.getPlayerByName(senderPlayerName).getDiscardPile().addCardToThisSet(game.getTablePile().drawCardFromSet(card));
            incomingCardCommand.cancel(game);
            Card guerrillas = game.getPlayerByName(senderPlayerName).getHand().getCardByName("Guerrillas", true);
            game.getPlayerByName(senderPlayerName).getDiscardPile().addCardToThisSet(guerrillas);

        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return card.getCardName() + " cart rejected by " + senderPlayerName;
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }

        @Override
        public void cancel(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public static class DoNotRejectCardCommand implements CardCommandInterface {

        Card card;
        String senderPlayerName;

        public DoNotRejectCardCommand(Card card, String senderPlayerName) {
            this.card = card;
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {
            if (game.getCurrentPlayer().getName().equals(senderPlayerName)) {
                game.getCardCommandFactory().resetFactory();
            } else {
                game.getCardCommandFactory().awakeObserver();
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.CARD_NOT_REJECTED);
                game.getCardCommandFactory().resetFactory();
            }
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return card.getCardName() + " cart was not rejected " + senderPlayerName;
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }

        @Override
        public void cancel(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public static class ForcedMarchCommand implements CardCommandInterface {

        Command moveUnitCommand;
        Card card;
        Command moveToTableCommand;
        String senderPlayerName;

        public ForcedMarchCommand(Command moveUnitCommand, Card card, String senderPlayerName) {
            this.moveUnitCommand = moveUnitCommand;
            this.card = card;
            this.moveToTableCommand = new CardCommands.MoveToTableCommand(card, senderPlayerName);
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {

            moveToTableCommand.execute(game);
            moveUnitCommand.execute(game);
            if (game.getCurrentPlayer().getName().equals(senderPlayerName)) {
                //do nothing special
            } else {
                game.getCardCommandFactory().setIncomingCardCommand(this); //set this comand to be able to reject it
            }
            game.getCardCommandFactory().notifyObservers(CardCommandFactory.CARD_DIALOG);
        }

        @Override
        public void undo(Game game) {

            moveUnitCommand.undo(game);

        }

        @Override
        public String logCommand() {
            return senderPlayerName + " played " + card.getCardName();
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }

        @Override
        public void cancel(Game game) {
            moveUnitCommand.undo(game);
            game.getCurrentPlayer().setMoved(true);
            game.getCardCommandFactory().resetFactory();
        }

    }
    
     public static class WithrdawCommand implements CardCommandInterface {

        Command moveUnitCommand;
        Card card;
        Command moveToTableCommand;
        String senderPlayerName;

        public WithrdawCommand(Command moveUnitCommand, Card card, String senderPlayerName) {
            this.moveUnitCommand = moveUnitCommand;
            this.card = card;
            this.moveToTableCommand = new CardCommands.MoveToTableCommand(card, senderPlayerName);
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {

            moveToTableCommand.execute(game);
            moveUnitCommand.execute(game);
            game.getCardCommandFactory().notifyObservers(CardCommandFactory.CARD_DIALOG);
            
            game.getCardCommandFactory().resetFactory();
            
        }

        @Override
        public void undo(Game game) {

            moveUnitCommand.undo(game);

        }

        @Override
        public String logCommand() {
            return senderPlayerName + " played " + card.getCardName();
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }

        @Override
        public void cancel(Game game) {
            moveUnitCommand.undo(game);
            game.getCurrentPlayer().setMoved(true);
            game.getCardCommandFactory().resetFactory();
        }

    }


    public static class AttackCommand implements Command {

        Unit attackedUnit;
        Card attackingCard;
        Command moveToTableCommand;
        String senderPlayerName;
        Combat combat;

        public AttackCommand(Unit defendingUnit, Card attackingCard, String senderPlayerName, Unit attackingUnit, Terrain attackTerrain, Terrain defenseTerrain) {
            this.attackedUnit = defendingUnit;
            this.attackingCard = attackingCard;
            this.moveToTableCommand = new CardCommands.MoveToTableCommand(attackingCard, senderPlayerName);
            this.senderPlayerName = senderPlayerName;
            
            
            combat = new Combat(attackingCard.getPlayingCardMode(), attackingUnit, attackingCard, attackTerrain, defendingUnit, defenseTerrain);
            

        }

        @Override
        public void execute(Game game) {
            
           moveToTableCommand.execute(game);
            
           game.setCombat(combat);
           //now is the time for opponent to choose defensive cards
           game.getCombat().setState(Combat.PICK_DEFENSE_CARDS);
           game.getCardCommandFactory().setAttackedUnit(attackedUnit);
           
           if(!game.getCurrentPlayer().getName().equals(senderPlayerName)){
               game.getCardCommandFactory().setOpponentCard(attackingCard);
               game.getCardCommandFactory().awakeObserver();
               game.getCardCommandFactory().notifyObservers(CardCommandFactory.ATTACK_DIALOG);
               
           }

        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return senderPlayerName + " played " + attackingCard.getCardName();
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }

    }

    public static class MoveToHandCommand implements Command {    //just for test popup

        String senderPlayerName;
        CardSet cardSet;
        int numberOfChosenCards;
        boolean deleteCards;
        StringBuilder stringBuilder = new StringBuilder();

        public MoveToHandCommand(CardSet cardSet, int numberOfChosenCards, String senderPlayerName, boolean deleteCards) {
            this.deleteCards = deleteCards;
            this.cardSet = cardSet;
            this.numberOfChosenCards = numberOfChosenCards;
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {
            if (game.getCurrentPlayer().getName().equals(senderPlayerName)) {
                game.getCurrentPlayer().getHand().addCardsFromOtherSet(numberOfChosenCards, cardSet, true, deleteCards);//add to own hand

            } else {
                game.getOpponentPlayer().getHand().addCardsFromOtherSet(numberOfChosenCards, cardSet, true, deleteCards); //add cards to opponent hand
                //repaint is made by CommandQueue
            }
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            stringBuilder.setLength(0);//delete all string

            stringBuilder.append(senderPlayerName + " has drawn " + cardSet.cardsLeftInSet() + "cards from Test Panel \n");
            for (int i = 0; i < cardSet.cardsLeftInSet(); i++) {
                stringBuilder.append(i + ": " + cardSet.getCardNameByPosInSet(i) + "\n");
            }

            return stringBuilder.toString();
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }
    }

    public static class CleanTableCommand implements Command {    //just for test popup

        public CleanTableCommand() {

        }

        @Override
        public void execute(Game game) {
            game.getTablePile().clear();
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return "Table cleard";
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }
    }

    public static class ResetCardFactory implements Command {

        String senderPlayerName;

        public ResetCardFactory(String senderPlayerName) {
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {
            game.getCardCommandFactory().resetFactory();

        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return senderPlayerName + " reseted his card factory";
        }

        @Override
        public int getType() {
            return Param.RESET_FACTORY;
        }
    }

     public static class MoveDefensiveCardsToTableCommand implements CardCommandInterface {

        ArrayList<Card> cards;
        String senderPlayerName;

        public MoveDefensiveCardsToTableCommand(ArrayList<Card> cards, String senderPlayerName) {
            this.cards = cards;
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {
                for(Card card:cards){
                Card movingCard = game.getPlayerByName(senderPlayerName).getHand().getCardByCard(card);
                game.getTablePileDefPart().addCardToThisSet(movingCard);// Put cards on own table
                game.getPlayerByName(senderPlayerName).getHand().drawCardFromSet(movingCard);//remove cards from own hand
               
                }    
                //game.getCombat().calculateCombatValues();
                if (game.getCurrentPlayer().getName().equals(senderPlayerName)) {
                    game.getCombat().setDefenceCards(cards);
                   // game.getCardCommandFactory().clearDefendingCards();
                    //do nothing
                } else {

                game.getCardCommandFactory().awakeObserver();
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.DEFENDING_CARDS_PLAYED);
                game.getCardCommandFactory().resetFactory();
                }
            }
 
        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            if(cards.size()==1)//if player is playing one card
                if (cards.get(0).getCanBeCancelled()) {
                    return cards.get(0).getCardName() + " cart moved to the table " + senderPlayerName + " have to wait for acceptance";
                }
                
            
            return "More than one cart moved to the table";
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }

        @Override
        public void cancel(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

     public static class DefendCommand implements CardCommandInterface {

        ArrayList<Card> defendCards;
        String senderPlayerName;
        int combatType;

        public DefendCommand(int combatType, ArrayList<Card> defendCards, String senderPlayerName) {
            this.combatType = combatType;
            this.defendCards = defendCards;
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {
            /*
            If we dont have no card
            */
            switch(combatType) {
                
                case  Combat.BOMBARD : {
                 if (!game.getCurrentPlayer().getName().equals(senderPlayerName))
                    game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_ACCEPTED);
                 break;
                }
                
                case Combat.ASSAULT:
                {
                    MoveDefensiveCardsToTableCommand md = new MoveDefensiveCardsToTableCommand(defendCards, senderPlayerName);
                    md.execute(game);
                    break;
                    
                    
                }
            
 
                
            }
            
        }
        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            if(defendCards.size()==1)//if player is playing one card
                if (defendCards.get(0).getCanBeCancelled()) {
                    return defendCards.get(0).getCardName() + " cart moved to the table " + senderPlayerName + " have to wait for acceptance";
                }
                
            
            return "More than one cart moved to the table";
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }

        @Override
        public void cancel(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
 public static class CombatOutcomeCommand implements Command {

        Combat combat;
        ThrowDiceCommand td;
        String senderPlayerName;

        public CombatOutcomeCommand(String senderPlayerName, Combat combat, ThrowDiceCommand td ) {
            this.combat = combat;
            this.td = td;
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {
           
            /*
            Set cardFactorywith dices
            */
            game.getTablePile().clear();
            game.getTablePileDefPart().clear();
            
            if(game.getCurrentPlayer().getName().equals(senderPlayerName))
            {
                td.execute(game);
            
                combat.setDices(game.getCardCommandFactory().getAllDices());
            
                combat.calculateCombatValues();
            }
            
            switch(combat.getOutcome()){
            
                case  Combat.NO_EFFECT:
                
                {
                     game.getCardCommandFactory().awakeObserver();   
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_NO_RESULT);
                     break;
                }
                
                case  Combat.DEFFENDER_TAKES_HIT:
                
                {
                     
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getDefendingUnit().getName()).takeHit();
                     if(  !game.getUnitByName(combat.getDefendingUnit().getName()).isEliminated())
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_TAKES_HIT);
                     else 
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                     break;
                }
                
                case  Combat.ELIMINATE:
                
                {   
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getDefendingUnit().getName()).eliminate();
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                     break;
                }
                
                case Combat.ATTACKER_TAKES_HIT :
                {
                     
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getAttackingUnit().getName()).takeHit();
                     if(  !game.getUnitByName(combat.getAttackingUnit().getName()).isEliminated())
                        game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_ATTACKER_TAKES_HIT);
                     else 
                        game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_ATTACKER_ELIMINATE);
                     break;  
                }
                /*
                Temporary we assume that defender always choose to retreat
                */
                case Combat.DEFENDER_CHOSES :
                {
                     
                     game.getCardCommandFactory().awakeObserver();
                     Unit defendingUnit =  game.getUnitByName(combat.getDefendingUnit().getName());
                     ArrayList<Position> retreatPosition =  game.getRetreatPositions(defendingUnit);
                     /*
                     TODO - create full retreat dialog with defender choice
                     Currently - take first possible position
                     */
                     
                     if(retreatPosition.size() > 0){
                         MoveUnitCommand moveUnitCommand = new MoveUnitCommand(senderPlayerName  , defendingUnit,  retreatPosition.get(0));
                         moveUnitCommand.execute(game);
                     }
                     
                     
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.OPPONENT_WITHDRAW);
                     break;  
                }
                                /*
                Temporary we assume that attacker always choose to take hit
                */
                case Combat.ATTACKER_CHOSES :
                {
                     
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getDefendingUnit().getName()).takeHit();
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_TAKES_HIT);
                     break;
                }
                case Combat.HIT_AND_RETREAT :
                {
                     
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getDefendingUnit().getName()).takeHit();
                     Unit defendingUnit =  game.getUnitByName(combat.getDefendingUnit().getName());
                     ArrayList<Position> retreatPosition =  game.getRetreatPositions(defendingUnit);
                     /*
                     TODO - create full retreat dialog with defender choice
                     Currently - take first possible position
                     */
                     
                     if(retreatPosition.size() > 0){
                         MoveUnitCommand moveUnitCommand = new MoveUnitCommand(senderPlayerName  , defendingUnit,  retreatPosition.get(0));
                         moveUnitCommand.execute(game);
                     }
                     
                     
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_TAKES_HIT);
                     break;  
                }
            }
            
           game.setCombat(null);
           
          
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return senderPlayerName + " generated outcome";
        }

        @Override
        public int getType() {
            return Param.COMBAT;
        }

    }
}
