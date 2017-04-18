/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import manouvre.game.interfaces.MapInterface;
import manouvre.game.interfaces.TerrainInterface;

/**
 *
 * @author Piotr
 */
public class Map implements MapInterface, Serializable{

    private static final long serialVersionUID = 4665321L;
    Terrain[][] terrains ;

    public Map() {
        this.terrains = new Terrain[8][8];
        generateMap();
    }
    
    @Override
    public Terrain[][] getTerrains() {
        return terrains;
    }
    
    
    public ArrayList<Terrain> getTerrainz() {
        
        ArrayList<Terrain> terrains = new ArrayList<Terrain>();
        
        for (int i=0;i<8;i++)
            {
                   for(int j=0;j<8;j++){
                       
                       terrains.add(this.terrains[i][j]);
                       
                   }
            }
                
        
        return terrains;
    }

    public void setTerrains(Terrain[][] terrains) {
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
    public Terrain getTerrainAtXY(int x, int y) {
        return terrains[x][y];
    }
    
    
    public Terrain getTerrainAtPosition(Position pos) {
        return terrains[pos.getX()][pos.getY()];
    }
    
        
    public void unselectAllTerrains()
    {
        for(Terrain terrain: getTerrainz())
      {
          terrain.setSelected(false);
        }
    }
        
    
    
}
