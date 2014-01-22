package org.mam.eti.pg.gda.pl.marblegame;

import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Point;

public class MazeSolver {

    final static int TRIED = 2;
    final static int PATH = 3;
    static int move = 0;

    // @formatter:off
    private static int[][] GRID = { 
        { 1, 1, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1 },
        { 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 1 },
        { 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0 },
        { 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1 },
        { 1, 0, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 1 },
        { 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1 },
        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } 
    };
    // @formatter:off

    private int[][] grid;
    private int height;
    private int width;
    private GridPoint start;
    private GridPoint end;

    private int[][] map;
    private ArrayList<GridPoint> pointsy = new ArrayList<GridPoint>(300);
    
    public MazeSolver(int[][] grid, GridPoint startPoint, GridPoint endPoint) {
        this.grid = grid;
        this.height = grid.length;
        this.width = grid[0].length;
        this.map = new int[height][width];
        this.start = startPoint;
        this.end = endPoint;
    }
    public int[][] getMap() {
    	return map;
    }
    public boolean solve() {
        return traverse(end.x,end.y);
    }

    private boolean traverse(int i, int j) {
        if (!isValid(i,j)) {
            return false;
        }
        
        if ( isEnd(i, j) ) {
            map[i][j] = PATH;
            GridPoint punkt = new GridPoint(i, j);
            pointsy.add(punkt);
            move++;
            return true;
        } else {
            map[i][j] = TRIED;
        }

        // west
        if (traverse(i - 1, j)) {
            map[i-1][j] = PATH;
            GridPoint punkt = new GridPoint(i-1, j);
            pointsy.add(punkt);
            move++;
            return true;
        }
        // south
        if (traverse(i, j + 1)) {
            map[i][j + 1] = PATH;
            GridPoint punkt = new GridPoint(i, j+1);
            pointsy.add(punkt);
            move++;
            return true;
        }
        // east
        if (traverse(i + 1, j)) {
            map[i + 1][j] = PATH;
            GridPoint punkt = new GridPoint(i+1, j);
            pointsy.add(punkt);
            move++;
            return true;
        }
        // west
        if (traverse(i, j - 1)) {
            map[i][j - 1] = PATH;
            GridPoint punkt = new GridPoint(i, j-1);
            pointsy.add(punkt);
            
            move++;
            return true;
        }

        return false;
    }

    private boolean isEnd(int i, int j) {
        return i == start.x && j == start.y;
    }
    public ArrayList<GridPoint> getPointsy() {
    	return pointsy;
    }
    private boolean isValid(int i, int j) {
        if (inRange(i, j) && isOpen(i, j) && !isTried(i, j)) {
            return true;
        }

        return false;
    }

    private boolean isOpen(int i, int j) {
        return grid[i][j] == 1;
    }

    private boolean isTried(int i, int j) {
        return map[i][j] == TRIED;
    }

    private boolean inRange(int i, int j) {
        return inHeight(i) && inWidth(j);
    }

    private boolean inHeight(int i) {
        return i >= 0 && i < height;
    }

    private boolean inWidth(int j) {
        return j >= 0 && j < width;
    }

    public String toString() {
        String s = "";
        for (int[] row : map) {
            s += Arrays.toString(row) + "\n";
        }

        return s;
    }

}
