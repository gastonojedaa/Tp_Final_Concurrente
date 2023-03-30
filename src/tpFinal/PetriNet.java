package tpFinal;

import tpFinal.utils.Constants;
import tpFinal.utils.Matrix;
import java.util.HashMap;
import java.util.Arrays;

public class PetriNet {

    private int[][] incMatrix;
    private int[][] backwardMatrix;
    private int[][] currentMarking;
    public static HashMap<Integer, Long> timeSensitiveTransitions;
    private int beta;
    private int[] alpha;
    public int[] sleepingThreads;
    private long currentPeriod;

    public PetriNet(int[][] incMatrix, int[][] backwardMatrix, int[][] initialMarking, int[] alpha, int beta) {
        this.incMatrix = incMatrix;
        this.backwardMatrix = backwardMatrix;
        this.currentMarking = initialMarking;
        timeSensitiveTransitions = new HashMap<Integer, Long>();
        currentPeriod = 0;
        this.alpha = alpha;
        this.beta = beta;

        sleepingThreads = new int[Constants.TRANSITIONS_COUNT];
        Arrays.fill(sleepingThreads, 0);

        // Inicializo las transiciones que son temporizadas
        timeSensitiveTransitions.put(7, null); // trans 2
        timeSensitiveTransitions.put(8, null);// 3
        timeSensitiveTransitions.put(10, null);// 5
        timeSensitiveTransitions.put(11, null);// 6
        timeSensitiveTransitions.put(2, null);// 14
        timeSensitiveTransitions.put(3, null);// 15
        timeSensitiveTransitions.put(5, null);// 17
        timeSensitiveTransitions.put(6, null);// 18
    }

    // getCurrentMarking
    public int[][] getCurrentMarking() {
        return currentMarking;
    }

    /**
     * 
     * @return vector de transiciones sensibilizadas
     */
    public int[][] getSensTransitions() {
        int[][] sensTransitions = new int[1][Constants.TRANSITIONS_COUNT];

        for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
            for (int j = 0; j < Constants.PLACES_COUNT; j++) {
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
     * Esta funcion recorre el array de transiciones sensibilizadas y devuelve true
     * si la transicion que se le pasa por parametro esta sensibilizada
     * 
     * @param transitionIndex
     * @return True si transitionIndex es una transición sensibilizada
     */
    public Boolean isTransitionValid(int transitionIndex) {
        int[][] sensTransitions = getSensTransitions();
        return sensTransitions[0][transitionIndex] == 1;
    }

    /**
     * Actualiza el marcado de la Red de Petri.
     * 
     * @param fireSequence
     * @return 0 si pudo actualizar el marcado, -1 si no pudo
     */
    public int updateMarking(int[][] fireSequence) {
        try {
            // Fundamental equation mk = mi + W*s
            this.currentMarking = Matrix.add(currentMarking,
                    Matrix.transpose(Matrix.multiply(incMatrix, Matrix.transpose(fireSequence))));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }

        if (!checkPInvariants()) {
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            System.out.println("Error: Invariantes de P violados");
            System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        } // Chequeo los invariantes de P
          // Si esto falla algo se rompio y deberia finalizar el programa

        return 0;
    }

    public Boolean isSomeoneSleeping(int transitionIndex) {
        return sleepingThreads[transitionIndex] > 0;
    }

    public long timeToWindow(int transitionIndex) {
        // Si no es transicion temporal puedo disparar
        if (!timeSensitiveTransitions.containsKey(transitionIndex))
            return 0;

        // Calculo donde estoy respecto a la ventana
        currentPeriod = getCurrentPeriod(transitionIndex);
        long alpha_value = alpha[Policy.transitionToInvariant.get(transitionIndex)];

        // Si estoy antes de la ventana, tengo que esperar x ms
        if (currentPeriod < alpha_value)
            return alpha_value - currentPeriod;

        // Actualizo el timestamp
        setNewTimeStamp(transitionIndex);

        // Si estoy dentro de la ventana, puedo disparar
        if (currentPeriod < beta)
            return 0;

        // En caso contrario me pase de la ventana
        return -1;
    }

    /**
     * Actualiza la marca de tiempo de las trancisiones temporales sensibilizadas
     */
    public void setNewTimeStamp(int transitionIndex) {
        /*
         * cuando se actualiza el estado de la red, se actualiza el marcado, esto puede
         * hacer que dejen de estar sensibilizadas algunas transiciones que estaban
         * esperando y les debemos borrar la marca de tiempo, algunas que no estaban
         * sensibilizadas ahora si lo están y hay que actualizarles la marca de tiempo
         */
        timeSensitiveTransitions.put(transitionIndex, System.currentTimeMillis());
    }

    // tiempo que la transicion lleva sensibilizada
    private long getCurrentPeriod(int transitionIndex) {
        long currentPeriod = System.currentTimeMillis() - timeSensitiveTransitions.get(transitionIndex);
        return currentPeriod;
    }

    private boolean checkPInvariants() {
        boolean I1 = (currentMarking[0][Constants.P1_INDEX] + currentMarking[0][Constants.P2_INDEX]
                + currentMarking[0][Constants.P3_INDEX]) == 3;
        boolean I2 = (currentMarking[0][Constants.P100_INDEX] + currentMarking[0][Constants.P2_INDEX]
                + currentMarking[0][Constants.P5_INDEX]) == 2;
        boolean I3 = (currentMarking[0][Constants.P13_INDEX] + currentMarking[0][Constants.P14_INDEX]
                + currentMarking[0][Constants.P15_INDEX]) == 3;
        boolean I4 = (currentMarking[0][Constants.P16_INDEX] + currentMarking[0][Constants.P17_INDEX]
                + currentMarking[0][Constants.P18_INDEX]) == 3;
        boolean I5 = (currentMarking[0][Constants.P14_INDEX] + currentMarking[0][Constants.P17_INDEX]
                + currentMarking[0][Constants.P2_INDEX] + currentMarking[0][Constants.P200_INDEX]
                + currentMarking[0][Constants.P5_INDEX]) == 3;
        boolean I6 = (currentMarking[0][Constants.P14_INDEX] + currentMarking[0][Constants.P18_INDEX]
                + currentMarking[0][Constants.P2_INDEX] + currentMarking[0][Constants.P25_INDEX]
                + currentMarking[0][Constants.P6_INDEX]) == 2;
        boolean I7 = (currentMarking[0][Constants.P26_INDEX] + currentMarking[0][Constants.P3_INDEX]
                + currentMarking[0][Constants.P5_INDEX]) == 1;
        boolean I8 = (currentMarking[0][Constants.P15_INDEX] + currentMarking[0][Constants.P17_INDEX]
                + currentMarking[0][Constants.P28_INDEX]) == 1;
        boolean I9 = (currentMarking[0][Constants.P14_INDEX] + currentMarking[0][Constants.P17_INDEX]
                + currentMarking[0][Constants.P300_INDEX]) == 2;
        boolean I10 = (currentMarking[0][Constants.P4_INDEX] + currentMarking[0][Constants.P5_INDEX]
                + currentMarking[0][Constants.P6_INDEX]) == 3;

        return (I1 && I2 && I3 && I4 && I5 && I6 && I7 && I8 && I9 && I10);
    }

}
