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
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class IDE implements Runnable {
	
	public static int codeRefreshRate;
	protected static boolean darkMode;
	protected static JTextPane console;
	protected static JTextPane codeTextPane;
	protected static boolean hasChanged = false;
	protected static boolean restartTimer = false;
	
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
		
		codeTextPane = new JTextPane();

		JScrollPane codeScrollPane = new JScrollPane(codeTextPane);
		JScrollPane consoleScrollPane = new JScrollPane(console);

		JTextField fileNameTextField = new JTextField("program");
		
		JButton assembleButton = new JButton("Assemble");
		JButton loadButton = new JButton("Load");
		JButton modeSwitchButton = new JButton();
		JButton saveButton = new JButton("Save");
		JButton debugButton = new JButton("Debug");
		
		//Setup panels
		BoxLayout bottomPanelLayout = new BoxLayout(bottomPanel, BoxLayout.X_AXIS);
		bottomPanel.setLayout(bottomPanelLayout);
		
		BorderLayout topPanelLayout = new BorderLayout();
		topPanel.setLayout(topPanelLayout);
		
		//Add button action listeners
		assembleButton.addActionListener(Helpers.getAssembleButtonActionListener(codeTextPane, fileNameTextField));
		loadButton.addActionListener(Helpers.getLoadButtonActionListener(codeTextPane));
		modeSwitchButton.addActionListener(Helpers.getModeSwitchButtonActionListener(topPanel, modeSwitchButton, codeTextPane, codeScrollPane, consoleScrollPane));
		saveButton.addActionListener(Helpers.getSaveButtonActionListener(codeTextPane, fileNameTextField));
		debugButton.addActionListener(Helpers.getDebugButtonActionListener(codeTextPane, fileNameTextField));
		codeTextPane.getDocument().addDocumentListener(Helpers.getDocumentListener());
		
		//Set properties of GUI elements
		console.setEditable(false);
		console.setPreferredSize(new Dimension(300, Integer.MAX_VALUE));
		
		codeTextPane.setFont(new Font("Courier New", 0, 20));
		
		fileNameTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		
		init(darkMode, panel, modeSwitchButton, codeTextPane, codeScrollPane, consoleScrollPane);
		
		//Add GUI elements to panels
		topPanel.add(codeScrollPane);
		topPanel.add(consoleScrollPane, BorderLayout.EAST);
		
		bottomPanel.add(assembleButton);
		bottomPanel.add(saveButton);
		bottomPanel.add(loadButton);
		bottomPanel.add(debugButton);
		bottomPanel.add(fileNameTextField);
		bottomPanel.add(modeSwitchButton);
		
		panel.add(topPanel);
		panel.add(bottomPanel);
		
		//Final setup
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
		Thread timer = new Thread() {

	        StyledDocument document;
	        AttributeSet defaultColor;
	        AttributeSet instruction;
	        AttributeSet register;
	        AttributeSet name;
	        AttributeSet call;
	        AttributeSet function;
	        AttributeSet functionName;
	        AttributeSet functionCall;
	        AttributeSet number;
	        AttributeSet comment;
			
			public void run() {
				restartTimer(false);
				initTimer();
				
				while (!shouldTimerRestart()) {
					if (lastUpdate < System.currentTimeMillis() && hasChanged) {
						updateCodePaneMarking(document, defaultColor, instruction, register, name, call, function, functionName, functionCall, number, comment);
						hasChanged = false;
					}
					try {
						Thread.sleep(codeRefreshRate);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				initTimer();
				run();
			}
			
			private void initTimer() {
		        document = codeTextPane.getStyledDocument();
		        StyleContext context = StyleContext.getDefaultStyleContext();
		        
				if (darkMode) {
			        defaultColor = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.GRAY);
			        instruction = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0xd0024a));
			        register = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0x00e5d2));
			        AttributeSet name_temp = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0xe04508));
			        name = context.addAttribute(name_temp, StyleConstants.Bold, true);
			        call = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0xe0c408));
			        AttributeSet function_temp = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0x00960e));
			        function = context.addAttribute(function_temp, StyleConstants.Bold, true);
			        functionName = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0x46ff57));
			        AttributeSet functionCall_temp = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Italic, true);
			        functionCall = context.addAttribute(functionCall_temp, StyleConstants.Foreground, new Color(0x46ff57));
			        number = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0x0a27ff));
			        AttributeSet comment_temp = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Italic, true);
			        comment = context.addAttribute(comment_temp, StyleConstants.Foreground, new Color(0x00adc1));
				}else{
			        defaultColor = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLACK);
			        instruction = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0x2e2e2e));
			        register = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0xd0024a));
			        AttributeSet name_temp = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0x036f82));
			        name = context.addAttribute(name_temp, StyleConstants.Bold, true);
			        call = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0x03badb));
			        AttributeSet function_temp = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0xb10893));
			        function = context.addAttribute(function_temp, StyleConstants.Bold, true);
			        functionName = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0x777777));
			        AttributeSet functionCall_temp = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Italic, true);
			        functionCall = context.addAttribute(functionCall_temp, StyleConstants.Foreground, new Color(0x777777));
			        number = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0x199319));
			        AttributeSet comment_temp = context.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Italic, true);
			        comment = context.addAttribute(comment_temp, StyleConstants.Foreground, new Color(0x0522ff));
				}
			}
		};
		timer.start();
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
	
    protected static void appendToCode(String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);
        
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Courier New");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        
        int len = codeTextPane.getDocument().getLength();
        codeTextPane.setCaretPosition(len);
        codeTextPane.setCharacterAttributes(aset, false);
        codeTextPane.replaceSelection(msg);
    }
    
    private static long lastUpdate = 0;
    
    public static void updateCodePaneMarking(StyledDocument document, AttributeSet defaultColor, AttributeSet instruction, AttributeSet register, AttributeSet name, AttributeSet call, AttributeSet function, AttributeSet functionName, AttributeSet functionCall, AttributeSet number, AttributeSet comment) {
    	if (lastUpdate + codeRefreshRate > System.currentTimeMillis()) {
    		return;
    	}
    	lastUpdate = System.currentTimeMillis();
        
        boolean inName = false;
        boolean inCall = false;
        boolean inFuncName = false;
        boolean callingFunc = false;
        boolean inHexNumber = false;
        boolean inComment = false;
        
        try {
        	main: for (int index = 0; index < document.getLength(); index++) {
        		String potentialMatch = "";
        		if (inHexNumber) {
        			potentialMatch = document.getText(index, 1).toUpperCase();
        			if (!(potentialMatch.equals("A") || potentialMatch.equals("B") || potentialMatch.equals("C") || potentialMatch.equals("D") || potentialMatch.equals("E") || potentialMatch.equals("F") || Character.isDigit(potentialMatch.toCharArray()[0]))) {
        				inHexNumber = false;
        				continue;
        			}
    				document.setCharacterAttributes(index, 1, number, true);
        		}else if (inName) {
    				document.setCharacterAttributes(index, 1, name, true);
        			potentialMatch = document.getText(index, 1);
        			if (potentialMatch.equals(":") || potentialMatch.equals("\n") || potentialMatch.equals(" ") || potentialMatch.equals("	")) {
        				inName = false;
        			}
        		}else if (inCall) {
    				document.setCharacterAttributes(index, 1, call, true);
        			potentialMatch = document.getText(index, 1);
        			if (potentialMatch.equals("\n") || potentialMatch.equals(" ") || potentialMatch.equals("	")) {
        				inCall = false;
        			}
        		}else if (callingFunc) {
    				document.setCharacterAttributes(index, 1, functionCall, true);
        			potentialMatch = document.getText(index, 1);
        			if (potentialMatch.equals("\n") || potentialMatch.equals(" ") || potentialMatch.equals("	")) {
        				callingFunc = false;
        			}
        		}else if (inFuncName) {
    				document.setCharacterAttributes(index, 1, functionName, true);
        			potentialMatch = document.getText(index, 1);
        			if (potentialMatch.equals(":") || potentialMatch.equals("\n") || potentialMatch.equals(" ") || potentialMatch.equals("	")) {
        				inFuncName = false;
        			}
        		}else if (inComment) {
        			potentialMatch = document.getText(index, 1).toUpperCase();
        			if (potentialMatch.equals("\n") || index + 1 == document.getLength()) {
        				inComment = false;
        				continue;
        			}
        			document.setCharacterAttributes(index,  1, comment, true);
        		}else{
        			potentialMatch = document.getText(index, 1);
        			String find = "";
        			if (potentialMatch.equals("#") || (potentialMatch.equals("/") && (index + 1 < document.getLength() && document.getText(index + 1, 1).equals("/")))) {
        				document.setCharacterAttributes(index, 1, comment, true);
        				inComment = true;
        				continue;
        			}else if (potentialMatch.equals(".") && ((index > 0 && document.getText(index - 1, 1).equals("\n")) || index == 0)) {
        				document.setCharacterAttributes(index, 1, name, true);
            			inName = true;
            			continue;
            		}else if (potentialMatch.equals(".") && (document.getText(index - 1, 1).equals(" ") || document.getText(index - 1, 1).equals("	"))){
        				document.setCharacterAttributes(index, 1, call, true);
            			inCall = true;
            			continue;
            		}else{
	            		find = "FUNCTION";
	            		if (index + find.length() < document.getLength()) {
	            			potentialMatch = document.getText(index, find.length() + 1).toUpperCase();
	            			if (potentialMatch.equals(find + " ")) {
	            				document.setCharacterAttributes(index, find.length(), function, true);
	    	                    index += find.length();
	    	                    inFuncName = true;
	    	                    continue main;
	            			}
	            		}
	            		find = "FUNC";
	            		if (index + find.length() < document.getLength()) {
	            			potentialMatch = document.getText(index, find.length() + 1).toUpperCase();
	            			if (potentialMatch.equals(find + " ")) {
	            				document.setCharacterAttributes(index, find.length(), function, true);
	    	                    index += find.length();
	    	                    inFuncName = true;
	    	                    continue main;
	            			}
	            		}
	            		find = "DEF";
	            		if (index + find.length() < document.getLength()) {
	            			potentialMatch = document.getText(index, find.length() + 1).toUpperCase();
	            			if (potentialMatch.equals(find + " ")) {
	            				document.setCharacterAttributes(index, find.length(), function, true);
	    	                    index += find.length();
	    	                    inFuncName = true;
	    	                    continue main;
	            			}
	            		}
            		}
            		find = "0X";
            		if (index + find.length() < document.getLength()) {
            			potentialMatch = document.getText(index, find.length()).toUpperCase();
            			if (potentialMatch.equals(find)) {
            				document.setCharacterAttributes(index, find.length(), number, true);
    	                    index += find.length() - 1;
    	                    inHexNumber = true;
    	                    continue main;
            			}
            		}
            		find = "0B";
            		if (index + find.length() < document.getLength()) {
            			potentialMatch = document.getText(index, find.length()).toUpperCase();
            			if (potentialMatch.equals(find)) {
            				document.setCharacterAttributes(index, find.length(), number, true);
    	                    index += find.length() - 1;
    	                    continue main;
            			}
            		}
            		if (index + 2 < document.getLength()) {
            			char[] chars = document.getText(index == 0 ? index : index - 1, 3).toCharArray();
            			if (Character.isDigit(chars[0]) && chars[1] == '.' && Character.isDigit(chars[2])) {
            				document.setCharacterAttributes(index - 1, 3, number, true);
    	                    index += 2;
    	                    continue main;
            			}
            		}
        			potentialMatch = document.getText(index, 1);
            		if (Character.isDigit(potentialMatch.toCharArray()[0])) {
        				document.setCharacterAttributes(index, 1, number, true);
            			continue main;
            		}
	            	for (Instruction instr : Instruction.instructions) {
	            		find = instr.toString();
	            		if (index + find.length() < document.getLength()) {
	            			potentialMatch = document.getText(index, find.length() + 1).toUpperCase();
	            			if (potentialMatch.equals(find + " ") || potentialMatch.equals(find + "	") || potentialMatch.equals(find + "\n")) {
	            				document.setCharacterAttributes(index, find.length(), instruction, true);
	    	                    index += find.length();
	    	                    if (potentialMatch.substring(0, potentialMatch.length() - 1).equals(Instruction.CALL.toString())) {
	    	                    	callingFunc = true;
	    	                    }
	    	                    continue main;
	            			}
	            		}
	            	}
	            	for (Register reg : Register.registers) {
	            		find = reg.toString();
	            		if (index + find.length() < document.getLength()) {
	            			potentialMatch = document.getText(index == 0 ? index : index - 1, find.length() + 1).toUpperCase();
	            			if (potentialMatch.equals(" " + find) || potentialMatch.equals("	" + find)) {
	            				document.setCharacterAttributes(index, find.length(), register, true);
	    	                    index += find.length() - 1;
	    	                    continue main;
	            			}
	            		}
	            	}
	        		if (index + 1 < document.getLength()) {
	        			potentialMatch = document.getText(index, 2);
	        			if (potentialMatch.equals(" -") || potentialMatch.equals("	-")) {
	        				document.setCharacterAttributes(index + 1, 2, register, true);
	        				index++;
		                    continue main;
	        			}
	        		}
					document.setCharacterAttributes(index, 1, defaultColor, true);
        		}
            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        System.gc();
    }
	
	protected static void init(boolean dark, JPanel panel, JButton modeSwitchButton, JTextPane codeTextArea, JScrollPane codeScrollPane, JScrollPane consoleScrollPane) {
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
		restartTimer(true);
	}
	
	private static synchronized boolean shouldTimerRestart() {
		return restartTimer;
	}
	
	private static synchronized void restartTimer(boolean b) {
		restartTimer = b;
	}
	
	public static void saveConfig() {
		Thread t = new Thread() {
			
			public void run() {
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(new File("sheep_assembler.config")));
					writer.write("dark=" + String.valueOf(darkMode) + System.lineSeparator());
					writer.write("refresh=" + String.valueOf(codeRefreshRate) + System.lineSeparator());
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				System.gc();
				System.exit(0);
			}
		};
		t.start();
	}
	
	public static void loadConfig() {
		File f = new File("sheep_assembler.config");
		if (!f.exists()) {
			darkMode = false;
			codeRefreshRate = 1000;
			return;
		}
		try {
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				if (buffer.startsWith("dark")) {
					String value = buffer.substring(buffer.indexOf('=') + 1, buffer.length());
					darkMode = Boolean.parseBoolean(value);
				}else if (buffer.startsWith("refresh")) {
					String value = buffer.substring(buffer.indexOf('=') + 1, buffer.length());
					codeRefreshRate = Integer.parseInt(value);
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new IDE());
	}
}
