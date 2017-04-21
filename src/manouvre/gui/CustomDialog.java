/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import manouvre.game.Game;
import manouvre.game.commands.CommandQueue;
import manouvre.game.interfaces.ClientInterface;
import manouvre.network.client.Message;
import manouvre.network.client.SocketClient;
import manouvre.game.interfaces.CommandInterface;

/**
 *
 * @author Piotr
 */
public class CustomDialog extends javax.swing.JFrame {

    /**
     * Creates new form CustomDialog
     */
    
    public static final int OK_CANCEL_TYPE = 1;
    public static final int CONFIRMATION_TYPE = 2;
    public static final int YES_NO_TYPE = 3;
    public static final int YES_NO_UNDO_TYPE = 4;
    public static final int YES_NO_WITH_CARD = 5;
    
    int dialogType;
    boolean executeOK;
    
    CommandInterface okCommand;
    CommandInterface cancelCommand;
    
    String infoText;
    
    ClientInterface client;
    CommandQueue cmdQueue;
    Game game;
    
    public CustomDialog() {
        initComponents();
    }

    public CustomDialog(int dialogType, String infoText) {
        this.dialogType = dialogType;
        this.infoText = infoText;
        
        
        this.setUndecorated(true);
        this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        
        initComponents();
        
        //textPane.setBackground(jPanel1.getBackground());
        //textPane.setForeground(jPanel1.getBackground());
        textPane.setBackground(Color.BLACK);
        StyledDocument doc = textPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        textPane.setOpaque(false);
        textPane.setText(infoText);
       
              
        setButtonVisibility();
        
        //textArea.setHorizontalAlignment(SwingConstants.CENTER);
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        // Determine the new location of the window
        int w = this.getSize().width;
        int h = this.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        this.setLocation(x, y);
       
        setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
   
    public CustomDialog(int dialogType, String infoText,  ClientInterface client, Game game){
        this(dialogType, infoText);
       this.client = client;
       this.game = game;
        
    }
    
    public CustomDialog(int dialogType, String infoText,  CommandQueue cmd, Game game){
       this(dialogType, infoText);
       this.game = game;
       this.cmdQueue = cmd;
        
    }
    
    private void setButtonVisibility()
    {
        switch(dialogType){
            case OK_CANCEL_TYPE: 
                okButton.setVisible(true);
                cancelButton.setVisible(true);
                break;  
            case CONFIRMATION_TYPE :
                
                okButton.setVisible(true);
                cancelButton.setVisible(false);
                break;
            case YES_NO_TYPE: 
                okButton.setVisible(true);
                okButton.setLabel("Yes");
                cancelButton.setVisible(true);
                cancelButton.setLabel("No");
                break;
            case YES_NO_UNDO_TYPE: 
                okButton.setVisible(true);
                okButton.setLabel("Yes");
                cancelButton.setVisible(true);
                cancelButton.setLabel("No");
                break;    
            
                       
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

        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        textPane = new javax.swing.JTextPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.FlowLayout());

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(textPane, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addGap(148, 148, 148)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(63, Short.MAX_VALUE)
                .addComponent(textPane, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
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
        
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        
        
        if(cancelCommand != null){
        
            cmdQueue.storeAndExecuteAndSend(cancelCommand);
            
        }
        else if (dialogType == YES_NO_UNDO_TYPE)
            
        {
            cmdQueue.undoCommand(cancelCommand);
                   }
            
            
        
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

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
            java.util.logging.Logger.getLogger(CustomDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CustomDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CustomDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CustomDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CustomDialog().setVisible(true);
            }
        });
    }
    public void setOkCommand(CommandInterface okCommand) {
        this.okCommand = okCommand;
    }

    public void setCancelCommand(CommandInterface cancelCommand) {
        this.cancelCommand = cancelCommand;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton okButton;
    private javax.swing.JTextPane textPane;
    // End of variables declaration//GEN-END:variables
}
