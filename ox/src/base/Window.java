package base;

import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JFrame;

class Window extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private int sizeX = 600;
	private int sizeY = 600;
	
	Window(Operator operator) {
		setLocationRelativeTo (null);
		Point p = this.getLocation();
		setBounds ((int)(p.getX() - sizeX/2),
				(int)(p.getY() - sizeY/2),
				sizeX,
				sizeY);
		
		setLayout (new GridLayout(1, 1));
		
		add(new GamePanel(operator));
		
		setTitle ("Noughts and crosses");
		
		setResizable (false);
		
		this.setDefaultCloseOperation (EXIT_ON_CLOSE);
		
		setVisible (true);
	}
}
