/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.state;

import manouvre.interfaces.MapState;
import java.io.Serializable;
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
import manouvre.game.Terrain;
import manouvre.interfaces.Command;
import manouvre.gui.CustomDialogFactory;
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
        
    switch(playingCard.getType()){
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
                case Card.SUPPLY:
                    switch(game.getPhase()){
                        case Game.MOVE:
                            pickedUnit.setSelected(true);
                            game.notifyAbout(EventType.SUPPLY_SELECTED);
                        break;
                        case Game.RESTORATION:
                             Command restoreCommand = ccf.createRestorationCommand(playingCard, pickedUnit);
                             CustomDialogFactory.showSureToPlayCardDialog(cmdQueue, restoreCommand, game);
                        break;        
                            
                            
                    }
                break;
                default : System.err.println("Nie obslugujemy tej karty " + playingCard.getHQType() );    
            }
        break;    

        case Card.UNIT:
            switch(game.getPhase()){
                case Game.COMBAT:
                    Combat combat = game.getCombat() ;
                    switch(combat.getState()){
                        case Combat.PURSUIT:
                            pickedUnit.setSelected(true);
                            pickedUnit.setAdvanced(true);
                            Command advanceCommand = 
                               new AdvanceUnitCommand(game.getCurrentPlayer().getName(),
                                   pickedUnit , 
                                   game.getCombat().getDefendingUnit().getPosition(),
                                   game.getCombat().getPursuitCards(pickedUnit)
                                   );
                            cmdQueue.storeAndExecuteAndSend(advanceCommand);
                            game.notifyAbout(EventType.PICKED_ADVANCE);
                        break;
                        case Combat.INITIALIZING_COMBAT:
                            
                            Unit defendingUnit = game.getUnitAtPosition(pos);
                            combat.setDefendingUnit(defendingUnit);
                            Terrain defenceTerrain = game.getMap().getTerrainAtPosition(defendingUnit.getPosition());
                            combat.setDefenseTerrain(defenceTerrain);
                            
                            Command attackCommand = ccf.createCardCommand(playingCard);
                            CustomDialogFactory.showSureToPlayCardDialog(cmdQueue, attackCommand, game);
                        break;    
                        default: System.err.println("MapPickUnitByCardStatt.handleINput() Nie obslugujemy tego stanu combat: " + combat.getState());

                    break;
                    }
                break;   
                case Game.RESTORATION:
                    Unit restoreUnit  = game.getUnitAtPosition(pos);
                    restoreUnit.setSelected(true);
                    Command restoreCommand = ccf.createRestorationCommand(playingCard, restoreUnit);
                    CustomDialogFactory.showSureToPlayCardDialog(cmdQueue, restoreCommand, game);
                    
                break;    
                
                default: System.err.println("Nie obslugujemy tej fazy gry: " + game.getPhase());
            }
        break;
    } 
}

    @Override
    public String toString() {
        return MapStateHandler.PICK_UNIT_BY_CARD;
    }

}
