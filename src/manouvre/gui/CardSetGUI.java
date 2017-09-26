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
import java.util.Observable;
import java.util.Observer;
import manouvre.game.Card;
import manouvre.game.CardSet;
import manouvre.game.Game;
import static java.lang.Math.round;
import static java.lang.Math.round;

/**
 * Retrives card picture/image
 * @author Bartosz
 */

    public class CardSetGUI implements Observer{
   /*
        Hand pixel constantances
   */
    public static final int CARDPADDINGTOP = 40;
    public static final int CARDPADDINGLEFT = 10;
    public static final int GAP = 5;
    
    /*
    Table constans
    */
    
    public static final int TABLE_CARD_PADDING_TOP_OPP = 20 ;
    public static final int TABLE_CARD_PADDING_TOP = TABLE_CARD_PADDING_TOP_OPP + CardGUI.HEIGHT_TABLE + GAP;
    
    
   
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
                for(Card card : cardSet.cardList)
                {
                 tableOpponentGUI.add(new CardGUI(card));
                }
        
    }
    
    private void loadSet(CardSet cardSet) 
    {
        
        switch(cardSet.name){
            case "HAND"  :{
                handGUI.clear();
                if(cardSet.size() > 0 )
                    for(Card card : cardSet.cardList)
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
                for(Card card : cardSet.cardList)
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
    
    
     public void paintHand(Graphics g, Game game)                 
    {   
        CardSet hand=game.getCurrentPlayer().getHand();
        int CARDPADDINGTOPTemp=CARDPADDINGTOP;
        Integer j=0;
        if(!hand.selectionSeq.isEmpty())
        {
            Card card =hand.selectionSeq.get(hand.selectionSeq.size()-1);              
            j=hand.getPositionInSet(card); 
            int[] xPoints={CARDPADDINGLEFT+35+CardGUI.WIDTH*j+(GAP*j),CARDPADDINGLEFT+95+CardGUI.WIDTH*j+(GAP*j),CARDPADDINGLEFT+35+(95-35)/2+CardGUI.WIDTH*j+(GAP*j)};
            int[] yPoints={CARDPADDINGTOP+180,CARDPADDINGTOP+180,CARDPADDINGTOP+170};
            g.setColor(Color.white);
            g.setFont(new Font("Bookman Old Style", 1, 11));
                    if(game.getPhase()==Game.DISCARD){
                    g.drawString("This card will be visible",CARDPADDINGLEFT+CardGUI.WIDTH*j+(GAP*j)-10,41+190);
                    g.drawString("on the Discard Pile",CARDPADDINGLEFT+CardGUI.WIDTH*j+(GAP*j)+0,54+190); 
                    }
            g.fillPolygon(xPoints, yPoints, 3);
         }
       /*
        Order of placing card in array is the same as 
        */
        for ( int i=0; i < handGUI.size(); i++) 
        {
            CardGUI cardGUI = handGUI.get(i);
            Card card = cardGUI.getCard();
          
            if(     card.isMouseOverCard()
                    || card.isSelected()
                    && card.getAvailableForPhase(game)
                   
                    )
                    CARDPADDINGTOPTemp=CARDPADDINGTOP-20;
            else CARDPADDINGTOPTemp=CARDPADDINGTOP;
            
            g.drawImage(cardGUI.getImgFull(), CARDPADDINGLEFT+(CardGUI.WIDTH+GAP)* i  ,
                    CARDPADDINGTOPTemp, CardGUI.WIDTH, CardGUI.HEIGHT, null);  
        }
        
    }  

    public void paintTablePanel(Graphics g){
        
        if(tableGUI.size()>0){  //paint NO CARD
        for (int i=0; i<tableGUI.size(); i++){  
            
            
        g.drawImage(tableGUI.get(i).getImgFull(), 
                    CARDPADDINGLEFT + (CardGUI.WIDTH+GAP)*i, 
                    TABLE_CARD_PADDING_TOP, 
                    CardGUI.WIDTH_TABLE, 
                    CardGUI.HEIGHT_TABLE,
                    null);        
        
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
        
        }
        
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
        int x=35,y=40,w=195,h=300; //cropp image
        int width=round(w*CardGUI.SCALE_FACTOR), height=round(h*CardGUI.SCALE_FACTOR);
        int cardPaddingTop=20;
        int cardPaddingLeft=8;
        if (paintOpponent==true){
            if(game.getOpponentPlayer().getDiscardPile().size()>0){
                cardGui=new CardGUI(game.getOpponentPlayer().getDiscardPile().getLastCard(false));
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
            if(discardGUI != null){
                /*
                We take first top card from list
                */
                Image image = 
                cropImage(discardGUI.getImgFull(),x,y,w,h);
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
        for (int i=0; i<game.getCurrentPlayer().getHand().size(); i++){
            game.getCurrentPlayer().getHand().getCardByPosInSet(i).setMouseOverCard(false);//delete all selections first
        }
        Card cardOverMouse=getCardFromMousePosition(mouseX,mouseY);
        if(cardOverMouse!=null){
            cardOverMouse.setMouseOverCard(true);
        }
        else{
           // System.err.println("card null");
        }
    }
            
    @Override
    public void update(Observable o, Object arg) {
        loadSet( game.getCurrentPlayer().getHand() );
        loadSet( game.getCurrentPlayer().getDiscardPile());
        loadSet( game.getCurrentPlayer().getTablePile());
        
    }
         
         
}
