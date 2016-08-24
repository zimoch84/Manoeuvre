/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.io.IOException;
import java.util.ArrayList;
import manouvre.game.Map;

/**
 * /**
 *Map looks like this

| Y/X      | 0        | 1        | 2        | 3        | 4        | 5        | 6        | 7        |
+----------+----------+----------+----------+----------+----------+----------+----------+----------+
| 7        |          |          |          |          |          |          |          |          |
|          |          |          |          |          |          |          |          |          |
| 6        |          |          |          |          |          |          |          |          |
|          |          |          |          |          |          |          |          |          |
| 5        |          |          |          |          |          |          |          |          |
|          |          |          |          |          |          |          |          |          |
| 4        |          |          |          |          |          |          |          |          |
|          |          |          |          |          |          |          |          |          |
| 3        |          |          |          |          |          |          |          |          |
|          |          |          |          |          |          |          |          |          |
| 2        |          |          |          |          |          |          |          |          |
|          |          |          |          |          |          |          |          |          |
| 1        |          |          |          |          |          |          |          |          |
|          |          |          |          |          |          |          |          |          |
| 0        |          |          |          |          |          |          |          |          |
+----------+----------+----------+----------+----------+----------+----------+----------+----------+
 *
 * @author Piotr
 */
public class MapGUI{
    
     ArrayList<TerrainGUI> terrainsGUI;
     
     Map map;
     
     boolean unitSelected;

    
    
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
    
    this.map =  map;
    
    terrainsGUI = new ArrayList<>();
    unitSelected = false;
    loadTerrains();
    
    
    }
    
    private void loadTerrains() throws IOException{
    
          for (int i=0;i<8;i++)
            {
                   for(int j=0;j<8;j++){
                       terrainsGUI.add(new TerrainGUI(map.getTileAtIndex(i, j)) );
                       
                   }
            }
         
                 
    
    }

    public ArrayList<TerrainGUI> getTerrainsGUI() {
        return terrainsGUI;
    }

    public void setTerrainsGUI(ArrayList<TerrainGUI> terrainsGUI) {
        this.terrainsGUI = terrainsGUI;
    }
         
    public boolean isUnitSelected() {
        return unitSelected;
    }

    public void setUnitSelected(boolean unitSelected) {
        this.unitSelected = unitSelected;
    }
    
    
   }
    
    
    
    
    
    

