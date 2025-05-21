// The goal of this project is to be able to solve an
// N-Dimensional Sudoku game in a reasonable amount of
// time. Currently can only solve up to a 4D game, and
// can initialize 5.

// This project expands upon an assignment from my advanced
// data structures and algorithms course, done originally as
// a group project between me, https://github.com/Horizon489732
// and https://github.com/jrsussner18

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args){

        // Demonstration of a 2D and 3D game, from the original college assignment

        /*
        // Normal sudoku (from the paper)
        String file = "sudokuInput/easy2D.txt";
        Sudoku sudoku = new Sudoku(file);

        System.out.println("Initial Sudoku Grid:");
        sudoku.printBoard(sudoku.org_sudoku);

        sudokuDFSSearch dfs = new sudokuDFSSearch(sudoku, 81);
        sudokuBFSSearch bfs = new sudokuBFSSearch(sudoku);

        int[][] dfsSolution = dfs.solve();
        ArrayList<int[][]> bfsSolution = bfs.BFSearch();

        if(dfsSolution != null) {
            System.out.println("Depth Limited Solution: ");
            sudoku.printBoard(dfsSolution);
        } else System.out.println("No Depth Limited Solution Found");

        if(!bfsSolution.isEmpty()){
            System.out.println("Breadth First Solution(s): ");
            for(int[][] s : bfsSolution) {
                System.out.println();
                sudoku.printBoard(s);
            }
            System.out.println("Total BFS solutions: " + bfsSolution.size());
        } else System.out.println("No Breadth First Solution Found");
        System.out.println();

        // 3-dimensional sudoku
        file = "sudokuInput/easy3D.txt";
        nSudoku nsudoku = new nSudoku(3, 9, file);
        System.out.println("3-Dimensional Sudoku");
        nsudoku.solveDLS();
        */

        // 4D testing

        String x = "sudokuInput/blank4D.txt";
        nSudoku test4D = new nSudoku(4, 16, x);
        test4D.solveDLS();
    }
}
