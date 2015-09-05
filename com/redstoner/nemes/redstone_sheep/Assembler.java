package com.redstoner.nemes.redstone_sheep;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Assembler {
	
	private static boolean darkMode = false;
	
	public Assembler() {
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		frame.setTitle("Redstone Sheep Assembler");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600, 400));
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		frame.add(panel);
		
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);
		
		JTextArea text = new JTextArea();
		JScrollPane pane = new JScrollPane(text);
		JButton button = new JButton("Assemble");
		JButton load = new JButton("Load file");
		JTextField name = new JTextField("program");
		JPanel buttonPanel = new JPanel();
		JButton mode = new JButton();
		
		ActionListener listener = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Resolver.resolve(text.getText(), name.getText());
			}
		};
		
		ActionListener loadListener = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				JFileChooser browser = new JFileChooser();
				browser.addChoosableFileFilter(new FileNameExtensionFilter("Redstone Sheep Assembly file", "rsasm"));
				browser.showSaveDialog(null);
				File f = browser.getSelectedFile();
				if (f != null) {
					FileManager.read(text, f);
				}
			}
		};
		
		ActionListener modeListener = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				darkMode = !darkMode;
				init(darkMode, panel, mode, load, button, name, buttonPanel, text, pane);
			}
		};
		
		BoxLayout buttonLayout = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
		buttonPanel.setLayout(buttonLayout);
		button.addActionListener(listener);
		load.addActionListener(loadListener);
		mode.addActionListener(modeListener);
		init(darkMode, panel, mode, load, button, name, buttonPanel, text, pane);
		text.setFont(new Font("Courier New", 0, 20));
		name.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		
		panel.add(pane);
		panel.add(buttonPanel);
		
		buttonPanel.add(button);
		buttonPanel.add(load);
		buttonPanel.add(name);
		buttonPanel.add(mode);
		
		frame.setVisible(true);
	}
	
	private static void init(boolean dark, JPanel panel, JButton mode, JButton load, JButton button, JTextField name, JPanel buttonPanel, JTextArea text, JScrollPane pane) {
		if (dark) {
			mode.setText("Normal");
			panel.setBackground(Color.BLACK);
			text.setForeground(Color.GRAY);
			text.setBackground(Color.BLACK);
			text.setCaretColor(Color.WHITE);
			pane.setBackground(Color.BLACK);
		}else{
			mode.setText("Dark");
			panel.setBackground(Color.WHITE);
			text.setForeground(Color.BLACK);
			text.setBackground(Color.WHITE);
			text.setCaretColor(Color.GRAY);
			pane.setBackground(Color.WHITE);
		}
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		new Assembler();
	}
}
