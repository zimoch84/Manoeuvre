/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game.commands;

import java.util.ArrayList;
import manouvre.game.Card;
import manouvre.game.Dice;
import manouvre.game.Game;
import manouvre.game.Param;
import manouvre.game.interfaces.Command;
import manouvre.game.interfaces.DiceInterface;

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
    this.cards = cards;
    
    d6dices = new ArrayList<>();
    d8dices = new ArrayList<>();
    d10dices = new ArrayList<>();
    
    if(!cards.isEmpty())
    for(Card checkCard: cards )
        {
        switch(checkCard.getUnitDiceValue()){
        
            case DiceInterface.DICE1d6:{
                d6dices.add(new Dice(Dice.D6));
                break;
            }
            case DiceInterface.DICE2d6:{
                d6dices.add(new Dice(Dice.D6));
                d6dices.add(new Dice(Dice.D6));
                break;
            }
            case DiceInterface.DICE1d8:{
                d8dices.add(new Dice(Dice.D8));
                break;
            }
            case DiceInterface.DICE2d8:{
                d8dices.add(new Dice(Dice.D8));
                d8dices.add(new Dice(Dice.D8));
                break;
            }
            case DiceInterface.DICE1d10:{
                d10dices.add(new Dice(Dice.D10));
                break;
            }
            case DiceInterface.DICE2d10:{
                d10dices.add(new Dice(Dice.D10));
                d10dices.add(new Dice(Dice.D10));
                break;
            }
        }
    }
    }
    @Override
    public void execute(Game game) {
       
        
    
        game.getCardCommandFactory().setD6dices(d6dices);
        game.getCardCommandFactory().setD8dices(d8dices);
        game.getCardCommandFactory().setD10dices(d10dices);
  
        
        
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
    public int getType() {
        return Param.THROW_DICE;
    }
    
    
    
}
