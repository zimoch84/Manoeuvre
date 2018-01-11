/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import java.util.ArrayList;
import manouvre.game.Card;
import manouvre.game.CardCommandFactory;
import manouvre.game.CardSet;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.game.Position;
import manouvre.game.Terrain;
import manouvre.game.Unit;
import manouvre.interfaces.CardCommandInterface;
import manouvre.interfaces.Command;
import manouvre.network.server.UnoptimizedDeepCopy;
import manouvre.state.CardStateHandler;
import manouvre.state.MapInputStateHandler;
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
        public String getType() {
            return Command.PLAY_CARD;
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
        public String getType() {
            return Command.PLAY_CARD;
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
        public String getType() {
            return Command.PLAY_CARD;
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
        public String getType() {
            return Command.PLAY_CARD;
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
        String log;

        public WithrdawCommand(Command moveUnitCommand, Card card, String senderPlayerName) {
            this.moveUnitCommand = moveUnitCommand;
            this.card = card;
            if(card != null)
                this.moveToTableCommand = new CardCommands.MoveToTableCommand(card, senderPlayerName);
            
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {
            /*
            If card is null then we retreat after combat resolution rather than play card
            */
            if(card != null)
            {
                moveToTableCommand.execute(game);
                log = senderPlayerName + " played " + card.getCardName();
            }
            
            moveUnitCommand.execute(game);
            
            log = senderPlayerName +" withdrawn" ;
            
            game.getCombat().setState(Combat.PURSUIT);
            
            game.swapActivePlayer();
            LOGGER.debug(game.getCurrentPlayer().getName() + " swapActivePlayer " + game.getCurrentPlayer().isActive());
            /*
            Attacking player chooses unit to pursuit
            */
            if(game.getCurrentPlayer().isActive())
            {
                game.setInfoBarText("Pick puruit unit");
                if(card != null){
                    LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapInputStateHandler.PICK_UNIT_BY_CARD");
                    game.mapInputHandler.setState(MapInputStateHandler.PICK_UNIT_BY_CARD);
                    }
                else 
                {
                    LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT");
                    game.mapInputHandler.setState(MapInputStateHandler.PICK_ONE_UNIT);
                }    
            
            }
             /*
            Defending player waits
            */
            else
            {
                LOGGER.debug(game.getCurrentPlayer().getName() + " zmiana stanu na MapInputStateHandler.NOSELECTION");
                game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
                game.setInfoBarText("Opponent is picking puruit unit");
            
            }
            
        }

        @Override
        public void undo(Game game) {

            moveUnitCommand.undo(game);

        }

        @Override
        public String logCommand() {
            
            return log;
        }

        @Override
        public String getType() {
            return Command.PLAY_CARD;
        }

        @Override
        public void cancel(Game game) {
            moveUnitCommand.undo(game);
            game.getCurrentPlayer().setMoved(true);
            LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
            game.getCardCommandFactory().resetFactory();
        }

    }
     
      public static class ScoutCommand implements CardCommandInterface {

        Card card;
        Command moveToTableCommand;
        String senderPlayerName;
        String log;

        public ScoutCommand(Card card, String senderPlayerName) {
           
            this.card = card;
            if(card != null)
                this.moveToTableCommand = new CardCommands.MoveToTableCommand(card, senderPlayerName);
            
            this.senderPlayerName = senderPlayerName;
        }

        @Override
        public void execute(Game game) {
            /*
            If card is null then we retreat after combat resolution rather than play card
            */
            if(card != null)
            {
                moveToTableCommand.execute(game);
                log = senderPlayerName + " played " + card.getCardName();
            }
            /*
            Attacking player chooses unit to pursuit
            */
            if(game.getCurrentPlayer().isActive())
            {
                game.setInfoBarText("You can look at opponent hand");
                game.setShowOpponentHand(true);
            }
             /*
            Defending player waits
            */
            else
            {
               game.setInfoBarText("Opponent can see Your hand");
            }
        }

        @Override
        public void undo(Game game) {

        }

        @Override
        public String logCommand() {
            
            return log;
        }

        @Override
        public String getType() {
            return Command.PLAY_CARD;
        }

        @Override
        public void cancel(Game game) {
     
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
            
            this.combat = new Combat(attackingUnit, attackingCard, attackTerrain, defendingUnit, defenseTerrain);
            
            Combat cloneCombat = (Combat) UnoptimizedDeepCopy.copy (combat);
            
            this.combat = cloneCombat;

        }

        @Override
        public void execute(Game game) {
            
           moveToTableCommand.execute(game);
           game.setCombat(combat);
           game.getCardCommandFactory().setAttackedUnit(game.getUnitByName(attackedUnit.getName()));
           game.getPlayerByName(senderPlayerName).setAttacked(true);
           
           
           if(!combat.getCombatType().equals(Combat.VOLLEY) && !combat.getCombatType().equals(Combat.BOMBARD) )
           {
            game.getCardCommandFactory().awakeObserver();
            game.getCardCommandFactory().notifyObservers(CardCommandFactory.ASSAULT_BEGINS);
            game.getCombat().setState(Combat.PICK_DEFENSE_CARDS);
            game.swapActivePlayer();
            LOGGER.debug(game.getCurrentPlayer().getName() + " swapActivePlayer " + game.getCurrentPlayer().isActive());
            
            if(game.getCurrentPlayer().isActive())
                 {
                 LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na CardStateHandler.MULTIPLE_PICK");
                 game.cardStateHandler.setState(CardStateHandler.MULTIPLE_PICK);
                 LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na MapInputStateHandler.NOSELECTION");
                 game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
                 }
            else
            {
                 LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na CardStateHandler.NOSELECTION");
                 game.cardStateHandler.setState(CardStateHandler.NOSELECTION);
                 LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na MapInputStateHandler.NOSELECTION");
                 game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
            }
               
           }
           /*Bombard or Volley*/
           else 
           {
            /*
            To enable roll dice action button
            */
            if(game.getCurrentPlayer().isActive())
            {
                combat.setState(Combat.PICK_SUPPORTING_CARDS);
                LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na CardStateHandler.NOSELECTION");
                game.cardStateHandler.setState(CardStateHandler.NOSELECTION);
            }
            else 
            {   
                combat.setState(Combat.PICK_SUPPORTING_CARDS);
                LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na CardStateHandler.NOSELECTION");
                game.cardStateHandler.setState(CardStateHandler.NOSELECTION);
            }
           }
           
  
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            return senderPlayerName + " attacked " +  combat.getDefendingUnit().getName()   +" with " + attackingCard.getCardName() ;
        }

        @Override
        public String getType() {
            return Command.PLAY_CARD;
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
        public String getType() {
            return Command.PLAY_CARD;
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
        public String getType() {
            return Command.PLAY_CARD;
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
        public String getType() {
            return Command.RESET_FACTORY;
        }
    }

     
     public static class SupportCardsCommand implements CardCommandInterface {

        ArrayList<Card> cards;
        String senderPlayerName;
        ArrayList<MoveToTableCommand> mttc;

       public SupportCardsCommand(ArrayList<Card> cards, String senderPlayerName) {
            this.cards = cards;
            this.senderPlayerName = senderPlayerName;
            mttc = new ArrayList<>();
            
            for(Card card:cards){
                    MoveToTableCommand mtt = new MoveToTableCommand(card, senderPlayerName);
                    mttc.add(mtt);
            }  
            
        }

        @Override
        public void execute(Game game) {
                
                /*
                Moving cards to table
                */
                for(MoveToTableCommand moveToTableCommand : mttc) 
                {
                moveToTableCommand.execute(game);
                }
                
                /*
                Setting cards to combat object to calculate combat values
                */
                
               // for(Card supportCard : game.getCardCommandFactory().getAttackingCards())
               // game.getCombat().getSupportCards().add(e)
                //game.getCombat().setSupportingUnits(cards);
                //game.getCombat().setSupportingLeader(cards);
                
                
                game.getCombat().calculateCombatValues();

                /*
                Attacker rolls dice
                */
                game.getCombat().setState(Combat.THROW_DICES);
                game.getCardCommandFactory().awakeObserver();
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.ATTACKER_ROLL_DICES);
                /*
                Swap player - attacker has action now
                */
                               
                if(game.getCurrentPlayer().isActive())
                {
                LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na CardStateHandler.MULTIPLE_PICK");
                game.cardStateHandler.setState(CardStateHandler.NOSELECTION);
                LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na MapInputStateHandler.NOSELECTION");
                game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
                }
                else
                {
                LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na CardStateHandler.NOSELECTION");
                game.cardStateHandler.setState(CardStateHandler.NOSELECTION);
                LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na MapInputStateHandler.NOSELECTION");
                game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
                }
                
            }
 
        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            String out= ""; 
            if(cards.size() > 0)
                for(MoveToTableCommand moveToTableCommand : mttc) 
                        out += moveToTableCommand.logCommand() + "\n";
            
            else
               out += "Defender played no defending cards" ;
                    
            return out;
            
        }

        @Override
        public String getType() {
            return Command.PLAY_CARD;
        }

        @Override
        public void cancel(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
     

     public static class DefendCommand implements CardCommandInterface {

        ArrayList<Card> defendCards;
        String senderPlayerName;
        String combatType;
        Combat combat;
        ArrayList<MoveToTableCommand> mttc ;
        
        
        public DefendCommand(String senderPlayerName, Combat combat) {
            this.combat=combat;
            this.combatType = combat.getCombatType();
            this.defendCards = combat.getDefenceCards();
            this.senderPlayerName = senderPlayerName;
            mttc = new ArrayList<>();
            for(Card card:defendCards){
                    MoveToTableCommand mtt = new MoveToTableCommand(card, senderPlayerName);
                    mttc.add(mtt);
            }  
            
//            Combat cloneCombat = (Combat) UnoptimizedDeepCopy.copy (combat);  //btest
//            this.combat = cloneCombat;
            
        }

        @Override
        public void execute(Game game) {
            
            game.setCombat(combat);
            switch(combatType) {
                
//                case  Combat.BOMBARD : {
//                 if (
//                         !game.getCurrentPlayer().getName().equals(senderPlayerName))
//                          game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_ACCEPTED);
//                 break;
//                }
//                case  Combat.VOLLEY : {
//                 if (
//                         !game.getCurrentPlayer().getName().equals(senderPlayerName))
//                          game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_ACCEPTED);
//                 break;
//                }
//                
                
                case Combat.ASSAULT:
                {
                /*
                Moving cards to table
                */
                if(mttc != null)
                    for(MoveToTableCommand moveToTableCommand : mttc) 
                    {
                    moveToTableCommand.execute(game);
                    }
                
                /*
                Setting cards to combat object to calculate combat values
                */
                game.getCombat().setDefenceCards(defendCards);
                game.getCombat().calculateCombatValues();
                /*
                Set Combat to play support cards
                */
                game.getCombat().setState(Combat.PICK_SUPPORTING_CARDS);
                
                game.getCardCommandFactory().awakeObserver();
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.DEFENDING_CARDS_PLAYED);
                
                game.swapActivePlayer();
                LOGGER.debug(game.getCurrentPlayer().getName() + " swapActivePlayer " + game.getCurrentPlayer().isActive());
                if(game.getCurrentPlayer().isActive())
                    {
                    LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na CardStateHandler.MULTIPLE_PICK");
                    game.cardStateHandler.setState(CardStateHandler.MULTIPLE_PICK);
                    LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na MapInputStateHandler.NOSELECTION");
                    game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
                    }
                else
                    {
                    LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na CardStateHandler.NOSELECTION");
                    game.cardStateHandler.setState(CardStateHandler.NOSELECTION);
                    LOGGER.debug(game.getCurrentPlayer().getName() + "zmiana stanu na MapInputStateHandler.NOSELECTION");
                    game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
                    }
                
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
            
            String log = "";
            if(!defendCards.isEmpty())
            {
                for(Card card:defendCards)
                {
                    log += card.getCardName() + " ";
                }
                log += "were played as defence card";
            }
            else 
                log += "No card was played as defence card";
            
            return log;
        }

        @Override
        public String getType() {
            return Command.PLAY_CARD;
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
        String log;
        ArrayList<MoveToTableCommand> mttc;
        private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(CombatOutcomeCommand.class.getName());
        
        public CombatOutcomeCommand(String senderPlayerName, Combat combat) {

            this.senderPlayerName = senderPlayerName;
            /*
            Dices are thrown in contructor of ThrowDiceCommand
            */
            mttc = new ArrayList<>();
            ThrowDiceCommand td = new ThrowDiceCommand(senderPlayerName , combat.getAttackCards());
            for(Card card:combat.getSupportCards()){
                    MoveToTableCommand mtt = new MoveToTableCommand(card, senderPlayerName);
                    mttc.add(mtt);
            }  
            if(combat.getSupportingLeader()!= null)
            {
                    MoveToTableCommand mtt = new MoveToTableCommand(combat.getSupportingLeader(), senderPlayerName);
                    mttc.add(mtt);
            }
                                        
            Combat tcombatclone = (Combat) UnoptimizedDeepCopy.copy (combat);
            ThrowDiceCommand tdclone = (ThrowDiceCommand) UnoptimizedDeepCopy.copy (td);
            this.td = tdclone;
            this.combat = tcombatclone;
        }

        @Override
        public void execute(Game game) {
           
            /*
                Moving cards to table
                */
                for(MoveToTableCommand moveToTableCommand : mttc) 
                {
                moveToTableCommand.execute(game);
                }
            
            /*
            Set cardFactorywith dices
            */
            
            td.execute(game);
            
            
            //combat.setDices(game.getCardCommandFactory().getAllDices());
            
            
            
            game.getCombat().calculateCombatValues();
            //game.setCombat(combat);

            switch(game.getCombat().getOutcome()){
            
                case  Combat.NO_EFFECT:
                
                {
                     game.getCardCommandFactory().awakeObserver();   
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_NO_RESULT);
                     log = "Combat ends with no effect";
                     game.getCombat().setState(Combat.END_COMBAT);
                     break;
                }
                
                case  Combat.DEFFENDER_TAKES_HIT:
                
                {
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getDefendingUnit().getName()).takeHit(game);
                     if(  !game.getUnitByName(combat.getDefendingUnit().getName()).isEliminated())
                     {
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_TAKES_HIT);
                         log = "Combat ends with defending unit takes a hit";
                     }
                     else 
                     {
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                         game.getCombat().setState(Combat.PURSUIT);
                         if(game.getCurrentPlayer().isActive())
                            {
                            LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT ");
                            game.mapInputHandler.setState(MapInputStateHandler.PICK_ONE_UNIT);
                            }
                        log = "Combat ends with defending unit takes a hit and is eliminated";
                         
                         
                     }
                     
                     game.getCombat().setState(Combat.END_COMBAT);
                     break;
                }
                
                case  Combat.ELIMINATE:
                
                {   
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getDefendingUnit().getName()).eliminate(game);
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                     log = "Combat ends with defending unit is eliminated";
                     game.getCombat().setState(Combat.PURSUIT);
                     if(game.getCurrentPlayer().isActive())
                        {
                        LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT ");
                        game.mapInputHandler.setState(MapInputStateHandler.PICK_ONE_UNIT);
                        }
                     
                     
                     break;
                }
                
                case Combat.ATTACKER_TAKES_HIT :
                {
                     
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getAttackingUnit().getName()).takeHit(game);
                     if(  !game.getUnitByName(combat.getAttackingUnit().getName()).isEliminated())
                     {game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_ATTACKER_TAKES_HIT);
                        log = "Combat ends with attacking unit takes a hit";
                     }
                     else 
                     {
                        game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_ATTACKER_ELIMINATE);
                        log = "Combat ends with attacking unit takes a hit and is eliminated" ;
                     }
                     
                     game.getCombat().setState(Combat.END_COMBAT);
                     break;  
                }
                /*
                
                */
                case Combat.DEFENDER_DECIDES :
                {
                     
                     game.getCardCommandFactory().awakeObserver();
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.DEFENDER_DECIDES);

                     /*
                     */
                      
                     game.swapActivePlayer();
                     LOGGER.debug(game.getCurrentPlayer().getName() + " swapActivePlayer " + game.getCurrentPlayer().isActive());
                     log = "Defending player decides";
                     game.getCombat().setState(Combat.DEFENDER_DECIDES);
                     break;  
                }
                
                case Combat.ATTACKER_DECIDES :
                {
                     game.getCardCommandFactory().awakeObserver();
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.ATTACKER_DECIDES);
                     log = "Attacking player decides" ;
                     game.getCombat().setState(Combat.ATTACKER_DECIDES);
                     break;
                }
                case Combat.HIT_AND_RETREAT :
                {
                    game.getCardCommandFactory().awakeObserver();
                    Unit defendingUnit =  game.getUnitByName(combat.getDefendingUnit().getName());
                    ArrayList<Position> retreatPosition =  game.getRetreatPositions(defendingUnit);
                    defendingUnit.takeHit(game);
                    
                    if(  !game.getUnitByName(combat.getDefendingUnit().getName()).isEliminated())
                    {
                        if(retreatPosition.size() > 0){
                         
                            game.getCardCommandFactory().notifyObservers(CardCommandFactory.DEFENDING_WITHDRAW); 
                            defendingUnit.setRetriving(true);
                            defendingUnit.setSelected(true);
                            game.getCombat().setState(Combat.WITHRDAW);
                            game.swapActivePlayer();
                            LOGGER.debug(game.getCurrentPlayer().getName() + " swapActivePlayer " + game.getCurrentPlayer().isActive());
                            if(game.getCurrentPlayer().isActive())
                            {
                            LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_MOVE_POSITION ");
                            game.mapInputHandler.setState(MapInputStateHandler.PICK_MOVE_POSITION);
                            }

                            log = "Defending unit takes a hit and choose where to retreat";
                        }
                        else          
                        {
                            game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                            game.getCombat().setState(Combat.PURSUIT);

                            if(game.getCurrentPlayer().isActive())
                            {
                            LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT ");
                            game.mapInputHandler.setState(MapInputStateHandler.PICK_ONE_UNIT);
                            }

                            log = "Combat ends with defending unit takes a hit and is eliminated with no room to retreat";
                            break;
                        }
                     
                     }
                    
                     else 
                     {
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                         log = "Combat ends with defending takes hit and is eliminated" ;
                         game.getCombat().setState(Combat.PURSUIT);
                         if(game.getCurrentPlayer().isActive())
                            {
                            LOGGER.debug(game.getCurrentPlayer().getName() + " Zmiana stanu na MapInputStateHandler.PICK_ONE_UNIT ");
                            game.mapInputHandler.setState(MapInputStateHandler.PICK_ONE_UNIT);
                            }
                     }
      
                     break;  
                }
            }
          
        //LOGGER.debug(game.getCurrentPlayer().getName() + " game.unselectAllUnits();"  );
        //game.unselectAllUnits();

        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            log = td.logCommand();
                    
                    ;
            log += "Att Val :" + combat.getAttackValue()+ " " + "Att Val :" + combat.getAttackBonus()+ " " +
                   "Def Val :" + combat.getDefenceValue()+ " " + "Def Bon :" + combat.getDefenseBonus()+ " " 
                    + log
                    ;
            return log;
        }

        @Override
        public String getType() {
            return Command.COMBAT;
        }

    }
    public static class PursuitCommand implements Command {

        String senderPlayerName;
        ArrayList<Card> pursuitCards;
        ThrowDiceCommand tdc;

        public PursuitCommand(String senderPlayerName, ArrayList<Card> pursuitCards) {
            this.senderPlayerName = senderPlayerName;
            this.pursuitCards =  pursuitCards;
           /*
            Set this locally to create card.
            */
            for(Card pursuitCard:pursuitCards)
            {
            pursuitCard.setPlayingCardMode(Card.PURSUIT);
            }
         
            tdc = new ThrowDiceCommand(senderPlayerName, pursuitCards);
            
            
        }
        
        
        @Override
        public void execute(Game game) {
        
            tdc.execute(game);
            ArrayList<String> pursuitResults = new ArrayList<>();
            boolean pursuitSucceed = false;
            
            for(Card pursuitCard: pursuitCards)    
                {
                game.getPlayerByName(senderPlayerName).getTablePile().getCard(pursuitCard).setPlayingCardMode(Card.PURSUIT);
                pursuitResults.add( game.getCombat().getPursuitOutcome( game.getCardFromTable(pursuitCard) ));
                }
            
            for(String outcome: pursuitResults)    
            {
                switch(outcome)
                {
                    case  Combat.NO_EFFECT:
                    {
                        break;
                    }
                    case  Combat.DEFFENDER_TAKES_HIT:
                    {
                         game.getUnit(game.getCombat().getDefendingUnit()).takeHit(game);
                         pursuitSucceed = true;
                         break;
                    }
                }
            }
            
            if(pursuitSucceed)
            {
                game.getCardCommandFactory().awakeObserver();
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.PUSRUIT_SUCCEDED);
            }
            else {
                game.getCardCommandFactory().awakeObserver();   
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.PUSRUIT_FAILED);
               }
           
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
            
            
            String log= senderPlayerName + " has pursuit and inflict " + " hits /n" ;
            log += tdc.logCommand();
            return log;
        }

        @Override
        public String getType() {
            return Command.COMBAT;
        }
    
    }
  public static class RedoubtCommand implements CardCommandInterface {

        String log;
        String senderPlayerName;
        Unit redoubtUnit;
        Card redoubtCard;
        MoveToTableCommand moveToTable;
        
        
        public RedoubtCommand(String senderPlayerName, Unit redoubtUnit, Card redoubtCard) {
             this.senderPlayerName = senderPlayerName;
            this.redoubtUnit = redoubtUnit;
            this.redoubtCard = redoubtCard;
            moveToTable = new MoveToTableCommand(redoubtCard, senderPlayerName);
        }
       
        @Override
        public void cancel(Game game) {
            
            Unit unit = game.getUnit(redoubtUnit);
            game.getMap().getTerrainAtPosition(unit.getPosition()).setRedoubt(false);
            
        }

        @Override
        public void execute(Game game) {
         
            moveToTable.execute(game);
            
            Unit unit = game.getUnit(redoubtUnit);
            
            game.getMap().getTerrainAtPosition(unit.getPosition()).setRedoubt(true);
            
            log = senderPlayerName + " played redoubt ";
            
            game.getCardCommandFactory().setPlayingCard(null);
            
            
        }

        @Override
        public void undo(Game game) {
            Unit unit = game.getUnit(redoubtUnit);
            game.getMap().getTerrainAtPosition(unit.getPosition()).setRedoubt(false);
        }

        @Override
        public String logCommand() {
            return log;
        }

        @Override
        public String getType() {
            return Command.REDOUBT;
        }
   

  }
  
    public static class SkirmishCommand implements CardCommandInterface {

        String log;
        String senderPlayerName,opponentPlayerName;
        Card skirmishCard;
        MoveToTableCommand moveToTable;
        Command moveUnitCommand;
        /*
        The Active Player returns the Unit Card
        used to initiate the combat into their hand. All cards which were played by
        the Defending Player are discarded. The attacking unit may be moved up to 2
        squares in any combination of directions (except diagonally). The Active Player
        may not play a Leader Card during the same combat as a Skirmish card. The
        Active Player may not initiate another combat this player turn. A Skirmish card
        may not be played at all during an Ambush.
        */
        
        public SkirmishCommand(String senderPlayerName, String opponentPlayerName, Card skirmishCard, Command moveUnitCommand) {
            this.senderPlayerName = senderPlayerName;
            this.opponentPlayerName = opponentPlayerName;
            this.skirmishCard = skirmishCard;
            moveToTable = new MoveToTableCommand(skirmishCard, senderPlayerName);
            this.moveUnitCommand = moveUnitCommand;
           
        }
       
        @Override
        public void cancel(Game game) {
            
           
            
        }

        @Override
        public void execute(Game game) {
         
            moveToTable.execute(game);
            log = senderPlayerName + " played skirmish ";
            
            /*
            Return attacking card to Hand
            */
            Player player  = game.getPlayerByName(senderPlayerName);
            Combat combat = game.getCombat();
            combat.setState(Combat.SKIRMISH);
            Card attackingCard = combat.getInitAttackCard();
           
            player.getTablePile().moveCardTo(attackingCard, player.getHand());
            
            /*
            Clean defending cards
            */
            game.getPlayerByName(opponentPlayerName).getTablePile().moveTopXCardsTo(
            game.getPlayerByName(opponentPlayerName).getTablePile().size(), 
            game.getPlayerByName(opponentPlayerName).getDiscardPile()
            );      
            
            
            /*
            Move unit
            */
            if(moveUnitCommand != null)
                moveUnitCommand.execute(game);
            
            //Set action button and info bar
            game.getCardCommandFactory().awakeObserver();   
            game.getCardCommandFactory().notifyObservers(CardCommandFactory.SKIRMISH_PLAYED);
            
            game.getCombat().endCombat(game);
           
            
        }

        @Override
        public void undo(Game game) {
            
        }

        @Override
        public String logCommand() {
            return log;
        }

        @Override
        public String getType() {
            return Command.SKIRMISH;
        }
   

  }
}
