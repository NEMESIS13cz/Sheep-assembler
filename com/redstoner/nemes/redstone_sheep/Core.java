package com.redstoner.nemes.redstone_sheep;

public class Core {
	
	private Memory memory;
	private int[] registers = new int[35];
	private int instruction = 0;
	private int pointer = 0;
	private boolean enabled = false;
	private int[] stack = new int[64];
	private int[] addrStack = new int[32];
	private int addrPtr = 0;
	private int stackPtr = 0;
	private ALU ALU = new ALU();
	private FPU FPU = new FPU();
	
	public Core(Memory mem) {
		memory = mem;
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
	
	public void tick() {
		if (enabled) {
			instruction = memory.getMemoryAddress(pointer);
			pointer++;
			boolean skipNext = false;
			
			if ((instruction & 0b1111110000000000) == 0b0001110000000000) {
				// ADDWF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				boolean sign = (instruction & 0b0000000000000001) == 1;
				
				setData(0, true, ALU.add(getData(0, true), getData(addr, flag), sign, false));
			}else if ((instruction & 0b1111110000000000) == 0b0001010000000000) {
				// ANDWF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				
				setData(0, true, ALU.and(getData(0, true), getData(addr, flag)));
			}else if ((instruction & 0b1111111000000000) == 0b0000011000000000) {
				// CLRF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000000000000001) == 1;
				
				setData(addr, flag, 0);
			}else if ((instruction & 0b1111111000000000) == 0b0000010000000000) {
				// CLRW
				
				setData(0, true, 0);
			}else if ((instruction & 0b1111110000000000) == 0b0010010000000000) {
				// COMF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				boolean sign = (instruction & 0b0000000000000001) == 1;
				
				setData(0, true, ALU.com(getData(addr, flag), sign));
			}else if ((instruction & 0b1111110000000000) == 0b0000110000000000) {
				// DECF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				boolean sign = (instruction & 0b0000000000000001) == 1;
				
				setData(addr, flag, ALU.dec(getData(addr, flag), sign));
			}else if ((instruction & 0b1111110000000000) == 0b0010110000000000) {
				// DECFSZ
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				boolean sign = (instruction & 0b0000000000000001) == 1;

				setData(addr, flag, ALU.dec(getData(addr, flag), sign));
				if (getData(addr, flag) == 0) {
					skipNext = true;
				}
			}else if ((instruction & 0b1111110000000000) == 0b0010100000000000) {
				// INCF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				boolean sign = (instruction & 0b0000000000000001) == 1;

				setData(addr, flag, ALU.inc(getData(addr, flag), sign));
			}else if ((instruction & 0b1111110000000000) == 0b0011110000000000) {
				// INCFSZ
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				boolean sign = (instruction & 0b0000000000000001) == 1;

				setData(addr, flag, ALU.inc(getData(addr, flag), sign));
				if (getData(addr, flag) == 0) {
					skipNext = true;
				}
			}else if ((instruction & 0b1111110000000000) == 0b0001000000000000) {
				// IORWF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				
				setData(0, true, ALU.or(getData(0, true), getData(addr, flag)));
			}else if ((instruction & 0b1111110000000000) == 0b0010000000000000) {
				// MOVF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				
				setData(0, true, getData(addr, flag));
			}else if ((instruction & 0b1111111000000000) == 0b0000001000000000) {
				// MOVWF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000000000000001) == 1;
				
				setData(addr, flag, getData(0, true));
			}else if ((instruction & 0b1111111001111110) == 0b0000000000000000) {
				// NOP
				// huh?
			}else if ((instruction & 0b1111110000000000) == 0b0011010000000000) {
				// RLF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				
				setData(0, true, ALU.shift(getData(addr, flag), true));
			}else if ((instruction & 0b1111110000000000) == 0b0011000000000000) {
				// RRF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				
				setData(0, true, ALU.shift(getData(addr, flag), false));
			}else if ((instruction & 0b1111110000000000) == 0b0000100000000000) {
				// SUBWF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				boolean sign = (instruction & 0b0000000000000001) == 1;
				
				setData(0, true, ALU.sub(getData(addr, flag), getData(0, true), sign));
			}else if ((instruction & 0b1111110000000000) == 0b0011100000000000) {
				// SWAPF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				
				setData(0, true, ALU.swap(getData(addr, flag)));
			}else if ((instruction & 0b1111110000000000) == 0b0001100000000000) {
				// XORWF
				int addr     = (instruction & 0b0000000111111110) >> 1;
				boolean flag = (instruction & 0b0000001000000000) >> 9 == 1;
				
				setData(0, true, ALU.xor(getData(addr, flag), getData(0, true)));
			}else if ((instruction & 0b1111000000000000) == 0b0100000000000000) {
				// BCF
				int addr     = instruction & 0b0000000011111111;
				int bit      = (instruction & 0b0000111100000000) >> 8;
				
				setData(0, true, ALU.setBit(getData(addr, true), bit, false));
			}else if ((instruction & 0b1111000000000000) == 0b0101000000000000) {
				// BSF
				int addr     = instruction & 0b0000000011111111;
				int bit      = (instruction & 0b0000111100000000) >> 8;

				setData(0, true, ALU.setBit(getData(addr, true), bit, true));
			}else if ((instruction & 0b1111000000000000) == 0b0110000000000000) {
				// BTFSC
				int addr     = instruction & 0b0000000011111111;
				int bit      = (instruction & 0b0000111100000000) >> 8;
				
				if (!ALU.getBit(getData(addr, true), bit)) {
					skipNext = true;
				}
			}else if ((instruction & 0b1111000000000000) == 0b0111000000000000) {
				// BTFSS
				int addr     = instruction & 0b0000000011111111;
				int bit      = (instruction & 0b0000111100000000) >> 8;

				if (ALU.getBit(getData(addr, true), bit)) {
					skipNext = true;
				}
			}else if ((instruction & 0b1111100000000010) == 0b1111100000000000) {
				// ADDLW
				int lit      = (instruction & 0b0000001111111100) >> 2;
				boolean sign = (instruction & 0b0000000000000001) == 1;
				
				setData(0, true, ALU.add(lit, getData(0, true), sign, false));
			}else if ((instruction & 0b1111110000000010) == 0b1110010000000000) {
				// ANDLW
				int lit      = (instruction & 0b0000001111111100) >> 2;
				
				setData(0, true, ALU.and(lit, getData(0, true)));
			}else if ((instruction & 0b1110000000000011) == 0b1000000000000001) {
				// CALL
				int addr     = (instruction & 0b0001111111100000) >> 5;
				
				pushAddress();
				pointer = addr;
			}else if ((instruction & 0b1111111111111100) == 0b0000000110010000) {
				// CLRWDT
				
				//TODO clear wdt
			}else if ((instruction & 0b1110000000000010) == 0b1010000000000000) {
				// GOTO
				int addr     = (instruction & 0b0001111111100000) >> 5;
				
				pointer = addr;
			}else if ((instruction & 0b1111110000000010) == 0b1110000000000000) {
				// IORLW
				int lit      = (instruction & 0b0000001111111100) >> 2;
				
				setData(0, true, ALU.or(getData(0, true), lit));
			}else if ((instruction & 0b1111000000000010) == 0b1100000000000000) {
				// MOVLW
				int lit      = (instruction & 0b0000001111111100) >> 2;
				
				setData(0, true, lit);
			}else if ((instruction & 0b1111111111111100) == 0b0000000000100100) {
				// RETFIE
				
				popAddress();
				//TODO pop all the crap outta stack
			}else if ((instruction & 0b1111000000000010) == 0b1101000000000000) {
				// RETLW
				int lit      = (instruction & 0b0000001111111100) >> 2;
				
				setData(0, true, lit);
				popAddress();
			}else if ((instruction & 0b1111111111111100) == 0b0000000000100000) {
				// RETURN
				
				popAddress();
			}else if ((instruction & 0b1111111111111100) == 0b0000000110001100) {
				// SLEEP
				
				//TODO sleep?
			}else if ((instruction & 0b1111100000000010) == 0b1111000000000000) {
				// SUBLW
				int lit      = (instruction & 0b0000001111111100) >> 2;
				boolean sign = (instruction & 0b0000000000000001) == 1;
				
				setData(0, true, ALU.sub(lit, getData(0, true), sign));
			}else if ((instruction & 0b1111110000000010) == 0b1110100000000000) {
				// XORLW
				int lit      = (instruction & 0b0000001111111100) >> 2;
				
				setData(0, true, ALU.xor(lit, getData(0, true)));
			}else if ((instruction & 0b1111100000000010) == 0b1000000000000010) {
				// FADDWF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
				setData(0, true, FPU.add(getData(addr, flag), getData(0, true)));
			}else if ((instruction & 0b1111100000000010) == 0b1000100000000010) {
				// FCOMF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
				setData(0, true, ALU.xor(getData(addr, flag), 0x8000));
			}else if ((instruction & 0b1111100000000010) == 0b1001000000000010) {
				// FDECF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
				setData(0, true, FPU.sub(getData(addr, flag), FPU.one()));
			}else if ((instruction & 0b1111100000000010) == 0b1001100000000010) {
				// FDECFSZ
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
				setData(0, true, FPU.sub(getData(addr, flag), FPU.one()));
				if (getData(0, true) == 0 || getData(0, true) == 0x8000) {
					skipNext = true;
				}
			}else if ((instruction & 0b1111100000000010) == 0b1010000000000010) {
				// FINCF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;

				setData(0, true, FPU.add(getData(addr, flag), FPU.one()));
			}else if ((instruction & 0b1111100000000010) == 0b1010100000000010) {
				// FINCFSZ
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;

				setData(0, true, FPU.add(getData(addr, flag), FPU.one()));
				if (getData(0, true) == 0 || getData(0, true) == 0x8000) {
					skipNext = true;
				}
			}else if ((instruction & 0b1111100000000010) == 0b1011000000000010) {
				// FSUBWF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
				setData(0, true, FPU.sub(getData(addr, flag), getData(0, true)));
			}else if ((instruction & 0b1111100000000010) == 0b1011100000000010) {
				// FMULWF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
				setData(0, true, FPU.mul(getData(addr, flag), getData(0, true)));
			}else if ((instruction & 0b1111100000000010) == 0b1100000000000010) {
				// FDIVWF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;

				setData(0, true, FPU.div(getData(addr, flag), getData(0, true)));
			}else if ((instruction & 0b1111100000000010) == 0b1100100000000010) {
				// FPTINT
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				boolean sign = (instruction & 0b0000000000000001) == 1;
				
			}else if ((instruction & 0b1111100000000010) == 0b1101000000000010) {
				// INTTFP
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				boolean sign = (instruction & 0b0000000000000001) == 1;
				
			}else if ((instruction & 0b1111100000000010) == 0b1101100000000010) {
				// FCOMPWF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
			}else if ((instruction & 0b1111000000000011) == 0b1000000000000001) {
				// MULWF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				boolean sign = (instruction & 0b0000100000000000) >> 11 == 1;
				
			}else if ((instruction & 0b1111000000000011) == 0b1001000000000001) {
				// DIVWF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				boolean sign = (instruction & 0b0000100000000000) >> 11 == 1;
				
			}else if ((instruction & 0b1111000000000011) == 0b1010000000000001) {
				// BOOL
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
			}else if ((instruction & 0b1111000000000011) == 0b1011000000000001) {
				// STO
				int addr     = (instruction & 0b0000001111111100) >> 2;
				int prog     = (instruction & 0b0000110000000000) >> 10;
				
			}else if ((instruction & 0b1111000000000011) == 0b1100000000000001) {
				// SHFTRN
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
			}else if ((instruction & 0b1111000000000011) == 0b1101000000000001) {
				// SHFTLN
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
			}else if ((instruction & 0b1111000000000011) == 0b1110000000000001) {
				// SHFTRR
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
			}else if ((instruction & 0b1111000000000011) == 0b1111000000000001) {
				// SHFTLR
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				
			}else if ((instruction & 0b1111000000000011) == 0b1101000000000010) {
				// COMPWF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				boolean sign = (instruction & 0b0000100000000000) >> 11 == 1;
				
			}else if ((instruction & 0b1111000000000011) == 0b1110000000000010) {
				// LOADLW
				int addr     = (instruction & 0b0000001111111100) >> 2;
				boolean flag = (instruction & 0b0000010000000000) >> 10 == 1;
				int lit      = memory.getMemoryAddress(pointer);
				skipNext = true;
				
			}else if ((instruction & 0b1111000000000011) == 0b1111000000000010) {
				// GOTOF
				int addr     = (instruction & 0b0000001111111100) >> 2;
				int cond     = memory.getMemoryAddress(pointer);
				skipNext = true; //TODO only if we don't jump
				
			}else if ((instruction & 0b1111000000000011) == 0b1000000000000010) {
				// SBAP
				int addr     = (instruction & 0b0000001111111100) >> 2;
				int core     = (instruction & 0b0000110000000000) >> 10;
				
			}else if ((instruction & 0b1111000000000011) == 0b1001000000000010) {
				// STTH
				int core     = (instruction & 0b0000110000000000) >> 10;
				
			}else if ((instruction & 0b1111000000000011) == 0b1010000000000010) {
				// SPTH
				int core     = (instruction & 0b0000110000000000) >> 10;
				
			}else if ((instruction & 0b1111000000000011) == 0b1011000000000010) {
				// LOCK
				
			}else if ((instruction & 0b1111000000000011) == 0b1100000000000010) {
				// UNLOCK
				
			}
			if (skipNext) {
				pointer++;
			}
			if (pointer > 255) {
				disable();
			}
		}
	}

	private void setData(int id, boolean flag, int data) {
		if (flag) {
			setRegister(id, data);
		}else{
			memory.setMemoryAddress(id, data);
		}
	}
	
	private int getData(int id, boolean flag) {
		if (flag) {
			return getRegister(id);
		}else{
			return memory.getMemoryAddress(id);
		}
	}
	
	private void setRegister(int id, int data) {
		if (id == 20) { // STACK
			if (stackPtr == 63) {
				// stack overflow
				return;
			}
			stack[stackPtr] = data;
			stackPtr++;
			return;
		}
		if (id > 12 && id < 20) { // FLAGS
			data = data & 1;
		}
		if (id > 34) { // BWIO
			registers[id] = (int) (registers[id] | ((data & 1) << (id - 35)));
			return;
		}
		registers[id] = data;
	}
	
	private int getRegister(int id) {
		if (id == 20) {
			return stack[stackPtr];
		}
		if (id > 34) {
			return (int) ((registers[id] >> (id - 35)) & 1);
		}
		return registers[id];
	}
	
	private void popStack() {
		stack[stackPtr] = 0;
		if (stackPtr == 0) {
			// stack underflow
			return;
		}
		stackPtr--;
	}
	
	private void pushAddress() {
		addrStack[addrPtr] = pointer;
		if (addrPtr < 64) {
			addrPtr++;
		}
	}
	
	private void popAddress() {
		if (addrPtr > 0) {
			addrPtr--;
		}
		pointer = addrStack[addrPtr];
	}
}
