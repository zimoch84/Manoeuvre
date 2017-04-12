/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import manouvre.game.interfaces.TerrainInterface;

/**
 *
 * @author Piotr
 */
public class Terrain  implements TerrainInterface, Serializable {

    private static final long serialVersionUID = 432123L;
    private int type;
    private int defenceBonus;
    private boolean blockingLOS;
    
    boolean ispassable;
    boolean endsMove;


    boolean noAssaultOut;
    
    boolean isRedoubt;
    boolean isOccupiedByUnit;

    Position pos;
    boolean selected;
   
    
    public Position getPosition() {
        return pos;
    }

    public void setPosition(Position pos) {
        this.pos = pos;
    }

    public boolean setIsRedoubt() {
        return isRedoubt;
    }

    public void setIsRedoubt(boolean isRedoubt) {
        this.isRedoubt = isRedoubt;
    }

    public boolean getIsOccupiedByUnit() {
        return isOccupiedByUnit;
    }

    public void setIsOccupiedByUnit(boolean isOccupiedByUnit) {
        this.isOccupiedByUnit = isOccupiedByUnit;
    }
     
    
    public Terrain(int type, Position pos){
        
        this.pos = pos;
        this.type = type;
        calculateDefenceBonus();
        calculateBlockLOS();
        calculatePassable();
            
        if(type == Terrain.MARSH){
           this.endsMove = true; 
           this.noAssaultOut = true;
        }
        else {   
         this.endsMove = false; 
         this.noAssaultOut = false;
        }

    }
    
    @Override
    public int getType() {
        return type;
    }

     @Override
    public String toString(){
    
        String out;
        out = "Terrain Type:";
        switch (type){
            case Terrain.CITY : out=  out + " City" ; break;
            case Terrain.CLEAR: out = out + " Clear";break;
            case Terrain.FIELDS: out = out + " Fields"; break;
            case Terrain.FOREST : out=  out + " Forest" ; break;
            case Terrain.HILL: out = out + " Hill"; break;
            case Terrain.LAKE: out = out + " Lake"; break;
            case Terrain.MARSH: out = out + " Marsh"; break;
            default: out = out + " No definition " ;
        }
        return out;
            
            
    }
  
   /*
    calculate base defence bonus of terrain
    */
    private void calculateDefenceBonus() {
       
        switch (type){
            case Terrain.CITY : defenceBonus= 3 ; break;
            case Terrain.CLEAR: defenceBonus = 0; break;
            case Terrain.FIELDS: defenceBonus =0;break;
            case Terrain.FOREST : defenceBonus =  2 ;break;
            case Terrain.HILL: defenceBonus = 2;break;
            case Terrain.LAKE: defenceBonus = 0;break;
            case Terrain.MARSH: defenceBonus = 1;break;
            default: defenceBonus = 0;
        }
        
    }
    /*
    calculate base LOS of terrain
    */
    private void calculateBlockLOS() {
    
        switch (type){
            case Terrain.CITY : blockingLOS= true ;break;
            case Terrain.CLEAR: blockingLOS= false;break;
            case Terrain.FIELDS: blockingLOS= false;break;
            case Terrain.FOREST : blockingLOS= true ;break;
            case Terrain.HILL: blockingLOS= true;break;
            case Terrain.LAKE: blockingLOS= true;break;
            case Terrain.MARSH: blockingLOS= true;break;
            default: blockingLOS = false;
        }
    
    }
    
    /*
    calculate base passability of terrain
    */
    private void calculatePassable() {
    
        switch (type){
            case Terrain.CITY : ispassable= true ; break;
            case Terrain.CLEAR: ispassable= true;break;
            case Terrain.FIELDS: ispassable= true;break;
            case Terrain.FOREST : ispassable= true ;break;
            case Terrain.HILL: ispassable= true;break;
            case Terrain.LAKE: ispassable= false;break;
            case Terrain.MARSH: ispassable= true;break;
            default: ispassable = true;
        }
    
    }
    
        
    @Override
    public int getAttackBonus(TerrainInterface attackingTile) {
       if(attackingTile.getType()!= Terrain.HILL  )
           return 2;
       
       else return 0;
       
       
    }
    @Override
    public boolean isPassable() {
        if (!getIsOccupiedByUnit())   
            return ispassable;
        else 
            return false;
    }

    public boolean isTerrainPassable() {
        return ispassable;
        
    }

    
    
    @Override
    public boolean isBlockingLOS() {
          
        if(isOccupiedByUnit || blockingLOS) 
            return true;
        else return blockingLOS;
            
    }

    @Override
    public int getDefenceBonus() {
        if (isRedoubt) 
            return defenceBonus + 3;
        else return defenceBonus;
        
    }

    public boolean isEndsMove() {
        return endsMove;
    }

    public void setEndsMove(boolean endsMove) {
        this.endsMove = endsMove;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    
    
    
}
