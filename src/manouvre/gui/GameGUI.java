/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import static java.lang.Math.round;
import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;
import static java.lang.Math.round;
import static java.lang.Math.round;
import static java.lang.Math.round;

/**
 *
 * @author Bartosz
 */
public class GameGUI {
    
    Game game;
    ArrayList<UnitGUI> unitsGui = new ArrayList<UnitGUI>(); 
    MapGUI mapGui;
    CardSetGUI cardSetGui;
    CardSetGUI discardSetGui;
    
    
    
    
    public GameGUI (Game newGame) throws IOException{
        this.game=newGame;
        this.mapGui = new MapGUI(game.getMap());
        this.generateUnitsUI();
        this.cardSetGui = new CardSetGUI(game.getCurrentPlayer().getHand());
        this.discardSetGui = new CardSetGUI(game.getCurrentPlayer().getDiscardPile());
       
    }
//------------- MAP - RIGHT UPPER CORNER OF THE SCREEN -----------------------------------
    void drawMap(Graphics g) {
        int gap = 5;
        // draw background
        //g.drawImage(this.imgBackground, 0, 0, null);
        // draw terrain
        for (TerrainGUI terrainGUI : mapGui.getTerrainsGUI()) {
            g.drawImage(terrainGUI.getImg(), terrainGUI.getPos().getMouseX(), terrainGUI.getPos().getMouseY(), null);
        }
        /*
        Draws selection
         */
        if (mapGui.isUnitSelected()) {
            for (TerrainGUI terrain : mapGui.getTerrainsGUI()) {
                if (terrain.isSelected()) {
                    g.drawRoundRect(terrain.getPos().getMouseX() + gap, terrain.getPos().getMouseY() + gap, MapGUI.SQUARE_WIDTH - 2 * gap, MapGUI.SQUARE_HEIGHT - 2 * gap, 10, 10);
                    System.out.println("Position " + terrain.getPos());
                    /*
                    Draw AdjencedSpace /Move
                     */
                    if (!terrain.getTerrain().getIsOccupiedByUnit()) {
                        ArrayList<Position> adjencedPositions = terrain.getPos().getAdjencedPositions();
                        System.out.println(terrain.getPos().toString());
                        g.setColor(Color.red);
                        for (int k = 0; k < adjencedPositions.size(); k++) {
                            g.drawRoundRect(adjencedPositions.get(k).getMouseX() + gap, adjencedPositions.get(k).getMouseY() + gap, MapGUI.SQUARE_WIDTH - 2 * gap, MapGUI.SQUARE_HEIGHT - 2 * gap, 10, 10);
                        }
                    } else {
                        System.out.println("manouvre.gui.ClientUI.drawMap() : " + game.getUnitAtPosition(terrain.getPos()).toString());
                        ArrayList<Position> movePositions = game.getPossibleMovement(game.getUnitAtPosition(terrain.getPos()));
                        for (Position drawMovePosion : movePositions) {
                            g.setColor(Color.blue);
                            g.drawRoundRect(drawMovePosion.getMouseX() + gap, drawMovePosion.getMouseY() + gap, MapGUI.SQUARE_WIDTH - 2 * gap, MapGUI.SQUARE_HEIGHT - 2 * gap, 10, 10);
                        }
                    }
                }
            }
        }
        /*
        Draw units
         */
        int gapUnit = 7;
        for (UnitGUI drawUnit : unitsGui) {
            g.drawImage(drawUnit.getImg(), drawUnit.getUnit().getPos().getMouseX() + gapUnit, drawUnit.getUnit().getPos().getMouseY() + gapUnit, 46, 46, null);
        }
    }

    void generateUnitsUI() {
        for (Unit unit : game.getCurrentPlayer().getArmy()) {
            unitsGui.add(new UnitGUI(unit));
        }
    }

    UnitGUI getSelectedUnit() {
        for (UnitGUI unitSearch : this.unitsGui) {
            if (unitSearch.isSelected()) {
                return unitSearch;
            }
        }
        return null;
    }

    void unselectAllUnits() {
        unitsGui.stream().forEach((UnitGUI unit) -> {
            unit.setSelected(false);
        });
        mapGui.setUnitSelected(false);
    }
    
    public Game getGame() {
        return game;
    }

    public MapGUI getMapGui() {
        return mapGui;
    }
    
    
    
 //-------- CARDS - BOTTOM OF THE SCREEN -----------------------------------

    public void drawCard(Graphics g, int mouseCoorX, int mouseCoorY, int mouseClick)                 
    {   
        float f=0.5f; //scale factor //Normally cards has 260x375 pixels
        int width=round(260*f), height=round(375*f);
        int cardPaddingTop=70;
        int cardPaddingLeft=20;
        int gap = 5;
        
        
       // System.out.println("MouseX: "+mouseCoorX +" MouseY: "+mouseCoorY);
        
        for (int i=0; i<cardSetGui.cardsLeftInSet(); i++){  
            
           if(mouseCoorY>(cardPaddingTop-20*cardSetGui.getCardByPosInSet(i).isOverCard()-20*cardSetGui.getCardByPosInSet(i).isSelected()) && mouseCoorY<(cardPaddingTop+height)){ // if mouse is in row with cards
                if ((mouseCoorX>cardPaddingLeft+(gap*i)+width*(i)) && mouseCoorX<(cardPaddingLeft+(gap*i)+width*(i+1))){ //if mouse is in th collon with card
                   cardSetGui.getCardByPosInSet(i).setOverCard(1);
                } 
                else{
                   cardSetGui.getCardByPosInSet(i).setOverCard(0);
                }
            }  
            else  cardSetGui.getCardByPosInSet(i).setOverCard(0);
            if(mouseClick==1&&cardSetGui.getCardByPosInSet(i).isOverCard()==1){
                if(cardSetGui.getCardByPosInSet(i).isSelected()==0) {
                    cardSetGui.getCardByPosInSet(i).setSelected(1);
                    System.out.println("card ID("+cardSetGui.getCardIDBySetID(i)+") Selected Status:" + cardSetGui.getCardByPosInSet(i).isSelected());
                }   
                else {
                    cardSetGui.getCardByPosInSet(i).setSelected(0);
                    System.out.println("card ID("+cardSetGui.getCardIDBySetID(i)+") Selected Status:" + cardSetGui.getCardByPosInSet(i).isSelected());
                }           
            }
          
        g.drawImage(cardSetGui.getCardByPosInSet(i).getImgFull(), cardPaddingLeft+(width+gap)*i, cardPaddingTop-20*cardSetGui.getCardByPosInSet(i).isOverCard()-20*cardSetGui.getCardByPosInSet(i).isSelected(), width, height, null);       
        }
    
    }
    
    public void discardSelCards(){
        //get all selected cards
        int selCards=0;
          for (int i=0; i<cardSetGui.cardsLeftInSet(); i++){  
            if(cardSetGui.getCardByPosInSet(i).isSelected()==1){
                System.out.println("remove card ID" + game.getCurrentPlayer().getHand().getCardIDByPosInSet(i));
                game.getCurrentPlayer().getHand().dealCardToOtherSetByHandPos(i, game.getCurrentPlayer().getDiscardPile());
                cardSetGui.removeCardBySetID(i);
                i=-1;
                selCards++;
            }
          }
          if(selCards!=0){
            game.getCurrentPlayer().getHand().addRandomCardsFromOtherSet(selCards, game.getCurrentPlayer().getDrawPile());
            game.getCurrentPlayer().getHand().sortCard();
            cardSetGui.reSet();
            discardSetGui.reSet();
            selCards=0; 
             System.out.println("------START--------");
            System.out.println("Draw number of cards Left:"+game.getCurrentPlayer().getDrawPile().cardsLeftInSet());       
            System.out.println("Draw cards Left:");
            game.getCurrentPlayer().getDrawPile().getAllCardsIDFromSet();
             System.out.println("---------------");
            System.out.println("Discard number of cards: "+game.getCurrentPlayer().getDiscardPile().cardsLeftInSet());       
            System.out.println("Discard cards Left:");
            game.getCurrentPlayer().getDiscardPile().getAllCardsIDFromSet();
             System.out.println("------END--------");
          }
    }
    public void drawDiscard(Graphics g){
        float f=0.5f; //scale factor //Normally cards has 260x375 pixels
        int width=round(260*f), height=round(375*f);
        int cardPaddingTop=70;
        int cardPaddingLeft=20;
        if(discardSetGui.cardsLeftInSet()>0){
         g.drawImage(discardSetGui.getCardByPosInSet(discardSetGui.cardsLeftInSet()-1).getImgFull(), cardPaddingLeft, cardPaddingTop, width, height, null);           
        }
        else{
          g.drawString("No Card",20,60);  
        }
    }
    
    public void drawDrawLeft(Graphics g){
        Integer drawLeft=game.getCurrentPlayer().getDrawPile().cardsLeftInSet();
        
        g.drawString(drawLeft.toString(),20,60); 
    }
    
    
}
