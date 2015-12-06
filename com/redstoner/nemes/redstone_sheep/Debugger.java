package com.redstoner.nemes.redstone_sheep;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Debugger extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public static boolean debugging = false;
	private static JFrame frame = null;
	
	public static void start(String data) {
		if (debugging) {
			frame.dispose();
		}
		debugging = true;
		
		//Setup frame and main panel
		frame = new JFrame();
		Debugger panel = new Debugger();
		
		frame.setBackground(Color.black);
		frame.setTitle("Redstone Sheep Debugger");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600, 400));
		
		frame.getContentPane().add(panel);
		frame.addWindowListener(Helpers.getDebuggerWindowListener());
		
		//Final setup
		frame.pack();
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
		
		while (true) {
			Memory mem = new Memory(data);
			Core c0 = new Core(mem);
			Core c1 = new Core(mem);
			c0.enable();
			
			update(c0, c1);

			panel.repaint();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void update(Core c0, Core c1) {
		c0.tick();
		c1.tick();
	}
	
	public void paint(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());
		
	}
}
