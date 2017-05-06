/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import manouvre.game.Card;
import manouvre.game.CardSet;
import manouvre.game.Combat;
import static manouvre.game.Combat.DEFFENDER_TAKES_HIT;
import manouvre.game.Game;
import manouvre.game.Unit;
import manouvre.game.commands.CommandQueue;
import manouvre.game.interfaces.CardInterface;
import manouvre.game.interfaces.ClientInterface;
import manouvre.network.client.Message;
import manouvre.network.client.SocketClient;
import manouvre.game.interfaces.Command;

/**
 *
 * @author Piotr
 */
public class AttackDialog extends javax.swing.JFrame {

    Command okCommand;
    Command withdrawCommand;
    
    Card playedCard;
    Unit attackedUnit;
    
    String attackType;
    
    ClientInterface client;
    CommandQueue cmdQueue;
    Game game;
    int nrOfChosenCards;
    int numberOfAvailableDeffendingCards=0;
   
    
    public AttackDialog() {
        initComponents();
    }

    public AttackDialog(String attackType, Card playedCard, Unit attackedUnit, ClientInterface client, CommandQueue cmdQueue, Game game) {
        
        this.attackType = attackType;
        this.playedCard = playedCard;
        this.attackedUnit = attackedUnit;
        this.client = client;
        this.cmdQueue = cmdQueue;
        this.game = game;

        this.setUndecorated(true);
        this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        
        initComponents();
        
        //textPane.setBackground(jPanel1.getBackground());
        //textPane.setForeground(jPanel1.getBackground());
        
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                   
        setWithdrawButtonVisibility();
        setNrOfChosenCards(0);
        //textArea.setHorizontalAlignment(SwingConstants.CENTER);
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        this.setLocation(x, y);
        
        
        setTheFrameAccordingToAttackType();
        setAttackPoints();
        chooseCardsAvailableForDefence();
        setDeffensivePoints();
        labelTitle.setText(labelTitle.getText() + " " + attackType+"ED");
       
        
         
            
        
        setVisible(true);
        this.setAlwaysOnTop (true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    
    private void setTheFrameAccordingToAttackType(){
        if(attackType.equals("BOMBARD")){
            okButton.setText("OK");
            jLabel1.setEnabled(false); //choose from
            nrOfDefendingCardsTxt.setEnabled(false);
            jLabel2.setEnabled(false); //deffending cards
            attackTxt.setEnabled(false);
            jLabel5.setEnabled(false);
            jLabel6.setText("Enemy Bombard:");
            defenceTxt.setEnabled(false);
            cancelButton.setEnabled(false);
//                switch(game.getCombat().getAssaultOutcome()){
//                    case Combat.DEFFENDER_TAKES_HIT:{
//                        jLabel3.setEnabled(true);
//                        break;
//                    }
//                    default:{
//                        jLabel3.setForeground(Color.WHITE);
//                        jLabel3.setText("NO EFFEKT!");
//                        jLabel3.setEnabled(true);
//                    }
//                }
        }
        else{
             jLabel3.setText("Attack=");
             okButton.setText("DEFEND!");
        }
    }
    private void chooseCardsAvailableForDefence(){
       
        CardSet hand=game.getCurrentPlayer().getHand();
           for(int i=0; i<hand.cardsLeftInSet();i++){
               if(hand.getCardByPosInSet(i).getAvailableForPhase(game))numberOfAvailableDeffendingCards++;
           }
          
           nrOfDefendingCardsTxt.setText(Integer.toString(numberOfAvailableDeffendingCards));
    }
     private void setAttackPoints(){
           attackTxt.setText(Integer.toString(game.getCombat().getAttackValue()));
    }
      private void setDeffensivePoints(){
           defenceTxt.setText(Integer.toString(game.getCombat().getDefenceValue()));
    }
    private void setWithdrawButtonVisibility()
    {
        
        if(game.getCurrentPlayer().getHand().getCardByName("Withdraw", false)!=null)
         cancelButton.setEnabled(true);
    }
            
    
        

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        labelTitle = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawUnit(g);

            }
        }
        ;
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        attackTxt = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        nrOfDefendingCardsTxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        defenceTxt = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.FlowLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        okButton.setText("DEFEND!");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Withdraw");
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 159, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        labelTitle.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        labelTitle.setText("You have been");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 90, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 90, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel4.setText("Result:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 51, 0));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("YOU GOT HIT!");

        attackTxt.setEditable(false);
        attackTxt.setForeground(new java.awt.Color(255, 51, 102));
        attackTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        attackTxt.setText("12");
        attackTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attackTxtActionPerformed(evt);
            }
        });

        jLabel1.setText("Choose from ");

        nrOfDefendingCardsTxt.setEditable(false);
        nrOfDefendingCardsTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nrOfDefendingCardsTxt.setText("5");
        nrOfDefendingCardsTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nrOfDefendingCardsTxtActionPerformed(evt);
            }
        });

        jLabel2.setText("defending cards");

        jLabel5.setForeground(new java.awt.Color(255, 51, 51));
        jLabel5.setText("Your defence:");

        defenceTxt.setEditable(false);
        defenceTxt.setForeground(new java.awt.Color(255, 51, 51));
        defenceTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        defenceTxt.setText("11");
        defenceTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defenceTxtActionPerformed(evt);
            }
        });

        jLabel6.setForeground(new java.awt.Color(255, 51, 51));
        jLabel6.setText("Enemy Attack:");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(attackTxt))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(defenceTxt))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nrOfDefendingCardsTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(53, 53, 53))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(23, 23, 23))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(4, 4, 4)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(attackTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nrOfDefendingCardsTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(defenceTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        jButton1.setText("refresh..");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(cancelButton)
                        .addGap(34, 34, 34)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(labelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(21, 21, 21))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(75, 77, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(labelTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton)
                    .addComponent(jButton1))
                .addContainerGap())
        );

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
         if(okCommand != null)   {
            /*
            Execute command locally and remotely
            */
            cmdQueue.storeAndExecuteAndSend(okCommand);
            
        }
        
        //if(attackType.equals("BOMBARD")) this.dispose();
      //         else cmdQueue.storeAndExecuteAndSend(game.getCardCommandFactory().createMoveDefensiveCardsToTableCommand(game.getCardCommandFactory().getPickedDefendingCards()));
        this.dispose();
        
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        
        
        if(withdrawCommand != null){
        
            cmdQueue.storeAndExecuteAndSend(withdrawCommand);
            
        }
        
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void attackTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attackTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_attackTxtActionPerformed

    private void nrOfDefendingCardsTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nrOfDefendingCardsTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nrOfDefendingCardsTxtActionPerformed

    private void defenceTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defenceTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_defenceTxtActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

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
            java.util.logging.Logger.getLogger(AttackDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AttackDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AttackDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AttackDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new AttackDialog().setVisible(true);
            }
        });
    }
    public void setOkCommand(Command okCommand) {
        this.okCommand = okCommand;
    }

    public void setWithdrawCommand(Command withdrawCommand) {
        this.withdrawCommand = withdrawCommand;
    }

    private void drawCard(Graphics g){
    CardGUI card = new CardGUI(playedCard);
        g.drawImage(card.getImgFull(),0,0, 
                (int) (card.getImgFull().getWidth(this) * CardGUI.SCALE_FACTOR),
                (int)(card.getImgFull().getHeight(this) * CardGUI.SCALE_FACTOR)
                , null);
        
    }
    
    private void drawUnit(Graphics g){
    UnitGUI unit = new UnitGUI(attackedUnit);
         g.drawImage(unit.getImg(),0,0, 
                MapGUI.PIECE_WIDTH
               ,MapGUI.PIECE_HEIGHT, null);
    }
     public int getNrOfChosenCards() {
        return nrOfChosenCards;
    }

    public void setNrOfChosenCards(int nrOfChosenCards) {
        this.nrOfChosenCards = nrOfChosenCards;
         nrOfDefendingCardsTxt.setText(Integer.toString(numberOfAvailableDeffendingCards-nrOfChosenCards));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField attackTxt;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField defenceTxt;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel labelTitle;
    private javax.swing.JTextField nrOfDefendingCardsTxt;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
