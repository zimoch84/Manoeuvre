/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import manouvre.interfaces.MapState;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.Card;
import manouvre.game.CardCommandFactory;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;
import manouvre.commands.AdvanceUnitCommand;
import manouvre.commands.CardCommands;
import manouvre.commands.CommandQueue;
import manouvre.events.EventType;
import manouvre.interfaces.Command;
import manouvre.gui.CustomDialog;
import manouvre.gui.GameWindow;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author xeon
 */
public class MapPickUnitByCardState implements MapState, Serializable{
    
    Card playingCard;
    CardCommandFactory ccf;
    
    
    public MapPickUnitByCardState(Card playingCard, CardCommandFactory ccf) {
        this.playingCard = playingCard;
        this.ccf = ccf;
    }
    
private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(MapPickUnitByCardState.class.getName());     
@Override
public void handleInput(Position pos, Game game, CommandQueue cmdQueue , MapStateHandler handler) {

    if(game.positionCalculator.getUnitsPositionToSelectByCard(playingCard).contains(pos))
         handleUnitsHitsPositionSituation( game,  pos,  cmdQueue) ;

}

private void handleUnitsHitsPositionSituation(Game game, Position pos, CommandQueue cmdQueue)    {
    
    Unit pickedUnit = game.getCurrentPlayerUnitAtPosition(pos);
        
    switch(playingCard.getCardType()){
        case Card.HQCARD:
            switch(playingCard.getHQType()){
                case Card.REDOUBDT:
                    CardCommands.RedoubtCommand redoubtCommand = 
                            new CardCommands.RedoubtCommand(
                                    game.getCurrentPlayer().getName(),
                                    pickedUnit, 
                                    playingCard);
                    cmdQueue.storeAndExecuteAndSend(redoubtCommand);
                break;    
            }
        break;    

        case Card.UNIT:
            Combat combat = game.getCombat() ;
            /*
            If its withdraw then select attacking unit position
            */
            if(combat.getState() == Combat.PURSUIT) 
            {
                pickedUnit.setSelected(true);
                /*Chosen unit to advance*/
                pickedUnit.setAdvanced(true);
                Command advanceCommand = 
                   new AdvanceUnitCommand(game.getCurrentPlayer().getName(),
                       pickedUnit , 
                       game.getCombat().getDefendingUnit().getPosition(),
                       game.getCombat().getPursuitCards(game)
                       );
                cmdQueue.storeAndExecuteAndSend(advanceCommand);
                game.notifyAbout(EventType.PICKED_ADVANCE);
            }
        break;
    } 
}
}
