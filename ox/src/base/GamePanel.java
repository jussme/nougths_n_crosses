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
import javax.swing.border.EtchedBorder;

import imgscalr.Scalr;

class GamePanel extends JPanel{
	private static final long serialVersionUID = 1L;
	static final byte CROSS = 1;
	static final byte NAUGHT = -1;
	
	private Tile[][] tiles;
		Tile[][] getTilesArray() {
			return tiles;
		}
		
	private final Operator OPERATOR;	
	
	GamePanel(Operator operator){
		int[] settings = operator.getSettings();
		int size_rows = settings[0];
		int size_columns = settings[1];
		
		setLayout(new GridLayout(size_rows, size_columns));
		
		tiles = new Tile[size_rows][size_columns];
		
		for(int it = 0; it < size_rows; ++it)
			for(int itt = 0; itt < size_columns; ++itt) 
				tiles[it][itt] = (Tile) add(new Tile(this));
		
		OPERATOR = operator;
		
		Tile.getImages();
		
		setBackground(new Color(158, 198, 255));
	}
	
	void placeANaught(short row, short column) {
		try{
			tiles[row][column].setValue(NAUGHT);
		}catch(IndexOutOfBoundsException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	void placeACross(int row, int column) {
		try{
			tiles[row][column].setValue(CROSS);
		}catch(IndexOutOfBoundsException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	static class Tile extends JPanel{
		private static final long serialVersionUID = 1L;

		private static BufferedImage imageX, image0;
		private static BufferedImage imageXResized, image0Resized;
		
		private byte value = 0;
			void setValue(byte value) {
				if(value == NAUGHT || value == CROSS)
					this.value = value;
				this.repaint();
			}
			
			byte getValue() {
				return this.value;
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
		
		Tile(GamePanel gamePanel) {
			
			this.setSize(100, 100);
			
			setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED,
					new Color(189, 210, 255), new Color(189, 210, 255)));
			
			setBackground(new Color(158, 198, 255));
			
			addComponentListener(new ComponentListener() {
				@Override
				public void componentResized(ComponentEvent e) {
					Dimension dimension = Tile.this.getSize();
					imageXResized = Scalr.resize(imageX,
							dimension.width,
							dimension.height,
							null);
					image0Resized = Scalr.resize(image0,
							dimension.width,
							dimension.height,
							null);
				}

				@Override public void componentMoved(ComponentEvent e) {}
				@Override public void componentShown(ComponentEvent e) {}
				@Override public void componentHidden(ComponentEvent e) {}
			});
		
			addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
					Tile tile = Tile.this;
					if(tile.value == 0) 
						gamePanel.OPERATOR.playerWantsToMakeAMove(gamePanel.tiles, gamePanel, Tile.this);
				}
				
				@Override public void mouseClicked(MouseEvent e) {}
				@Override public void mousePressed(MouseEvent e) {}
				@Override public void mouseEntered(MouseEvent e) {}
				@Override public void mouseExited(MouseEvent e) {}
			});
		}
		
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
	}
}