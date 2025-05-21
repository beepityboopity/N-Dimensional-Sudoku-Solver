// For the original CSC 301 assignment
// Initializes and solves a 2D Sudoku puzzle
// Done in collaboration with https://github.com/Horizon489732 and https://github.com/jrsussner18

// Source reference paper: Lina, T. N., & Rumetna, M. S. (2021). Comparative Analysis of Breadth First Search and Depth Limited Search Algorithms in Sudoku Game. Bulletin of Computer Science and Electrical Engineering, 2(2), 74-83.
// Additional code learned from: https://github.com/jderrickguarin/sudoku-uninformed-search/blob/master/main/DFS_Sudoku.py

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Sudoku {
	public static final int SIZE = 9;
	public static final int SUBGRIDSIZE = 3;
	
	int[][] org_sudoku = new int[SIZE][SIZE];

	public Sudoku(String fileName) {
    	this.createSudoku(fileName);
    }
	
	public void createSudoku(String fileName) {

        ArrayList<int[]> puzzleVals = new ArrayList<int[]>();
        try{
            File puzzle = new File(fileName);
            Scanner fileScanner = new Scanner(puzzle);
            while(fileScanner.hasNextLine()){

                String line = fileScanner.nextLine();
                String[] vals = line.split(" ");
                int[] index = new int[3];
                for(int i = 0; i < 3; i++) index[i] = Integer.parseInt(vals[i]);
                puzzleVals.add(index);
            }
        }
        catch (FileNotFoundException error){
            System.out.println("Bad File Path");
        }

        for(int[] val : puzzleVals){
            org_sudoku[val[0]][val[1]] = val[2];
        }
	}

	public int[] getFirstEmptySpot() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (org_sudoku[row][col] == 0) {
                    return new int[] { row, col };
                }
            }
        }
        return null;
    }
	
	public boolean checkLegal(int[][] state) {
        int total = 45;

        for (int row = 0; row < SIZE; row++) {
            int rowSum = 0;
            int colSum = 0;
            for (int col = 0; col < SIZE; col++) {
                rowSum += state[row][col];
                colSum += state[col][row];
            }
            if (rowSum != total || colSum != total) {
                return false;
            }
        }

        // Check quadrants (3x3 blocks)
        for (int rowStart = 0; rowStart < SIZE; rowStart += SUBGRIDSIZE) {
            for (int colStart = 0; colStart < SIZE; colStart += SUBGRIDSIZE) {
                int blockSum = 0;
                for (int r = 0; r < SUBGRIDSIZE; r++) {
                    for (int c = 0; c < SUBGRIDSIZE; c++) {
                        blockSum += state[rowStart + r][colStart + c];
                    }
                }
                if (blockSum != total) {
                    return false;
                }
            }
        }
        return true;
    }

	
	// Return set of valid numbers from values that do not appear in used
	public ArrayList<Integer> filterValues(ArrayList<Integer> values, ArrayList<Integer> used) {
		ArrayList<Integer> validValues = new ArrayList<>();
        for (Integer value : values) {
            if (!used.contains(value)) {
                validValues.add(value);
            }
        }
        return validValues;
    }
	
	// Filter valid values based on row
    public ArrayList<Integer> filterRow(int[][] state, int row) {
    	ArrayList<Integer> numberSet = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

        ArrayList<Integer> inRow = new ArrayList<>();
        for (int col = 0; col < SIZE; col++) {
            if (state[row][col] != 0) {
                inRow.add(state[row][col]);
            }
        }
        return filterValues(numberSet, inRow);
    }
	
    // Filter valid values based on column
    public ArrayList<Integer> filterCol(ArrayList<Integer> options, int[][] state, int column) {
    	ArrayList<Integer> inColumn = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            if (state[row][column] != 0) {
                inColumn.add(state[row][column]);
            }
        }
        return filterValues(options, inColumn);
    }
    
    // Filter valid values based on quadrant
    public ArrayList<Integer> filterQuad(ArrayList<Integer> options, int[][] state, int row, int column) {
    	ArrayList<Integer> inBlock = new ArrayList<>();
        int rowStart = (row / SUBGRIDSIZE) * SUBGRIDSIZE;
        int colStart = (column / SUBGRIDSIZE) * SUBGRIDSIZE;

        for (int blockRow = 0; blockRow < SUBGRIDSIZE; blockRow++) {
            for (int blockCol = 0; blockCol < SUBGRIDSIZE; blockCol++) {
                inBlock.add(state[rowStart + blockRow][colStart + blockCol]);
            }
        }
        return filterValues(options, inBlock);
    }
    
    
    public ArrayList<int[][]> findNextSolution(int[][] state) {
        int[] spot = getFirstEmptySpot();
        if (spot == null) {
            return new ArrayList<>(); // No empty spots
        }

        int row = spot[0];
        int column = spot[1];

        ArrayList<Integer> options = filterRow(state, row);
        options = filterCol(options, state, column);
        options = filterQuad(options, state, row, column);
        
//        Return states for each valid option
        ArrayList<int[][]> newStates = new ArrayList<>();
        for (Integer number : options) {
            int[][] newState = new int[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                newState[i] = state[i].clone();
            }
            newState[row][column] = number;
            newStates.add(newState);
        }
        return newStates;
    }

    // Method to print a clean looking board
    // Got this from: https://stackoverflow.com/questions/21503476/printing-a-sudoku-matrix-in-java-how-to-get-output-to-the-correct-format
    public void printBoard(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            if (i % 3 == 0 && i != 0) {
                System.out.println("------+-------+------");
            }

            for (int j = 0; j < SIZE; j++) {
                if (j % 3 == 0 && j != 0) {
                    System.out.print("| ");
                }
                System.out.print(board[i][j] == 0 ? ". " : board[i][j] + " ");
            }
            System.out.println();
        }
    }
}

class SudokuTreeNode {
	
	public int[][] sudokuState;
    public SudokuTreeNode parent;
    public ArrayList<SudokuTreeNode> children;
	
	public SudokuTreeNode(int[][] board) {
		this.sudokuState = board;
		this.parent = null;
	    this.children = new ArrayList<>();
	};
	
	public SudokuTreeNode(int[][] board, SudokuTreeNode parent) {
        this.sudokuState = board;
        this.parent = parent;
        this.children = new ArrayList<>();
    }
	
	public ArrayList<SudokuTreeNode> expandChildren(Sudoku problem) {
        ArrayList<int[][]> nextStates = problem.findNextSolution(this.sudokuState);
        ArrayList<SudokuTreeNode> expanded = new ArrayList<>();

        for (int[][] state : nextStates) {
            SudokuTreeNode child = new SudokuTreeNode(state, this);
            expanded.add(child);
            this.children.add(child);
        }

        return expanded;
    }
	
}

class sudokuBFSSearch {
    BFSNode root;
    Sudoku puzzle;
    int[][] originalBoard;
    int[][] currBoard;
    static int size = Sudoku.SIZE;
    static int subSize = Sudoku.SUBGRIDSIZE;

    public sudokuBFSSearch(Sudoku sudoku) {
        this.root = new BFSNode();
        this.puzzle = sudoku;
        this.originalBoard = sudoku.org_sudoku;
        this.currBoard = new int[size][size];
        /*
        // test board with multiple solutions (128)
        originalBoard = new int[][]{
                {0, 5, 0, 0, 1, 0, 2, 0, 0},
                {0, 0, 8, 0, 0, 0, 0, 1, 0},
                {0, 0, 0, 0, 3, 0, 0, 0, 8},
                {0, 0, 0, 0, 0, 2, 0, 0, 0},
                {0, 7, 0, 0, 0, 6, 0, 3, 0},
                {1, 0, 0, 0, 7, 0, 9, 0, 0},
                {7, 0, 0, 0, 0, 0, 5, 0, 0},
                {4, 0, 0, 0, 0, 0, 0, 6, 0},
                {3, 0, 0, 8, 0, 0, 0, 4, 0}};
         */

        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                currBoard[i][j] = originalBoard[i][j];
            }
        }
    }

    public ArrayList<int[][]> BFSearch() {
        // variable setup
        ArrayList<int[][]> solutions = new ArrayList<int[][]>();
        ArrayList<BFSNode> currChildren = new ArrayList<BFSNode>();
        ArrayList<Integer> nextChildren;
        int[] currIndex = {0, 0}; // current index within the sudoku board
        int currSize; // number of nodes in each level

        nextChildren = getPossValues(originalBoard, currIndex);
        for(int val : nextChildren) currChildren.add(root.addNewChild(val, currIndex.clone()));

        // iteration by tree level
        for(int level = 0; level < 80; level++){
            currSize = currChildren.size();
            // index for nodes in the next level
            currIndex[0] = Math.floorDiv(level + 1, size);
            currIndex[1] = (level + 1) % size;

            // iterate through each node in the level and set their children
            for(int i = 0; i < currSize; i++){
                BFSNode currNode = currChildren.getFirst();
                setCurrBoard(currNode);
                nextChildren = getPossValues(currBoard, currIndex);
                for(int val : nextChildren) currChildren.add(currNode.addNewChild(val, new int[]{currIndex[0], currIndex[1]}));
                currChildren.removeFirst();
            }
            if(currChildren.isEmpty()) break; // board has no solution
        }

        // return solution board for every level 80 node
        for(BFSNode child : currChildren) solutions.add(setCurrBoard(child));
        return solutions;
    }
    public int[][] setCurrBoard(BFSNode leaf){
        // sets current board based on the bfs tree
        BFSNode currParent = leaf;
        while(currParent.hasParent()){
            this.currBoard[currParent.index[0]][currParent.index[1]] = currParent.value;
            currParent = currParent.parent;
        }
        return this.currBoard;
    }
    public ArrayList<Integer> getPossValues(int[][] board, int[] index){
        ArrayList<Integer> values = new ArrayList<Integer>();
        if(board[index[0]][index[1]] != 0) values.add(board[index[0]][index[1]]);
        else{
            // start with all possible values, then remove ones found in the board
            values.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
            for(int i = 0; i < size; i++){
                values.remove(Integer.valueOf(board[index[0]][i])); // row values
                values.remove(Integer.valueOf(board[i][index[1]])); // column values
            }

            int rowStart = (index[0] / subSize) * subSize;
            int colStart = (index[1] / subSize) * subSize;
            for (int blockRow = 0; blockRow < subSize; blockRow++) {
                for (int blockCol = 0; blockCol < subSize; blockCol++) {
                    values.remove(Integer.valueOf(board[rowStart + blockRow][colStart + blockCol]));
                }
            }
        }
        return values;
    }
}

class sudokuDFSSearch {
    private Sudoku sudoku;
    private int depthLimit;
    private int size = Sudoku.SIZE;

    // Constructor
    public sudokuDFSSearch(Sudoku sudoku, int depthLimit) {
        this.sudoku = sudoku;
        this.depthLimit = depthLimit;
    }

    // Public method for DLS
    public int[][] solve() {
        return depthLimitedDFS(sudoku.org_sudoku, 0);
    }

    // Main method for recursive-based DLS
    private int[][] depthLimitedDFS(int[][] board, int depth) {
        // Exceeded depth of board
        if (depth > depthLimit) {
            return null;
        }

        // Get next empty cell to test
        int[] emptySpot = findNextEmpty(board);
        if (emptySpot == null) {
            // No empty spots left - board is potentially solved
            if (sudoku.checkLegal(board)) {
                return board;
            } else {
                // Invalid complete board
                return null;
            }
        }
        int row = emptySpot[0];
        int col = emptySpot[1];

        // Generate possible values for current empty cell using row, column, and subgrid filters
        ArrayList<Integer> options = sudoku.filterRow(board, row);
        options = sudoku.filterCol(options, board, col);
        options = sudoku.filterQuad(options, board, row, col);

        // Try each valid number recursively
        for (int val : options) {
            int[][] newBoard = copyBoard(board);
            newBoard[row][col] = val;

            // Increase depth with each recursive call
            int[][] result = depthLimitedDFS(newBoard, depth + 1);
            if (result != null) {
                return result; // Valid solution
            }
        }

        // No solution
        return null;
    }

    // Method to find the next empty cell
    private int[] findNextEmpty(int[][] board) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    // Method to copy the current board state
    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[size][size];
        for (int i = 0; i < size; i++) {
            copy[i] = board[i].clone();
        }
        return copy;
    }

    // Method to count the number of empty cells in board
    private static int countEmpty(int[][] board) {
        int count = 0;
        for (int[] row : board) {
            for (int val : row) {
                if (val == 0) {
                    count++;
                }
            }
        }
        return count;
    }

}
