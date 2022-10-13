import utils.Constants;
import utils.Matrix;
import java.util.Arrays;

public class PetriNet {

    private int [][] incMatrix;
    private int [][] sensTransitions;
    private int [][] currentMarking;

    public PetriNet(int [][] incMatrix, int [][] initialMarking){
        this.incMatrix = incMatrix;
        this.currentMarking = initialMarking;
    }

    // fireTransition

    // getCurrentMarking
    public int[][] getCurrentMarking(){
        return currentMarking;
    }
    // getSensTransitions
    public int[][] getSensTransitions(){
        int[][] sensTransitions = new int[0][Constants.TRANSITIONS_COUNT];

        for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++){
            for (int j = 0; j < Constants.PLACES_COUNT; j++){
                if (incMatrix[j][i] == 1 && incMatrix[j][i] != currentMarking[0][j]){
                    sensTransitions[0][j] = 0;
                    continue;
                }
                sensTransitions[0][j] = 1;
            }
        }
        return sensTransitions;
    }
    // updateMarking

    public int updateMarking(int[][] fireSequence){

        try {
            currentMarking = Matrix.add(currentMarking, Matrix.multiply(incMatrix ,fireSequence));    // Fundamental equation mk = mi + W*s
        } catch(Exception e) {
            // TODO Log exception when Logger its implemented
            return -1;
        }

        return 0;
    }

}
