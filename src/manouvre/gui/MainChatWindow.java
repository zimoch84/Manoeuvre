package manouvre.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import manouvre.game.Player;
import manouvre.network.client.Message;
import manouvre.network.client.SocketClient;
import manouvre.network.server.GameRoom;
import manouvre.game.interfaces.FrameInterface;
import static java.lang.Thread.sleep;

class GameRoomRenderer extends JLabel implements ListCellRenderer<GameRoom> {
 
    protected static TitledBorder focusBorder = new TitledBorder(LineBorder.createGrayLineBorder(),
      "title");

    protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
    
    @Override
    public Component getListCellRendererComponent(JList<? extends GameRoom> list, GameRoom room, int index,
        boolean isSelected, boolean cellHasFocus) {
          
        String code = room.toString();
        ImageIcon imageIcon = new ImageIcon("resources\\icons\\AUicon.jpg");
         
        setIcon(imageIcon);
        setText(code);
               
        if(cellHasFocus)
            setBackground(Color.BLUE) ;
                    else 
            setBackground(defaultRenderer.getBackground()) ;
               
        
        return this;
    }
     
}
class GameRoomListModel extends AbstractListModel{
    private  ArrayList<GameRoom> list;

    public GameRoomListModel(ArrayList<GameRoom> list) {
        this.list = list;
    }

    public GameRoomListModel() {
        super();
        list = new ArrayList<GameRoom>();
    }
    
    @Override
    public int getSize() {
        return list.size();
    }

    @Override
    public Object getElementAt(int index) {
        return list.get(index);
    }
    
    public void setList(ArrayList<GameRoom> list){
    
    this.list = list;
    
    fireContentsChanged(this, 0, getSize());
    }
    
    public void nullList()
            
    {
    this.list = null;
    }
    

}



public class MainChatWindow extends javax.swing.JFrame implements FrameInterface{

    public SocketClient client;
    public DefaultListModel model;

    
    Player player;
    
    
    
    Image bgImage;
    
    Thread roomListenerThread = null;
    RoomListener roomListener = null;
    
    //DefaultListModel<GameRoom> roomListModel;
    GameRoomListModel  gameRoomListModel;
    GameRoomRenderer gameRoomListRenderer;
        
    public MainChatWindow(SocketClient passSocket, Player player) throws IOException{
       
     
        
        client = passSocket;
        this.player  = player;
        gameRoomListModel = new GameRoomListModel();
        gameRoomListRenderer = new GameRoomRenderer();
        
        
        
        initComponents();
       
        roomList.setCellRenderer(gameRoomListRenderer);
        
        jPlayerTextField.setText(player.getName());
        this.setTitle("Manouvre");
            model.addElement("All");
            model.addElement(player.getName());
        userList.setSelectedIndex(0);
        bgImage = ImageIO.read( new File("resources\\backgrounds\\cossaks.jpg"));
          this.addWindowListener(new WindowListener() {

            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) { try{ client.send(new Message(Message.BYE, player.getName(), ".bye", "SERVER"));  }catch(Exception ex){} }
            @Override public void windowClosed(WindowEvent e) {}
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
    }
    

    public MainChatWindow() throws IOException {
        //this.roomListModel = new DefaultListModel<>();
        initComponents();
        this.setTitle("Manouvre");
        model.addElement("All");
        userList.setSelectedIndex(0);
         bgImage = ImageIO.read( new File("resources\\backgrounds\\cossaks.jpg"));
        
        this.addWindowListener(new WindowListener() {

            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) { try{ client.send(new Message(Message.BYE, player.getName(), ".bye", "SERVER"));}catch(Exception ex){} }
            @Override public void windowClosed(WindowEvent e) {}
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        
            }
    
    public boolean isWin32(){
        return System.getProperty("os.name").startsWith("Windows");
    }

    private void drawBackground(Graphics g){
        g.drawImage(bgImage, 0, 0, 653, 447,Color.red, null);
    
    }
    
    public void setRoomList(ArrayList<GameRoom> inList)
    {
     gameRoomListModel.setList(inList);      
     roomList.setModel(gameRoomListModel);
           
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator3 = new javax.swing.JSeparator();
        mainPanel = new javax.swing.JPanel()
        {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBackground(g);

            }
        }
        ;
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        reconnectButton = new javax.swing.JButton();
        jPlayerTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jSeparator1 = new javax.swing.JSeparator();
        jButton2 = new javax.swing.JButton();
        createRoomButton = new javax.swing.JButton();
        joinButton = new javax.swing.JButton();
        jSeparator4 = new javax.swing.JSeparator();
        chatRoomTabbedPanel = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainChat = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        messageTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        userList = new javax.swing.JList();
        sendButton = new javax.swing.JButton();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        roomList = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Host Address : ");

        jTextField1.setText("localhost");
        jTextField1.setEnabled(false);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Host Port : ");

        jTextField2.setEditable(false);
        jTextField2.setText("5002");

        reconnectButton.setText("Reconnect");

        jPlayerTextField.setText("zimoch");
        jPlayerTextField.setEnabled(false);

        jLabel3.setText("Password :");

        jLabel4.setText("Username :");

        jPasswordField1.setText("uka");
        jPasswordField1.setEnabled(false);

        jButton2.setText("Login");
        jButton2.setEnabled(false);

        createRoomButton.setText("Create Room");
        createRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createRoomButtonActionPerformed(evt);
            }
        });

        joinButton.setText("Join Room");
        joinButton.setEnabled(false);
        joinButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                joinButtonActionPerformed(evt);
            }
        });

        chatRoomTabbedPanel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chatRoomTabbedPanelStateChanged(evt);
            }
        });

        jPanel2.setOpaque(false);

        mainChat.setColumns(20);
        mainChat.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        mainChat.setRows(5);
        jScrollPane1.setViewportView(mainChat);

        jLabel5.setForeground(new java.awt.Color(255, 51, 0));
        jLabel5.setText("Message : ");

        messageTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                messageTextFieldActionPerformed(evt);
            }
        });

        userList.setModel((model = new DefaultListModel()));
        jScrollPane2.setViewportView(userList);

        sendButton.setText("Send Message ");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jSeparator5.setOpaque(true);

        jSeparator6.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator6.setOpaque(true);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(messageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendButton, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8))
                    .addComponent(jSeparator5)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator5, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(messageTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton)))
            .addComponent(jScrollPane2)
            .addComponent(jSeparator6)
        );

        chatRoomTabbedPanel.addTab("Chat", jPanel2);

        roomList.setModel(gameRoomListModel);
        roomList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        roomList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                roomListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(roomList);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        chatRoomTabbedPanel.addTab("Rooms", jPanel3);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addGap(0, 428, Short.MAX_VALUE)
                        .addComponent(createRoomButton)
                        .addGap(18, 18, 18)
                        .addComponent(joinButton))
                    .addComponent(chatRoomTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 629, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel1)
                                .addComponent(jLabel4))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPlayerTextField)
                                .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE))
                            .addGap(18, 18, 18)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel3))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                .addComponent(jPasswordField1))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(reconnectButton, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGap(43, 43, 43))
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addComponent(jSeparator1)
                            .addContainerGap()))))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(106, 106, 106)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(createRoomButton)
                    .addComponent(joinButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chatRoomTabbedPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(mainPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(reconnectButton))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jPlayerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel4)
                        .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 24, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(341, 341, 341)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        String msg = messageTextField.getText();
        String target = userList.getSelectedValue().toString();

        if(!msg.isEmpty() && !target.isEmpty()){
            messageTextField.setText("");
            client.send(new Message(Message.CHAT, player.getName(), msg, target));
        }
    }//GEN-LAST:event_sendButtonActionPerformed

    private void messageTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messageTextFieldActionPerformed
        String msg = messageTextField.getText();
        String target = userList.getSelectedValue().toString();

        if(!msg.isEmpty() && !target.isEmpty()){
            messageTextField.setText("");
            client.send(new Message(Message.CHAT, player.getName(), msg, target));
        }
    }//GEN-LAST:event_messageTextFieldActionPerformed

    private void createRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createRoomButtonActionPerformed
           
       
          /*
        Run CreateRoomWindow (name + password) 
          */
               player.setHost(true);
               java.awt.EventQueue.invokeLater(new Runnable() {
              public void run() {
                  CreateRoomWindow createRoom = new CreateRoomWindow(client, player, CreateRoomWindow.AS_HOST);
                  createRoom.setVisible(true);
              }
          });
               
               
        
    }//GEN-LAST:event_createRoomButtonActionPerformed

    private void roomListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_roomListValueChanged
        
        JList<GameRoom> roomList = (JList<GameRoom>) evt.getSource();
        
        GameRoom roomSelected = (GameRoom)roomList.getSelectedValue();
        
        if(roomSelected != null)
            
            joinButton.setEnabled(true);
        
        else
            joinButton.setEnabled(false);
            
        
    }//GEN-LAST:event_roomListValueChanged

    private void chatRoomTabbedPanelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chatRoomTabbedPanelStateChanged
        
        if ( chatRoomTabbedPanel.getSelectedIndex() == 1 )
        {
                if(roomListenerThread == null)
                {
                    roomListener = new  RoomListener();
                    roomListenerThread = new Thread (roomListener);
                    roomListenerThread.start();
                   System.out.println("manouvre.gui.MainChatWindow.chatRoomTabbedPanelStateChanged() Thread successfully started.");
                }   
                
        }       
         
        else 
             if(roomListenerThread != null)
            {   roomListener.terminate();
                roomListenerThread = null;
            System.out.println("manouvre.gui.MainChatWindow.chatRoomTabbedPanelStateChanged() Thread successfully stopped.");
              }
        
        
    }//GEN-LAST:event_chatRoomTabbedPanelStateChanged

    private void joinButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_joinButtonActionPerformed
   
                        /*
                            Sending guestPlayer object to server to join room 
                            as return we expect HostPlayer object from server
                         */
        
                      GameRoom selected = roomList.getSelectedValue(); 
                      Message msg = new  Message (Message.JOIN_ROOM, player.getName(), selected.toString(), "SERVER" );
                       //Addind currentPlayer to the list as first
                      player.setHost(false);
                      
                      msg.addPlayer(player);
                      
                      msg.addGameRoom(selected);
                      selected.setGuestSocketPortId(client.socket.getPort());
                     
                      
                      client.send(msg);
        
      
    }//GEN-LAST:event_joinButtonActionPerformed

    @Override
    public void printOnChat(String inString)    {
    
    mainChat.append(inString+ "\n");
    }
    
    
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch(Exception ex){
            System.out.println("Look & Feel exception");
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new MainChatWindow().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(MainChatWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    class RoomListener implements Runnable{

        private volatile boolean running = true;    
        
        public void terminate() {
            running = false;
        } 
        
        public void resume() {
            running = true;
        }
        
    @Override
    public void run() {
        Message msg = new Message (Message.GET_ROOM_LIST, player.getName(), "clientRequest", "SERVER");
         
        /*
        While we are selecting room tab send room list reqest to server 
        
        */
        while (running)
        {
       
            
            try {
                client.send(msg);
                /*
                Pause for 10 sec.
                */
                sleep(20000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MainChatWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        
        }
            
            
        
    }


}
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane chatRoomTabbedPanel;
    private javax.swing.JButton createRoomButton;
    public javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    public javax.swing.JPasswordField jPasswordField1;
    public javax.swing.JTextField jPlayerTextField;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    public javax.swing.JTextField jTextField1;
    public javax.swing.JTextField jTextField2;
    private javax.swing.JButton joinButton;
    public javax.swing.JTextArea mainChat;
    private javax.swing.JPanel mainPanel;
    public javax.swing.JTextField messageTextField;
    public javax.swing.JButton reconnectButton;
    private javax.swing.JList<GameRoom> roomList;
    public javax.swing.JButton sendButton;
    public javax.swing.JList userList;
    // End of variables declaration//GEN-END:variables
}
