package manouvre.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Observable;
import java.util.Observer;
import manouvre.events.EventType;
import manouvre.game.Card;
import manouvre.game.CardSet;
import manouvre.game.Combat;
import manouvre.game.Game;
import manouvre.game.Dice;
import manouvre.game.Position;
import manouvre.game.Terrain;
import manouvre.game.Unit;

    /**
     * Retrives card picture/image
     * @author Bartosz
     */

    public class CardSetsGUI implements Observer{

        
    public static final int START_X_COMBAT_PANEL = 460;
    public static final int START_Y_COMBAT_PANEL =200;
    public static final int COMBAT_PANEL_Y_GAP = 16;
    
    public static final int INFOPANEL_TERRAIN_X_OFFSET = 460;
    public static final int INFOPANEL_TERRAIN_Y_OFFSET = 50;
    public static final int INFOPANEL_UNIT_X_OFFSET = 300;
    public static final int INFOPANEL_UNIT_Y_OFFSET = 50;
    public static final float INFOPANEL_UNIT_SCALE = 0.7f;

    public static final int INFOPANEL_DECRIPTION_Y_GAP = 12;
    public static final int INFOPANEL_DECRIPTION_Y_OFFSET = INFOPANEL_TERRAIN_Y_OFFSET + 76 ;
    public static final float INFOPANEL_TERRAIN_SCALE = 0.7f;    
    
    public static final int INFOPANEL_TEXT_START_X = 10;
    public static final int INFOPANEL_TEXT_START_Y = 380;
        
    public static final int HAND_CARDPADDINGTOP = 20;
    public static final int HAND_CARDPADDINGLEFT = 5;
    public static final int HAND_CARDPADDELTA = 20;
    public static final int HAND_CARDGAP = 5;
    public static final int HAND_LINEGAP = 5;
    public static final int HAND_RECTANGLE_LINEGAP = 10;
    
    /*
    Table constans
    */
    public static final int TABLE_CARD_PADDING_TOP_OPP = 20 ;
    public static final int TABLE_CARD_PADDING_TOP = TABLE_CARD_PADDING_TOP_OPP + CardGUI.HEIGHT_TABLE + HAND_CARDGAP;
    public static final int DICE_PADDING_TOP = 50;
    
    ArrayList<CardGUI> cardListGui = new ArrayList<CardGUI>();  
    ArrayList<CardGUI> handGUI = new ArrayList<CardGUI>(); 
    CardGUI discardGUI;
    CardGUI discardGUIOpponnent;
    ArrayList<CardGUI> tableGUI = new ArrayList<CardGUI>(); 
    ArrayList<CardGUI> tableOpponentGUI = new ArrayList<CardGUI>(); 
    
    Game game;
    CardSet cardSet;  
    Position hoverPosition;
    MapGUI mapGui;

    public CardSetsGUI(Game game, MapGUI mapGui){
        
        this.mapGui = mapGui;
        this.game = game;
        loadAllSets();
     
     }
    
    public void loadAllSets()
    
    {
        loadSet( game.getCurrentPlayer().getHand() );
        loadSet( game.getCurrentPlayer().getDiscardPile());
        loadSet( game.getCurrentPlayer().getTablePile());
        loadOpponentTable( game.getOpponentPlayer().getTablePile());
    }
            
    
    private void loadOpponentTable(CardSet cardSet)
    {
        tableOpponentGUI.clear();
        if(cardSet.size() > 0 )
            for(Card card : cardSet.getCardList())
            {
             tableOpponentGUI.add(new CardGUI(card));
            }
    }
    
    private synchronized void loadSet(CardSet cardSet) 
    {
        switch(cardSet.name){
            case "HAND"  :{
                handGUI.clear();
                if(cardSet.size() > 0 )
                    for(Card card : cardSet.getCardList())
                    {
                     handGUI.add(new CardGUI(card));
                    }
                break;
            }
            case "DISCARD"  :{
                if(cardSet.size() > 0 )
                 if(game.getCurrentPlayer().getDiscardPile().getLastCard(false)!=null)
                    discardGUI = new CardGUI(game.getCurrentPlayer().getDiscardPile().getLastCard(false));
                
                if(cardSet.size() > 0 )
                 if(game.getOpponentPlayer().getDiscardPile().getLastCard(false) != null)
                    discardGUIOpponnent = new CardGUI(game.getOpponentPlayer().getDiscardPile().getLastCard(false));
               break;
            }
            case "TABLE"  :{
                tableGUI.clear();
                if(cardSet.size() > 0 )
                for(Card card : cardSet.getCardList())
                {
                 tableGUI.add(new CardGUI(card));
                }
            break;
            }
        }
    }
  
    public int cardsLeftInSet() {
        return cardListGui.size();    
    }
    public CardGUI getCardByPosInSet(int cardPosition){
       return cardListGui.get(cardPosition);     
    }

    public String getCardNameByPosInSet(int cardPosition){
       return cardListGui.get(cardPosition).card.getCardName();     
    }
    public void paintOpponentHand(Graphics g, Game game )
    {
        CardSet opponentHand=game.getOpponentPlayer().getHand();
        for ( int i=0; i < opponentHand.getCardList().size(); i++) 
        {
            Card opponentCard = opponentHand.getCardList().get(i);
            CardGUI oppCardGUI = new CardGUI(opponentCard);
            if(game.showOpponentHand())
            g.drawImage(oppCardGUI.getImgFull(), HAND_CARDPADDINGLEFT+(CardGUI.WIDTH+HAND_CARDGAP)* i  ,
                    HAND_CARDPADDINGTOP, CardGUI.WIDTH, CardGUI.HEIGHT, null);  
            else 
            g.drawImage(oppCardGUI.getImgBackCover(), HAND_CARDPADDINGLEFT+(CardGUI.WIDTH+HAND_CARDGAP)* i  ,
                    HAND_CARDPADDINGTOP, CardGUI.WIDTH, CardGUI.HEIGHT, null);  
        }
    }
    
    private void paintDeadCard(Graphics g, int positionInSet, boolean overCard ){
        
        g.setColor(Color.RED);
        
        int cardPadTemp;
        if(!overCard)
            cardPadTemp = HAND_CARDPADDINGTOP;
        else
            cardPadTemp = HAND_CARDPADDINGTOP-HAND_CARDPADDELTA;
        
        int leftUpCornerX =  HAND_CARDPADDINGLEFT + HAND_LINEGAP +(CardGUI.WIDTH+HAND_CARDGAP) * positionInSet ;
        int leftUpCornerY =  cardPadTemp  + HAND_LINEGAP;
        int rightUpCornerX = HAND_CARDPADDINGLEFT + HAND_LINEGAP +(CardGUI.WIDTH) * (positionInSet + 1)  ;
        int rightUpCornerY = cardPadTemp  + CardGUI.HEIGHT - HAND_LINEGAP ;        
       
        
        g.drawLine(leftUpCornerX, leftUpCornerY, rightUpCornerX,  rightUpCornerY);
        g.drawLine(leftUpCornerX, rightUpCornerY, rightUpCornerX,  leftUpCornerY);
        
    }
    
    private void paintCardSelectionRectangle(Graphics g, int positionInSet, boolean isUpped)
    {
        g.setColor(Color.RED);
       
        int cardPadTemp;
        if(!isUpped)
            cardPadTemp = HAND_CARDPADDINGTOP;
        else
            cardPadTemp = HAND_CARDPADDINGTOP-HAND_CARDPADDELTA;
        
        int x1 =  HAND_CARDPADDINGLEFT + ( CardGUI.WIDTH+HAND_CARDGAP )* positionInSet + HAND_RECTANGLE_LINEGAP;
        int y1 =  cardPadTemp  + HAND_RECTANGLE_LINEGAP;
        int width  = CardGUI.WIDTH -2*HAND_RECTANGLE_LINEGAP  ;
        int height = CardGUI.HEIGHT - 2*HAND_RECTANGLE_LINEGAP ;       
        
        g.drawRect(x1, y1, width, height);

        
    }
    
    private void paintCard(Graphics g, Image drawingCard, int positionInSet, boolean overCard)
    {
        int cardPadTemp;
        
        if(!overCard)
            cardPadTemp = HAND_CARDPADDINGTOP;
        else
            cardPadTemp = HAND_CARDPADDINGTOP-HAND_CARDPADDELTA;
        
        g.drawImage(drawingCard, 
        HAND_CARDPADDINGLEFT+(CardGUI.WIDTH+HAND_CARDGAP)*  positionInSet,
        cardPadTemp,
        CardGUI.WIDTH, 
        CardGUI.HEIGHT, null); 
        
    }
    
    
     public void paintHand(Graphics g, Game game){   
       try{
       for (CardGUI drawingCard : handGUI ){
            Card card = drawingCard.getCard();
            boolean isUpped = (card.isSelected());
            int positionInSet = game.getCurrentPlayer().getHand().getPositionInSet(card); 

            paintCard(g, drawingCard.getImgFull(), positionInSet, isUpped);
            
            if(card.isSelected())
                paintCardSelectionRectangle(g, positionInSet, true);
            
            if(card.getType() == Card.UNIT)
            {
                Unit cardUnit = game.getUnitByCard(card);
                if(cardUnit.isEliminated())
                    paintDeadCard(g, positionInSet, isUpped );
            }
        }
        }
        catch (ConcurrentModificationException ex)
           {
               ex.printStackTrace();
           }
    }  

    public void paintTablePanel(Graphics g){
        paintPlayingCards(g);
        paintCombatInfo(g);
        paintTerrainDetails(g);
        paintInfoPanelText(g);
    }
    
    private void paintPlayingCards(Graphics g){
    
        if(tableGUI.size()>0){  
        for (int i=0; i<tableGUI.size(); i++){  

        g.drawImage(tableGUI.get(i).getImgFull(), 
                    HAND_CARDPADDINGLEFT + (CardGUI.WIDTH_TABLE+HAND_CARDGAP)*i, 
                    TABLE_CARD_PADDING_TOP, 
                    CardGUI.WIDTH_TABLE, 
                    CardGUI.HEIGHT_TABLE,
                    null);   
        
        paintDices(g,  tableGUI.get(i).getCard(), i, false);
        
        }
        
        }
     
    if(tableOpponentGUI.size()>0){  //paint NO CARD
        for (int i=0; i<tableOpponentGUI.size(); i++){  
            
        
        g.drawImage(tableOpponentGUI.get(i).getImgFull(), 
                    HAND_CARDPADDINGLEFT + (CardGUI.WIDTH+HAND_CARDGAP)*i, 
                    TABLE_CARD_PADDING_TOP_OPP, 
                    CardGUI.WIDTH_TABLE, 
                    CardGUI.HEIGHT_TABLE,
                    null);        
        
        paintDices(g,  tableOpponentGUI.get(i).getCard(), i, true);
        }
        }
    }
    
    private void paintDices(Graphics g, Card paintedCard, int cardPosition, boolean ifopp){

        int dicePaddingLeft = (int)(CardGUI.WIDTH_TABLE)*(cardPosition) + HAND_CARDGAP;
        int tablePaddingTop;
        
        if(ifopp)
            tablePaddingTop = TABLE_CARD_PADDING_TOP_OPP;
        else 
            tablePaddingTop = TABLE_CARD_PADDING_TOP;
        int i=0;
        
        if(paintedCard.getDices()!=null)
            for(Dice dice : paintedCard.getDices() ){
               
                DiceGUI diceGUI = new DiceGUI(dice);
                int x = dicePaddingLeft + (i) * (int)(DiceGUI.D6SQUARE_WIDTH*DiceGUI.SCALE_FACTOR_D6);
                int sizex = (int)(diceGUI.getImage().getWidth()* DiceGUI.SCALE_FACTOR_D6);
                int sizey = (int)(diceGUI.getImage().getHeight()*DiceGUI.SCALE_FACTOR_D6);
                
                g.drawImage(diceGUI.getImage(),  x + HAND_CARDGAP, tablePaddingTop + DICE_PADDING_TOP ,sizex, sizey , null);
                    i++;
                }
  
    }
    
     public void paintDiscard(Graphics g, boolean paintOpponent, Game game){
        CardGUI cardGui;
        if (paintOpponent==true){
            if(game.getOpponentPlayer().getDiscardPile().size()>0){
                cardGui=new CardGUI(game.getOpponentPlayer().getDiscardPile().getLastCard(false));
                 Image image = cardGui.getImgFull();
                
                 g.drawImage(image, 0  ,HAND_CARDPADDINGTOP
                    , CardGUI.WIDTH, CardGUI.HEIGHT, null); 
            }
            else{
                g.setColor(Color.white);
                g.setFont(new Font("Bookman Old Style", 1, 20));
                g.drawString("No Card",20,100);  
            }   
        }
        else{
            if(game.getCurrentPlayer().getDiscardPile().size()>0){
                
                cardGui=new CardGUI(game.getCurrentPlayer().getDiscardPile().getLastCard(false));
                Image image = cardGui.getImgFull();
                g.drawImage(image, 0  ,HAND_CARDPADDINGTOP
                    , CardGUI.WIDTH, CardGUI.HEIGHT, null); 
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
            drawLeft=game.getOpponentPlayer().getDrawPile().size();
        else 
            drawLeft=game.getOpponentPlayer().getDrawPile().size();

        g.setColor(Color.white);
        g.setFont(new Font("Bookman Old Style", 1, 50));        
        g.drawString(drawLeft.toString(),cardPaddingLeft,cardPaddingTop); 
    }
    private boolean isCursorOverCard(int cardPosition, int mouseX, int mouseY){
    
        int mouseYmin = CardSetsGUI.HAND_CARDPADDINGTOP;
        int mouseYmax = CardSetsGUI.HAND_CARDPADDINGTOP  + CardGUI.HEIGHT;
        int mouseXmin = CardSetsGUI.HAND_CARDPADDINGLEFT +(CardSetsGUI.HAND_CARDGAP*cardPosition) +  CardGUI.WIDTH*(cardPosition);
        int mouseXmax = CardSetsGUI.HAND_CARDPADDINGLEFT +(CardSetsGUI.HAND_CARDGAP*cardPosition)  + CardGUI.WIDTH*(cardPosition+1);
        return mouseY>mouseYmin && mouseY<mouseYmax && mouseX>mouseXmin && mouseX<mouseXmax;
    
    }
    public CardGUI getCardFromMousePosition(int mouseX, int mouseY){
        for (int i=0; i<game.getCurrentPlayer().getHand().size(); i++)
        {
            if(isCursorOverCard(i, mouseX, mouseY))
               return handGUI.get(i);
        }
        return  new CardGUI();
    } 
         
    @Override
    public void update(Observable o, Object arg) {
         String dialogType = (String) arg;
         switch(dialogType){
        
            case EventType.CARDS_DISCARDED:
                loadSet( game.getCurrentPlayer().getHand() );
                loadSet( game.getCurrentPlayer().getDiscardPile());
            break;
            case EventType.CARDS_DRAWNED:
                loadSet( game.getCurrentPlayer().getHand() );
                loadSet( game.getCurrentPlayer().getDiscardPile());
            break;
            case EventType.CARD_MOVED_TO_TABLE:
                loadSet( game.getCurrentPlayer().getHand() );
                loadSet( game.getCurrentPlayer().getTablePile());
                loadOpponentTable( game.getOpponentPlayer().getTablePile());
            break;
            case EventType.TABLE_CLEANED:
                loadSet( game.getCurrentPlayer().getDiscardPile());
                loadSet( game.getCurrentPlayer().getTablePile());
                loadOpponentTable( game.getOpponentPlayer().getTablePile());
            break; 
            case EventType.COMBAT_DICE_ROLLED:
                loadSet( game.getCurrentPlayer().getDiscardPile());
                loadSet( game.getCurrentPlayer().getTablePile());
                loadOpponentTable( game.getOpponentPlayer().getTablePile());
            break;
            
         }
    }

    private void paintTerrainDetails(Graphics g){
     if (getHoverPosition() != null) {
            TerrainGUI terrain = mapGui.getTerrainGuiAtPosition(hoverPosition);
            Unit unit = game.getUnitAtPosition(hoverPosition);
            if (unit != null) {
                UnitGUI unitGUI = new UnitGUI(unit);
                g.drawImage(unitGUI.getImg(), INFOPANEL_UNIT_X_OFFSET, INFOPANEL_UNIT_Y_OFFSET, 
                        (int) INFOPANEL_UNIT_SCALE * unitGUI.getImg().getWidth(), 
                        (int) INFOPANEL_UNIT_SCALE * unitGUI.getImg().getHeight(), 
                        null);
            }
            if (terrain != null) {
                g.drawImage(terrain.getImg(), INFOPANEL_TERRAIN_X_OFFSET, INFOPANEL_TERRAIN_Y_OFFSET, (int) (terrain.getImg().getWidth() * INFOPANEL_TERRAIN_SCALE), (int) (terrain.getImg().getHeight() * INFOPANEL_TERRAIN_SCALE), null);
                g.setColor(Color.RED);
                g.drawString(terrain.getTerrain().getTypeToString(), INFOPANEL_TERRAIN_X_OFFSET, INFOPANEL_DECRIPTION_Y_OFFSET + INFOPANEL_DECRIPTION_Y_GAP);
                g.drawString("Def Bonus: " + Integer.toString(terrain.getTerrain().getDefenceBonus()), INFOPANEL_TERRAIN_X_OFFSET, INFOPANEL_DECRIPTION_Y_OFFSET + INFOPANEL_DECRIPTION_Y_GAP * 2);
                g.drawString("Block LOS: " + (terrain.getTerrain().getBlockingLOS() ? "yes" : "no"), INFOPANEL_TERRAIN_X_OFFSET, INFOPANEL_DECRIPTION_Y_OFFSET + INFOPANEL_DECRIPTION_Y_GAP * 3);
                if (terrain.getTerrain().getType() == Terrain.HILL) {
                    g.drawString("Att Bon.vs non-Hill: 2", INFOPANEL_TERRAIN_X_OFFSET, INFOPANEL_DECRIPTION_Y_OFFSET + INFOPANEL_DECRIPTION_Y_GAP * 4);
                }
            }
        }
    
    
    }
    
    private void paintInfoPanelText(Graphics g){
    
        g.setColor(Color.white);
        g.setFont(new Font("Bookman Old Style", 1, 20));
        if (game.getInfoBarText() != null) {
            g.drawString(game.getInfoBarText(), INFOPANEL_TEXT_START_X, INFOPANEL_TEXT_START_Y);
        }
    }
    
    private void paintCombatInfo(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Bookman Old Style", 1, 15));
        if (!game.getCombat().getState().equals(Combat.State.COMBAT_NOT_INITIALIZED) && !game.getCombat().getState().equals(Combat.State.INITIALIZING_COMBAT)) {
            Combat combat = game.getCombat();
                g.drawString("Attack: " + combat.getAttackValue(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL + COMBAT_PANEL_Y_GAP);
                g.drawString("Defense: " + combat.getDefenceValue(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL + 2 * COMBAT_PANEL_Y_GAP);
            g.drawString("Def.Terrain: " + combat.getDefenseTerrain().getTypeToString(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL + 3 * COMBAT_PANEL_Y_GAP);
            g.drawString("Ter.Def.Bonus: " + combat.getDefenseBonus(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL + 4 * COMBAT_PANEL_Y_GAP);
            g.drawString("Att.Terrain: " + combat.getAttackTerrain().getTypeToString(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL + 5 * COMBAT_PANEL_Y_GAP);
            g.drawString("Ter.Att.Bonus: " + combat.getAttackBonus(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL + 6 * COMBAT_PANEL_Y_GAP);
            g.drawString("Leader Bonus: " + combat.getLeaderBonus(), START_X_COMBAT_PANEL, START_Y_COMBAT_PANEL + 7 * COMBAT_PANEL_Y_GAP);
        }
    }
    public Position getHoverPosition() {
        return hoverPosition;
    }

    public void setHoverPosition(Position hoverUnit) {
        this.hoverPosition = hoverUnit;
    }
         
         
}
