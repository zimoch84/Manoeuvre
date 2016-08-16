/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

/**
 *
 * @author Bartosz
 */
public class Test {
    
    public static void main(String[] args) { 
        
        CsvFileReader Card = new CsvFileReader(33);
        String name=Card.getCardName();
        
        System.out.println("Name:" + name);
        
        
    }

    
}
