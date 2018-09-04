/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author piotr_grudzien
 */
public class PositionCalculator implements Serializable{
    
    Game game;

    public PositionCalculator(Game game) {
        this.game = game;
    }
    
    
    
    public ArrayList<Position> getUnitsPositionToSelectByCard(Card card) 
    {
        ArrayList<Position> returnPositions = new ArrayList<>();
        switch (card.getType()) {
            case Card.HQCARD:
                if (card.getHQType() == Card.SUPPLY) {
                    if (game.getPhase() == Game.MOVE) {
                        return getCurrentPlayerNotMovedUnits();
                    }
                    if (game.getPhase() == Game.RESTORATION) {
                        return getCurrentPlayerInjuredUnitPositions();
                    }
                }
                /*
                Defending player
                 */
                if (card.getHQType() == Card.WITHDRAW) {
                    returnPositions.add(game.getCombat().getDefendingUnit().getPosition());
                    return returnPositions;
                }
                if (card.getHQType() == Card.REDOUBDT) {
                    return game.getCurrentPlayer().getArmyPositions();
                }
                break;
            case Card.UNIT:
                switch(game.getPhase()){
                    case Game.COMBAT:
                    Combat combat = game.getCombat();
                    switch(combat.getState()){
                        case Combat.COMBAT_NOT_INITIALIZED:
                        case Combat.INITIALIZING_COMBAT:    
                            if(card.getPlayingCardMode() != Card.NO_TYPE )
                            {
                                return getAttackingPositions(card, game.getUnitByCard(card));    
                            }
                        break;
               
                        case Combat.PURSUIT:
                            returnPositions.add(combat.getAttackingUnit().getPosition());
                            return returnPositions;
                        default:
                            System.err.println("Position Calculator Nie obslugujemy tego stanu combat: " + combat.getState() );

                    }
                    break;
                    /*
                    If its withdraw then select attacking unit position
                    TODO - supply with other ant not require to advance
                     */
                    case Game.RESTORATION:
                        ArrayList<Position> positions = new ArrayList<>();
                        positions.add(game.getCurrentPlayerUnitByCard(card).getPosition());
                        return positions;
                }
            break;
            case Card.LEADER:
                switch(game.getPhase()){
                    case Game.COMBAT:
                    Combat combat = game.getCombat();
                    switch(combat.getState()){
                        case Combat.PICK_SUPPORT_UNIT:
                          return getPossibleSupportingUnitsPositions();
                      
                        case Combat.PURSUIT:
                            returnPositions.add(combat.getAttackingUnit().getPosition());
                            return returnPositions;
                    }
                    break;
                    case Game.RESTORATION:
                        return getCurrentPlayerInjuredUnitPositions();
                    }
            break;
        }
        
        return returnPositions;
    }

    public ArrayList<Position> getCurrentPlayerAvalibleMoveUnitPositions() {
        Unit selectedUnit;
        switch (game.getPhase()) {
            case Game.SETUP:
                    return getSetupPossibleMovement();
            case Game.DISCARD:
                return new ArrayList<>();
            case Game.MOVE:
                if (game.freeMove) 
                    return getSetupPossibleMovement();
                selectedUnit = game.getSelectedUnit();
                ArrayList<Position> movePositions = game.getPossibleMovement(selectedUnit);
                return movePositions;
            case Game.COMBAT:
                switch (game.getCombat().getState()) {
                    case Combat.WITHRDAW:
                        {
                        if (game.getUnit(game.getCombat().getDefendingUnit()).isRetriving()) {
                            ArrayList<Position> retreatPosition = getRetreatPositions(game.getUnit(game.getCombat().getDefendingUnit()));
                            return retreatPosition;
                        }
                        }
                }
        }
        return new ArrayList<>();
    }
    /*
    Returns reference to searched Unit
     */

    public ArrayList<Position> getSetupPossibleMovement() {
        ArrayList<Position> moves;
        moves = new ArrayList<>();
        int maxRow = game.getCurrentPlayer().isHost() ? Position.ROW_6 : Position.ROW_1;
        for (Terrain terrains : game.getMap().getTerrainz()) {
            if (game.getCurrentPlayer().isHost()) {
                if (terrains.getPosition().getY() < Position.ROW_7 && terrains.isPassable()) {
                    moves.add(terrains.getPosition());
                }
            } else if (terrains.getPosition().getY() > Position.ROW_1 && terrains.isPassable()) {
                moves.add(terrains.getPosition());
            }
        }
        return moves;
    }

    public ArrayList<Position> getCurrentPlayerNotMovedUnits() {
        ArrayList<Position> units = new ArrayList<>();
        for (Unit unitSearch : game.getCurrentPlayer().getNotKilledUnits()) {
            if (!unitSearch.hasMoved()) {
                units.add(unitSearch.getPosition());
            }
        }
        return units;
    }

    public ArrayList<Position> getCurrentPlayerAvalibleUnitToSelect() {
        switch (game.getPhase()) {
            case Game.SETUP:
                return game.getCurrentPlayer().getArmyPositions();
            case Game.DISCARD:
                return new ArrayList<>();
            case Game.MOVE:
                return game.getCurrentPlayer().getArmyPositions();
            case Game.COMBAT:
                ArrayList<Position> possiblePositions = new ArrayList<>();
                switch (game.getCombat().getState()) {
                    case Combat.COMBAT_NOT_INITIALIZED:
                        return game.getCurrentPlayer().getArmyPositions();
                    case Combat.WITHRDAW:
                        if (game.getCurrentPlayer().hasAttacked()) {
                            possiblePositions.add(game.getCombat().getAttackingUnit().getPosition());
                            return possiblePositions;
                        } else {
                            possiblePositions.add(game.getCombat().getDefendingUnit().getPosition());
                            return possiblePositions;
                        }
                    case Combat.PURSUIT:
                        {
                            for (Unit pursueUnit : game.getCombat().getUnitThatCanAdvance()) {
                                possiblePositions.add(pursueUnit.getPosition());
                            }
                            return possiblePositions;
                        }
                    case Combat.PICK_SUPPORT_UNIT:
                        return getPossibleSupportingUnitsPositions();
                }
                break;
            case Game.RESTORATION:
                return game.getCurrentPlayerInjuredUnitPositions();
        }
        return new ArrayList<>();
    }

    public ArrayList<Position> getCurrentPlayerInjuredUnitPositions() {
        ArrayList<Position> units = new ArrayList<>();
        for (Unit unitSearch : game.getCurrentPlayer().getNotKilledUnits()) {
            if (unitSearch.isInjured()) {
                units.add(unitSearch.getPosition());
            }
        }
        return units;
    }

    /*
    Get unit positions that  can join attack , this is for selection purpose only
     */
    public ArrayList<Position> getPossibleSupportingUnitsPositions() {
        /*
        get attacking Units position that are adjenced to the defending one
         */
        Unit attackingUnit = game.getCombat().getAttackingUnit();
        
        ArrayList<Position> possiblePositions = game.getOneSquarePositions(game.getCombat().getDefendingUnit().getPosition());
        ArrayList<Position> supportingPositions = new ArrayList<>();
        Position atackingPosition = game.getCombat().getAttackingUnit().getPosition();
        for (Position checkPosition : possiblePositions) {
            if (game.isCurrentPlayerUnitAtPosition(checkPosition) && !checkPosition.equals(atackingPosition)) 
                if(game.getUnitAtPosition(checkPosition).getOwner().getNation() == attackingUnit.getOwner().getNation())
            
                supportingPositions.add(checkPosition);
        }
        return supportingPositions;
    }

    public ArrayList<Position> getRetreatPositions(Unit unit) {
        /*
        A Retreat result will force the affected unit to vacate its current square. A unit
        that Retreats is moved one square away from its current location. The choice of
        the direction of the Retreat must be directly towards the unit’s starting edge of
        the battlefield. If that square is blocked (by either friendly or enemy units), then
        the Retreat must be to either flank square, retreating player’s choice. A flank
        square is one not toward either side’s Starting Edge. If all three of these squares
        are blocked or are the map edge, then and only then the unit may retreat towards
        the enemy’s Starting Edge. If all four squares are blocked or are the map edge,
        then the unit may not retreat and is Eliminated instead.
         */
        ArrayList<Position> possibleMovements = game.getOneSquareMovements(unit.getPosition());
        /*
        If there is no room for movement return null
         */
        if (possibleMovements.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<Position> retreatMovements = new ArrayList<>();
        /*
        If player is a host we move increasing y else we decrease y
         */
        int deltaMove = unit.getOwner().isHost() ? -1 : 1;
        /*
        Checking possible retreat to unit starting edge
         */
        for (Position checkRetreatPos : possibleMovements) {
            /*
            If we have space to move back
             */
            if (checkRetreatPos.getY() == unit.getPosition().getY() + deltaMove) {
                retreatMovements.add(checkRetreatPos);
                return retreatMovements;
            }
        }
        for (Position checkRetreatPos : possibleMovements) {
            /*
            If we have space to move aside
             */
            if (checkRetreatPos.getX() == unit.getPosition().getX() + 1 || checkRetreatPos.getX() == unit.getPosition().getX() - 1) {
                retreatMovements.add(checkRetreatPos);
            }
        }
        /*
        If we have side way movements return them
         */
        if (!retreatMovements.isEmpty()) {
            return retreatMovements;
        } else {
            return possibleMovements;
        }
    }

    public ArrayList<Position> getMovePositionsByCard(Card playingCard, Unit movingUnit) {
        ArrayList<Position> movePositions = new ArrayList<>();
        if (movingUnit != null) {
            switch (playingCard.getType()) {
                case Card.HQCARD:
                    switch (playingCard.getHQType()) {
                        case Card.FORCED_MARCH:
                            movePositions = game.getOneSquareMovements(movingUnit.getPosition());
                        break;
                        case Card.SUPPLY:
                            movePositions = game.getPossibleMovement(movingUnit);
                        break;
                        case Card.WITHDRAW:
                            movePositions = game.positionCalculator.getRetreatPositions(movingUnit);
                        break;
                        case Card.SKIRMISH:
                            movePositions = game.getTwoSquareMovements(movingUnit.getPosition());
                        break;
                    }
                    break;
            }
        }
        return movePositions;
    }

    public ArrayList<Position> getAttackingPositions(Card attackCard, Unit attackUnit) {
        
        ArrayList<Position> attackPositions = new ArrayList<Position>();
        
        if (attackCard.getPlayingCardMode().equals(Card.ASSAULT) 
                || attackCard.getPlayingCardMode().equals(Card.VOLLEY)) {
            attackPositions = game.getPossibleAssault(attackUnit);
        } 
        else if (attackCard.getPlayingCardMode().equals(Card.BOMBARD)) {
        {
            ArrayList<Position> attackPossiblePositions  = game.getLOS(attackUnit, 2);
            for (Position checkPosition : attackPossiblePositions) {
                if (game.checkOpponentPlayerUnitAtPosition(checkPosition)) {
                    attackPositions.add(checkPosition);
                }
            }
        }
        }
        return attackPositions;
    }
   
}
