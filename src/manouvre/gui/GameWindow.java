/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Color;
import java.awt.Desktop;
import static java.awt.Desktop.isDesktopSupported;
import manouvre.network.client.Message;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.text.DefaultCaret;
import manouvre.game.Game;
import manouvre.game.Position;
import manouvre.interfaces.FrameInterface;
import manouvre.interfaces.ClientInterface;
import manouvre.commands.CommandQueue;
import javax.swing.UIManager;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import manouvre.game.Card;
import java.util.Observable;
import java.util.Observer;
import org.apache.logging.log4j.LogManager;
import manouvre.events.ButtonEventObserver;
import manouvre.events.ButtonActions;
import manouvre.events.PanelsEventObserver;
import manouvre.state.PlayerState;
import static java.lang.Math.abs;
import static java.lang.Math.abs;
import static java.lang.Math.abs;
import static java.lang.Math.abs;

/**
 *
 * @author Piotr
 */
public class GameWindow extends javax.swing.JFrame  implements FrameInterface, Observer{

    /*
    Network variables
    */
    private ClientInterface client;
    private Thread clientThread;

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(GameWindow.class.getName());

    private int windowMode;

    private Game game;
    private GameGUI gameGui;
    
    private Image bgImage;
  
    private ButtonEventObserver buttonEventsObserver;
    private PanelsEventObserver panelObserver;
    
    private ButtonActions butttonActions;
    
    public  CommandQueue cmdQueue;
    private  CommandLogger cmdLogger;
    
    private PlayerState playerState;
    
    
    public GameWindow(Game game, int windowMode, CommandQueue cmdQueue) throws IOException{
        /*
         Game has generated players army , hand and comes from serwver
         */
        this.game = game;
        this.windowMode = windowMode;
        this.cmdQueue = cmdQueue;
        this.cmdLogger = new CommandLogger(this);
        
        this.cmdQueue.addObserver(cmdLogger);
        this.cmdQueue.addObserver(this);
        
        
        
        playerState = new PlayerState(game);
        
        /*Sets current Player based on HOST/GUEST settings*/
        game.setCurrentPlayer(windowMode);
        
        /*
        Creates new GUI respects HOST/GUEST settings
        */
        gameGui = new GameGUI(this.game, playerState, windowMode);
        bgImage = ImageIO.read( new File("resources\\backgrounds\\24209cb208yezho.jpg"));

        String title = "Manouvre, " + (windowMode== CreateRoomWindow.AS_HOST  
                            ? game.getHostPlayer().getName() + " as HOST" 
                            : game.getGuestPlayer().getName() + " as GUEST" );

        title = title + (game.getCurrentPlayer().isFirst() ? " and first player" : " and second player");
        this.setTitle(title);

        game.setInfoBarText(title);
        
        UIManager.put("TabbedPane.contentOpaque", false);  
         
        initComponents();
        initButtons();

        setPhaseLabelText();
        setPlayerInfoValues();

        
        buttonEventsObserver = new ButtonEventObserver(game, actionButton, buttonYes, buttonNo, buttonToNextPhase);
        panelObserver = new PanelsEventObserver(game, currentPlayerPanel, opponentPlayerPanel, infoPanel, tablePanel, buttonsPanel);
        butttonActions = new ButtonActions(actionButton, buttonYes, buttonNo, this.game, this.cmdQueue, playerState );
        
                
        this.addWindowListener(new ManouvreWindowListener(game, client, clientThread));
      
        DefaultCaret caret = (DefaultCaret)chatTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }
    
    private final void initButtons(){
        buttonYes.setVisible(false);
        buttonNo.setVisible(false);
        actionButton.setVisible(false);
        buttonToNextPhase.setText("End Setup");
        buttonToNextPhase.setVisible(true);
        buttonToNextPhase.setEnabled(true);
        
    }
    @Override
    public void update(Observable o, Object arg) {
        refreshAll();  
        repaint();
    }
     

    private void drawMainBackground(Graphics g){
     g.drawImage(bgImage, 0, 0, this.getSize().width, this.getSize().height,Color.red, null);
    }
     
    private void paintInfoPanel(Graphics g){
    
        gameGui.paintInfoPanel(g);
    }
    
    private void drawMap(Graphics g )                   
    {
        gameGui.drawMap(g,windowMode );
    }
    
    public void refreshAll(){
        
        setPhaseLabelText();
        gameTurnCounter.setText(Integer.toString(game.getTurn()));
        setPlayerInfoValues();
        /*
        Updates gui for card sets
        */
        game.setShowOpponentHand(false);
        subphaseLabel.setText(game.getCombat().getState());

    }
     
    private void  buttonActionSetText(String text, boolean isActive){
    
            actionButton.setText(text);
            actionButton.setEnabled(isActive);
    }
    
    private void setPlayerInfoValues(){
    
        scoreLabelCurrPlayer.setText("Score: " + game.getCurrentPlayer().getScore());
        scoreOpponent.setText("Score :" + game.getOpponentPlayer().getScore());
        
        unitsKilledCurrPlayer.setText("Units killed: " + game.getCurrentPlayer().getUnitsKilled());
        unitsKilledOppPlayer.setText("Units killed: " + game.getOpponentPlayer().getUnitsKilled());
        
        drawPileCurrPlayer.setText("Draw Pile: " +  game.getCurrentPlayer().getDrawPile().getCardSetSize() );
        drawPileOppPlayer.setText("Draw Pile: " +  game.getOpponentPlayer().getDrawPile().getCardSetSize() );
        
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
    
     public void paintOpponentHand(Graphics g )                 
    {   
      gameGui.paintOpponentHand(g); 
    }

    
    private void paintDiscard(Graphics g, boolean paintOpponent)                 
    {   
        gameGui.paintDiscard(g, paintOpponent); 
        
    }
    
    private void paintDrawLeft(Graphics g, boolean paintOpponent) {
        gameGui.paintDrawLeft(g, paintOpponent); 
    }
    
    
    private void paintInfoBarPanel (Graphics g){
        //gameGui.paintInfoBarPanel(g);
        
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
        jMenuItem3 = new javax.swing.JMenuItem();
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
        rightSidePanel = new javax.swing.JPanel();
        tablePanel = new javax.swing.JPanel(){
            @Override
            public void paintComponent(Graphics g) {
                paintTablePanel(g);

            }
        }
        ;
        infoPanel = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                paintInfoPanel(g);

            }
        }
        ;
        bottomPanel = new javax.swing.JPanel();
        playersTabbedPane = new javax.swing.JTabbedPane();
        currentPlayerPanel = new javax.swing.JPanel();
        scoreLabelCurrPlayer = new javax.swing.JLabel();
        unitsKilledCurrPlayer = new javax.swing.JLabel();
        curPlayerFlag = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawCurrentPlayerFlag(g);

            }
        }
        ;
        drawPileCurrPlayer = new javax.swing.JLabel();
        discardPanel = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                paintDiscard(g, false);

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
        opponentPlayerPanel = new javax.swing.JPanel();
        scoreOpponent = new javax.swing.JLabel();
        discardPanel1 = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                paintDiscard(g, true);

            }
        }
        ;
        unitsKilledOppPlayer = new javax.swing.JLabel();
        opoPlayerFlag = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                drawOpponentPlayerFlag(g);

            }
        }
        ;
        ;
        drawPileOppPlayer = new javax.swing.JLabel();
        opponentHandPanel = new javax.swing.JPanel()
        {
            @Override
            public void paintComponent(Graphics g) {
                paintOpponentHand(g);

            }
        }
        ;
        buttonsPanel = new javax.swing.JPanel();
        buttonToNextPhase = new javax.swing.JButton();
        actionButton = new javax.swing.JButton();
        buttonYes = new javax.swing.JButton();
        buttonNo = new javax.swing.JButton();
        phaseLabel = new javax.swing.JLabel();
        phaseNameLabel = new javax.swing.JLabel();
        turnLabel = new javax.swing.JLabel();
        gameTurnCounter = new javax.swing.JLabel();
        subphaseLabel = new javax.swing.JLabel();
        chatPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        sendMessageButton = new javax.swing.JButton();
        sendText = new javax.swing.JTextField();
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
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        AttackingDialogMenu = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jCheckBoxMenuItem2 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem3 = new javax.swing.JCheckBoxMenuItem();
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

        jMenuItem3.setText("jMenuItem3");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        mainMapPanel.setPreferredSize(new java.awt.Dimension(600, 600));
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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mainMapPanelMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mainMapPanelMouseReleased(evt);
            }
        });

        javax.swing.GroupLayout mainMapPanelLayout = new javax.swing.GroupLayout(mainMapPanel);
        mainMapPanel.setLayout(mainMapPanelLayout);
        mainMapPanelLayout.setHorizontalGroup(
            mainMapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 666, Short.MAX_VALUE)
        );
        mainMapPanelLayout.setVerticalGroup(
            mainMapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        rightSidePanel.setOpaque(false);

        tablePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Table Panel"));
        tablePanel.setName(""); // NOI18N
        tablePanel.setOpaque(false);

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 603, Short.MAX_VALUE)
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 295, Short.MAX_VALUE)
        );

        infoPanel.setOpaque(false);

        javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
        infoPanel.setLayout(infoPanelLayout);
        infoPanelLayout.setHorizontalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        infoPanelLayout.setVerticalGroup(
            infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 218, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout rightSidePanelLayout = new javax.swing.GroupLayout(rightSidePanel);
        rightSidePanel.setLayout(rightSidePanelLayout);
        rightSidePanelLayout.setHorizontalGroup(
            rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightSidePanelLayout.createSequentialGroup()
                .addGroup(rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(infoPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        rightSidePanelLayout.setVerticalGroup(
            rightSidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightSidePanelLayout.createSequentialGroup()
                .addComponent(tablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tablePanel.getAccessibleContext().setAccessibleName("");

        bottomPanel.setOpaque(false);

        playersTabbedPane.setFocusCycleRoot(true);

        currentPlayerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(game.getCurrentPlayer().getNationAsString(false)+ (game.getCurrentPlayer().isFirst() ? " (First player)" : "")));
        currentPlayerPanel.setForeground(new java.awt.Color(153, 0, 102));
        currentPlayerPanel.setOpaque(false);
        currentPlayerPanel.setPreferredSize(new java.awt.Dimension(955, 250));

        scoreLabelCurrPlayer.setText("Score: " + game.getCurrentPlayer().getScore());

        unitsKilledCurrPlayer.setText("Units Killed: " + game.getCurrentPlayer().getUnitsKilled());

        curPlayerFlag.setPreferredSize(new java.awt.Dimension(64, 56));

        javax.swing.GroupLayout curPlayerFlagLayout = new javax.swing.GroupLayout(curPlayerFlag);
        curPlayerFlag.setLayout(curPlayerFlagLayout);
        curPlayerFlagLayout.setHorizontalGroup(
            curPlayerFlagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        curPlayerFlagLayout.setVerticalGroup(
            curPlayerFlagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
        );

        drawPileCurrPlayer.setText("Draw Pile: " + game.getCurrentPlayer().getDrawPile().size());

        discardPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Discard Pile"));
        discardPanel.setPreferredSize(new java.awt.Dimension(100, 25));

        javax.swing.GroupLayout discardPanelLayout = new javax.swing.GroupLayout(discardPanel);
        discardPanel.setLayout(discardPanelLayout);
        discardPanelLayout.setHorizontalGroup(
            discardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 141, Short.MAX_VALUE)
        );
        discardPanelLayout.setVerticalGroup(
            discardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

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
            .addGap(0, 700, Short.MAX_VALUE)
        );
        playerHandPanelLayout.setVerticalGroup(
            playerHandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout currentPlayerPanelLayout = new javax.swing.GroupLayout(currentPlayerPanel);
        currentPlayerPanel.setLayout(currentPlayerPanelLayout);
        currentPlayerPanelLayout.setHorizontalGroup(
            currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(unitsKilledCurrPlayer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                    .addComponent(curPlayerFlag, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scoreLabelCurrPlayer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(drawPileCurrPlayer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(playerHandPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(discardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
        );
        currentPlayerPanelLayout.setVerticalGroup(
            currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(currentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(discardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                    .addGroup(currentPlayerPanelLayout.createSequentialGroup()
                        .addComponent(curPlayerFlag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scoreLabelCurrPlayer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(unitsKilledCurrPlayer)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(drawPileCurrPlayer)
                        .addGap(0, 103, Short.MAX_VALUE))
                    .addComponent(playerHandPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        playersTabbedPane.addTab(game.getCurrentPlayer().getName(), currentPlayerPanel);
        currentPlayerPanel.getAccessibleContext().setAccessibleName(game.getCurrentPlayer().getName());

        opponentPlayerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(game.getOpponentPlayer().getNationAsString(false)+ (game.getOpponentPlayer().isFirst() ? " (First player)" : "")));
        opponentPlayerPanel.setForeground(new java.awt.Color(153, 0, 102));
        opponentPlayerPanel.setOpaque(false);

        scoreOpponent.setText("Score: " + game.getOpponentPlayer().getScore());

        discardPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Discard Pile"));
        discardPanel1.setPreferredSize(new java.awt.Dimension(100, 25));
        discardPanel1.setRequestFocusEnabled(false);

        javax.swing.GroupLayout discardPanel1Layout = new javax.swing.GroupLayout(discardPanel1);
        discardPanel1.setLayout(discardPanel1Layout);
        discardPanel1Layout.setHorizontalGroup(
            discardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 138, Short.MAX_VALUE)
        );
        discardPanel1Layout.setVerticalGroup(
            discardPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        unitsKilledOppPlayer.setText("Units Killed: " + game.getOpponentPlayer().getUnitsKilled());

        opoPlayerFlag.setPreferredSize(new java.awt.Dimension(64, 56));

        javax.swing.GroupLayout opoPlayerFlagLayout = new javax.swing.GroupLayout(opoPlayerFlag);
        opoPlayerFlag.setLayout(opoPlayerFlagLayout);
        opoPlayerFlagLayout.setHorizontalGroup(
            opoPlayerFlagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        opoPlayerFlagLayout.setVerticalGroup(
            opoPlayerFlagLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
        );

        drawPileOppPlayer.setText("Draw Pile: " + game.getOpponentPlayer().getDrawPile().size());

        opponentHandPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                opponentHandPanelMouseMoved(evt);
            }
        });
        opponentHandPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                opponentHandPanelMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                opponentHandPanelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                opponentHandPanelMouseExited(evt);
            }
        });

        javax.swing.GroupLayout opponentHandPanelLayout = new javax.swing.GroupLayout(opponentHandPanel);
        opponentHandPanel.setLayout(opponentHandPanelLayout);
        opponentHandPanelLayout.setHorizontalGroup(
            opponentHandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
        );
        opponentHandPanelLayout.setVerticalGroup(
            opponentHandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout opponentPlayerPanelLayout = new javax.swing.GroupLayout(opponentPlayerPanel);
        opponentPlayerPanel.setLayout(opponentPlayerPanelLayout);
        opponentPlayerPanelLayout.setHorizontalGroup(
            opponentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(opponentPlayerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(opponentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(scoreOpponent, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(drawPileOppPlayer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(opoPlayerFlag, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(unitsKilledOppPlayer, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(opponentHandPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(discardPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
        );
        opponentPlayerPanelLayout.setVerticalGroup(
            opponentPlayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(opponentPlayerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(opoPlayerFlag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scoreOpponent)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(unitsKilledOppPlayer, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(drawPileOppPlayer)
                .addContainerGap(97, Short.MAX_VALUE))
            .addComponent(opponentHandPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(discardPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
        );

        playersTabbedPane.addTab(game.getOpponentPlayer().getName(), opponentPlayerPanel);
        opponentPlayerPanel.getAccessibleContext().setAccessibleName(game.getOpponentPlayer().getName());

        buttonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Phase Panel"));
        buttonsPanel.setOpaque(false);

        buttonToNextPhase.setText("to Next Phase");
        buttonToNextPhase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonToNextPhaseActionPerformed(evt);
            }
        });

        actionButton.setText("action Name");
        actionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });

        buttonYes.setText("Yes");
        buttonYes.setEnabled(false);
        buttonYes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonYesActionPerformed(evt);
            }
        });

        buttonNo.setText("No");
        buttonNo.setEnabled(false);
        buttonNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonNoActionPerformed(evt);
            }
        });

        phaseLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        phaseLabel.setForeground(new java.awt.Color(255, 255, 255));
        phaseLabel.setText("Phase:");

        phaseNameLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        phaseNameLabel.setForeground(new java.awt.Color(255, 255, 255));
        phaseNameLabel.setText("Phase Name");

        turnLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        turnLabel.setForeground(new java.awt.Color(255, 255, 255));
        turnLabel.setText("Turn:");

        gameTurnCounter.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        gameTurnCounter.setForeground(new java.awt.Color(255, 255, 255));
        gameTurnCounter.setText("0");
        gameTurnCounter.setEnabled(false);

        subphaseLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        subphaseLabel.setForeground(new java.awt.Color(255, 255, 255));
        subphaseLabel.setText("Subphase: NO_COMBAT");

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(buttonsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(buttonNo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(buttonsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonYes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(buttonsPanelLayout.createSequentialGroup()
                                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(buttonsPanelLayout.createSequentialGroup()
                                        .addComponent(turnLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(gameTurnCounter, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(subphaseLabel)
                                    .addGroup(buttonsPanelLayout.createSequentialGroup()
                                        .addComponent(phaseLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(phaseNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(buttonsPanelLayout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(actionButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(buttonToNextPhase, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(turnLabel)
                    .addComponent(gameTurnCounter))
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phaseLabel)
                    .addComponent(phaseNameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(subphaseLabel)
                .addGap(18, 18, 18)
                .addComponent(buttonNo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonYes)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(actionButton)
                .addGap(7, 7, 7)
                .addComponent(buttonToNextPhase, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout bottomPanelLayout = new javax.swing.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(playersTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 957, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(bottomPanelLayout.createSequentialGroup()
                .addComponent(playersTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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

        sendText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendTextActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout chatPanelLayout = new javax.swing.GroupLayout(chatPanel);
        chatPanel.setLayout(chatPanelLayout);
        chatPanelLayout.setHorizontalGroup(
            chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(chatPanelLayout.createSequentialGroup()
                        .addComponent(sendText)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sendMessageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20))
                    .addGroup(chatPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 591, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        chatPanelLayout.setVerticalGroup(
            chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(chatPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
                .addGap(8, 8, 8)
                .addGroup(chatPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sendText, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendMessageButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout mainWindowPanelLayout = new javax.swing.GroupLayout(mainWindowPanel);
        mainWindowPanel.setLayout(mainWindowPanelLayout);
        mainWindowPanelLayout.setHorizontalGroup(
            mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainWindowPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bottomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(mainWindowPanelLayout.createSequentialGroup()
                        .addComponent(mainMapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 666, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rightSidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(chatPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(0, 0, 0))
        );
        mainWindowPanelLayout.setVerticalGroup(
            mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainWindowPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mainMapPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 682, Short.MAX_VALUE)
                    .addGroup(mainWindowPanelLayout.createSequentialGroup()
                        .addComponent(rightSidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chatPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jMenu1.setText("Debug");
        jMenu1.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu1MenuSelected(evt);
            }
        });
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

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

        jCheckBoxMenuItem1.setSelected(true);
        jCheckBoxMenuItem1.setText("freeMove");
        jCheckBoxMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem1ActionPerformed(evt);
            }
        });
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

        jMenuItem4.setText("setDefenceCardsAvailable");
        jMenuItem4.setToolTipText("Attacking card must be selected");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenu1.add(jMenu3);

        jMenuItem5.setText("jMenuItem5");
        jMenu1.add(jMenuItem5);

        jMenu7.setText("During Attack");

        AttackingDialogMenu.setText("AttackingDialog BOMBARD");
        AttackingDialogMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AttackingDialogMenuActionPerformed(evt);
            }
        });
        jMenu7.add(AttackingDialogMenu);

        jMenuItem7.setText("AttackingDialog ASSAULT");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu7.add(jMenuItem7);

        jMenu1.add(jMenu7);

        jMenu8.setText("Change Phase to");

        jMenuItem6.setText("Discard");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu8.add(jMenuItem6);

        jMenu1.add(jMenu8);

        jCheckBoxMenuItem2.setSelected(true);
        jCheckBoxMenuItem2.setText("Lock/Unlock");
        jCheckBoxMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jCheckBoxMenuItem2);

        jCheckBoxMenuItem3.setSelected(true);
        jCheckBoxMenuItem3.setText("ActivePlayer/NotActive");
        jCheckBoxMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jCheckBoxMenuItem3);

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
                .addGap(953, 953, 953)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainWindowPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(mainWindowPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentHidden
    

    
    private void playerHandPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseClicked
 
            Card cardClicked = gameGui.cardSetsGUI.getCardFromMousePosition(evt.getPoint().x,evt.getPoint().y);
            
            if(cardClicked != null)
            {   
                playerState.cardStateHandler.handle(cardClicked, game);
                LOGGER.debug(game.getCurrentPlayer().getName() + " clicked card: " + cardClicked);
            }
            
            repaint();
            
    }//GEN-LAST:event_playerHandPanelMouseClicked

            
    private void playerHandPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseEntered
         // TODO add your handling code here:
         
    }//GEN-LAST:event_playerHandPanelMouseEntered

    private void playerHandPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseExited
         // TODO add your handling code here:
        // repaint(CURR_X, CURR_Y, CURR_W, CURR_H);
    }//GEN-LAST:event_playerHandPanelMouseExited

    private void playerHandPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playerHandPanelMouseMoved
        
        int handMouseCoorX,handMouseCoorY;
        int handMouseCoorXdeaf=0;
        int handMouseCoorYdeaf=0;
        int deafband=20; //repaint after mouse change of
        int repaintAddedArea=20;//refreash little more than just the sqare
         final int CURR_X = playerHandPanel.getX();
         final int CURR_Y = playerHandPanel.getY();
         final int CURR_W = playerHandPanel.getWidth()+repaintAddedArea;
         final int CURR_H = playerHandPanel.getHeight()+3*repaintAddedArea;
        handMouseCoorX = evt.getPoint().x;
        handMouseCoorY = evt.getPoint().y;

        gameGui.cardSetsGUI.setMouseOverCard(handMouseCoorX, handMouseCoorY);
        if(abs(handMouseCoorX-handMouseCoorXdeaf)>deafband){  //change repainting step
            handMouseCoorXdeaf=handMouseCoorX;
            //repaint(CURR_X, CURR_Y, CURR_W, CURR_H);
            repaint();
        }

        if(abs(handMouseCoorY-handMouseCoorYdeaf)>deafband){
           handMouseCoorYdeaf=handMouseCoorY; 
           repaint();
        }

    
 
    }//GEN-LAST:event_playerHandPanelMouseMoved

    private void mainMapPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainMapPanelMouseClicked
        
        
        
    }//GEN-LAST:event_mainMapPanelMouseClicked
    private void mainMapPanelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainMapPanelMouseReleased
  //nothing
    }//GEN-LAST:event_mainMapPanelMouseReleased
          
    private void buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonActionPerformed
        butttonActions.buttonActionPerformed(evt);
    }//GEN-LAST:event_buttonActionPerformed

    private void buttonToNextPhaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonToNextPhaseActionPerformed
        butttonActions.buttonToNextPhaseActionPerformed(evt);
    }//GEN-LAST:event_buttonToNextPhaseActionPerformed

    private void sendMessageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendMessageButtonActionPerformed
        String msg = sendText.getText();
        String target = "All";
        
        printOnChat(msg);
        if(!msg.isEmpty() && !target.isEmpty()){
            sendText.setText("");
            client.send(new Message(Message.CHAT_IN_ROOM, game.getCurrentPlayer().getName(), msg, game.getOpponentPlayer().getName()));
        }
    }//GEN-LAST:event_sendMessageButtonActionPerformed

    private void FindCardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FindCardActionPerformed
         TestWindow testWindow = new TestWindow(game, this, game.getPhase(), cmdQueue, gameGui);
         testWindow.setBounds(50, 100, testWindow.getWidth(), testWindow.getHeight());
         testWindow.setVisible(true);
         game.setPhase(Game.DISCARD);
         refreshAll();
    }//GEN-LAST:event_FindCardActionPerformed

    private void MoveToTableCommandActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MoveToTableCommandActionPerformed
                  
                       
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
        Position movedPos = Position.getPositionFromMouse(x, y, windowMode);
        gameGui.setHoverPosition(movedPos);
        repaint();
   
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

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
       
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenu1MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu1MenuSelected
        game.freeMove = !game.freeMove;
        repaint();// TODO add your handling code here:
    }//GEN-LAST:event_jMenu1MenuSelected
    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
      
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void AttackingDialogMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AttackingDialogMenuActionPerformed
        // TEMP btestfalse
       
       
    }//GEN-LAST:event_AttackingDialogMenuActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        game.setPhase(Game.DISCARD);
        refreshAll();
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
     
       
                                        
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void sendTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendTextActionPerformed
        String msg = sendText.getText();
        String target = game.getOpponentPlayer().getName();
        if(!msg.isEmpty() && !target.isEmpty()){
          
            client.send(new Message(Message.CHAT_IN_ROOM, game.getCurrentPlayer().getName(), msg, target));
        }
        sendText.setText("");
    }//GEN-LAST:event_sendTextActionPerformed

    private void mainMapPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainMapPanelMousePressed

        int x = evt.getPoint().x;
        int y = evt.getPoint().y;
        Position clickedPos = Position.getPositionFromMouse(x, y, windowMode);
        playerState.mapStateHandler.handle(clickedPos, game, cmdQueue);
        
        repaint();
       
    }//GEN-LAST:event_mainMapPanelMousePressed

    private void buttonYesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonYesActionPerformed
        butttonActions.buttonYesActionPerformed(evt);
    }//GEN-LAST:event_buttonYesActionPerformed

    private void buttonNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonNoActionPerformed
        butttonActions.buttonNoActionPerformed(evt);
        
    }//GEN-LAST:event_buttonNoActionPerformed

    private void jCheckBoxMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxMenuItem1ActionPerformed

    private void jCheckBoxMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxMenuItem2ActionPerformed

    private void jCheckBoxMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxMenuItem3ActionPerformed

    private void opponentHandPanelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opponentHandPanelMouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_opponentHandPanelMouseMoved

    private void opponentHandPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opponentHandPanelMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_opponentHandPanelMouseClicked

    private void opponentHandPanelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opponentHandPanelMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_opponentHandPanelMouseEntered

    private void opponentHandPanelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_opponentHandPanelMouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_opponentHandPanelMouseExited
 
           
    @Override
    public void printOnChat(String inString)    {
    
    chatTextArea.append(inString+ "\n");
    }
        
   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem AttackingDialogMenu;
    private javax.swing.JMenuItem FindCard;
    private javax.swing.JMenuItem MoveToTableCommand;
    javax.swing.JButton actionButton;
    private javax.swing.JCheckBoxMenuItem allowDrawOnDiscard;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton buttonNo;
    private javax.swing.JButton buttonToNextPhase;
    private javax.swing.JButton buttonYes;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JPanel chatPanel;
    private javax.swing.JTextArea chatTextArea;
    private javax.swing.JCheckBoxMenuItem checkLos;
    private javax.swing.JCheckBoxMenuItem checkRetreat;
    private javax.swing.JPanel curPlayerFlag;
    private javax.swing.JPanel currentPlayerPanel;
    private javax.swing.JPanel discardPanel;
    private javax.swing.JPanel discardPanel1;
    private javax.swing.JLabel drawPileCurrPlayer;
    private javax.swing.JLabel drawPileOppPlayer;
    private javax.swing.JLabel gameTurnCounter;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem2;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem3;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
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
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel mainMapPanel;
    private javax.swing.JPanel mainWindowPanel;
    private javax.swing.JPanel opoPlayerFlag;
    private javax.swing.JPanel opponentHandPanel;
    private javax.swing.JPanel opponentPlayerPanel;
    private javax.swing.JLabel phaseLabel;
    private javax.swing.JLabel phaseNameLabel;
    private javax.swing.JPanel playerHandPanel;
    private javax.swing.JTabbedPane playersTabbedPane;
    private javax.swing.JPanel rightSidePanel;
    private javax.swing.JLabel scoreLabelCurrPlayer;
    private javax.swing.JLabel scoreOpponent;
    private javax.swing.JButton sendMessageButton;
    private javax.swing.JTextField sendText;
    private javax.swing.JLabel subphaseLabel;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JLabel turnLabel;
    private javax.swing.JLabel unitsKilledCurrPlayer;
    private javax.swing.JLabel unitsKilledOppPlayer;
    // End of variables declaration//GEN-END:variables

  



}
