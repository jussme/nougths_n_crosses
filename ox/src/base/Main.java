package base;

public class Main {
	public static void main(String[] args) {
		int[] initValues = SettingsInputDialog.getUserSettings();
		new Window(new Operator(initValues[0], initValues[1],
				initValues[2], initValues[3], initValues[4]));
	}
}
