import utils.Constants;
import utils.Matrix;
import java.util.Arrays;

public class PetriNet {

    private int[][] incMatrix;
    private int[][] backwardMatrix;
    private int[][] sensTransitions;
    private int[][] currentMarking;

    public PetriNet(int[][] incMatrix, int[][] backwardMatrix, int[][] initialMarking) {
        this.incMatrix = incMatrix;
        this.backwardMatrix = backwardMatrix;
        this.currentMarking = initialMarking;
    }

    // fireTransition

    // getCurrentMarking
    public int[][] getCurrentMarking() {
        return currentMarking;
    }

    // getSensTransitions
    public int[][] getSensTransitions() {
        // System.out.println(Constants.TRANSITIONS_COUNT);
        // System.out.println(Constants.PLACES_COUNT);
        int[][] sensTransitions = new int[1][Constants.TRANSITIONS_COUNT];

        for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
            // System.out.println(i);
            for (int j = 0; j < Constants.PLACES_COUNT; j++) {
                // System.out.println(j);
                if (backwardMatrix[j][i] == 1 && backwardMatrix[j][i] > currentMarking[0][j]) {
                    sensTransitions[0][i] = 0;
                    break;
                } else
                    sensTransitions[0][i] = 1;
            }
        }
        return sensTransitions;
    }
    // updateMarking

    public int updateMarking(int[][] fireSequence) {

        System.out.println(Arrays.deepToString(currentMarking));
        try {

            this.currentMarking = Matrix.add(currentMarking,
                    Matrix.transpose(Matrix.multiply(incMatrix, Matrix.transpose(fireSequence))));
            // Fundamental
            // equation mk = mi +
            // W*s
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }
        System.out.println(Arrays.deepToString(currentMarking));

        return 0;
    }

}
