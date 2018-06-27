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
import manouvre.commands.CommandQueue;
import manouvre.game.Card;
import manouvre.game.CardCommandFactory;
import manouvre.game.Player;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import manouvre.game.Dice;
import manouvre.game.Combat;
import manouvre.game.Terrain;
import org.apache.logging.log4j.LogManager;



/**
 *
 * @author Bartosz
 */
public class GameGUI {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(GameGUI.class.getName());
    
     //-------- CARDS - BOTTOM OF THE SCREEN -----------------------------------
    
    //int width=round(CardGUI.CARD_WIDTH*CardGUI.SCALE_FACTOR), height=round(CardGUI.CARD_HEIGHT*CardGUI.SCALE_FACTOR);
    
    
    
    Game game;
    ArrayList<UnitGUI> currentPlayerArmy = new ArrayList<UnitGUI>(); 
    ArrayList<UnitGUI> opponnetPlayerArmy = new ArrayList<UnitGUI>(); 
    MapGUI mapGui;
    
    Player currentPlayer;
    
    CardSetGUI cardSetsGUI;
    
   
    BufferedImage  infoImage;
    
    Position hoverPosition;
    
    CardCommandFactory cardFactory;
    /*
    Wielkosc ramki stolu w kwadracie w pikselach
    */
    final int BACKGRNDTABLE = 678;
 
    final int gapSelection = 5;
    final int gapUnit = 7;
    
    
    int windowMode;
    
    boolean lockGUI=false;
    
    CommandQueue cmdQueue;
    
    BufferedImage redoubtImage;
    
    public GameGUI (Game newGame, int windowMode, CommandQueue cmdQueue) throws IOException{
        this.game=newGame;
        this.currentPlayer=game.getCurrentPlayer();
        this.windowMode = windowMode;
        this.mapGui = new MapGUI(game.getMap(), windowMode);
        this.generateUnitsUI();
        
        this.cardSetsGUI = new CardSetGUI(game);
        
        this.cardFactory = game.getCardCommandFactory();
        this.cmdQueue=cmdQueue;
               
        /*
        Set info about first / second player
        */
        CustomDialog dialog = new CustomDialog(CustomDialog.CONFIRMATION_TYPE, currentPlayer.getName() + ", You are" + (
        currentPlayer.isFirst() ? " first " : " second ") + "player"  , (CommandQueue) null, game);
        dialog.setVisible(true);
        
         try {
            redoubtImage = ImageIO.read(new File("resources\\icons\\Redbt_mini.png"));
        } catch (IOException ex) {
            LOGGER.error("Error during loading redoubt image " + ex.toString());
        }
        
        
    }
    void paintInfoPanel(Graphics g){
    
        
        final int TERRAIN_X_OFFSET = 460;
        final int TERRAIN_Y_OFFSET = 50;
        
        final int UNIT_X_OFFSET = 300;
        final int UNIT_Y_OFFSET = 50;
       
        final int DECRIPTION_Y_GAP = 12;
        final int DECRIPTION_Y_OFFSET = TERRAIN_Y_OFFSET + 76 ;
        final float TERRAIN_SCALE = 0.7f;
        
        if(getHoverPosition() != null)
        {
        TerrainGUI terrain = mapGui.getTerrainGuiAtPosition(hoverPosition);
        UnitGUI unit =  getUnitGuiOnMapGui(hoverPosition);
        if(unit != null)
            
            g.drawImage(unit.getImg(), UNIT_X_OFFSET, UNIT_Y_OFFSET,
                    unit.getImg().getWidth(),
                    unit.getImg().getHeight(), null);
        
        if(terrain!= null)
        {
            g.drawImage(
                    terrain.getImg(), 
                    TERRAIN_X_OFFSET,
                    TERRAIN_Y_OFFSET,
                    (int) (terrain.getImg().getWidth() * TERRAIN_SCALE),
                    (int) (terrain.getImg().getHeight() * TERRAIN_SCALE),
                    null) ;
        
            g.drawString(terrain.getTerrain().getTypeToString() ,
            TERRAIN_X_OFFSET, DECRIPTION_Y_OFFSET + DECRIPTION_Y_GAP);        
        
            g.drawString("Def Bonus: " + Integer.toString(terrain.getTerrain().getDefenceBonus()) ,
            TERRAIN_X_OFFSET, DECRIPTION_Y_OFFSET + DECRIPTION_Y_GAP*2);
            
            g.drawString("Block LOS: " + (terrain.getTerrain().getBlockingLOS() ? "yes" : "no" ) ,
            TERRAIN_X_OFFSET, DECRIPTION_Y_OFFSET + DECRIPTION_Y_GAP*3);
            
            if(terrain.getTerrain().getType() == Terrain.HILL){
            g.drawString("Att Bon.vs non-Hill: 2" ,
            TERRAIN_X_OFFSET, DECRIPTION_Y_OFFSET + DECRIPTION_Y_GAP*4);
            }
        }  
        }
        
        final int START_X = 10;
        final int START_Y = 37;
        g.setColor(Color.white);
        g.setFont(new Font("Bookman Old Style", 1, 20));
        if(game.getInfoBarText() != null)
            g.drawString(game.getInfoBarText(), START_X, START_Y);
        
        
         final int START_X_COMBAT_PANEL = 10;
        final int START_Y_COMBAT_PANEL =90;
        final int Y_GAP = 16;
                
        g.setColor(Color.white);
        g.setFont(new Font("Bookman Old Style", 1, 15));
        
        if(game.getCombat()!=null)
            
        {
            Combat combat =  game.getCombat();
            
            g.drawString("Attack: " + combat.getAttackValue(), START_X_COMBAT_PANEL,START_Y_COMBAT_PANEL+ Y_GAP);
            
            g.drawString("Defense: " + combat.getDefenceValue(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL + 2*Y_GAP);
            
            g.drawString("Def.Terrain: " + combat.getDefenseTerrain().getTypeToString(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL+ 3*Y_GAP);
            
            g.drawString("Ter.Def.Boznus: " + combat.getDefenseBonus(), START_X_COMBAT_PANEL,START_Y_COMBAT_PANEL+ 4*Y_GAP);
            
            g.drawString("Att.Terrain: " + combat.getAttackTerrain().getTypeToString(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL+  5*Y_GAP);
            
            g.drawString("Ter.Att.Bonus: " + combat.getAttackBonus(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL+ 6*Y_GAP);
            
            g.drawString("Leader Bonus: " + combat.getLeaderBonus(), START_X_COMBAT_PANEL,START_Y_COMBAT_PANEL+  7*Y_GAP);
  
        }
        
        
               
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
        Draw units
         */
        drawArmy(g);
         /*
        Draws selection
         */
        drawPossibleMove(g);
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
        /*
        Draws Attacking arrow in combat
        
        */
        drawCombat(g);
        
        /*
        Draw redoubt
        */

       
    }
      private void drawTerrains(Graphics g){
    
        for (TerrainGUI terrainGUI : mapGui.getTerrainsGUI()) {
                        
            drawTerrainOnPosition(g, terrainGUI.getPos(), terrainGUI.getImg());
            
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
    
    private void drawPossibleMove(Graphics g){
    /*
        Draws selection of possible move whlie not playing card
        */
    Card playingCard = game.getCardCommandFactory().getPlayingCard();
    if(playingCard == null)
     {
         Unit selectedUnit = game.getSelectedUnit();
            if (selectedUnit != null ) 
                {
                drawRectangleOnPosition(g, selectedUnit.getPosition(), Color.WHITE);
                ArrayList<Position> movePositions;
                if(game.getPhase() == Game.SETUP || (game.freeMove && game.getPhase() == Game.MOVE) )
                {
                    movePositions = game.getSetupPossibleMovement();
                    drawMultipleRectanglesOnPositions(g, movePositions, Color.blue);
                }
                else if (selectedUnit.isRetriving())
                {
                    return;
                }
                else if(game.getPhase() == Game.MOVE)
                {
                    if(!selectedUnit.hasMoved())
                    {
                        movePositions = game.getPossibleMovement(selectedUnit);
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
                
                for (UnitGUI drawUnit : currentPlayerArmy) 
                {
                if(!drawUnit.getUnit().isEliminated())
                {

                drawImageOnPosition(g, drawUnit.getUnit().getPosition(), drawUnit.getImg());
                drawTerrainLetter(g, drawUnit.getUnit());
                

                        
                /*
                Draw bad position rectangle
                */
                if( !game.getMap().getTerrainAtPosition(drawUnit.getUnit().getPosition()).isTerrainPassable()
                         || 
                            ( windowMode == CreateRoomWindow.AS_HOST ? 
                                (drawUnit.getUnit().getPosition().getY()  >  Position.ROW_2) : 
                                (drawUnit.getUnit().getPosition().getY()  <  Position.ROW_7)
                            )
                      )
                   {
                    g.setColor(Color.RED);
                    g.drawRoundRect(
                                drawUnit.getUnit().getPosition().getMouseX(windowMode) + gapSelection, 
                                drawUnit.getUnit().getPosition().getMouseY(windowMode) + gapSelection, 
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
                {
                    Unit unit = drawUnit.getUnit();
                    drawImageOnPosition(g, unit.getPosition(), drawUnit.getImg());
                    drawTerrainLetter(g, unit);
                    if(game.getMap().getTerrainAtPosition(unit.getPosition()).isRedoubt())
                        drawRedoubt(g, unit.getPosition());
                    
                    if(game.getCombat()!= null)
                        if(drawUnit.getUnit().isSupporting()  && game.getCombat().getState() == Combat.PICK_SUPPORT_UNIT)
                            drawSupporting(g, drawUnit.getUnit());
                    
                        
                }
            }
            for (UnitGUI drawUnit : opponnetPlayerArmy) {
                if(!drawUnit.getUnit().isEliminated()){
                     Unit unit = drawUnit.getUnit();
                     drawImageOnPosition(g, unit.getPosition(), drawUnit.getImg());
                     drawTerrainLetter(g, unit);
                    if(game.getMap().getTerrainAtPosition(unit.getPosition()).isRedoubt())
                        drawRedoubt(g, unit.getPosition());
                    if(drawUnit.getUnit().isSupporting())
                    drawSupporting(g, drawUnit.getUnit());
                }
                }
            }
    }
    private void drawCardSelections(Graphics g){
    
        Card playingCard = game.getCardCommandFactory().getPlayingCard();
        if(playingCard!= null)
        {
            ArrayList<Position> movePositions;
           
           if(playingCard.canBePlayed(game))
               
           switch (playingCard.getCardType()){
                case Card.HQCARD :
                {
                    switch(playingCard.getHQType())
                    {
                    case Card.FORCED_MARCH: 
                        {
                            
                        if(!playingCard.hasPlayed()){
                            Unit lastMovedUnit = currentPlayer.getLastMovedUnit();
                            movePositions = game.getOneSquareMovements(lastMovedUnit.getPosition());
                            drawMultipleRectanglesOnPositions(g, movePositions, Color.BLUE);
                        }
                        break;
                        }
                    case Card.SUPPLY: 
                        {
                        if(game.getSelectedUnit()!= null)  {  
                                Unit selectedUnit = game.getSelectedUnit();
                                movePositions = game.getPossibleMovement(selectedUnit);
                                drawMultipleRectanglesOnPositions(g, movePositions, Color.BLUE);
                        }
                        
                        else {
                                drawMultipleRectanglesOnPositions(g, game.getCurrentPlayerNotMovedUnits(), Color.BLUE);
                        }
                        
                        break;
                        }    
                        
                    case Card.WITHDRAW: 
                        {
                        Unit attackedUnit = game.getCardCommandFactory().getAttackedUnit();
                        movePositions = game.getRetreatPositions(attackedUnit);
                        drawMultipleRectanglesOnPositions(g, movePositions, Color.BLUE);
                        break;
                        }
                     case Card.REDOUBDT: 
                        {
                        
                        drawMultipleRectanglesOnPositions(g, game.getCurrentPlayer().getArmyPositions(), Color.BLUE);
                        break;
                        }  
                     case Card.SKIRMISH: 
                        {
                        Unit attackedUnit = game.getCombat().getAttackingUnit();
                        movePositions = game.getTwoSquareMovements(attackedUnit.getPosition());
                        drawMultipleRectanglesOnPositions(g, movePositions, Color.BLUE);
                        break;
                        }  
                        
                        
                        
                    }

                }
                case Card.UNIT :{
                    /*
                    Draw selection of unit that matches playing card
                    */
                switch(game.getPhase()){    
                   
                    case Game.MOVE:{    
                        if(game.checkCurrentPlayerUnitByName(playingCard.getCardName()))
                        {
                           Unit attackingUnit = game.getCurrentPlayerUnitByName(playingCard.getCardName());
                           Position unitPosition = attackingUnit.getPosition();
                           drawRectangleOnPosition(g, unitPosition, Color.BLUE);
                        }
                        break;
                    }
                    case Game.COMBAT:
                    {
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
                                Unit attackingUnit = game.getCurrentPlayerUnitByName(playingCard.getCardName());
                                drawRectangleOnPosition(g, attackingUnit.getPosition(), Color.WHITE);
                                
                            if(playingCard.getPlayingCardMode() !=null  )
                            { 
                                
                                if(cardFactory.getAttackingPositions() != null)
                                    drawArrowToPositions(g, 
                                    attackingUnit.getPosition(),
                                    cardFactory.getAttackingPositions(),
                                    Color.RED
                                    );
                            }
      
                            }
                        /*
                        We have combat
                        */
                        else
                        {
                           switch(game.getCombat().getState()){
                           
                           case Combat.PICK_SUPPORT_UNIT:
                                {
                                drawMultipleRectanglesOnPositions(g,
                                        game.getPossibleSupportingUnitsPositions(game.getCombat().getDefendingUnit())
                                        , Color.red);   
                                break;
                                }
                           
                           }
                            
                        }        
                                
                    break;
                    }    
   
                }  

                break;
                }
                case Card.LEADER:
                {
                    switch(game.getPhase()){
                        case Game.COMBAT:
                        {
                            if(game.getCombat()!=null)
                                if(game.getCombat().getState() == Combat.PICK_SUPPORT_CARDS)
                                    drawMultipleRectanglesOnPositions(g,
                                        game.getPossibleSupportingUnitsPositions(game.getCombat().getDefendingUnit())
                                        , Color.BLUE);   
                            break;
                        }
                    
                    }
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
                        position.getMouseX(windowMode) + MapGUI.PIECES_START_X
                                ,
                        position.getMouseY(windowMode) + MapGUI.PIECES_START_Y ,
                        MapGUI.PIECE_WIDTH, 
                        MapGUI.PIECE_HEIGHT
                        , null);
    }
    private void drawTerrainOnPosition(Graphics g, Position position, Image image){

         g.drawImage(image, 
                        position.getMouseX(windowMode) 
                                ,
                        position.getMouseY(windowMode) ,
                        MapGUI.SQUARE_WIDTH,
                        MapGUI.SQUARE_HEIGHT
                        , null);
    }
    
    /*
    Draw rectangle on position
    */
    private void drawRectangleOnPosition(Graphics g, Position position, Color color){
    
    g.setColor(color);
    g.drawRoundRect(
            
                    position.getMouseX(windowMode) + gapSelection,
                    position.getMouseY(windowMode) + gapSelection,
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
    private  void drawArrowToPosition(Graphics g , Position fromPosition, Position toPosition, Color color)
    {
    
        g.setColor(color);
        
        /*
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
                            toPosition.getMouseX() + MapGUI.PIECE_WIDTH / 2
                            :
                            toPosition.transpoze().getMouseX() + MapGUI.PIECE_WIDTH / 2        
                            ,
                    (windowMode == CreateRoomWindow.AS_HOST) ?
                            toPosition.getMouseY() +  MapGUI.PIECE_WIDTH / 2
                            :
                            toPosition.transpoze().getMouseY() +  MapGUI.PIECE_WIDTH / 2      
                    , 10,15)
                            ;
            */
        drawArrow(g,
                            fromPosition.getMouseX(windowMode) +  MapGUI.PIECE_WIDTH / 2 
                            
                            ,
                    
                            fromPosition.getMouseY(windowMode) +  MapGUI.PIECE_HEIGHT / 2
                             
                                    ,                    
                    
                            toPosition.getMouseX(windowMode) + MapGUI.PIECE_WIDTH / 2
                               
                            ,
                   
                            toPosition.getMouseY(windowMode) +  MapGUI.PIECE_WIDTH / 2      
                    , 10,15)
                            ;
    }
    
    
    private  void drawArrowToPositions(Graphics g , Position fromPosition, ArrayList<Position> toPositions, Color color){
    
        g.setColor(color);
        for (Position toPositon: toPositions )
            {
            drawArrowToPosition(g , fromPosition, toPositon,  color);
            }

    }
    
    private void drawRedoubt(Graphics g , Position position){

       g.drawImage(redoubtImage, 
                        position.getMouseX(windowMode) + MapGUI.PIECES_START_X
                              ,
                        position.getMouseY(windowMode) + MapGUI.PIECES_START_Y ,
                        (int) (MapGUI.PIECE_WIDTH * MapGUI.REDOUBT_IMG_SCALE), 
                        (int) (MapGUI.PIECE_HEIGHT * MapGUI.REDOUBT_IMG_SCALE) 
                        , null);  
        
    }
    
    private void drawLetterOnPosition(Graphics g, Position pos, Color color, String letter)
    {
    g.setColor(color);
    g.setFont(new Font("Bookman Old Style", 1, 14));
    g.drawString(letter, 
    pos.getMouseX(windowMode) + MapGUI.LETTER_OFFSET_X,
    pos.getMouseY(windowMode) + MapGUI.PIECES_START_Y + MapGUI.LETTER_OFFSET_Y);
    
    }
    
    
    private void drawTerrainLetter(Graphics g, Unit unit)
    {
                /*
                Draw a single letter to indicate rough terrain
                */
                Terrain terrain = game.getMap().getTerrainAtPosition(unit.getPosition());
                
                if(
                        game.getMap().getTerrainAtPosition(unit.getPosition()).getType() == Terrain.CITY ||
                        game.getMap().getTerrainAtPosition(unit.getPosition()).getType() == Terrain.HILL ||
                        game.getMap().getTerrainAtPosition(unit.getPosition()).getType() == Terrain.MARSH ||
                        game.getMap().getTerrainAtPosition(unit.getPosition()).getType() == Terrain.FOREST
                        )
                {
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Bookman Old Style", 1, 14));
                    switch(terrain.getType())
                    {
                        case Terrain.CITY: 
                        {
                        drawLetterOnPosition(g, unit.getPosition(), Color.BLACK, "C");
                        
                        break;
                        }
                        case Terrain.HILL: 
                        {
                        drawLetterOnPosition(g, unit.getPosition(), Color.BLACK, "H");
                        break;
                        }
                        case Terrain.MARSH: 
                        {
                        drawLetterOnPosition(g, unit.getPosition(), Color.BLACK, "M");
                        break;
                        }
                        case Terrain.FOREST: 
                        {
                        drawLetterOnPosition(g, unit.getPosition(), Color.BLACK, "F");
                        break;
                        }
                        
                    
                    }
                    
                
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
     
    if(game.getCombat() != null)    
    {if(game.getCombat().getState() == Combat.WITHRDAW)
    if(game.getSelectedUnit()!= null){
        
        Unit selectedUnit = game.getSelectedUnit();
        if(selectedUnit != null)
            if(selectedUnit.isRetriving()) 
                     
                drawArrowToPositions(g,  selectedUnit.getPosition(), game.getRetreatPositions(selectedUnit), Color.GREEN);
                       
            }
    
     if(game.getCombat().getState() == Combat.PURSUIT)
     {
            drawMultipleRectanglesOnPositions(g, game.getCurrentPlayerAvalibleUnitToSelect(), Color.RED);
       
     }
    }
      
    }
    
    private void drawCombat(Graphics g){
    Combat combat = game.getCombat();
        
    if(combat!= null)
    if(
          combat.getState().equals(Combat.INITIALIZING_COMBAT) ||
            combat.getState().equals(Combat.PICK_DEFENSE_CARDS) ||  
            combat.getState().equals(Combat.PICK_SUPPORT_CARDS) ||  
            combat.getState().equals(Combat.PICK_SUPPORT_UNIT) ||    
            combat.getState().equals(Combat.DEFENDER_DECIDES) ||    
            combat.getState().equals(Combat.ATTACKER_DECIDES)  ||
             combat.getState().equals(Combat.THROW_DICES) 
 
         )
    drawArrowToPosition(g,  combat.getAttackingUnit().getPosition(), 
                combat.getDefendingUnit().getPosition(), Color.GREEN);

    /*
    Draw supporting
    */
    
      for (Unit  unit : game.getCurrentPlayer().getArmy()) {
                if(!unit.isEliminated())
                {
                    if(unit.isSupporting())
                    drawSupporting(g, unit);
                }
            }
       for (Unit  unit : game.getOpponentPlayer().getArmy()) {
                if(!unit.isEliminated())
                {
                    if(unit.isSupporting())
                    drawSupporting(g, unit);
                    
                        
                }
            }
    
    
    }
    
    private void drawSupporting(Graphics g, Unit unit)
    {
        drawRectangleOnPosition(g, unit.getPosition(), Color.BLUE);
        drawArrowToPosition(g, unit.getPosition(), game.getCombat().getDefendingUnit().getPosition(),
                Color.BLUE);
        //drawLetterOnPosition(g, unit.getPosition(), Color.BLUE, "SUP");
    }
    
    
    
    
    
    private void drawLOS(Graphics g){
        if (game.getSelectedUnit()!= null){
        Unit selectedUnit = game.getSelectedUnit();
            if(selectedUnit.isShowingLOS()) 
                drawArrowToPositions(g, selectedUnit.getPosition(), game.getLOS(selectedUnit, 2), Color.yellow);
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
    
    public void keepOneSelectedCard(Card cardClicked){
  
        game.getCurrentPlayer().getHand().unselectAllCards();
        game.getCurrentPlayer().getHand().selectCard(cardClicked);

    }
    public void paintHand(Graphics g)                 
    {   
        cardSetsGUI.paintHand(g, game);
    }
    
    
    public void paintOpponentHand(Graphics g)                 
    {   
        cardSetsGUI.paintOpponentHand(g, game);
    }

   
    private BufferedImage cropImage(Image img, int x, int y, int width, int height){
        BufferedImage buffImage = (BufferedImage)img;
        return buffImage.getSubimage(x, y, width, height);
    }
            
    public void paintDiscard(Graphics g, boolean paintOpponent){
        
        cardSetsGUI.paintDiscard(g, paintOpponent, game);

    }
    
    public void paintDrawLeft(Graphics g, boolean paintOpponent){
        cardSetsGUI.paintDrawLeft(g, paintOpponent);
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
       
        cardSetsGUI.paintTablePanel(g);
        //paintDices(g);
    
    }
    
   
    private void paintDices(Graphics g){
        
        final int STARTING_D6_X = 480;
        final int STARTING_D6_Y = 30;
        
        final int STARTING_D8_X = STARTING_D6_X;
        final int STARTING_D8_Y = STARTING_D6_Y + DiceGUI.D6SQUARE_HEIGHT ;
        
        final int STARTING_D10_X = STARTING_D6_X;
        final int STARTING_D10_Y = STARTING_D8_Y + DiceGUI.D8SQUARE_HEIGHT ;

        int i=0;
        
        if(game.getCardCommandFactory().getD6dices() != null)
            for(Dice d6  :  game.getCardCommandFactory().getD6dices() ){
                i++;
                DiceGUI d6gui = new DiceGUI(d6);
                

                g.drawImage(d6gui.getImage(), 
                STARTING_D6_X  - (i-1)* (int)(DiceGUI.D6SQUARE_WIDTH*DiceGUI.SCALE_FACTOR_D6), 
                CardSetGUI.TABLE_CARD_PADDING_TOP ,
                (int)(d6gui.getImage().getWidth()*DiceGUI.SCALE_FACTOR_D6),
                (int)(d6gui.getImage().getHeight()*DiceGUI.SCALE_FACTOR_D6)

                , null); 
                    
                    
            } 
        i=0;
        if(game.getCardCommandFactory().getD8dices()!= null)
            for(Dice d8 : game.getCardCommandFactory().getD8dices() ){
                i++;
                
                 

                DiceGUI d8gui = new DiceGUI(d8);
                
                g.drawImage(d8gui.getImage(),
                        STARTING_D8_X -  (i-1)* (int)(DiceGUI.D8SQUARE_WIDTH*DiceGUI.SCALE_FACTOR_D8) , 
                        
                        CardSetGUI.TABLE_CARD_PADDING_TOP, 
                        (int)(d8gui.getImage().getWidth()*DiceGUI.SCALE_FACTOR_D8),
                        (int)(d8gui.getImage().getHeight()*DiceGUI.SCALE_FACTOR_D8),
                        
                        null);   
            } 
        i=0;
        if(game.getCardCommandFactory().getD10dices()!= null)
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

    public Position getHoverPosition() {
        return hoverPosition;
    }

    public void setHoverPosition(Position hoverUnit) {
        this.hoverPosition = hoverUnit;
    }

    public BufferedImage getInfoImage() {
        return infoImage;
    }

    public void setInfoImage(BufferedImage infoImage) {
        this.infoImage = infoImage;
    }
    
    public ArrayList<UnitGUI> getUnitsGui() {
        
        ArrayList<UnitGUI> packUnits = new ArrayList<>();
        packUnits.addAll(currentPlayerArmy);
        if(game.getPhase() != Game.SETUP)
            packUnits.addAll(opponnetPlayerArmy);
      
        return packUnits;
    }

  
    
    
}
