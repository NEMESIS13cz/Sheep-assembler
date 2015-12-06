package com.redstoner.nemes.redstone_sheep;

public class ALU {
	
	public int add(int a, int b, boolean signed, boolean carry) {
		if (signed) {
			int res = a + b + (carry ? 1 : 0);
			int carries = ((((a & 0x7FFF) + (b & 0x7FFF)) >> 15) | ((res >> 15) & 0b10));
			
			if (carries == 0b01) {
				// ALU overflow
			}else if (carries == 0b10) {
				// ALU underflow
			}
			return res & 0xFFFF;
		}else{
			int res = a + b;
			if (res > 65535) {
				// ALU overflow
			}
			return res & 0xFFFF;
		}
	}
	
	public int sub(int a, int b, boolean signed) {
		return add(a, b ^ 0xFFFF, signed, true);
	}
	
	public int and(int a, int b) {
		return (a & b) & 0xFFFF;
	}
	
	public int or(int a, int b) {
		return (a | b) & 0xFFFF;
	}
	
	public int xor(int a, int b) {
		return (a ^ b) & 0xFFFF;
	}
	
	public int com(int a, boolean signed) {
		return add(xor(a, 0xFFFF), 0, signed, true);
	}
	
	public int dec(int a, boolean signed) {
		return sub(a, 1, signed);
	}
	
	public int inc(int a, boolean signed) {
		return add(a, 1, signed, false);
	}
	
	public int shift(int a, boolean left) {
		if (left) {
			return a << 1;
		}else{
			return a >> 1;
		}
	}
	
	public int swap(int a) {
		return ((a & 0xFF) << 8) | ((a & 0xFF00) >> 8);
	}
	
	public int setBit(int a, int index, boolean value) {
		if (value) {
			return a | (1 << index);
		}else{
			return a & ((1 << index) ^ 0xFFFF);
		}
	}
	
	public boolean getBit(int a, int index) {
		return ((a >> index) & 1) == 1;
	}
}
