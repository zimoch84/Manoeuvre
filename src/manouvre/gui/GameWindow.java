/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Color;
import manouvre.network.client.Message;
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
import javax.swing.text.DefaultCaret;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.game.Position;
import manouvre.game.Unit;
import manouvre.game.commands.MoveUnitCommand;
import manouvre.game.interfaces.FrameInterface;
import java.util.Arrays;
import manouvre.game.commands.SetupPositionCommand;
import manouvre.game.commands.EndSetupCommand;
import manouvre.game.commands.NextPhaseCommand;
import manouvre.game.interfaces.ClientInterface;
import manouvre.game.interfaces.Command;
import manouvre.game.commands.CommandQueue;
import manouvre.game.commands.EndTurnCommand;
import static java.lang.Math.abs;
import javax.swing.UIManager;


/**
 *
 * @author Piotr
 */
public class GameWindow extends javax.swing.JFrame implements FrameInterface{

    /*
    Network variables
    */
    
    public ClientInterface client;
    public int port;
    public String serverAddr,  password;
    public Thread clientThread;
    public Player player;
    
    private int handMouseCoorX,handMouseCoorY;
    private int handMouseCoorXdeaf=0;
    private int handMouseCoorYdeaf=0;
   
    
    CardSetGUI cardSetsGUI;
    int windowMode;
    
    /*
    Object hold whole game logically
    */
    Game game;

    GameGUI gameGui;
    
    private Image bgImage;
   
    public CommandQueue cmd;
    public CommandLogger cmdLogger;
 
    /*
    This is main contructor
    */
     public GameWindow(Game game, ClientInterface passSocket,  int windowMode) throws IOException{
        
        /*
         Game has generated players army , hand and comes from serwver
         */
        this.game = game;
        this.windowMode = windowMode;
        this.client = passSocket;
        this.cmdLogger = new CommandLogger(this);
        this.cmd = new CommandQueue(game, cmdLogger, this, passSocket);
        
        
        /*
        Sets current Player based on HOST/GUEST settings
        */
        game.setCurrentPlayer(windowMode);

        /*
        Creates new GUI respects HOST/GUEST settings
        */
        gameGui = new GameGUI(this.game, windowMode);
        
        bgImage = ImageIO.read( new File("resources\\backgrounds\\24209cb208yezho.jpg"));
        /*
        Create title
        */
        String title = "Manouvre, " + (windowMode== CreateRoomWindow.AS_HOST  
                            ? game.getHostPlayer().getName() + " as HOST" 
                            : game.getGuestPlayer().getName() + " as GUEST" );
        
        title = title + (game.getCurrentPlayer().isFirst() ? " and first player" : " and second player");

        this.setTitle(title);
        
        
        initComponents();
        
        refreshAll();
        
        
        
    
        this.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) { try{ client.send(new Message("message", game.getCurrentPlayer().getName(), ".bye", "SERVER")); clientThread.stop();  }catch(Exception ex){} }
            @Override public void windowClosed(WindowEvent e) {}
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
     UIManager.put("TabbedPane.contentOpaque", false);  
     DefaultCaret caret = (DefaultCaret)chatTextArea.getCaret();
     caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }
    
    
    public Game getGame() {
         return game;
    }

     public void setGame(Game game) {
         this.game = game;
    }

    public void drawMainBackground(Graphics g){
    
     g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height,Color.red, null);
    }
     
    private void drawInfoPanel(Graphics g){
    
        gameGui.drawInfoPanel(g);
    }
    
    private void drawMap(Graphics g )                   
    {
        gameGui.drawMap(g,windowMode );
     
    }
    
    void checkLockingGUI()
            
    {
    if(game.getPhase()== Game.SETUP && game.getCurrentPlayer().isFinishedSetup() && !game.getOpponentPlayer().isFinishedSetup() )
        gameGui.lockGUI();
    else if(game.getPhase()!= Game.SETUP && !game.getCurrentPlayer().isActive())
        gameGui.lockGUI();            
    else
        gameGui.unlockGUI();
    }
    
    public void refreshAll(){
        checkLockingGUI();
        setActionButtonText();
        setPhaseLabelText();
        gameTurnCounter.setText(Integer.toString(game.getTurn()));
        setNextPhaseButonText(); 
        
        updateGui();
    }
    
    public void updateGui(){
        gameGui.resetAllCardSets();
        
    }
    private void setActionButtonText(){
        /*
        If card is Selected change Label to "Play Card" 
        */
        if(game.getCurrentPlayer().getHand().isCardSelected() && game.getPhase() != Game.DISCARD )
        {
             actionButton.setText("Play Card");
            actionButton.setEnabled(true);
            
        }
         else
        {
        
           switch(game.getPhase()){
           case Game.SETUP:
           {
            actionButton.setVisible(false);
            actionButton.setText("Setup");
            actionButton.setEnabled(false);
            break;
           }    
           case Game.DISCARD:
           {
            actionButton.setVisible(true);
            actionButton.setEnabled(!gameGui.getSelectionSeqIsEmpty() && game.getCurrentPlayer().isActive() && !gameGui.isLocked());
            actionButton.setText("Discard");
            break;
           }
           case Game.DRAW:
           {
             
             actionButton.setEnabled(gameGui.getNumberOfDiscardedCards()>0 && game.getCurrentPlayer().isActive() && !gameGui.isLocked() );
             actionButton.setText("Draw");
              break;
           }
           case Game.MOVE:
           {
            actionButton.setVisible(true);
            actionButton.setText("Move");
            if(game.getCurrentPlayer().hasMoved())
            {
            actionButton.setEnabled(game.getCurrentPlayer().isActive() && !gameGui.isLocked() );  
            actionButton.setText("Undo");
            }
            else  actionButton.setEnabled(false);  
            
             break;
           }
           case Game.COMBAT:
           {
            actionButton.setEnabled(game.getCurrentPlayer().isActive() && !gameGui.isLocked() );  
            actionButton.setText("Combat");
             break;
           }
            case Game.RESTORATION:
           {
            actionButton.setEnabled(game.getCurrentPlayer().isActive() && !gameGui.isLocked() );  
            actionButton.setText("Restoration");
            break;
           }
         }
           
        }
                
    }
    
     private void setPhaseLabelText(){
      {
        
           switch(game.getPhase()){
           case Game.SETUP:
           {
           phaseNameLabel.setText("Setup");
            break;
           }    
           case Game.DISCARD:
           {
            phaseNameLabel.setText("Discard");
            break;
           }
           case Game.DRAW:
           {
             phaseNameLabel.setText("Draw");
              break;
           }
           case Game.MOVE:
           {
            phaseNameLabel.setText("Move");
             break;
           }
           case Game.COMBAT:
           {
            phaseNameLabel.setText("Combat");
             break;
           }
            case Game.RESTORATION:
           {
            phaseNameLabel.setText("Restoration");
            break;
           }
         }
        }
    }
     
    private void setNextPhaseButonText(){
      {
        
           switch(game.getPhase()){
           case Game.SETUP:
           {
            buttonToNextPhase.setText("End setup");
            
            if(game.getCurrentPlayer().isFinishedSetup() && !game.getOpponentPlayer().isFinishedSetup() )
            {
                buttonToNextPhase.setText("Waiting for opponent to end setup");
                buttonToNextPhase.setEnabled(false);
            }
            
            break;
           }    
           case Game.DISCARD:
           {
               
            buttonToNextPhase.setEnabled(game.getCurrentPlayer().isActive() && !gameGui.isLocked() );
            buttonToNextPhase.setText("End discard");
            break;
           }
           case Game.DRAW:
           {
            buttonToNextPhase.setText("End draw");
            buttonToNextPhase.setEnabled(!game.getCurrentPlayer().hasDrawn() && game.getCurrentPlayer().isActive() && !gameGui.isLocked());
            break;
           }
           
           case Game.MOVE:
           {
            buttonToNextPhase.setText("End move");
            buttonToNextPhase.setEnabled(game.getCurrentPlayer().hasMoved() && !gameGui.isLocked() 
                                        && game.getCurrentPlayer().isActive() );
            break;
           }
           case Game.COMBAT:
           {
            buttonToNextPhase.setEnabled(game.getCurrentPlayer().isActive() && !gameGui.isLocked());
            buttonToNextPhase.setText("End combat");
             break;
           }
            case Game.RESTORATION:
           {
            buttonToNextPhase.setEnabled(game.getCurrentPlayer().isActive() && !gameGui.isLocked());
            buttonToNextPhase.setText("End turn");
            break;
           }
         }
           
        }
    } 
 
    public void drawCurrentPlayerFlag(Graphics g){
    
        g.drawImage(gameGui.getFlagIcon(game.getCurrentPlayer()), 0, 0,64,56, null);
        
    }
    
     public void drawOpponentPlayerFlag(Graphics g){
    
        g.drawImage(gameGui.getFlagIcon(game.getOpponentPlayer()), 0, 0,64,56, null);
        
    }
    
    public void paintHand(Graphics g )                 
    {   
        gameGui.paintHand(g); 
         
      
        
    }
    
    private void paintDiscard(Graphics g, boolean paintOpponent)                 
    {   
        gameGui.paintDiscard(g, paintOpponent); 
        
    }
    
    private void paintDrawLeft(Graphics g, boolean paintOpponent) {
        gameGui.paintDrawLeft(g, paintOpponent); 
    }
    
    private void paintCombatPanel(Graphics g){
        gameGui.paintCombatPanel(g);
        
    }
    private void paintTablePanel (Graphics g){
        gameGui.paintTablePanel(g);
        
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
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        mainWindowPanel = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawMainBackground(g);

            }
        }
        ;
        mainMapPanel = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawMap(g);

            }
        }
        ;
        playerHandPanel = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                paintHand(g);

            }
        }
        ;
        rightSidePanel = new javax.swing.JPanel();
        buttonsPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        buttonToNextPhase = new javax.swing.JButton();
        actionButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        gameTurnCounter = new javax.swing.JLabel();
        phaseNameLabel = new javax.swing.JLabel();
        tablePanel = new javax.swing.JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                paintTablePanel(g);

            }
        }
        ;
        chatPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        sendMessageButton = new javax.swing.JButton();
        sendText = new javax.swing.JTextField();
        playersTabbedPane = new javax.swing.JTabbedPane();
        currentPlayerPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        discardPanel = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                paintDiscard(g, false);

            }
        }
        ;
        playerDrawLeftPanel = new javax.swing.JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                paintDrawLeft(g, false);

            }
        }
        ;
        jLabel2 = new javax.swing.JLabel();
        curPlayerFlag = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawCurrentPlayerFlag(g);

            }
        }
        ;
        opponentPlayerPanel = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        discardPanel1 = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                paintDiscard(g, true);

            }
        }
        ;
        playerDrawLeftPanel1 = new javax.swing.JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                paintDrawLeft(g, true);

            }
        }
        ;
        jLabel3 = new javax.swing.JLabel();
        opoPlayerFlag = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawOpponentPlayerFlag(g);

            }
        }
        ;
        ;
        jPanel2 = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawInfoPanel(g);

            }
        }
        ;
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        FindCard = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        checkRetreat = new javax.swing.JCheckBoxMenuItem();
        checkLos = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
        jMenu3 = new javax.swing.JMenu();
        MoveToTableCommand = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        mainMapPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Map"));
        mainMapPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseMoved(evt);
            }
        });
        mainMapPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseExited(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout mainMapPanelLayout = new javax.swing.GroupLayout(mainMapPanel);
        mainMapPanel.setLayout(mainMapPanelLayout);
        mainMapPanelLayout.setHorizontalGroup(
            mainMapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 696, Short.MAX_VALUE)
        );
        mainMapPanelLayout.setVerticalGroup(
            mainMapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 650, Short.MAX_VALUE)
        );

        playerHandPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hand"));
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

        javax.swing.GroupLayout playerHandPanelLayout = new javax.swing.GroupLayout(playerHandPanel);
        playerHandPanel.setLayout(playerHandPanelLayout);
        playerHandPanelLayout.setHorizontalGroup(
            playerHandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        playerHandPanelLayout.setVerticalGroup(
            playerHandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 222, Short.MAX_VALUE)
        );

        rightSidePanel.setOpaque(false);

        buttonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Phase Panel"));
        buttonsPanel.setOpaque(false);

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Phase:");

        buttonToNextPhase.setText("to Next Phase");
        buttonToNextPhase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonToNextPhaseActionPerformed(evt);
            }
        });

        actionButton.setText("action Name");
        actionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Turn:");

        gameTurnCounter.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        gameTurnCounter.setForeground(new java.awt.Color(255, 255, 255));
        gameTurnCounter.setText("0");
        gameTurnCounter.setEnabled(false);

        phaseNameLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        phaseNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        phaseNameLabel.setText("Phase Name");

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(buttonsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gameTurnCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(actionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(buttonToNextPhase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(phaseNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(gameTurnCounter)
                    .addComponent(actionButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(phaseNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonToNextPhase, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );

        tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Table Panel"));
        tablePanel.setName(""); // NOI18N
        tablePanel.setOpaque(false);

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 543, Short.MAX_VALUE)
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 279, Short.MAX_VALUE)
        );

        chatPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Chat / Game Log"));
        chatPanel.setOpaque(false);

        jScrollPane1.setAutoscrolls(true);

        chatTextArea.setColumns(20);
        chatTextArea.setRows(5);
        jScrollPane1.setViewportView(chatTextArea);

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(chatPanelLayout.createSequentialGroup()
                        .addComponent(sendText, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendMessageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(51, 51, 51))
        );
        chatPanelLayout.setVerticalGroup(
            chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sendText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendMessageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        playersTabbedPane.setFocusCycleRoot(true);

        currentPlayerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(game.getCurrentPlayer().getNationAsString(false)+ (game.getCurrentPlayer().isFirst() ? " (First player)" : "")));
        currentPlayerPanel.setForeground(new java.awt.Color(153, 0, 102));
        currentPlayerPanel.setOpaque(false);

        jLabel6.setText("Score:");

        discardPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Discard Pile"));
        discardPanel.setPreferredSize(new java.awt.Dimension(100, 25));

        javax.swing.GroupLayout discardPanelLayout = new javax.swing.GroupLayout(discardPanel);
        discardPanel.setLayout(discardPanelLayout);
        discardPanelLayout.setHorizontalGroup(
            discardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 90, Short.MAX_VALUE)
        );
        discardPanelLayout.setVerticalGroup(
            discardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 130, Short.MAX_VALUE)
        );

        playerDrawLeftPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Draw Pile"));

        javax.swing.GroupLayout playerDrawLeftPanelLayout = new javax.swing.GroupLayout(playerDrawLeftPanel);
        playerDrawLeftPanel.setLayout(playerDrawLeftPanelLayout);
        playerDrawLeftPanelLayout.setHorizontalGroup(
            playerDrawLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 57, Short.MAX_VALUE)
        );
        playerDrawLeftPanelLayout.setVerticalGroup(
            playerDrawLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 21, Short.MAX_VALUE)
        );

        jLabel2.setText("Units Killed:");

        curPlayerFlag.setPreferredSize(new java.awt.Dimension(64, 56));

        javax.swing.GroupLayout curPlayerFlagLayout = new javax.swing.GroupLayout(curPlayerFlag);
        curPlayerFlag.setLayout(curPlayerFlagLayout);
        curPlayerFlagLayout.setHorizontalGroup(
            curPlayerFlagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );
        curPlayerFlagLayout.setVerticalGroup(
            curPlayerFlagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout currentPlayerPanelLayout = new javax.swing.GroupLayout(currentPlayerPanel);
        currentPlayerPanel.setLayout(currentPlayerPanelLayout);
        currentPlayerPanelLayout.setHorizontalGroup(
            currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2)
                    .addComponent(playerDrawLeftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(curPlayerFlag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(discardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        currentPlayerPanelLayout.setVerticalGroup(
            currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(curPlayerFlag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playerDrawLeftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                .addComponent(discardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        playersTabbedPane.addTab(game.getCurrentPlayer().getName(), currentPlayerPanel);
        currentPlayerPanel.getAccessibleContext().setAccessibleName(game.getCurrentPlayer().getName());

        opponentPlayerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(game.getOpponentPlayer().getNationAsString(false)+ (game.getOpponentPlayer().isFirst() ? " (First player)" : "")));
        opponentPlayerPanel.setForeground(new java.awt.Color(153, 0, 102));
        opponentPlayerPanel.setOpaque(false);

        jLabel10.setText("Score:");

        discardPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Discard Pile"));
        discardPanel1.setPreferredSize(new java.awt.Dimension(100, 25));
        discardPanel1.setRequestFocusEnabled(false);

        javax.swing.GroupLayout discardPanel1Layout = new javax.swing.GroupLayout(discardPanel1);
        discardPanel1.setLayout(discardPanel1Layout);
        discardPanel1Layout.setHorizontalGroup(
            discardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 90, Short.MAX_VALUE)
        );
        discardPanel1Layout.setVerticalGroup(
            discardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        playerDrawLeftPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Draw Pile"));

        javax.swing.GroupLayout playerDrawLeftPanel1Layout = new javax.swing.GroupLayout(playerDrawLeftPanel1);
        playerDrawLeftPanel1.setLayout(playerDrawLeftPanel1Layout);
        playerDrawLeftPanel1Layout.setHorizontalGroup(
            playerDrawLeftPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 61, Short.MAX_VALUE)
        );
        playerDrawLeftPanel1Layout.setVerticalGroup(
            playerDrawLeftPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        jLabel3.setText("Units Killed:");

        opoPlayerFlag.setPreferredSize(new java.awt.Dimension(64, 56));

        javax.swing.GroupLayout opoPlayerFlagLayout = new javax.swing.GroupLayout(opoPlayerFlag);
        opoPlayerFlag.setLayout(opoPlayerFlagLayout);
        opoPlayerFlagLayout.setHorizontalGroup(
            opoPlayerFlagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 64, Short.MAX_VALUE)
        );
        opoPlayerFlagLayout.setVerticalGroup(
            opoPlayerFlagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout opponentPlayerPanelLayout = new javax.swing.GroupLayout(opponentPlayerPanel);
        opponentPlayerPanel.setLayout(opponentPlayerPanelLayout);
        opponentPlayerPanelLayout.setHorizontalGroup(
            opponentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(opponentPlayerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(opponentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel3)
                    .addComponent(playerDrawLeftPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(opoPlayerFlag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(discardPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        opponentPlayerPanelLayout.setVerticalGroup(
            opponentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(opponentPlayerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(opoPlayerFlag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playerDrawLeftPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 13, Short.MAX_VALUE))
            .addGroup(opponentPlayerPanelLayout.createSequentialGroup()
                .addComponent(discardPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addContainerGap())
        );

        playersTabbedPane.addTab(game.getOpponentPlayer().getName(), opponentPlayerPanel);
        opponentPlayerPanel.getAccessibleContext().setAccessibleName(game.getOpponentPlayer().getName());

        jPanel2.setOpaque(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 192, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout rightSidePanelLayout = new javax.swing.GroupLayout(rightSidePanel);
        rightSidePanel.setLayout(rightSidePanelLayout);
        rightSidePanelLayout.setHorizontalGroup(
            rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightSidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rightSidePanelLayout.createSequentialGroup()
                        .addComponent(playersTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(rightSidePanelLayout.createSequentialGroup()
                        .addGroup(rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(0, 0, 0))
        );
        rightSidePanelLayout.setVerticalGroup(
            rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightSidePanelLayout.createSequentialGroup()
                .addGroup(rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rightSidePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(playersTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rightSidePanelLayout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        tablePanel.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout mainWindowPanelLayout = new javax.swing.GroupLayout(mainWindowPanel);
        mainWindowPanel.setLayout(mainWindowPanelLayout);
        mainWindowPanelLayout.setHorizontalGroup(
            mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainWindowPanelLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(mainMapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(playerHandPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(rightSidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );
        mainWindowPanelLayout.setVerticalGroup(
            mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainWindowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rightSidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainWindowPanelLayout.createSequentialGroup()
                        .addComponent(mainMapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(playerHandPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 6, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jMenu1.setText("Debug");

        FindCard.setText("FindCard");
        FindCard.setToolTipText("");
        FindCard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FindCardActionPerformed(evt);
            }
        });
        jMenu1.add(FindCard);

        jMenuItem2.setText("jMenuItem2");
        jMenu1.add(jMenuItem2);

        checkRetreat.setText("toogleRetreat");
        checkRetreat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkRetreatActionPerformed(evt);
            }
        });
        jMenu1.add(checkRetreat);

        checkLos.setText("toogleLOS");
        checkLos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkLosActionPerformed(evt);
            }
        });
        jMenu1.add(checkLos);

        jCheckBoxMenuItem1.setText("freeMove");
        jMenu1.add(jCheckBoxMenuItem1);

        jMenu3.setText("Commands");

        MoveToTableCommand.setText("MoveToTable");
        MoveToTableCommand.setToolTipText("Card Must Be Selected");
        MoveToTableCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MoveToTableCommandActionPerformed(evt);
            }
        });
        jMenu3.add(MoveToTableCommand);

        jMenuItem4.setText("jMenuItem4");
        jMenu3.add(jMenuItem4);

        jMenu1.add(jMenu3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1312, 1312, 1312)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(mainWindowPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainWindowPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentHidden
    

    
    private void playerHandPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseClicked
                gameGui.mouseClickedCard(game.getCardCommandFactory()); 
                setActionButtonText();  //when card is selected set the buttons
                repaint();
                if(game.getPhase()==Game.DISCARD)
                    setActionButtonText();  //if selection was done discard button should be visible
    }//GEN-LAST:event_playerHandPanelMouseClicked

    private void playerHandPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseEntered
         // TODO add your handling code here:
         
    }//GEN-LAST:event_playerHandPanelMouseEntered

    private void playerHandPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseExited
         // TODO add your handling code here:
        // repaint(CURR_X, CURR_Y, CURR_W, CURR_H);
    }//GEN-LAST:event_playerHandPanelMouseExited

    private void playerHandPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseMoved
                int deafband=20; //repaint after mouse change of
                int repaintAddedArea=20;//refreash little more than just the sqare
                 final int CURR_X = playerHandPanel.getX();
                 final int CURR_Y = playerHandPanel.getY();
                 final int CURR_W = playerHandPanel.getWidth()+repaintAddedArea;
                 final int CURR_H = playerHandPanel.getHeight()+3*repaintAddedArea;
                handMouseCoorX = evt.getPoint().x;
		handMouseCoorY = evt.getPoint().y;
    
                gameGui.mouseMovedOverHand(handMouseCoorX, handMouseCoorY);
                if(abs(handMouseCoorX-handMouseCoorXdeaf)>deafband){  //change repainting step
                    handMouseCoorXdeaf=handMouseCoorX;
                    repaint(CURR_X, CURR_Y, CURR_W, CURR_H);
                }
              
                if(abs(handMouseCoorY-handMouseCoorYdeaf)>deafband){
                    handMouseCoorYdeaf=handMouseCoorY; 
                    repaint(CURR_X, CURR_Y, CURR_W, CURR_H);
                }
                //System.out.println("x:" + handMouseCoorX + " y:"+handMouseCoorY);
                
                
    }//GEN-LAST:event_playerHandPanelMouseMoved

    private void mainMapPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainMapPanelMouseClicked
        
        /*
        If current player is active and have unlocked gui can move - else we lock interface
        */
        if( game.getCurrentPlayer().isActive() && ! gameGui.isLocked() || game.getPhase() == Game.SETUP )
        {
        int x = evt.getPoint().x;
        int y = evt.getPoint().y;
        
        //player must be in correct phase to be able to move units
        if(game.getPhase()==Game.MOVE || game.getPhase() == Game.SETUP )
        { 
        if(!gameGui.mapGui.isUnitSelected() )
            {
           
                Position clickedPos = Position.getPositionFromMouse(x, y);
                if(clickedPos != null)
                {
                   if(windowMode == CreateRoomWindow.AS_GUEST) 
                        {
                            clickedPos = clickedPos.transpoze();
                        }
                    game.getMap().getTerrainAtPosition(clickedPos).setSelected(true);    
                   
                    System.out.println("manouvre.gui.GameWindow.mainMapPanelMouseClicked() " + clickedPos);
                    /*
                    If player clicks on unit select it
                    */    
                    if(game.checkCurrentPlayerUnitAtPosition(clickedPos) ) {
                       gameGui.mapGui.setUnitSelected(true);
                       gameGui.getUnitGuiOnMapGui(clickedPos).getUnit().setSelected(true);

                       }
                }
                      
                this.repaint();
            }
        
            
        
        /*
        If unit is selected find which unit to move and move into
        */
        else  
        {
            if(!game.getCurrentPlayer().hasMoved() || gameGui.freeMove)
            {
            Unit selectedUnit = game.getSelectedUnit();
            
            Position clickedPosition = new Position(  Position.convertMouseXToX(x)   , Position.convertMouseYToY(y)) ;
            if(windowMode == CreateRoomWindow.AS_GUEST)
                    clickedPosition = clickedPosition.transpoze();
            
            if(!selectedUnit.getPosition().equals(clickedPosition))
            {
                System.out.println("manouvre.gui.ClientUI.mainMapPanelMouseClicked().clickedPosition :" + clickedPosition) ;
                ArrayList<Position> movePositions;
                if(game.getPhase() == Game.SETUP)
                {
                    movePositions = game.getSetupPossibleMovement();
        }
                else if (game.getCurrentPlayer().isPlayingCard() )
                {
                    movePositions = game.getOneSquareMovements(selectedUnit.getPosition());
                }
                else{
                     movePositions = game.getPossibleMovement(selectedUnit);
                }
       
        
                for(Position checkPosition: movePositions){
       
                    if(checkPosition.equals(clickedPosition))
                    {
                      MoveUnitCommand moveUnit = new MoveUnitCommand(game.getCurrentPlayer().getName() , selectedUnit,  clickedPosition);
                        
                        if(!game.getCurrentPlayer().isPlayingCard() && game.getPhase() != Game.SETUP)
                        {        
                                cmd.storeAndExecuteAndSend(moveUnit);
                        }
                        
                        /*
                        Regular move has to be send but not the move from HQ or else cards
                        */
                        else if(game.getCurrentPlayer().isPlayingCard())
                        {   /*
                            We attach move command to wrap it to postpone execution in card command
                            */
                            game.getCardCommandFactory().setAttachedCommand(moveUnit);
                            /*
                            Confirmation dialog
                            */
                            CustomDialog dialog = new CustomDialog(CustomDialog.YES_NO_UNDO_TYPE, "Are You sure to play that card? " , client, game);
                            
                            try {
                                dialog.setOkCommand(game.getCardCommandFactory().createCardCommand());
                                dialog.setCancelCommand(moveUnit);
                            } catch (Exception ex) {
                                Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            dialog.setVisible(true);
                            
                            game.getCurrentPlayer().setPlayingCard(false);
                        }
                        
                        else 
                        {   
                            /*
                            Just execute on client
                            */
                            cmd.storeAndExecute(moveUnit);
                        
                        }
                        
                        
                         //Move in game and GUI
                        //Unselect all
                        gameGui.unselectAllUnits();
                        //exit loop
                        
                        break;
                    }
                }
                mainMapPanel.repaint();
            }
            /*
            Clicking on the same unit - deselects it.
            */
            else
            {
                gameGui.unselectAllUnits();
                mainMapPanel.repaint();
            }
            }
            /*
            If we moved already put popup to shop popup that
            */
            else 
            {
                CustomDialog cd = new CustomDialog(CustomDialog.CONFIRMATION_TYPE, "You have moved already, \n play card or proceed to next phase");
                cd.setVisible(true);
                gameGui.unselectAllUnits();
                this.repaint();
                
            }
            

            // game.moveUnit(  , newPosition);
        }            
        }
    }       

        
       
        
    
       
    }//GEN-LAST:event_mainMapPanelMouseClicked
    private void mainMapPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainMapPanelMouseReleased
  //nothing
    }//GEN-LAST:event_mainMapPanelMouseReleased

    private void actionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionButtonActionPerformed
       /*
        Play action based on current button description
        */
        switch (actionButton.getText()){
            
            case "Play Card":
            {
                gameGui.playSelectedCard();
                this.repaint();
            }
            
            case "Undo":
            
            cmd.undoLastCommand();
                
            /*
            Else play action based on game turn 
            */
            default:
            {
                
            switch(game.getPhase()){
            case Game.SETUP :
            {
                break;
            }

            case Game.DISCARD :
            {
                
                client.send(gameGui.discardSelCards());             
                setActionButtonText();
                this.repaint();
                break;
            }
            case Game.DRAW:
            {
                client.send(gameGui.drawCards());
                setActionButtonText();

                game.nextPhase(); 
                this.repaint();
                break;
            }
            case Game.MOVE:
            {
                 //game.nextPhase();  //move
                 setActionButtonText();
                 this.repaint();
                 break;
            }
            case Game.COMBAT:
            {
                 //game.nextPhase();  //move
                 setActionButtonText();
                 this.repaint();
                 break;
            }
            case Game.RESTORATION:
                 //game.nextPhase();   //restoration
                 setActionButtonText();
                 this.repaint();
                 break;
              }
            }
            }
       
       
       
           
    }//GEN-LAST:event_actionButtonActionPerformed

    private void buttonToNextPhaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonToNextPhaseActionPerformed
        /*
        IF setup then ask for confirmation
        */
        gameGui.phaseChanged();
        if(game.getPhase() == Game.SETUP )
        {
            /*
            Validate setup army posiotion
            */
            Unit badPlacedUnit  = game.validateArmySetup(game.getCurrentPlayer());
            if (badPlacedUnit == null)
            {
            /*
            Setting end setup flag and after confirmation dialog
            */
            SetupPositionCommand setupCommand = new SetupPositionCommand(
                       game.getCurrentPlayer().getName(),
                       new ArrayList<Unit>(
                                        Arrays.asList(
                                                    game.getCurrentPlayer().getArmy()
                                                    )));
            EndSetupCommand endSetupCommand = new EndSetupCommand(game.getCurrentPlayer().getName(), setupCommand );
            
            CustomDialog dialog = new CustomDialog(CustomDialog.YES_NO_TYPE, "Are You sure to end setup?", cmd, game);
            dialog.setOkCommand(endSetupCommand);
            
            /*
            Lock GUI if opponent hasnt finished setup
            */
            if(!game.getOpponentPlayer().isFinishedSetup()  && game.getCurrentPlayer().isActive())
            {
                gameGui.lockGUI();
            
            }
            
              
            }
            else
            {           
            CustomDialog dialog = new CustomDialog(
            CustomDialog.CONFIRMATION_TYPE, "Unit " + badPlacedUnit.getName() +  " is placed wrong", cmd, game);
            }
          
        }
        
        else if (game.getPhase() == Game.RESTORATION)
        {
        Command endTurnCommand = new EndTurnCommand(game.getCurrentPlayer().getName());
        cmd.storeAndExecuteAndSend(endTurnCommand);
        }
        else 
        {
        Command nextPhaseCommand = new NextPhaseCommand(game.getCurrentPlayer().getName(), game.getPhase()+ 1);
        cmd.storeAndExecuteAndSend(nextPhaseCommand);
        }    
        setActionButtonText();
        this.repaint();
    }//GEN-LAST:event_buttonToNextPhaseActionPerformed

    private void sendMessageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendMessageButtonActionPerformed
        String msg = sendText.getText();
        String target = "All";

        if(!msg.isEmpty() && !target.isEmpty()){
            sendText.setText("");
            client.send(new Message("message", game.getCurrentPlayer().getName(), msg, target));
        }
    }//GEN-LAST:event_sendMessageButtonActionPerformed

    private void FindCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindCardActionPerformed
         TestWindow testWindow = new TestWindow(game, this, game.getPhase());
         testWindow.setBounds(50, 100, testWindow.getWidth(), testWindow.getHeight());
         testWindow.setVisible(true);
    }//GEN-LAST:event_FindCardActionPerformed

    private void MoveToTableCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MoveToTableCommandActionPerformed
        if(game.getCardCommandFactory().getCurrentPlayedCard()!=null){
            Command moveToTable = game.getCardCommandFactory().createCardCommand();
            cmd.storeAndExecuteAndSend(moveToTable);
        }               
                       
    }//GEN-LAST:event_MoveToTableCommandActionPerformed

    private void checkRetreatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkRetreatActionPerformed
         game.getSelectedUnit().setRetriving(!game.getSelectedUnit().isRetriving());
         this.repaint();
    }//GEN-LAST:event_checkRetreatActionPerformed

    private void checkLosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkLosActionPerformed
        game.getSelectedUnit().setShowingLOS(!game.getSelectedUnit().isShowingLOS());
        this.repaint();
    }//GEN-LAST:event_checkLosActionPerformed

    private void mainMapPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainMapPanelMouseEntered
        
       
        
        
    }//GEN-LAST:event_mainMapPanelMouseEntered

    private void mainMapPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainMapPanelMouseMoved
       int x = evt.getPoint().x;
        int y = evt.getPoint().y;
        
        Position clickedPos = Position.getPositionFromMouse(x, y);
        
        
        if (windowMode == CreateRoomWindow.AS_GUEST) 
            {
                clickedPos = clickedPos.transpoze();
            }
               
        UnitGUI unit = gameGui.getUnitGuiOnMapGui(clickedPos);
        if(unit != null){
        
        gameGui.setInfoImage(unit.getImg());
        repaint();
        
        }
    }//GEN-LAST:event_mainMapPanelMouseMoved

    private void mainMapPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainMapPanelMouseExited
        gameGui.setInfoImage(null);
    }//GEN-LAST:event_mainMapPanelMouseExited
 
//    public void clientSend(Message message){
//        client.send(gameGui.discardSelCards());
//        this.repaint();
//    }

    public GameGUI getGameGui() {
        return gameGui;
    }

    public void setGameGui(GameGUI gameGui) {
        this.gameGui = gameGui;
    }
    
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
        
    @Override
    public void printOnChat(String inString)    {
    
    chatTextArea.append(inString+ "\n");
    }
        
   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem FindCard;
    private javax.swing.JMenuItem MoveToTableCommand;
    javax.swing.JButton actionButton;
    private javax.swing.JButton buttonToNextPhase;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JPanel chatPanel;
    private javax.swing.JTextArea chatTextArea;
    private javax.swing.JCheckBoxMenuItem checkLos;
    private javax.swing.JCheckBoxMenuItem checkRetreat;
    private javax.swing.JPanel curPlayerFlag;
    private javax.swing.JPanel currentPlayerPanel;
    private javax.swing.JPanel discardPanel;
    private javax.swing.JPanel discardPanel1;
    private javax.swing.JLabel gameTurnCounter;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainMapPanel;
    private javax.swing.JPanel mainWindowPanel;
    private javax.swing.JPanel opoPlayerFlag;
    private javax.swing.JPanel opponentPlayerPanel;
    private javax.swing.JLabel phaseNameLabel;
    private javax.swing.JPanel playerDrawLeftPanel;
    private javax.swing.JPanel playerDrawLeftPanel1;
    private javax.swing.JPanel playerHandPanel;
    private javax.swing.JTabbedPane playersTabbedPane;
    private javax.swing.JPanel rightSidePanel;
    private javax.swing.JButton sendMessageButton;
    private javax.swing.JTextField sendText;
    private javax.swing.JPanel tablePanel;
    // End of variables declaration//GEN-END:variables



}
