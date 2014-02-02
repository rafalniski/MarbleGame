package com.marbles.entity;

import java.util.Currency;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.marbles.activity.MarbleGameActivity;
import com.marbles.utils.MathUtilities;


import android.graphics.Point;
import android.util.Log;

public class BubblesGrid {
	private Ball[][] bubbles;
	public static final int BALLS_AT_THE_BEGGINING = 20;
	public static final int HOW_MANY_NEW_BALLS = 3;
	/* NUMBER OF ROWS AND COLUMNS SHOULD ALWAYS BE EQUAL AND +1 MORE THAN ACTUAL GRID */
	public static final int GRID_ROWS = 9;
	public static final int GRID_COLUMNS = 9;
	private int currentBallColor;
	private Ball[] nextBalls, nextBallsShow;

	public BubblesGrid() {
		this.bubbles = new Ball[GRID_COLUMNS][GRID_ROWS];
		this.nextBalls 		= new Ball[BubblesGrid.HOW_MANY_NEW_BALLS];
		this.nextBallsShow 	= new Ball[BubblesGrid.HOW_MANY_NEW_BALLS];
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
	public ITextureRegion getRandBall(Ball[][] bubbles) {
		switch (MathUtilities.getRandInt(9)) {
		case 0:
			currentBallColor = 0;
			return TextureRegion.mBallGreen;
		case 1:
			currentBallColor = 1;
			return TextureRegion.mBallGrey;
		case 2:
			currentBallColor = 2;
			return TextureRegion.mBallBlue;
		case 3:
			currentBallColor = 3;
			return TextureRegion.mBallYellow;
		case 4:
			currentBallColor = 4;
			return TextureRegion.mBallPurple;
		case 5:
			currentBallColor = 5;
			return TextureRegion.mBallRed;
		case 6:
			if (MathUtilities.getRandInt(3) == 1) {
				currentBallColor = 6;
				return TextureRegion.mBallX;
			} else {
				currentBallColor = 3;
				return TextureRegion.mBallYellow;
			}
		case 7:
			currentBallColor = 7;
			return TextureRegion.mBallAll;
		case 8:
			if (MathUtilities.getRandInt(3) == 1) {
				currentBallColor = 8;
				return TextureRegion.mBallRand;
			} else {
				currentBallColor = 4;
				return TextureRegion.mBallPurple;
			}
		default:
			currentBallColor = 5;
			return TextureRegion.mBallRed;
		}
	}
	public void generateBallsAtTheBeggining(Scene scene, MarbleGameActivity activity) {
		int generatedBalls = 0;
		boolean gotRandColor = false;
		while (generatedBalls < BubblesGrid.BALLS_AT_THE_BEGGINING) {
			int x = MathUtilities.getRandInt(8);
			int y = MathUtilities.getRandInt(8);
			if (bubbles[x][y] == null) {
				ITextureRegion colorRegion = getRandBall(bubbles);
				boolean colorx = currentBallColor == 6 || currentBallColor  == 8 ? true
						: false;
				if (currentBallColor  == 8) {
					gotRandColor = true;
				}
				Ball newBall = new Ball(colorx, currentBallColor , x, y, 15
						+ (30 + Ball.BALL_WIDTH) * x, 15 + (30 + Ball.BALL_HEIGHT) * y,
						colorRegion, activity.getVertexBufferObjectManager());
				bubbles[x][y] = newBall;
				scene.attachChild(bubbles[x][y]);
				generatedBalls++;
			}
		}
		if (gotRandColor) {
			detachedRandBalls();
			gotRandColor = false;
		}
	}
	
	public void generateNextBalls(Scene scene, MarbleGameActivity activity) {
		int generatedBalls = 0;
		while (generatedBalls < BubblesGrid.HOW_MANY_NEW_BALLS) {
			ITextureRegion colorRegion = getRandBall(bubbles);
			boolean colorx = currentBallColor  == 6 ? true : false;
			if (colorx == true) {
				Log.e("ss", "Jestem sztoska X.");
			}
			// always detach current, before generating new one so it 
			// won't get at the top of the "stack"
			if(this.nextBallsShow[generatedBalls] != null)
				this.nextBallsShow[generatedBalls].detachSelf();
			this.nextBallsShow[generatedBalls] = new Ball(colorx,
					currentBallColor , 0, 0, 850, 500 + (100 * generatedBalls),
					colorRegion, activity.getVertexBufferObjectManager());
			scene.attachChild(this.nextBallsShow[generatedBalls++]);
		}

	}
	
	public void addGeneratedBalls(Scene scene, MarbleGameActivity activity, Grid gameGrid, Achievement stats) {
		int generatedBalls = 0;
		while (generatedBalls < BubblesGrid.HOW_MANY_NEW_BALLS) {
			int x = MathUtilities.getRandInt(BubblesGrid.GRID_COLUMNS-1);
			int y = MathUtilities.getRandInt(BubblesGrid.GRID_ROWS-1);
			if (bubbles[x][y] == null) {
				int colorIndex = this.nextBallsShow[generatedBalls]
						.getBallColor();
				ITextureRegion color = TextureRegion.getColor(colorIndex);
				this.nextBalls[generatedBalls] = new Ball(
						colorIndex == 6 ? true : false, colorIndex, 0, 0, 850,
						420 + (100 * generatedBalls), color,
						activity.getVertexBufferObjectManager());
				this.nextBalls[generatedBalls].setX(x);
				this.nextBalls[generatedBalls].setY(y);
				bubbles[x][y] = this.nextBalls[generatedBalls];
				Point gridXY = gameGrid.getGridCoordinates(x, y, Ball.BALL_WIDTH, Ball.BALL_HEIGHT);
				bubbles[x][y].setPosition(gridXY.x + 15, gridXY.y + 15);
				scene.attachChild(bubbles[x][y]);
				generatedBalls++;
				boolean checkResult = checkPattern(
						bubbles[x][y].getBallColor(), stats);
				if(checkResult == true) {
					MusicHelper.scoreMusic.play();
					stats.setComboAchievementCounter(1);
				}
				TextureRegion.textStroke.setText("Score\n" + stats.getScore());
				if (colorIndex == 8)
					detachedRandBalls();

			}
		}
	}
	public void detachNextBalls() {
		for (int i = 0; i < HOW_MANY_NEW_BALLS; i++) {
			if(this.nextBallsShow[i] != null) {
				this.nextBallsShow[i].detachSelf();
				this.nextBallsShow[i] = null;
				TextureRegion.textStrokeNextBalls.setText("");
			}
		}
	}
	
	public void detachedRandBalls() {
		int randColor = MathUtilities.getRandInt(6);
		Log.e("detach", "Wywolanie programu!!!");
		for (int i = 0; i < GRID_COLUMNS; i++) {
			for (int j = 0; j < GRID_ROWS; j++) {
				if (bubbles[i][j] != null
						&& (bubbles[i][j].getBallColor() == randColor || bubbles[i][j]
								.getBallColor() == 8)) {
					bubbles[i][j].detachSelf();
					Log.e("Detach", "wywo??ano detach na x i y : " + i + " " + j);
					bubbles[i][j] = null;
				}
			}
		}
	}
	
	public boolean isBubblesFull() {
		boolean isBubblesFull = false;
		int howManyEmptySpaces = 0;
		for (int i = 0; i < GRID_COLUMNS-1; i++) {
			for (int j = 0; j < GRID_ROWS-1; j++) {
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
		for (int h = 0; h < GRID_COLUMNS; h++) {
			for (int i = 0; i < GRID_ROWS - 5 + 1; i++) {
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
							stats.setScore((stats.getScore()+ 50));							
							return true;
						}

					}
				}
			}
		}
		// wyszukiwanie w wierszach
		for (int h = 0; h < GRID_COLUMNS; h++) {
			for (int i = 0; i < GRID_ROWS - 5 + 1; i++) {
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
							stats.setScore((stats.getScore()+ 50));
							return true;
						}

					}
				}
			}
		}
		// wyszukiwanie po przekatnej w lewo
		for (int h = 0; h < GRID_COLUMNS-1; h++) {
			for (int i = 0; i < GRID_ROWS-1 - 5 + 1; i++) {
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
							stats.setScore((stats.getScore()+ 50));
							return true;
						}

					}
				}
			}
		}
		// wyszukiwanie po przekatnej w prawo
		for (int h = 0; h < GRID_COLUMNS - 5 + 1; h++) {
			for (int i = 0; i < GRID_ROWS; i++) {
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
							stats.setScore((stats.getScore()+ 50));
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public void resetBubbles() {
		for (int i = 0; i < GRID_COLUMNS ; i++) {
			for (int j = 0; j < GRID_ROWS; j++) {
				if (bubbles[i][j] != null) {
					bubbles[i][j].detachSelf();
					bubbles[i][j] = null;
				}
			}
		}
	}
	public int getCurrentBallColor() {
		return currentBallColor;
	}
	public void setCurrentBallColor(int currentBallColor) {
		this.currentBallColor = currentBallColor;
	}
	
}
