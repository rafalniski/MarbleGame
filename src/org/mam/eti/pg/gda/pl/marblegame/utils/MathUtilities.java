package org.mam.eti.pg.gda.pl.marblegame.utils;

import java.util.Random;

public class MathUtilities {
	public static int getRandInt(int num) {
		Random generator = new Random();
		return generator.nextInt(num);
	}
}
