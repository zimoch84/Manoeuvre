/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.io.IOException;
import java.util.Random;
import manouvre.game.Map;
import manouvre.game.Position;
import manouvre.game.Terrain;
import manouvre.game.interfaces.PositionInterface;

/**
 *
 * @author Piotr
 */
public class MapGUI extends Map{
    
     TerrainGUI [][] terrains = new TerrainGUI[8][8];

    public TerrainGUI[][] getTerrains() {
        return terrains;
    }

    public void setTerrains(TerrainGUI[][] terrains) {
        this.terrains = terrains;
    }
             
    
     public static final int BOARD_START_X = 0;
     public static final int BOARD_START_Y = 0;

     public static final int SQUARE_WIDTH = 60;
     public static final int SQUARE_HEIGHT = 60;

     public static final int PIECE_WIDTH = 60;
     public static final int PIECE_HEIGHT = 60;

     public static final int PIECES_START_X = BOARD_START_X + (int)(SQUARE_WIDTH/2.0 - PIECE_WIDTH/2.0);
     public static final int PIECES_START_Y = BOARD_START_Y + (int)(SQUARE_HEIGHT/2.0 - PIECE_HEIGHT/2.0);
    
    public MapGUI() throws IOException{
            generateMap();
            
    }         
     
    
        private void generateMap() throws IOException {
      
        Random rand = new Random();
        int var;
        
        for (int i=0;i<8;i++)
            {
                   for(int j=0;j<8;j++){
                       
                       var = rand.nextInt()%100;
                       Position pos = new Position(i, j);
                       if (var < 40) 
                           terrains[i][j] = new TerrainGUI(Terrain.CLEAR, pos);
                       else if ( (var >= 40) && (var <50) )
                           terrains[i][j] = new TerrainGUI(Terrain.CITY, pos);
                       else if ( (var >= 50) && (var <60) )
                           terrains[i][j] = new TerrainGUI(Terrain.HILL, pos);
                       else if ( (var >= 60) && (var <70) )
                           terrains[i][j] = new TerrainGUI(Terrain.FIELDS, pos);
                       else if ( (var >= 70) && (var <80) )
                           terrains[i][j] = new TerrainGUI(Terrain.LAKE, pos);
                       else if ( (var >= 80) && (var <90) )
                           terrains[i][j] = new TerrainGUI(Terrain.FOREST, pos);
                       else if ( (var >= 90) && (var <100) )
                           terrains[i][j] = new TerrainGUI(Terrain.MARSH,pos);
                       else   
                           terrains[i][j] = new TerrainGUI(Terrain.CLEAR, pos);   
                            
                       
                   }
            }        
             
    }
    
    
    
    
    
    
}
