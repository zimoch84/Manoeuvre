/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.interfaces.PositionInterface;
import manouvre.game.interfaces.UnitInterface;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author Piotr
 */
public class Unit implements UnitInterface{

    
    Position pos;
    boolean injured;
    boolean eliminated;
   
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
        
        try{
            
            FileReader fileReader = new FileReader(new File("resources\\units\\units.csv"));
            CSVParser csvP = new CSVParser(fileReader, CSVFormat.EXCEL.withHeader("dent","Name","MaskFile","FrontFile","ID2"));

            for (CSVRecord csvRecord : csvP) {
                
                 if ( csvRecord.get("dent").equals(Integer.toString(ID)) ) 
                 {
                 
                    name = csvRecord.get("name");
                    imageReducedName= csvRecord.get("imageReduced");
                    imageFullName = csvRecord.get("imageFull");
                    strenght = new Integer(csvRecord.get("fullStrength") ) ;
                    reducedStrength = new Integer (csvRecord.get("reducedStrength"));
                    type = new Integer (csvRecord.get("type") ) ;
                    army = csvRecord.get("country");
                    break;
                }
            } 
        }
        catch (FileNotFoundException ex) { 
            Logger.getLogger(Unit.class.getName()).log(Level.SEVERE, null, ex);
        } 
        catch (IOException ex) {
            Logger.getLogger(Unit.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
        
   
    @Override
    public int getType() {
        return type;
    }

    @Override
    public PositionInterface getPosition() {
        return pos;
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
    return "Unit ID:[" + ID +"] Name: "  + name + " Army "+ army + " type "  + type + "Strenght [" + strenght +"/" + reducedStrength +"]";
      
            
    }
    
    
    
}
