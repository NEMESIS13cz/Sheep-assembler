package com.redstoner.nemes.redstone_sheep;

public class Memory {

	private int[] memory = new int[256];
	
	public Memory(String data) {
		String[] instructions = data.split("\n");
		
		if (instructions.length > memory.length) {
			return;
		}
		for (int i = 0; i < instructions.length; i++) {
			memory[i] = Integer.parseInt(instructions[i], 2) & 0xFFFF;
		}
	}
	
	protected int getMemoryAddress(int addr) {
		return memory[addr];
	}
	
	protected void setMemoryAddress(int addr, int data) {
		memory[addr] = data;
	}
}
