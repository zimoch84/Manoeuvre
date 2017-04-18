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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;
import manouvre.game.commands.DiscardCardCommand;
import manouvre.game.commands.DrawCardCommand;
import manouvre.network.client.Message;
import manouvre.game.commands.CommandQueue;
import manouvre.game.Card;
import manouvre.game.CardCommandFactory;
import manouvre.game.Player;
import static java.lang.Math.round;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import manouvre.game.Dice;
import manouvre.game.Terrain;
import manouvre.game.commands.EndSetupCommand;
import manouvre.game.interfaces.CardInterface;
import manouvre.game.interfaces.Command;
import static java.lang.Math.round;
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

    Player currentPlayer;
    CardSetGUI discardSetGui;
    CardSetGUI drawSetGui;
    CardSetGUI tableSetGui;
    
    
    BufferedImage  infoImage;
    
    /*
    Wielkosc ramki stolu w kwadracie w pikselach
    */
    final int BACKGRNDTABLE = 678;
    int numberOfDiscardedCards=0;
    
    final int gapSelection = 5;
    final int gapUnit = 7;
    
    ArrayList<Integer> selectionSeq = new ArrayList<Integer>();
    
    int windowMode;
    
    boolean lockGUI=false;
    boolean freeMove = false;
    
    CommandQueue cmdQueue;
    
    public GameGUI (Game newGame, int windowMode, CommandQueue cmdQueue) throws IOException{
        this.game=newGame;
        this.currentPlayer=game.getCurrentPlayer();
        this.windowMode = windowMode;
        this.mapGui = new MapGUI(game.getMap(), windowMode);
        this.generateUnitsUI();
       
        this.handSetGui = new CardSetGUI(currentPlayer.getHand());
        this.discardSetGui = new CardSetGUI(currentPlayer.getDiscardPile());
        this.drawSetGui = new CardSetGUI(currentPlayer.getDrawPile());//empty
        this.tableSetGui = new CardSetGUI(currentPlayer.getTablePile());//empty
        
        this.cmdQueue=cmdQueue;
               
        /*
        Set info about first / second player
        */
        CustomDialog dialog = new CustomDialog(CustomDialog.CONFIRMATION_TYPE, currentPlayer.getName() + ", You are" + (
        currentPlayer.isFirst() ? " first " : " second ") + "player"  , (CommandQueue) null, game);
        dialog.setVisible(true);
        
    }
    void drawInfoPanel(Graphics g){
    
        if(infoImage != null)
            
            g.drawImage(infoImage, 0, 0,infoImage.getHeight(),infoImage.getHeight(), null);
        
    
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
        /*
        Draw retrieving arrows
        */
        //drawRetrieving(g);
        /*
        Draw LOS
        */
        //drawLOS(g);
        /*
        Draw card actions
        */
        drawCardSelections(g);
        
       
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
        
    if (mapGui.isUnitSelected()) 
        {
        for (Terrain terrain : game.getMap().getTerrainz()) {
            if (terrain.isSelected()) {
                drawRectangleOnPosition(g, terrain.getPosition(), Color.yellow);
                /*                Draw AdjencedSpace /Move                 */
                if (terrain.getIsOccupiedByUnit()) 
                {
                    ArrayList<Position> movePositions;
                    if(game.getPhase() == Game.SETUP && !game.getCurrentPlayer().isPlayingCard())
                    {
                        movePositions = game.getSetupPossibleMovement();
                    }
                    else if (game.getCurrentPlayerUnitAtPosition(terrain.getPosition()).isRetriving()  )
                    {
                        return;
                    }
                    else
                    {
                        movePositions = game.getPossibleMovement(game.getCurrentPlayerUnitAtPosition(terrain.getPosition()));
                    }
                
                    drawMultipleRectanglesOnPositions(g, movePositions, Color.blue);
                }
            }
            }
        }
    }
    private void drawArmy(Graphics g){
    
    
        /*
        In setup draw only self army
        */
        if(game.getPhase()== Game.SETUP &&  !( currentPlayer.isFinishedSetup() && game.getOpponentPlayer().isFinishedSetup() ) )
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
            
        {

            for (UnitGUI drawUnit : currentPlayerArmy) {
                drawImageOnPosition(g, drawUnit.getUnit().getPosition(), drawUnit.getImg());
                
            }
            for (UnitGUI drawUnit : opponnetPlayerArmy) {
                 drawImageOnPosition(g, drawUnit.getUnit().getPosition(), drawUnit.getImg());
                }
            }

    }
    private void drawCardSelections(Graphics g){
    
        if(currentPlayer.isPlayingCard())
        {
            ArrayList<Position> movePositions;
            CardCommandFactory cardFactory = game.getCardCommandFactory();
            Card playingCard = cardFactory.getCurrentPlayedCard();

            switch (playingCard.getCardType()){
                case Card.HQCARD :
                {
                    switch(playingCard.getHQType())
                    {
                    case Card.FORCED_MARCH: 
                        {
                        Unit lastMovedUnit = currentPlayer.getLastMovedUnit();
                        movePositions = game.getOneSquareMovements(lastMovedUnit.getPosition());
                        
                        drawMultipleRectanglesOnPositions(g, movePositions, Color.red);
                        break;
                        }
                    }

                }
                case Card.UNIT :{
                    /*
                    Draw selection of unit that matches playing card
                    */
                    if(game.checkCurrentPlayerUnitByName(playingCard.getCardName()))
                    {
                        Unit attackingUnit = game.getCurrentPlayerUnitByName(playingCard.getCardName());
                        Position unitPosition = attackingUnit.getPosition();
                        drawRectangleOnPosition(g, unitPosition, Color.red);
                    /*
                    Draw possible targets if we know playing Card Mode            
                    */                    
                        if(playingCard.getPlayingCardMode() > 0  )
                        {
                             cardFactory.setSelectedUnit(attackingUnit);
                             cardFactory.calculateAttackingPositions();

                             if(!cardFactory.getAttackingPositions().isEmpty())
                             drawArrowToPositions(g, 
                                     attackingUnit.getPosition(),
                                     cardFactory.getAttackingPositions()
                             );

                        }
                   }
                break;    
                }   
            }
               
        }
    
    }
    /*
    Draw image on position
    */
    
    private void drawImageOnPosition(Graphics g, Position position, Image image){
        
        
         g.drawImage(image, 
                        (windowMode == CreateRoomWindow.AS_HOST) ? 
                        position.getMouseX() + MapGUI.PIECES_START_X:
                        position.transpoze().getMouseX() + MapGUI.PIECES_START_X
                                ,
                        (windowMode == CreateRoomWindow.AS_HOST) ?
                        position.getMouseY() + MapGUI.PIECES_START_Y : 
                        position.transpoze().getMouseY() + MapGUI.PIECES_START_Y,
                        MapGUI.PIECE_WIDTH, 
                        MapGUI.PIECE_HEIGHT
                        , null);
    }
    /*
    Draw rectangle on position
    */
    private void drawRectangleOnPosition(Graphics g, Position position, Color color){
    
    g.setColor(color);
    g.drawRoundRect(
            (windowMode == CreateRoomWindow.AS_HOST) ? 
                    position.getMouseX(): 
                    position.transpoze().getMouseX()
            + gapSelection,
            (windowMode == CreateRoomWindow.AS_HOST) ?
                    position.getMouseY(): 
                    position.transpoze().getMouseY()
                    + gapSelection, 
            MapGUI.SQUARE_WIDTH - 2 * gapSelection, 
            MapGUI.SQUARE_HEIGHT - 2 * gapSelection, 
            10, 10);
    
    }
    
      /*
    Draw rectangles on positions
    */
    private void drawMultipleRectanglesOnPositions(Graphics g, ArrayList<Position> positions, Color color){
    
    
    for(Position drawPosition: positions)
    {
    drawRectangleOnPosition( g,  drawPosition, color);
    }
    }
    
    /*
    Draw arrows to position
    */
    
    private  void drawArrowToPositions(Graphics g , Position fromPosition, ArrayList<Position> toPositions){
    
    for (Position losPositons: toPositions  )
                {
                    drawArrow(g,
                    (windowMode == CreateRoomWindow.AS_HOST) ? 
                            fromPosition.getMouseX() +  MapGUI.PIECE_WIDTH / 2 
                            :
                            fromPosition.transpoze().getMouseX() +  MapGUI.PIECE_WIDTH / 2
                            ,
                    (windowMode == CreateRoomWindow.AS_HOST)
                            ?
                            fromPosition.getMouseY() +  MapGUI.PIECE_HEIGHT / 2
                            :        
                            fromPosition.transpoze().getMouseY() +  MapGUI.PIECE_HEIGHT / 2        
                                    ,                    
                    (windowMode == CreateRoomWindow.AS_HOST) ?
                            losPositons.getMouseX() + MapGUI.PIECE_WIDTH / 2
                            :
                            losPositons.transpoze().getMouseX() + MapGUI.PIECE_WIDTH / 2        
                            ,
                    (windowMode == CreateRoomWindow.AS_HOST) ?
                            losPositons.getMouseY() +  MapGUI.PIECE_WIDTH / 2
                            :
                            losPositons.transpoze().getMouseY() +  MapGUI.PIECE_WIDTH / 2      
                    , 10,15)
                            ;
                    }
    
    }
    /**
      * Draw an arrow line betwwen two point 
      * @param g the graphic component
      * @param x1 x-position of first point
      * @param y1 y-position of first point
      * @param x2 x-position of second point
      * @param y2 y-position of second point
      * @param d  the width of the arrow
      * @param h  the height of the arrow
      */
     private void drawArrow(Graphics g, int x1, int y1, int x2, int y2, int d, int h){
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx*dx + dy*dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy/D, cos = dx/D;

        x = xm*cos - ym*sin + x1;
        ym = xm*sin + ym*cos + y1;
        xm = x;

        x = xn*cos - yn*sin + x1;
        yn = xn*sin + yn*cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        g.drawLine(x1, y1, x2, y2);
        g.fillPolygon(xpoints, ypoints, 3);
     }
    
    /**
      * Draw an arrow line betwwen two point 
      * @param g the graphic component
      * @param x1 x-position of first point
      * @param y1 y-position of first point
      * @param x2 x-position of second point
      * @param y2 y-position of second point
      * @param d  the width of the arrow
      * @param h  the height of the arrow
      */
     private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h){
        int dx = x2 - x1, dy = y2 - y1;
        double D = Math.sqrt(dx*dx + dy*dy);
        double xm = D - d, xn = xm, ym = h, yn = -h, x;
        double sin = dy/D, cos = dx/D;

        x = xm*cos - ym*sin + x1;
        ym = xm*sin + ym*cos + y1;
        xm = x;

        x = xn*cos - yn*sin + x1;
        yn = xn*sin + yn*cos + y1;
        xn = x;

        int[] xpoints = {x2, (int) xm, (int) xn};
        int[] ypoints = {y2, (int) ym, (int) yn};

        g.drawLine(x1, y1, x2, y2);
        g.fillPolygon(xpoints, ypoints, 3);
     }
    
    private void drawRetrieving(Graphics g){
     
    if (mapGui.isUnitSelected()){
        
        Unit selectedUnit = game.getSelectedUnit();
        
            if(selectedUnit.isRetriving()) 
                
                for (Position retrivingPositons: game.getRetreatPositions(selectedUnit))
                {
                    
                    drawArrowLine(g,
                    (windowMode == CreateRoomWindow.AS_HOST) ? 
                            selectedUnit.getPosition().getMouseX() +  MapGUI.PIECE_WIDTH / 2 
                            :
                            selectedUnit.getPosition().transpoze().getMouseX() +  MapGUI.PIECE_WIDTH / 2
                            ,
                    (windowMode == CreateRoomWindow.AS_HOST)
                            ?
                            selectedUnit.getPosition().getMouseY() +  MapGUI.PIECE_HEIGHT / 2
                            :        
                            selectedUnit.getPosition().transpoze().getMouseY() +  MapGUI.PIECE_HEIGHT / 2        
                                    ,                    
                    (windowMode == CreateRoomWindow.AS_HOST) ?
                            retrivingPositons.getMouseX() + MapGUI.PIECE_WIDTH / 2
                            :
                            retrivingPositons.transpoze().getMouseX() + MapGUI.PIECE_WIDTH / 2        
                            ,
                    (windowMode == CreateRoomWindow.AS_HOST) ?
                            retrivingPositons.getMouseY() +  MapGUI.PIECE_WIDTH / 2
                            :
                            retrivingPositons.transpoze().getMouseY() +  MapGUI.PIECE_WIDTH / 2      
                    , 10,15)
                            ;
                    }
            }
    
      
    }
    
    
    private void drawLOS(Graphics g){
    
        if (mapGui.isUnitSelected()){
        
        Unit selectedUnit = game.getSelectedUnit();
        
            if(selectedUnit.isShowingLOS()) 
                
                
                
                for (Position losPositons: game.getLOS(selectedUnit, 2)  )
                {
                    drawArrow(g,
                    (windowMode == CreateRoomWindow.AS_HOST) ? 
                            selectedUnit.getPosition().getMouseX() +  MapGUI.PIECE_WIDTH / 2 
                            :
                            selectedUnit.getPosition().transpoze().getMouseX() +  MapGUI.PIECE_WIDTH / 2
                            ,
                    (windowMode == CreateRoomWindow.AS_HOST)
                            ?
                            selectedUnit.getPosition().getMouseY() +  MapGUI.PIECE_HEIGHT / 2
                            :        
                            selectedUnit.getPosition().transpoze().getMouseY() +  MapGUI.PIECE_HEIGHT / 2        
                                    ,                    
                    (windowMode == CreateRoomWindow.AS_HOST) ?
                            losPositons.getMouseX() + MapGUI.PIECE_WIDTH / 2
                            :
                            losPositons.transpoze().getMouseX() + MapGUI.PIECE_WIDTH / 2        
                            ,
                    (windowMode == CreateRoomWindow.AS_HOST) ?
                            losPositons.getMouseY() +  MapGUI.PIECE_WIDTH / 2
                            :
                            losPositons.transpoze().getMouseY() +  MapGUI.PIECE_WIDTH / 2      
                    , 10,15)
                            ;
                    }
            }
    }
    
    private void generateUnitsUI() {
        for (Unit unit : currentPlayer.getArmy()) {
            currentPlayerArmy.add(new UnitGUI(unit));
        }
        
        for (Unit unit : game.getOpponentPlayer().getArmy()) {
            opponnetPlayerArmy.add(new UnitGUI(unit));
        }
    }

   void unselectAllUnits() {
    for (Unit unit: currentPlayer.getArmy()){
             unit.setSelected(false);
             mapGui.setUnitSelected(false);
             mapGui.getMap().unselectAllTerrains();
       }
       
    }
    
    public Game getGame() {
        return game;
    }

    public MapGUI getMapGui() {
        return mapGui;
    }
    
    Image getFlagIcon(Player player){
        Image flag;
         try {
            switch (player.getNation()) {
            case Card.BR : {
                flag = ImageIO.read( new File("resources\\icons\\BRicon.jpg" ));
                return flag;
                }
            case Card.AU : {
                flag = ImageIO.read( new File("resources\\icons\\AUicon.jpg" ));
                return flag;
                }
            case Card.FR : {
                flag = ImageIO.read( new File("resources\\icons\\FRicon.jpg" ));
                return flag;
                }
            case Card.OT : {
                flag = ImageIO.read( new File("resources\\icons\\OTicon.jpg" ));
                return flag;
                }
            case Card.PR : {
                flag = ImageIO.read( new File("resources\\icons\\PRicon.jpg" ));
                return flag;
                }
            case Card.RU : {
                flag = ImageIO.read( new File("resources\\icons\\RUicon.jpg" ));
                return flag;
                }
            case Card.SP : {
                flag = ImageIO.read( new File("resources\\icons\\SPicon.jpg" ));
                return flag;
                }
            case Card.US : {
                flag = ImageIO.read( new File("resources\\icons\\USicon.jpg" ));
                return flag;
                }
            
            default: return null;
            }
          } catch (IOException ex) {
                Logger.getLogger(GameGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
         return null;
    }
    
    
 //-------- CARDS - BOTTOM OF THE SCREEN -----------------------------------
    public void phaseChanged(){
         selectionSeq.clear();//clear selection if phase was changed
             for (int i=0; i<handSetGui.cardsLeftInSet(); i++){  
              handSetGui.getCardByPosInSet(i).setSelected(0);   
             }
    }
    public void keepOneSelectedCard(int positionInSet){
        for (int i=0; i<handSetGui.cardsLeftInSet(); i++){ 
            handSetGui.getCardByPosInSet(i).setSelected(0);
        }
        handSetGui.getCardByPosInSet(positionInSet).setSelected(1);
    }
    public void mouseClickedCard(CardCommandFactory cardEngine){
        for (int i=0; i<handSetGui.cardsLeftInSet(); i++){     
            if(handSetGui.getCardByPosInSet(i).isOverCard()==1){
                if(currentPlayer.getHand().getCardByPosInSet(i).isPlayableInPhase()){ //select card if it is playable
                    if(handSetGui.getCardByPosInSet(i).isSelected()==0) {
                        handSetGui.getCardByPosInSet(i).setSelected(1);
                        if(game.getPhase()==Game.MOVE||game.getPhase()==Game.COMBAT)//in this phase it is possible to select ONE card, thats why all have to be unselected before click
                        {
                            keepOneSelectedCard(i);
                        }
                        selectionSeq.add(handSetGui.getCardIDByPosInSet(i)); 
                        if(game.getPhase()!=Game.DISCARD)currentPlayer.setPlayingCard(true);  //not playing cards on Table during Discard
                    }   
                    else {
                        handSetGui.getCardByPosInSet(i).setSelected(0);
                        Integer j=handSetGui.getCardIDByPosInSet(i);
                        selectionSeq.remove(j); //remove number Integer j, not position int i
                    }  
                }
            }
        }
        if(selectionSeq.size()!=0){
            
            if(game.getPhase() != Game.SETUP)
            {
            /*
            FIX it
            set actual creference to card from set instead create new objest
            */
            Card playingCard = new Card(
                    selectionSeq.get(selectionSeq.size()-1)
                    );
            
            if(playingCard.canBePlayed(game))
            {
            /*
            If card have only 1 attacking mode set it here to avoid custom dialog
            If card have 2 attacking mode then later we'll ask user about which mode he choses
            */
                if(playingCard.getCardType() == Card.UNIT)
                {
                    if(playingCard.getPlayingPossibleCardModes().size() == 1 )
                        playingCard.setPlayingCardMode(playingCard.getPlayingPossibleCardModes().get(0));
                }
            }
        cardEngine.setPlayingCard(playingCard); //set this card to be played -> here will always come last selected card
        
            }
        
        }
        else {
            cardEngine.resetPlayingCard();//reset if no selected cards
            currentPlayer.setPlayingCard(false);
        } 
    }
    public void mouseMovedOverHand(int mouseCoorX, int mouseCoorY){
        float f=0.5f; //scale factor //Normally cards has 260x375 pixels
        int width=round(260*f), height=round(375*f);
        int cardPaddingTop=20;
        int cardPaddingLeft=20;
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
            
                
                    
        }
    }
    public void paintHand(Graphics g)                 
    {   
    float f=0.50f; //scale factor //Normally cards has 260x375 pixels
    int width=round(260*f), height=round(375*f);
    int cardPaddingTop=40;
    int cardPaddingLeft=10;
    int cardPaddingTopTemp=cardPaddingTop;
    int gap = 5;    

        Integer j=0;
        if(!selectionSeq.isEmpty()){
            j=selectionSeq.get(selectionSeq.size()-1);              
            j=handSetGui.getPositionInSetByCardID(j); 
            int[] xPoints={cardPaddingLeft+35+width*j+(gap*j),cardPaddingLeft+95+width*j+(gap*j),cardPaddingLeft+35+(95-35)/2+width*j+(gap*j)};
            int[] yPoints={cardPaddingTop+180,cardPaddingTop+180,cardPaddingTop+170};
            g.setColor(Color.white);
            g.setFont(new Font("Bookman Old Style", 1, 11));
            g.drawString("This card will be visible",cardPaddingLeft+width*j+(gap*j)-10,41+190);
            g.drawString("on the Discard Pile",cardPaddingLeft+width*j+(gap*j)+0,54+190);  
            g.fillPolygon(xPoints, yPoints, 3);
        }  
        for (int i=0; i<handSetGui.cardsLeftInSet(); i++) {   
            if((handSetGui.getCardByPosInSet(i).isOverCard()==1 || handSetGui.getCardByPosInSet(i).isSelected()==1)&&currentPlayer.getHand().getCardByPosInSet(i).isPlayableInPhase()) cardPaddingTopTemp=cardPaddingTop-20;
            else cardPaddingTopTemp=cardPaddingTop;
            g.drawImage(handSetGui.getCardByPosInSet(i).getImgFull(), cardPaddingLeft+(width+gap)*i, cardPaddingTopTemp, width, height, null);  
        }
    }
    public void resetAllCardSets(){
        handSetGui.reSet(); //reset GUI
        discardSetGui.reSet(); //reset GUI
        drawSetGui.reSet(); //reset GUI
        tableSetGui.reSet();
        
    }
    
    public int getNumberOfDiscardedCards(){
    return currentPlayer.getHand().getCardSetSize()-currentPlayer.getHand().cardsLeftInSet();
    }
    
    public void playSelectedCard(){
         for (int i=0; i<selectionSeq.size(); i++){   
            currentPlayer.getHand().dealCardToOtherSetByCardID(selectionSeq.get(i),  currentPlayer.getTablePile());
            }
            selectionSeq.clear();
            resetAllCardSets();
    
    }
    

    public boolean getSelectionSeqIsEmpty() {
        return selectionSeq.isEmpty();
    }
    
    private BufferedImage cropImage(Image img, int x, int y, int width, int height){
        BufferedImage buffImage = (BufferedImage)img;
        return buffImage.getSubimage(x, y, width, height);
    }
            
    public void paintDiscard(Graphics g, boolean paintOpponent){
        CardGUI cardGui;
        float f=0.5f; //scale factor //Normally cards has 260x375 pixels
        int x=35,y=45,w=195,h=140; //cropp image
        int width=round(w*f), height=round(h*f);
        int cardPaddingTop=20;
        int cardPaddingLeft=8;
        if (paintOpponent==true){
            if(game.getOpponentPlayer().getDiscardPile().cardsLeftInSet()>0){
                cardGui=new CardGUI(game.getOpponentPlayer().getDiscardPile().lastCardFromThisSet(false));
                Image image = cropImage(cardGui.getImgFull(),x,y,w,h);
                 g.drawImage(image, cardPaddingLeft, cardPaddingTop, width, height, null);  
            }
            else{
                g.setColor(Color.white);
                g.setFont(new Font("Bookman Old Style", 1, 20));
                g.drawString("No Card",20,100);  
            }   
        }
        else{
            if(discardSetGui.cardsLeftInSet()>0){
                Image image = cropImage(discardSetGui.getCardByPosInSet(discardSetGui.cardsLeftInSet()-1).getImgFull(),x,y,w,h);
                g.drawImage(image, cardPaddingLeft, cardPaddingTop, width, height, null);  
            }
            else{
                g.setColor(Color.white);
                g.setFont(new Font("Bookman Old Style", 1, 20));
                g.drawString("No Card",20,100);  
            }
        }
    }
    
    public void paintDrawLeft(Graphics g, boolean paintOpponent){
        int cardPaddingTop=55;
        int cardPaddingLeft=5;
        Integer drawLeft;
        if (paintOpponent==true)
            drawLeft=game.getOpponentPlayer().getDrawPile().cardsLeftInSet();
        else 
            drawLeft=drawSetGui.cardsLeftInSet();

        g.setColor(Color.white);
        g.setFont(new Font("Bookman Old Style", 1, 50));        
        g.drawString(drawLeft.toString(),cardPaddingLeft,cardPaddingTop); 
    }
    
   private static void drawStringMultiLine(Graphics g, String text, int lineWidth, int x, int y) {
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
        
        if(tableSetGui.cardsLeftInSet()==0){  //paint NO CARD
            g.setColor(Color.white);
            g.setFont(new Font("Bookman Old Style", 1, 20));
            g.drawString("No Card",20,100);  
        }
        
       
       
        for (int i=0; i<tableSetGui.cardsLeftInSet(); i++){  
           
            g.drawImage(tableSetGui.getCardByPosInSet(i).getImgFull(), cardPaddingLeft+(width+gap)*i, cardPaddingTop, width, height, null);

           
                 
             
             
             
             
             
            /* if(tableSetGui.getCardByPosInSet(i).card.getCardType()==0){ //if UNIT card selected
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
            }*/
            
            
            
        }
         Dice d6 = new Dice(Dice.D6);
        d6.generateResult();
        DiceGUI d6gui = new DiceGUI(d6);
        
        g.drawImage(d6gui.getImage(), 400, 30, null);
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
    public UnitGUI getUnitGuiThatHasMoved(){
    
           for(UnitGUI unitSearch: getUnitsGui()){
        
            if(unitSearch.getUnit().hasMoved())
            {
                return unitSearch;
              }
            
        
        }
              
        return null;
    
    }

    public BufferedImage getInfoImage() {
        return infoImage;
    }

    public void setInfoImage(BufferedImage infoImage) {
        this.infoImage = infoImage;
    }
    
    
    
//    ArrayList<Position> movePositions=null;
//    public void drawPossibleMovements(){
//        Unit selectedUnit = game.getSelectedUnit();
//         movePositions=null;
//                if(game.getPhase() == Game.SETUP)
//                {
//                    movePositions = game.getSetupPossibleMovement();
//                }
//                else if (currentPlayer.isPlayingCard()) //if playin' a card
//                {
//                    switch (currentPlayer.getCardCommandFactory().getPlayingCard().getHQType()){
//                        case Card.FORCED_MARCH:
//                        movePositions = game.getOneSquareMovements(selectedUnit.getPosition()); //Forced amarch
//                        break;
//                    }   
//                }
//                else{
//                     movePositions = game.getPossibleMovement(selectedUnit);
//                }
//    }
//    public void paintUnitSelection(int mouseX, int mouseY, CommandQueue cmdQueue){
//       
//    int x=mouseX;
//    int y=mouseY;
//    if(game.getPhase()==Game.MOVE || game.getPhase() == Game.SETUP )//player must be in correct phase to be able to move units    
//    { 
//    if(checkIfPossibleToSelectUnit()){  //if it is legal to select new unit event select automatically if neccesery
//        if(!mapGui.isUnitSelected())
//        {
//            for(TerrainGUI terrainGUI: mapGui.getTerrainsGUI())
//            {
//            terrainGUI.setSelected(false);
//            if(windowMode == CreateRoomWindow.AS_HOST)
//                {   
//                  if(terrainGUI.getPos().checkIfMouseFitInPositon(x, y))
//                        {
//                        terrainGUI.setSelected(true);
//                        Position selectedPosition = terrainGUI.getPos();
//                        System.out.println("manouvre.gui.GameWindow.mainMapPanelMouseClicked() " + terrainGUI.getPos());
//                        if(game.checkCurrentPlayerUnitAtPosition(selectedPosition) ) {
//                            mapGui.setUnitSelected(true);
//                            getUnitGuiOnMapGui(selectedPosition).getUnit().setSelected(true);
//                            }
//                        }
//                }
//            else if(windowMode == CreateRoomWindow.AS_GUEST)
//            {   
//                if(terrainGUI.getPos().transpoze().checkIfMouseFitInPositon(x, y))
//                {
//                    terrainGUI.setSelected(true);
//                    Position selectedPosition = terrainGUI.getPos();
//                    System.out.println("manouvre.gui.GameWindow.mainMapPanelMouseClicked() " + terrainGUI.getPos());
//                    if(game.checkCurrentPlayerUnitAtPosition(selectedPosition) ) {
//                        mapGui.setUnitSelected(true);
//                        getUnitGuiOnMapGui(selectedPosition).getUnit().setSelected(true);
//
//                    }
//                }    
//
//            }   
//            }
//        }  
//
//    /*
//    If unit is selected find which unit to move and move into
//    */
//        else if (mapGui.isUnitSelected())  {
//            Unit selectedUnit = game.getSelectedUnit();
//            Position clickedPosition = new Position(Position.convertMouseXToX(x), Position.convertMouseYToY(y));
//            if(windowMode == CreateRoomWindow.AS_GUEST)
//                    clickedPosition = clickedPosition.transpoze();
//            if(!selectedUnit.getPosition().equals(clickedPosition))
//            {
//                System.out.println("manouvre.gui.ClientUI.mainMapPanelMouseClicked().clickedPosition :" + clickedPosition) ;
//                drawPossibleMovements();
//
//
//                for(Position checkPosition: movePositions){
//
//                    if(checkPosition.equals(clickedPosition))
//                    {
//
//                        MoveUnitCommand moveUnit = new MoveUnitCommand(currentPlayer.getName() , selectedUnit,  clickedPosition);
//
//                        if(!currentPlayer.isPlayingCard() && (game.getPhase() != Game.SETUP) )
//                        {        
//                                cmdQueue.storeAndExecuteAndSend(moveUnit);
//                        }
//
//                        /*
//                        Regular move has to be send but not the move from HQ or else cards
//                        */
//                        else if(currentPlayer.isPlayingCard())
//                        {   /*
//                            We attach move command to wrap it to postpone execution in card command
//                            */
//                            currentPlayer.getCardCommandFactory().setAttachedCommand(moveUnit);
//                            /*
//                            Confirmation dialog
//                            */
//                            /*CustomDialog dialog = new CustomDialog(CustomDialog.YES_NO_UNDO_TYPE, "Are You sure to play that card? " , client, game);
//
//                            try {
//                                dialog.setOkCommand(currentPlayer.getCardCommandFactory().createCardCommand());
//                                dialog.setCancelCommand(moveUnit);
//                            } catch (Exception ex) {
//                                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
//                            }
//                            dialog.setVisible(true);
//                            */
//                            currentPlayer.setPlayingCard(false);
//                        }
//
//                        else 
//                        {   
//                            /*
//                            Just execute on socketClient
//                            */
//                            cmdQueue.storeAndExecute(moveUnit);
//
//                        }
//                        //Unselect all
//                        unselectAllUnits();
//                        //exit loop
//                        break;
//                    }
//                }
//
//            }
//            /*
//            Clicking on the same unit - deselects it.
//            */
//            else
//            {
//                unselectAllUnits();
//            }
//
//            // game.moveUnit(  , newPosition);
//
//        }
//    }
//    }
//    }
//    
//    public boolean checkIfPossibleToSelectUnit(){ //if it is legal to select new unit event select automatically if neccesery
//        if (currentPlayer.hasMoved()){//if player has already moved
//            if(currentPlayer.isPlayingCard()){//but playin' a card
//                switch (currentPlayer.getCardCommandFactory().getPlayingCard().getHQType()){
//                    case Card.FORCED_MARCH:{//if force march select last moved unit automaticaly
//                            getUnitGuiThatHasMoved().getUnit().setSelected(true);
//                            mapGui.setUnitSelected(true);
//                            return true;
//                        }
//                    default: return false;
//                }
//                
//            }
//            return false;
//        }
//        return true;
//    }
    
    
    public boolean isLocked() {
    return lockGUI;
    }

    public void lockGUI() {
    this.lockGUI = true;
    }
    public void unlockGUI(){
    this.lockGUI = false;
    }
            
            
    
    public ArrayList<UnitGUI> getUnitsGui() {
        return currentPlayerArmy;
    }

    public void setUnitsGui(ArrayList<UnitGUI> unitsGui) {
        this.currentPlayerArmy = unitsGui;
    }
    public CardSetGUI getHandSetGui() {
        return handSetGui;
    }

    public Message drawCards() {
        //draw a card from a pile
        //execute externally
        this.numberOfDiscardedCards = this.getNumberOfDiscardedCards();
        DrawCardCommand drawCard = new DrawCardCommand(this.numberOfDiscardedCards, this.currentPlayer.getName());
        Message drawCardMessage = new Message(Message.COMMAND, this.currentPlayer.getName(), Message.DRAW_CARD_COMMAND, "IN_CHANNEL");
        drawCardMessage.setCommand(drawCard);
        //execute locally
        drawCard.execute(this.game);
        this.resetAllCardSets();
        return drawCardMessage;
    }

    public Message discardSelCards() {
        //done on hand itseld not on HandGui
        ArrayList<Integer> selectionSeqTemp = new ArrayList<Integer>();
        selectionSeqTemp.clear();
        for (Integer i : this.selectionSeq) {
            //make a copy to loose referance
            selectionSeqTemp.add(i);
        }
        //execute externally
        DiscardCardCommand discardCard = new DiscardCardCommand(selectionSeqTemp, this.currentPlayer.getName());
        Message discardCardMessage = new Message(Message.COMMAND, this.currentPlayer.getName(), Message.DISCARD_CARD_COMMAND, "IN_CHANNEL");
        discardCardMessage.setCommand(discardCard);
        //execute locally
        discardCard.execute(this.game);
        //        for (int i=0; i<selectionSeq.size(); i++){
        //            currentPlayer.getHand().dealCardToOtherSetByCardID(selectionSeq.get(i),  currentPlayer.getDiscardPile());
        //           }
        this.selectionSeq.clear();
        this.handSetGui.reSet(); //reset GUI
        this.discardSetGui.reSet(); //reset GUI
        this.drawSetGui.reSet(); //reset GUI
        return discardCardMessage;
    }
    
     
}
