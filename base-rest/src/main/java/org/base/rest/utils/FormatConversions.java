package org.base.rest.utils;

import java.util.Scanner;
import java.util.Stack;

public class FormatConversions {
	private static char[] array = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();
	private static String numStr = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * byte数组转换成16进制字符串
	 * 
	 * @param src
	 * @return
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder();
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	// 10进制转为其他进制，除留取余，逆序排列
	public static String _10_to_N(long number, int N) {
		Long rest = number;
		Stack<Character> stack = new Stack<Character>();
		StringBuilder result = new StringBuilder(0);
		while (rest != 0) {
			stack.add(array[new Long((rest % N)).intValue()]);
			rest = rest / N;
		}
		for (; !stack.isEmpty();) {
			result.append(stack.pop());
		}
		return result.length() == 0 ? "0" : result.toString();

	}

	// 其他进制转为10进制，按权展开
	public static long N_to_10(String number, int N) {
		char ch[] = number.toCharArray();
		int len = ch.length;
		long result = 0;
		if (N == 10) {
			return Long.parseLong(number);
		}
		long base = 1;
		for (int i = len - 1; i >= 0; i--) {
			int index = numStr.indexOf(ch[i]);
			result += index * base;
			base *= N;
		}

		return result;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Scanner in = new Scanner(System.in);
		while (in.hasNext()) {
			int src = in.nextInt();
			int aim = in.nextInt();
			String intStr = in.next();

			Long tmp = N_to_10(intStr, src);
			String tmp2 = _10_to_N(tmp, aim);

			String newStr = tmp2.replaceFirst("^0*", "");
			System.out.println(newStr);

		}
	}
}