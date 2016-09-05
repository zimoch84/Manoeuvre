/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;

/**
 *
 * @author Bartosz
 */
public class GameGUI {
    
    Game game;
    ArrayList<UnitGUI> unitsGUI = new ArrayList<UnitGUI>(); 
    MapGUI mapGUI;
    
    
    
    
    
    
    public GameGUI (Game newGame) throws IOException{
        this.game=newGame;
        this.mapGUI = new MapGUI(game.getMap());
        this.generateUnitsUI();
        
    }

    void drawMap(Graphics g) {
        int gap = 5;
        // draw background
        //g.drawImage(this.imgBackground, 0, 0, null);
        // draw terrain
        for (TerrainGUI terrainGUI : mapGUI.getTerrainsGUI()) {
            g.drawImage(terrainGUI.getImg(), terrainGUI.getPos().getMouseX(), terrainGUI.getPos().getMouseY(), null);
        }
        /*
        Draws selection
         */
        if (mapGUI.isUnitSelected()) {
            for (TerrainGUI terrain : mapGUI.getTerrainsGUI()) {
                if (terrain.isSelected()) {
                    g.drawRoundRect(terrain.getPos().getMouseX() + gap, terrain.getPos().getMouseY() + gap, MapGUI.SQUARE_WIDTH - 2 * gap, MapGUI.SQUARE_HEIGHT - 2 * gap, 10, 10);
                    System.out.println("Position " + terrain.getPos());
                    /*
                    Draw AdjencedSpace /Move
                     */
                    if (!terrain.getTerrain().getIsOccupiedByUnit()) {
                        ArrayList<Position> adjencedPositions = terrain.getPos().getAdjencedPositions();
                        System.out.println(terrain.getPos().toString());
                        g.setColor(Color.red);
                        for (int k = 0; k < adjencedPositions.size(); k++) {
                            g.drawRoundRect(adjencedPositions.get(k).getMouseX() + gap, adjencedPositions.get(k).getMouseY() + gap, MapGUI.SQUARE_WIDTH - 2 * gap, MapGUI.SQUARE_HEIGHT - 2 * gap, 10, 10);
                        }
                    } else {
                        System.out.println("manouvre.gui.ClientUI.drawMap() : " + game.getUnitAtPosition(terrain.getPos()).toString());
                        ArrayList<Position> movePositions = game.getPossibleMovement(game.getUnitAtPosition(terrain.getPos()));
                        for (Position drawMovePosion : movePositions) {
                            g.setColor(Color.blue);
                            g.drawRoundRect(drawMovePosion.getMouseX() + gap, drawMovePosion.getMouseY() + gap, MapGUI.SQUARE_WIDTH - 2 * gap, MapGUI.SQUARE_HEIGHT - 2 * gap, 10, 10);
                        }
                    }
                }
            }
        }
        /*
        Draw units
         */
        int gapUnit = 7;
        for (UnitGUI drawUnit : unitsGUI) {
            g.drawImage(drawUnit.getImg(), drawUnit.getUnit().getPos().getMouseX() + gapUnit, drawUnit.getUnit().getPos().getMouseY() + gapUnit, 46, 46, null);
        }
    }

    void generateUnitsUI() {
        for (Unit unit : game.getCurrentPlayer().getArmy()) {
            unitsGUI.add(new UnitGUI(unit));
        }
    }

    UnitGUI getSelectedUnit() {
        for (UnitGUI unitSearch : this.unitsGUI) {
            if (unitSearch.isSelected()) {
                return unitSearch;
            }
        }
        return null;
    }

    void unselectAllUnits() {
        unitsGUI.stream().forEach((UnitGUI unit) -> {
            unit.setSelected(false);
        });
        mapGUI.setUnitSelected(false);
    }
    
    public Game getGame() {
        return game;
    }

    public MapGUI getMapGui() {
        return mapGUI;
    }
}
