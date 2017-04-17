/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import manouvre.game.Card;
import manouvre.game.CardSet;
import manouvre.game.Game;
import manouvre.game.Param;
import manouvre.game.Unit;
import manouvre.game.interfaces.Command;

/**
 *
 * @author Piotr
 */
public class CardCommands {
    
public static class MoveToTableCommand implements Command{    
            
    
        Card card;
        String senderPlayerName;
        public MoveToTableCommand(Card card, String senderPlayerName) {
            this.card = card;
            this.senderPlayerName=senderPlayerName;
        }
     
        @Override
        public void execute(Game game) {
            if(game.getCurrentPlayer().getName().equals(senderPlayerName)){
                if(!card.getCanBeCancelled())
                game.getCurrentPlayer().getTablePile().addCardToThisSet(game.getCurrentPlayer().getHand().drawCardFromSet(card));//remove card from own hand and put it on table
                //repaint is made by CommandQueue
                else{
                    //card is not yet  at the table, opponent have to confirm
                }
            }else{
                game.getCurrentPlayer().getTablePile().addCardToThisSet(game.getOpponentPlayer().getHand().drawCardFromSet(card)); //remove card from opponent hand and put it on table
                //repaint is made by CommandQueue
            }
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return card.getCardName() + " cart moved to the table";
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }
    }    
 
public static class RejectCardCommand implements Command{    
            
    
        Card card;
        String senderPlayerName;
        public RejectCardCommand(Card card, String senderPlayerName) {
            this.card = card;
            this.senderPlayerName=senderPlayerName;
        }
     
        @Override
        public void execute(Game game) {
            if(game.getCurrentPlayer().getName().equals(senderPlayerName)){
                game.getCurrentPlayer().getDiscardPile().addCardToThisSet(game.getCurrentPlayer().getTablePile().drawCardFromSet(card)); 
            }
            else{
                game.getOpponentPlayer().getDiscardPile().addCardToThisSet(game.getCurrentPlayer().getHand().drawCardFromSet(card));
            }
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
    }   

public static class DoNotRejectCardCommand implements Command{    
            
    
        Card card;
        String senderPlayerName;
        public DoNotRejectCardCommand(Card card, String senderPlayerName) {
            this.card = card;
            this.senderPlayerName=senderPlayerName;
        }
     
        @Override
        public void execute(Game game) {
            if(game.getCurrentPlayer().getName().equals(senderPlayerName)){
                //do nothing here
            }
            else{
                game.getCurrentPlayer().getTablePile().addCardToThisSet(game.getCurrentPlayer().getHand().drawCardFromSet(card));//remove card from own hand and put it on table
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
    }    


public static class ForcedMarchCommand implements Command {

        Command moveUnitCommand;
        Card card;
        Command moveToTableCommand;
    
        public ForcedMarchCommand(Command moveUnitCommand, Card card, String senderPlayerName) {
            this.moveUnitCommand = moveUnitCommand;
            this.card = card;
            this.moveToTableCommand = new CardCommands.MoveToTableCommand(card, senderPlayerName);
            
            
        }

        @Override
        public void execute(Game game) {
          
            moveToTableCommand.execute(game);
            moveUnitCommand.execute(game);
        }

        @Override
        public void undo(Game game) {
            
            moveUnitCommand.undo(game);
            
        }

        @Override
        public String logCommand() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }


}    
public static class AttackCommand implements Command {

        Unit attackedUnit;
        Card card;
        Command moveToTableCommand;
        
        public AttackCommand(Unit attackedUnit, Card card, String senderPlayerName) {
            this.attackedUnit = attackedUnit;
            this.card = card;
            this.moveToTableCommand = new CardCommands.MoveToTableCommand(card, senderPlayerName);
            
            
        }
        @Override
        public void execute(Game game) {
            
            moveToTableCommand.execute(game);
            
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getType() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
   
    }
public static class MoveToHandCommand implements Command{    //just for test popup
            
    
        String senderPlayerName;
        CardSet cardSet;
        int numberOfChosenCards;
         StringBuilder stringBuilder=new StringBuilder();
        public MoveToHandCommand(CardSet cardSet, int numberOfChosenCards, String senderPlayerName) {
            this.cardSet=cardSet;
            this.numberOfChosenCards=numberOfChosenCards;
            this.senderPlayerName=senderPlayerName;
        }
     
        @Override
        public void execute(Game game) {
            if(game.getCurrentPlayer().getName().equals(senderPlayerName)){
                game.getCurrentPlayer().getHand().addCardsFromOtherSet(numberOfChosenCards, cardSet, true, false);//add to own hand
                
            }else{
                game.getOpponentPlayer().getHand().addCardsFromOtherSet(numberOfChosenCards, cardSet, true, false); //add card to opponent hand
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
            
            stringBuilder.append(senderPlayerName+ " has drawn "+cardSet.cardsLeftInSet()+"cards from Test Panel \n" );
            for (int i=0; i<cardSet.cardsLeftInSet();i++){
                stringBuilder.append(i+": "+ cardSet.getCardNameByPosInSet(i)+"\n");
            }
            
            return stringBuilder.toString();
        }

        @Override
        public int getType() {
            return Param.PLAY_CARD;
        }
    }    
 



}


