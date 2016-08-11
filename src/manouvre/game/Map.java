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
    
    TerrainInterface[][] terrains = new TerrainInterface[8][8] ;

    public Map() {
        generateMap();
    }
    
    @Override
    public TerrainInterface[][] getTerrains() {
        return terrains;
    }

    public void setTerrains(TerrainInterface[][] terrains) {
        this.terrains = terrains;
    }
    
    
 
    private void generateMap() {
      
        Random rand = new Random();
        int var;
        
        for (int i=0;i<8;i++)
            {
                   for(int j=0;j<8;j++){
                       
                       var = rand.nextInt()%100;
                       Position pos = new Position(i, j);
                       if (var < 40) 
                           terrains[i][j] = new Terrain(Terrain.CLEAR, pos);
                       else if ( (var >= 40) && (var <50) )
                           terrains[i][j] = new Terrain(Terrain.CITY, pos);
                       else if ( (var >= 50) && (var <60) )
                           terrains[i][j] = new Terrain(Terrain.HILL, pos);
                       else if ( (var >= 60) && (var <70) )
                           terrains[i][j] = new Terrain(Terrain.FIELDS, pos);
                       else if ( (var >= 70) && (var <80) )
                           terrains[i][j] = new Terrain(Terrain.LAKE, pos);
                       else if ( (var >= 80) && (var <90) )
                           terrains[i][j] = new Terrain(Terrain.FOREST, pos);
                       else if ( (var >= 90) && (var <100) )
                           terrains[i][j] = new Terrain(Terrain.MARSH,pos);
                       else   
                           terrains[i][j] = new Terrain(Terrain.CLEAR, pos);   
                            
                       
                   }
            }
        
        
        
    }

    @Override
    public TerrainInterface getTileAtIndex(int x, int y) {
        return terrains[x][y];
    }
    
    
    public void changeTerrainAtIndex(int x, int y , TerrainInterface finalTerrain){
    
        terrains[x][y] = finalTerrain;
    }
    
    
    
    
}
