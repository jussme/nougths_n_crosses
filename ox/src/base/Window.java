package base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

class Window extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private static final Border BORDER = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	private static final Border BORDER_R = BorderFactory.createTitledBorder(BORDER, "Rows >= 3");
	private static final Border BORDER_C = BorderFactory.createTitledBorder(BORDER, "Columns >= 3");
	private static final Border BORDER_S = BorderFactory.createTitledBorder(BORDER, "S >= 3 && <= R && <= C");
	
	private static final Color ERROR_COLOR = new Color(255, 61, 61);
	
	private int sizeX = 600;
	private int sizeY = 600;
	
	Window() {
		setLocationRelativeTo (null);
		Point p = this.getLocation();
		setBounds ((int)(p.getX() - sizeX/2),
				(int)(p.getY() - sizeY/2),
				sizeX,
				sizeY);
		
		setLayout (new GridLayout(1, 1));
		
		add (new GridSizeInputPanel());
		
		setTitle ("Noughts and crosses");
		
		setResizable (false);
		
		this.setDefaultCloseOperation (EXIT_ON_CLOSE);
		
		setVisible (true);
	}
	
	private class GridSizeInputPanel extends JPanel{
		private static final long serialVersionUID = 1L;
		private static final int tSX = 240;
		private static final int tSY = 60;
		private static final float FONT_SIZE = 30f;
		
		GridSizeInputPanel(){
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			setBackground(new Color(255, 255, 255));
			
			class FocusListenerJTF implements FocusListener {
				@Override
				public void focusGained(FocusEvent e) {
					((JTextField)e.getSource()).setBackground(Color.WHITE);
				}

				@Override
				public void focusLost(FocusEvent e) {
					
				}
			}
			
			FocusListenerJTF focusListener = new FocusListenerJTF();
			
			JTextField tX = new JTextField();
			{
				
				tX.setMaximumSize(new Dimension(tSX, tSY));
				tX.setHorizontalAlignment(JTextField.CENTER);
				tX.setAlignmentX(CENTER_ALIGNMENT);
				tX.setAlignmentY(CENTER_ALIGNMENT);
				tX.setFont(tX.getFont().deriveFont(FONT_SIZE));
				tX.setBorder(BORDER_R);
				tX.addFocusListener(focusListener);
			}
			
			JTextField tY = new JTextField();
			{
				tY.setMaximumSize(new Dimension(tSX, tSY));
				tY.setHorizontalAlignment(JTextField.CENTER);
				tY.setAlignmentX(CENTER_ALIGNMENT);
				tY.setAlignmentY(CENTER_ALIGNMENT);
				tY.setFont(tX.getFont().deriveFont(FONT_SIZE));
				tY.setBorder(BORDER_C);
				tY.addFocusListener(focusListener);
			}
			
			JTextField tS = new JTextField();
			{
				tS.setMaximumSize(new Dimension(tSX, tSY));
				tS.setHorizontalAlignment(JTextField.CENTER);
				tS.setAlignmentX(CENTER_ALIGNMENT);
				tS.setAlignmentY(CENTER_ALIGNMENT);
				tS.setFont(tX.getFont().deriveFont(FONT_SIZE));
				tS.setBorder(BORDER_S);
				tS.addFocusListener(focusListener);
			}
			
			JButton button = new JButton("Set");
			{
				button.setAlignmentX(CENTER_ALIGNMENT);
				button.setAlignmentY(CENTER_ALIGNMENT);
				button.setMaximumSize(new Dimension(80, 40));
				button.addActionListener(event -> {
					//min static values
					JTextField[] input = {tX, tY, tS};
					String[] inputString = {tX.getText().strip(),
							tY.getText().strip(),
							tS.getText().strip()
					};
					boolean validInput = true;
					for(int it = 0; it < input.length; ++it) 
					{
						if(inputString[it].isEmpty()
								|| !inputString[it].matches("\\d+"))
						{
							input[it].setBackground(ERROR_COLOR);
							validInput = false;
						}
						
					}
					
					if(validInput) 
					{
						//whether series length is valid
						int[] inputInt = {Integer.valueOf(inputString[0]),
								Integer.valueOf(inputString[1]),
								Integer.valueOf(inputString[2])};
						if(inputInt[2] > inputInt[0] && inputInt[2] > inputInt[1]
								|| inputInt[2] < 3)
						{
							tS.setBackground(ERROR_COLOR);
							validInput = false;
						}
						if(validInput)
						{
							Window.this.remove(this);
						
							GamePanel gPanel = new GamePanel(inputInt[0], inputInt[1], inputInt[2]);
							Window.this.add(gPanel);
							Window.this.setVisible(false);
							Window.this.setVisible(true);
						}
					}
				});
			}
			
			add(tX);
			add(tY);
			add(tS);
			add(button);
		}
	}
}
