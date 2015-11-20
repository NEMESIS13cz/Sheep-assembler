package com.redstoner.nemes.redstone_sheep;

import java.util.HashMap;

public class Assembler {
	
	private static HashMap<String, Integer> map = new HashMap<String, Integer>();

	public static String resolve(String text, String fileName) {
		long begin = System.nanoTime();
		boolean usingBCX = false;
		boolean usingFPX = false;
		boolean usingMCX = false;
		map.clear();
		String[] lines = text.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String s = lines[i];
			String res = "";
			boolean last = false;
			boolean isFirst = true;
			for (char c : s.toCharArray()) {
				if (c == ' ' || c == '	') {
					if (!last && !isFirst) {
						res += c;
						last = true;
					}
				}else{
					last = false;
					isFirst = false;
					res += c;
				}
			}
			lines[i] = res + " ";
		}
		String compiled = "";
		int address = 0;
		
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
				if (first.startsWith(".") && first.endsWith(":")) {
					map.put(first.substring(0, first.length() - 1), address + 1);
				}else if ((first.equals("DEF") || first.equals("FUNCTION")) && arg1.endsWith(":")) {
					map.put(arg1.substring(0, arg1.length() - 1), address + 1);
				}else if (first.equals(Instruction.ADDWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000111" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.ADDWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.ADDWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.ANDWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000101" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.ANDWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.ANDWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.CLRF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1 && !register) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "0000011" + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.CLRF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.CLRF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.CLRW.toString())) {
					String instr = "0000010000000000";
					compiled += instr + "\n"; address++;
				}else if (first.equals(Instruction.COMF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001001" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.COMF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.COMF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.DECF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000011" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.DECF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.DECF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.DECFSZ.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001011" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.DECFSZ.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.DECFSZ.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.INCF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001010" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.INCF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.INCF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.INCFSZ.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001111" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.INCFSZ.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.INCFSZ.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.IORWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000100" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.IORWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.IORWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.MOVF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001000" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.MOVF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.MOVF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.MOVWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "0000001" + fixToBitSize(addr, EnumSize.BITSIZE_8) + (register ? "0" : "1");
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.MOVWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.MOVWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.NOP.toString())) {
					String instr = "0000000000000000";
					compiled += instr + "\n"; address++;
				}else if (first.equals(Instruction.RLF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001101" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.RLF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.RLF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.RRF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001100" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.RRF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.RRF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.SUBWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000010" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.SUBWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.SUBWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.SWAPF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "001110" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.SWAPF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.SWAPF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.XORWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "000110" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "0";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.XORWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.XORWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.BCF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1 && !register) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							if (isNumber(arg2) && isInRange(parseNumber(arg2), EnumSize.BITSIZE_4)) {
								String instr = "0100" + fixToBitSize(parseNumber(arg2), EnumSize.BITSIZE_4) + fixToBitSize(addr, EnumSize.BITSIZE_8);
								compiled += instr + "\n"; address++;
							}else{
								throw new BuildException("line " + (i + 1) + " " + Instruction.BCF.toString() + ": bit address invalid!");
							}
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.BCF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.BCF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.BSF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1 && !register) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							if (isNumber(arg2) && isInRange(parseNumber(arg2), EnumSize.BITSIZE_4)) {
								String instr = "0101" + fixToBitSize(parseNumber(arg2), EnumSize.BITSIZE_4) + fixToBitSize(addr, EnumSize.BITSIZE_8);
								compiled += instr + "\n"; address++;
							}else{
								throw new BuildException("line " + (i + 1) + " " + Instruction.BSF.toString() + ": bit address invalid!");
							}
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.BSF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.BSF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.BTFSC.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1 && !register) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							if (isNumber(arg2) && isInRange(parseNumber(arg2), EnumSize.BITSIZE_4)) {
								String instr = "0110" + fixToBitSize(parseNumber(arg2), EnumSize.BITSIZE_4) + fixToBitSize(addr, EnumSize.BITSIZE_8);
								compiled += instr + "\n"; address++;
							}else{
								throw new BuildException("line " + (i + 1) + " " + Instruction.BTFSC.toString() + ": bit address invalid!");
							}
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.BTFSC.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.BTFSC.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.BTFSS.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1 && !register) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							if (isNumber(arg2) && isInRange(parseNumber(arg2), EnumSize.BITSIZE_4)) {
								String instr = "0111" + fixToBitSize(parseNumber(arg2), EnumSize.BITSIZE_4) + fixToBitSize(addr, EnumSize.BITSIZE_8);
								compiled += instr + "\n"; address++;
							}else{
								throw new BuildException("line " + (i + 1) + " " + Instruction.BTFSS.toString() + ": bit address invalid!");
							}
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.BTFSS.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.BTFSS.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.ADDLW.toString())) {
					if (isNumber(arg1)) {
						int lit = parseNumber(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "111110" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "0" + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.ADDLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.ADDLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.ANDLW.toString())) {
					if (isNumber(arg1)) {
						int lit = parseNumber(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "111001" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "00";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.ANDLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.ANDLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.CALL.toString())) {
					String instr = "100<" + arg1 + ">00001";
					compiled += instr + "\n"; address++;
				}else if (first.equals(Instruction.CLRWDT.toString())) {
					String instr = "0000000110010000";
					compiled += instr + "\n"; address++;
				}else if (first.equals(Instruction.GOTO.toString())) {
					String instr = "101<" + arg1 + ">00000";
					compiled += instr + "\n"; address++;
				}else if (first.equals(Instruction.IORLW.toString())) {
					if (isNumber(arg1)) {
						int lit = parseNumber(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "111000" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "00";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.IORLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.IORLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.MOVLW.toString())) {
					if (isNumber(arg1)) {
						int lit = parseNumber(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "110000" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "00";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.MOVLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.MOVLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.RETFIE.toString())) {
					String instr = "0000000000100100";
					compiled += instr + "\n"; address++;
				}else if (first.equals(Instruction.RETLW.toString())) {
					if (isNumber(arg1)) {
						int lit = parseNumber(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "110100" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "00";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.RETLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.RETLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.RETURN.toString())) {
					String instr = "0000000000100000";
					compiled += instr + "\n"; address++;
				}else if (first.equals(Instruction.SLEEP.toString())) {
					String instr = "0000000110001100";
					compiled += instr + "\n"; address++;
				}else if (first.equals(Instruction.SUBLW.toString())) {
					if (isNumber(arg1)) {
						int lit = parseNumber(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "111100" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "0" + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.SUBLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.SUBLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.XORLW.toString())) {
					if (isNumber(arg1)) {
						int lit = parseNumber(arg1);
						if (isInRange(lit, EnumSize.BITSIZE_8)) {
							String instr = "111010" + fixToBitSize(lit, EnumSize.BITSIZE_8) + "00";
							compiled += instr + "\n"; address++;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.XORLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.XORLW.toString() + ": literal invalid!");
					}
				}else if (first.equals(Instruction.FADDWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "10000" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.FADDWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.FADDWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.FCOMF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "10001" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.FCOMF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.FCOMF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.FDECF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "10010" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.FDECF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.FDECF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.FDECFSZ.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "10011" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.FDECFSZ.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.FDECFSZ.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.FINCF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "10100" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.FINCF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.FINCF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.FINCFSZ.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "10101" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.FINCFSZ.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.FINCFSZ.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.FSUBWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "10110" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.FSUBWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.FSUBWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.FMULWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "10111" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.FMULWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.FMULWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.FDIVWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "11000" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.FDIVWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.FDIVWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.FPTINT.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "11001" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "1" + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.FPTINT.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.FPTINT.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.INTTFP.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "11010" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "1" + (arg2.toLowerCase().equals("-u") ? "0" : "1");
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.INTTFP.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.INTTFP.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.FCOMPWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "11011" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingFPX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.FCOMPWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.FCOMPWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.MULWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "1000" + (arg2.toLowerCase().equals("-u") ? "0" : "1") + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "01";
							compiled += instr + "\n"; address++;
							usingBCX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.MULWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.MULWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.DIVWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "1001" + (arg2.toLowerCase().equals("-u") ? "0" : "1") + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "01";
							compiled += instr + "\n"; address++;
							usingBCX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.DIVWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.DIVWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.BOOL.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "10100" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "01";
							compiled += instr + "\n"; address++;
							usingBCX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.BOOL.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.BOOL.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.STO.toString())) {
					int addr = parseNumber(arg1);
					int prgID = parseNumber(arg2);
					if (isInRange(prgID, EnumSize.BITSIZE_2)) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "1011" + fixToBitSize(prgID, EnumSize.BITSIZE_2) + fixToBitSize(addr, EnumSize.BITSIZE_8) + "01";
							compiled += instr + "\n"; address++;
							usingBCX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.STO.toString() + ": memory offset exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.STO.toString() + ": program ID exceeds maximum!");
					}
				}else if (first.equals(Instruction.SHFTRN.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "11000" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "01";
							compiled += instr + "\n"; address++;
							usingBCX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.SHFTRN.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.SHFTRN.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.SHFTLN.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "11010" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "01";
							compiled += instr + "\n"; address++;
							usingBCX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.SHFTLN.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.SHFTLN.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.SHFTRR.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "11100" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "01";
							compiled += instr + "\n"; address++;
							usingBCX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.SHFTRR.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.SHFTRR.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.SHFTLR.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "11110" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "01";
							compiled += instr + "\n"; address++;
							usingBCX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.SHFTLR.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.SHFTLR.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.COMPWF.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					if (addr != -1) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "1101" + (register ? "0" : "1") + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingBCX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.COMPWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.COMPWF.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.LOADLW.toString())) {
					int addr = translateRegister(arg1);
					boolean register = addr > -1;
					if (!register) addr += 1000;
					int lit = parseNumber(arg2);
					if (addr != -1) {
						if (isInRange(lit, EnumSize.BITSIZE_16)) {
							if (isInRange(addr, EnumSize.BITSIZE_8)) {
								String instr = "11100" + (register ? "0" : "1") + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
								compiled += instr + "\n"; address++;
								compiled += fixToBitSize(lit, EnumSize.BITSIZE_16) + "\n"; address++;
								usingBCX = true;
							}else{
								throw new BuildException("line " + (i + 1) + " " + Instruction.LOADLW.toString() + ": memory address exceeds maximum!");
							}
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.LOADLW.toString() + ": literal too big!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.LOADLW.toString() + ": memory address invalid!");
					}
				}else if (first.equals(Instruction.GOTOF.toString())) {
					int cond = parseNumber(arg2);
					if (isInRange(cond, EnumSize.BITSIZE_16)) {
						String instr = "111100<" + arg1 + ">10";
						compiled += instr + "\n"; address++;
						compiled += fixToBitSize(cond, EnumSize.BITSIZE_16) + "\n"; address++;
						usingBCX = true;
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.GOTOF.toString() + ": conditions invalid!");
					}
				}else if (first.equals(Instruction.SBAP.toString())) {
					int addr = parseNumber(arg1);
					int thread = parseNumber(arg2);
					if (isInRange(thread, EnumSize.BITSIZE_2)) {
						if (isInRange(addr, EnumSize.BITSIZE_8)) {
							String instr = "1000" + fixToBitSize(thread, EnumSize.BITSIZE_2) + fixToBitSize(addr, EnumSize.BITSIZE_8) + "10";
							compiled += instr + "\n"; address++;
							usingMCX = true;
						}else{
							throw new BuildException("line " + (i + 1) + " " + Instruction.SBAP.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.SBAP.toString() + ": thread ID exceeds maximum!");
					}
				}else if (first.equals(Instruction.STTH.toString())) {
					int thread = parseNumber(arg2);
					if (isInRange(thread, EnumSize.BITSIZE_2)) {
						String instr = "1001" + fixToBitSize(thread, EnumSize.BITSIZE_2) + "0000000010";
						compiled += instr + "\n"; address++;
						usingMCX = true;
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.STTH.toString() + ": thread ID exceeds maximum!");
					}
				}else if (first.equals(Instruction.SPTH.toString())) {
					int thread = parseNumber(arg2);
					if (isInRange(thread, EnumSize.BITSIZE_2)) {
						String instr = "1010" + fixToBitSize(thread, EnumSize.BITSIZE_2) + "0000000010";
						compiled += instr + "\n"; address++;
						usingMCX = true;
					}else{
						throw new BuildException("line " + (i + 1) + " " + Instruction.SPTH.toString() + ": thread ID exceeds maximum!");
					}
				}else if (first.equals(Instruction.LOCK.toString())) {
					String instr = "1011000000000010";
					compiled += instr + "\n"; address++;
					usingMCX = true;
				}else if (first.equals(Instruction.UNLOCK.toString())) {
					String instr = "1100000000000010";
					compiled += instr + "\n"; address++;
					usingMCX = true;
				}
			}catch (BuildException e) {
				IDE.errln(e.getLocalizedMessage());
				return null;
			}
			// </semi-dirty>
		}
		
		String compiled_ = "";
		String buffer = "";
		boolean tag = false;
		for (char c : compiled.toCharArray()) {
			if (tag) {
				if (c == '>') {
					tag = false;
					int addr = 0;
					try{
						addr = map.get(buffer);
					}catch (NullPointerException e) {
						IDE.errln("Address linker: Could not find address space! (" + buffer + ")");
						return null;
					}
					if (isInRange(addr, EnumSize.BITSIZE_8)) {
						compiled_ += fixToBitSize(addr, EnumSize.BITSIZE_8);
					}else{
						IDE.errln("Address linker: Address out of range! (" + buffer + ")");
						return null;
					}
					buffer = "";
					continue;
				}
				buffer += c;
			}else if (c == '<') {
				tag = true;
			}else{
				compiled_ += c;
			}
		}
		long done = System.nanoTime();
		
		int size = FileManager.write(text, compiled_, fileName);
		IDE.println(compiled_);
		IDE.println("Done assembling...");
		IDE.println("Using BCX: " + usingBCX);
		IDE.println("Using FPX: " + usingFPX);
		IDE.println("Using MCX: " + usingMCX);
		IDE.println("Total size: \n    " + size + " bytes (" + (size / 2) + " addresses)");
		IDE.println("Finished in " + (done - begin) + "ns (" + ((done - begin) / 1000000) + "ms)");
		IDE.println("");
		System.gc();
		return compiled_;
	}
	
	public static boolean isNumber(String s) {
		char[] chars = s.toCharArray();
		for (char c : chars) {
			if (!Character.isDigit(c) && !s.startsWith("0X") && !s.startsWith("0B")) {
				return false;
			}
		}
		return true;
	}
	
	public static int parseNumber(String s) {
		if (s.startsWith("0X")) {
			return Integer.parseInt(s.substring(2, s.length()), 16);
		}else if (s.startsWith("0B")) {
			return Integer.parseInt(s.substring(2, s.length()), 2);
		}else{
			return Integer.parseInt(s, 10);
		}
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
