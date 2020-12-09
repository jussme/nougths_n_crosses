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

class SettingsInputDialog extends JFrame{
	private static final long serialVersionUID = 1L;
	
	private static final int WIDTH = 600;
	private static final int HEIGHT = 600;
	
	//borders for input fields 
	private static final Border BORDER = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
	private static final Border BORDER_R = BorderFactory.createTitledBorder(BORDER, "Rows >= 3");
	private static final Border BORDER_C = BorderFactory.createTitledBorder(BORDER, "Columns >= 3");
	private static final Border BORDER_S = BorderFactory.createTitledBorder(BORDER, "S <= 3 && <= R && <= C");
	private static final Border BORDER_T = BorderFactory.createTitledBorder(BORDER, "Number of threads");
	private static final Border BORDER_D = BorderFactory.createTitledBorder(BORDER, "Recursion depth(n/2 + 1 enemy moves)");
	
	private static final Color ERROR_COLOR = new Color(255, 61, 61);

	private static final int TXT_FIELD_SIZE_X = 240;
	private static final int TXT_FIELD_SIZE_y = 60;
	
	private static final float FONT_SIZE = 30f;
	
	private final int[] values = new int[5];
	
	static int[] getUserSettings() {
		SettingsInputDialog frame = new SettingsInputDialog();
		
		synchronized(frame.values) {
			try {
				frame.values.wait();
			}catch(InterruptedException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		
		frame.dispose();
		return frame.values;
	}
	
	protected SettingsInputDialog(){
		setLayout(new GridLayout(1, 1));
		setTitle("Settings");
		setLocationRelativeTo(null);
		Point p = this.getLocation();
		setBounds ((int)(p.getX() - WIDTH/2),
				(int)(p.getY() - HEIGHT/2),
				WIDTH,
				HEIGHT);
		setResizable(false);
		
		class Panel extends JPanel{
			private static final long serialVersionUID = 1L;

			Panel() {
				setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				
				setBackground(new Color(255, 255, 255));
				
				//to set the JTextField background colour white again, after indicating an error with red
				class FocusListenerJTF implements FocusListener {
					@Override
					public void focusGained(FocusEvent e) {
						((JTextField)e.getSource()).setBackground(Color.WHITE);
					}

					@Override public void focusLost(FocusEvent e) {}
				}
				
				FocusListenerJTF focusListener = new FocusListenerJTF();
				
				JTextField tR = new JTextField();
				{
					tR.setMaximumSize(new Dimension(TXT_FIELD_SIZE_X, TXT_FIELD_SIZE_y));
					tR.setHorizontalAlignment(JTextField.CENTER);
					tR.setAlignmentX(CENTER_ALIGNMENT);
					tR.setAlignmentY(CENTER_ALIGNMENT);
					tR.setFont(tR.getFont().deriveFont(FONT_SIZE));
					tR.setBorder(BORDER_R);
					tR.addFocusListener(focusListener);
				}
				
				JTextField tC = new JTextField();
				{
					tC.setMaximumSize(new Dimension(TXT_FIELD_SIZE_X, TXT_FIELD_SIZE_y));
					tC.setHorizontalAlignment(JTextField.CENTER);
					tC.setAlignmentX(CENTER_ALIGNMENT);
					tC.setAlignmentY(CENTER_ALIGNMENT);
					tC.setFont(tR.getFont().deriveFont(FONT_SIZE));
					tC.setBorder(BORDER_C);
					tC.addFocusListener(focusListener);
				}
				
				JTextField tS = new JTextField();
				{
					tS.setMaximumSize(new Dimension(TXT_FIELD_SIZE_X, TXT_FIELD_SIZE_y));
					tS.setHorizontalAlignment(JTextField.CENTER);
					tS.setAlignmentX(CENTER_ALIGNMENT);
					tS.setAlignmentY(CENTER_ALIGNMENT);
					tS.setFont(tR.getFont().deriveFont(FONT_SIZE));
					tS.setBorder(BORDER_S);
					tS.addFocusListener(focusListener);
				}
				
				JTextField tT = new JTextField();
				{
					tT.setMaximumSize(new Dimension(TXT_FIELD_SIZE_X, TXT_FIELD_SIZE_y));
					tT.setHorizontalAlignment(JTextField.CENTER);
					tT.setAlignmentX(CENTER_ALIGNMENT);
					tT.setAlignmentY(CENTER_ALIGNMENT);
					tT.setFont(tR.getFont().deriveFont(FONT_SIZE));
					tT.setBorder(BORDER_T);
					tT.addFocusListener(focusListener);
				}
				
				JTextField tD = new JTextField();
				{
					tD.setMaximumSize(new Dimension(TXT_FIELD_SIZE_X, TXT_FIELD_SIZE_y));
					tD.setHorizontalAlignment(JTextField.CENTER);
					tD.setAlignmentX(CENTER_ALIGNMENT);
					tD.setAlignmentY(CENTER_ALIGNMENT);
					tD.setFont(tR.getFont().deriveFont(FONT_SIZE));
					tD.setBorder(BORDER_D);
					tD.addFocusListener(focusListener);
				}
				
				JButton button = new JButton("Set");
				{
					button.setAlignmentX(CENTER_ALIGNMENT);
					button.setAlignmentY(CENTER_ALIGNMENT);
					button.setMaximumSize(new Dimension(80, 40));
					button.addActionListener(event -> {
						
						JTextField[] inputFields = {tR, tC, tS, tT, tD};
						String[] inputStrings = {
								tR.getText().strip(),
								tC.getText().strip(),
								tS.getText().strip(),
								tT.getText().strip(),
								tD.getText().strip()
						};
						
						boolean valid = true;
						for(int it = 0; it < inputFields.length; ++it)
							try {
								values[it] = Integer.valueOf(inputStrings[it]);
							}catch(NumberFormatException e) {
								inputFields[it].setBackground(ERROR_COLOR);
								valid = false;
							}
						
						if(valid) {
							synchronized(values) {
								values.notify();
							}
						}
						
						/*
						JTextField[] inputFields = {tR, tC, tS};
						String[] inputStrings = {
								tR.getText().strip(),
								tC.getText().strip(),
								tS.getText().strip()
						};
						
						boolean validInput = true;
						
						//input format validation - whether empty or not a number
						int[] inputInt = new int[3];
						for(int it = 0; it < inputFields.length; ++it) {
							try {
								inputInt[it] = Integer.valueOf(inputStrings[it]);
							}catch(NumberFormatException e) {
								inputFields[it].setBackground(ERROR_COLOR);
								validInput = false;
							}
						}
						
						//change 1st OR to AND for possibly less victory boards
						//checking if requirements for the rules are met - series length
						if(inputInt[2] > inputInt[0] || inputInt[2] > inputInt[1]
								|| inputInt[2] < 3)
						{
							tS.setBackground(ERROR_COLOR);
							validInput = false;
						}
						
						//checking if requirements for the rules are met - rows
						if(inputInt[0] < 3) {
							tR.setBackground(ERROR_COLOR);
							validInput = false;
						}
						
						//checking if requirements for the rules are met - columns
						if(inputInt[1] < 3) {
							tC.setBackground(ERROR_COLOR);
							validInput = false;
						}
						
						//if the input is valid and adheres to the rules
						if(validInput)
						{
							synchronized(values) {
								values[0] = inputInt[0];
								values[1] = inputInt[1];
								values[2] = inputInt[2];
								
								values.notify();
							}	
							
						}*/
					});
				}
				
				add(tR);
				add(tC);
				add(tS);
				add(tT);
				add(tD);
				add(button);
			}
		}
	
		add(new Panel());	
		setVisible(true);
	}
	
	
}