import java.util.ArrayList;
import java.util.Arrays;

import utils.Constants;

public class Policy {
    private static Policy instance = null;
    private static int index;
    private static int transitionInv;
    private static int[] counters = new int[4];
    private static Boolean[] invariants = new Boolean[4];
    private int fireableTransitions[] = new int[Constants.TRANSITIONS_COUNT];
    private static ArrayList<Integer> transInv1; // contienen las transiciones de cada invariante
    private static ArrayList<Integer> transInv2;
    private static ArrayList<Integer> transInv3;
    private static ArrayList<Integer> transInv4;

    private Policy() {

    }

    /**
     * 
     * @return instancia de la política
     */
    public static Policy getInstance() {
        if (Policy.instance == null) {
            Policy.instance = new Policy();
            for (int i = 0; i < 4; i++) {
                Policy.invariants[i] = false;
                Policy.counters[i] = 0;
                Policy.index = 0;
                transInv1 = new ArrayList<>(Arrays.asList(1, 2, 3));
                transInv2 = new ArrayList<>(Arrays.asList(4, 5, 6));
                transInv3 = new ArrayList<>(Arrays.asList(13, 14, 15));
                transInv4 = new ArrayList<>(Arrays.asList(16, 17, 18));
            }
        }
        return Policy.instance;
    }

    /**
     * Esta funcion incrementa un contador de disparos asociado a cada invariante de
     * transicion
     * 
     * @param transitionIndex
     */
    public void increment(int transitionIndex) {
        transitionInv = whatInvIs(transitionIndex);
        counters[transitionInv]++;
        System.out.println("Contador 1: " + counters[0] + " \nContador 2: " + counters[1] + " \nContador 3: "
                + counters[2] + " \nContador 4: " + counters[3]);
    }

    /**
     * Esta funcion devuelve el indice del invariante al que pertenece la transicion
     * 
     * @param trans
     * @return invariante de transicion al que pertenece
     */
    static int whatInvIs(int trans) { // codigo pro LGBTQIA+
        if (transInv1.contains(trans))
            return 0;
        else if (transInv2.contains(trans))
            return 1;
        else if (transInv3.contains(trans))
            return 2;
        else
            return 3;
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
        // reset invariants
        for (int i = 0; i < invariants.length; i++) {
            invariants[i] = false;
        }
        // reset fireableTransitions
        for (int i = 0; i < fireableTransitions.length; i++) {
            fireableTransitions[i] = 0;
        }

        for (int i = 0; i < sensTransitions[0].length; i++) {
            if (sensTransitions[0][i] == 1 && waitingThreads[i] > 0) {
                fireableTransitions[i] = 1;
                transitionInv = whatInvIs(i);
                invariants[transitionInv] = true;
            }
        }

        // System.out.println("Contador min: " + min);
        // System.out.println("FireableTransitions: " +
        // java.util.Arrays.toString(fireableTransitions));

        // choose index of fireable transition which has smaller counter value
        int aux = 0;
        int temp = Integer.MAX_VALUE;
        for (int i = 0; i < fireableTransitions.length; i++) {
            if (fireableTransitions[i] == 1) {
                // si la transicion pertenece a un invariante con un contador mas chico que el
                // que tengo guardado, lo actualizo
                if (counters[whatInvIs(Constants.transitionIndexes[i])] < temp) {
                    temp = counters[whatInvIs(Constants.transitionIndexes[i])];
                    aux = i;
                    index = aux;
                } else {
                    // si el contador al que pertenece la transición no es el mas chico, dejo el
                    // indice que ya tengo guardado
                    index = aux;
                }
            }
        }
        // System.out.println("Index: " + index);
        return index;
    }
}
