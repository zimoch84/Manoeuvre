/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import java.util.ArrayList;
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
import manouvre.network.server.UnoptimizedDeepCopy;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Piotr
 */
public class CardCommands {
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(CardCommands.class.getName());  
    public static class MoveToTableCommand implements CardCommandInterface {
    
        Card card;
        String senderPlayerName;

        public MoveToTableCommand(Card card, String senderPlayerName) {
            this.card = card;
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {
                Card movingCard = game.getPlayerByName(senderPlayerName).getHand().getCard(card);
                movingCard.setPlayed(true);
                
                game.getPlayerByName(senderPlayerName).getHand().moveCardTo(movingCard,
                        game.getPlayerByName(senderPlayerName).getTablePile()
                        );
               
                
                
                if (!game.getCurrentPlayer().getName().equals(senderPlayerName)) 
                 {
                    /*
                    This is set to possible guirella this card by opponent
                    */
                    game.getCardCommandFactory().setOpponentCard(card);
                }
                //repaint is made by CommandQueue
            }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return senderPlayerName  + " moved "  + card.getCardName()  + " to the table";
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

    public static class GuerrillaCardCommand implements CardCommandInterface {

        Card card;
        String senderPlayerName;
        CardCommandInterface incomingCardCommand;
        MoveToTableCommand moveToTable;

        public GuerrillaCardCommand(Card card, String senderPlayerName, CardCommandInterface incomingCardCommand) {
            this.card = card;
            this.senderPlayerName = senderPlayerName;
            this.incomingCardCommand = incomingCardCommand;
            this.moveToTable = new CardCommands.MoveToTableCommand(card, senderPlayerName);
        }

        @Override
        public void execute(Game game) {
            
            moveToTable.execute(game);
            incomingCardCommand.cancel(game);
            
            if (!game.getCurrentPlayer().getName().equals(senderPlayerName))
            {
                game.getCardCommandFactory().awakeObserver();
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.CARD_REJECTED);
            }

        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            
            return moveToTable.logCommand() + " " + card.getCardName() + " cart rejected by " + senderPlayerName;
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
                LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
                game.getCardCommandFactory().resetFactory();
            } else {
                game.getCardCommandFactory().awakeObserver();
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.CARD_NOT_REJECTED);
                LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
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
                
                //LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
                //game.getCardCommandFactory().resetFactory();
                
            } else {
                game.getCardCommandFactory().setIncomingCardCommand(this); //set this comand to be able to reject it
                game.getCardCommandFactory().awakeObserver();
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.CARD_DIALOG);
            }
            
            
             
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
            LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
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
            
            game.getCombat().setState(Combat.WITHRDAW);
            
            /*
            Setting non active after choosing withdraw position on map to avoid 
            */
            if(game.getCurrentPlayer().getName().equals(senderPlayerName))
                
            {  
                game.getCurrentPlayer().setActive(false);
               
            }
        
            else
            { 
                 game.getCardCommandFactory().notifyObservers(CardCommandFactory.OPPONENT_WITHDRAW);
                 
            }
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
            LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
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
            
            Card cloneCard = (Card) UnoptimizedDeepCopy.copy(attackingCard);
            
            this.combat = new Combat(cloneCard.getPlayingCardMode(), attackingUnit, attackingCard, attackTerrain, defendingUnit, defenseTerrain);
            
            Combat cloneCombat = (Combat) UnoptimizedDeepCopy.copy (combat);
            
            this.combat = cloneCombat;

        }

        @Override
        public void execute(Game game) {
            
           moveToTableCommand.execute(game);
           combat.setState(Combat.PICK_DEFENSE_CARDS);
          

           game.setCombat(combat);
           /*
           TODO: zmienic na stany ręki
           */
           game.checkLockingGUI();
           //now is the time for opponent to choose defensive cards
           
           game.getCardCommandFactory().setAttackedUnit(
                   game.getUnitByName(attackedUnit.getName())
           );
           if(game.getCurrentPlayer().getName().equals(senderPlayerName)){
               
               game.getCurrentPlayer().setAttacked(true);
               
           }else{
               //game.getCardCommandFactory().setOpponentCard(attackingCard);  it exists in move to table
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
            game.getPlayerByName(senderPlayerName).getDrawPile().moveTopXCardsTo(numberOfChosenCards, 
                    game.getPlayerByName(senderPlayerName).getHand()
            );
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            stringBuilder.setLength(0);//delete all string

            stringBuilder.append(senderPlayerName + " has drawn " + cardSet.size() + "cards from Test Panel \n");
            for (int i = 0; i < cardSet.size(); i++) {
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

        String playerName;
        public CleanTableCommand(String playerName) {
            this.playerName = playerName;
        }

        @Override
        public void execute(Game game) {
            game.getCurrentPlayer().getTablePile().moveTopXCardsTo(
            game.getCurrentPlayer().getTablePile().size(), 
            game.getCurrentPlayer().getDiscardPile()
            );
            
            game.getOpponentPlayer().getTablePile().moveTopXCardsTo(
            game.getOpponentPlayer().getTablePile().size(), 
            game.getOpponentPlayer().getDiscardPile()
            );
            
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return "Player " + playerName + " cleared table";
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
            LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
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
                Card movingCard = game.getPlayerByName(senderPlayerName).getHand().getCard(card);
                game.getCurrentPlayer().getTablePile().addCard(movingCard);// Put cards on own table
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
                LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
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
        Combat combat;

        public DefendCommand(int combatType, ArrayList<Card> defendCards, String senderPlayerName, Combat combat) {
            this.combat=combat;
            this.combatType = combatType;
            this.defendCards = defendCards;
            this.senderPlayerName = senderPlayerName;
//            Combat cloneCombat = (Combat) UnoptimizedDeepCopy.copy (combat);  //btest
//            this.combat = cloneCombat;
            
        }

        @Override
        public void execute(Game game) {
            /*
            If we dont have no card
            */
            
            
            game.setCombat(combat);
            game.getCombat().setState(Combat.PICK_SUPPORTING_CARDS);
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
           
            Combat tcombatclone = (Combat) UnoptimizedDeepCopy.copy (combat);
            ThrowDiceCommand tdclone = (ThrowDiceCommand) UnoptimizedDeepCopy.copy (td);
            this.td = tdclone;
            this.combat = tcombatclone;
            this.senderPlayerName = senderPlayerName;
            
        }

        @Override
        public void execute(Game game) {
           
            /*
            Set cardFactorywith dices
            */
            
            
            if(game.getCurrentPlayer().getName().equals(senderPlayerName))
            {
                td.execute(game);
                            
                combat.setDices(game.getCardCommandFactory().getAllDices());
            
                combat.calculateCombatValues();
            }
            game.setCombat(combat);
            
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
    game.getCurrentPlayer().getTablePile().moveTopXCardsTo(
            game.getCurrentPlayer().getTablePile().size(), 
            game.getCurrentPlayer().getDiscardPile()
            );
            
    }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            String log = td.logCommand();
            
            log += "Att Val :" + combat.getAttackValue()+ " " + "Att Val :" + combat.getAttackBonus()+ " " +
                   "Def Val :" + combat.getDefenceValue()+ " " + "Def Bon :" + combat.getDefenseBonus()+ " " ;
            return log;
        }

        @Override
        public int getType() {
            return Param.COMBAT;
        }

    }
}
