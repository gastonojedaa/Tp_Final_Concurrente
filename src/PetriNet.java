import utils.Constants;
import utils.Matrix;

public class PetriNet {

    private int[][] incMatrix;
    private int[][] backwardMatrix;
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

    /**
     * 
     * @return vector de transiciones sensibilizadas
     */
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

    /**
     * 
     * @param transitionIndex
     * @return True si transitionIndex es una transición sensibilizada
     */
    public Boolean isTransitionValid(int transitionIndex) {
        int[][] sensTransitions = getSensTransitions();
        if (sensTransitions[0][transitionIndex] == 1)
            return true;
        else
            return false;
    }

    // updateMarking

    public int updateMarking(int[][] fireSequence) {

        // System.out.println("[[P1-P100-P13-P14-P15-P16-P17-P18-P2-P200-P25-P26-P28-P3-P300-P4-P5-P6]]");
        // System.out.println(Arrays.deepToString(currentMarking));
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
        // System.out.println("[[P1-P100-P13-P14-P15-P16-P17-P18-P2-P200-P25-P26-P28-P3-P300-P4-P5-P6]]");
        // System.out.println(Arrays.deepToString(currentMarking));

        return 0;
    }

    /**
    *  @return true si pudo disparar la transicion, false si no pudo porque no era válida
    * @param transitionIndex indice de la transicion a disparar
    */
    public Boolean tryUpdateMarking(int transitionIndex) {
        Boolean isTransitionValid = this.isTransitionValid(transitionIndex);
        if (!isTransitionValid)
            return false;

        int[][] fireSequence = new int[1][Constants.TRANSITIONS_COUNT];
        fireSequence[0][transitionIndex] = 1;
        this.updateMarking(fireSequence);
        return true;
    }
}
