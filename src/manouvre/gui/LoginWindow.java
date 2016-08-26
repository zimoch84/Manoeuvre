/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import manouvre.network.client.Message;
import manouvre.network.client.SocketClient;

/**
 *
 * @author Piotr
 */
public class LoginWindow extends javax.swing.JFrame {

    /*
    Network variables
    */
    
    public SocketClient client;
    public int port;
    public String serverAddr, username, password;
    public Thread clientThread;
    
    
    
     
    /**
     * Creates new form WelcomeWindow
     */
    public LoginWindow() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialogBadLogin = new javax.swing.JDialog();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try{
                    Image bgImage =
                    ImageIO.read(
                        new File("resources\\backgrounds\\Manouvre.jpg"));

                    g.drawImage(bgImage, 0, 0, null);

                }
                catch(IOException ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
        };
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jUserTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPasswordField = new javax.swing.JPasswordField();
        jPanel3 = new javax.swing.JPanel();
        jCancelButton = new javax.swing.JButton();
        jOKButton = new javax.swing.JButton();

        javax.swing.GroupLayout jDialogBadLoginLayout = new javax.swing.GroupLayout(jDialogBadLogin.getContentPane());
        jDialogBadLogin.getContentPane().setLayout(jDialogBadLoginLayout);
        jDialogBadLoginLayout.setHorizontalGroup(
            jDialogBadLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialogBadLoginLayout.setVerticalGroup(
            jDialogBadLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(500, 500));
        setResizable(false);
        getContentPane().setLayout(new java.awt.FlowLayout());

        jPanel4.setLayout(new java.awt.GridLayout(4, 1));

        jPanel5.setPreferredSize(new java.awt.Dimension(366, 100));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 366, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel5);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("Username");
        jPanel1.add(jLabel1, new java.awt.GridBagConstraints());

        jUserTextField.setColumns(20);
        jUserTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jUserTextFieldActionPerformed(evt);
            }
        });
        jPanel1.add(jUserTextField, new java.awt.GridBagConstraints());

        jPanel4.add(jPanel1);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel2.setText("Password");
        jPanel2.add(jLabel2, new java.awt.GridBagConstraints());

        jPasswordField.setColumns(20);
        jPasswordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordFieldActionPerformed(evt);
            }
        });
        jPanel2.add(jPasswordField, new java.awt.GridBagConstraints());

        jPanel4.add(jPanel2);

        jCancelButton.setText("Cancel");
        jPanel3.add(jCancelButton);

        jOKButton.setText("OK");
        jOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOKButtonActionPerformed(evt);
            }
        });
        jPanel3.add(jOKButton);

        jPanel4.add(jPanel3);

        getContentPane().add(jPanel4);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginToServer(ActionEvent evt){
    username = jUserTextField.getText();
        password = Arrays.toString(jPasswordField.getPassword());
         
        try {
             client = new SocketClient(this);
            clientThread = new Thread(client);
            clientThread.start();
            
            Message msg = new Message("login", username, password, "SERVER");
            
            client.send(msg);
            
        } catch (IOException ex) {
            System.out.println("manouvre.gui.WelcomeWindow.loginToServer()" + ex.getMessage());
            Logger.getLogger(LoginWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
//        finally
//        {
//           client.closeThread(clientThread);
//        }
//        finally
//        {
//           client.closeThread(clientThread);
//        }
        
    }
    private void jPasswordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordFieldActionPerformed
        loginToServer(evt);
         
    }//GEN-LAST:event_jPasswordFieldActionPerformed

    private void jOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOKButtonActionPerformed
          loginToServer(evt);
    }//GEN-LAST:event_jOKButtonActionPerformed

    private void jUserTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jUserTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jUserTextFieldActionPerformed

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
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jCancelButton;
    private javax.swing.JDialog jDialogBadLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton jOKButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPasswordField jPasswordField;
    private javax.swing.JTextField jUserTextField;
    // End of variables declaration//GEN-END:variables
}
