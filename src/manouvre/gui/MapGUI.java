/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.Map;
import manouvre.game.Position;
import manouvre.game.Terrain;
import manouvre.game.interfaces.PositionInterface;

/**
 *
 * @author Piotr
 */
public class MapGUI extends Map{
    
     ArrayList<TerrainGUI> terrainsGUI;
    
     public static final int BOARD_START_X = 0;
     public static final int BOARD_START_Y = 0;

     public static final int SQUARE_WIDTH = 60;
     public static final int SQUARE_HEIGHT = 60;

     public static final int PIECE_WIDTH = 60;
     public static final int PIECE_HEIGHT = 60;

     public static final int PIECES_START_X = BOARD_START_X + (int)(SQUARE_WIDTH/2.0 - PIECE_WIDTH/2.0);
     public static final int PIECES_START_Y = BOARD_START_Y + (int)(SQUARE_HEIGHT/2.0 - PIECE_HEIGHT/2.0);
    
    public MapGUI() throws IOException{
           
        super();
        terrainsGUI = new ArrayList<>();
        loadTerrains();
        
                    
    }   
    
    public MapGUI(Map map) throws IOException{
    
    this.setTerrains(map.getTerrains());
    terrainsGUI = new ArrayList<>();
    loadTerrains();
    
    
    }
    
    private void loadTerrains() throws IOException{
    
          for (int i=0;i<8;i++)
            {
                   for(int j=0;j<8;j++){
                       TerrainGUI  tempTerain =  new TerrainGUI(
                                       getTerrains()[i][j].getType(), 
                                       getTerrains()[i][j].getPos()
                       );
                               
                       terrainsGUI.add(
                         tempTerain      
                               );
                       
                   }
            }
         
                 
    
    }

    public ArrayList<TerrainGUI> getTerrainsGUI() {
        return terrainsGUI;
    }

    public void setTerrainsGUI(ArrayList<TerrainGUI> terrainsGUI) {
        this.terrainsGUI = terrainsGUI;
    }
         
    
   }
    
    
    
    
    
    

