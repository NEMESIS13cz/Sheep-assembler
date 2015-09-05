package com.redstoner.nemes.redstone_sheep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.JTextArea;

public class FileManager {

	public static int write(String raw, String compiled, String name) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(name + ".rsasm"))); // raw sheep assembly
			writer.write(raw);
			writer.close();
			OutputStream writer2 = new FileOutputStream(name + ".csasm", false); // compiled sheep assembly
			
			ArrayList<Byte> data = new ArrayList<Byte>();
			char[] chars = compiled.toCharArray();
			String buffer = "";
			
			for (int i = 0; i < compiled.length(); i++) {
				if (chars[i] == '0' || chars[i] == '1') {
					buffer += chars[i];
					if (buffer.length() == 8) {
						data.add((byte)Integer.parseInt(buffer, 2));
						buffer = "";
					}
				}
			}
			byte[] byteArray = new byte[data.size()];
			for (int i = 0; i < data.size(); i++) {
				byteArray[i] = data.get(i);
			}
			writer2.write(byteArray);
			writer2.close();
			return byteArray.length;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static void write(String raw, String name) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(name + ".rsasm"))); // raw sheep assembly
			writer.write(raw);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void read(JTextArea text, File f) {
		try {
			text.setText("");
			BufferedReader reader = new BufferedReader(new FileReader(f));
			String buffer = "";
			while ((buffer = reader.readLine()) != null) {
				text.append(buffer + "\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
