package com.marbles.utils;

import java.util.LinkedList;
import java.util.List;

public class PathFinder
{
	private int[][] map;
	private Node endPoint;
	public static class Node{
		public int x;
	    public int y;
	    
	    Node(int x, int y) {
	        this.x = x; 
	        this.y = y;
	    }
	    
	    public String toString(){
	    	return "(" + x + ", " + y + ") ";
	    } 
	}
	
	public PathFinder(int[][] map, Node endPoint){
        this.map = map;
        this.endPoint = endPoint;
	}
	
	protected boolean isGoal(Node node){
		return (node.x == endPoint.x) && (node.y == endPoint.y);
	}
	
	protected Double g(Node from, Node to){
	
	    if(from.x == to.x && from.y == to.y)
	                    return 0.0;
	
	    if(map[to.y][to.x] == 1)
	                    return 1.0;
	
	    return Double.MAX_VALUE;
	}
	
	protected Double h(Node from, Node to){
    	/* Use the Manhattan distance heuristic.  */
        return new Double(Math.abs(map[0].length - 1 - to.x) + Math.abs(map.length - 1 - to.y));
	}
	
	protected List<Node> generateSuccessors(Node node){
	    List<Node> ret = new LinkedList<Node>();
	    int x = node.x;
	    int y = node.y;
	    if(y < map.length - 1 && map[y+1][x] == 1)
	                    ret.add(new Node(x, y+1));
	
	    if(x < map[0].length - 1 && map[y][x+1] == 1)
	                    ret.add(new Node(x+1, y));
	
	    return ret;
	}
	
	public static void main(String [] args){
	    int [][] map = new int[][]{
	                    {1, 1, 1, 1, 1, 1, 1, 1, 1},
	                    {0, 0, 1, 0, 0, 0, 0, 0, 1},
	                    {1, 1, 1, 1, 0, 1, 1, 0, 1},
	                    {1, 1, 1, 1, 1, 1, 1, 0, 0},
	                    {1, 0, 0, 0, 0, 1, 0, 0, 0},
	                    {1, 1, 1, 1, 0, 1, 1, 1, 1},
	                    {1, 1, 1, 1, 0, 1, 0, 0, 1},
	                    {1, 1, 1, 1, 0, 1, 0, 0, 1},
	                    {1, 1, 1, 1, 0, 1, 0, 0, 1},
	                    {1, 1, 1, 1, 0, 1, 0, 0, 0},
	                    {1, 1, 1, 1, 0, 1, 1, 1, 1},
	    };
	    /*
        PathFinder pf = new PathFinder(map);
	
            System.out.println("Find a path from the top left corner to the right bottom one.");
	
	        for(int i = 0; i < map.length; i++){
	            for(int j = 0; j < map[0].length; j++)
                System.out.print(map[i][j] + " ");
	            System.out.println();
	        }
	
	        long begin = System.currentTimeMillis();
	
	        List<Node> nodes = pf.compute(new PathFinder.Node(0, 0));
	        
	        long end = System.currentTimeMillis();
	
	
	        System.out.println("Time = " + (end - begin) + " ms" );
	        System.out.println("Expanded = " + pf.getExpandedCounter());
	        System.out.println("Cost = " + pf.getCost());
	        
	        if(nodes == null)
	                        System.out.println("No path");
	        else{
	                        System.out.print("Path = ");
	                                for(Node n : nodes)
	                                                System.out.print(n);
	                                System.out.println();
	                }*/
	}

}
