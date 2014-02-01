package com.marbles.utils;

import java.util.Random;

public class MathUtilities {
	public static int getRandInt(int num) {
		Random generator = new Random();
		return generator.nextInt(num);
	}
}
