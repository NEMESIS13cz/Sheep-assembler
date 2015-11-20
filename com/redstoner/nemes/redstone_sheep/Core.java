package com.redstoner.nemes.redstone_sheep;

public class Core {
	
	private short[] registers = new short[35];
	private short[] memory = new short[256];
	private short instruction = 0;
	private byte pointer = 0;
	
	public void tick() {
		instruction = getMemoryAddress(pointer);
		
		
	}
	
	private void setData(byte id, boolean flag, short data) {
		if (flag) {
			setRegister(id, data);
		}else{
			setMemoryAddress(id, data);
		}
	}
	
	private short getData(byte id, boolean flag) {
		if (flag) {
			return getRegister(id);
		}else{
			return getMemoryAddress(id);
		}
	}
	
	private void setRegister(byte id, short data) {
		if (id > 34) {
			registers[id] = (short) (registers[id] | ((data & 1) << (id - 35)));
		}
		registers[id] = data;
	}
	
	private short getRegister(byte id) {
		if (id > 34) {
			return (short) ((registers[id] >> (id - 35)) & 1);
		}
		return registers[id];
	}
	
	private short getMemoryAddress(byte addr) {
		return memory[addr];
	}
	
	private void setMemoryAddress(byte addr, short data) {
		memory[addr] = data;
	}
}
