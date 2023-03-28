import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import utils.Constants;

public class Policy {
    private static Policy instance = null;
    private static int transitionInv;
    private static int[] counters = new int[4];
    private static Boolean[] invariants = new Boolean[4];
    private int fireableTransitions[] = new int[Constants.TRANSITIONS_COUNT];// TODO Maybe change int to boolean
    public static HashMap<Integer, Integer> transitionToInvariant;

    private Policy() {
    }

    /**
     * 
     * @return instancia de la política
     */
    public static Policy getInstance() {
        if (instance == null) {
            instance = new Policy();

            Arrays.fill(invariants, false);
            Arrays.fill(counters, 0);

            transitionToInvariant = new HashMap<Integer, Integer>();

            // Inicializo el mapa de transiciones a invariantes
            for (int value : Constants.T_INVARIANT_1) {
                transitionToInvariant.put(value, 0);
            }

            for (int value : Constants.T_INVARIANT_2) {
                transitionToInvariant.put(value, 1);
            }

            for (int value : Constants.T_INVARIANT_3) {
                transitionToInvariant.put(value, 2);
            }

            for (int value : Constants.T_INVARIANT_4) {
                transitionToInvariant.put(value, 3);
            }
        }
        return instance;
    }

    /**
     * Esta funcion incrementa un contador de disparos asociado a cada invariante de
     * transicion
     * 
     * @param transitionIndex
     */
    public void increment(int transitionIndex) {
        transitionInv = transitionToInvariant.get(transitionIndex);
        counters[transitionInv]++;
        System.out.println("Contador 1: " + counters[0] + " \nContador 2: " + counters[1] + " \nContador 3: "
                + counters[2] + " \nContador 4: " + counters[3]);
    }

    /**
     * Esta funcion devuelve el indice de la transicion que se debe disparar
     * buscando entre las transiciones disparables (sensibilizadas y en la cola de
     * espera) aquellas que tengan el contador mas chico, es decir, que se hayan
     * disparado menos veces.
     * 
     * @param sensTransitions
     * @param waitingThreads
     * @return indice de transicion a disparar
     */
    public int whoToFire(int[][] sensTransitions, int[] waitingThreads) {
        int fireableTransitionsCount = 0;
        // reset invariants
        Arrays.fill(invariants, false);

        // reset fireableTransitions
        Arrays.fill(fireableTransitions, 0);

        for (int i = 0; i < sensTransitions[0].length; i++) {
            if (sensTransitions[0][i] == 1 && waitingThreads[i] > 0) {
                fireableTransitionsCount++;
                fireableTransitions[i] = 1;
                transitionInv = transitionToInvariant.get(i);
                invariants[transitionInv] = true;
            }
        }

        if (fireableTransitionsCount == 0) {
            return -1;
        }

        // Chequeo que invariante tengo que disparar
        int invariantToFire = 0;
        int currentCounter = Integer.MAX_VALUE;
        for (int i = 0; i < invariants.length; i++) {
            if (invariants[i] == true) {
                if (counters[i] < currentCounter) {
                    currentCounter = counters[i];
                    invariantToFire = i;
                }
            }
        }

        // Del invariante que tengo que disparar busco la primer transicion
        // sensibilizada
        for (int i = 0; i < fireableTransitions.length; i++) {
            if (transitionToInvariant.get(i) == invariantToFire && fireableTransitions[i] == 1) {
                return i;
            }
        }

        System.out.print("Error en la política");
        return -1;
    }
}
