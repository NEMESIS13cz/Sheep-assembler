package com.redstoner.nemes.redstone_sheep;

public class FPU {
	
	public int add(int a, int b) {
		int mantissa_a = (a & 0x03FF) << 20 | 0x40000000;
		int mantissa_b = (b & 0x03FF) << 20 | 0x40000000;
		int exponent_a = (a & 0x7C00) >> 10;
		int exponent_b = (b & 0x7C00) >> 10;
		boolean sign_a = (a & 0x8000) >> 15 == 1;
		boolean sign_b = (b & 0x8000) >> 15 == 1;
		
		int comp = exponent_a == exponent_b ? mantissa_a == mantissa_b ? 1 : mantissa_a > mantissa_b ? 0 : 2 : exponent_a > exponent_b ? 0 : 2;
		int diff = Math.abs(exponent_a) > Math.abs(exponent_b) ? Math.abs(exponent_a) - Math.abs(exponent_b) : Math.abs(exponent_b) - Math.abs(exponent_a);
		boolean invert = sign_a ^ sign_b;
		
		int aligned_a = exponent_a > exponent_b ? mantissa_a : mantissa_a >> diff;
		int aligned_b = exponent_a < exponent_b ? mantissa_b : mantissa_b >> diff;
		
		int inv_a = invert && (comp == 0 || comp == 1) ? aligned_a ^ 0xFFFFFFFF : aligned_a;
		int inv_b = invert && (comp == 2)              ? aligned_b ^ 0xFFFFFFFF : aligned_b;
		
		int res = (inv_a & 0x7FFFFFFF) + (inv_b & 0x7FFFFFFF);
		int norm_res = 0;
		int norm_project = 0;
		int final_exp = comp == 2 ? exponent_b : exponent_a;
		boolean final_sign = invert ? comp == 2 ? sign_a : sign_b : sign_a;
		
		if ((res & 0x80000000) == 0x80000000) {
			norm_res = res >> 20;
			norm_project = -1;
		}else{
			while ((res & 0x80000000) != 0x80000000) {
				res = res << 1;
				norm_project++;
			}
			norm_res = res >> 19;
		}
		final_exp -= norm_project;
		
		final_exp = final_exp & 0x1F;
		norm_res = norm_res & 0x3FF;
		
		return (final_sign ? 0x8000 : 0) | (final_exp << 10) | norm_res;
	}
	
	public int sub(int a, int b) {
		return add(a, b ^ 0x8000);
	}
	
	public int mul(int a, int b) {
		int mantissa_a = (a & 0x03FF);
		int mantissa_b = (b & 0x03FF);
		int exponent_a = (a & 0x7C00) >> 10;
		int exponent_b = (b & 0x7C00) >> 10;
		boolean sign_a = (a & 0x8000) >> 15 == 1;
		boolean sign_b = (b & 0x8000) >> 15 == 1;
		
		int final_exponent = (exponent_a + exponent_b) & 0x1F;
		boolean final_sign = sign_a ^ sign_b;
		int mul_mantissa = (mantissa_a | 0x400) * (mantissa_b | 0x400);
		int final_mantissa = 0;
		
		if (mul_mantissa > 0x700) {
			while (mul_mantissa > 0x700) {
				mul_mantissa >>= 1;
				final_exponent++;
			}
			final_mantissa = mul_mantissa & 0x3FF;
		}else{
			while (mul_mantissa < 0x700) {
				mul_mantissa <<= 1;
				final_exponent--;
			}
			final_mantissa = mul_mantissa & 0x3FF;
		}
		
		return (final_sign ? 0x8000 : 0) | (final_exponent << 10) | final_mantissa;
	}
	
	public int div(int a, int b) {
		int mantissa_a = (a & 0x03FF);
		int mantissa_b = (b & 0x03FF);
		int exponent_a = (a & 0x7C00) >> 10;
		int exponent_b = (b & 0x7C00) >> 10;
		boolean sign_a = (a & 0x8000) >> 15 == 1;
		boolean sign_b = (b & 0x8000) >> 15 == 1;
		
		int final_exponent = (exponent_a - exponent_b + 0xF) & 0x1F;
		boolean final_sign = sign_a ^ sign_b;
		int mul_mantissa = (mantissa_a | 0x400) / (mantissa_b | 0x400);
		int final_mantissa = 0;
		
		if (mul_mantissa > 0x700) {
			while (mul_mantissa > 0x700) {
				mul_mantissa >>= 1;
				final_exponent++;
			}
			final_mantissa = mul_mantissa & 0x3FF;
		}else{
			while (mul_mantissa < 0x700) {
				mul_mantissa <<= 1;
				final_exponent--;
			}
			final_mantissa = mul_mantissa & 0x3FF;
		}
		
		return (final_sign ? 0x8000 : 0) | (final_exponent << 10) | final_mantissa;
	}
	
	public int one() {
		return 0b0011110000000000;
	}
}
