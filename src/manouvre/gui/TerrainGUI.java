/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manouvre.gui;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import manouvre.game.Position;
import manouvre.game.Terrain;

/**
 *
 * @author Piotr
 */
public class TerrainGUI {
    
        final private int SQUARE_WIDTH = 100;
        final private int SQUARE_HEIGHT = 100;
        private BufferedImage img;
        
        
        Terrain terrain;

        public Terrain getTerrain() {
            return terrain;
        }

        public void setTerrain(Terrain terrain) {
            this.terrain = terrain;
        }

        public TerrainGUI(Terrain terrain) throws IOException {
            
            this.terrain = terrain;
            generateImageForPiece(terrain.getType());
            
            
        }

        public Position getPos(){
        return terrain.getPosition();
        }
        
        
        public void generateImageForPiece(int terrainType) throws IOException {

		String filename = "resources\\terrain\\TerrainMap400.png";
               
                BufferedImage bigImage = ImageIO.read(new File(filename));
                BufferedImage cutImage;
  
                switch (terrainType) {
			case Terrain.CITY:
                            cutImage = bigImage.getSubimage(0, 0, SQUARE_WIDTH, SQUARE_WIDTH);
 				break;
			case Terrain.CLEAR:
                            cutImage = bigImage.getSubimage(SQUARE_WIDTH*1, SQUARE_WIDTH*3, SQUARE_WIDTH, SQUARE_WIDTH);
				break;
			case Terrain.FIELDS:
                            cutImage = bigImage.getSubimage(SQUARE_WIDTH*0, SQUARE_WIDTH*3, SQUARE_WIDTH, SQUARE_WIDTH);
				break;
			case Terrain.FOREST:
                            cutImage = bigImage.getSubimage(SQUARE_WIDTH*3, SQUARE_WIDTH*0, SQUARE_WIDTH, SQUARE_WIDTH);
				break;
			case Terrain.HILL:
                            cutImage = bigImage.getSubimage(SQUARE_WIDTH*2, SQUARE_WIDTH*0, SQUARE_WIDTH, SQUARE_WIDTH);
				break;
			case Terrain.LAKE:
                            cutImage = bigImage.getSubimage(SQUARE_WIDTH*1, SQUARE_WIDTH*0, SQUARE_WIDTH, SQUARE_WIDTH);	
				break;
                        case Terrain.MARSH:
                            cutImage = bigImage.getSubimage(SQUARE_WIDTH*1, SQUARE_WIDTH*1, SQUARE_WIDTH, SQUARE_WIDTH);	
				break;
                        default: throw new IOException("No such terrain");
		}
                   this.img = cutImage;
	}
     public BufferedImage getImg() {
        return img;
    }
     
     public int getWidth() {
		return MapGUI.SQUARE_WIDTH;
	}

    public int getHeight() {
		return MapGUI.SQUARE_HEIGHT;
	}
}
