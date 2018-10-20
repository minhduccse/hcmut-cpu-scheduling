import javax.swing.JOptionPane;

import cpuscheduling.*;

public class Main {
    public static void main(String[] args) {
		System.out.println("start");
		new FrameMain();
		JOptionPane.showMessageDialog(null, "+ Type your file name to import\n+ Choose algorithm\n+ Click Save changes\n+ Click Export ", 
				null, JOptionPane.INFORMATION_MESSAGE);
	}
}