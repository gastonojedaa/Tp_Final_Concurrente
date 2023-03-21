import utils.Constants;
import utils.Matrix;
import java.util.HashMap;
import java.util.Arrays;

public class PetriNet {

    private int[][] incMatrix;
    private int[][] backwardMatrix;
    private int[][] currentMarking;
    private static HashMap<Integer, Long> timeSensitiveTransitions;
    private int beta;
    private int[] alpha;
    public int[] sleepingThreads;
    private static long currentPeriod;
    private static Policy policy;

    public PetriNet(int[][] incMatrix, int[][] backwardMatrix, int[][] initialMarking, int[] alpha, int beta) {
        this.incMatrix = incMatrix;
        this.backwardMatrix = backwardMatrix;
        this.currentMarking = initialMarking;
        timeSensitiveTransitions = new HashMap<Integer, Long>();
        currentPeriod = 0;
        this.alpha = alpha;
        this.beta = beta;
        policy = Policy.getInstance();

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
            // pregunto si hay alguien esperando
            if (sleepingThreads[transitionIndex] == 1) {
                // hay una transición esperando, nadie mas puede intentar dispararla
                return false; // (?????)
            }
            if (testWindowPeriod(transitionIndex) == false) {
            /*     System.out.println("Thread " + Thread.currentThread().getId() + " periodo actual: " +
                currentPeriod);    */
                if (getCurrentPeriod(transitionIndex) < alpha[Policy.whatInvIs(transitionIndex)]) {
                    // no estoy dentro de la ventana temporal, pero todavía no llegué al límite
                    // de espera, entonces me duermo

                    sleepingThreads[transitionIndex] = 1;
                    return false;
                }
                return false;
            }
        }

        if (timeSensitiveTransitions.containsKey(transitionIndex))
            setNewTimeStamp(transitionIndex);

        int[][] fireSequence = new int[1][Constants.TRANSITIONS_COUNT];
        fireSequence[0][transitionIndex] = 1;
        int[][] oldSensTransitions = getSensTransitions();
        this.updateMarking(fireSequence);
        int[][] newSensTransitions = getSensTransitions();

        for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
            if (timeSensitiveTransitions.containsKey(i)) {
                if (oldSensTransitions[0][i] == 0 && newSensTransitions[0][i] == 1)
                    setNewTimeStamp(i);
            }
        }
        // No es necesario actualizar el timestamp de las que quedan sin sensibilizar ya
        // que
        // no se puede disparar una transicion que no esta sensibilizadas por lo que
        // nunca
        // se va a leer el timestamp de la misma.
        // Cuando esta se vuelva a sensibilizar se actualiza el timestamp
        // if oldSensTrans==1 y newSensTransitions==0
        // Null
        // actualizo la marca de tiempo en la que se sensibilizó la transición

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
        if (alpha[Policy.whatInvIs(transitionIndex)] < currentPeriod && currentPeriod < beta)
            return true;
        else
            return false;
    }

    /**
     * Actualiza la marca de tiempo de las trancisiones temporales sensibilizadas
     */
    private void setNewTimeStamp(int transitionIndex) {
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

        // |---------------->
        // sensib currentTimeMillis()

        // print time sensitive transition and currentperiod
        /*
         * System.out.println("Transition: " + transitionIndex + " se sensibilizó: " +
         * timeSensitiveTransitions.get(transitionIndex) + " tiempo actual: " +
         * System.currentTimeMillis() + " periodo actual: " + currentPeriod);
         */
        return currentPeriod;
    }

    /**
     * Devuelve el tiempo que debe dormir el hilo que quiere disparar la transicion.
     * 
     * @param transitionIndex
     * @return long cuanto tiempo debe dormir el hilo
     */
    public long howMuchToSleep(int transitionIndex) {
        long time = 0;
        long currentPeriod = getCurrentPeriod(transitionIndex);
        // print thread and current period
        
        /*  System.out.println("(Sleep)Thread " + Thread.currentThread().getId() + " periodo actual: " +
         currentPeriod);         
          */
        // alpha
        // |---------------|
        // |----------------------------|
        // beta
        // |------------|
        // ventana de disparo

        time = Math.max(0l, alpha[Policy.whatInvIs(transitionIndex)] - currentPeriod);
        return time;
    }
}
