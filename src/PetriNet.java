import utils.Constants;
import utils.Matrix;
import java.util.HashMap;
import java.util.Arrays;

public class PetriNet {

    private int[][] incMatrix;
    private int[][] backwardMatrix;
    private int[][] currentMarking;
    private static HashMap<Integer, Long> timeSensitiveTransitions;
    private int alpha, beta;
    public int[] sleepingThreads;
    private static long currentPeriod;

    public PetriNet(int[][] incMatrix, int[][] backwardMatrix, int[][] initialMarking, int alpha, int beta) {
        this.incMatrix = incMatrix;
        this.backwardMatrix = backwardMatrix;
        this.currentMarking = initialMarking;
        timeSensitiveTransitions = new HashMap<Integer, Long>();
        currentPeriod = 0;
        this.alpha = alpha;
        this.beta = beta;

        sleepingThreads = new int[Constants.TRANSITIONS_COUNT];
        Arrays.fill(sleepingThreads, 0);

        // inicializo las transiciones que son temporizadas
        timeSensitiveTransitions.put(7, null); //trans 2
        timeSensitiveTransitions.put(8, null);//  3
        timeSensitiveTransitions.put(10, null);// 5
        timeSensitiveTransitions.put(11, null);// 6
        timeSensitiveTransitions.put(2, null);//  14
        timeSensitiveTransitions.put(3, null);//  15
        timeSensitiveTransitions.put(5, null);//  17
        timeSensitiveTransitions.put(6, null);//  18
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
     * Esta funcion recorre el array de transiciones sensibilizadas y devuelve true
     * si la transicion que se le pasa por parametro esta sensibilizada
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

    /**
     * Actualiza el marcado de la Red de Petri.
     * 
     * @param fireSequence
     * @return 0 si pudo actualizar el marcado, -1 si no pudo
     */
    public int updateMarking(int[][] fireSequence) {
        try {
            this.currentMarking = Matrix.add(currentMarking,
                    Matrix.transpose(Matrix.multiply(incMatrix, Matrix.transpose(fireSequence))));
            // Fundamental equation mk = mi + W*s
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return -1;
        }

        return 0;
    }

    /**
     * @return true si pudo disparar la transicion, false si no pudo porque no era
     *         válida
     * @param transitionIndex indice de la transicion a disparar
     */
    public Boolean tryUpdateMarking(int transitionIndex) {
        Boolean isTransitionValid = this.isTransitionValid(transitionIndex);
        if (!isTransitionValid)
            return false;

        // pregunto si es una transición temporal
        if (timeSensitiveTransitions.containsKey(transitionIndex)) {
            // pregunto si estoy dentro de la ventana temporal
            if (sleepingThreads[transitionIndex] == 1) {
                // hay una transición esperando, nadie mas puede intentar dispararla
                return false; // (?????)
            } else if (testWindowPeriod(transitionIndex) == false) {
                if (getCurrentPeriod(transitionIndex) < alpha) {
                    // no estoy dentro de la ventana temporal, pero todavía no llegué al límite
                    // de espera, entonces me duermo
                    sleepingThreads[transitionIndex] = 1;
                    return false;
                } else {
                    return true;
                }
            }
        }

        int[][] fireSequence = new int[1][Constants.TRANSITIONS_COUNT];
        fireSequence[0][transitionIndex] = 1;
        this.updateMarking(fireSequence);

        // actualizo la marca de tiempo en la que se sensibilizó la transición
        setNewTimeStamp();
        // reset vector de espera
        return true;
    }

    /**
     * Analiza si la transicion que se le pasa por parametro esta dentro del periodo
     * habilitado para dispararse, para esto calcula el periodo desde que la
     * transición se sensibilizó y la marca de tiempo actual para saber si se
     * encuentra dentro de la ventana temporal.
     * 
     * @param transitionIndex
     * @return true si la transicion esta dentro de la ventana temporal, false si no
     */
    private Boolean testWindowPeriod(int transitionIndex) {
        currentPeriod = getCurrentPeriod(transitionIndex);
        if (alpha < currentPeriod && currentPeriod < beta)
            return true;
        else
            return false;
    }

    /**
     * Actualiza la marca de tiempo de las trancisiones temporales sensibilizadas
     */
    private void setNewTimeStamp() {
        /*
         * cuando se actualiza el estado de la red, se actualiza el marcado, esto puede
         * hacer que dejen de estar sensibilizadas algunas transiciones que estaban
         * esperando y les debemos borrar la marca de tiempo, algunas que no estaban
         * sensibilizadas ahora si lo están y hay que actualizarles la marca de tiempo
         */
        int[][] sensTransitions = getSensTransitions();
        for (int keys : timeSensitiveTransitions.keySet()) {
            if (sensTransitions[0][keys] == 1) {
                timeSensitiveTransitions.put(keys, System.currentTimeMillis());
            } else {
                // se le borra la marca de tiempo de aquellas transiciones que no están
                // sensibilizadas
                timeSensitiveTransitions.put(keys, null);
            }
        }
    }

    /* Forma de saber cuanto tiempo pasó desde que se sensibilizó la transición */
    private long getCurrentPeriod(int transitionIndex) {
        long currentPeriod = timeSensitiveTransitions.get(transitionIndex) - System.currentTimeMillis();
        return currentPeriod;
    }

    /**
     * Devuelve el tiempo que debe dormir el hilo que quiere disparar la transicion.
     * @param transitionIndex
     * @return long cuanto tiempo debe dormir el hilo
     */
    public long howMuchToSleep(int transitionIndex){
        long time = 0;
        long currentPeriod = getCurrentPeriod(transitionIndex);
        //       alpha
        // |---------------| 
        // |----------------------------|
        //             beta
        //                 |------------|
        //               ventana de disparo

        time = alpha - currentPeriod;
        return time;
    }
    
}
