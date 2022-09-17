package utils;

public final class Matrix {

    public static int[][] multiply(int[][] A, int[][] B) throws Exception {

        // Check matrix dimensions
        int numberOfRowsA = A.length;
        int numberOfRowsB = B.length;
        int numberOfColsA = A[0].length;
        int numberOfColsB = B[0].length;

        if (numberOfColsA != numberOfRowsB) {
            throw new Exception("Error while multiplying matrices. Number of rows of first matrix should match the number of columns of the second one");
        }

        int[][] result = new int[numberOfRowsA][numberOfColsB ];

        // Matrix multiplication operation
        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[0].length; col++) {

                // Dot Product
                for (int i = 0; i < numberOfRowsB ; i++)
                    result[row][col] = result[row][col] + A[row][i] * B[i][col];
            }
        }

        return result;
    }

    public static int[][] add(int[][] A, int[][] B) throws Exception {

        // Check matrix dimensions
        int numberOfRowsA = A.length;
        int numberOfRowsB = B.length;
        int numberOfColsA = A[0].length;
        int numberOfColsB = B[0].length;

        if (numberOfColsA != numberOfColsB | numberOfRowsA != numberOfRowsB) {
            throw new Exception("Error while adding matrices. Number of rows and columns should be equal for both matrices.");
        }

        int[][] result = new int[numberOfRowsA][numberOfColsA];

        // Matrix addition operation
        for (int row = 0; row < result.length; row++) {
            for (int col = 0; col < result[0].length; col++) {

                result[row][col] = A[row][col] + B[row][col];
            }
        }

        return result;
    }

    // Unused but may be helpful in the future
    private static int dotProduct(int[] vectorA, int[] vectorB) {

        int product = 0;
        int n = vectorA.length;

        for (int i = 0; i < n; i++)
            product = product + vectorA[i] * vectorB[i];

        return product;
    }
}
