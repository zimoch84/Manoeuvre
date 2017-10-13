/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import manouvre.game.Map;
import manouvre.game.Position;


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
    
     TerrainGUI[][] terrainsGUI ;
     /*
     For convinience only
     */
     ArrayList<TerrainGUI> wrappingArrayOfTerrains = new ArrayList<>();
     Map map;
     

     int windowMode;
    
     public static final int BOARD_START_X = 60;
     public static final int BOARD_START_Y = 60;

     public static final int SQUARE_WIDTH = 70;
     public static final int SQUARE_HEIGHT = 70;

     public static final int PIECE_WIDTH = 65;
     public static final int PIECE_HEIGHT = 65;

     public static final int PIECES_START_X =  (int)((SQUARE_WIDTH - PIECE_WIDTH)/2.0);
     public static final int PIECES_START_Y =  (int)((SQUARE_HEIGHT - PIECE_HEIGHT)/2.0);
    
     public static final int LETTER_OFFSET_X = 5;
     public static final int LETTER_OFFSET_Y = PIECE_HEIGHT - 5;
     
     public static final float REDOUBT_IMG_SCALE = 1f;
     
     Image background ;
    
     public MapGUI() throws IOException{
           
        super();
        this.terrainsGUI = new TerrainGUI[8][8];
        loadTerrains();
        background= ImageIO.read( new File("resources\\backgrounds\\table800_800.jpg" ));
        
                    
    }   
    
    public MapGUI(Map map, int windowMode) throws IOException{
    
    this.map =  map;
    this.windowMode = windowMode;
    
    this.terrainsGUI = new TerrainGUI[8][8];
  
    loadTerrains();
    background= ImageIO.read( new File("resources\\backgrounds\\table800_800.jpg" ));
    
    }
   
    private void loadTerrains() throws IOException{
    
          for (int x=0;x<8;x++)
            {
                   for(int y=0;y<8;y++){
                       if(windowMode == CreateRoomWindow.AS_HOST)
                       { terrainsGUI[x][y] = new TerrainGUI(map.getTerrainAtXY(x, y));
                        wrappingArrayOfTerrains.add(terrainsGUI[x][y]);
                       }
                       else
                       {
                           /*
                           Rotate map view 180 degreees
                           */
                       terrainsGUI[x][y] = new TerrainGUI(map.getTerrainAtXY(7-x, 7-y));
                       wrappingArrayOfTerrains.add(terrainsGUI[x][y]);
                       }
                       
                          
                       
                   }
            }
         
                 
    
    }
    public ArrayList<TerrainGUI> getTerrainsGUI() {
        return wrappingArrayOfTerrains;
    }

    public void setTerrainsGUI(ArrayList<TerrainGUI> terrainsGUI) {
        this.wrappingArrayOfTerrains = terrainsGUI;
    }
    public TerrainGUI[][] getTerrainsArrayGUI() {
        return terrainsGUI;
    }

    public void getTerrainsArrayGUI(TerrainGUI[][] terrainsGUI) {
        this.terrainsGUI = terrainsGUI;
    }

 
    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public TerrainGUI getTerrainGuiAtPosition(Position position)
    {
        for(TerrainGUI terrain : getTerrainsGUI())
        {
            if(terrain.getPos().equals(position))
                return terrain;
        }
        return null;
            
    }
    
   }
    
    
    
    
    
    

