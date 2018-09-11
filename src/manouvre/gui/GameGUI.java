/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.game.Unit;
import manouvre.game.Card;
import manouvre.game.Player;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import manouvre.game.Combat;
import manouvre.game.Dice;
import manouvre.game.Terrain;
import manouvre.state.PlayerState;
import org.apache.logging.log4j.LogManager;



/**
 *
 * @author Bartosz
 */
public class GameGUI {
    
    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(GameGUI.class.getName());
  
    Game game;
    PlayerState playerStateHandler;
    
    ArrayList<UnitGUI> currentPlayerArmy = new ArrayList<UnitGUI>(); 
    ArrayList<UnitGUI> opponentPlayerArmy = new ArrayList<UnitGUI>(); 
    MapGUI mapGui;
    
    Player currentPlayer;
    
    CardSetGUI cardSetsGUI;
   
    BufferedImage  infoImage;
    
    Position hoverPosition;
    
    /*
    Wielkosc ramki stolu w kwadracie w pikselach
    */
    final int BACKGRNDTABLE = 678;
    final int GAP_SELECTION = 5;
    final int GAP_UNIT = 7;
    
    
    private int windowMode;
    
    private BufferedImage redoubtImage;
    

    
    public GameGUI (Game newGame, PlayerState playerStateHandler,  int windowMode) throws IOException{

        this.game=newGame;
        this.playerStateHandler  = playerStateHandler;
        this.currentPlayer=game.getCurrentPlayer();
        this.windowMode = windowMode;
        this.mapGui = new MapGUI(game.getMap(), windowMode);
        this.generateUnitsUI();
        
        this.cardSetsGUI = new CardSetGUI(game);
        game.addObserver(cardSetsGUI);
                              
        /*
        Set info about first / second player
        */
        CustomDialogFactory.showConfirmationDialog(
             currentPlayer.getName() + ", You are" + (currentPlayer.isFirst() ? " first " : " second ") + "player");
           
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
        final float UNIT_SCALE = 0.7f;
        
        final int DECRIPTION_Y_GAP = 12;
        final int DECRIPTION_Y_OFFSET = TERRAIN_Y_OFFSET + 76 ;
        final float TERRAIN_SCALE = 0.7f;
        
        if(getHoverPosition() != null)
        {
        TerrainGUI terrain = mapGui.getTerrainGuiAtPosition(hoverPosition);
        UnitGUI unit =  getUnitGuiOnMapGui(hoverPosition);
        if(unit != null)
            
        g.drawImage(unit.getImg(), UNIT_X_OFFSET, UNIT_Y_OFFSET,
                (int) UNIT_SCALE * unit.getImg().getWidth(),
                (int) UNIT_SCALE * unit.getImg().getHeight(), null);
        
        if(terrain!= null)
        {
            g.drawImage(
                    terrain.getImg(), 
                    TERRAIN_X_OFFSET,
                    TERRAIN_Y_OFFSET,
                    (int) (terrain.getImg().getWidth() * TERRAIN_SCALE),
                    (int) (terrain.getImg().getHeight() * TERRAIN_SCALE),
                    null) ;
        
            g.setColor(Color.RED);
            
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
       
        
        if(
           !game.getCombat().getState().equals(Combat.COMBAT_NOT_INITIALIZED)
        && !game.getCombat().getState().equals(Combat.INITIALIZING_COMBAT)
                )
        {
            Combat combat =  game.getCombat();
            g.drawString("Attack: " + combat.getAttackValue(), START_X_COMBAT_PANEL,START_Y_COMBAT_PANEL+ Y_GAP);
            
            g.drawString("Defense: " + combat.getDefenceValue(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL + 2*Y_GAP);
            
            g.drawString("Def.Terrain: " + combat.getDefenseTerrain().getTypeToString(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL+ 3*Y_GAP);
            
            g.drawString("Ter.Def.Bonus: " + combat.getDefenseBonus(), START_X_COMBAT_PANEL,START_Y_COMBAT_PANEL+ 4*Y_GAP);
            
            g.drawString("Att.Terrain: " + combat.getAttackTerrain().getTypeToString(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL+  5*Y_GAP);
            
            g.drawString("Ter.Att.Bonus: " + combat.getAttackBonus(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL+ 6*Y_GAP);
            
            g.drawString("Leader Bonus: " + combat.getLeaderBonus(), START_X_COMBAT_PANEL,START_Y_COMBAT_PANEL+  7*Y_GAP);
  
        }
    }
    
//------------- MAP - LEFT UPPER CORNER OF THE SCREEN -----------------------------------
    void drawMap( Graphics g, int windowMode) {
        // draw background
           drawMapByPhase(g);
    }
    
    private void drawMapByPhase(Graphics g){
        g.drawImage(mapGui.background, 0, 0,BACKGRNDTABLE,BACKGRNDTABLE, null);
        drawTerrains(g);
        drawBorder(g);
        
        switch(game.getPhase()){
        
            case Game.SETUP:   
                drawSetup(g);
                break;
            case Game.DISCARD:
            case Game.DRAW:  
                drawArmyWithLetterAndRedoubt(g);
                break;
            case Game.MOVE:    
                drawMove(g);
                break;
            case Game.COMBAT:  
                drawCombat(g);
            break;
            case Game.RESTORATION:
               drawRestoration(g);
        }
        
    
    }
    
    
    private void drawPossibleMoveInSetup(Graphics g){
        Unit selectedUnit = game.getSelectedUnit();
        if (selectedUnit.getID() !=-1 ) 
            {
            drawRectangleOnPosition(g, selectedUnit.getPosition(), Color.WHITE);
            ArrayList<Position> movePositions;
            movePositions = game.positionCalculator.getSetupPossibleMovement();
            drawMultipleRectanglesOnPositions(g, movePositions, Color.blue);
            }
    
    }
    private void drawUnitSelection(Graphics g){
        for(Unit selectedUnit:game.getCurrentPlayer().getNotKilledUnits())
            if (selectedUnit.isSelected())
                drawRectangleOnPosition(g, selectedUnit.getPosition(), Color.WHITE);    
        
        for(Unit selectedUnit:game.getOpponentPlayer().getNotKilledUnits())
            if (selectedUnit.isSelected())
                drawRectangleOnPosition(g, selectedUnit.getPosition(), Color.WHITE);    
    }
    private void drawPossibleMove(Graphics g){
        /*Draws selection of possible move whlie not playing card
            */
        Unit selectedUnit = game.getSelectedUnit();
        if (selectedUnit.getID() != -1) 
            {
                ArrayList<Position> movePositions;
                if(game.freeMove)
                    if(!selectedUnit.hasMoved())
                        drawPossibleMoveInSetup(g);
                else                 
                    if(!selectedUnit.hasMoved())
                    {
                        movePositions = game.positionCalculator.getPossibleMovement(selectedUnit);
                        drawMultipleRectanglesOnPositions(g, movePositions, Color.blue);
                    }
            }    
    }
    private void drawSetup(Graphics g){
        /*
        In setup draw only self army
        */
        if(!( currentPlayer.isFinishedSetup() && game.getOpponentPlayer().isFinishedSetup() ) )
        {
                for (UnitGUI drawUnit : currentPlayerArmy) 
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
                        g.drawRoundRect(drawUnit.getUnit().getPosition().getMouseX(windowMode) + GAP_SELECTION, 
                                    drawUnit.getUnit().getPosition().getMouseY(windowMode) + GAP_SELECTION, 
                                    MapGUI.SQUARE_WIDTH - 2 * GAP_SELECTION, 
                                    MapGUI.SQUARE_HEIGHT - 2 * GAP_SELECTION, 
                                    10, 10
                            ); 
                        }        
                }
        }
        else drawArmyWithLetterAndRedoubt(g);
        
        drawPossibleMoveInSetup(g);
    
    }
    
    private void drawMove(Graphics g){
    
        drawArmyWithLetterAndRedoubt(g);
        drawUnitSelection(g);
        Card playingCard = playerStateHandler.cardPlayingHandler.getPlayingCard();
        if(playingCard.getType() != Card.NO_CARD)
            drawMoveCards(g);
        else 
            drawPossibleMove(g);
    
    }
    private void drawMoveCards(Graphics g){
           Card playingCard = playerStateHandler.cardPlayingHandler.getPlayingCard();
           ArrayList<Position> movePositions;
           switch (playingCard.getType()){
                case Card.HQCARD :
                    switch(playingCard.getHQType())
                    {
                    case Card.FORCED_MARCH: 
                        if(!playingCard.hasPlayed()){
                            Unit lastMovedUnit = currentPlayer.getLastMovedUnit();
                            movePositions = game.positionCalculator.getOneSquareMovements(lastMovedUnit.getPosition());
                            drawMultipleRectanglesOnPositions(g, movePositions, Color.BLUE);
                        }
                        break;
                    case Card.SUPPLY: 
                        if(game.getSelectedUnit().getID() != -1)  {  
                            Unit selectedUnit = game.getSelectedUnit();
                            if(!selectedUnit.hasMoved())
                            {
                                movePositions = game.positionCalculator.getPossibleMovement(selectedUnit);
                                drawMultipleRectanglesOnPositions(g, movePositions, Color.BLUE);
                            }
                        }
                        else 
                            if(!playingCard.hasPlayed())
                                drawMultipleRectanglesOnPositions(g, game.positionCalculator.getCurrentPlayerNotMovedUnits(), Color.BLUE);
                            
                        break;
                    }
                case Card.UNIT:
                    if(game.checkCurrentPlayerUnitByCard(playingCard))
                    {
                        Unit attackingUnit = game.getCurrentPlayerUnitByCard(playingCard);
                        Position unitPosition = attackingUnit.getPosition();
                        drawRectangleOnPosition(g, unitPosition, Color.BLUE);
                    }
                break;
           }
    }
     private void drawCombat(Graphics g){
    
        drawArmyWithLetterAndRedoubt(g);
        drawCombatUnitSelection(g);
        drawComabatCardSelections(g);
    }
    
    
    private void drawCombatUnitSelection(Graphics g){
    
    if(game.getPhase() == Game.COMBAT)
    {
        Combat combat = game.getCombat();
        switch(combat.getState())
        {
        case Combat.COMBAT_NOT_INITIALIZED:
        case Combat.END_COMBAT:
        break;
        
        case Combat.INITIALIZING_COMBAT:
            drawAttackingUnitSelection(g);
            drawPossibleAttackPositions(g);
        break;
        
        case Combat.PICK_DEFENSE_CARDS:
        case Combat.PICK_SUPPORT_CARDS:
        case Combat.DEFENDER_DECIDES:
        case Combat.ATTACKER_DECIDES:
            drawArrowFromAttackingToDefending(g);
            drawArrowFromSupportingUnits(g);
            drawRetrievingPositionPossibility(g);
            drawPursuitAvalaibleUnits(g);
        break;
        
        case Combat.PURSUIT:
            drawArrowFromAttackingToDefending(g);
            drawArrowFromSupportingUnits(g);
            drawPursuitAvalaibleUnits(g);
        break;    
        
        case Combat.WITHRDAW:
            drawArrowFromAttackingToDefending(g);
            drawArrowFromSupportingUnits(g);
            drawRetrievingPositionPossibility(g);
        break;
        
        case Combat.PICK_SUPPORT_UNIT:
            drawArrowFromAttackingToDefending(g);
            drawPossibleSupportingUnits(g);
            drawArrowFromSupportingUnits(g);
        break;    
             
        default: 
            drawArrowFromAttackingToDefending(g);
            drawArrowFromSupportingUnits(g);
            System.out.println("manouvre.gui.GameGUI.drawCombat() nie obslugujemy tego w draw" + combat.getState() );
        }
    }
    }
    private void drawPossibleAttackPositions(Graphics g){
        Unit attackingUnit = game.getCombat().getAttackingUnit();
        Card attackCard = game.getCombat().getInitAttackCard();

        if(attackCard.getPlayingCardMode() !=Card.NO_TYPE)
        { 
            if(game.getCombat().getAttackingPositions() != null)
                drawArrowToPositions(g, 
                attackingUnit.getPosition(),
                game.getCombat().getAttackingPositions(),
                Color.RED
                );
        }
    }
    
    private void drawPossibleSupportingUnits(Graphics g)
    {
        drawMultipleRectanglesOnPositions(g,
                    game.positionCalculator.getPossibleSupportingUnitsPositions()
                    , Color.red);   
    }
    
    private void drawPursuitAvalaibleUnits(Graphics g){
        drawMultipleRectanglesOnPositions(g, game.positionCalculator.getCurrentPlayerAvalibleUnitToSelect(), Color.RED);
    
    }
    
    private void drawRetrievingPositionPossibility(Graphics g){
        Unit retrievingUnit = game.getUnit(game.getCombat().getDefendingUnit());
        if(retrievingUnit.isRetriving()){
                  drawArrowToPositions(g,  retrievingUnit.getPosition(), 
                          game.positionCalculator.getRetreatPositions(retrievingUnit),
                          Color.GREEN);
        }
    
    
    }
    
    private void drawArrowFromAttackingToDefending(Graphics g){
        
        drawArrowToPosition(g,  game.getCombat().getAttackingUnit().getPosition(), 
                     game.getCombat().getDefendingUnit().getPosition(), Color.GREEN);
    
    }
    
    private void drawArrowFromSupportingUnits(Graphics g)
    {
        for (Unit  unit : game.getCurrentPlayer().getNotKilledUnits()) {
            if(unit.isSupporting())
                {
                drawRectangleOnPosition(g, unit.getPosition(), Color.BLUE);
                drawArrowToPosition(g, unit.getPosition(), game.getCombat().getDefendingUnit().getPosition(),
                        Color.BLUE);
                }
        }
        for (Unit  unit : game.getOpponentPlayer().getNotKilledUnits()) {
            if(unit.isSupporting())
               {
                drawRectangleOnPosition(g, unit.getPosition(), Color.BLUE);
                drawArrowToPosition(g, unit.getPosition(), game.getCombat().getDefendingUnit().getPosition(),
                        Color.BLUE);
                } 
        }
    }
    private void drawArmyWithLetterAndRedoubt(Graphics g){
        
        for (UnitGUI drawUnit : currentPlayerArmy) {
            if(!drawUnit.getUnit().isEliminated())
            {
                Unit unit = drawUnit.getUnit();
                drawImageOnPosition(g, unit.getPosition(), drawUnit.getImg());
                drawTerrainLetter(g, unit);
                if(game.getMap().getTerrainAtPosition(unit.getPosition()).isRedoubt())
                    drawRedoubt(g, unit.getPosition());
            }
        }
        for (UnitGUI drawUnit : opponentPlayerArmy) {
            if(!drawUnit.getUnit().isEliminated()){
                 Unit unit = drawUnit.getUnit();
                 drawImageOnPosition(g, unit.getPosition(), drawUnit.getImg());
                 drawTerrainLetter(g, unit);
                if(game.getMap().getTerrainAtPosition(unit.getPosition()).isRedoubt())
                    drawRedoubt(g, unit.getPosition());
            }
            }
    }
    
    
    private void drawComabatCardSelections(Graphics g){
    
        Card playingCard = playerStateHandler.cardPlayingHandler.getPlayingCard();
        if(playingCard.getType() != Card.NO_CARD)
        {
           ArrayList<Position> movePositions;
           switch (playingCard.getType()){
                case Card.HQCARD :
                {
                    switch(playingCard.getHQType()){
                    case Card.WITHDRAW: 
                        Unit defendingUnit = game.getCombat().getDefendingUnit();
                        movePositions = game.positionCalculator.getRetreatPositions(defendingUnit);
                        drawMultipleRectanglesOnPositions(g, movePositions, Color.BLUE);
                        break;
                     case Card.REDOUBDT: 
                        drawMultipleRectanglesOnPositions(g, game.getCurrentPlayer().getArmyPositions(), Color.BLUE);
                        break;
                     case Card.SKIRMISH: 
                        Unit attackingUnit = game.getCombat().getAttackingUnit();
                        movePositions = game.positionCalculator.getTwoSquareMovements(attackingUnit.getPosition());
                        drawMultipleRectanglesOnPositions(g, movePositions, Color.BLUE);
                        break;
                    }
                }
                case Card.LEADER:
                    if(game.getCombat().getState().equals(Combat.PICK_SUPPORT_CARDS))
                        drawMultipleRectanglesOnPositions(g,
                            game.positionCalculator.getPossibleSupportingUnitsPositions()
                            , Color.BLUE);   
                break;
                
                case Card.NO_CARD:
                break;
                
                case Card.UNIT:
                     drawUnitSelection(g);
                break;     
                
                default: System.err.println("drawCardSelections()  Brak typu karty " + playingCard.getCardName());
            }
               
        }
    
    }
    
    private void drawRestoration(Graphics g){
    
        drawArmyWithLetterAndRedoubt(g);
        drawRestorationCards(g);
        drawRestorationLeaderDices(g);
    
    }
    
    private void drawRestorationCards(Graphics g){
        
        Card playingCard = playerStateHandler.cardPlayingHandler.getPlayingCard();
        ArrayList<Position> restorePossiblePostions;
        if(playingCard.getType() != Card.NO_CARD)
        {
            switch (playingCard.getType()){
                case Card.HQCARD :
                    switch(playingCard.getHQType()){
                    case Card.SUPPLY: 
                    case Card.REGROUP:
                         restorePossiblePostions = game.positionCalculator.getCurrentPlayerInjuredUnitPositions();
                        drawMultipleRectanglesOnPositions(g, restorePossiblePostions, Color.yellow);
                    break;
                    case Card.REDOUBDT:
                        drawMultipleRectanglesOnPositions(g, game.getCurrentPlayer().getArmyPositions(), Color.yellow);
                    break;    
                    }
                    
                break;
                case Card.UNIT :
                    Position restorePossiblePostion = game.getCurrentPlayerUnitByCard(playingCard).getPosition();
                    drawRectangleOnPosition(g, restorePossiblePostion, Color.yellow);
                break;
                case Card.LEADER:
                    restorePossiblePostions = game.positionCalculator.getCurrentPlayerInjuredUnitPositions();
                    drawMultipleRectanglesOnPositions(g, restorePossiblePostions, Color.yellow);
                break; 
                
                }
        
        }
    }
    
    private void drawRestorationLeaderDices(Graphics g){
        
        for (UnitGUI drawUnit : currentPlayerArmy) {
            if(!drawUnit.getUnit().isEliminated())
            {
                Unit unit = drawUnit.getUnit();
                if(unit.getRestorationDice() != null)
                    drawDice(g,unit.getRestorationDice(), unit.getPosition());
            }
        }
        for (UnitGUI drawUnit : opponentPlayerArmy) {
            if(!drawUnit.getUnit().isEliminated()){
                 Unit unit = drawUnit.getUnit();
                 if(unit.getRestorationDice() != null)
                    drawDice(g,unit.getRestorationDice(), unit.getPosition());
            }
            }
    }
    
    private void drawDice(Graphics g, Dice dice, Position unitPosition){

        DiceGUI diceGUI = new DiceGUI(dice);
        int sizex = (int)(diceGUI.getImage().getWidth()* DiceGUI.SCALE_FACTOR_D6);
        int sizey = (int)(diceGUI.getImage().getHeight()*DiceGUI.SCALE_FACTOR_D6);
        g.drawImage(diceGUI.getImage(), 
                   unitPosition.getMouseX(windowMode),
                   unitPosition.getMouseY(windowMode),
                   sizex, 
                   sizey
                   , null);

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
    
    private void drawAttackingUnitSelection(Graphics g)
    {
         Unit attackingUnit = game.getCombat().getAttackingUnit();
         drawRectangleOnPosition(g, attackingUnit.getPosition(), Color.WHITE);
    }
    
    
    /*
    Draw rectangle on position
    */
    private void drawRectangleOnPosition(Graphics g, Position position, Color color){
    
    g.setColor(color);
    g.drawRoundRect(position.getMouseX(windowMode) + GAP_SELECTION,
                    position.getMouseY(windowMode) + GAP_SELECTION,
                    MapGUI.SQUARE_WIDTH - 2 * GAP_SELECTION, 
                    MapGUI.SQUARE_HEIGHT - 2 * GAP_SELECTION, 
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
        int ARROW_WIDTH = 10;
        int ARROW_HEIGHT = 15;
        g.setColor(color);
        drawArrow(  
                g,
                fromPosition.getMouseX(windowMode) +  MapGUI.PIECE_WIDTH / 2 
                ,
                fromPosition.getMouseY(windowMode) +  MapGUI.PIECE_HEIGHT / 2
                ,                    
                toPosition.getMouseX(windowMode) + MapGUI.PIECE_WIDTH / 2
                ,
                toPosition.getMouseY(windowMode) +  MapGUI.PIECE_WIDTH / 2      
                ,ARROW_WIDTH
                ,ARROW_HEIGHT
                );
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
    
    private void drawCombatPursuit(Graphics g){
  
        drawMultipleRectanglesOnPositions(g, game.positionCalculator.getCurrentPlayerAvalibleUnitToSelect(), Color.RED);
    
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
    
    private void drawLOS(Graphics g){
        if (game.getSelectedUnit().getID() != -1){
        Unit selectedUnit = game.getSelectedUnit();
            if(selectedUnit.isShowingLOS()) 
                drawArrowToPositions(g, selectedUnit.getPosition(), game.positionCalculator.getLOS(selectedUnit, 2), Color.yellow);
            }
    }
    
    private void generateUnitsUI() {
        for (Unit unit : currentPlayer.getArmy()) {
            currentPlayerArmy.add(new UnitGUI(unit));
        }
        for (Unit unit : game.getOpponentPlayer().getArmy()) {
            opponentPlayerArmy.add(new UnitGUI(unit));
        }
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
    
    public void paintHand(Graphics g){   
        cardSetsGUI.paintHand(g, game);
    }
    
    public void paintOpponentHand(Graphics g){   
       cardSetsGUI.paintOpponentHand(g, game);
    }
           
    public void paintDiscard(Graphics g, boolean paintOpponent){
         cardSetsGUI.paintDiscard(g, paintOpponent, game);
    }
    public void paintDrawLeft(Graphics g, boolean paintOpponent){
        cardSetsGUI.paintDrawLeft(g, paintOpponent);
    }
         
    public void paintTablePanel(Graphics g){
        cardSetsGUI.paintTablePanel(g);
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
            packUnits.addAll(opponentPlayerArmy);
        return packUnits;
    }
}
