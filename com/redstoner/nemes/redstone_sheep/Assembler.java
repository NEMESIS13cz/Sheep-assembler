package com.redstoner.nemes.redstone_sheep;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class Assembler implements Runnable {
	
	private static boolean darkMode;
	private static JTextPane console;
	
	public void run() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		loadConfig();
		JFrame frame = new JFrame();
		JPanel panel = new JPanel();
		frame.setTitle("Redstone Sheep Assembler");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600, 400));
		WindowListener windowListener = new WindowListener() {

			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				saveConfig();
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		};
		
		frame.getContentPane().add(panel);
		frame.addWindowListener(windowListener);
		
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(layout);
		
		JTextArea text = new JTextArea();
		JScrollPane pane = new JScrollPane(text);
		JButton button = new JButton("Assemble");
		JButton load = new JButton("Load file");
		JTextField name = new JTextField("program");
		JPanel buttonPanel = new JPanel();
		JButton mode = new JButton();
		JPanel windowPanel = new JPanel();
		console = new JTextPane();
		JScrollPane consolePane = new JScrollPane(console);
		
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
				init(darkMode, panel, mode, text, pane, consolePane);
			}
		};
		
		BoxLayout buttonLayout = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
		buttonPanel.setLayout(buttonLayout);
		BorderLayout windowLayout = new BorderLayout();
		windowPanel.setLayout(windowLayout);
		button.addActionListener(listener);
		load.addActionListener(loadListener);
		mode.addActionListener(modeListener);
		init(darkMode, panel, mode, text, pane, consolePane);
		text.setFont(new Font("Courier New", 0, 20));
		console.setEditable(false);
		console.setPreferredSize(new Dimension(300, Integer.MAX_VALUE));
		name.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		
		panel.add(windowPanel);
		panel.add(buttonPanel);
		
		windowPanel.add(pane);
		windowPanel.add(consolePane, BorderLayout.EAST);
		
		buttonPanel.add(button);
		buttonPanel.add(load);
		buttonPanel.add(name);
		buttonPanel.add(mode);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
    private static void appendToPane(String msg, Color c) {
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
    	appendToPane(msg + "\n", Color.BLACK);
    	System.out.println(msg);
    }
    
    public static void errln(String msg) {
    	appendToPane(msg + "\n", Color.RED);
    	System.err.println(msg);
    }
	
	private static void init(boolean dark, JPanel panel, JButton mode, JTextArea text, JScrollPane pane, JScrollPane consolePane) {
		if (dark) {
			mode.setText("Normal");
			panel.setBackground(Color.BLACK);
			text.setForeground(Color.GRAY);
			text.setBackground(Color.BLACK);
			text.setCaretColor(Color.WHITE);
			pane.setBackground(Color.BLACK);
			consolePane.setBackground(Color.GRAY);
			console.setBackground(Color.GRAY);
		}else{
			mode.setText("Dark");
			panel.setBackground(Color.WHITE);
			text.setForeground(Color.BLACK);
			text.setBackground(Color.WHITE);
			text.setCaretColor(Color.GRAY);
			pane.setBackground(Color.WHITE);
			consolePane.setBackground(Color.LIGHT_GRAY);
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
