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
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Observable;
import java.util.Observer;
import manouvre.events.EventType;
import manouvre.game.Card;
import manouvre.game.CardSet;
import manouvre.game.Game;
import manouvre.game.Dice;

/**
 * Retrives card picture/image
 * @author Bartosz
 */

    public class CardSetGUI implements Observer{
   /*
        Hand pixel constantances
   */
    public static final int CARDPADDINGTOP = 10;
    public static final int CARDPADDINGLEFT = 10;
    public static final int CARDPADDELTA = 20;
    public static final int GAP = 5;
    
    /*
    Table constans
    */
    
    public static final int TABLE_CARD_PADDING_TOP_OPP = 20 ;
    public static final int TABLE_CARD_PADDING_TOP = TABLE_CARD_PADDING_TOP_OPP + CardGUI.HEIGHT_TABLE + GAP;
    
    public static final int DICE_PADDING_TOP = 15;
    
   
    //private static final int ONETHIRDCARD = 30; //set during mouse move
    private static final int LIFTCARDIFSELECTEDBY = 20;//pixels if card selected

    
    ArrayList<CardGUI> cardListGui = new ArrayList<CardGUI>();  
    
    ArrayList<CardGUI> handGUI = new ArrayList<CardGUI>(); 
    CardGUI discardGUI;
    CardGUI discardGUIOpponnent;
    ArrayList<CardGUI> tableGUI = new ArrayList<CardGUI>(); 
    ArrayList<CardGUI> tableOpponentGUI = new ArrayList<CardGUI>(); 
    
    Game game;
    
    
    CardSet cardSet;  //decide which cardSet shall be processed
   // CardGUI backCover;

    public CardSetGUI(Game game)
             
     {
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
            g.drawImage(oppCardGUI.getImgFull(), CARDPADDINGLEFT+(CardGUI.WIDTH+GAP)* i  ,
                    CARDPADDINGTOP, CardGUI.WIDTH, CardGUI.HEIGHT, null);  
            else 
            g.drawImage(oppCardGUI.getImgBackCover(), CARDPADDINGLEFT+(CardGUI.WIDTH+GAP)* i  ,
                    CARDPADDINGTOP, CardGUI.WIDTH, CardGUI.HEIGHT, null);  
        }
    }
    
     public void paintHand(Graphics g, Game game)                 
    {   
       int cardPadTemp;
       
       try{
       for (CardGUI drawingCard : handGUI ){
            Card card = drawingCard.getCard();
             if(     card.isMouseOverCard()
                    || card.isSelected()
                   //&& card.canBePlayed(game)
                    )
                    cardPadTemp=CARDPADDINGTOP-CARDPADDELTA;
            else cardPadTemp=CARDPADDINGTOP;
            g.drawImage(drawingCard.getImgFull(), 
                    CARDPADDINGLEFT+(CardGUI.WIDTH+GAP)*  game.getCurrentPlayer().getHand().getPositionInSet(card) ,
                    cardPadTemp, 
                    CardGUI.WIDTH, 
                    CardGUI.HEIGHT, null);  
           }
       }
       catch (ConcurrentModificationException ex)
           {
               
               ex.printStackTrace();
           }
       
    }  

    public void paintTablePanel(Graphics g){
        
            if(tableGUI.size()>0){  
        for (int i=0; i<tableGUI.size(); i++){  
            
            
        g.drawImage(tableGUI.get(i).getImgFull(), 
                    CARDPADDINGLEFT + (CardGUI.WIDTH+GAP)*i, 
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
                    CARDPADDINGLEFT + (CardGUI.WIDTH+GAP)*i, 
                    TABLE_CARD_PADDING_TOP_OPP, 
                    CardGUI.WIDTH_TABLE, 
                    CardGUI.HEIGHT_TABLE,
                    null);        
        
        paintDices(g,  tableOpponentGUI.get(i).getCard(), i, true);
        
        }
        
        }
     
       
    }
    
    private void paintDices(Graphics g, Card paintedCard, int cardPosition, boolean ifopp){

        int dicePaddingLeft = (int)(CardGUI.WIDTH)*(cardPosition) + GAP;
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
                
                g.drawImage(diceGUI.getImage(),  x, tablePaddingTop + DICE_PADDING_TOP ,sizex, sizey , null);
                    i++;
                }
  
    }
    
    public void paintDefenceCardsOnTheTable(Graphics g){
        int cropFrame=30;
        double resizeFactor=0.4;
        int cardPaddingLeftDef=80;
        int width=(int)((260-2*cropFrame)*resizeFactor);
        int height=(int)((375-2*cropFrame)*resizeFactor);
        int gapDef=50;
       
        for (int i=0; i<tableOpponentGUI.size(); i++){  
                Image image = tableOpponentGUI.get(i).getImgSmall(cropFrame);
                g.drawImage(image, cardPaddingLeftDef+(width-gapDef)*i, 
                        CARDPADDINGTOP+gapDef*i, width, height, null);
            }
    }
    
     public void paintDiscard(Graphics g, boolean paintOpponent, Game game){
        CardGUI cardGui;
        //int x=35,y=40,w=195,h=300; //cropp image
        //int width=round(w*CardGUI.SCALE_FACTOR), height=round(h*CardGUI.SCALE_FACTOR);
        int cardPaddingTop=20;
        int cardPaddingLeft=8;
       
        if (paintOpponent==true){
            if(game.getOpponentPlayer().getDiscardPile().size()>0){
                cardGui=new CardGUI(game.getOpponentPlayer().getDiscardPile().getLastCard(false));
                 Image image = cardGui.getImgFull();
                
                 g.drawImage(image, 0  ,cardPaddingTop
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
                g.drawImage(image, 0  ,cardPaddingTop
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
   
    private BufferedImage cropImage(Image img, int x, int y, int width, int height){
        BufferedImage buffImage = (BufferedImage)img;
        return buffImage.getSubimage(x, y, width, height);
    }
         
    public Card getCardFromMousePosition(int mouseCoorX, int mouseCoorY){
      
        for (int i=0; i<game.getCurrentPlayer().getHand().size(); i++)
        {
            int mouseYmin = CardSetGUI.CARDPADDINGTOP;
            int mouseYmax = CardSetGUI.CARDPADDINGTOP  + CardGUI.HEIGHT;
            int mouseXmin = CardSetGUI.CARDPADDINGLEFT +(CardSetGUI.GAP*i) +  CardGUI.WIDTH*(i);
            int mouseXmax = CardSetGUI.CARDPADDINGLEFT +(CardSetGUI.GAP*i)  + CardGUI.WIDTH*(i+1);
            
            if(    
                   game.getCurrentPlayer().getHand().getCardByPosInSet(i).isMouseOverCard() ||
                   game.getCurrentPlayer().getHand().getCardByPosInSet(i).isSelected()
               )
                mouseYmin=mouseYmin-CardGUI.LIFTSELECTEDBY;
    
            if( mouseCoorY>mouseYmin && mouseCoorY<mouseYmax &&
                mouseCoorX>mouseXmin && mouseCoorX<mouseXmax)
               return game.getCurrentPlayer().getHand().getCardByPosInSet(i);
        }
        return (Card)null;
    } 

     public void setMouseOverCard(int mouseX, int mouseY){
        
        game.getCurrentPlayer().getHand().unselectMouseOverCard();
        
        Card cardOverMouse=getCardFromMousePosition(mouseX,mouseY);
        if(cardOverMouse!=null){
            cardOverMouse.setMouseOverCard(true);
        }

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
         
         
}
