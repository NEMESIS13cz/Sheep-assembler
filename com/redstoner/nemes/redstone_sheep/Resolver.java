package com.redstoner.nemes.redstone_sheep;

public class Resolver {

	public static void resolve(String text, String fileName) {
		FileManager.write(text, "1000101", fileName);
	}
}
