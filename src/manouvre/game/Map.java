/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.util.ArrayList;
import java.util.Random;
import manouvre.game.interfaces.MapInterface;
import manouvre.game.interfaces.TerrainInterface;

/**
 *
 * @author Piotr
 */
public class Map implements MapInterface {
    
    /*
    Map is 8x8 Terrain square
    
    ArrayList index 0 = 0x0 point and lastIndex
    */
    
    TerrainInterface[][] tiles  ;
    
    
    @Override
    public void generateMap() {
      
        Random rand = new Random();
        int var;
        
        for (int i=0;i<8;i++)
            {
                   for(int j=0;j<8;j++){
                       
                       var = rand.nextInt()%100;
                       
                       if (var < 40) 
                           tiles[i][j] = new Terrain(Terrain.CLEAR);
                       else if ( (var >= 40) && (var <50) )
                           tiles[i][j] = new Terrain(Terrain.CITY);
                       else if ( (var >= 50) && (var <60) )
                           tiles[i][j] = new Terrain(Terrain.HILL);
                       else if ( (var >= 60) && (var <70) )
                           tiles[i][j] = new Terrain(Terrain.FIELDS);
                       else if ( (var >= 70) && (var <80) )
                           tiles[i][j] = new Terrain(Terrain.LAKE);
                       else if ( (var >= 80) && (var <90) )
                           tiles[i][j] = new Terrain(Terrain.FOREST);
                       else if ( (var >= 90) && (var <100) )
                           tiles[i][j] = new Terrain(Terrain.MARSH);
                       else   
                           tiles[i][j] = new Terrain(Terrain.CLEAR);   
                            
                       
                   }
            }
        
        
        
    }

    @Override
    public TerrainInterface getTileAtIndex(int x, int y) {
        return tiles[x][y];
    }
    
    
    public void changeTerrainAtIndex(int x, int y , TerrainInterface finalTerrain){
    
        tiles[x][y] = finalTerrain;
    }
    
    
    
    
}
