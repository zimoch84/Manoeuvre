/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import com.sun.beans.util.Cache;
import java.awt.Color;
import java.awt.Desktop;
import static java.awt.Desktop.isDesktopSupported;
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
import manouvre.game.commands.CommandQueue;
import manouvre.game.commands.EndTurnCommand;
import javax.swing.UIManager;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import manouvre.game.Card;
import manouvre.game.interfaces.Command;
import static java.lang.Math.abs;
import java.util.Observable;
import java.util.Observer;
import manouvre.game.CardCommandFactory;



/**
 *
 * @author Piotr
 */
public class GameWindow extends javax.swing.JFrame  implements FrameInterface, Observer{

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
   
    public CommandQueue cmdQueue;
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
        this.cmdQueue = new CommandQueue(game, cmdLogger, this, passSocket);
        
        
        /*
        Sets current Player based on HOST/GUEST settings
        */
        game.setCurrentPlayer(windowMode);

        /*
        Creates new GUI respects HOST/GUEST settings
        */
        gameGui = new GameGUI(this.game, windowMode, cmdQueue);
        
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
        
        game.getCardCommandFactory().addObserver(this);
        
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
    
    
    @Override
    public void update(Observable o, Object arg) {
        CardCommandFactory ccmdf = (CardCommandFactory) o;
        String dialogType = (String) arg;
        
       switch (dialogType){
       
           case CardCommandFactory.CARD_DIALOG:
           {
           Command rejectCard = ccmdf.createRejectCardCommand();
           Command doNotRejectCard = ccmdf.createDoNotRejectCardCommand();
           CardDialog cd  = new CardDialog(new CardGUI (ccmdf.getOpponentCard()), client, cmdQueue, game);
           cd.setOkCommand(doNotRejectCard);
           cd.setCancelCommand(rejectCard);   
           
           cd.setVisible(true);
           break;
               
           }
           
           case CardCommandFactory.ATTACK_DIALOG:
           {
           /*
               Create withdraw or ok dialog
               */
               Command withdrawCommand;   // ccmdf.createRejectCardCommand();
               Command okCommand;//
               Command pickDefenseCardsCommand;
               AttackDialog ad = new AttackDialog(ccmdf.getOpponentCard().getPlayiningMode(), new CardGUI(ccmdf.getOpponentCard()), 
                       new UnitGUI(ccmdf.getAttackedUnit()), client, cmdQueue, game);
               
               ad.setVisible(true);
               
           }
           default :
               System.out.println("manouvre.gui.GameWindow.update() No such dialog Type" );
       
       }
        
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
        //checkIfNewCardCanBeRejected();
    }
    
    public void updateGui(){
        gameGui.resetAllCardSets();
        
    }
    public void  checkPopUps(){ 
        
            
//show popup if card cen be cancelled
        if(game.getCardCommandFactory().getPlayingCard()!=null){
            if(game.getCardCommandFactory().getPlayingCard().getCanBeCancelled()){
                Command rejectCard = game.getCardCommandFactory().createRejectCardCommand();
                Command doNotRejectCard = game.getCardCommandFactory().createDoNotRejectCardCommand();
                if(game.getCurrentPlayer().getHand().getCardByName("Guerrillas", false)!=null){//if player has Guerrillas
                    CustomDialog dialog = new CustomDialog(CustomDialog.YES_NO_TYPE, game.getCurrentPlayer().getName()+" Your enemy played this card, will you reject?", cmdQueue, game);
                    game.getCardCommandFactory().getPlayingCard().setCanBeCanceled(false);
                    dialog.setOkCommand(rejectCard);
                    dialog.setCancelCommand(doNotRejectCard);
                } else{
                    CustomDialog dialog = new CustomDialog(CustomDialog.CONFIRMATION_TYPE, game.getCurrentPlayer().getName()+" Your enemy played this card, you can not reject..", cmdQueue, game);
                    game.getCardCommandFactory().getPlayingCard().setCanBeCanceled(false);
                    dialog.setOkCommand(doNotRejectCard);
                    rejectCard=null;
                }
            }
        }
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
             if(game.getCurrentPlayer().hasMoved()) //this is not working with doing undo for Forced March - when rejected
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
        jRadioButtonMenuItem1 = new javax.swing.JRadioButtonMenuItem();
        jMenu4 = new javax.swing.JMenu();
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
        allowDrawOnDiscard = new javax.swing.JCheckBoxMenuItem();
        jMenu3 = new javax.swing.JMenu();
        MoveToTableCommand = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItemAmbush = new javax.swing.JMenuItem();
        jMenuItemCommAttack = new javax.swing.JMenuItem();
        jMenuItemGuerrillas = new javax.swing.JMenuItem();
        jMenuItemRedoubt = new javax.swing.JMenuItem();
        jMenuItemRegroup = new javax.swing.JMenuItem();
        jMenuItemSkirmish = new javax.swing.JMenuItem();
        jMenuItemSupply = new javax.swing.JMenuItem();
        jMenuItemWithdraw = new javax.swing.JMenuItem();
        jMenuItemForced_March = new javax.swing.JMenuItem();
        jMenuItemEngineers = new javax.swing.JMenuItem();
        jMenuItemScout = new javax.swing.JMenuItem();

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

        jRadioButtonMenuItem1.setSelected(true);
        jRadioButtonMenuItem1.setText("jRadioButtonMenuItem1");

        jMenu4.setText("jMenu4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        mainMapPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseMoved(evt);
            }
        });
        mainMapPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseExited(evt);
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
                    .addComponent(buttonToNextPhase, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(buttonsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gameTurnCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(actionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(buttonsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(phaseNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
            .addGap(0, 328, Short.MAX_VALUE)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, chatPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, chatPanelLayout.createSequentialGroup()
                        .addComponent(sendText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendMessageButton)))
                .addGap(280, 280, 280))
        );
        chatPanelLayout.setVerticalGroup(
            chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
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
            .addGap(0, 0, Short.MAX_VALUE)
        );
        discardPanelLayout.setVerticalGroup(
            discardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 93, Short.MAX_VALUE)
        );

        playerDrawLeftPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Draw Pile"));

        javax.swing.GroupLayout playerDrawLeftPanelLayout = new javax.swing.GroupLayout(playerDrawLeftPanel);
        playerDrawLeftPanel.setLayout(playerDrawLeftPanelLayout);
        playerDrawLeftPanelLayout.setHorizontalGroup(
            playerDrawLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 63, Short.MAX_VALUE)
        );
        playerDrawLeftPanelLayout.setVerticalGroup(
            playerDrawLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
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
                    .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                        .addComponent(playerDrawLeftPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(143, Short.MAX_VALUE))
                    .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                        .addGroup(currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel2)
                            .addComponent(curPlayerFlag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addComponent(discardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))))
        );
        currentPlayerPanelLayout.setVerticalGroup(
            currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                .addGroup(currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(curPlayerFlag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2))
                    .addComponent(discardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playerDrawLeftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addGap(0, 0, Short.MAX_VALUE)
        );
        discardPanel1Layout.setVerticalGroup(
            discardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 93, Short.MAX_VALUE)
        );

        playerDrawLeftPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Draw Pile"));

        javax.swing.GroupLayout playerDrawLeftPanel1Layout = new javax.swing.GroupLayout(playerDrawLeftPanel1);
        playerDrawLeftPanel1.setLayout(playerDrawLeftPanel1Layout);
        playerDrawLeftPanel1Layout.setHorizontalGroup(
            playerDrawLeftPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 63, Short.MAX_VALUE)
        );
        playerDrawLeftPanel1Layout.setVerticalGroup(
            playerDrawLeftPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
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
                    .addGroup(opponentPlayerPanelLayout.createSequentialGroup()
                        .addComponent(playerDrawLeftPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(143, Short.MAX_VALUE))
                    .addGroup(opponentPlayerPanelLayout.createSequentialGroup()
                        .addGroup(opponentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel3)
                            .addComponent(opoPlayerFlag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addComponent(discardPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE))))
        );
        opponentPlayerPanelLayout.setVerticalGroup(
            opponentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(opponentPlayerPanelLayout.createSequentialGroup()
                .addGroup(opponentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(opponentPlayerPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(opoPlayerFlag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addComponent(discardPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playerDrawLeftPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                            .addComponent(chatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 1, Short.MAX_VALUE))))
        );
        rightSidePanelLayout.setVerticalGroup(
            rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightSidePanelLayout.createSequentialGroup()
                .addGroup(rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(rightSidePanelLayout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(playersTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                        .addGap(0, 19, Short.MAX_VALUE)))
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

        allowDrawOnDiscard.setText("Allow draw during discard");
        jMenu1.add(allowDrawOnDiscard);

        jMenu3.setText("Commands");

        MoveToTableCommand.setText("MoveToTable");
        MoveToTableCommand.setToolTipText("Card Must Be Selected");
        MoveToTableCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MoveToTableCommandActionPerformed(evt);
            }
        });
        jMenu3.add(MoveToTableCommand);

        jMenuItem4.setText("Attack");
        jMenuItem4.setToolTipText("Attacking card must be selected");
        jMenu3.add(jMenuItem4);

        jMenu1.add(jMenu3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu5.setText("Help");
        jMenu5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);

        jMenuItem1.setText("Manoeuvre_Rules.pdf");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu5.add(jMenuItem1);

        jMenu6.setText("Show Card Description");

        jMenuItemAmbush.setText("Ambush");
        jMenuItemAmbush.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAmbushActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemAmbush);

        jMenuItemCommAttack.setText("Committed Attack");
        jMenuItemCommAttack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCommAttackActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemCommAttack);

        jMenuItemGuerrillas.setText("Guerrillas");
        jMenuItemGuerrillas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGuerrillasActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemGuerrillas);

        jMenuItemRedoubt.setText("Redoubt");
        jMenuItemRedoubt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRedoubtActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemRedoubt);

        jMenuItemRegroup.setText("Regroup");
        jMenuItemRegroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRegroupActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemRegroup);

        jMenuItemSkirmish.setText("Skirmish");
        jMenuItemSkirmish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSkirmishActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemSkirmish);

        jMenuItemSupply.setText("Supply");
        jMenuItemSupply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSupplyActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemSupply);

        jMenuItemWithdraw.setText("Withdraw");
        jMenuItemWithdraw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemWithdrawActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemWithdraw);

        jMenuItemForced_March.setText("Forced March");
        jMenuItemForced_March.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemForced_MarchActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemForced_March);

        jMenuItemEngineers.setText("Engineers/Royal Eng");
        jMenuItemEngineers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemEngineersActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemEngineers);

        jMenuItemScout.setText("Scout/Spy");
        jMenuItemScout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemScoutActionPerformed(evt);
            }
        });
        jMenu6.add(jMenuItemScout);

        jMenu5.add(jMenu6);

        jMenuBar1.add(Box.createHorizontalGlue());// <-- horizontal glue

        jMenuBar1.add(jMenu5);

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
                .addGap(19, 19, 19))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainWindowPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentHidden
    

    
    private void playerHandPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseClicked
        handMouseCoorX = evt.getPoint().x;
	handMouseCoorY = evt.getPoint().y;        
        gameGui.mouseClickedCard(handMouseCoorX,handMouseCoorY); 
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
    
                gameGui.setMouseOverCard(handMouseCoorX, handMouseCoorY);
                if(abs(handMouseCoorX-handMouseCoorXdeaf)>deafband){  //change repainting step
                    handMouseCoorXdeaf=handMouseCoorX;
                    //repaint(CURR_X, CURR_Y, CURR_W, CURR_H);
                    repaint();
                }
              
                if(abs(handMouseCoorY-handMouseCoorYdeaf)>deafband){
                    handMouseCoorYdeaf=handMouseCoorY; 
                   // repaint(CURR_X, CURR_Y, CURR_W, CURR_H);
                   repaint();
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
        Position clickedPos = Position.getPositionFromMouse(x, y);
        if(windowMode == CreateRoomWindow.AS_GUEST)
                    clickedPos = clickedPos.transpoze();
        
        //player must be in correct phase to be able to move units 
        if((game.getPhase()==Game.MOVE || game.getPhase() == Game.SETUP) && !game.getCurrentPlayer().isPlayingCard())
        { 
  
        if(!gameGui.mapGui.isUnitSelected() )
            {
                if(clickedPos != null)
                {
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
            ArrayList<Position> movePositions;
                if(game.getPhase() == Game.SETUP)
                {
                    movePositions = game.getSetupPossibleMovement();
                }
                /*
                There is no setup and we don playing card 
                */
                else{
                     movePositions = game.getPossibleMovement(selectedUnit);
                }
               /*
                Check if we can move to clicked position
                If we clicked on other position that selected unit.
                */ 
             if(!selectedUnit.getPosition().equals(clickedPos) && movePositions.contains(clickedPos))
                    {
                    MoveUnitCommand moveUnit = new MoveUnitCommand(game.getCurrentPlayer().getName() , selectedUnit,  clickedPos);
                        /*
                      If we done play card and we are not in setup
                      */
                        if(game.getPhase() != Game.SETUP)
                        {        
                        cmdQueue.storeAndExecuteAndSend(moveUnit);
                        gameGui.unselectAllUnits();   
                        }
                        /*
                        If we dont play card or we are in setup
                        */
                        else 
                        {   
                        /*
                        Just execute on client
                        */
                        cmdQueue.storeAndExecute(moveUnit);
                        gameGui.unselectAllUnits();   
                        }
                 }
                else{ 
                //Unselect all if not clicked at allowed move
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
        }            
        }

        else if (game.getCurrentPlayer().isPlayingCard())
        {
             /*
            Phase 1 either player has selected unit or has to selelect card
            */

            Card playingCard = game.getCardCommandFactory().getPlayingCard();
            
            switch (playingCard.getAtionType(game.getPhase())){
                
                case Card.MOVE_ACTION:
                {
                    /*
                    We have unit selected
                    */
                if(game.getSelectedUnit()!= null)
                {  
                Unit selectedUnit = game.getSelectedUnit();
                if(!getMovePositions(playingCard).isEmpty())
                    if(getMovePositions(playingCard).contains(clickedPos))
                    {
                        MoveUnitCommand moveUnit = new MoveUnitCommand(game.getCurrentPlayer().getName() , selectedUnit,  clickedPos);
                        /*
                        We attach move command to wrap it to postpone execution in card command
                        */
                        game.getCardCommandFactory().setAttachedCommand(moveUnit);
                        /*
                        Confirmation dialog
                        */
                        showConfirmationCardDialog();
                    }
                    /*
                    Deselect card and clear Card Factory
                    */
                    else {
                        game.getCardCommandFactory().resetPlayingCard();
                        this.repaint();
                    }
                } 
                /*
                We dont have selected unit
                */
                else {
                  /*
                    TODO getPossibleUnitsToSelect() and select it - do regular Move
                    */  
                }
                break;
                }
                case Card.PICK_ACTION:    
                {
                if(!getAvaliblePositionToSelect().isEmpty())    
                    if(getAvaliblePositionToSelect().contains(clickedPos))
                    {
                        Unit attackedUnit = game.getOpponentPlayerUnitAtPosition(clickedPos);
                        game.getCardCommandFactory().setAttackedUnit(attackedUnit);
                        showConfirmationCardDialog();
                    }
                    else 
                    showCannotPlayCardDialog();    
                else 
                {
                game.getCardCommandFactory().resetPlayingCard();
                this.repaint();
                }   
                break;
                }
            }}
      
        }
        
    }//GEN-LAST:event_mainMapPanelMouseClicked
    private void mainMapPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainMapPanelMouseReleased
  //nothing
    }//GEN-LAST:event_mainMapPanelMouseReleased
    private void showConfirmationCardDialog(){
    /*
        Confirmation dialog
        */
        CustomDialog dialog = 
                new CustomDialog(CustomDialog.YES_NO_UNDO_TYPE, 
                        "Are You sure to play that card? " ,
                        cmdQueue, game);
        try {
            dialog.setOkCommand(game.getCardCommandFactory().createCardCommand());
            //dialog.setCancelCommand(moveUnit);
        } catch (Exception ex) {
            Logger.getLogger(GameWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        dialog.setVisible(true);
        
    }
    private void showCannotPlayCardDialog(){
    /*
        Confirmation dialog
        */
        CustomDialog dialog = 
                new CustomDialog(CustomDialog.CONFIRMATION_TYPE, 
                        "You cannot play this card",
                        cmdQueue, game);
        dialog.setVisible(true);
        
    }
    private ArrayList<Position> getAvaliblePositionToSelect()
    {
        if(game.getCurrentPlayer().isPlayingCard())
        {
            Card playingCard = game.getCardCommandFactory().getPlayingCard();
            switch(playingCard.getCardType()){
                case Card.HQCARD:
                {
                if(playingCard.getHQType() == Card.SUPPLY)
                {
                   return null;
                    /*
                    GetNonDeadUnits beside that 1 has moved
                    */
                }
                break;
                }
                case Card.UNIT:
                {
                    /*
                    calculate possible targets if we know playing Card Mode            
                    */                    
                    if(playingCard.getPlayingCardMode() > 0  )
                    {
                         game.getCardCommandFactory().setSelectedUnit(game.getSelectedUnit());
                         game.getCardCommandFactory().calculateAttackingPositions();    
                         return  game.getCardCommandFactory().getAttackingPositions();

                    }
                
                }    
                    
                
            }
                
        }
    return null;
    
    }
    
    private ArrayList<Position> getMovePositions(Card playingCard){
     
            ArrayList<Position> movePositions = null;
            if(gameGui.mapGui.isUnitSelected())
                {   
                    Unit selectedUnit = game.getSelectedUnit();
                    switch(playingCard.getCardType()){
                        
                        case Card.HQCARD :
                        {
                            switch(playingCard.getHQType()){
                           
                                case Card.FORCED_MARCH:
                                {
                                   movePositions = game.getOneSquareMovements(selectedUnit.getPosition()); 
                                   break;
                                }
                                case Card.SUPPLY:
                                {
                                   movePositions = game.getPossibleMovement(selectedUnit); 
                                   break;
                                }
                                
                            }
                            break;
                        }
     
                    }
                }
    return movePositions;
}
            
            
    
    
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
            
            cmdQueue.undoLastCommand();
                
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
                if(allowDrawOnDiscard.isSelected())//if  Debug is active draw cards after Discard
                {
                    client.send(gameGui.drawCards());
                    game.getCurrentPlayer().setDraw(false);
                }     
                setActionButtonText();
                this.repaint();
                break;
            }
            case Game.DRAW:
            {
                client.send(gameGui.drawCards());
                setActionButtonText();
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
            
            CustomDialog dialog = new CustomDialog(CustomDialog.YES_NO_TYPE, "Are You sure to end setup?", cmdQueue, game);
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
            CustomDialog.CONFIRMATION_TYPE, "Unit " + badPlacedUnit.getName() +  " is placed wrong", cmdQueue, game);
            }
          
        }
        
        else if (game.getPhase() == Game.RESTORATION)
        {
        Command endTurnCommand = new EndTurnCommand(game.getCurrentPlayer().getName());
        cmdQueue.storeAndExecuteAndSend(endTurnCommand);
        }
        else 
        {
        Command nextPhaseCommand = new NextPhaseCommand(game.getCurrentPlayer().getName(), game.getPhase()+ 1);
        cmdQueue.storeAndExecuteAndSend(nextPhaseCommand);
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
         TestWindow testWindow = new TestWindow(game, this, game.getPhase(), cmdQueue);
         testWindow.setBounds(50, 100, testWindow.getWidth(), testWindow.getHeight());
         testWindow.setVisible(true);
         game.setPhase(Game.DISCARD);
         refreshAll();
    }//GEN-LAST:event_FindCardActionPerformed

    private void MoveToTableCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MoveToTableCommandActionPerformed
        if(game.getCardCommandFactory().getCurrentPlayedCard()!=null){
            Command moveToTable = game.getCardCommandFactory().createCardCommand();
            
            cmdQueue.storeAndExecuteAndSend(moveToTable);
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

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    File pdfFile = new File("resources\\ManoeuvreRules-2010.pdf");   
    if(isDesktopSupported()){
        try {
            Desktop.getDesktop().open(pdfFile);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog( null
                                         , "An error happened trying to open file : " + pdfFile.getPath()
                                         , "IOException"
                                         , JOptionPane.WARNING_MESSAGE
            );
        }
    }
    else{
        JOptionPane.showMessageDialog( null
                                         , "This is not supported on your Operating System: " 
                                         , "IOException"
                                         , JOptionPane.WARNING_MESSAGE
            );
    }
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItemAmbushActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAmbushActionPerformed
        ImageIcon  imageIcon = new ImageIcon();
        try {
            Image imgFull = ImageIO.read(new File("resources\\cards\\AUAmbush.jpg"));
            imageIcon = new ImageIcon(imgFull);
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog( null
                                         , "6 Cards\n\n"
                                                 + "2 in Austria\n"
                                                 + "4 in USA"
                                         , "Card Description"
                                         , JOptionPane.PLAIN_MESSAGE
                                         , imageIcon
        );
    }//GEN-LAST:event_jMenuItemAmbushActionPerformed

    private void jMenuItemCommAttackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCommAttackActionPerformed
        ImageIcon  imageIcon = new ImageIcon();
        try {
            Image imgFull = ImageIO.read(new File("resources\\cards\\AUCattk.JPG"));
            imageIcon = new ImageIcon(imgFull);
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog( null
                                         , "10 Cards\n\n"
                                                 + "1 in Britain\n"
                                                 + "1 in Austria\n"
                                                 + "4 in Ottoman\n"
                                                 + "1 in Prussia\n"
                                                 + "3 in Russia"
                                         , "Card Description"
                                         , JOptionPane.PLAIN_MESSAGE
                                         , imageIcon
        );
    }//GEN-LAST:event_jMenuItemCommAttackActionPerformed

    private void jMenuItemGuerrillasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGuerrillasActionPerformed
        ImageIcon  imageIcon = new ImageIcon();
        try {
            Image imgFull = ImageIO.read(new File("resources\\cards\\AUGuer.JPG"));
            imageIcon = new ImageIcon(imgFull);
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog( null
                                         , "9 Cards\n\n"
                                                 + "2 in Austria\n"
                                                 + "1 in Prussia\n"
                                                 + "1 in Russia\n"
                                                 + "5 in Spain" 
                                         , "Card Description"
                                         , JOptionPane.PLAIN_MESSAGE
                                         , imageIcon
        );        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItemGuerrillasActionPerformed

    private void jMenuItemRedoubtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRedoubtActionPerformed
        ImageIcon  imageIcon = new ImageIcon();
        try {
            Image imgFull = ImageIO.read(new File("resources\\cards\\AURedbt.JPG"));
            imageIcon = new ImageIcon(imgFull);
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog( null
                                         , "18 Cards\n\n"
                                                 + "2 in Britain\n"
                                                 + "3 in Austria\n"
                                                 + "1 in Frace\n"
                                                 + "3 in Prussia\n"
                                                 + "2 in Russia\n"
                                                 + "4 in Spain\n"
                                                 + "3 in USA"  
                                         , "Card Description"
                                         , JOptionPane.PLAIN_MESSAGE
                                         , imageIcon
        );    
    }//GEN-LAST:event_jMenuItemRedoubtActionPerformed

    private void jMenuItemRegroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRegroupActionPerformed
        ImageIcon  imageIcon = new ImageIcon();
        try {
            Image imgFull = ImageIO.read(new File("resources\\cards\\OTRegroup.JPG"));
            imageIcon = new ImageIcon(imgFull);
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog( null
                                         , "7 Cards\n\n"
                                                 + "7 in Ottoman"
                                                   
                                         , "Card Description"
                                         , JOptionPane.PLAIN_MESSAGE
                                         , imageIcon
        );  
    }//GEN-LAST:event_jMenuItemRegroupActionPerformed

    private void jMenuItemSkirmishActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSkirmishActionPerformed
        ImageIcon  imageIcon = new ImageIcon();
        try {
            Image imgFull = ImageIO.read(new File("resources\\cards\\BRSkirm.JPG"));
            imageIcon = new ImageIcon(imgFull);
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog( null
                                         , "9 Cards\n\n"
                                                 + "1 in Britain\n"
                                                 + "2 in Austria\n"
                                                 + "1 in Frace\n"
                                                 + "2 in Prussia\n"
                                                 + "1 in Russia\n"
                                                 + "1 in Spain\n"
                                                 + "1 in USA"  
                                         , "Card Description"
                                         , JOptionPane.PLAIN_MESSAGE
                                         , imageIcon
        );  
    }//GEN-LAST:event_jMenuItemSkirmishActionPerformed

    private void jMenuItemSupplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSupplyActionPerformed
        ImageIcon  imageIcon = new ImageIcon();
        try {
            Image imgFull = ImageIO.read(new File("resources\\cards\\BRSuply.JPG"));
            imageIcon = new ImageIcon(imgFull);
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog( null
                                         , "25 Cards\n\n"
                                                 + "4 in Britain\n"
                                                 + "4 in Austria\n"
                                                 + "4 in Frace\n"
                                                 + "4 in Prussia\n"
                                                 + "4 in Russia\n"
                                                 + "2 in Spain\n"
                                                 + "3 in USA"   
                                         , "Card Description"
                                         , JOptionPane.PLAIN_MESSAGE
                                         , imageIcon
        );  
    }//GEN-LAST:event_jMenuItemSupplyActionPerformed

    private void jMenuItemWithdrawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemWithdrawActionPerformed
        ImageIcon  imageIcon = new ImageIcon();
        try {
            Image imgFull = ImageIO.read(new File("resources\\cards\\BRWdraw.JPG"));
            imageIcon = new ImageIcon(imgFull);
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog( null
                                         , "23 Cards\n\n"
                                                 + "3 in Britain\n"
                                                 + "2 in Austria\n"
                                                 + "4 in Frace\n"
                                                 + "5 in Ottoman\n"
                                                 + "2 in Prussia\n"
                                                 + "2 in Russia\n"
                                                 + "3 in Spain\n"
                                                 + "2 in USA"  
                                         , "Card Description"
                                         , JOptionPane.PLAIN_MESSAGE
                                         , imageIcon
        );  
    }//GEN-LAST:event_jMenuItemWithdrawActionPerformed

    private void jMenuItemForced_MarchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemForced_MarchActionPerformed
        ImageIcon  imageIcon = new ImageIcon();
        try {
            Image imgFull = ImageIO.read(new File("resources\\cards\\FRFMarch.JPG"));
            imageIcon = new ImageIcon(imgFull);
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog( null
                                         , "8 Cards\n\n"
                                                 + "2 in Britain\n"
                                                 + "3 in Frace\n"
                                                 + "3 in Prussia"
                                         , "Card Description"
                                         , JOptionPane.PLAIN_MESSAGE
                                         , imageIcon
        ); 
    }//GEN-LAST:event_jMenuItemForced_MarchActionPerformed

    private void jMenuItemEngineersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemEngineersActionPerformed
        ImageIcon  imageIcon = new ImageIcon();
        try {
            Image imgFull = ImageIO.read(new File("resources\\cards\\USEng.JPG"));
            imageIcon = new ImageIcon(imgFull);
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog( null
                                         , "2 Cards\n\n"
                                                 + "1 in Britain\n"
                                                 + "(Royal Engineers)\n"
                                                 + "1 in USA"
                                         , "Card Description"
                                         , JOptionPane.PLAIN_MESSAGE
                                         , imageIcon
        ); 
    }//GEN-LAST:event_jMenuItemEngineersActionPerformed

    private void jMenuItemScoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemScoutActionPerformed
        ImageIcon  imageIcon = new ImageIcon();
        try {
            Image imgFull = ImageIO.read(new File("resources\\cards\\USScout.JPG"));
            imageIcon = new ImageIcon(imgFull);
        } catch (IOException ex) {
            Logger.getLogger(UnitGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        JOptionPane.showMessageDialog( null
                                         , "2 Cards\n\n"
                                                 + "1 in Britain\n"
                                                 + "(Spy)\n"
                                                 + "1 in USA\n"
                                                 + "(Scout)"
                                         , "Card Description"
                                         , JOptionPane.PLAIN_MESSAGE
                                         , imageIcon
        ); 
    }//GEN-LAST:event_jMenuItemScoutActionPerformed
 
//    public void clientSend(Message message){
//        clOTRegroup.JPGient.send(gameGui.discardSelCards());
//        this.repaint();OTRegroup.JPG
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
    private javax.swing.JCheckBoxMenuItem allowDrawOnDiscard;
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
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItemAmbush;
    private javax.swing.JMenuItem jMenuItemCommAttack;
    private javax.swing.JMenuItem jMenuItemEngineers;
    private javax.swing.JMenuItem jMenuItemForced_March;
    private javax.swing.JMenuItem jMenuItemGuerrillas;
    private javax.swing.JMenuItem jMenuItemRedoubt;
    private javax.swing.JMenuItem jMenuItemRegroup;
    private javax.swing.JMenuItem jMenuItemScout;
    private javax.swing.JMenuItem jMenuItemSkirmish;
    private javax.swing.JMenuItem jMenuItemSupply;
    private javax.swing.JMenuItem jMenuItemWithdraw;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
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
