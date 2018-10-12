/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;
import manouvre.interfaces.PositionInterface;
import manouvre.network.server.UnoptimizedDeepCopy;

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
                
                switch(card.getHQType()){
                    case Card.SUPPLY:
                        if (game.getPhase() == Game.MOVE) 
                            return getCurrentPlayerNotMovedUnits();
                        if (game.getPhase() == Game.RESTORATION) 
                            return getCurrentPlayerInjuredUnitPositions();
                    break;
                    
                    case Card.WITHDRAW:
                        returnPositions.add(game.getCombat().getDefendingUnit().getPosition());
                        return returnPositions;
                      
                    case Card.REDOUBDT:
                        return game.getCurrentPlayer().getArmyPositions();
                    
                    case Card.AMBUSH:
                        return game.getOpponentPlayer().getArmyPositions();
                
                }
               
            case Card.UNIT:
                switch(game.getPhase()){
                    case Game.COMBAT:
                    Combat combat = game.getCombat();
                    switch(combat.getState()){
                        case COMBAT_NOT_INITIALIZED:
                        case INITIALIZING_COMBAT:    
                            if(card.getPlayingCardMode() != Combat.Type.NO_TYPE )
                            {
                                return getAttackingPositions(card, game.getUnitByCard(card));    
                            }
                        break;
               
                        case PURSUIT:
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
                        case PICK_SUPPORT_UNIT:
                          return getPossibleSupportingUnitsPositions();
                      
                        case PURSUIT:
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
                ArrayList<Position> movePositions = getPossibleMovement(selectedUnit);
                return movePositions;
            case Game.COMBAT:
                switch (game.getCombat().getState()) {
                    case WITHRDAW:
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
                    case COMBAT_NOT_INITIALIZED:
                        return game.getCurrentPlayer().getArmyPositions();
                    case WITHRDAW:
                        if (game.getCurrentPlayer().hasAttacked()) {
                            possiblePositions.add(game.getCombat().getAttackingUnit().getPosition());
                            return possiblePositions;
                        } else {
                            possiblePositions.add(game.getCombat().getDefendingUnit().getPosition());
                            return possiblePositions;
                        }
                    case PURSUIT:
                        {
                            for (Unit pursueUnit : game.getCombat().getUnitThatCanAdvance()) {
                                possiblePositions.add(pursueUnit.getPosition());
                            }
                            return possiblePositions;
                        }
                    case PICK_SUPPORT_UNIT:
                        return getPossibleSupportingUnitsPositions();
                    case COMMITTED_ATTACK_CASUALITIES:
                        return getAllAttackingUnitsPositions();
                }
                break;
            case Game.RESTORATION:
                return getCurrentPlayerInjuredUnitPositions();
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
        Unit defendingUnit = game.getCombat().getDefendingUnit();
        Position atackingPosition = attackingUnit.getPosition();
         
        ArrayList<Position> possiblePositions = getOneSquarePositions(defendingUnit.getPosition());
        ArrayList<Position> supportingPositions = new ArrayList<>();
       
        for (Position checkPosition : possiblePositions) {
            if (game.isCurrentPlayerUnitAtPosition(checkPosition) && !checkPosition.equals(atackingPosition)) 
                if(game.getUnitAtPosition(checkPosition).getOwner().getNation() != defendingUnit.getOwner().getNation())
                    supportingPositions.add(checkPosition);
        }
        return supportingPositions;
    }
    
    public ArrayList<Position> getAllAttackingUnitsPositions(){
        
        ArrayList<Position> allPositions = new ArrayList<>();
                
        game.getCombat().getAttackingUnits().stream().map(Unit::getPosition).forEach(allPositions::add);
        
        return allPositions;
    
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
        ArrayList<Position> possibleMovements = getOneSquareMovements(unit.getPosition());
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
                            movePositions = getOneSquareMovements(movingUnit.getPosition());
                        break;
                        case Card.SUPPLY:
                            movePositions = getPossibleMovement(movingUnit);
                        break;
                        case Card.WITHDRAW:
                            movePositions = game.positionCalculator.getRetreatPositions(movingUnit);
                        break;
                        case Card.SKIRMISH:
                            movePositions = getTwoSquareMovements(movingUnit.getPosition());
                        break;
                    }
                    break;
            }
        }
        return movePositions;
    }

    public ArrayList<Position> getAttackingPositions(Card attackCard, Unit attackUnit) {
        
        ArrayList<Position> attackPositions = new ArrayList<Position>();
        
        if (attackCard.getPlayingCardMode().equals(Combat.Type.ASSAULT) 
                || attackCard.getPlayingCardMode().equals(Combat.Type.VOLLEY)) {
            attackPositions = getPossibleAssault(attackUnit);
        } 
        else if (attackCard.getPlayingCardMode().equals(Combat.Type.BOMBARD)) {
        {
            ArrayList<Position> attackPossiblePositions  = getLOS(attackUnit, 2);
            for (Position checkPosition : attackPossiblePositions) {
                if (game.checkOpponentPlayerUnitAtPosition(checkPosition)) {
                    attackPositions.add(checkPosition);
                }
            }
        }
        }
        return attackPositions;
    }

    /**
    Firstly get adjenced tiles then check on terrain restrictions then check if another tile is occupied
     * @param unit
     * @return Position
     */
    public ArrayList<Position> getOneSquareMovements(Position unitPosition) {
        ArrayList<Position> moves;
        moves = new ArrayList<>();
        /*
        Firstly get adjenced tiles then check on terrain restrictions then check if another tile is occupied
         */
        if (unitPosition.getX() - 1 >= 0) {
            if (game.getMap().getTerrainAtXY(unitPosition.getX() - 1, unitPosition.getY()).isPassable()) {
                moves.add(new Position(unitPosition.getX() - 1, unitPosition.getY()));
            }
        }
        if (unitPosition.getY() - 1 >= 0) {
            if (game.getMap().getTerrainAtXY(unitPosition.getX(), unitPosition.getY() - 1).isPassable()) {
                moves.add(new Position(unitPosition.getX(), unitPosition.getY() - 1));
            }
        }
        if (unitPosition.getY() + 1 <= PositionInterface.ROW_8) {
            if (game.getMap().getTerrainAtXY(unitPosition.getX(), unitPosition.getY() + 1).isPassable()) {
                moves.add(new Position(unitPosition.getX(), unitPosition.getY() + 1));
            }
        }
        if (unitPosition.getX() + 1 <= PositionInterface.COLUMN_H) {
            if (game.getMap().getTerrainAtXY(unitPosition.getX() + 1, unitPosition.getY()).isPassable()) {
                moves.add(new Position(unitPosition.getX() + 1, unitPosition.getY()));
            }
        }
        return moves;
    }

    public ArrayList<Position> getTwoSquareMovements(Position unitPosition) {
        ArrayList<Position> moves = game.positionCalculator.getOneSquareMovements(unitPosition);
        ArrayList<Position> tempMoves;
        ArrayList<Position> tempMoves2 = new ArrayList<Position>();
        for (Position move : moves) {
            if (!game.getMap().getTerrainAtXY(move.getX(), move.getY()).isEndsMove()) {
                tempMoves = game.positionCalculator.getOneSquareMovements(move);
                for (Position addPosition : tempMoves) {
                    if (!moves.contains(addPosition) && !addPosition.equals(unitPosition)) {
                        tempMoves2.add(addPosition);
                    }
                }
            }
        }
        moves.addAll(tempMoves2);
        return moves;
    }

    public ArrayList<Position> getOneSquarePositions(Position unitPosition) {
        ArrayList<Position> positions = new ArrayList<>();
        if (unitPosition.getX() - 1 >= 0) {
            positions.add(new Position(unitPosition.getX() - 1, unitPosition.getY()));
        }
        if (unitPosition.getY() - 1 >= 0) {
            positions.add(new Position(unitPosition.getX(), unitPosition.getY() - 1));
        }
        if (unitPosition.getY() + 1 <= PositionInterface.ROW_8) {
            positions.add(new Position(unitPosition.getX(), unitPosition.getY() + 1));
        }
        if (unitPosition.getX() + 1 <= PositionInterface.COLUMN_H) {
            positions.add(new Position(unitPosition.getX() + 1, unitPosition.getY()));
        }
        return positions;
    }

    public ArrayList<Position> getPossibleVolley(Unit unit) {
        return getLOS(unit, 1);
    }

    public ArrayList<Position> getPossibleBombard(Unit unit) {
        return getLOS(unit, 2);
    }

    public ArrayList<Position> getPossibleAssault(Unit unit) {
        ArrayList<Position> getOneSquarePositionsArray = game.positionCalculator.getOneSquarePositions(unit.getPosition());
        ArrayList<Position> getPossibleAssaultArray = new ArrayList<>();
        for (Position checkPositon : getOneSquarePositionsArray) {
            if (game.checkOpponentPlayerUnitAtPosition(checkPositon)) {
                getPossibleAssaultArray.add(checkPositon);
            }
        }
        return getPossibleAssaultArray;
    }

    public ArrayList<Position> getLOS(Unit unit, int lenght) {
        Position unitPosition = unit.getPosition();
        ArrayList<Position> los = game.positionCalculator.getOneSquarePositions(unitPosition);
        ArrayList<Position> loscopy = (ArrayList<Position>) UnoptimizedDeepCopy.copy(los);
        ArrayList<Position> los2;
        /*
        If length = 1 then we have volley
         */
        if (lenght == 1) {
            return loscopy;
        } else {
            for (Iterator<Position> checkLOSPosition = loscopy.iterator(); checkLOSPosition.hasNext();) {
                /*
                If 1st square terrain blocks los then 2nd squara wont be visible
                 */
                Position position = checkLOSPosition.next();
                if (!game.getMap().getTerrainAtPosition(position).isBlockingLOS()) {
                    los2 = getOneSquarePositions(position);
                    los2.remove(unitPosition);
                    los.addAll(los2);
                }
            }
        }
        return los;
    }

    public ArrayList<Position> getPossibleMovement(Unit unit) {
        ArrayList<Position> moves = new ArrayList<>();
        /*
        get Infantry Moves
         */
        if (unit.getType() == Unit.INFANTRY) {
            moves = getOneSquareMovements(unit.getPosition());
            /*
            If calvary do check of every infantry move considering Terrain.MARSH which ends move
             */
        } else if (unit.getType() == Unit.CALVARY) {
            moves =getTwoSquareMovements(unit.getPosition());
        }
        return moves;
    }
   
}
