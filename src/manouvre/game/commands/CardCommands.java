/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.Card;
import manouvre.game.CardCommandFactory;
import manouvre.game.CardSet;
import manouvre.game.Game;
import manouvre.game.Param;
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
                game.getTablePile().addCardToThisSet(movingCard);// Put card on own table
                game.getPlayerByName(senderPlayerName).getHand().drawCardFromSet(movingCard);//remove card from own hand
                
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
        Card card;
        Command moveToTableCommand;
        String senderPlayerName;

        public AttackCommand(Unit attackedUnit, Card card, String senderPlayerName) {
            this.attackedUnit = attackedUnit;
            this.card = card;
            this.moveToTableCommand = new CardCommands.MoveToTableCommand(card, senderPlayerName);
            this.senderPlayerName = senderPlayerName;

        }

        @Override
        public void execute(Game game) {
            
           moveToTableCommand.execute(game);
            
            
           game.getCardCommandFactory().setAttackedUnit(attackedUnit);
           
           if(game.getCurrentPlayer().getName() != senderPlayerName){
               
               game.getCardCommandFactory().setOpponentCard(card);
               game.getCardCommandFactory().notifyObservers(CardCommandFactory.ATTACK_DIALOG);
               
           }
           
           
          
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return senderPlayerName + " played " + card.getCardName();
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
                game.getOpponentPlayer().getHand().addCardsFromOtherSet(numberOfChosenCards, cardSet, true, deleteCards); //add card to opponent hand
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

}
