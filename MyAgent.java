import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Objects;

import za.ac.wits.snake.DevelopmentAgent;

public class MyAgent extends DevelopmentAgent {
	 private int snakeHeadX,snakeHeadY,foodX,foodY,width,height,snakeSize;
	 private int[] snakeTail;
	 private static final int[][] DIRECTIONS = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
	 private int[][] grid;

    public static void main(String args[]) {
        MyAgent agent = new MyAgent();
        MyAgent.start(agent, args);
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String initString = br.readLine();
            String[] temp;
            if(initString.contains(",")) {
            	temp = initString.split(",");
            }else {
            	temp = initString.split(" ");
            }
           
            int nSnakes = Integer.parseInt(temp[0]);
           
           
            width = Integer.parseInt(temp[1]);
            height = Integer.parseInt(temp[2]);
            
            //get the game mode
            int mode = Integer.parseInt(temp[3]);

            while (true) {
                String line = br.readLine();
                if (line.contains("Game Over")) {
                    break;
                }
                
                //initialise a new grid
                grid = new int[height][width];
                
                //deal with the apple
                placeFoodOnGrid(line); 
               

                // read in obstacles and do something with them!
                int nObstacles = 3;
                for (int obstacle = 0; obstacle < nObstacles; obstacle++) {
                    String obs = br.readLine();
                    /// do something with obs
                    placeObstacleOnGrid(obs);
                }

                // read in zombies and do something with them!
                int nZombies = 3;
                for (int zombie = 0; zombie < nZombies; zombie++) {
                    String zom = br.readLine();
                    /// do something with zom
                    placeZombieOnGrid(zom);
                }
                
              
                int mySnakeNum = Integer.parseInt(br.readLine());
                for (int i = 0; i < nSnakes; i++) {
                    String snakeLine = br.readLine();
                    if (i == mySnakeNum) {
                        //hey! That's me :)
                    	placeMySnakeOnGrid(snakeLine);
                    	continue;
                    }
                    //do stuff with other snakes
                    placeOtherSnakesOnGrid(snakeLine);
                }
                //finished reading, calculate move:
               
                List<int[]> path = bfs(grid,new int[]{snakeHeadX,snakeHeadY}, new int[]{foodX, foodY});
                if(path == null || path.isEmpty() || path.size() < 2) {
                	path = NoPathToFoodHandler();
                }
              
                snakeMakeMove(path);
                
                
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    private void snakeMakeMove(List<int[]> path) {
    	// TODO Auto-generated method stub
    	 
        //checking if the path is empty
    	 if (path == null || path.isEmpty()) {
	        System.out.println(5); // No valid path or path is too short
	        return;
    	  }
        
      //get the next path from the head
        int[] nextStep = path.get(1);
        
        // now check if i should go up,down,left or right
        if(snakeHeadX == nextStep[0]) {
        	if(nextStep[1] > snakeHeadY) {
        		//go down
        		System.out.println(1);
        		
        		return;
        	}else {
        		//go up
        		System.out.println(0);
        		
        		return;
        	}
        }
        
        if(snakeHeadY == nextStep[1]) {
        	if(nextStep[0] > snakeHeadX) {
        		//go right
        		System.out.println(3);
        		
        		return;
        	}else {
        		//go left
        		System.out.println(2);
        		
        		return;
        	}
        }
        //end of checking
    	
    }

	private void placeFoodOnGrid(String line) {
		// TODO Auto-generated method stub
		String[] parts = line.split(" ");
        foodX = Integer.parseInt(parts[0]);
        foodY = Integer.parseInt(parts[1]);
        grid[foodX][foodY] = 3; // food is represented by 3
		
	}

	private void placeOtherSnakesOnGrid(String snakeLine) {
		// TODO Auto-generated method stub
		placeSnakeOnGrid(snakeLine,1); // 1 is for other snakes,along with zombies and obstacles	
	}

	private void placeMySnakeOnGrid(String snakeLine) {
		// TODO Auto-generated method stub
		placeSnakeOnGrid(snakeLine,2); //2 is for my snake in the time being
	}

	private void placeSnakeOnGrid(String snakeLine, int snakeNumber) {
		// TODO Auto-generated method stub
		String[] parts = snakeLine.split(" ");
        
        //if the snake is dead then just return
        if(parts[0].equals("dead")) {
        	return;
        }
        if(snakeNumber == 2) {
        	snakeSize = Integer.parseInt(parts[1]);
        }
        
        //start placing the snakes on the grid
        for (int k = 3; k < parts.length - 1; k++) {
        	 String[] coordOne = parts[k].split(","); //firstCoord
             String[] coordTwo = parts[k + 1].split(",");
             
             //checking if it my snake and k is 3 in order to get the head coordinates and tail coordinates
             if(k == 3 && snakeNumber == 2) {
            	 snakeHeadX = Integer.parseInt(coordOne[0]);
            	 snakeHeadY = Integer.parseInt(coordOne[1]);
            	 
            	 //getting the tail coordinates
            	 String[] tail = parts[parts.length - 1].split(",");
            	 snakeTail = new int[] {Integer.parseInt(tail[0]),Integer.parseInt(tail[1])};
             }
             
             //adding a buffer zone to other snakes head
             if( k == 3 && snakeNumber != 2) {
            	 addBufferToOtherSnakesHead(Integer.parseInt(coordOne[0]),Integer.parseInt(coordOne[1]));
             }
             
             //place the coordinates
             placingOnGridHelper(coordOne,coordTwo,snakeNumber);
               
        }
		
	}

	private void placeZombieOnGrid(String zombieLine) {
		// TODO Auto-generated method stub
		String[] parts = zombieLine.split(" ");
		
		//start with placing of the zombies on the grid
		for(int i = 0; i < parts.length - 1 ; i++) {
			String[] coordOne = parts[i].split(","); //firstCoord
            String[] coordTwo = parts[i + 1].split(",");
            
            //place the coordinates
            
            placingOnGridHelper(coordOne,coordTwo,4);
            
            //create a buffer zome around the zombieheads to make sure theres always atleast one block between the zombie head and my head
            if (i == 0) {
            	addBufferZoneToZombieHead(Integer.parseInt(coordOne[0]),Integer.parseInt(coordOne[1]));
            }
			
		}
		
	}

	private void placeObstacleOnGrid(String obstacleLine) {
		// TODO Auto-generated method stub
		String[] parts = obstacleLine.split(" ");
        
        // Retrieve the first element
        String firstElement = parts[0];
        
        // Retrieve the last element
        String lastElement = parts[parts.length - 1];
        
        // Handling obstacles
        String[] coordFirst = firstElement.split(",");
        String[] coordLast = lastElement.split(",");
        
        //place the coordinates
        placingOnGridHelper(coordFirst,coordLast,1);          
	}
	
	private void placingOnGridHelper(String[] coordOne,String[] coordTwo,int uniqueNumber) {
		// TODO Auto-generated method stub
		
		//getting the two coordinates
        int coordOneX = Integer.parseInt(coordOne[0]);
        int coordOneY = Integer.parseInt(coordOne[1]);
        
        int coordTwoX = Integer.parseInt(coordTwo[0]);
        int coordTwoY = Integer.parseInt(coordTwo[1]);
        
      //checking if we having intermediate coordinates or not
        if(coordOneX == coordTwoX) {
       	 //if the x values are the same then we have intermediates vertically
       	 
       	 //starting point of our loop and our ending point
       	 int minimumY = Math.min(coordOneY, coordTwoY);
            int maximumY = Math.max(coordOneY, coordTwoY);
            
            //Now start placing all
            for (int j = minimumY; j <= maximumY; j++) {
        		grid[coordOneX][j] = uniqueNumber;
        	}
       	 
        }else if(coordOneY == coordTwoY) {
       	 //if the y values are the same then we have intermediates horizontally
       	 
       	 //starting point of our loop and our ending point
       	 int minimumX = Math.min(coordOneX, coordTwoX);
            int maximumX = Math.max(coordOneX, coordTwoX);
            
          //Now start placing all
            for (int j = minimumX; j <= maximumX; j++) {
        		grid[j][coordOneY] = uniqueNumber;
        	}
       	 
        }else {
       	 //we do not have intermediates therefore just place the two coordinates
       	 grid[coordOneX][coordOneY] = uniqueNumber;
       	 grid[coordTwoX][coordTwoY] = uniqueNumber;
        }
			
	}
	
	 //avoiding head collision with other snakes
    private void addBufferToOtherSnakesHead(int otherSnakeHeadX, int otherSnakeHeadY) {
    	addBufferZoneToHead(otherSnakeHeadX,otherSnakeHeadY); 
    }

	
	//this code add some buffer around the zombie head(extra protection)
	private void addBufferZoneToZombieHead(int zombieHeadX, int zombieHeadY) {
		addBufferZoneToHead(zombieHeadX,zombieHeadY);
	}
	
	private void addBufferZoneToHead(int HeadX,int HeadY) {	
		// List of directions to create buffer zone
	    int[][] bufferDirections = {
	        {0, 1}, {0, -1}, {1, 0}, {-1, 0}, // Adjacent cells
	    };
	    
	    for (int[] direction : bufferDirections) {
	        int newX = HeadX + direction[0];
	        int newY = HeadY + direction[1];
	        
	        // Check bounds and mark as obstacle
	        if (newX >= 0 && newX < height && newY >= 0 && newY < width) {
	            grid[newX][newY] = 1; // Marking buffer zone as obstacle
	        }
	    }
		
	}
	
    //the code below is for bfs
	
	 private static boolean isValidMove(int row, int col, int rows, int cols, int[][] grid) {
	        // Check if within bounds and not an obstacle
	        return row >= 0 && row < rows && col >= 0 && col < cols && grid[row][col] != 1 && grid[row][col] != 2 && grid[row][col] != 4;
	 }
	 

    private static List<int[]> bfs(int[][] grid, int[] start, int[] target) {
        int rows = grid.length;
        int cols = grid[0].length;

        Queue<List<int[]>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        // Start the BFS from the snake's head
        List<int[]> startPath = new ArrayList<>();
        startPath.add(start);
        queue.offer(startPath);
        visited.add(start[0] + "," + start[1]);

        while (!queue.isEmpty()) {
            List<int[]> currentPath = queue.poll();
            int[] currentPos = currentPath.get(currentPath.size() - 1);

            // Check if we've reached the apple
            if (Arrays.equals(currentPos, target)) {
                return currentPath; // Found the shortest path
            }

            // Explore all possible directions
            for (int[] direction : DIRECTIONS) {
                int newRow = currentPos[0] + direction[0];
                int newCol = currentPos[1] + direction[1];

                if (isValidMove(newRow, newCol, rows, cols, grid) && !visited.contains(newRow + "," + newCol)) {
                    // Mark the position as visited
                    visited.add(newRow + "," + newCol);

                    // Add the new position to the path and enqueue it
                    List<int[]> newPath = new ArrayList<>(currentPath);
                    newPath.add(new int[]{newRow, newCol});
                    queue.offer(newPath);
                }
            }
        }

        // If no path is found
        return null;
    }
	    
    //if there's no path to the food,try to find a safe space to move to
    private List<int[]> NoPathToFoodHandler() {
    	List<int[]> safePath = new ArrayList<>();
    	safePath = SnakeFollowsItsTail();
    	
    	//try following the snakes tail
    	if(!(safePath == null || safePath.isEmpty() || safePath.size() < 2)) {
    		return safePath;
    	}
    	
  
		//just make any random move
		return makeRandomMove();
    
        
    }
    
    
    //the snake must follow its tail if there's no path to the food,first handling mechanism
    private List<int[]> SnakeFollowsItsTail(){
    	List<int[]> tracker = new ArrayList<>();
    	
    	//set the tail of my snake on the grid to be an open space
    	grid[snakeTail[0]][snakeTail[1]] = 0;
    	//perform a bfs to the tail
    	tracker = bfs(grid,new int[]{snakeHeadX,snakeHeadY},snakeTail);
    	//set the tail of the snake back to 2 on the grid marking it as part of the snake
    	grid[snakeTail[0]][snakeTail[1]] = 2;
    	//return the path to the tail if it exists
    	return tracker;
    	
    }
    
    // make any random move if there's no path to food and no path to the tail,second mechanism
    private List<int[]> makeRandomMove() {
		List<int[]> safePath = new ArrayList<>();
        
        //add the head of the snake
        safePath.add(new int[] {snakeHeadX,snakeHeadY});

        // Iterate through all possible directions
        for (int[] direction : DIRECTIONS) {
            int newX = snakeHeadX + direction[0];
            int newY = snakeHeadY + direction[1];

            // Check if the move is valid and safe
            if (isValidMove(newX, newY, grid.length, grid[0].length, grid)) {
                // If a safe move is found, add it to the path and return
                safePath.add(new int[]{newX, newY});
                return safePath;  // Exit early since we found a valid move
            }
        }

        // If no safe moves are found, stay in place (rare case)
        safePath.add(new int[]{foodX, foodY});
        return safePath;
    	
    }
    
    //look for the longest path if there's no path to food
    private List<int[]> FindTheLongestSafePath() {
        //perform bfs for largest reachability
    	return bfsLongestPath(grid,snakeHeadX,snakeHeadY);
    	
    }
    
 // BFS method to find the longest path and return the path as a list of coordinates
    public static ArrayList<int[]> bfsLongestPath(int[][] grid, int startX, int startY) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Create a visited array
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        Queue<ArrayList<int[]>> paths = new LinkedList<>();
        
        // Add the starting point to the queue
        queue.add(new int[]{startX, startY});
        ArrayList<int[]> initialPath = new ArrayList<>();
        initialPath.add(new int[]{startX, startY});
        paths.add(initialPath);
        visited[startX][startY] = true;

        ArrayList<int[]> longestPath = new ArrayList<>();

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            ArrayList<int[]> currentPath = paths.poll();
            int x = current[0];
            int y = current[1];

            // Update the longest path if the current path is longer
            if (currentPath.size() > longestPath.size()) {
                longestPath = new ArrayList<>(currentPath);  // Make a copy of the current path
            }

            // Explore neighbors
            for (int[] dir : DIRECTIONS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                if (isValid(grid, visited, newX, newY)) {
                    visited[newX][newY] = true;
                    queue.add(new int[]{newX, newY});

                    // Create a new path list by copying the current path and adding the new position
                    ArrayList<int[]> newPath = new ArrayList<>(currentPath);
                    newPath.add(new int[]{newX, newY});
                    paths.add(newPath);
                }
            }
        }

        return longestPath;
    }
    
    private static boolean isValid(int[][] grid, boolean[][] visited, int row, int col) {
        int rows = grid.length;
        int cols = grid[0].length;
        return row >= 0 && row < rows && col >= 0 && col < cols && grid[row][col] != 1 && grid[row][col] != 2 && grid[row][col] != 4  && !visited[row][col];
    }


    


	 

   


	
}