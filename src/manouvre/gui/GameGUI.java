/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;
import manouvre.game.commands.DiscardCardCommand;
import manouvre.game.commands.DrawCardCommand;
import manouvre.network.client.Message;
import static java.lang.Math.round;
import static java.lang.Math.round;
import static java.lang.Math.round;
import static java.lang.Math.round;
import static java.lang.Math.round;
import static java.lang.Math.round;



/**
 *
 * @author Bartosz
 */
public class GameGUI {
    
    Game game;
    ArrayList<UnitGUI> currentPlayerArmy = new ArrayList<UnitGUI>(); 
    ArrayList<UnitGUI> opponnetPlayerArmy = new ArrayList<UnitGUI>(); 
    MapGUI mapGui;
    CardSetGUI handSetGui;
    CardSetGUI discardSetGui;
    CardSetGUI drawSetGui;
    CardSetGUI tableSetGui;
    /*
    Wielkosc ramki stolu w kwadracie w pikselach
    */
    final int BACKGRNDTABLE = 678;
    int numberOfDiscardedCards=0;
    
    final int gapSelection = 5;
    final int gapUnit = 7;
    
    ArrayList<Integer> selectionSeq = new ArrayList<Integer>();
    
    int windowMode;
    
    public GameGUI (Game newGame, int windowMode) throws IOException{
        this.game=newGame;
        this.windowMode = windowMode;
        this.mapGui = new MapGUI(game.getMap(), windowMode);
        this.generateUnitsUI();
        this.handSetGui = new CardSetGUI(game.getCurrentPlayer().getHand());
        this.discardSetGui = new CardSetGUI(game.getCurrentPlayer().getDiscardPile());
        this.drawSetGui = new CardSetGUI(game.getCurrentPlayer().getDrawPile());//empty
        this.tableSetGui = new CardSetGUI(game.getCurrentPlayer().getTablePile());//empty
       
        /*
        Set info about first / second player
        */
        CustomDialog dialog = new CustomDialog(CustomDialog.CONFIRMATION_TYPE, "You are" + (
        game.getCurrentPlayer().isFirst() ? " first " : " second ") + "player"  , null, game);
        dialog.setVisible(true);
        
    }
//------------- MAP - LEFT UPPER CORNER OF THE SCREEN -----------------------------------
    void drawMap( Graphics g, int windowMode) {
       
        // draw background
        
        g.drawImage(mapGui.background, 0, 0,BACKGRNDTABLE,BACKGRNDTABLE, null);
        // draw Terrains
       
        drawTerrains(g);
        /*
        Draw border
        */
        drawBorder(g);
        /*
        Draws selection
         */
        drawSelection(g);
        /*
        Draw units
         */
        drawArmy(g);
       
    }
    private void drawTerrains(Graphics g){
    
    if(windowMode == CreateRoomWindow.AS_HOST)
        for (TerrainGUI terrainGUI : mapGui.getTerrainsGUI()) {
                        
            
            g.drawImage(
                    terrainGUI.getImg(), 
                    terrainGUI.getPos().getMouseX(), 
                    terrainGUI.getPos().getMouseY(), 
                    MapGUI.SQUARE_WIDTH,
                    MapGUI.SQUARE_HEIGHT,
                  
                    null);
            
        }
        else if(windowMode == CreateRoomWindow.AS_GUEST)
            for (TerrainGUI terrainGUI : mapGui.getTerrainsGUI()) {
             
            g.drawImage(
                    terrainGUI.getImg(), 
                    terrainGUI.getPos().transpoze().getMouseX(), 
                    terrainGUI.getPos().transpoze().getMouseY(), 
                    MapGUI.SQUARE_WIDTH,
                    MapGUI.SQUARE_HEIGHT,
                    
                    null);
        }
    }
    
    private void drawBorder(Graphics g){
    for(int i=0;i<8;i++){
        
        g.setColor(Color.white);
        
        if(windowMode == CreateRoomWindow.AS_HOST)
                {
                    g.drawString(Integer.toString(i),
                    i* MapGUI.SQUARE_WIDTH + MapGUI.BOARD_START_X + (MapGUI.SQUARE_WIDTH/2), 
                    MapGUI.SQUARE_WIDTH/2)
                    ;
                    
                    g.drawString(Integer.toString(i),
                    (MapGUI.SQUARE_WIDTH/2)
                   ,  (7-i) * MapGUI.SQUARE_WIDTH + MapGUI.BOARD_START_Y + (MapGUI.SQUARE_WIDTH/2))
                    ;
                }
        else if(windowMode == CreateRoomWindow.AS_GUEST)
                {
                 g.drawString(Integer.toString(i),
                    (7-i) * MapGUI.SQUARE_WIDTH + MapGUI.BOARD_START_X + (MapGUI.SQUARE_WIDTH/2), 
                    MapGUI.SQUARE_WIDTH/2)
                    ;
                    
                    g.drawString(Integer.toString(i),
                    (MapGUI.SQUARE_WIDTH/2)
                   ,  (i) * MapGUI.SQUARE_WIDTH + MapGUI.BOARD_START_Y + (MapGUI.SQUARE_WIDTH/2))
                    ;
                }
            
        }
    }
    
    private void drawSelection(Graphics g){
        
     
     if(windowMode == CreateRoomWindow.AS_HOST)
        {
            if (mapGui.isUnitSelected()) {
                for (TerrainGUI terrainGUI : mapGui.getTerrainsGUI()) {
                    if (terrainGUI.isSelected()) {
                        g.drawRoundRect(terrainGUI.getPos().getMouseX() + gapSelection, 
                                terrainGUI.getPos().getMouseY() + gapSelection, 
                                MapGUI.SQUARE_WIDTH - 2 * gapSelection, 
                                MapGUI.SQUARE_HEIGHT - 2 * gapSelection, 
                                10, 10
                        );
                        System.out.println("manouvre.gui.GameGUI.drawMap() " + terrainGUI.getPos().toString());
                        /*
                        Draw AdjencedSpace /Move
                         */
                        if (terrainGUI.getTerrain().getIsOccupiedByUnit()) {

                            System.out.println("manouvre.gui.ClientUI.drawMap() : " + game.getCurrentPlayerUnitAtPosition(terrainGUI.getPos()).toString());
                            ArrayList<Position> movePositions;
                            if(game.getPhase() == Game.SETUP)
                            {movePositions = game.getSetupPossibleMovement();}
                            else if (game.getCurrentPlayer().isPlayingCard() )
                            {
                                movePositions = game.getOneSquareMovements(terrainGUI.getPos());
                            }
                            else
                            {movePositions = game.getPossibleMovement(game.getCurrentPlayerUnitAtPosition(terrainGUI.getPos()));}
                            
                            for (Position drawMovePosion : movePositions) {
                                g.setColor(Color.blue);
                                g.drawRoundRect(drawMovePosion.getMouseX() + gapSelection, 
                                        drawMovePosion.getMouseY() + gapSelection, 
                                        MapGUI.SQUARE_WIDTH - 2 * gapSelection, 
                                        MapGUI.SQUARE_HEIGHT - 2 * gapSelection, 
                                        10, 10);
                            }
                        }
                    }
                }
            }
        }
        else if(windowMode == CreateRoomWindow.AS_GUEST)
        {
            if (mapGui.isUnitSelected()) {
                for (TerrainGUI terrainGUI : mapGui.getTerrainsGUI()) {
                    if (terrainGUI.isSelected()) {
                        g.drawRoundRect(terrainGUI.getPos().transpoze().getMouseX() + gapSelection, 
                                terrainGUI.getPos().transpoze().getMouseY() + gapSelection, 
                                MapGUI.SQUARE_WIDTH - 2 * gapSelection, 
                                MapGUI.SQUARE_HEIGHT - 2 * gapSelection, 
                                10, 10
                        );
                        System.out.println("manouvre.gui.GameGUI.drawMap() " + terrainGUI.getPos().toString());
                        /*
                        Draw AdjencedSpace /Move
                         */
                        if (terrainGUI.getTerrain().getIsOccupiedByUnit()) {

                            System.out.println("manouvre.gui.ClientUI.drawMap() : " + game.getCurrentPlayerUnitAtPosition(terrainGUI.getPos()).toString());
                            ArrayList<Position> movePositions;
                            
                            if(game.getPhase() == Game.SETUP)
                            {movePositions = game.getSetupPossibleMovement();}
                            else if (game.getCurrentPlayer().isPlayingCard() )
                            {
                                movePositions = game.getOneSquareMovements(terrainGUI.getPos());
                            }
                            else
                            {movePositions = game.getPossibleMovement(game.getCurrentPlayerUnitAtPosition(terrainGUI.getPos()));}
                            
                            for (Position drawMovePosion : movePositions) {
                                g.setColor(Color.blue);
                                g.drawRoundRect(drawMovePosion.transpoze().getMouseX() + gapSelection, 
                                        drawMovePosion.transpoze().getMouseY() + gapSelection, 
                                        MapGUI.SQUARE_WIDTH - 2 * gapSelection, 
                                        MapGUI.SQUARE_HEIGHT - 2 * gapSelection, 
                                        10, 10);
                            }
                        }
                    }
                }
            }
        }    
    }
    private void drawArmy(Graphics g){
    
    
        /*
        In setup draw only self army
        */
        if(game.getPhase()== Game.SETUP &&  !( game.getCurrentPlayer().isFinishedSetup() && game.getOpponentPlayer().isFinishedSetup() ) )
        {
            if(windowMode == CreateRoomWindow.AS_HOST)
            {
            for (UnitGUI drawUnit : currentPlayerArmy) {
                g.drawImage(drawUnit.getImg(), 
                        drawUnit.getUnit().getPosition().getMouseX() + MapGUI.PIECES_START_X,
                        drawUnit.getUnit().getPosition().getMouseY() + MapGUI.PIECES_START_Y,
                        MapGUI.PIECE_WIDTH, 
                        MapGUI.PIECE_HEIGHT
                        , null);
                /*
                Draw bad position rectangle
                */
                if( !game.getMap().getTerrainAtPosition(
                        drawUnit.getUnit().getPosition()).isTerrainPassable()
                         || 
                         drawUnit.getUnit().getPosition().getY()  >  Position.ROW_2 
                         
                      )
                {
                    g.setColor(Color.RED);
                    
                    g.drawRoundRect(
                                drawUnit.getUnit().getPosition().getMouseX() + gapSelection, 
                                drawUnit.getUnit().getPosition().getMouseY() + gapSelection, 
                                MapGUI.SQUARE_WIDTH - 2 * gapSelection, 
                                MapGUI.SQUARE_HEIGHT - 2 * gapSelection, 
                                10, 10
                        ); 
                }        
                   
            }
                
                
            }
            else if(windowMode == CreateRoomWindow.AS_GUEST)
            {
               for (UnitGUI drawUnit : currentPlayerArmy) {
                g.drawImage(drawUnit.getImg(), 
                        drawUnit.getUnit().getPosition().transpoze().getMouseX() + MapGUI.PIECES_START_X,
                        drawUnit.getUnit().getPosition().transpoze().getMouseY() + MapGUI.PIECES_START_Y,
                        MapGUI.PIECE_WIDTH, 
                        MapGUI.PIECE_HEIGHT
                        , null);
                
                /*
                Draw bad position rectangle
                */
                if( !game.getMap().getTerrainAtPosition(
                        drawUnit.getUnit().getPosition()
                                                    ).isTerrainPassable()
                         || drawUnit.getUnit().getPosition().getY()  <  Position.ROW_7
                       
                        )
                {
                    g.setColor(Color.red);
                    
                    g.drawRoundRect(
                                drawUnit.getUnit().getPosition().transpoze().getMouseX() + gapSelection, 
                                drawUnit.getUnit().getPosition().transpoze().getMouseY() + gapSelection, 
                                MapGUI.SQUARE_WIDTH - 2 * gapSelection, 
                                MapGUI.SQUARE_HEIGHT - 2 * gapSelection, 
                                10, 10
                        ); 
                }        
                } 
            }
        }
            /*
            On rest phases paint both players army
            */
        else     
          if(windowMode == CreateRoomWindow.AS_HOST){
            for (UnitGUI drawUnit : currentPlayerArmy) {
                g.drawImage(drawUnit.getImg(), 
                        drawUnit.getUnit().getPosition().getMouseX() + MapGUI.PIECES_START_X,
                        drawUnit.getUnit().getPosition().getMouseY() + MapGUI.PIECES_START_Y,
                        MapGUI.PIECE_WIDTH, 
                        MapGUI.PIECE_HEIGHT
                        , null);
            }
            for (UnitGUI drawUnit : opponnetPlayerArmy) {
                g.drawImage(drawUnit.getImg(), 
                        drawUnit.getUnit().getPosition().getMouseX() + MapGUI.PIECES_START_X,
                        drawUnit.getUnit().getPosition().getMouseY() + MapGUI.PIECES_START_Y,
                        MapGUI.PIECE_WIDTH, 
                        MapGUI.PIECE_HEIGHT
                        , null);
                }
            }
            else if(windowMode == CreateRoomWindow.AS_GUEST)
            {
               for (UnitGUI drawUnit : currentPlayerArmy) {
                g.drawImage(drawUnit.getImg(), 
                        drawUnit.getUnit().getPosition().transpoze().getMouseX() + MapGUI.PIECES_START_X,
                        drawUnit.getUnit().getPosition().transpoze().getMouseY() + MapGUI.PIECES_START_Y,
                        MapGUI.PIECE_WIDTH, 
                        MapGUI.PIECE_HEIGHT
                        , null);
                }
            for (UnitGUI drawUnit : opponnetPlayerArmy) {
                g.drawImage(drawUnit.getImg(), 
                        drawUnit.getUnit().getPosition().transpoze().getMouseX() + MapGUI.PIECES_START_X,
                        drawUnit.getUnit().getPosition().transpoze().getMouseY() + MapGUI.PIECES_START_Y,
                        MapGUI.PIECE_WIDTH, 
                        MapGUI.PIECE_HEIGHT
                        , null);
                }
            }
    }
    
    private void generateUnitsUI() {
        for (Unit unit : game.getCurrentPlayer().getArmy()) {
            currentPlayerArmy.add(new UnitGUI(unit));
        }
        
        for (Unit unit : game.getOpponentPlayer().getArmy()) {
            opponnetPlayerArmy.add(new UnitGUI(unit));
        }
    }

    UnitGUI getSelectedUnit() {
        for (UnitGUI unitSearch : this.currentPlayerArmy) {
            if (unitSearch.isSelected()) {
                return unitSearch;
            }
        }
        return null;
    }

    void unselectAllUnits() {
        currentPlayerArmy.stream().forEach((UnitGUI unit) -> {
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
int prevPhase=0;

    public void paintHand(Graphics g, int mouseCoorX, int mouseCoorY, int mouseClick, int phase)                 
    {   
        boolean phaseChanged=false;
        if (prevPhase!=phase) phaseChanged=true;
        prevPhase=phase;
         if(phaseChanged) {
             selectionSeq.clear();//clear selection if phase was changed
             for (int i=0; i<handSetGui.cardsLeftInSet(); i++){  
              handSetGui.getCardByPosInSet(i).setSelected(0);   
             }
         } 
        
        
        float f=0.5f; //scale factor //Normally cards has 260x375 pixels
        int width=round(260*f), height=round(375*f);
        int cardPaddingTop=20;
        int cardPaddingLeft=20;
        int cardPaddingTopTemp=cardPaddingTop;
        int gap = 5;    
        for (int i=0; i<handSetGui.cardsLeftInSet(); i++){  
           if(mouseCoorY>(cardPaddingTop-20*handSetGui.getCardByPosInSet(i).isOverCard()-20*handSetGui.getCardByPosInSet(i).isSelected()) && mouseCoorY<(cardPaddingTop+height)){ // if mouse is in row with cards
                if ((mouseCoorX>cardPaddingLeft+(gap*i)+width*(i)) && mouseCoorX<(cardPaddingLeft+(gap*i)+width*(i+1))){ //if mouse is in th collon with card
                    handSetGui.getCardByPosInSet(i).setOverCard(1);
                } 
                else{
                    handSetGui.getCardByPosInSet(i).setOverCard(0);
                }
            }  
            else  handSetGui.getCardByPosInSet(i).setOverCard(0);
            if(mouseClick==1&&handSetGui.getCardByPosInSet(i).isOverCard()==1){
                if(game.getCurrentPlayer().getHand().getCardByPosInSet(i).isPlayable()){ //select card if it is playable
                    if(handSetGui.getCardByPosInSet(i).isSelected()==0) {
                        handSetGui.getCardByPosInSet(i).setSelected(1);
                        selectionSeq.add(handSetGui.getCardIDByPosInSet(i)); 
                    }   
                    else {
                        handSetGui.getCardByPosInSet(i).setSelected(0);
                        Integer j=handSetGui.getCardIDByPosInSet(i);
                        selectionSeq.remove(j); //remove number Integer j, not position int i
                    }  
                }
            }
                if((handSetGui.getCardByPosInSet(i).isOverCard()==1 || handSetGui.getCardByPosInSet(i).isSelected()==1)&&game.getCurrentPlayer().getHand().getCardByPosInSet(i).isPlayable()) cardPaddingTopTemp=cardPaddingTop-20;
                else cardPaddingTopTemp=cardPaddingTop;
                g.drawImage(handSetGui.getCardByPosInSet(i).getImgFull(), cardPaddingLeft+(width+gap)*i, cardPaddingTopTemp, width, height, null);       
        }
                Integer j=0;
               
                if(!selectionSeq.isEmpty()){ 
                   
                    j=selectionSeq.get(selectionSeq.size()-1);              
                    j=handSetGui.getPositionInSetByCardID(j); 
                    int[] xPoints={cardPaddingLeft+35+width*j+(gap*j),cardPaddingLeft+95+width*j+(gap*j),cardPaddingLeft+35+(95-35)/2+width*j+(gap*j)};
                    int[] yPoints={cardPaddingTop+190,cardPaddingTop+190,cardPaddingTop+178};
                    g.setColor(Color.white);
                    g.setFont(new Font("Bookman Old Style", 1, 11));
                    g.drawString("This card will be visible",cardPaddingLeft+width*j+(gap*j)-10,31+190);
                    g.drawString("on the Discard Pile",cardPaddingLeft+width*j+(gap*j)+0,44+190);  
                    g.fillPolygon(xPoints, yPoints, 3);
                }                         
        }

    public Message discardSelCards(){ //done on hand itseld not on HandGui
        ArrayList<Integer> selectionSeqTemp=new ArrayList<Integer>();
        selectionSeqTemp.clear();
        for(Integer i: selectionSeq) { //make a copy to loose referance
            selectionSeqTemp.add(i);
        }
        
        //execute externally
        DiscardCardCommand discardCard = new DiscardCardCommand(selectionSeqTemp, game.getCurrentPlayer().getName());
                        
        Message discardCardMessage = new Message(Message.COMMAND, game.getCurrentPlayer().getName() , Message.DISCARD_CARD_COMMAND, "IN_CHANNEL");
        discardCardMessage.setCommand(discardCard);
        
        //execute locally
        discardCard.execute(game);
//        for (int i=0; i<selectionSeq.size(); i++){   
//            game.getCurrentPlayer().getHand().dealCardToOtherSetByCardID(selectionSeq.get(i),  game.getCurrentPlayer().getDiscardPile());
//           }
        
            
            selectionSeq.clear();
            handSetGui.reSet(); //reset GUI
            discardSetGui.reSet(); //reset GUI
            drawSetGui.reSet(); //reset GUI
            
           return  discardCardMessage;
    }
    
    public Message drawCards(){  //draw a card from a pile
        //execute externally
        numberOfDiscardedCards=getNumberOfDiscardedCards();
        DrawCardCommand drawCard = new DrawCardCommand(numberOfDiscardedCards, game.getCurrentPlayer().getName());
                        
        Message drawCardMessage = new Message(Message.COMMAND, game.getCurrentPlayer().getName() , Message.DRAW_CARD_COMMAND, "IN_CHANNEL");
        drawCardMessage.setCommand(drawCard);
        
        //execute locally
        drawCard.execute(game);
  
        handSetGui.reSet(); //reset GUI
        discardSetGui.reSet(); //reset GUI
        drawSetGui.reSet(); //reset GUI
        
        
        return  drawCardMessage;
        
    }
    
    public int getNumberOfDiscardedCards(){
    return game.getCurrentPlayer().getHand().getCardSetSize()-game.getCurrentPlayer().getHand().cardsLeftInSet();
    }
    
    public void playSelectedCard(){
         for (int i=0; i<selectionSeq.size(); i++){   
            game.getCurrentPlayer().getHand().dealCardToOtherSetByCardID(selectionSeq.get(i),  game.getCurrentPlayer().getTablePile());
            }
            selectionSeq.clear();
            handSetGui.reSet(); //reset GUI
            discardSetGui.reSet(); //reset GUI
            drawSetGui.reSet(); //reset GUI
            tableSetGui.reSet();
    
    }
    

    public boolean getSelectionSeqIsEmpty() {
        return selectionSeq.isEmpty();
    }
    
   
            
    public void paintDiscard(Graphics g, boolean paintOpponent){
        CardGUI cardGui;
        float f=0.41f; //scale factor //Normally cards has 260x375 pixels
        int width=round(260*f), height=round(375*f);
        int cardPaddingTop=16;
        int cardPaddingLeft=5;
        if (paintOpponent==true){
            if(game.getOpponentPlayer().getDiscardPile().cardsLeftInSet()>0){
                cardGui=new CardGUI(game.getOpponentPlayer().getDiscardPile().lastCardFromThisSet(false));
                g.drawImage(cardGui.getImgFull(), cardPaddingLeft, cardPaddingTop, width, height, null);           
            }
            else{
                g.setColor(Color.white);
                g.setFont(new Font("Bookman Old Style", 1, 20));
                g.drawString("No Card",20,100);  
            }   
        }
        else{
            if(discardSetGui.cardsLeftInSet()>0){
                g.drawImage(discardSetGui.getCardByPosInSet(discardSetGui.cardsLeftInSet()-1).getImgFull(), cardPaddingLeft, cardPaddingTop, width, height, null);           
            }
            else{
                g.setColor(Color.white);
                g.setFont(new Font("Bookman Old Style", 1, 20));
                g.drawString("No Card",20,100);  
            }
        }
    }
    
    public void paintDrawLeft(Graphics g, boolean paintOpponent){
        float f=0.41f; //scale factor //Normally cards has 260x375 pixels
        int width=round(260*f), height=round(375*f);
        int cardPaddingTop=16;
        int cardPaddingLeft=5;
        
        Integer drawLeft;
       
        if (paintOpponent==true)
            drawLeft=game.getOpponentPlayer().getDrawPile().cardsLeftInSet();
        else 
            drawLeft=drawSetGui.cardsLeftInSet();

        g.setColor(Color.white);
        g.setFont(new Font("Bookman Old Style", 1, 50));        
        g.drawString(drawLeft.toString(),20,110); 
    }
    
   public static void drawStringMultiLine(Graphics g, String text, int lineWidth, int x, int y) {
    FontMetrics m = g.getFontMetrics();
    if(m.stringWidth(text) < lineWidth) {
        g.drawString(text, x, y);
    } else {
        String[] words = text.split(" ");
        String currentLine = words[0];
        for(int i = 1; i < words.length; i++) {
            if(m.stringWidth(currentLine+words[i]) < lineWidth) {
                currentLine += " "+words[i];
            } else {
                g.drawString(currentLine, x, y);
                y += m.getHeight();
                currentLine = words[i];
            }
        }
        if(currentLine.trim().length() > 0) {
            g.drawString(currentLine, x, y);
        }
    }
}
    
    public void paintTablePanel(Graphics g){
        Integer tempInt;
        String tempString;
        int gap=5;
        float f=0.41f; //scale factor //Normally cards has 260x375 pixels
        int width=round(260*f), height=round(375*f);
        int cardPaddingTop=16;
        int cardPaddingLeft=10;
        int cardPaddingTopText=138;
        for (int i=0; i<tableSetGui.cardsLeftInSet(); i++){  
            if(tableSetGui.cardsLeftInSet()>0){
             g.drawImage(tableSetGui.getCardByPosInSet(i).getImgFull(), cardPaddingLeft+(width+gap)*i, cardPaddingTop, width, height, null);   
             if(tableSetGui.getCardByPosInSet(i).card.getCardType()==0){ //if UNIT card selected
                g.setColor(Color.white);
                g.setFont(new Font("Bookman Old Style", 1, 11));
                
                tempInt=tableSetGui.getCardByPosInSet(i).card.getUnitAttack();
                tempString=tempInt.toString();
                g.drawString("Attack",cardPaddingLeft+width*i+(gap*i)+0,44+cardPaddingTopText); g.drawString(tempString, cardPaddingLeft+width*i+(gap*i)+55,44+cardPaddingTopText);
                
                tempInt=tableSetGui.getCardByPosInSet(i).card.getUnitDefence();
                tempString=tempInt.toString();
                g.drawString("Defence",cardPaddingLeft+width*i+(gap*i)+0,54+cardPaddingTopText); g.drawString(tempString, cardPaddingLeft+width*i+(gap*i)+55,54+cardPaddingTopText);
                
                tempInt=tableSetGui.getCardByPosInSet(i).card.getUnitPursuit();
                tempString=tempInt.toString();
                g.drawString("Pursuit",cardPaddingLeft+width*i+(gap*i)+0,64+cardPaddingTopText); g.drawString(tempString, cardPaddingLeft+width*i+(gap*i)+55,64+cardPaddingTopText);
                
                tempInt=tableSetGui.getCardByPosInSet(i).card.getUnitRange();
                tempString=tempInt.toString();
                g.drawString("Range",cardPaddingLeft+width*i+(gap*i)+0,74+cardPaddingTopText); g.drawString(tempString, cardPaddingLeft+width*i+(gap*i)+55,74+cardPaddingTopText);
                
                tempInt=tableSetGui.getCardByPosInSet(i).card.getUnitVolley();
                tempString=tempInt.toString();
                g.drawString("Volley",cardPaddingLeft+width*i+(gap*i)+0,84+cardPaddingTopText); g.drawString(tempString, cardPaddingLeft+width*i+(gap*i)+55,84+cardPaddingTopText);
                
                 
               tempInt=tableSetGui.getCardByPosInSet(i).card.getUnitBombard();
                tempString=tempInt.toString();
                g.drawString("Bombard",cardPaddingLeft+width*i+(gap*i)+0,94+cardPaddingTopText); g.drawString(tempString, cardPaddingLeft+width*i+(gap*i)+55,94+cardPaddingTopText);
             }
             if(tableSetGui.getCardByPosInSet(i).card.getCardType()==1){//if HQUNIT card selected
                g.setColor(Color.white);
                g.setFont(new Font("Bookman Old Style", 1, 11));

                tempString=tableSetGui.getCardByPosInSet(i).card.getUnitDescr();
                drawStringMultiLine(g, tempString, 100, cardPaddingLeft+width*i+(gap*i)+5,44+cardPaddingTopText); 
             }
             
             if(tableSetGui.getCardByPosInSet(i).card.getCardType()==2){//if HQLeader card selected
                g.setColor(Color.white);
                g.setFont(new Font("Bookman Old Style", 1, 11));
                
                tempString=tableSetGui.getCardByPosInSet(i).card.getLederCommand();
                g.drawString("Command",cardPaddingLeft+width*i+(gap*i)+0,44+cardPaddingTopText); g.drawString(tempString, cardPaddingLeft+width*i+(gap*i)+55,44+cardPaddingTopText);
                
                tempInt=tableSetGui.getCardByPosInSet(i).card.getLederCombat();
                tempString=tempInt.toString();
                g.drawString("Defence",cardPaddingLeft+width*i+(gap*i)+0,54+cardPaddingTopText); g.drawString(tempString, cardPaddingLeft+width*i+(gap*i)+55,54+cardPaddingTopText);
                
                tempInt=tableSetGui.getCardByPosInSet(i).card.getLederRally();
                tempString=tempInt.toString();
                g.drawString("Pursuit",cardPaddingLeft+width*i+(gap*i)+0,64+cardPaddingTopText); g.drawString(tempString, cardPaddingLeft+width*i+(gap*i)+55,64+cardPaddingTopText);
                
                tempString=tableSetGui.getCardByPosInSet(i).card.getLederGrandBatt();
                g.drawString("Range",cardPaddingLeft+width*i+(gap*i)+0,74+cardPaddingTopText); g.drawString(tempString, cardPaddingLeft+width*i+(gap*i)+55,74+cardPaddingTopText);
                
                tempString=tableSetGui.getCardByPosInSet(i).card.getUnitDescr();
                drawStringMultiLine(g, tempString, 100, cardPaddingLeft+width*i+(gap*i)+5,84+cardPaddingTopText);
             }
            }
            else{
                g.setColor(Color.white);
                g.setFont(new Font("Bookman Old Style", 1, 20));
                g.drawString("No Card",20,100);  
            }
        }
    }
   
   
    public void paintCombatPanel(Graphics g){ //paint all the details of the cards and units on the table
      
        
    }
    
    public UnitGUI getUnitGuiOnMapGui(Position position){
    
           for(UnitGUI unitSearch: getUnitsGui()){
        
            if(unitSearch.getUnit().getPosition().equals(position))
            {
                return unitSearch;
              }
            
        
        }
              
        return null;
    
    }
    
    
    public ArrayList<UnitGUI> getUnitsGui() {
        return currentPlayerArmy;
    }

    public void setUnitsGui(ArrayList<UnitGUI> unitsGui) {
        this.currentPlayerArmy = unitsGui;
    }
    
     
}
