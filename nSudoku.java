// Expansion of the 2D Sudoku solver to work for N dimensions
// Originally done in collaboration with https://github.com/Horizon489732 and https://github.com/jrsussner18
// Further expanded by me after assignment completion on May 2, 2025

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class nSudoku {
    int dimensions;
    int gridSize;
    int subGridSize;
    
    NDArray org_board;
    NDArray curr_board;

    ArrayList<valRegister> valueRegisters; // stores possible values for each unique dimension pair
    ArrayList<Integer> possVals = new ArrayList<Integer>(); // for the n dimensional index

    BFSNode root;
    BFSNode currNode;
    int[] index;
    int[] traversalIndex;
    int[] subGridIndex = new int[2];

    public nSudoku(int dim, int size, String fileName){
        this.dimensions = dim;
        this.gridSize = size;
        this.subGridSize = (int)Math.sqrt(size);
        this.index = new int[dim];
        this.traversalIndex = new int[dim];

        initBoard(fileName);
        initRegisters();
    }
    
    
    private void initBoard(String fileName){
        // Initialize board based on dimensions and size
        // Set current board as a copy of original board
    	
    	int[] sudoku_dim = new int[dimensions];
    	for (int i = 0; i < sudoku_dim.length; i++) {
    		sudoku_dim[i] = this.gridSize;
    	}
    	this.org_board = new NDArray(sudoku_dim);

        ArrayList<int[]> puzzleVals = new ArrayList<int[]>();
        try{
            File puzzle = new File(fileName);
            Scanner fileScanner = new Scanner(puzzle);
            while(fileScanner.hasNextLine()){

                String line = fileScanner.nextLine();
                String[] vals = line.split(" ");
                int[] index = new int[dimensions + 1];
                for(int i = 0; i < dimensions + 1; i++) index[i] = Integer.parseInt(vals[i]);
                puzzleVals.add(index);
            }
        }
        catch (FileNotFoundException error){
            System.out.println("Bad File Path");
        }

        for(int[] val : puzzleVals){
            int[] index = new int[val.length - 1];
            for(int i = 0; i < index.length; i++) index[i] = val[i];
            this.org_board.set(index, val[val.length-1]);
        }
    	this.curr_board = this.org_board.clone_copy();
    }

    private void initRegisters(){
        // Once the board has been created, create the value registers for all unique pairs of dimensions
        this.valueRegisters = new ArrayList<valRegister>();
        
        int[] pair = new int[2];
        
        for (int i = 0; i < this.dimensions; i++) {
        	
        	for (int j = i + 1; j < this.dimensions; j++) {
        		
        		pair[0] = i;
        		pair[1] = j;
        		
        		valRegister register = new valRegister(pair);
        		
        		this.valueRegisters.add(register);
        		
        	}
        	
        }
    }

    private void getCurrBoard(BFSNode leaf){
        // Get the current board based on the dls tree
        this.curr_board = this.org_board.clone_copy();

        // Using a walker node to safely walk up parent chain without altering the current solution state
        BFSNode walker = leaf;
        while (walker != null && walker.index != null) {
            this.curr_board.set(walker.index, walker.value);
            walker = walker.parent;
        }
        this.currNode = leaf;
    }

    private void getPossVals(){
        // Get possible values based on the intersection of possible values for registers

        if(this.curr_board.get(this.index) != 0){
            this.possVals.clear();
            this.possVals.add(this.curr_board.get(this.index));
        }
        else{
            // Reset possible values
            this.possVals.clear();
            for (int i = 1; i <= gridSize; i++) possVals.add(i);
            updateRegisters();
            removeIndexRows();

            // Remove a value from possVals if it is not present in every register's possible values
            for (int i = 1; i <= gridSize; i++) {
                for (valRegister reg : valueRegisters) {
                    if (!reg.possVals.contains(i)) {
                        possVals.remove(Integer.valueOf(i));
                        break;
                    }
                }
            }
        }
    }

    private void removeIndexRows(){
        for(int i = 0; i < dimensions; i++){
            System.arraycopy(this.index, 0, this.traversalIndex, 0, dimensions);
            for(int j = 0; j < gridSize; j++){
                traversalIndex[i] = j;
                this.possVals.remove(Integer.valueOf(curr_board.get(traversalIndex)));
            }
        }
    }

    private void updateRegisters(){
        // For each value register, create its respective 2D board and get its possible values at the current index2D
        for(valRegister currReg : valueRegisters){
            currReg.possVals.clear();
            for(int i = 1; i <= gridSize; i++) currReg.possVals.add(i);

            System.arraycopy(this.index, 0, this.traversalIndex, 0, dimensions);
            // subgrid values
            subGridIndex[0] = (index[currReg.axisID[0]] / subGridSize) * subGridSize;
            subGridIndex[1] = (index[currReg.axisID[1]] / subGridSize) * subGridSize;
            for (int blockRow = 0; blockRow < subGridSize; blockRow++) {
                for (int blockCol = 0; blockCol < subGridSize; blockCol++) {
                    traversalIndex[currReg.axisID[0]] = subGridIndex[0] + blockRow;
                    traversalIndex[currReg.axisID[1]] = subGridIndex[1] + blockCol;
                    currReg.possVals.remove(Integer.valueOf(curr_board.get(traversalIndex)));
                }
            }
        }
    }

    public void solveDLS(){
        // Does a DLS to find the solution, if one exists
        // Final solution is stored within curr_board

        // Perform recursive DLS
        boolean found = DLS(curr_board.size());

        // Save the solution if found
        if (found) {
            getCurrBoard(currNode);
            try {
                FileWriter w = new FileWriter("sudokuInput/4Dout.txt");
                w.write(curr_board.toString());
                w.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
            }
        } else {
            System.out.println("No solution found within depth limit.");
        }
    }

    // Algorithm for Depth Limited Search
    private boolean DLS(int limit){
        this.root = new BFSNode();
        this.index = computeIndexFromFlat(0);
        getPossVals();

        for(int v: possVals) root.addNewChild(v, index);
        currNode = root.children.getFirst();
        int depth = 1;
        int maxDepth = 0;

        while(depth < limit){
            if(depth > maxDepth) {
                maxDepth = depth;
                System.out.println("Max Depth: " + maxDepth);
            }
            this.index = computeIndexFromFlat(depth);
            getCurrBoard(this.currNode);
            getPossVals();

            if(this.possVals.isEmpty()){
                while(true){
                    depth--;
                    if(this.currNode.parent == null) return false;
                    this.currNode = this.currNode.parent;

                    if(this.currNode.childInd < this.currNode.children.size() - 1){
                        this.currNode.childInd++;
                        this.currNode = this.currNode.children.get(this.currNode.childInd);
                        depth++;
                        break;
                    }
                }
            }
            else{
                for(int v: possVals) this.currNode.addNewChild(v, index);
                this.currNode = this.currNode.children.getFirst();
                depth++;
            }
        }
        return true;
    }

    // Method to convert a flat array index into an n-dimensional index
    private int[] computeIndexFromFlat(int flatIndex) {
        int[] result = new int[dimensions];
        int[] dims = curr_board.shape();
        int[] multipliers = new int[dimensions];

        // Compute dimension multipliers for index conversion
        multipliers[dimensions - 1] = 1;
        for (int i = dimensions - 2; i >= 0; i--) {
            multipliers[i] = multipliers[i + 1] * dims[i + 1];
        }

        // Decompose flat index into multi-dimensional coordinates
        for (int i = 0; i < dimensions; i++) {
            result[i] = flatIndex / multipliers[i];
            flatIndex = flatIndex % multipliers[i];
        }

        return result;
    }

    public void printBoard(int[][] board) {
        for (int i = 0; i < gridSize; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("------+-------+------");
            }

            for (int j = 0; j < gridSize; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print(board[i][j] == 0 ? ". " : board[i][j] + " ");
            }
            System.out.println();
        }
    }
}

class valRegister {
    int[] axisID = new int[2]; // The dimension pair for a particular register
    ArrayList<Integer> possVals;

    valRegister(int[] id){
        this.axisID[0] = id[0];
        this.axisID[1] = id[1];
        possVals = new ArrayList<Integer>();
    }
}

class NDArray {
	
	private int[] array; 	   //our actual array that holds all the data
	private int[] dimensions;  //how many dimension
	private int[] multipliers; //how far you move in one direction
	
	NDArray(int[] dimensions) {
        // Initializes the n dimensional array
	    int arraySize = 1;

	    multipliers = new int[dimensions.length];
	    for (int idx = dimensions.length - 1; idx >= 0; idx--) {
	      multipliers[idx] = arraySize;
	      arraySize *= dimensions[idx];
	    }
	    array = new int[arraySize];
	    this.dimensions = dimensions;
	  }
	
	public int size() {
	    return array.length;
	}
	
	public int[] shape() {
	    return dimensions.clone();
	}
	
	public int get(int[] indices) {
        // Get a particular value in the array
		if (indices.length != dimensions.length) {
            System.out.print(indices.length + " " + dimensions.length);
	        throw new IllegalArgumentException("Incorrect number of indices");
	    }
		
	    int internalIndex = 0;

	    for (int idx = 0; idx < indices.length; idx++) {
	      internalIndex += indices[idx] * multipliers[idx];
	    }
	    return array[internalIndex];
	}
	
	public void set(int[] indices, int value) {
        // Set a particular value in the array
		if (indices.length != dimensions.length) {
	        throw new IllegalArgumentException("Incorrect number of indices");
	    }
		
	    int internalIndex = 0;

	    for (int idx = 0; idx < indices.length; idx++) {
	        internalIndex += indices[idx] * multipliers[idx];
	    }
	    array[internalIndex] = value;
	}
	
	public NDArray clone_copy() {
        // Duplicates the n dimensional array
        NDArray newNDArray = new NDArray(this.dimensions.clone());

        newNDArray.multipliers = this.multipliers.clone();
        newNDArray.array = this.array.clone();

        return newNDArray;
    }
	
	
	@Override
	public String toString() {
		
		StringBuilder strResult = new StringBuilder();
		
		int[] indices = new int[dimensions.length];
		int value;
		int flatIndex;
		
		for (int i = 0; i < array.length; i++) {
		    value = array[i];
		    
		    flatIndex = i;
		    for (int idx = 0; idx < dimensions.length; idx++) {
		        indices[idx] = flatIndex / multipliers[idx];
		        flatIndex %= multipliers[idx];
		    }

		    strResult.append(Arrays.toString(indices))
            		 .append(" = ")
            		 .append(value)
            		 .append("\n");
		}

		return strResult.toString();
	}
}
