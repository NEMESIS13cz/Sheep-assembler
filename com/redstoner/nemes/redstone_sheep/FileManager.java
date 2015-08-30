package com.redstoner.nemes.redstone_sheep;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTextArea;

public class FileManager {

	public static void write(String raw, String compiled, String name) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(name + ".rsasm"))); // raw sheep assembly
			writer.write(raw);
			writer.close();
			BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File(name + ".csasm"))); // compiled sheep assembly
			writer2.write(compiled);
			writer2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void read(JTextArea text, File f) {
		try {
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
