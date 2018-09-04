/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.game;

import java.io.Serializable;
import java.util.ArrayList;
import manouvre.interfaces.CardInterface;

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
    int nation; //for nation description see Card
    
    CardSet hand;
    CardSet drawPile;
    CardSet discardPile;
    CardSet tablePile;
    /*
    Field to store whole card flow needed data.
    */
    CardCommandFactory cardCommandFactory ;
 
    /*
    Is player currently playing and is first player
    */
    boolean active, first, moved, draw , attacked;
    
    Unit[] army;
    
    int score;
 
    boolean host, finishedSetup;

    public Player(String name) {
       this.name = name;
       this.active = true;
       this.score = 0;
    }
    public void resetPlayer(){
    
    for(Unit unit : getArmy()) {
        if(unit.hasMoved())
             unit.setMoved(false);
        if(unit.isSelected())
            unit.setSelected(false);
        if(unit.isRetriving())
            unit.setRetriving(false);
        if(unit.isSupporting())
            unit.setSupporting(false);
        if(unit.getRestorationDice()!= null)
            unit.setRestorationDice(null);
        }   
        setMoved(false);
        setDraw(false);
        hasAttacked(false);
        getHand().unselectAllCards();
    }

    public String getNationAsString(boolean shortName)
    {
    if(!shortName) {       
        switch(getNation())
        {
        case Card.BR: return "GreatBritain";
        case Card.AU: return "Austria";
        case Card.FR: return "France"; 
        case Card.OT: return "Ottoman";
        case Card.PR: return "Prussia";
        case Card.RU: return "Russland";
        case Card.SP: return "Spain";
        case Card.US: return "USA";
        default: return "Unknown";
        }
    }
    else
    {
        switch(getNation())
        {
        case Card.BR: return "BR";
        case Card.AU: return "AU";
        case Card.FR: return "FR"; 
        case Card.OT: return "OT";
        case Card.PR: return "PR";
        case Card.RU: return "RU";
        case Card.SP: return "SP";
        case Card.US: return "US";
        default: return "Unknown";
        }  
    }
    }
            
    
    public void setCards() {
        this.drawPile = new CardSet(nation ,"DRAW"); 
        this.hand = new CardSet(nation ,"HAND"); 
        this.discardPile = new CardSet(nation ,"DISCARD"); 
        this.tablePile = new CardSet(nation ,"TABLE");
        if(nation==CardInterface.AU){
            hand.addCard(drawPile.getCardByName("Skirmish", true));
            hand.addCard(drawPile.getCardByName("Guerrillas", true));
            hand.addCard(drawPile.getCardByName("Supply", true));
            hand.addCard(drawPile.getCardByName("Duke of Schwarzenberg", true));
            hand.addCard(drawPile.getCardByName("4th  Regiment", true));
        }
        else if(nation==CardInterface.FR){
            hand.addCard(drawPile.getCardByName("Joachim Murat", true));
            hand.addCard(drawPile.getCardByName("French Sappers", true));
            hand.addCard(drawPile.getCardByName("1st Cuirassiers", true));
            hand.addCard(drawPile.getCardByName("Forced March", true));
            hand.addCard(drawPile.getCardByName("Supply", true));
        } 
        else
        drawPile.moveTopXCardsTo(5, hand);
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
            unit.setOwner(this);
             army[y] =   unit  ;      
              y++;     
         }
 
    }
    
    /*
    Getters and setters
    */
    
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
    
    public CardSet getDrawPile() {
        return drawPile;
    }
    
    public CardSet getHand() {
        return hand;
    }

    public Unit[] getArmy() {
        return army;
    }
    
    public ArrayList<Position> getArmyPositions(){
     
       ArrayList<Position> unitsPositions = new ArrayList<>();
       Unit[] units = getArmy();
        
    for (int i=0;i<getArmy().length;i++)
             {
                if(!units[i].isEliminated())
                        unitsPositions.add(units[i].getPosition());
             }
             return unitsPositions;
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

    public void setTablePile(CardSet tablePile) {
        this.tablePile = tablePile;
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

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean hasMoved() {
        return moved;
    }

    public boolean hasAttacked() {
        return attacked;
    }

    public void hasAttacked(boolean attacked) {
        this.attacked = attacked;
    }

    
    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public boolean hasDrawn() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    
    
    
    
    @Override
    public String toString() {
    
        return getName() +
                ",Nation: " + getNationAsString(false)+  
                ",Army size: " + (army != null ? getArmy().length  : "0"
                +",Hand Size: "  + (hand != null ? getHand().getCardList().size()  : "0") 
                ) ;

    }
 
    public Unit getLastMovedUnit(){
    
        for(Unit checkUnit: getArmy()){
        
            if(checkUnit.hasMoved()) return checkUnit;
        }
        return null;
        
    }
    
    public Unit getUnitByPosition(Position position){
    
           for(Unit unitSearch: getArmy()){
        
            if(unitSearch.getPosition().equals(position))
            {
                return unitSearch;
            }

        }
              
        return null;
    
    }
    
    public int getScore()
    {
        return score;
    }
    
    public ArrayList<Unit> getNotKilledUnits(){
        
        ArrayList notKilledUnits = new ArrayList();
        for(Unit unit:getArmy())    
            if(!unit.isEliminated()) 
                notKilledUnits.add(unit);

        return notKilledUnits;
    }
    
    public int getUnitsKilled()
    {
        int unitskilled = 0;
        for(Unit unit:getArmy())    
        {
            if(unit.isEliminated()) unitskilled ++;
        }
        
        return unitskilled;
    }
    
}
