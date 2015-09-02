package com.redstoner.nemes.redstone_sheep;

public class Resolver {

	public static void resolve(String text, String fileName) {
		String[] lines = text.split("\n");
		for (int i = 0; i < lines.length; i++) {
			lines[i] += " ";
		}
		String compiled = "";
		
		for (int i = 0; i < lines.length; i++) {
			// <dirty>
			String temp1 = "";
			try {
				temp1 = lines[i].substring(lines[i].indexOf(' ') + 1);
			} catch(Exception e) {}
			String temp2 = "";
			try {
				temp2 = temp1.substring(temp1.indexOf(' ') + 1);
			} catch(Exception e) {}
			String first = Instruction.NOP.toString();
			try {
				first = lines[i].substring(0, lines[i].indexOf(' ')).toUpperCase();
			} catch(Exception e) {}
			String arg1 = "";
			try {
				arg1 = temp1.substring(0, temp1.indexOf(", ")).toUpperCase();
			} catch(Exception e) {
				try {
					arg1 = temp1.substring(0, temp1.indexOf(' ')).toUpperCase();
				} catch(Exception e1) {}
			}
			String arg2 = "";
			try {
				arg2 = temp2.substring(0, temp2.indexOf(' ')).toUpperCase();
			} catch(Exception e) {}
			// </dirty>
			// <semi-dirty>
			try {
				if (first.equals(Instruction.ADDWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000111" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.ADDWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.ADDWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.ANDWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000101" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.ANDWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.ANDWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.CLRF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1 && !register) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "0000011" + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.CLRF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.CLRF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.CLRW.toString())) {
					String instr = "0000010000000000";
					compiled += instr + "\n";
				}else if (first.equals(Instruction.COMF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001001" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.COMF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.COMF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.DECF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000011" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.DECF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.DECF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.DECFSZ.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001011" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.DECFSZ.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.DECFSZ.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.INCF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001010" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.INCF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.INCF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.INCFSZ.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001111" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.INCFSZ.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.INCFSZ.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.IORWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000100" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.IORWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.IORWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.MOVF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001000" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.MOVF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.MOVF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.MOVWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "0000001" + fixToBitSize(addr, EnumSize.BITSIZE_8) + (register ? "0" : "1");
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.MOVWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.MOVWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.NOP.toString())) {
					String instr = "0000000000000000";
					compiled += instr + "\n";
				}else if (first.equals(Instruction.RLF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001101" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.RLF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.RLF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.RRF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001100" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.RRF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.RRF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.SUBWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000010" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.SUBWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.SUBWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.SWAPF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001110" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.SWAPF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.SWAPF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.XORWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000110" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.XORWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.XORWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.BCF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1 && !register) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							if (isNumber(arg2) && isInRange(Integer.parseInt(arg2), EnumSize.BITSIZE_4)) {
								String instr = "0100" + fixToBitSize(Integer.parseInt(arg2), EnumSize.BITSIZE_4) + fixToBitSize(addr, EnumSize.BITSIZE_8);
								compiled += instr + "\n";
							}else{
								throw new BuildException(Instruction.BCF.toString() + ": bit address invalid!");
							}
						}else{
							throw new BuildException(Instruction.BCF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.BCF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.BSF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1 && !register) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							if (isNumber(arg2) && isInRange(Integer.parseInt(arg2), EnumSize.BITSIZE_4)) {
								String instr = "0101" + fixToBitSize(Integer.parseInt(arg2), EnumSize.BITSIZE_4) + fixToBitSize(addr, EnumSize.BITSIZE_8);
								compiled += instr + "\n";
							}else{
								throw new BuildException(Instruction.BSF.toString() + ": bit address invalid!");
							}
						}else{
							throw new BuildException(Instruction.BSF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.BSF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.BTFSC.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1 && !register) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							if (isNumber(arg2) && isInRange(Integer.parseInt(arg2), EnumSize.BITSIZE_4)) {
								String instr = "0110" + fixToBitSize(Integer.parseInt(arg2), EnumSize.BITSIZE_4) + fixToBitSize(addr, EnumSize.BITSIZE_8);
								compiled += instr + "\n";
							}else{
								throw new BuildException(Instruction.BTFSC.toString() + ": bit address invalid!");
							}
						}else{
							throw new BuildException(Instruction.BTFSC.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.BTFSC.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.BTFSS.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1 && !register) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							if (isNumber(arg2) && isInRange(Integer.parseInt(arg2), EnumSize.BITSIZE_4)) {
								String instr = "0111" + fixToBitSize(Integer.parseInt(arg2), EnumSize.BITSIZE_4) + fixToBitSize(addr, EnumSize.BITSIZE_8);
								compiled += instr + "\n";
							}else{
								throw new BuildException(Instruction.BTFSS.toString() + ": bit address invalid!");
							}
						}else{
							throw new BuildException(Instruction.BTFSS.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.BTFSS.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.ADDLW.toString())) {
					if (isNumber(arg1)) {
						int lit = Integer.parseInt(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "111110" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "0" + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.ADDLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException(Instruction.ADDLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.ANDLW.toString())) {
					if (isNumber(arg1)) {
						int lit = Integer.parseInt(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "111001" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "00";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.ANDLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException(Instruction.ANDLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.CALL.toString())) {
					//TODO CALL
				}else if (first.equals(Instruction.CLRWDT.toString())) {
					String instr = "0000000110010000";
					compiled += instr + "\n";
				}else if (first.equals(Instruction.GOTO.toString())) {
					//TODO GOTO
				}else if (first.equals(Instruction.IORLW.toString())) {
					if (isNumber(arg1)) {
						int lit = Integer.parseInt(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "111000" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "00";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.IORLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException(Instruction.IORLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.MOVLW.toString())) {
					if (isNumber(arg1)) {
						int lit = Integer.parseInt(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "110000" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "00";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.MOVLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException(Instruction.MOVLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.RETFIE.toString())) {
					String instr = "0000000000100100";
					compiled += instr + "\n";
				}else if (first.equals(Instruction.RETLW.toString())) {
					if (isNumber(arg1)) {
						int lit = Integer.parseInt(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "110100" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "00";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.RETLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException(Instruction.RETLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.RETURN.toString())) {
					String instr = "0000000000100000";
					compiled += instr + "\n";
				}else if (first.equals(Instruction.SLEEP.toString())) {
					String instr = "0000000110001100";
					compiled += instr + "\n";
				}else if (first.equals(Instruction.SUBLW.toString())) {
					if (isNumber(arg1)) {
						int lit = Integer.parseInt(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "111100" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "0" + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.SUBLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException(Instruction.SUBLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.XORLW.toString())) {
					if (isNumber(arg1)) {
						int lit = Integer.parseInt(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "111010" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "00";
							compiled += instr + "\n";
						}else{
							throw new BuildException(Instruction.XORLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException(Instruction.XORLW.toString() + ": literal invalid!");
					}
				}
			}catch (BuildException e) {
				System.err.println(e.getLocalizedMessage());
				return;
			}
			// </semi-dirty>
		}
		
		System.out.println(compiled);
		FileManager.write(text, compiled, fileName);
	}
	
	public static boolean isNumber(String s) {
		char[] chars = s.toCharArray();
		for (char c : chars) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isInRange(int i, EnumSize range) {
		if (i < 0) {
			return false;
		}
		if (range == EnumSize.BITSIZE_2 && i <= 0x3) {
			return true;
		}else if (range == EnumSize.BITSIZE_4 && i <= 0xF) {
			return true;
		}else if (range == EnumSize.BITSIZE_8 && i <= 0xFF) {
			return true;
		}else if (range == EnumSize.BITSIZE_16 && i <= 0xFFFF) {
			return true;
		}
		return false;
	}
	
	public static String fixToBitSize(int in, EnumSize range) {
		String s = Integer.toBinaryString(in);
		int begin = s.length();
		if (range == EnumSize.BITSIZE_2 && s.length() < 2) {
			for (int i = 0; i < 2 - begin; i++) {
				s = '0' + s;
			}
		}else if (range == EnumSize.BITSIZE_4 && s.length() < 4) {
			for (int i = 0; i < 4 - begin; i++) {
				s = '0' + s;
			}
		}else if (range == EnumSize.BITSIZE_8 && s.length() < 8) {
			for (int i = 0; i < 8 - begin; i++) {
				s = '0' + s;
			}
		}else if (range == EnumSize.BITSIZE_16 && s.length() < 16) {
			for (int i = 0; i < 16 - begin; i++) {
				s = '0' + s;
			}
		}
		return s;
	}
	
	public static int translateRegister(String reg) {
		switch (reg.toLowerCase()) {
		case "-": return 0;
		case "w": return 0;
		case "ax": return 1;
		case "bx": return 2;
		case "cx": return 3;
		case "dx": return 4;
		case "r5": return 5;
		case "r6": return 6;
		case "r7": return 7;
		case "r8": return 8;
		case "cr1": return 9;
		case "cr2": return 10;
		case "cr3": return 11;
		case "cr4": return 12;
		case "stack": return 20;
		case "tr1": return 21;
		case "tr2": return 22;
		case "tr3": return 23;
		case "tr4": return 24;
		case "pnni": return 25;
		case "gpioi": return 26;
		case "bwioi": return 27;
		case "eti": return 28;
		case "exi": return 29;
		case "pnn1": return 30;
		case "pnn2": return 31;
		case "pnn3": return 32;
		case "pnn4": return 33;
		case "gpio1": return 34;
		case "gpio2": return 35;
		case "gpio3": return 36;
		case "gpio4": return 37;
		case "bwio1": return 38;
		case "bwio2": return 39;
		case "bwio3": return 40;
		case "bwio4": return 41;
		case "bwio5": return 42;
		case "bwio6": return 43;
		case "bwio7": return 44;
		case "bwio8": return 45;
		case "bwio9": return 46;
		case "bwio10": return 47;
		case "bwio11": return 48;
		case "bwio12": return 49;
		case "bwio13": return 50;
		case "bwio14": return 51;
		case "bwio15": return 52;
		case "bwio16": return 53;
		}
		if (isNumber(reg)) {
			return Integer.parseInt(reg) - 1000;
		}
		return -1;
	}
}
