import utils.Matrix;

public class PetriNet {

    private int [][] incMatrix;
    private int [][] sensTransitions;
    private int [][] initialMarking;
    private int [][] currentMarking;

    public PetriNet(int [][] incMatrix, int [][] initialMarking){
        this.incMatrix = incMatrix;
        this.initialMarking = initialMarking;
    }

    // fireTransition
    public int fireTransition(int[][] fireSequence){

        try {
            currentMarking = Matrix.add(currentMarking, Matrix.multiply(incMatrix ,fireSequence));    // Fundamental equation mk = mi + W*s
        } catch(Exception e) {
            // TODO Log exception when Logger its implemented
            return -1;
        }

        return 0;
    }
    // getCurrentMarking
    // getSensTransitions
    // updateMarking
    // checkCo
}
