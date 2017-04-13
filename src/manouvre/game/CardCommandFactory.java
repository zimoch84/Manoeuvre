/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import manouvre.game.commands.CardCommands;
import manouvre.game.interfaces.Command;

/**
 * Class to serve for whole card flow in game.
 * @author Piotr
 */
public class CardCommandFactory implements Serializable{
    
    Game game;
    Command attachedCommand, cardCommand;
     
    Card playingCard;
    
    ArrayList<Position> attackingPositions;

    Unit selectedUnit;
    
    public CardCommandFactory(Game game) {
        
        this.game = game;
    }

    
    public ArrayList<Position> getAttackingPositions() {
        return attackingPositions;
    }

    public void setAttackingPositions(ArrayList<Position> attackingPositions) {
        this.attackingPositions = attackingPositions;
    }

    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    public void setSelectedUnit(Unit selectedUnit) {
        this.selectedUnit = selectedUnit;
    }

    public Command getAttachedCommand() {
        return attachedCommand;
    }

    public void setAttachedCommand(Command attachedCommand) {
        this.attachedCommand = attachedCommand;
    }

    public Card getPlayingCard() {
        return playingCard;
    }

    public void setPlayingCard(Card playingCard) {
        this.playingCard = playingCard;
    }
    
    public void resetPlayingCard(){
        this.playingCard = null;
    }
    
        /*
    Funtion to get current playing card
    */
    public Card getCurrentPlayedCard(){
        
        return playingCard;
    
    }
    
    public void calculateAttackingPositions(){
        ArrayList<Position> attackPossiblePositions;
        ArrayList<Position>  attackPositions  = new ArrayList<Position>();      
        
        

        if(playingCard.getPlayingCardMode()== Card.ASSAULT 
        || playingCard.getPlayingCardMode()== Card.VOLLEY)
            {
            attackPossiblePositions= game.getPossibleAssault(selectedUnit);
            for(Position checkPosition : attackPossiblePositions)
            {
                if(game.checkOpponentPlayerUnitAtPosition(checkPosition))
                    attackPositions.add(checkPosition);
            }

                setAttackingPositions(attackPositions);
            }
        else if (playingCard.getPlayingCardMode()== Card.BOMBARD) 
        {    
        {
            attackPossiblePositions= game.getLOS(selectedUnit, 2);
            for(Position checkPosition : attackPossiblePositions)
            {
                if(game.checkOpponentPlayerUnitAtPosition(checkPosition))
                    attackPositions.add(checkPosition);
            }

                setAttackingPositions(attackingPositions);
            }
        }

   }   
            
    
    public void resetFactory()
    {
        setAttachedCommand(null);
        setAttackingPositions(null);
        setPlayingCard(null);
        setSelectedUnit(null);
        
    }
        
    /*
    Crate card command based on Card
    */
    public Command createCardCommand() {
    
    playingCard= getCurrentPlayedCard();
        
    switch (playingCard.getCardType() ) {
    
    
        case Card.FORCED_MARCH : return new CardCommands.ForcedMarchCommand(attachedCommand, playingCard, game.getCurrentPlayer().getName()) ; 
        default: return new CardCommands.MoveToTableCommand(playingCard, game.getCurrentPlayer().getName()) ; //if any card selected temp
//throw new Exception("There is no such card type");
    }
    }
    
    
    
    
}
