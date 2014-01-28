package org.mam.eti.pg.gda.pl.marblegame;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.mam.eti.pg.gda.pl.marblegame.utils.MathUtilities;

import android.util.Log;

public class BubblesGrid {
	private Ball[][] bubbles;
	public BubblesGrid(int x, int y) {
		this.bubbles = new Ball[x][y];
	}
	public void setBubbles(int x, int y, Ball value) {
		bubbles[x][y] = value;
	}
	public void setBubbleNull(int x, int y) {
		bubbles[x][y] = null;
	}
	public Ball[][] getBubbles() {
		return bubbles;
	}
	public Ball getBubble(int x, int y) {
		return bubbles[x][y];
	}
	public boolean isBubblesNull(int x, int y) {
		return bubbles[x][y] == null ? true : false;
	}
	
	public void detachedRandBalls() {
		int randColor = MathUtilities.getRandInt(6);
		Log.e("detach", "Wywolanie programu!!!");
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (bubbles[i][j] != null
						&& (bubbles[i][j].getBallColor() == randColor || bubbles[i][j]
								.getBallColor() == 8)) {
					bubbles[i][j].detachSelf();
					Log.e("Detach", "wywołano detach na x i y : " + i + " " + j);
					bubbles[i][j] = null;
				}
			}
		}
	}
	
	public boolean isBubblesFull() {
		boolean isBubblesFull = false;
		int howManyEmptySpaces = 0;
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if (bubbles[i][j] == null)
					howManyEmptySpaces++;
			}
		}
		if (howManyEmptySpaces <= 3)
			isBubblesFull = true;
		return isBubblesFull;
	}
	
	
	public boolean checkPattern(int ballColor, Achievement stats) {

		
		// wyszukiwanie w kolumnach
		boolean czyZnaleziono = false;
		for (int h = 0; h < 8; h++) {
			for (int i = 0; i < 8 - 5 + 1; i++) {
				int j = 1;
				if (bubbles[h][i + j - 1] != null) {
					while (j <= 5
							&& bubbles[h][i + j - 1] != null
							&& (bubbles[h][i + j - 1].getBallColor() == ballColor || bubbles[h][i
									+ j - 1].getBallColor() == 7)) {
						j++;
						if (j == 6) {
							Log.w("pattern",
									"Hurra! Znaleziono 5 kulek - wiersz: " + h
											+ " kolumna: " + i + "\n");
							
							for (int k = 0; k < 5; k++) {
								bubbles[h][i].setBlendFunction(
										GL10.GL_SRC_ALPHA,
										GL10.GL_ONE_MINUS_SRC_ALPHA);
								IEntityModifier iem = new AlphaModifier(2f, 0,
										255);
								iem.setAutoUnregisterWhenFinished(true);
								bubbles[h][i].registerEntityModifier(iem);
								bubbles[h][i].detachSelf();
								bubbles[h][i] = null;
								i++;
							}
							czyZnaleziono = true;
							stats.setScore(50);
							stats.setComboAchievementCounter(1);
							
							return true;
						}

					}
				}
			}
		}
		// wyszukiwanie w wierszach
		for (int h = 0; h < 8; h++) {
			for (int i = 0; i < 8 - 5 + 1; i++) {
				int j = 1;
				if (bubbles[i + j - 1][h] != null) {
					while (j <= 5
							&& bubbles[i + j - 1][h] != null
							&& (bubbles[i + j - 1][h].getBallColor() == ballColor || bubbles[i
									+ j - 1][h].getBallColor() == 7)) {
						j++;
						if (j == 6) {
							Log.w("pattern",
									"Hurra! Znaleziono 5 kulek - wiersz: " + h
											+ " kolumna: " + i + "\n");
							for (int k = 0; k < 5; k++) {
								bubbles[i][h].setBlendFunction(
										GL10.GL_SRC_ALPHA,
										GL10.GL_ONE_MINUS_SRC_ALPHA);
								IEntityModifier iem = new AlphaModifier(2f, 0,
										255);
								iem.setAutoUnregisterWhenFinished(true);
								bubbles[i][h].registerEntityModifier(iem);
								bubbles[i][h].detachSelf();
								bubbles[i][h] = null;
								i++;
							}
							czyZnaleziono = true;
							stats.setScore(50);
							stats.setComboAchievementCounter(1);
							return true;
						}

					}
				}
			}
		}
		// wyszukiwanie po przekatnej w lewo
		for (int h = 0; h < 8; h++) {
			for (int i = 0; i < 8 - 5 + 1; i++) {
				int j = 1;
				if (bubbles[i + j - 1][h + j - 1] != null) {
					while (j <= 5
							&& bubbles[i + j - 1][h + j - 1] != null
							&& (bubbles[i + j - 1][h + j - 1].getBallColor() == ballColor || bubbles[i
									+ j - 1][h + j - 1].getBallColor() == 7)) {
						j++;
						if (j == 6) {
							for (int k = 0; k < 5; k++) {
								bubbles[i][h].detachSelf();
								bubbles[i][h] = null;
								i++;
								h++;
							}
							czyZnaleziono = true;
							stats.setScore(50);
							stats.setComboAchievementCounter(1);
							return true;
						}

					}
				}
			}
		}
		// wyszukiwanie po przekatnej w prawo
		for (int h = 0; h < 8 - 5 + 1; h++) {
			for (int i = 0; i < 8; i++) {
				int j = 1;
				int w = -1;
				if (bubbles[i + w + 1][h + j - 1] != null) {
					while (w >= -5
							&& (i + w - 1) >= 0
							&& bubbles[i + w + 1][h + j - 1] != null
							&& (bubbles[i + w + 1][h + j - 1].getBallColor() == ballColor || bubbles[i
									+ w + 1][h + j - 1].getBallColor() == 7)) {
						j++;
						w--;
						if (w == -6) {
							
							for (int k = 0; k < 5; k++) {
								bubbles[i][h].detachSelf();
								bubbles[i][h] = null;
								i--;
								h++;
							}
							czyZnaleziono = true;
							stats.setScore(50);
							stats.setComboAchievementCounter(1);
							return true;
						}
					}
				}
			}
		}
		stats.resetCombo();
		return false;
	}
	
	public void resetBubbles() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (bubbles[i][j] != null) {
					bubbles[i][j].detachSelf();
					bubbles[i][j] = null;
				}
			}
		}
	}
	
}
