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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Helpers {

	public static ActionListener getAssembleButtonActionListener(JTextArea text, JTextField name) {
		return new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Resolver.resolve(text.getText(), name.getText());
			}
		};
	}
	
	public static ActionListener getSaveButtonActionListener(JTextArea text, JTextField name) {
		return new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				FileManager.write(text.getText(), name.getText());
			}
		};
	}
	
	public static ActionListener getLoadButtonActionListener(JTextArea text) {
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
	
	public static ActionListener getModeSwitchButtonActionListener(JPanel panel, JButton mode, JTextArea text, JScrollPane pane, JScrollPane consolePane) {
		return new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				Assembler.darkMode = !Assembler.darkMode;
				Assembler.init(Assembler.darkMode, panel, mode, text, pane, consolePane);
			}
		};
	}
	
	public static WindowListener getWindowListener() {
		return new WindowListener() {

			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				Assembler.saveConfig();
			}
			public void windowClosed(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		};
	}
}
