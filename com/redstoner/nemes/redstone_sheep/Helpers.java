package com.redstoner.nemes.redstone_sheep;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Helpers {

	public static ActionListener getDebugButtonActionListener(JTextPane text, JTextField name) {
		return new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String data = Assembler.resolve(text.getText(), name.getText());
				if (data == null) {
					return;
				}
				Thread t = new Thread() {
					
					public void run() {
						Debugger.start(data);
					}
				};
				t.start();
			}
		};
	}
	
	public static ActionListener getAssembleButtonActionListener(JTextPane text, JTextField name) {
		return new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Assembler.resolve(text.getText(), name.getText());
			}
		};
	}
	
	public static ActionListener getSaveButtonActionListener(JTextPane text, JTextField name) {
		return new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				FileManager.write(text.getText(), name.getText());
			}
		};
	}
	
	public static ActionListener getLoadButtonActionListener(JTextPane text) {
		return new ActionListener() {
			
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
	}
	
	public static ActionListener getModeSwitchButtonActionListener(JPanel panel, JButton mode, JTextPane text, JScrollPane pane, JScrollPane consolePane) {
		return new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				IDE.darkMode = !IDE.darkMode;
				IDE.init(IDE.darkMode, panel, mode, text, pane, consolePane);
			}
		};
	}
	
	public static WindowListener getWindowListener() {
		return new WindowListener() {

			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				IDE.saveConfig();
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		};
	}
	
	public static WindowListener getDebuggerWindowListener() {
		return new WindowListener() {

			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				Debugger.debugging = false;
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		};
	}
	
	public static DocumentListener getDocumentListener() {
		return new DocumentListener() {

			public void insertUpdate(DocumentEvent e) {
				IDE.hasChanged = true;
			}

			public void removeUpdate(DocumentEvent e) {
				IDE.hasChanged = true;
			}

			public void changedUpdate(DocumentEvent e) {
				IDE.hasChanged = true;
			}
		};
	}
}
