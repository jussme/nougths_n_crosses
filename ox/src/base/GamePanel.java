package base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import imgscalr.Scalr;

class GamePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	static final byte CROSS = 1;
	static final byte NAUGHT = -1;
	
	private byte[][] tab;
		byte[][] getTab(){
			return tab;
		}
	private Tile[][] tiles;	
	
	private boolean turnIndicator = true;
		void setTurnIndicator(boolean turnIndicator){
			this.turnIndicator = turnIndicator;
		}
		boolean getTurnIndicator() {
			return turnIndicator;
		}
		
	private static final Operator OPERATOR = new Operator(3, 3, 3, null);	
	
	GamePanel(int size_rows, int size_columns, int seriesLength){
		setLayout(new GridLayout(size_rows, size_columns));
		
		tab = new byte[size_rows][size_columns];
		tiles = new Tile[size_rows][size_columns];
		
		for(int it = 0; it < size_rows; ++it)
			for(int itt = 0; itt < size_columns; ++itt) {
				tiles[it][itt] = (Tile) add(new Tile(it, itt, this));
				tab[it][itt] = 0;
			}
		
		OPERATOR.setProperties(size_rows, size_columns, seriesLength, this);
		
		Tile.getImages();
		
		setBackground(new Color(158, 198, 255));
	}
	
	void placeANaught(short row, short column) {
		tab[row][column] = NAUGHT;
	}
	
	void updateGraphicalTiles() {
		for(int row = 0; row < tiles.length; ++row)
			for(int column = 0; column < tiles[0].length; ++column) {
				tiles[row][column].setValue(tab[row][column]);
			}
	}
	
	private static class Tile extends JPanel{
		private static final long serialVersionUID = 1L;
		private final static Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED,
				new Color(189, 210, 255), new Color(189, 210, 255));
		
		private float value = 0;
		
		private static BufferedImage imageX, image0;
		private static BufferedImage imageXResized, image0Resized;
	
		private Dimension dimension;
		
		private static void getImages() {
			//preparing images for x and 0
			BufferedImage imageXBuff = null, image0Buff = null;
			try {
				imageXBuff = ImageIO.read(GamePanel.class.getResourceAsStream("x(8).png"));
				image0Buff = ImageIO.read(GamePanel.class.getResourceAsStream("naught(6).jpg"));
			}catch(IOException e) {
				e.printStackTrace();
				System.exit(0);
			}
			imageX = imageXBuff;
			image0 = image0Buff;
		}
		
		private void setValue(int value) {
			if(value == NAUGHT || value == CROSS)
				this.value = value;
			this.repaint();
		}
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			if(value == NAUGHT)
				g.drawImage(image0Resized,
						0,
						0,
						null);
			else
				if(value == CROSS)
					g.drawImage(imageXResized,
							0,
							0,
							null);
		}
		
		Tile(int row, int column, GamePanel gamePanel) {
			
			this.setSize(100, 100);
			
			setBorder(border);
			
			setBackground(new Color(158, 198, 255));
			
			dimension = this.getSize();
			
			addComponentListener(new ComponentListener() {

				@Override
				public void componentResized(ComponentEvent e) {
					dimension = Tile.this.getSize();
					imageXResized = Scalr.resize(imageX,
							dimension.width,
							dimension.height,
							null);
					image0Resized = Scalr.resize(image0,
							dimension.width,
							dimension.height,
							null);
				}

				@Override
				public void componentMoved(ComponentEvent e) {
				}

				@Override
				public void componentShown(ComponentEvent e) {
				}

				@Override
				public void componentHidden(ComponentEvent e) {
				}
			});
		
			addMouseListener(new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					
				}

				@Override
				public void mousePressed(MouseEvent e) {
					
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					Tile tile = Tile.this;
					if(tile.value == 0) 
						if(gamePanel.turnIndicator) {
							gamePanel.turnIndicator = false;
							gamePanel.tab[row][column] = CROSS;
							gamePanel.updateGraphicalTiles();
							OPERATOR.naughtsMove();
							gamePanel.updateGraphicalTiles();
						}
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					
				}

				@Override
				public void mouseExited(MouseEvent e) {
					
				}
			});
		}
	}
}