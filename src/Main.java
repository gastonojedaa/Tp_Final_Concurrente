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

        for (int i = 0; i < firingAmount; i++) { //dispara 5 veces
            int[][] sensTransitions = petriNet.getSensTransitions(); //se obtienen las transiciones sensibilizadas

            System.out.println(Arrays.deepToString(sensTransitions));

            ArrayList<Integer> sensTransitionsIndex = new ArrayList<Integer>(); //crea un arraylist para guardar los indices de las transiciones sensibilizadas

            for (int j = 0; j < Constants.TRANSITIONS_COUNT; j++) {
                if (sensTransitions[0][j] == 1) {
                    sensTransitionsIndex.add(j); //agrega el indice de la transicion sensibilizada al arraylist
                }
            }

            System.out.println(sensTransitionsIndex);

            int rand = random.nextInt(sensTransitionsIndex.size()); //se elige un indice aleatorio del arraylist

            System.out.println(rand);

            int[][] fireSequence = new int[1][Constants.TRANSITIONS_COUNT]; // crea una secuencia de disparo

            fireSequence[0][sensTransitionsIndex.get(rand)] = 1; //se dispara la transicion elegida aleatoriamente

            System.out.println(Arrays.deepToString(fireSequence));

            petriNet.updateMarking(fireSequence); //se actualiza el marcado

            System.out.println("------------------");
        }

    }
}
