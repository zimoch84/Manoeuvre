/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import manouvre.network.client.Message;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import static java.lang.Math.abs;
import static java.lang.Math.round;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.text.DefaultCaret;
import manouvre.game.CardSet;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.game.Position;
import manouvre.game.Terrain;
import manouvre.game.Unit;
import manouvre.game.interfaces.PositionInterface;
import manouvre.game.interfaces.TerrainInterface;
import manouvre.network.client.SocketClient;
import static java.lang.Math.round;


/**
 *
 * @author Piotr
 */
public class GameWindow extends javax.swing.JFrame {

    /*
    Network variables
    */
    
    public SocketClient client;
    public int port;
    public String serverAddr,  password;
    public Thread clientThread;
    public Player player;
    
    private int handMouseCoorX,handMouseCoorY;
    private int handMouseCoorXdeaf=0;
    private int handMouseCoorYdeaf=0;
    int mouseClickedOnHand=0;
    
    CardSetGUI cardSetsGUI;
    

    /*
    Object hold whole game logically
    */
    Game game;

    GameGUI gameGui;
   
    
    
 
    public GameWindow(SocketClient passSocket, Player player) throws IOException{
        
        
        client = passSocket;
        game = new Game(player);
        gameGui = new GameGUI(game);
        
        /*
        game.generateMap();
        game.setCurrentPlayer(player);
        */
   
        initComponents();
        
        jPlayer.setText("Player : " + player.getName());
      

        this.addWindowListener(new WindowListener() {

            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) { try{ client.send(new Message("message", game.getCurrentPlayer().getName(), ".bye", "SERVER")); clientThread.stop();  }catch(Exception ex){} }
            @Override public void windowClosed(WindowEvent e) {}
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        
     DefaultCaret caret = (DefaultCaret)chatTextArea.getCaret();
     caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }
    
    /**
     * Creates new form ClientUI
     */
    public GameWindow() throws IOException {
        initComponents();
        game=new Game(new Player("Bartek"));
        gameGui = new GameGUI(game); //TEMP
        
        gameGui.getGame().getMap(); //TEMP
       
        gameGui.mapGui = new MapGUI(game.getMap());
        this.addWindowListener(new WindowListener() {

            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) { try{ client.send(new Message("message",game.getCurrentPlayer().getName(), ".bye", "SERVER")); clientThread.stop();  }catch(Exception ex){} }
            @Override public void windowClosed(WindowEvent e) {}
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        
        try{
                client = new SocketClient(this);
                clientThread = new Thread(client);
                clientThread.start();
                //client.send(new Message("test", "testUser", "testContent", "SERVER"));
            }
            catch(Exception ex){
                chatTextArea.append("[Application > Me] : Server not found\n");
            }

    }


    public Game getGame() {
         return game;
     }

     public void setGame(Game game) {
         this.game = game;
     }

    private void drawMap(Graphics g )                   
    {
        gameGui.drawMap(g);
     
    }
 
    private void drawCard(Graphics g )                 
    {   
        gameGui.drawCard(g,  handMouseCoorX, handMouseCoorY, mouseClickedOnHand); 
        mouseClickedOnHand=0; 
    }
    
    private void drawDiscard(Graphics g )                 
    {   
        gameGui.drawDiscard(g); 
      
    }
    
    private void drawDrawLeft(Graphics g ) {
        gameGui.drawDrawLeft(g); 
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        mainMapPanel = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawMap(g);

            }
        }
        ;
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        rightSidePanel = new javax.swing.JPanel();
        currentPlayerPanel = new javax.swing.JPanel();
        gameLogPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPlayer = new javax.swing.JLabel();
        buttonsPanel = new javax.swing.JPanel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        moveButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        chatPanel = new javax.swing.JPanel();
        sendMessageButton = new javax.swing.JButton();
        sendText = new javax.swing.JTextField();
        bottomPanel = new javax.swing.JPanel();
        playerHandPanel = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawCard(g);

            }
        }
        ;
        jLabel1 = new javax.swing.JLabel();
        discardPanel = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawDiscard(g);

            }
        }
        ;
        jLabel2 = new javax.swing.JLabel();
        playerDrawLeftPanel = new javax.swing.JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                drawDrawLeft(g);

            }
        }
        ;
        jLabel3 = new javax.swing.JLabel();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        mainMapPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseClicked(evt);
            }
        });

        jLabel4.setText("Map");

        javax.swing.GroupLayout mainMapPanelLayout = new javax.swing.GroupLayout(mainMapPanel);
        mainMapPanel.setLayout(mainMapPanelLayout);
        mainMapPanelLayout.setHorizontalGroup(
            mainMapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainMapPanelLayout.createSequentialGroup()
                .addComponent(jLabel4)
                .addGap(0, 500, Short.MAX_VALUE))
        );
        mainMapPanelLayout.setVerticalGroup(
            mainMapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainMapPanelLayout.createSequentialGroup()
                .addComponent(jLabel4)
                .addContainerGap(545, Short.MAX_VALUE))
        );

        jLabel6.setText("Score:");

        jLabel7.setText("Game Turn:");

        jLabel8.setText("Active Player:");

        javax.swing.GroupLayout gameLogPanelLayout = new javax.swing.GroupLayout(gameLogPanel);
        gameLogPanel.setLayout(gameLogPanelLayout);
        gameLogPanelLayout.setHorizontalGroup(
            gameLogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gameLogPanelLayout.createSequentialGroup()
                .addGroup(gameLogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addGap(0, 9, Short.MAX_VALUE))
        );
        gameLogPanelLayout.setVerticalGroup(
            gameLogPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gameLogPanelLayout.createSequentialGroup()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPlayer.setText("Player");

        javax.swing.GroupLayout currentPlayerPanelLayout = new javax.swing.GroupLayout(currentPlayerPanel);
        currentPlayerPanel.setLayout(currentPlayerPanelLayout);
        currentPlayerPanelLayout.setHorizontalGroup(
            currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPlayer)
                .addContainerGap(58, Short.MAX_VALUE))
            .addGroup(currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(gameLogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        currentPlayerPanelLayout.setVerticalGroup(
            currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                .addComponent(jPlayer)
                .addGap(0, 85, Short.MAX_VALUE))
            .addGroup(currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                    .addGap(21, 21, 21)
                    .addComponent(gameLogPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(22, Short.MAX_VALUE)))
        );

        jButton8.setText("Discard");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Restore");
        jButton9.setEnabled(false);

        jButton7.setText("Draw");
        jButton7.setEnabled(false);

        jButton6.setText("Bombard");
        jButton6.setEnabled(false);

        jButton5.setText("Volley");
        jButton5.setEnabled(false);

        jButton4.setText("Ambush");
        jButton4.setEnabled(false);

        jButton3.setText("Assault");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        moveButton.setText("Move");
        moveButton.setEnabled(false);
        moveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setAutoscrolls(true);

        chatTextArea.setColumns(20);
        chatTextArea.setRows(5);
        chatTextArea.setText("Chat");
        jScrollPane1.setViewportView(chatTextArea);

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(moveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
            .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(buttonsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButton7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton8, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGroup(buttonsPanelLayout.createSequentialGroup()
                            .addComponent(jButton3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton4)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton6))
                        .addComponent(jButton9))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonsPanelLayout.createSequentialGroup()
                .addContainerGap(82, Short.MAX_VALUE)
                .addComponent(moveButton)
                .addGap(75, 75, 75)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(buttonsPanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jButton8)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jButton7)
                    .addGap(48, 48, 48)
                    .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton3)
                        .addComponent(jButton4)
                        .addComponent(jButton5)
                        .addComponent(jButton6))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jButton9)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        sendMessageButton.setText("Send");
        sendMessageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendMessageButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout chatPanelLayout = new javax.swing.GroupLayout(chatPanel);
        chatPanel.setLayout(chatPanelLayout);
        chatPanelLayout.setHorizontalGroup(
            chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(sendText, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sendMessageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );
        chatPanelLayout.setVerticalGroup(
            chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPanelLayout.createSequentialGroup()
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendMessageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout rightSidePanelLayout = new javax.swing.GroupLayout(rightSidePanel);
        rightSidePanel.setLayout(rightSidePanelLayout);
        rightSidePanelLayout.setHorizontalGroup(
            rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightSidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rightSidePanelLayout.createSequentialGroup()
                        .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(chatPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(rightSidePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(currentPlayerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(318, Short.MAX_VALUE)))
        );
        rightSidePanelLayout.setVerticalGroup(
            rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rightSidePanelLayout.createSequentialGroup()
                .addContainerGap(196, Short.MAX_VALUE)
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(rightSidePanelLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(currentPlayerPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(318, Short.MAX_VALUE)))
        );

        playerHandPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        playerHandPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                playerHandPanelMouseMoved(evt);
            }
        });
        playerHandPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                playerHandPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                playerHandPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                playerHandPanelMouseExited(evt);
            }
        });

        jLabel1.setText("Hand");

        javax.swing.GroupLayout playerHandPanelLayout = new javax.swing.GroupLayout(playerHandPanel);
        playerHandPanel.setLayout(playerHandPanelLayout);
        playerHandPanelLayout.setHorizontalGroup(
            playerHandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(playerHandPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(705, Short.MAX_VALUE))
        );
        playerHandPanelLayout.setVerticalGroup(
            playerHandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(playerHandPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(241, Short.MAX_VALUE))
        );

        discardPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setText("Discard");

        javax.swing.GroupLayout discardPanelLayout = new javax.swing.GroupLayout(discardPanel);
        discardPanel.setLayout(discardPanelLayout);
        discardPanelLayout.setHorizontalGroup(
            discardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(discardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(120, Short.MAX_VALUE))
        );
        discardPanelLayout.setVerticalGroup(
            discardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(discardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(245, Short.MAX_VALUE))
        );

        playerDrawLeftPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setText("Draw left");

        javax.swing.GroupLayout playerDrawLeftPanelLayout = new javax.swing.GroupLayout(playerDrawLeftPanel);
        playerDrawLeftPanel.setLayout(playerDrawLeftPanelLayout);
        playerDrawLeftPanelLayout.setHorizontalGroup(
            playerDrawLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(playerDrawLeftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(59, Short.MAX_VALUE))
        );
        playerDrawLeftPanelLayout.setVerticalGroup(
            playerDrawLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(playerDrawLeftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(245, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bottomPanelLayout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(playerHandPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(discardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(playerDrawLeftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bottomPanelLayout.createSequentialGroup()
                        .addComponent(playerHandPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(discardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(playerDrawLeftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(mainMapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rightSidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5)
                        .addGap(120, 120, 120))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rightSidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(mainMapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentHidden

    private void mainMapPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainMapPanelMouseClicked
                int x = evt.getPoint().x;
		int y = evt.getPoint().y;
		                
                if(! gameGui.mapGui.isUnitSelected() )
		for(TerrainGUI terrainGUI: gameGui.mapGui.getTerrainsGUI())  
                {
                       
                       terrainGUI.setSelected(false);
                       //game.getMap().getTileAtIndex( terrainGUI.getPos().getX(), terrainGUI.getPos().getY() ).setSelected(false);
     			if(mouseOverPiece(terrainGUI,x,y))
                        {   
  				terrainGUI.setSelected(true);
                                Position selectedPosition = terrainGUI.getPos();
                                
                                if(game.checkUnitAtPosition(selectedPosition) ) {
                                  
                                    gameGui.mapGui.setUnitSelected(true);
                                    getUnitGuiOnMapGui(selectedPosition).setSelected(true);
                                    
                                }
                                
                                //game.getMap().getTileAtIndex(terrainGUI.getPos().getX(), terrainGUI.getPos().getY()).setSelected(true);
                                this.repaint();
				
                        }
                   
                    }
                /*
                If unit is selected find which unit to move and move into 
                /*
                If unit is selected find which unit to move and move into 
                /*
                If unit is selected find which unit to move and move into 
                /*
                If unit is selected find which unit to move and move into 
                */
                else  {
                    Unit selectedUnit = gameGui.getSelectedUnit().getUnit();
                    Position clickedPosition = new Position(  PositionInterface.convertMouseXToX(x)   , PositionInterface.convertMouseYToY(y)) ;
                    
                    if(!selectedUnit.getPosition().equals(clickedPosition))
                    {
                    System.out.println("manouvre.gui.ClientUI.mainMapPanelMouseClicked().clickedPosition :" + clickedPosition) ;
                    
                    ArrayList<Position> movePositions =                 
                    game.getPossibleMovement(selectedUnit);
                    
                    for(Position checkPosition: movePositions){
                    
                        if(checkPosition.equals(clickedPosition))
                        {
                        
                            //Move in game and GUI
                            game.moveUnit(selectedUnit, clickedPosition);
                            
                             selectedUnit.setPos(clickedPosition);
                            //Unselect all
                            gameGui.unselectAllUnits();
                            //exit loop
                            repaint();
                            break;
                        }      
                    }
                    }
                    /*
                    Clicking on the same unit - deselects it.
                    */
                    else 
                    {
                        gameGui.unselectAllUnits();
                    repaint();
                    }
                    
                    
                    
                    
                   // game.moveUnit(  , newPosition);
                    
                }
                    
                    
        
        
        
    }//GEN-LAST:event_mainMapPanelMouseClicked
    private UnitGUI getUnitGuiOnMapGui(Position position){
    
           for(UnitGUI unitSearch: gameGui.unitsGui){
        
            if(unitSearch.getUnit().getPos().equals(position))
            {
                return unitSearch;
              }
            
        
        }
              
        return null;
    
    }
    
    
    
    
    private void moveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_moveButtonActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void sendMessageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendMessageButtonActionPerformed
        String msg = sendText.getText();
        String target = "All";

        if(!msg.isEmpty() && !target.isEmpty()){
            sendText.setText("");
            client.send(new Message("message", game.getCurrentPlayer().getName(), msg, target));
        }
    }//GEN-LAST:event_sendMessageButtonActionPerformed

    private void playerHandPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseClicked
     
                mouseClickedOnHand=1;       
                this.repaint();
                  
    }//GEN-LAST:event_playerHandPanelMouseClicked

    private void playerHandPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseEntered
         // TODO add your handling code here:
    }//GEN-LAST:event_playerHandPanelMouseEntered

    private void playerHandPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseExited
         // TODO add your handling code here:
    }//GEN-LAST:event_playerHandPanelMouseExited

    private void playerHandPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseMoved
                int deafband=20; //repaint after mouse change of
                
                handMouseCoorX = evt.getPoint().x;
		handMouseCoorY = evt.getPoint().y;
                if(abs(handMouseCoorX-handMouseCoorXdeaf)>deafband){
                    handMouseCoorXdeaf=handMouseCoorX;
                    this.repaint();
                }
              
                if(abs(handMouseCoorY-handMouseCoorYdeaf)>deafband){
                    handMouseCoorYdeaf=handMouseCoorY; 
                    this.repaint();
                }
                
                
                //System.out.println("x:" + handMouseCoorX + " y:"+handMouseCoorY);
                
                
    }//GEN-LAST:event_playerHandPanelMouseMoved

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
       gameGui.discardSelCards();
        this.repaint();
        // TODO add your hadgdndling code here:
    }//GEN-LAST:event_jButton8ActionPerformed
    
    /**
	 * check whether the mouse is currently over this piece
	 * @param piece the playing piece
	 * @param x x coordinate of mouse
	 * @param y y coordinate of mouse
	 * @return true if mouse is over the piece
	 */
	
        private boolean mouseOverPiece(TerrainGUI guiTerrain, int x, int y) {

		return guiTerrain.getPos().getMouseX() < x 
			&& guiTerrain.getPos().getMouseX() +guiTerrain.getWidth() > x
			&& guiTerrain.getPos().getMouseY() < y
			&& guiTerrain.getPos().getMouseY()+guiTerrain.getHeight() > y;
	
        }
        
    public void printOnChat(String inString)    {
    
    chatTextArea.append(inString+ "\n");
    }
        
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
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GameWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new GameWindow().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

   
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JPanel chatPanel;
    private javax.swing.JTextArea chatTextArea;
    private javax.swing.JPanel currentPlayerPanel;
    private javax.swing.JPanel discardPanel;
    private javax.swing.JPanel gameLogPanel;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jPlayer;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainMapPanel;
    private javax.swing.JButton moveButton;
    private javax.swing.JPanel playerDrawLeftPanel;
    private javax.swing.JPanel playerHandPanel;
    private javax.swing.JPanel rightSidePanel;
    private javax.swing.JButton sendMessageButton;
    private javax.swing.JTextField sendText;
    // End of variables declaration//GEN-END:variables



}
