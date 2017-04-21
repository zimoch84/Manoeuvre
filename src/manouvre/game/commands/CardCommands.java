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
                game.getCardCommandFactory().setPlayingCard(card);
               // game.getCardCommandFactory().setCancelCardPopupMode(true);
                
               
                
                

                //repaint is made by CommandQueue
            }
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            if(card.getCanBeCancelled()) return card.getCardName() + " cart moved to the table " + senderPlayerName + " have to wait for acceptance";
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
                Card guerrillas = game.getCurrentPlayer().getHand().getCardByName("Guerrillas", true);
                game.getCurrentPlayer().getDiscardPile().addCardToThisSet(guerrillas); 
                game.getOpponentPlayer().getDiscardPile().addCardToThisSet(game.getCurrentPlayer().getTablePile().drawCardFromSet(card)); 
                               
            }
            else{
                game.undoCommandBeforeLast(true);
                game.getCardCommandFactory().getPlayingCard().setCanBeCanceled(false);
                
                Card guerrillas = game.getOpponentPlayer().getHand().getCardByName("Guerrillas", true);
                game.getOpponentPlayer().getDiscardPile().addCardToThisSet(guerrillas); 
                
                game.getCurrentPlayer().getDiscardPile().addCardToThisSet(game.getCurrentPlayer().getHand().drawCardFromSet(card));
                game.getCardCommandFactory().resetFactory();
                
                
                
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
                game.getCardCommandFactory().getPlayingCard().setCanBeCanceled(false);
                
                game.getPlayerByName(senderPlayerName).setPlayingCard(false);
                game.getCardCommandFactory().resetFactory();
            }
            else{
                game.getCurrentPlayer().getTablePile().addCardToThisSet(game.getCurrentPlayer().getHand().drawCardFromSet(card));//remove card from own hand and put it on table
                game.getCardCommandFactory().getPlayingCard().setCanBeCanceled(false);
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
    }    


public static class ForcedMarchCommand implements Command {

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
        }

        @Override
        public void undo(Game game) {
            
            moveUnitCommand.undo(game);
            
        }

        @Override
        public String logCommand() {
             return senderPlayerName  + " played " + card.getCardName();
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
            
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return senderPlayerName  + " played " + card.getCardName();
        }

        @Override
        public int getType() {
           return Param.PLAY_CARD;
        }
   
    }
public static class MoveToHandCommand implements Command{    //just for test popup
            
    
        String senderPlayerName;
        CardSet cardSet;
        int numberOfChosenCards;
        boolean deleteCards;
         StringBuilder stringBuilder=new StringBuilder();
        public MoveToHandCommand(CardSet cardSet, int numberOfChosenCards, String senderPlayerName,  boolean deleteCards) {
            this.deleteCards=deleteCards;
            this.cardSet=cardSet;
            this.numberOfChosenCards=numberOfChosenCards;
            this.senderPlayerName=senderPlayerName;
        }
     
        @Override
        public void execute(Game game) {
            if(game.getCurrentPlayer().getName().equals(senderPlayerName)){
                game.getCurrentPlayer().getHand().addCardsFromOtherSet(numberOfChosenCards, cardSet, true, deleteCards);//add to own hand
                
            }else{
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


