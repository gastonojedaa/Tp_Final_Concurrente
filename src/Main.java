import utils.Constants;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Random;

public class Main {
    public static void main(String[] args) {

        int firingAmount = 5;

        Random random = new Random();

        PetriNet petriNet = new PetriNet(Constants.INCIDENCE_MATRIX, Constants.BACKWARD_MATRIX,
                Constants.INITIAL_MARKING);

        for (int i = 0; i < firingAmount; i++) {
            int[][] sensTransitions = petriNet.getSensTransitions();

            System.out.println(Arrays.deepToString(sensTransitions));

            ArrayList<Integer> sensTransitionsIndex = new ArrayList<Integer>();

            for (int j = 0; j < Constants.TRANSITIONS_COUNT; j++) {
                if (sensTransitions[0][j] == 1) {
                    sensTransitionsIndex.add(j);
                }
            }

            System.out.println(sensTransitionsIndex);

            int rand = random.nextInt(sensTransitionsIndex.size());

            System.out.println(rand);

            int[][] fireSequence = new int[1][Constants.TRANSITIONS_COUNT];

            fireSequence[0][sensTransitionsIndex.get(rand)] = 1;

            System.out.println(Arrays.deepToString(fireSequence));

            petriNet.updateMarking(fireSequence);

            System.out.println("------------------");
        }

    }
}
