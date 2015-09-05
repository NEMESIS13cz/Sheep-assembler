package com.redstoner.nemes.redstone_sheep;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class Assembler implements Runnable {
	
	protected static boolean darkMode;
	protected static JTextPane console;
	
	public void run() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			System.err.println("Failed to set look & feel!");
			e.printStackTrace();
		}
		loadConfig();
		//Setup frame and main panel
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		frame.setTitle("Redstone Sheep Assembler");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600, 400));
		
		frame.getContentPane().add(panel);
		frame.addWindowListener(Helpers.getWindowListener());
		
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);
		
		//Create GUI elements
		JPanel bottomPanel = new JPanel();
		JPanel topPanel = new JPanel();
		
		console = new JTextPane();
		
		JTextArea codeTextArea = new JTextArea();

		JScrollPane codeScrollPane = new JScrollPane(codeTextArea);
		JScrollPane consoleScrollPane = new JScrollPane(console);

		JTextField fileNameTextField = new JTextField("program");
		
		JButton assembleButton = new JButton("Assemble");
		JButton loadButton = new JButton("Load file");
		JButton modeSwitchButton = new JButton();
		JButton saveButton = new JButton("Save");
		
		//Setup panels
		BoxLayout bottomPanelLayout = new BoxLayout(bottomPanel, BoxLayout.X_AXIS);
		bottomPanel.setLayout(bottomPanelLayout);
		
		BorderLayout topPanelLayout = new BorderLayout();
		topPanel.setLayout(topPanelLayout);
		
		//Add button action listeners
		assembleButton.addActionListener(Helpers.getAssembleButtonActionListener(codeTextArea, fileNameTextField));
		loadButton.addActionListener(Helpers.getLoadButtonActionListener(codeTextArea));
		modeSwitchButton.addActionListener(Helpers.getModeSwitchButtonActionListener(topPanel, modeSwitchButton, codeTextArea, codeScrollPane, consoleScrollPane));
		saveButton.addActionListener(Helpers.getSaveButtonActionListener(codeTextArea, fileNameTextField));
		
		//Set properties of GUI elements
		console.setEditable(false);
		console.setPreferredSize(new Dimension(300, Integer.MAX_VALUE));
		
		codeTextArea.setFont(new Font("Courier New", 0, 20));

		fileNameTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		
		init(darkMode, panel, modeSwitchButton, codeTextArea, codeScrollPane, consoleScrollPane);
		
		//Add GUI elements to panels
		topPanel.add(codeScrollPane);
		topPanel.add(consoleScrollPane, BorderLayout.EAST);
		
		bottomPanel.add(assembleButton);
		bottomPanel.add(saveButton);
		bottomPanel.add(loadButton);
		bottomPanel.add(fileNameTextField);
		bottomPanel.add(modeSwitchButton);
		
		panel.add(topPanel);
		panel.add(bottomPanel);
		
		//Final setup
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
	}
	
    private static void appendToConsole(String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Courier New");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        
        int len = console.getDocument().getLength();
		console.setEditable(true);
        console.setCaretPosition(len);
        console.setCharacterAttributes(aset, false);
        console.replaceSelection(msg);
		console.setEditable(false);
    }
    
    public static void println(String msg) {
    	appendToConsole(msg + "\n", Color.BLACK);
    	System.out.println(msg);
    }
    
    public static void errln(String msg) {
    	appendToConsole(msg + "\n", Color.RED);
    	System.err.println(msg);
    }
	
	protected static void init(boolean dark, JPanel panel, JButton modeSwitchButton, JTextArea codeTextArea, JScrollPane codeScrollPane, JScrollPane consoleScrollPane) {
		if (dark) {
			modeSwitchButton.setText("Normal");
			panel.setBackground(Color.BLACK);
			codeTextArea.setForeground(Color.GRAY);
			codeTextArea.setBackground(Color.BLACK);
			codeTextArea.setCaretColor(Color.WHITE);
			codeScrollPane.setBackground(Color.BLACK);
			consoleScrollPane.setBackground(Color.GRAY);
			console.setBackground(Color.GRAY);
		}else{
			modeSwitchButton.setText("Dark");
			panel.setBackground(Color.WHITE);
			codeTextArea.setForeground(Color.BLACK);
			codeTextArea.setBackground(Color.WHITE);
			codeTextArea.setCaretColor(Color.GRAY);
			codeScrollPane.setBackground(Color.WHITE);
			consoleScrollPane.setBackground(Color.LIGHT_GRAY);
			console.setBackground(Color.LIGHT_GRAY);
		}
	}
	
	public static void saveConfig() {
		Thread t = new Thread() {
			
			public void run() {
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(new File("sheep_assembler.config")));
					writer.write("dark=" + String.valueOf(darkMode));
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		};
		t.start();
	}
	
	public static void loadConfig() {
		File f = new File("sheep_assembler.config");
		if (!f.exists()) {
			darkMode = false;
			return;
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				if (buffer.startsWith("dark")) {
					String value = buffer.substring(buffer.indexOf('=') + 1, buffer.length());
					darkMode = Boolean.parseBoolean(value);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Assembler());
	}
}
