package com.javafx;

import java.util.Random;

public class CreateMap {
    // Initialize a map, all roads are blocked by default
    // The row row represents the number of rows of the blank grid at the beginning, and there are walls between the grids, so the final two-dimensional array size is actually (2row+1) * (2colum+1)

    private int row;
    private int column;

    public int[][] map;// Array to store the maze
    public String[][] map2;// Array to store the maze

    // private Vector[] Pos;
    private int r;
    private int c;

    CreateMap(int r0, int c0) {
        row = r0;
        column = c0;
        r = 2 * row + 1;
        c = 2 * column + 1;

        map = new int[r][c];
    }

    public void Init() {

        for (int i = 0; i < r; i++) // Set all grids as walls
            for (int j = 0; j < c; j++)
                map[i][j] = 1;// 0 is the wall 1 is the road

        // The middle grid is set to 1
//        for (int i = 0; i < row; i++)
//            for (int j = 0; j < column; j++)
//                map[2 * i + 1][2 * j + 1] = 1;// 0 is the wall 1 is the road

        // Primm algorithm
        accLabPrime();
    }

    // Process the array through Prim algorithm to generate the final maze
    // Idea:
    // Randomly find the nearest point to visit (each point is only visited once, until all the roads are visited),
    //A way to visit all points (unordered) will be generated. When the next point is randomly found, the wall between the two adjacent grids will be opened

    public void accLabPrime() {
        // acc stores the visited queue, noacc stores no access queue
        int[] acc, noacc;
        int count = row * column;
        int accsize = 0;// Record the number of points visited

        acc = new int[count];
        noacc = new int[count];

        // Offset in each direction on row Offset in each direction of column 0 left 1 right 3 up 2 down

        int[] offR = {-1, 1, 0, 0};
        int[] offC = {0, 0, 1, -1};

        // Offset in four directions, left, right, up, down
        int[] offS = {-1, 1, row, -row}; // Move up and down is to change one line
        // In the initialization acc, 0 means not visited, and 0 in noacc means not visited
        for (int i = 0; i < count; i++) {
            acc[i] = 0;
            noacc[i] = 0;
        }

        // starting point
        Random rd = new Random();
        acc[0] = rd.nextInt(count);// starting point

        int pos = acc[0];
        // Deposit the first point
        noacc[pos] = 1;
        while (accsize < count) {
            // Take out the current point
            int x = pos % row;
            int y = pos / row;// the coordinates of the point
            int offpos = -1;// Used to record the offset
            int w = 0;
            // try in all four directions until you get it through
            while (++w < 5) {
                // Random access to the nearest point
                int point = rd.nextInt(4); // 0-3
                int repos;
                int move_x, move_y;
                // Calculate the moving direction
                repos = pos + offS[point];// Subscript after moving
                move_x = x + offR[point];// Position after moving
                move_y = y + offC[point];
                // Determine whether the movement is legal
                if (move_y >= 0 && move_x >= 0 && move_x < row && move_y < column && repos >= 0 && repos < count
                        && noacc[repos] != 1) {
                    noacc[repos] = 1;// Mark the point as visited
                    acc[++accsize] = repos;// ++accsize represents the number of visited points, and repos represents the subscript of the point
                    pos = repos;// Use this point as the starting point
                    offpos = point;
                    // Put 1 in the middle of the adjacent grid

                    map[2 * x + 1 + offR[point]][2 * y + 1 + offC[point]] = 1;
                    break;
                } else {
                    if (accsize == count - 1)
                        return;
                    continue;
                }
            }
            if (offpos < 0) {// I can’t find a way around, find a new starting point from the path I walked
                pos = acc[rd.nextInt(accsize + 1)];
            }
        }
    }
}
