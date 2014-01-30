package org.mam.eti.pg.gda.pl.marblegame;

import java.security.acl.LastOwnerException;
import java.util.LinkedList;
import java.util.List;

import org.mam.eti.pg.gda.pl.marblegame.PathFinder.Node;

import android.util.Log;

public class NewPathFinder {

	private int[][] GRID = null;

	public NewPathFinder(int[][] gridMap) {
		GRID = gridMap;
	}

	public class Point {
		int x;
		int y;
		int counter = 0;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public Point(int x, int y, int counter) {
			this.x = x;
			this.y = y;
			this.counter = counter;
		}

		public boolean isWall() {
			return GRID[y][x] == 1;
		}

		public boolean equals(Point anotherPoint) {
			return this.x == anotherPoint.x && this.y == anotherPoint.y;
		}

		public boolean isMoreImportantThan(Point point) {
			return this.counter < point.counter;
		}

		public String toString() {
			return "(" + x + "," + y + "," + counter + ")";
		}

		public Point getEastPoint(int counter) {
			int lengthX = GRID[0].length;
			if (x + 1 >= lengthX) {
				return null;
			} else
				return new Point(x + 1, y, counter);
		}

		public Point getWestPoint(int counter) {
			if (x - 1 < 0) {
				return null;
			} else
				return new Point(x - 1, y, counter);
		}

		public Point getSouthPoint(int counter) {
			int lengthY = GRID.length;
			if (y + 1 >= lengthY) {
				return null;
			} else
				return new Point(x, y + 1, counter);
		}

		public Point getNorthPoint(int counter) {
			if (y - 1 < 0) {
				return null;
			} else
				return new Point(x, y - 1, counter);
		}
	}

	private static final String TAG = "NewPathFinder";

	public LinkedList<Node> solve(Point startPoint, Point endPoint) {
		Log.d(TAG, "startPoint: "+startPoint.toString()+" endPoint: "+endPoint.toString());
		LinkedList<Point> queue = new LinkedList<Point>();
		queue.push(startPoint);
		int counter = 0;
		int lastQueueSize = 0;
		boolean finished = false;
		boolean successfull = false;
		for (;;) {
			counter++;
			finished = addPoints(queue,counter,endPoint);
			cleanUpQueue(queue);
			logQueue(queue, counter);
			
			if (finished) {
				successfull = true;
				Log.d(TAG, "finished successfully");
				break;
			} else if (lastQueueSize == queue.size()) {
				successfull = false;
				Log.d(TAG, "finished unsuccessfully");
				break;
			} else {
				lastQueueSize = queue.size();
			}
		}
		if (successfull) {
			LinkedList<Node> nodes = getNodePath(queue);
			return nodes;

		} else {
			return null;
		}
	}

	private LinkedList<Node> getNodePath(LinkedList<Point> queue){
		LinkedList<Point> path = new LinkedList<Point>();
		
		Point startPoint=queue.getFirst();
		Point endPoint=queue.getLast();
		
		path.add(endPoint);
		Point actualPoint = endPoint;
		Log.d(TAG, "adding: "+actualPoint.toString());
		for (;;) {
			boolean found = false;
			Point[] points = new Point[4];
			points[0] = actualPoint.getNorthPoint(0);
			points[1] = actualPoint.getSouthPoint(0);
			points[2] = actualPoint.getEastPoint(0);
			points[3] = actualPoint.getWestPoint(0);
			int bestCounter = Integer.MAX_VALUE;
			Point bestPoint = null;
			
			for (int i = 0; i < 4; i++) {
				int iteratedCounter = getCounterForPoint(queue, points[i]);
				if (iteratedCounter < bestCounter && iteratedCounter != -1) {
					bestCounter = iteratedCounter;
					bestPoint = points[i];
				}
			}
			actualPoint = bestPoint;
			path.add(actualPoint);
			Log.d(TAG, "adding: "+bestPoint);
			if(actualPoint.equals(startPoint)){
				Log.d(TAG, "found start point");
				break;
			}
		}
		LinkedList<Node> nodes = new LinkedList<Node>();
		for(int i=path.size()-1;i>=0;i--){
			Point point = path.get(i);
			Node node = new Node(point.x-1, point.y-1);
			Log.d(TAG, "node: "+node.toString());
			nodes.add(node);
		}
		Log.d(TAG, "path created OK");
		return nodes;
	}
	
	private boolean addPoints(LinkedList<Point> queue, int counter, Point endPoint){
		boolean finished = false;
		int queueSize = queue.size();
		for (int i = 0; i < queueSize; i++) {
			Point concernedPoint = queue.get(i);
			Point[] points = new Point[4];
			points[0] = concernedPoint.getNorthPoint(counter);
			points[1] = concernedPoint.getSouthPoint(counter);
			points[2] = concernedPoint.getEastPoint(counter);
			points[3] = concernedPoint.getWestPoint(counter);
			for (int j = 0; j < 4; j++) {
				if (shouldPointBeAdded(points[j], queue)) {
					queue.add(points[j]);
					if (points[j].equals(endPoint)) {
						finished = true;
						break;
					}
				}
			}

			if (finished) {
				break;
			}
		}
		return finished;
	}
	
	private void logQueue(LinkedList<Point> queue, int counter){
		String points = "After iteration " + counter + " ";
		for (int i = 0; i < queue.size(); i++) {
			Point iteratedPoint = queue.get(i);
			points += iteratedPoint.toString();
		}
		Log.i(TAG, points);
	}
	
	private static void cleanUpQueue(LinkedList<Point> queue) {
		for (int i = 0; i < queue.size(); i++) {
			Point iteratedPoint = queue.get(i);
			boolean removed = removeIfNecessary(queue, iteratedPoint);
			if (removed) {
				i--;
			}
		}
	}

	private static boolean pointExistsInQueue(LinkedList<Point> queue, Point point) {
		if (point == null) {
			return false;
		}
		for (int i = 0; i < queue.size(); i++) {
			Point iteratedPoint = queue.get(i);
			if (iteratedPoint.equals(point)) {
				return true;
			}
		}
		return false;
	}
	
	private static int getCounterForPoint(LinkedList<Point> queue, Point point) {
		if (point == null) {
			return -1;
		}
		for (int i = 0; i < queue.size(); i++) {
			Point iteratedPoint = queue.get(i);
			if (iteratedPoint.equals(point)) {
				return iteratedPoint.counter;
			}
		}
		return -1;
	}

	private static boolean removeIfNecessary(LinkedList<Point> queue,
			Point point) {
		for (int i = 0; i < queue.size(); i++) {
			Point iteratedPoint = queue.get(i);
			if (iteratedPoint != point && iteratedPoint.equals(point)
					&& iteratedPoint.isMoreImportantThan(point)) {
				queue.removeFirstOccurrence(point);
				return true;
			}
		}
		return false;
	}

	private static boolean doesPointExistInQueue(LinkedList<Point> queue,Point point) {
		for (int i = 0; i < queue.size(); i++) {
			Point iteratedPoint = queue.get(i);
			if (iteratedPoint != point && iteratedPoint.equals(point)
					&& iteratedPoint.isMoreImportantThan(point)) {
				return true;
			}
		}
		return false;
	}
	
	
	private static boolean shouldPointBeAdded(Point point,
			LinkedList<Point> queue) {
		if (point == null) {
			return false;
		} else if (point.isWall()) {
			return false;
		} else
			return !doesPointExistInQueue(queue, point);
	}

}
