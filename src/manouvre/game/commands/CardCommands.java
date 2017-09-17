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
                moveToTableCommand.execute(game);
            
            moveUnitCommand.execute(game);
            
            game.getCombat().setState(Combat.WITHRDAW);
            
            /*
            Setting non active after choosing withdraw position on map to avoid 
            */
            if(game.getCurrentPlayer().getName().equals(senderPlayerName))
                
            {  
                //game.getCurrentPlayer().setActive(false);
               
            }
        
            else
            { 
                 game.getCardCommandFactory().notifyObservers(CardCommandFactory.DEFENDING_WITHDRAW);
                 
            }
            game.swapActivePlayer();
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
            
            this.combat = new Combat(attackingUnit, attackingCard, attackTerrain, defendingUnit, defenseTerrain);
            
            Combat cloneCombat = (Combat) UnoptimizedDeepCopy.copy (combat);
            
            this.combat = cloneCombat;

        }

        @Override
        public void execute(Game game) {
            
           moveToTableCommand.execute(game);
           
           if(combat.getCombatType() != Combat.VOLLEY && combat.getCombatType() != Combat.BOMBARD )
           {
           combat.setState(Combat.PICK_DEFENSE_CARDS);
            
            if(game.getCurrentPlayer().getName().equals(senderPlayerName))
            {
                //TODO: Set info BAR
            }
            else{
                game.getCardCommandFactory().awakeObserver();
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.ATTACK_DIALOG);
             }
           /*
           Switch to enable interactions
           */
           game.swapActivePlayer();
           }
           else 
           {
            combat.setState(Combat.PICK_SUPPORTING_CARDS);
            game.cardStateHandler.setState(CardStateHandler.NOSELECTION);
           }
           game.setCombat(combat);
           game.getCardCommandFactory().setAttackedUnit(game.getUnitByName(attackedUnit.getName()));
           game.getPlayerByName(senderPlayerName).setAttacked(true);
  
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
        ArrayList<MoveToTableCommand> mttc;

        public MoveDefensiveCardsToTableCommand(ArrayList<Card> cards, String senderPlayerName) {
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
                game.getCombat().setDefenceCards(cards);
                game.getCombat().calculateCombatValues();
                
                game.getCombat().setState(Combat.PICK_SUPPORTING_CARDS);
                
                if (game.getCurrentPlayer().getName().equals(senderPlayerName)) {
                
                } 
                else {

                game.getCardCommandFactory().awakeObserver();
                game.getCardCommandFactory().notifyObservers(CardCommandFactory.DEFENDING_CARDS_PLAYED);
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
        Command moveToTableCommand;

        public DefendCommand(String senderPlayerName, Combat combat) {
            this.combat=combat;
            this.combatType = combat.getCombatType();
            this.defendCards = combat.getDefenceCards();
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
                 if (
                         !game.getCurrentPlayer().getName().equals(senderPlayerName))
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
            /*
            Switch active player
            */
            game.swapActivePlayer();
                       
            
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
        String log;
        
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
                     log = "Combat ends with no effect";
                     break;
                }
                
                case  Combat.DEFFENDER_TAKES_HIT:
                
                {
                     
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getDefendingUnit().getName()).takeHit();
                     if(  !game.getUnitByName(combat.getDefendingUnit().getName()).isEliminated())
                     {game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_TAKES_HIT);
                     log = "Combat ends with defending unit takes a hit";
                     }
                     else 
                     {
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                      log = "Combat ends with defending unit takes a hit and is eliminated";
                     }
                     break;
                }
                
                case  Combat.ELIMINATE:
                
                {   
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getDefendingUnit().getName()).eliminate();
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                     log = "Combat ends with defending unit is eliminated";
                     break;
                }
                
                case Combat.ATTACKER_TAKES_HIT :
                {
                     
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getAttackingUnit().getName()).takeHit();
                     if(  !game.getUnitByName(combat.getAttackingUnit().getName()).isEliminated())
                     {game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_ATTACKER_TAKES_HIT);
                        log = "Combat ends with attacking unit takes a hit";
                     }
                     else 
                     {
                        game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_ATTACKER_ELIMINATE);
                        log = "Combat ends with attacking unit takes a hit and is eliminated" ;
                     }
                     break;  
                }
                /*
                Temporary we assume that defender always choose to retreat
                */
                case Combat.DEFENDER_CHOSES :
                {
                     
                     game.getCardCommandFactory().awakeObserver();
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.DEFENDER_DECIDES);
                     
                     
                     Unit defendingUnit =  game.getUnitByName(combat.getDefendingUnit().getName());
                     ArrayList<Position> retreatPosition =  game.getRetreatPositions(defendingUnit);
                     /*
                     TODO - create full retreat dialog with defender choice
                     Currently - take first possible position
                     */
                      
                     
                     
                     if(retreatPosition.size() > 0){
                         
                         log = "Combat ends with defending unit retreats" ;
                         MoveUnitCommand moveUnitCommand = new MoveUnitCommand(senderPlayerName  , defendingUnit,  retreatPosition.get(0));
                         moveUnitCommand.execute(game);
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.DEFENDING_WITHDRAW);
                     }
                     
                     else 
                     {
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                         log = "Combat ends with defending unit has no place to retreat and is eliminated" ;
                         game.getUnitByName(combat.getDefendingUnit().getName()).eliminate();
                     }
                     
                     
                    
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
                     log = "Combat ends with attacking player decide to hit or forced to retreat" ;
                     break;
                }
                case Combat.HIT_AND_RETREAT :
                {
                     
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(combat.getDefendingUnit().getName()).takeHit();
                     
                     if(  !game.getUnitByName(combat.getDefendingUnit().getName()).isEliminated())
                     {game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_TAKES_HIT);
                     log = "Combat ends with defending unit takes a hit and retreats";
                     }
                     else 
                     {
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                         log = "Combat ends with defending unit takes a hit and is eliminated";
                         break;
                     }
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
                     
                    else 
                     {
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                         log = "Combat ends with defending unit has no place to retreat and is eliminated" ;
                         game.getUnitByName(combat.getDefendingUnit().getName()).eliminate();
                     }
      
                     game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_TAKES_HIT);
                     break;  
                }
            }
          game.getCombat().setState(Combat.END_COMBAT);
          
          game.unselectAllUnits();
          game.mapInputHandler.setState(MapInputStateHandler.NOSELECTION);
        }

        @Override
        public void undo(Game game) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String logCommand() {
     
            log = "Att Val :" + combat.getAttackValue()+ " " + "Att Val :" + combat.getAttackBonus()+ " " +
                   "Def Val :" + combat.getDefenceValue()+ " " + "Def Bon :" + combat.getDefenseBonus()+ " " 
                    + log
                    ;
            return log;
        }

        @Override
        public int getType() {
            return Param.COMBAT;
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

            int index = 0 ;
            
            for(Card pursuitCard: pursuitCards)    
                {
               
                game.getPlayerByName(senderPlayerName).getTablePile().getCard(pursuitCard).setPlayingCardMode(Card.PURSUIT);
               
                
                int outcome =game.getCombat().getPursuitOutcome(pursuitCard, 
                        tdc.d6dices.get(index));
                   
                switch(outcome)
                {
                case  Combat.NO_EFFECT:
                {
                    game.getCardCommandFactory().awakeObserver();   
                    game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_NO_RESULT);
                    break;
                }
                case  Combat.DEFFENDER_TAKES_HIT:
                {
                     game.getCardCommandFactory().awakeObserver();
                     game.getUnitByName(game.getCombat().getDefendingUnit().getName()).takeHit();
                     if(  !game.getUnitByName(game.getCombat().getDefendingUnit().getName()).isEliminated())
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_TAKES_HIT);
                     else 
                         game.getCardCommandFactory().notifyObservers(CardCommandFactory.COMBAT_DEFENDER_ELIMINATE);
                     break;
                }
                }
                
                
                }
            
            /*
            End Combat
            */
            game.getCombat().endCombat(game);
       
            game.unselectAllUnits();
            
              
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
        public int getType() {
            return Param.COMBAT;
        }
    
    }
 
}
