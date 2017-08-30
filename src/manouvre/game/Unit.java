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
import manouvre.game.interfaces.UnitInterface;
import manouvre.gui.GameWindow;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author Piotr
 */
public class Unit implements UnitInterface, Serializable{

    private static final long serialVersionUID = 46321L;
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Unit.class.getName());
    
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
   
    boolean selected; 
    boolean hasMoved = false;
    boolean hasAttacked =false;
    /*
    If unit advanced in combat
    */
    boolean advanced = false;
    
    boolean retrieving;
    boolean showLOS = false;
    
    boolean supporting = false;
   
    
    int ID;
    String name;

   
    String army;
    int type;
    int strenght;
    int reducedStrength;


     String imageReducedName ;
     String imageFullName;

     
     public Unit(int ID)  {
        this.ID  = ID;
        this.injured = false;
        this.eliminated = false;
        this.retrieving = false;
        
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        LOGGER.debug("Unit " + name + " has been " + (selected ?  " seleced"  : " deselected") );
        this.selected = selected;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
        
   
    @Override
    public boolean equals(Object in){
     
         if(in instanceof Unit){
            Unit unitIn = (Unit) in;
                if (unitIn.name.equals(this.name)) return true;
         }
         if(in instanceof Card){
            Card cardIn = (Card) in;
            if (cardIn.CardName.equals(this.name)) return true;
         }
        return false;
     }

    public boolean hasAdvanced() {
        return advanced;
    }

    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
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
    
    

    public void takeHit(){
    if (!isInjured())
        injured = true;
    else eliminated = true;
  
    }
    
    public void eliminate()
    {
        eliminated = true;
    }
    
    public void restoreUnit()
    {
    
        if(!eliminated)
            injured = false;
            
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
        setMoved(true);
            if(isSelected()) setSelected(false);
    }
        
    //Bart
    public boolean hasMoved() {
        return hasMoved;
    }

    public void setMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean isRetriving() {
        return retrieving;
    }

    public void setRetriving(boolean canRetrive) {
        this.retrieving = canRetrive;
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

    public boolean isShowingLOS() {
        return showLOS;
    }

    public void setShowingLOS(boolean showLOS) {
        this.showLOS = showLOS;
    }

    public int getCurrentStrenght() {
        return (isInjured() ? reducedStrength :strenght) ;
    }

    public boolean isSupporting() {
        return supporting;
    }

    public void setSupporting(boolean supporting) {
        this.supporting = supporting;
    }
    
    

}
