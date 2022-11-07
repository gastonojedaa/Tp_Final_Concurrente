package utils;

public final class Constants {

    public static final int[][] INCIDENCE_MATRIX  = new int [][]{
            {-1,0,0,0,0,0,0,0,1,0,0,0},
            {-1,0,0,0,0,0,0,1,0,-1,1,0},
            {0,-1,0,1,0,0,0,0,0,0,0,0},
            {0,1,-1,0,0,0,0,0,0,0,0,0},
            {0,0,1,-1,0,0,0,0,0,0,0,0},
            {0,0,0,0,-1,0,1,0,0,0,0,0},
            {0,0,0,0,1,-1,0,0,0,0,0,0},
            {0,0,0,0,0,1,-1,0,0,0,0,0},
            {1,0,0,0,0,0,0,-1,0,0,0,0},
            {-1,-1,1,0,-1,1,0,1,0,-1,1,0},
            {-1,-1,1,0,0,-1,1,1,0,0,-1,1},
            {0,0,0,0,0,0,0,-1,1,-1,1,0},
            {0,0,-1,1,-1,1,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,1,-1,0,0,0},
            {0,-1,1,0,-1,1,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,-1,0,1},
            {0,0,0,0,0,0,0,0,0,1,-1,0},
            {0,0,0,0,0,0,0,0,0,0,1,-1}};

    public static final int[][] BACKWARD_MATRIX = new int [][]{
            {1,0,0,0,0,0,0,0,0,0,0,0},
            {1,0,0,0,0,0,0,0,0,1,0,0},
            {0,1,0,0,0,0,0,0,0,0,0,0},
            {0,0,1,0,0,0,0,0,0,0,0,0},
            {0,0,0,1,0,0,0,0,0,0,0,0},
            {0,0,0,0,1,0,0,0,0,0,0,0},
            {0,0,0,0,0,1,0,0,0,0,0,0},
            {0,0,0,0,0,0,1,0,0,0,0,0},
            {0,0,0,0,0,0,0,1,0,0,0,0},
            {1,1,0,0,1,0,0,0,0,1,0,0},
            {1,1,0,0,0,1,0,0,0,0,1,0},
            {0,0,0,0,0,0,0,1,0,1,0,0},
            {0,0,1,0,1,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,1,0,0,0},
            {0,1,0,0,1,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,1,0,0},
            {0,0,0,0,0,0,0,0,0,0,1,0},
            {0,0,0,0,0,0,0,0,0,0,0,1}};

    public static final int[][] INITIAL_MARKING = {{3, 2, 3, 0, 0, 3, 0, 0, 0, 3, 2, 1, 1, 0, 2, 3, 0, 0}};
    public static final int PLACES_LENGTH = INCIDENCE_MATRIX.length;
    public static final int TRANSITIONS_LENGTH = INCIDENCE_MATRIX[0].length;
}
