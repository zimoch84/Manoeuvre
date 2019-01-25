package manouvre.network.server;

import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

public class ServerFrame extends javax.swing.JFrame implements Observer{

    public GameServer server;
    public Thread serverThread;
    public String filePath = "Data.xml";
    public JFileChooser fileChooser;
    
    public ServerFrame() {
        initComponents();     
        loggerTextArea.setEditable(false);
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        startServerButton = new javax.swing.JButton();
        scrollArea = new javax.swing.JScrollPane();
        loggerTextArea = new javax.swing.JTextArea();
        connectionInfoPanel = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawServerInfo(g);

            }
        }
        ;

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("jServer");

        startServerButton.setText("Start Server");
        startServerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startServerButtonActionPerformed(evt);
            }
        });

        loggerTextArea.setColumns(20);
        loggerTextArea.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        loggerTextArea.setRows(5);
        scrollArea.setViewportView(loggerTextArea);

        javax.swing.GroupLayout connectionInfoPanelLayout = new javax.swing.GroupLayout(connectionInfoPanel);
        connectionInfoPanel.setLayout(connectionInfoPanelLayout);
        connectionInfoPanelLayout.setHorizontalGroup(
            connectionInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 263, Short.MAX_VALUE)
        );
        connectionInfoPanelLayout.setVerticalGroup(
            connectionInfoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(startServerButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(scrollArea, javax.swing.GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(connectionInfoPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(startServerButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollArea, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                    .addComponent(connectionInfoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startServerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startServerButtonActionPerformed
        server = new GameServer(this);
        startServerButton.setEnabled(false); 
    }//GEN-LAST:event_startServerButtonActionPerformed

    
    public static void main(String args[]) {

        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception ex){
            System.out.println("Look & Feel Exception");
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerFrame().setVisible(true);
            }
        });
    }
    
    
    private void drawServerInfo(Graphics g){
    
        int startX = 0;
        int startY = 0;
        int gap = 10;
        if(server !=null)
        if(server.clients !=null)
        for(int i=0 ; i< 10 ; i++){
        
            if(server.clients[i] != null) 
            
                g.drawString("Socket "  + server.clients[i].clientServerSocket.toString(), startX, startY + i*gap);
            
                    
        }
        
        
    
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel connectionInfoPanel;
    public javax.swing.JTextArea loggerTextArea;
    private javax.swing.JScrollPane scrollArea;
    private javax.swing.JButton startServerButton;
    // End of variables declaration//GEN-END:variables

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }
}
