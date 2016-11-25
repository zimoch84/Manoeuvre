/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import manouvre.game.interfaces.CardInterface;
import manouvre.gui.UnitGUI;

/**
 *  Nation
 *  int BR  = 0; //GreatBritain
    int FR  = 2; //France
    int RU  = 5; //Russland
    int PR  = 4; //Prussia
    int AU  = 1; //Austria
    int SP  = 6; //Spain
    int OT  = 3; //Ottoman
    int US  = 7; //USA
 * @author Piotr
 */
public class Player  implements Serializable{
    
    private static final long serialVersionUID = 43211L;
    String name;
    int nation; //for nation description see CardInterface

   
    CardSet hand;
    CardSet drawPile;
    CardSet discardPile;
    CardSet tablePile;
    
    //ArrayList<Unit> army;
    
    boolean active;
    
    Unit[] army;
 
    boolean host, finishedSetup;

    

    public Player(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
 
    public void setNation(int nation) {
      //  System.out.println("Nation Set to:"+nation);
        this.nation = nation;
    }
    
    public int getNation() {
        return nation;
    }
    
    public String nationToString(int nation)
        {
    switch(getNation())
    {
    case CardInterface.BR: return "GreatBritain";
    case CardInterface.AU: return "Austria";
    case CardInterface.FR: return "France"; 
    case CardInterface.OT: return "Ottoman";
    case CardInterface.PR: return "Prussia";
    case CardInterface.RU: return "Russland";
    case CardInterface.SP: return "Spain";
    case CardInterface.US: return "USA";
    default : return "Unknown";
    }
    
    }
            
    
    public void setCards() {
        this.drawPile = new CardSet(60,nation); 
        this.hand = new CardSet(5);     
             
        this.discardPile = new CardSet();
        this.tablePile = new CardSet();

       
        hand.addCardsFromTheTopOfOtherSet(5, drawPile, false);
        hand.sortCard();  
    }
    public void generateUnits(){
     
        army = new Unit[8];
        /*
        If its host then place units on B row else place unit on G row
        */
        int y=0;
      for (int x=getNation()*8  ;x<getNation()*8+8;x++)
        {
            Unit unit =  new Unit(x+1);
          /*
            Startting position row B or G
            */
            unit.setPosition(new Position (  x- ( getNation()*8)    , ( isHost() ? Position.ROW_2 : Position.ROW_7) ));
             army[y] =   unit  ;      
              y++;     
         }
        
            
    // System.out.println("Units Generated:");
    }
    
//    public void generateUnits(){
//     
//        army = new ArrayList<Unit>();
//        /*
//        If its host then place units on B row else place unit on G row
//        */
//        
//        if(isHost())
//      for (int i=getNation()*8  ;i<getNation()*8+8;i++)
//        {
//
//            
//            Unit unit =  new Unit(i+1);
//          /*
//            Pozycja tymczasowo - bedzie tworzona w setupie
//            */
//            unit.setPosition(new Position (  i- ( getNation()*8)    ,1));
//            army.add(   unit     ) ;      
//                   
//              
//       
//        }
//        else 
//      for (int i=getNation()*8  ;i<getNation()*8+8;i++)
//        {
//
//            
//            Unit unit =  new Unit(i+1);
//          /*
//            Pozycja tymczasowo - bedzie tworzona w setupie
//            */
//            unit.setPosition(new Position (  i- ( getNation()*8)    ,7));
//            army.add(   unit     ) ;      
//                   
//              
//       
//        }      
//            
//    // System.out.println("Units Generated:");
//    }
    
    
    public CardSet getDrawPile() {
        return drawPile;
    }
    
    public CardSet getHand() {
        return hand;
    }

    public Unit[] getArmy() {
        return army;
    }

    public void setArmy(Unit[] army) {
        this.army = army;
    }

   
    public CardSet getDiscardPile() {
        return discardPile;
    }
    
     public CardSet getTablePile() {
        return tablePile;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }

    public boolean isFinishedSetup() {
        return finishedSetup;
    }

    public void setFinishedSetup(boolean finishedSetup) {
        this.finishedSetup = finishedSetup;
    }


    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }

    @Override
    public String toString() {
    
        return getName() +
                ",Nation: " + nationToString(getNation())+  
                ",Army size: " + (army != null ? getArmy().length  : "0"
                +",Hand Size: "  + (hand != null ? getHand().cardList.size()  : "0") 
                ) ;

    }
      
    public void setHandPlayableByPhaseAndPosition(int position, int phase) {
        this.getHand().getCardByPosInSet(position).setAvailableForPhase(phase);
    }
    
    

    
}
