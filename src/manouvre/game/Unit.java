/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import com.csvreader.CsvReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.interfaces.PositionInterface;
import manouvre.game.interfaces.UnitInterface;

/**
 *
 * @author Piotr
 */
public class Unit implements UnitInterface, Serializable{

    private static final long serialVersionUID = 46321L;
    
    Player owner;

   
    Position position;

    @Override
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position pos) {
        this.position = pos;
    }
    boolean injured;
    boolean eliminated;
   
    //Must be done Bart
    boolean isSelected; 
    boolean hasMoved = false;
    boolean hasAttacked =false;
    boolean canRetrive;
    //
   
    
    int ID;
    String name;

   
    String army;
    int type;
    int strenght;
    int reducedStrength;


     String  imageReducedName ;
     String imageFullName;

     
     public Unit(int ID)  {
        this.ID  = ID;
        this.injured = false;
        this.eliminated = false;
        
        try{
            CsvReader csvReader = new CsvReader("resources\\units\\units.csv", ';');
            csvReader.readHeaders();
            
           //FileReader fileReader = new FileReader(new File("resources\\units\\units.csv"));
           // CSVParser csvP = new CSVParser(fileReader, CSVFormat.EXCEL.withHeader("dent","Name","MaskFile","FrontFile","ID2"));
                
           csvReader.readRecord();
            while (  ID  !=   Integer.parseInt(csvReader.get("ID"))  )
                
            {
            csvReader.readRecord();
            }
                 
            name = csvReader.get("name");
            imageReducedName= csvReader.get("imageReduced");
            imageFullName = csvReader.get("imageFull");
            strenght = new Integer(csvReader.get("fullStrength") ) ;
            reducedStrength = new Integer (csvReader.get("reducedStrength"));
            type = new Integer (csvReader.get("type") ) ;
            army = csvReader.get("country");
          
          
        }
        catch (FileNotFoundException ex) { 
            Logger.getLogger(Unit.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) {
            Logger.getLogger(Unit.class.getName()).log(Level.SEVERE, null, ex);
        } 
         catch (NumberFormatException ex) {
            System.out.println("Exception in Contructor Unit.java ID: " +ID);
        } 
    }
        
   
    @Override
    public int getType() {
        return type;
    }

    @Override
    public boolean isEliminated() {
       return eliminated;
    }

    @Override
    public boolean isInjured() {
        return injured;
    }

    @Override
    public void healUnit(int healValue) {
        
        
    }

    @Override
    public void damageUnit(int damageValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    @Override
    public String toString(){
    return "Unit ID:[" + ID +"] Name: "  + name + " Army "+ army + " type "  + type + " Strenght [" + strenght +"/" + reducedStrength +"][Position:" + getPosition().toString()+"]";
      
            
    }
    
    public  String getImageReducedName() {
        return imageReducedName;
    }

    public  String getImageFullName() {
        return imageFullName;
    }
    
    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
    
    public void move(Position newPosition){
    
        setPosition(newPosition);
        hasMoved = true;
    }
    
    public boolean equals(Unit inUnit){
    
        if(this.name.equals(inUnit.name)) return true;
        
        else return false;
             
    }
    
    //Bart
    public boolean isHasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean isCanRetrive() {
        return canRetrive;
    }

    public void setCanRetrive(boolean canRetrive) {
        this.canRetrive = canRetrive;
    }
    
     public boolean isIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    public boolean isHasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }
    //Bart
    
    public String getName() {
        return name;
    }
    
    
}
