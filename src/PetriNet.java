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

    /**
     * Toma como argumento la secuencia de disparo (la transición a disparar) y la
     * compara con las transiciones sensibilizadas para saber si es disparable o no.
     * 
     * @param fireSequence
     * @return true si la transicion se puede disparar, false si no
     */
    public boolean isFireable(int[][] fireSequence) {
        for (int i = 0; i < sensTransitions.length; i++) {
            if (sensTransitions[0][i] == 1) {
                if (fireSequence[0][i] == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    // fireTransition
    public void fireTransition(int[][] fireSequence) throws InterruptedException {
        try {
            updateMarking(fireSequence);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // getCurrentMarking
    public int[][] getCurrentMarking() {
        return currentMarking;
    }

    public int[][] getIncidenceMatrix() {
        return incMatrix;
    }

    // getSensTransitions
    public int[][] getSensTransitions() {
        // System.out.println(Constants.TRANSITIONS_COUNT);
        // System.out.println(Constants.PLACES_COUNT);
        int[][] sensTransitions = new int[1][Constants.TRANSITIONS_LENGTH];

        for (int i = 0; i < Constants.TRANSITIONS_LENGTH; i++) {
            // System.out.println(i);
            for (int j = 0; j < Constants.PLACES_LENGTH; j++) {
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

    /**
     * Actualiza el marcado de la red de Petri en base a la ecuación fundamental: mk
     * = mi + W*s
     * 
     * @param fireSequence
     * @return
     */
    public int updateMarking(int[][] fireSequence) {

        System.out.println(Arrays.deepToString(currentMarking));
        try {
            this.currentMarking = Matrix.add(currentMarking,
                    Matrix.transpose(Matrix.multiply(incMatrix, Matrix.transpose(fireSequence))));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }
        System.out.println(Arrays.deepToString(currentMarking));

        return 0;
    }
}
