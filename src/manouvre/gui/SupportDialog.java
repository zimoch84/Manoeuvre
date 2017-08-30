/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import manouvre.game.Card;
import manouvre.game.CardSet;
import manouvre.game.Game;
import manouvre.game.commands.CommandQueue;
import manouvre.game.interfaces.ClientInterface;
import manouvre.game.interfaces.Command;

/**
 *
 * @author Piotr
 */
public class SupportDialog extends javax.swing.JFrame {

    /**
     * Creates new form CardDialog
     */
    Command okCommand;
    Command cancelCommand;
    
    CardGUI playedCard;
    ArrayList<Card> defCardsPlayerd;
    CardSetGUI defCardsPlayedGui;
  
    ClientInterface client;
    CommandQueue cmdQueue;
    Game game;
    
    CardSet hand;
    int nrOfChosenCards=0;
    int numberOfAvailableSupplyingCards=0;

    
    int cropFrame=30;
        double resizeFactor=0.7;
        int cardPaddingLeftDef=20;
        int cardPaddingTopDef=10;
        int width=(int)((260-2*cropFrame)*resizeFactor);
        int height=(int)((375-2*cropFrame)*resizeFactor);
        int gapDef=10;
    int leader=0;
    int attackingUnits=0;
    int attackPoints=0;
    
    public SupportDialog() {
        initComponents();
    }

    public SupportDialog(CardGUI playedCard, ArrayList<Card> defCardsPlayerd, ClientInterface client, CommandQueue cmdQueue, Game game)  {
       
        this.playedCard = playedCard;
         this.defCardsPlayerd=defCardsPlayerd;
         /*
         TODO : remove this
         */
         //this.defCardsPlayedGui=new CardSetGUI(defCardsPlayerd);
        this.game = game;
        this.cmdQueue = cmdQueue;
        this.setUndecorated(true);
        this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        this.hand=game.getCurrentPlayer().getHand();
        initComponents();
        
        //textPane.setBackground(jPanel1.getBackground());
        //textPane.setForeground(jPanel1.getBackground());
        
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        
        
       
              
        setButtonVisibility();
        
        //textArea.setHorizontalAlignment(SwingConstants.CENTER);
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        this.setLocation(x, y);
        
        chooseCardsAvailableForSupport();
        
         jLabel1.setText(game.getCurrentPlayer().getName() + ", "+jLabel1.getText() );
       
        setTextAreas();  //defence and attack
        setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    
   private void setTextAreas(){
      
        setAttackPoints();
        setDeffensivePoints();
               
   }
   
    private void setButtonVisibility()
    {
            if(leader+attackingUnits>0)
            cancelButton.setEnabled(true);
            else cancelButton.setEnabled(false);
    }
            
    private void drawCard(Graphics g){
        
       int i=0;
        if(defCardsPlayedGui!=null){
            for (i=0; i<defCardsPlayedGui.cardsLeftInSet(); i++){  
                if(i%2==0){
                    Image image = defCardsPlayedGui.getCardByPosInSet(i).getImgSmall(cropFrame);
                    g.drawImage(image, cardPaddingLeftDef+(width-gapDef)*i, cardPaddingTopDef, width, height, null);
                }else{
                    Image image = defCardsPlayedGui.getCardByPosInSet(i).getImgSmall(cropFrame);
                    g.drawImage(image, cardPaddingLeftDef+(width-gapDef)*i, cardPaddingTopDef+gapDef, width, height, null);
                }
            }
        }
        switch(defCardsPlayedGui.cardsLeftInSet()){
            case 0:{
                g.setFont(new Font("Bookman Old Style", 1, 19));
                g.drawString("Oponent haven't played any defensive card!",jPanel2.getWidth()/2-210,jPanel2.getHeight()/2);
                jScrollBar1.setVisible(false);
                break;
            }
            case 4:{
                jScrollBar1.setVisible(true);
                jScrollBar1.setMaximum(70);
                break;
            }
            case 5:{
                jScrollBar1.setVisible(true);
                jScrollBar1.setMaximum(220);
                break;
            }
            default: jScrollBar1.setVisible(false);
        }
        
        if(playedCard!=null){
        g.drawImage(playedCard.getImgFull(),0,0,(int)(CardGUI.CARD_WIDTH * CardGUI.SCALE_FACTOR) ,
                
                (int) (CardGUI.CARD_HEIGHT * CardGUI.SCALE_FACTOR), null);
        }
        
    }
        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawCard(g);

            }
        }
        ;
        jScrollBar1 = new javax.swing.JScrollBar();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        defenceTxt = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        nrOfSupplyingCardsTxt = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        attackMinTxt = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        attackMaxTxt = new javax.swing.JTextField();
        thowDice = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.FlowLayout());

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Support!");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jScrollBar1.setMaximum(800);
        jScrollBar1.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        jScrollBar1.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                jScrollBar1AdjustmentValueChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 282, Short.MAX_VALUE)
                .addComponent(jScrollBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel1.setText("Opponent plays:");

        jButton1.setText("repaint");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel7.setText("Result:");

        defenceTxt.setEditable(false);
        defenceTxt.setForeground(new java.awt.Color(255, 51, 102));
        defenceTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        defenceTxt.setText("12");
        defenceTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defenceTxtActionPerformed(evt);
            }
        });

        jLabel9.setText("Choose from ");

        nrOfSupplyingCardsTxt.setEditable(false);
        nrOfSupplyingCardsTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nrOfSupplyingCardsTxt.setText("5");
        nrOfSupplyingCardsTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nrOfSupplyingCardsTxtActionPerformed(evt);
            }
        });

        jLabel10.setText("supplying cards");

        jLabel11.setForeground(new java.awt.Color(255, 51, 51));
        jLabel11.setText("Your attack min:");

        attackMinTxt.setEditable(false);
        attackMinTxt.setForeground(new java.awt.Color(255, 51, 51));
        attackMinTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        attackMinTxt.setText("11");
        attackMinTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attackMinTxtActionPerformed(evt);
            }
        });

        jLabel12.setForeground(new java.awt.Color(255, 51, 51));
        jLabel12.setText("Enemy Defence:");

        jLabel13.setForeground(new java.awt.Color(255, 51, 51));
        jLabel13.setText("Your attack max:");

        attackMaxTxt.setEditable(false);
        attackMaxTxt.setForeground(new java.awt.Color(255, 51, 51));
        attackMaxTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        attackMaxTxt.setText("11");
        attackMaxTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attackMaxTxtActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(defenceTxt))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(nrOfSupplyingCardsTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(attackMinTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(53, 53, 53))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(attackMaxTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(defenceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nrOfSupplyingCardsTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attackMinTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attackMaxTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        thowDice.setText("Thow Dice");
        thowDice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thowDiceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(thowDice, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(102, 102, 102)
                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(thowDice, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jScrollBar1AdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_jScrollBar1AdjustmentValueChanged
        // TODO add your handling code here:
        cardPaddingLeftDef=-evt.getValue();
        this.repaint();

    }//GEN-LAST:event_jScrollBar1AdjustmentValueChanged

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed

        if(cancelCommand != null){

            cmdQueue.storeAndExecuteAndSend(cancelCommand);

        }
        //use the same dialog as for defensive for now
        cmdQueue.storeAndExecuteAndSend(game.getCardCommandFactory().createMoveDefensiveCardsToTableCommand(game.getCardCommandFactory().getPickedDefendingCards()));
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        if(okCommand != null)   {
            /*
            Execute command locally and remotely
            */
            cmdQueue.storeAndExecuteAndSend(okCommand);

        }

        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void defenceTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defenceTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_defenceTxtActionPerformed

    private void nrOfSupplyingCardsTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nrOfSupplyingCardsTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nrOfSupplyingCardsTxtActionPerformed

    private void attackMinTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attackMinTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_attackMinTxtActionPerformed

    private void thowDiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thowDiceActionPerformed
      Command coc = game.getCardCommandFactory().createOutcomeCombatCommand();
        cmdQueue.storeAndExecuteAndSend(coc);
        this.dispose();
    }//GEN-LAST:event_thowDiceActionPerformed

    private void attackMaxTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attackMaxTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_attackMaxTxtActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SupportDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SupportDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SupportDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SupportDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SupportDialog().setVisible(true);
            }
        });
    }
    public void setOkCommand(Command okCommand) {
        this.okCommand = okCommand;
    }

    public void setCancelCommand(Command cancelCommand) {
        this.cancelCommand = cancelCommand;
    }
    
    public int getNrOfChosenCards() {
        return nrOfChosenCards;
    }

    public void setNrOfChosenCards(int nrOfChosenCards) {
        this.nrOfChosenCards = nrOfChosenCards;
         nrOfSupplyingCardsTxt.setText(Integer.toString(numberOfAvailableSupplyingCards-nrOfChosenCards));
    }
    
    private void chooseCardsAvailableForSupport(){
       
        CardSet hand=game.getCurrentPlayer().getHand();
           for(int i=0; i<hand.cardsLeftInSet();i++){
               if(hand.getCardByPosInSet(i).getAvailableForPhase(game))numberOfAvailableSupplyingCards++;
           }
          
           nrOfSupplyingCardsTxt.setText(Integer.toString(numberOfAvailableSupplyingCards));
    }
     public void setAttackPoints(){
            attackMinTxt.setText(Integer.toString(game.getCombat().getAttackValue()+game.getCardCommandFactory().getMinFromDices(false)));
            attackMaxTxt.setText(Integer.toString(game.getCombat().getAttackValue()+game.getCardCommandFactory().getMaxFromDices(false)));
          //  game.getCardCommandFactory().prepareDices(false);
        //    attackMinTxt.setText(Integer.toString(game.getCombat().getAttackValue()+game.getCardCommandFactory().getAllDices().size()));
         //   attackMaxTxt.setText(Integer.toString(game.getCombat().getAttackValue()+game.getCardCommandFactory().getMaxFromDices()));
    }
      public void setDeffensivePoints(){
           defenceTxt.setText(Integer.toString(game.getCombat().getDefenceValue()));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField attackMaxTxt;
    private javax.swing.JTextField attackMinTxt;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField defenceTxt;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JTextField nrOfSupplyingCardsTxt;
    private javax.swing.JButton okButton;
    private javax.swing.JButton thowDice;
    // End of variables declaration//GEN-END:variables
}
