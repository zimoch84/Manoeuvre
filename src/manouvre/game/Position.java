/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import manouvre.interfaces.PositionInterface;
import manouvre.gui.CreateRoomWindow;
import manouvre.gui.MapGUI;

/**
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
 * @author Piotr
 */
public class Position implements PositionInterface, Serializable{

    private static final long serialVersionUID = 321L;
    private int x;
    private int y;
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
     
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        
    }
        
    /**
     * calculate upper left corner of terrain
     * @param 
     * @return x coordinate for upper right corner  terrain	
     */
    public  int getMouseX(int windowMode){

            if(windowMode == CreateRoomWindow.AS_HOST)
                return MapGUI.BOARD_START_X+  MapGUI.SQUARE_WIDTH * x;
            else
                return MapGUI.BOARD_START_X+  MapGUI.SQUARE_WIDTH * transpoze().getX();
    }
	
    /**
     * calculate upper left corner of terrain
     * @param 
     * @return y coordinate for  upper right corner  terrain
     */
    public  int getMouseY(int isHost){

        if(isHost == CreateRoomWindow.AS_HOST)
            return  MapGUI.SQUARE_HEIGHT * (PositionInterface.ROW_8 - y) + MapGUI.BOARD_START_Y ;
        else 
            return  MapGUI.SQUARE_HEIGHT * (PositionInterface.ROW_8 - transpoze().getY()) + MapGUI.BOARD_START_Y ;   
    }

    @Override
    public ArrayList<Position> getAdjencedPositions() {
       
    ArrayList<Position>  adjencedPositions = new ArrayList<Position>();
          if (this.getX()-1  >= 0 ) {
                adjencedPositions.add(new Position(this.getX()-1, this.getY()));
          }
          if (this.getY()-1 >= 0){      
                adjencedPositions.add(new Position(this.getX(), this.getY()-1 ));
          }
          if (this.getY()+1 <= ROW_8){
                adjencedPositions.add(new Position(this.getX(), this.getY()+1));
          }
          if (this.getX()+1 <= COLUMN_H){
                adjencedPositions.add(new Position(this.getX()+1, this.getY()));
          }
     return adjencedPositions;
    }
    
    @Override
    public String toString(){
        return "Position X: " + getX() + " Position Y: " + getY() ;
    }
    
    @Override
    public boolean equals(Object in){
        Position p = (Position) in;
        if (getX() == p.getX() && getY()==p.getY()) return true;
        
        else return false;
    }
    
    public Position transpoze(){
        return new Position(PositionInterface.ROW_8 - x, PositionInterface.COLUMN_H - y);
    }
    
   
    
    /*
    Convert mouse position to x coordinate
    */
    
             
    public static Position getPositionFromMouse(int x, int y, int windowMode){
        if(windowMode == CreateRoomWindow.AS_GUEST)
            return new Position(convertMouseXToX(x), convertMouseYToY(y)).transpoze();
        else 
            return new Position(convertMouseXToX(x), convertMouseYToY(y));
    }
    private static  int convertXtoMouseX(int x){
        return MapGUI.BOARD_START_X + MapGUI.SQUARE_WIDTH * x;
    }
	
    private static int convertYToMouseY(int y){
        return MapGUI.BOARD_START_Y + MapGUI.SQUARE_HEIGHT * (PositionInterface.ROW_8 - y);
    }
    private static int convertMouseXToX(int mouseX){
        return (int)  (mouseX - MapGUI.BOARD_START_X)  /MapGUI.SQUARE_WIDTH;
    }
    /*
    Convert mouse position y to y coordinate on board
    */
    private static int convertMouseYToY(int mouseY){
        return  ROW_8  -  (int)  ((mouseY - MapGUI.BOARD_START_X)  / (MapGUI.SQUARE_HEIGHT))  ;
    }
        
}
