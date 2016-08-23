/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import manouvre.game.Game;
import manouvre.game.Player;
import manouvre.game.Position;
import manouvre.game.Terrain;
import manouvre.game.Unit;
import manouvre.game.interfaces.PositionInterface;

/**
 *
 * @author Piotr
 */
public class ClientUI extends javax.swing.JFrame {

    /**
     * Gui objects
     */
    MapGUI map ;
    ArrayList<UnitGUI> unitsGUI = new ArrayList<UnitGUI>();  
    /*
    Game object
    */
    Game game;
    
    /**
     * Creates new form ClientUI
     */
    public ClientUI() throws IOException {
        initComponents();
        game = new Game();
        game.generateMap();
        map = new MapGUI(game.getMap());
        
        
        generateUnits();

    }

    private void generateUnits()
    {
        
    for (int i=0;i<8;i++)
    {
        
        Unit unit = new Unit(i+1);
        unit.setPos(new Position (i,1));
        
        unitsGUI.add(new UnitGUI(unit));
        
        game.placeUnit(new Player("Piotr"), unit);
        
        
        
    }
    
    }
    
 
    
    
    private void drawMap(Graphics g )
                      
    {
        int gap = 5;
        // draw background
        //g.drawImage(this.imgBackground, 0, 0, null);
        
        
        
        // draw terrain
        for(TerrainGUI terrainGUI: this.map.getTerrainsGUI())        
        {
               g.drawImage(terrainGUI.getImg(),
                    terrainGUI.getPos().getMouseX(),
                   terrainGUI.getPos().getMouseY(), null);
                
            }
          
        
        /*
        Draws selection
        */
        if (map.isUnitSelected())
        for(Terrain terrain : game.getMap().getTerrainz()  )  
        {
 
            if(terrain.isSelected()){
                    g.drawRoundRect(
                        terrain.getPos().getMouseX() + gap
                        ,terrain.getPos().getMouseY() + gap
                        , MapGUI.SQUARE_WIDTH - 2*gap , MapGUI.SQUARE_HEIGHT - 2*gap,
                        10,10);
                    
                    System.out.println("Position " + terrain.getPos());
                /*
                Draw AdjencedSpace /Move
                */
                
                    if(! terrain.getIsOccupiedByUnit() )
                    {
                    ArrayList<Position> adjencedPositions = terrain.getPos().getAdjencedPositions();
                    
                    System.out.println(terrain.getPos().toString());
                    
                    g.setColor(Color.red);
                    
                    
                    for(int k = 0 ;k < adjencedPositions.size();k++)
                    {
                  
                    g.drawRoundRect(
                        adjencedPositions.get(k).getMouseX() + gap 
                        ,adjencedPositions.get(k).getMouseY() + gap
                        , MapGUI.SQUARE_WIDTH - 2*gap , MapGUI.SQUARE_HEIGHT - 2*gap,
                        10,10);
                                        
                                        
                    
                    }
                    }
                    else 
                    
                    {
                        System.out.println("manouvre.gui.ClientUI.drawMap() : " + game.getUnitAtPosition(terrain.getPos()).toString()  );
                        ArrayList<Position> movePositions = 
                                game.getPossibleMovement(
                                game.getUnitAtPosition(terrain.getPos())
                                );
                        
                        for(Position drawMovePosion: movePositions){
                        g.setColor(Color.blue);
                          g.drawRoundRect(
                        drawMovePosion.getMouseX() + gap 
                        ,drawMovePosion.getMouseY() + gap
                        , MapGUI.SQUARE_WIDTH - 2*gap , MapGUI.SQUARE_HEIGHT - 2*gap,
                        10,10);
                                    
                        
                        }
                        
                    }
                    
                    
                }

            
        }
       
        
       /*
        Draw units
        */
        int gapUnit = 7;
        for (UnitGUI drawUnit: unitsGUI)
        {
            g.drawImage(
                drawUnit.getImg(),
                drawUnit.getUnit().getPos().getMouseX() + gapUnit,
                drawUnit.getUnit().getPos().getMouseY() + gapUnit,
                46,
                46,
                null);

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
        playerHandPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        discardPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        gameLogPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        moveButton = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();

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
                .addGap(0, 505, Short.MAX_VALUE))
        );

        jLabel1.setText("Hand");

        javax.swing.GroupLayout playerHandPanelLayout = new javax.swing.GroupLayout(playerHandPanel);
        playerHandPanel.setLayout(playerHandPanelLayout);
        playerHandPanelLayout.setHorizontalGroup(
            playerHandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(playerHandPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(502, Short.MAX_VALUE))
        );
        playerHandPanelLayout.setVerticalGroup(
            playerHandPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(playerHandPanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 143, Short.MAX_VALUE))
        );

        jLabel2.setText("Cards");

        javax.swing.GroupLayout discardPanelLayout = new javax.swing.GroupLayout(discardPanel);
        discardPanel.setLayout(discardPanelLayout);
        discardPanelLayout.setHorizontalGroup(
            discardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(discardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(388, Short.MAX_VALUE))
        );
        discardPanelLayout.setVerticalGroup(
            discardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(discardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addGap(0, 0, Short.MAX_VALUE))
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

        chatTextArea.setColumns(20);
        chatTextArea.setRows(5);
        chatTextArea.setText("Chat");
        jScrollPane1.setViewportView(chatTextArea);

        jLabel3.setText("Players");

        jButton1.setText("Send");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        moveButton.setText("Move");
        moveButton.setEnabled(false);
        moveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveButtonActionPerformed(evt);
            }
        });

        jButton3.setText("Assault");
        jButton3.setEnabled(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Ambush");
        jButton4.setEnabled(false);

        jButton5.setText("Volley");
        jButton5.setEnabled(false);

        jButton6.setText("Bombard");
        jButton6.setEnabled(false);

        jButton7.setText("Draw");
        jButton7.setEnabled(false);

        jButton8.setText("Discard");
        jButton8.setEnabled(false);

        jButton9.setText("Restore");
        jButton9.setEnabled(false);

        jButton10.setText("Yes");

        jButton11.setText("No");

        jTextField1.setText("Yes/No decision text");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jLabel9.setText("Opponent Hand");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(playerHandPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(mainMapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(127, 127, 127)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(jTextField1)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(gameLogPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                .addComponent(moveButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jButton8, javax.swing.GroupLayout.Alignment.LEADING))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jButton3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton4)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButton6))
                                            .addComponent(jButton9)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel3))
                                        .addGap(0, 0, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(jButton10)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(discardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(220, 220, 220)
                            .addComponent(jLabel5))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(22, 22, 22)
                            .addComponent(mainMapPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel9)
                        .addGap(81, 81, 81)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gameLogPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(moveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jButton4)
                            .addComponent(jButton5)
                            .addComponent(jButton6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton10)
                            .addComponent(jButton11))
                        .addGap(8, 8, 8)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(playerHandPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(discardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentHidden

    private void mainMapPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mainMapPanelMouseClicked
                int x = evt.getPoint().x;
		int y = evt.getPoint().y;
		                
                if(! map.isUnitSelected() )
		for(TerrainGUI terrainGUI: map.getTerrainsGUI())  
                {
                       
                       terrainGUI.setSelected(false);
                       game.getMap().getTileAtIndex(terrainGUI.getPos().getX(), terrainGUI.getPos().getY()).setSelected(false);
     			if(mouseOverPiece(terrainGUI,x,y))
                        {   
  				terrainGUI.setSelected(true);
                                Position selectedPosition = terrainGUI.getPos();
                                
                                if(game.checkUnitAtPosition(selectedPosition) ) {
                                  
                                    map.setUnitSelected(true);
                                    getUnitGuiOnMapGui(selectedPosition).setSelected(true);
                                    
                                }
                                
                                game.getMap().getTileAtIndex(terrainGUI.getPos().getX(), terrainGUI.getPos().getY()).setSelected(true);
                                this.repaint();
				
                        }
                   
                    }
                /*
                If unit is selected find which unit to move and move to 
                */
                else  {
                    Unit selectedUnit = getSelectedUnit().getUnit();
                    Position clickedPosition = new Position(  PositionInterface.convertMouseXToX(x)   , PositionInterface.convertMouseYToY(y)) ;
                    
                    System.out.println("manouvre.gui.ClientUI.mainMapPanelMouseClicked().clickedPosition :" + clickedPosition) ;
                    
                    ArrayList<Position> movePositions =                 
                    game.getPossibleMovement(selectedUnit);
                    
                    for(Position checkPosition: movePositions){
                    
                        if(checkPosition.equals(clickedPosition))
                        
                            //Move in game and GUI
                            game.moveUnit(game.getUnitAtPosition(checkPosition), clickedPosition);
                            
                             this.getSelectedUnit().getUnit().setPos(clickedPosition);
                            //Unselect all
                            unselectAllUnits();
                            //exit loop
                            repaint();
                            break;
                               
                    }
                    
                    
                    
                    
                   // game.moveUnit(  , newPosition);
                    
                }
                    
                    
        
        
        
    }//GEN-LAST:event_mainMapPanelMouseClicked
    private UnitGUI getUnitGuiOnMapGui(Position position){
    
           for(UnitGUI unitSearch: this.unitsGUI){
        
            if(unitSearch.getUnit().getPos().equals(position))
            {
                return unitSearch;
              }
            
        
        }
              
        return null;
    
    }
    
    private UnitGUI getSelectedUnit(){
    
           for(UnitGUI unitSearch: this.unitsGUI){
        
            if(unitSearch.isSelected())
            {
                return unitSearch;
              }
            
        
        }
              
        return null;
    
    }
    
    private void unselectAllUnits()
        
    {
        this.unitsGUI.stream().forEach((unit) -> {
            unit.setSelected(false);
        });
        
        map.setUnitSelected(false);
    }
    
    
    private void moveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_moveButtonActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed
    
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
            java.util.logging.Logger.getLogger(ClientUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ClientUI().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(ClientUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

   
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea chatTextArea;
    private javax.swing.JPanel discardPanel;
    private javax.swing.JPanel gameLogPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
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
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JPanel mainMapPanel;
    private javax.swing.JButton moveButton;
    private javax.swing.JPanel playerHandPanel;
    // End of variables declaration//GEN-END:variables



}
