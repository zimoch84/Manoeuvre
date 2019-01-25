/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.commands;

import java.util.ArrayList;
import manouvre.events.EventType;
import manouvre.game.Card;
import manouvre.game.Dice;
import manouvre.game.Game;
import manouvre.interfaces.Command;
import manouvre.network.server.UnoptimizedDeepCopy;


/**
 *
 * @author Piotr
 */
public class ThrowDiceCommand implements Command{

    String playerName;
    ArrayList<Card> cards;
    ArrayList<Dice> d6dices;
    ArrayList<Dice> d8dices;
    ArrayList<Dice> d10dices;
    
    public ThrowDiceCommand(String playerName, ArrayList<Card> cards) {
    this.playerName = playerName;
    
    ArrayList<Card> cardsClone = (ArrayList<Card>) UnoptimizedDeepCopy.copy (cards);
    
    this.cards = cardsClone;
    
    d6dices = new ArrayList<>();
    d8dices = new ArrayList<>();
    d10dices = new ArrayList<>();
    
    if(!cards.isEmpty())
    for(Card checkCard: this.cards )
        {
        ArrayList<Dice> dices = new ArrayList<>();
        if(checkCard.getType() != Card.LEADER)
        switch(checkCard.getUnitDiceValue()){
        
            case DICE1d6:{
                dices.add(new Dice(Dice.Type.D6));
                checkCard.setDices(dices);
                d6dices.addAll(dices);
                break;
            }
            case DICE2d6:{
                dices.add(new Dice(Dice.Type.D6));
                dices.add(new Dice(Dice.Type.D6));
                checkCard.setDices(dices);
                d6dices.addAll(dices);
                break;
            }
            case DICE1d8:{
                dices.add(new Dice(Dice.Type.D8));
                checkCard.setDices(dices);
                d8dices.addAll(dices);
                break;
            }
            case DICE2d8:{
                dices.add(new Dice(Dice.Type.D8));
                dices.add(new Dice(Dice.Type.D8));
                checkCard.setDices(dices);
                d8dices.addAll(dices);
                break;
            }
            case DICE1d10:{
                dices.add(new Dice(Dice.Type.D10));
                checkCard.setDices(dices);
                d10dices.addAll(dices);
                break;
            }
            case DICE2d10:{
                dices.add(new Dice(Dice.Type.D10));
                dices.add(new Dice(Dice.Type.D10));
                checkCard.setDices(dices);
                d10dices.addAll(dices);
                break;
            }
        }
    }
    }
    @Override
    public void execute(Game game) {
       
        
        for(Card card:cards)
        {
        game.getCardFromTable(card).setDices(card.getDices());
        
        }

        ArrayList<Dice> allDice = new ArrayList<>();
        
        allDice.addAll(d6dices);
        allDice.addAll(d8dices);
        allDice.addAll(d10dices);
        
        game.getCombat().setDices(allDice);
        
        game.notifyAbout(EventType.COMBAT_DICE_ROLLED);
        
    }

    @Override
    public void undo(Game game) {
   
        
    }
    
    @Override
    public String toString(){
    return "THROW_DICE_COMMAND";
    }
    
    @Override
    public String logCommand(){
        
        String out = playerName + " dices: d6 [";
        if(!d6dices.isEmpty())
        for(Dice dice : d6dices)
        {  
               out +=  String.valueOf(dice.getResult() ) + ",";
        }   
        out+= "] d8 [";
        if(!d8dices.isEmpty())
        for(Dice dice : d8dices)
        {  
               out +=  String.valueOf(dice.getResult() ) + ",";
        }   
        out+= "] d10 [";
        if(!d10dices.isEmpty())
        for(Dice dice : d10dices)
        {  
               out +=  String.valueOf(dice.getResult() ) + ",";
        }   
        out+= "] ";
       
        return out;
    
    }

    @Override
    public Type getType() {
        return Command.Type.THROW_DICE;
    }
    
    
    
}
