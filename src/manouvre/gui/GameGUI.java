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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import manouvre.game.Dice;
import javax.swing.JOptionPane;
import manouvre.game.CardSet;
import manouvre.game.Combat;
import static java.lang.Math.round;
import org.apache.logging.log4j.LogManager;



/**
 *
 * @author Bartosz
 */
public class GameGUI {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(GameGUI.class.getName());
    
    Game game;
    ArrayList<UnitGUI> currentPlayerArmy = new ArrayList<UnitGUI>(); 
    ArrayList<UnitGUI> opponnetPlayerArmy = new ArrayList<UnitGUI>(); 
    MapGUI mapGui;
    CardSetGUI handSetGui;

    Player currentPlayer;
    CardSetGUI discardSetGui;
    CardSetGUI drawSetGui;
    CardSetGUI tableSetGui,tableSetGuiDefPart;
    
    int stateTemp=Combat.INITIALIZING_COMBAT;
    BufferedImage  infoImage;
    CardCommandFactory cardFactory;
    /*
    Wielkosc ramki stolu w kwadracie w pikselach
    */
    final int BACKGRNDTABLE = 678;
    int numberOfDiscardedCards=0;
    
    final int gapSelection = 5;
    final int gapUnit = 7;
    
    
    int windowMode;
    
    boolean lockGUI=false;
    
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
        this.tableSetGui = new CardSetGUI(game.getTablePile());//empty
        this.tableSetGuiDefPart = new CardSetGUI(game.getTablePileDefPart());//empty
        this.cardFactory = game.getCardCommandFactory();
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
            
            g.drawImage(infoImage, 0, 0,infoImage.getWidth(),infoImage.getHeight(), null);
        
               
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
        drawRetrieving(g);
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
   
    if(!game.getCurrentPlayer().isPlayingCard())
     {
         Unit selectedUnit = game.getSelectedUnit();
            if (selectedUnit != null ) 
                {
                ArrayList<Position> movePositions;
                if(game.getPhase() == Game.SETUP || game.freeMove)
                {
                    movePositions = game.getSetupPossibleMovement();
                    drawMultipleRectanglesOnPositions(g, movePositions, Color.blue);
                }
                else if (selectedUnit.isRetriving())
                {
                    return;
                }
                else
                {
                    movePositions = game.getPossibleMovement(selectedUnit);
                    drawMultipleRectanglesOnPositions(g, movePositions, Color.blue);
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
                if(!drawUnit.getUnit().isEliminated())
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
                if(!drawUnit.getUnit().isEliminated())
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
                    if(!drawUnit.getUnit().isEliminated())
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
                if(!drawUnit.getUnit().isEliminated())
                    drawImageOnPosition(g, drawUnit.getUnit().getPosition(), drawUnit.getImg());
                
            }
            for (UnitGUI drawUnit : opponnetPlayerArmy) {
                if(!drawUnit.getUnit().isEliminated())
                     drawImageOnPosition(g, drawUnit.getUnit().getPosition(), drawUnit.getImg());
                }
            }

    }
    private void drawCardSelections(Graphics g){
    
        if(currentPlayer.isPlayingCard())
        {
            ArrayList<Position> movePositions;
            Card playingCard = cardFactory.getCurrentPlayedCard();
        
            if(playingCard != null)
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
                    case Card.SUPPLY: 
                        {
                        if(game.getSelectedUnit()!= null)  {  
                                Unit selectedUnit = game.getSelectedUnit();
                                movePositions = game.getOneSquareMovements(selectedUnit.getPosition());
                                drawMultipleRectanglesOnPositions(g, movePositions, Color.red);
                        }
                        break;
                        }    
                        
                    case Card.WITHDRAW: 
                        {
                        Unit attackedUnit = game.getCardCommandFactory().getAttackedUnit();
                        movePositions = game.getRetreatPositions(attackedUnit);
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

                    if(game.getPhase() == Game.COMBAT)
                        /*
                        Draw possible targets if we know playing Card Mode
                        if combat is null that means that is not initialized

                        */       
                        if(game.getCombat() == null )
                           
                            {
                                /*
                                If we have chosen attack type
                                */
                            if(playingCard.getPlayingCardMode() > 0  )
                                /*
                                If we are in Combat.INITIALIZING_COMBAT phase
                                */
                                if(game.getCombat().getState() == Combat.INITIALIZING_COMBAT)
                                    {
                                    if(!cardFactory.getAttackingPositions().isEmpty())
                                        drawArrowToPositions(g, 
                                        attackingUnit.getPosition(),
                                        cardFactory.getAttackingPositions(),
                                        Color.RED
                                                            );

                                    }
                            }
                    }    
                    
                break;    
                }  
                case Card.HQLEADER :{
                /*
                    If we have leader selected draw nothing
                    */
                break;
                }
                default: System.err.println("drawCardSelections()  Brak typu karty " + playingCard.getCardName());
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
                    position.getMouseX() + gapSelection: 
                    position.transpoze().getMouseX()
            + gapSelection,
            (windowMode == CreateRoomWindow.AS_HOST) ?
                    position.getMouseY() + gapSelection: 
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
    
    private  void drawArrowToPositions(Graphics g , Position fromPosition, ArrayList<Position> toPositions, Color color){
    
        g.setColor(color);
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
     
    if (game.getSelectedUnit()!= null){
        
        Unit selectedUnit = game.getSelectedUnit();
        if(selectedUnit != null)
            if(selectedUnit.isRetriving()) 
                     
                drawArrowToPositions(g,  selectedUnit.getPosition(), game.getRetreatPositions(selectedUnit), Color.GREEN);
                       
            }
    
      
    }
    
    
    private void drawLOS(Graphics g){
    
        if (game.getSelectedUnit()!= null){
        
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
        game.unselectAllUnits();
       
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
    //scale factor for Cards//Normally cards has 260x375 pixels
    int width=round(CardGUI.CARD_WIDTH*CardGUI.SCALE_FACTOR), height=round(CardGUI.CARD_HEIGHT*CardGUI.SCALE_FACTOR);
    int cardPaddingTop=40;
    int cardPaddingLeft=10;
    int gap = 5; 
    int oneThirdCard; //set during mouse move
    int liftCardIfSelectedBy=20;//pixels if card selected
    
    public void phaseChanged(){
         game.getCurrentPlayer().getHand().selectionSeq.clear();//clear selection if phase was changed
             for (int i=0; i<handSetGui.cardsLeftInSet(); i++){  
              handSetGui.getCardByPosInSet(i).getCard().setSelected(false);   
             }
    }
    public void keepOneSelectedCard(Card cardClicked){
        for (int i=0; i<handSetGui.getCardSet().cardsLeftInSet(); i++){ 
            handSetGui.getCardSet().getCardByPosInSet(i).setSelected(false);
        }
        game.getCurrentPlayer().getHand().selectionSeq.clear();
        if(handSetGui.getCardSet().getCardByCard(cardClicked)!=null){
        handSetGui.getCardSet().getCardByCard(cardClicked).setSelected(true);
        game.getCurrentPlayer().getHand().selectionSeq.add((Integer)cardClicked.getCardID());
        }
        else System.err.println("CARD IS NOT SELECTED - check GameGui.java method: keepOneSelectedCard");
    }
    public void mouseClickedCard(Card cardClicked){
        
        LOGGER.debug(game.getCurrentPlayer().getName() + " mouseClickedCard " + cardClicked.toString());
        if(cardClicked!=null)
        {
           //select card if it is    playable 
            if(cardClicked.getAvailableForPhase(game))
            { 
                //if not selected
                if(!cardClicked.isSelected()) 
                {
                    cardClicked.setSelected(true);

                    /*
                    If we are in move phase or Combat phase  but not during combat
                    */
                    if(game.getPhase()==Game.MOVE)

                        /* this phase it is possible 
                        to select ONE card, thats why all have to be unselected before click
                        */
                        {
                        game.getCardCommandFactory().setPlayingCard(cardClicked);
                        triggerCardActionOnSelection(cardClicked);
                        keepOneSelectedCard(cardClicked);
                        }
                     
                    else if(game.getPhase()==Game.COMBAT && (game.getCombat()==null)){ //select first card in combat
                        game.getCardCommandFactory().setPlayingCard(cardClicked);
                        game.getCardCommandFactory().addPickedAttackingCard(cardClicked);
                        triggerCardActionOnSelection(cardClicked);
                        keepOneSelectedCard(cardClicked);  
                        
                     }
                    /*
                    If we in combat phase during combat
                    */
                    else if (game.getPhase()==Game.COMBAT &&(game.getCombat()!= null))
                            {
                                if(game.getCombat().getState()!=stateTemp){  //reset selection if state was changing
                                game.getCurrentPlayer().getHand().selectionSeq.clear();
                                stateTemp=game.getCombat().getState();
                                }  
                            switch(game.getCombat().getState())
                                {
                                    case Combat.PICK_SUPPORTING_CARDS:
                                    {   
                                        if(game.getCurrentPlayer().isActive()){
                                            game.getCardCommandFactory().addPickedAttackingCard(cardClicked);
                                            game.getCurrentPlayer().getHand().selectionSeq.add((Integer)cardClicked.getCardID()); 
                                            break;
                                        }
                                    }
                                    case Combat.PICK_DEFENSE_CARDS:
                                    {
                                        if(!game.getCurrentPlayer().isActive()){
                                            game.getCardCommandFactory().addPickedDefendingCard(cardClicked);
                                            game.getCurrentPlayer().getHand().selectionSeq.add((Integer)cardClicked.getCardID()); 
                                            break;
                                        }
                                    }
                               
                                }
                            }    
                    /*
                    We are in other phases than MOVE and COMBAT
                    */
                    else if(game.getPhase()==Game.RESTORATION){
                        game.getCardCommandFactory().setPlayingCard(cardClicked);
                        triggerCardActionOnSelection(cardClicked);
                        keepOneSelectedCard(cardClicked);
                    }
                    else game.getCurrentPlayer().getHand().selectionSeq.add((Integer)cardClicked.getCardID());

                    /*
                Set playingCard always but Discard phase
                */
                    if(game.getPhase()!=Game.DISCARD)
                            currentPlayer.setPlayingCard(true);  //not playing cards on Table during Discard
                } 

            /*
            Unselect card that is is playable for this phase
            */
            else 
            {
                triggerCardActionOnDeSelection(cardClicked);
                cardClicked.setSelected(false);
                Integer j=cardClicked.getCardID();
                game.getCurrentPlayer().getHand().selectionSeq.remove(j); //remove number Integer j, not position int i
                if(game.getPhase()==Game.COMBAT && (game.getCombat()==null)){
                    game.getCardCommandFactory().removePickedAttackingCard(cardClicked);
                }
                if (game.getPhase()==Game.COMBAT &&(game.getCombat()!= null))
                {
                    switch(game.getCombat().getState())
                    {
                     case Combat.PICK_DEFENSE_CARDS:
                        {
                        game.getCardCommandFactory().removePickedDefendingCard(cardClicked);
                        break;
                        }
                        case Combat.PICK_SUPPORT_UNIT:
                        {
                        game.getCardCommandFactory().removePickedAttackingCard(cardClicked);
                        break;
                        }
                    }
                }
            }
        }
        else
            JOptionPane.showMessageDialog(null, "This card is not available in this phase", 
                 "Wrong Action", JOptionPane.OK_OPTION); 
        }
        
    }
    

    
    private void triggerCardActionOnSelection(Card playingCard){
    
         
         if(playingCard.canBePlayed(game))
            {
            /*
            Trigger action on selection
            */
            LOGGER.debug(game.getCurrentPlayer().getName() + " triggerCardActionOnSelection " + playingCard.toString());
 
            switch(playingCard.getCardType()){
            
                case Card.UNIT :
                {
                 /*
                If card have only 1 attacking mode set it here to avoid custom dialog
                If card have 2 attacking mode then later we'll ask user about which mode he choses
                */
                if(playingCard.getCardType() == Card.UNIT)
                {
                    if(playingCard.getPlayingPossibleCardModes().size() == 1 )
                    {
                        playingCard.setPlayingCardMode(playingCard.getPlayingPossibleCardModes().get(0));
                        playingCard.actionOnSelection(game);
                    }
                    else 
                    {
                       
                        /*
                        TODO Create dialog to choose 
                        */
                    }
                }
                break;
                }
                default:  playingCard.actionOnSelection(game);
            }
            }
    
    }
    
    private void triggerCardActionOnDeSelection(Card playingCard){
            LOGGER.debug(game.getCurrentPlayer().getName() + " triggerCardActionOnDeSelection " + playingCard.toString());
            /*
            Trigger action on selection
            */
            playingCard.actionOnDeselection(game);
            
            LOGGER.debug(game.getCurrentPlayer().getName() + "game.getCardCommandFactory().resetFactory()");
            game.getCardCommandFactory().resetFactory();
    
    }
    
    public Card getCardFromMousePosition(int mouseCoorX, int mouseCoorY){
        setInfoImage(null);
        for (int i=0; i<handSetGui.cardsLeftInSet(); i++){
            int mouseYmin = cardPaddingTop;
            if(handSetGui.getCardByPosInSet(i).getCard().isMouseOverCard()||handSetGui.getCardByPosInSet(i).getCard().isSelected())
                mouseYmin=mouseYmin-liftCardIfSelectedBy;
            int mouseYmax = cardPaddingTop+height;
            int mouseXmin = cardPaddingLeft+(gap*i)+width*(i);
            int mouseXmax = cardPaddingLeft+(gap*i)+width*(i+1);
            oneThirdCard = (mouseYmin+(mouseYmax-mouseYmin))/3;
            
            if(mouseCoorY>mouseYmin && mouseCoorY<mouseYmax){ // if mouse is in row with cards
                if (mouseCoorX>mouseXmin && mouseCoorX<mouseXmax){ //if mouse is in th collon with card
                     if(mouseCoorY>oneThirdCard*2){//bottom card selection
                        int x=0,y=375/2,w=260,h=375/2; //cropp image
                        Image image = cropImage(handSetGui.getCardByPosInSet(i).getImgFull(),x,y,w,h);
                        setInfoImage((BufferedImage)image);
                    }
                    if(oneThirdCard<mouseCoorY && mouseCoorY<=oneThirdCard*2){//middle card selection
                        int x=0,y=375/4,w=260,h=375/2; //cropp image
                        Image image = cropImage(handSetGui.getCardByPosInSet(i).getImgFull(),x,y,w,h);
                        setInfoImage((BufferedImage)image);
                    }
                    if(mouseCoorY<=oneThirdCard){//top card selection
                        int x=0,y=0,w=260,h=375/2; //cropp image
                        Image image = cropImage(handSetGui.getCardByPosInSet(i).getImgFull(),x,y,w,h);
                        setInfoImage((BufferedImage)image);
                    }
                    return handSetGui.getCardByPosInSet(i).getCard();
                }
            }
        }
        return (Card)null;
    }

    
    public void setMouseOverCard(int mouseX, int mouseY){
        for (int i=0; i<handSetGui.cardsLeftInSet(); i++){
            handSetGui.getCardByPosInSet(i).getCard().setMouseOverCard(false);//delete all selections first
        }
        Card cardOverMouse=getCardFromMousePosition(mouseX,mouseY);
        if(cardOverMouse!=null){
            cardOverMouse.setMouseOverCard(true);
        }
        else{
           // System.err.println("card null");
        }
    }
    
    public void paintHand(Graphics g)                 
    {   
        CardSet hand=currentPlayer.getHand();
        int cardPaddingTopTemp=cardPaddingTop;
        Integer j=0;
        if(!hand.selectionSeq.isEmpty()){
            j=hand.selectionSeq.get(hand.selectionSeq.size()-1);              
            j=handSetGui.getPositionInSetByCardID(j); 
            int[] xPoints={cardPaddingLeft+35+width*j+(gap*j),cardPaddingLeft+95+width*j+(gap*j),cardPaddingLeft+35+(95-35)/2+width*j+(gap*j)};
            int[] yPoints={cardPaddingTop+180,cardPaddingTop+180,cardPaddingTop+170};
            g.setColor(Color.white);
            g.setFont(new Font("Bookman Old Style", 1, 11));
            if(game.getPhase()==Game.DISCARD){
            g.drawString("This card will be visible",cardPaddingLeft+width*j+(gap*j)-10,41+190);
            g.drawString("on the Discard Pile",cardPaddingLeft+width*j+(gap*j)+0,54+190); 
            }
            g.fillPolygon(xPoints, yPoints, 3);
            if(game.getPhase()==Game.COMBAT&&(
                    (game.getCombat() != null ?
                    ( game.getCombat().getState()==Combat.PICK_DEFENSE_CARDS||game.getCombat().getState()==Combat.PICK_SUPPORTING_CARDS): false))){  //put triangle under all selected in Defence mode
                for(int s=0; s<hand.selectionSeq.size()-1; s++){
                    j=hand.selectionSeq.get(s);              
                    j=handSetGui.getPositionInSetByCardID(j); 
                    int[] xPoints2={cardPaddingLeft+35+width*j+(gap*j),cardPaddingLeft+95+width*j+(gap*j),cardPaddingLeft+35+(95-35)/2+width*j+(gap*j)};
                    int[] yPoints2={cardPaddingTop+180,cardPaddingTop+180,cardPaddingTop+170};
                    g.setColor(Color.white);
                    g.fillPolygon(xPoints2, yPoints2, 3);
                }
            }
            
        }  
        for (int i=0; i<handSetGui.cardsLeftInSet(); i++) {   
            if((handSetGui.getCardByPosInSet(i).getCard().isMouseOverCard()|| handSetGui.getCardByPosInSet(i).getCard().isSelected()) 
                    && (hand.getCardByPosInSet(i).getAvailableForPhase(game)))
                    cardPaddingTopTemp=cardPaddingTop-20;
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
         for (int i=0; i<game.getCurrentPlayer().getHand().selectionSeq.size(); i++){   
            currentPlayer.getHand().dealCardToOtherSetByCardID(game.getCurrentPlayer().getHand().selectionSeq.get(i),  game.getTablePile());
            }
            game.getCurrentPlayer().getHand().selectionSeq.clear();
            resetAllCardSets();
    
    }
    

    public boolean getSelectionSeqIsEmpty() {
        return game.getCurrentPlayer().getHand().selectionSeq.isEmpty();
    }
    
    private BufferedImage cropImage(Image img, int x, int y, int width, int height){
        BufferedImage buffImage = (BufferedImage)img;
        return buffImage.getSubimage(x, y, width, height);
    }
            
    public void paintDiscard(Graphics g, boolean paintOpponent){
        CardGUI cardGui;
        int x=35,y=40,w=195,h=300; //cropp image
        int width=round(w*CardGUI.SCALE_FACTOR), height=round(h*CardGUI.SCALE_FACTOR);
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
        }
        paintDices(g);
        
        paintDefenceCardsOnTheTable(g);
        
       
    }
   
    public void paintDefenceCardsOnTheTable(Graphics g){
        int cropFrame=30;
        double resizeFactor=0.4;
        int cardPaddingLeftDef=80;
        int width=(int)((260-2*cropFrame)*resizeFactor);
        int height=(int)((375-2*cropFrame)*resizeFactor);
        int gapDef=50;
       
        tableSetGuiDefPart.reSet();
            for (int i=0; i<tableSetGuiDefPart.cardsLeftInSet(); i++){  
                Image image = tableSetGuiDefPart.getCardByPosInSet(i).getImgSmall(cropFrame);
                g.drawImage(image, cardPaddingLeftDef+(width-gapDef)*i, cardPaddingTop+gapDef*i, width, height, null);
            }
    }
   
    public void paintCombatPanel(Graphics g){ //paint all the details of the cards and units on the table
      
        
    }
    
    public void paintCalculationCombatPanel(Graphics g) {
        
        final int START_X_COMBAT_PANEL = 50;
        final int START_Y_COMBAT_PANEL = 16;
        final int Y_GAP = 50;
        final int X_GAP = 50;
        
        g.setColor(Color.red);
        
        if(game.getCombat()!=null)
            
        {
            Combat combat =  game.getCombat();
            
            g.drawString("Attack :" + combat.getAttackValue(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL);
            
            g.drawString("Defense :" + combat.getDefenceValue(), START_X_COMBAT_PANEL, 2*START_Y_COMBAT_PANEL);
            
            g.drawString("Defense Terrain:" + combat.getDefenseTerrain().getTypeToString(), START_X_COMBAT_PANEL, 3*START_Y_COMBAT_PANEL);
            
            g.drawString("Terrain Defense Bonus :" + combat.getDefenseBonus(), START_X_COMBAT_PANEL, 4*START_Y_COMBAT_PANEL);
            
            g.drawString("Attacking Terrain:" + combat.getAttackTerrain().getTypeToString(), START_X_COMBAT_PANEL, 5*START_Y_COMBAT_PANEL);
            
            g.drawString("Terrain Attack Bonus :" + combat.getAttackBonus(), START_X_COMBAT_PANEL, 6*START_Y_COMBAT_PANEL);
            
            
            
        }
    }
    
    private void paintDices(Graphics g){
        final int DICE_GAP = 10;
        final int STARTING_D6_X = 480;
        final int STARTING_D6_Y = 30;
        
        final int STARTING_D8_X = STARTING_D6_X;
        final int STARTING_D8_Y = STARTING_D6_Y + DiceGUI.D6SQUARE_HEIGHT ;
        
        final int STARTING_D10_X = STARTING_D6_X;
        final int STARTING_D10_Y = STARTING_D8_Y + DiceGUI.D8SQUARE_HEIGHT ;
        
        
        
        int i=0;
        
        if(!game.getCardCommandFactory().getD6dices().isEmpty())
            for(Dice d6  :  game.getCardCommandFactory().getD6dices() ){
                i++;
                DiceGUI d6gui = new DiceGUI(d6);
                g.drawImage(d6gui.getImage(), 
                        STARTING_D6_X  - (i-1)* (int)(DiceGUI.D6SQUARE_WIDTH*DiceGUI.SCALE_FACTOR_D6), 
                        STARTING_D6_Y ,
                        (int)(d6gui.getImage().getWidth()*DiceGUI.SCALE_FACTOR_D6),
                        (int)(d6gui.getImage().getHeight()*DiceGUI.SCALE_FACTOR_D6)
                        
                        , null);
            } 
        i=0;
        if(!game.getCardCommandFactory().getD8dices().isEmpty())
            for(Dice d8 : game.getCardCommandFactory().getD8dices() ){
                i++;
                
                

                DiceGUI d8gui = new DiceGUI(d8);
                g.drawImage(d8gui.getImage(), STARTING_D8_X -  (i-1)* (int)(DiceGUI.D8SQUARE_WIDTH*DiceGUI.SCALE_FACTOR_D8) , STARTING_D8_Y , 
                        (int)(d8gui.getImage().getWidth()*DiceGUI.SCALE_FACTOR_D8),
                        (int)(d8gui.getImage().getHeight()*DiceGUI.SCALE_FACTOR_D8),
                        
                        null);
            } 
        i=0;
        if(!game.getCardCommandFactory().getD10dices().isEmpty())
            for(Dice d10 : game.getCardCommandFactory().getD10dices() ){
                i++;
                DiceGUI d10gui = new DiceGUI(d10);
                g.drawImage(d10gui.getImage(), STARTING_D10_X - (i-1)*(int)(DiceGUI.D10SQUARE_WIDTH * DiceGUI.SCALE_FACTOR_D10), STARTING_D10_Y ,
                        (int)(d10gui.getImage().getWidth()*DiceGUI.SCALE_FACTOR_D10),
                        (int)(d10gui.getImage().getHeight()*DiceGUI.SCALE_FACTOR_D10),
                        null);
            } 
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
        for (Integer i : game.getCurrentPlayer().getHand().selectionSeq) {
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
        game.getCurrentPlayer().getHand().selectionSeq.clear();
        this.handSetGui.reSet(); //reset GUI
        this.discardSetGui.reSet(); //reset GUI
        this.drawSetGui.reSet(); //reset GUI
        return discardCardMessage;
    }

    public CardSetGUI getTableSetGuiDefPart() {
        return tableSetGuiDefPart;
    }

    public void setTableSetGuiDefPart(CardSetGUI tableSetGuiDefPart) {
        this.tableSetGuiDefPart = tableSetGuiDefPart;
    }
    
     
}
