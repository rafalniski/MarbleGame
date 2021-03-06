package com.marbles.entity;


import android.graphics.Point;
import android.util.Log;

public class Grid {
	
	public Point getGridCoordinates(int x, int y, int ballWidth, int ballHeigth) {

		int gridX = (30 + ballWidth) * x;
		int gridY = (30 + ballHeigth) * y;
		Point point = new Point(gridX, gridY);
		return point;
	}
	public Point getBubbleCoordinates(float x, float y) {
		int xBubble = (int) x / 100;
		int yBubble = (int) (y + 4) / 100;
		Log.v("klik", "Punkt klikniety x: " + (int) x / 100 + " y: "
				+ (int) ((y + 4) / 100));
		Point point = new Point(xBubble, yBubble);
		return point;
	}
	public int[][] getPathMap(BubblesGrid bubbles) {
		int map[][] = new int[BubblesGrid.GRID_COLUMNS][BubblesGrid.GRID_ROWS];
		for (int i = 0; i < BubblesGrid.GRID_COLUMNS; i++) {
			map[0][i] = 1;
			map[BubblesGrid.GRID_ROWS-1][i] = 1;
			map[i][0] = 1;
			map[i][BubblesGrid.GRID_ROWS-1] = 1;
		}
		for (int i = 0; i < BubblesGrid.GRID_COLUMNS-1; i++) {
			for (int j = 0; j < BubblesGrid.GRID_ROWS-1; j++) {
				if (bubbles.isBubblesNull(i, j))
					map[j + 1][i + 1] = 0;
				else
					map[j + 1][i + 1] = 1;
			}
		}
		return map;
	}
	
	
}
