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
					if (isNumber(arg1)) {
						if (isInRange(Integer.parseInt(arg1), EnumSize.BITSIZE_8)) {
							String instr = "000111x" + fixToBitSize(Integer.parseInt(arg1), EnumSize.BITSIZE_8) + "s";
							System.out.println(instr);
						}else{
							throw new BuildException(Instruction.ADDWF.toString() + ": memory address exceeds maximum!");
						}
					}else{
						throw new BuildException(Instruction.ADDWF.toString() + ": memory address must be a number!");
					}
				}
			}catch (BuildException e) {
				e.printStackTrace();
				return;
			}
			// </semi-dirty>
		}
		
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
}
