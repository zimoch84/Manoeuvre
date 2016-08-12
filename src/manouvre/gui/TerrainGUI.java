/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import manouvre.game.Position;
import manouvre.game.Terrain;

/**
 *
 * @author Piotr
 */
public class TerrainGUI extends Terrain{
    
        private Image img;
	
	public TerrainGUI(int terrainType, Position pos) throws IOException  {
		
                super(terrainType, pos);
		
                generateImageForPiece(terrainType);

		//this.resetToUnderlyingPiecePosition();
	}
        

        public void generateImageForPiece(int terrainType) throws IOException {

		String filename = "resources\\terrain\\TerrainMap240.png";
               
                
                        final int rows = 4;
                final int cols = 4;
	
                BufferedImage bigImage = ImageIO.read(new File(filename));
                BufferedImage cutImage;
  
                switch (terrainType) {
			case Terrain.CITY:
                            cutImage = bigImage.getSubimage(0, 0, MapGUI.SQUARE_WIDTH, MapGUI.SQUARE_HEIGHT);
 				break;
			case Terrain.CLEAR:
                            cutImage = bigImage.getSubimage(MapGUI.SQUARE_WIDTH*1, MapGUI.SQUARE_HEIGHT*3, MapGUI.SQUARE_WIDTH, MapGUI.SQUARE_HEIGHT);
				break;
			case Terrain.FIELDS:
                            cutImage = bigImage.getSubimage(MapGUI.SQUARE_WIDTH*0, MapGUI.SQUARE_HEIGHT*3, MapGUI.SQUARE_WIDTH, MapGUI.SQUARE_HEIGHT);
				break;
			case Terrain.FOREST:
                            cutImage = bigImage.getSubimage(MapGUI.SQUARE_WIDTH*3, MapGUI.SQUARE_HEIGHT*0, MapGUI.SQUARE_WIDTH, MapGUI.SQUARE_HEIGHT);
				break;
			case Terrain.HILL:
                            cutImage = bigImage.getSubimage(MapGUI.SQUARE_WIDTH*2, MapGUI.SQUARE_HEIGHT*0, MapGUI.SQUARE_WIDTH, MapGUI.SQUARE_HEIGHT);
				break;
			case Terrain.LAKE:
                            cutImage = bigImage.getSubimage(MapGUI.SQUARE_WIDTH*1, MapGUI.SQUARE_HEIGHT*0, MapGUI.SQUARE_WIDTH, MapGUI.SQUARE_HEIGHT);	
				break;
                        case Terrain.MARSH:
                            cutImage = bigImage.getSubimage(MapGUI.SQUARE_WIDTH*1, MapGUI.SQUARE_HEIGHT*1, MapGUI.SQUARE_WIDTH, MapGUI.SQUARE_HEIGHT);	
				break;
                        default: throw new IOException("No such terrain");
		}
                   this.img = cutImage;
	}
     public Image getImg() {
        return img;
    }
}
