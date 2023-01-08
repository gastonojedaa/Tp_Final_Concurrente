import java.util.concurrent.Semaphore;
import utils.Constants;

public class Monitor {
    private static Monitor instance = null;
    private static Semaphore[] transitionQueues;
    // private int[] transitionQueuesQuantity;
    private static Semaphore mutex;
    private static PetriNet petriNet;
    private static int transitionToBeFired;
    private static int[] waitingThreads;
    private static int numberOfTransitionsFired;
    private static Boolean finalized;

    // k del video de mico
    private Boolean canBeFired;

    private Monitor() {
    }

    public static synchronized Monitor getNewInstance() {
        if (Monitor.instance == null) {
            Monitor.instance = new Monitor();
            Monitor.petriNet = new PetriNet(Constants.INCIDENCE_MATRIX, Constants.BACKWARD_MATRIX, Constants.INITIAL_MARKING);
            Monitor.numberOfTransitionsFired = 0;
            Monitor.finalized = false;

            // mutex del monitor
            Monitor.mutex = new Semaphore(1);

            Monitor.waitingThreads = new int[Constants.TRANSITIONS_COUNT];
            for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
                Monitor.waitingThreads[i] = 0;
            }

            // colas de transiciones
            Monitor.transitionQueues = new Semaphore[Constants.TRANSITIONS_COUNT];
            for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
                Monitor.transitionQueues[i] = new Semaphore(0);
            }
        }
        
        return Monitor.instance;
    }

    public Boolean fire(int transitionIndex) {

        // Tomar mutex del monitor
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // La transicion a disparar es la que se pasa por parametro
        transitionToBeFired = transitionIndex;
        canBeFired = true;

        while (canBeFired) {
            // Disparo la transicion, si esta sensibilizada devuelve true
            if (finalized) {
                for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
                    for (int j = 0; j < waitingThreads[j]; j++) {
                        transitionQueues[i].release();
                    }
                }
                mutex.release();
                return false;
            }

            canBeFired = petriNet.tryUpdateMarking(transitionToBeFired);

            int[] queuesLength = new int[Constants.TRANSITIONS_COUNT];
            for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
                queuesLength[i] = waitingThreads[i];
            }
            System.out.println("Colas de transiciones:          " + java.util.Arrays.toString(queuesLength));

            if (!canBeFired) {
                waitingThreads[transitionToBeFired]++;
                mutex.release();
                try {
                    // Lo pone en la cola de espera, ya que no hay tokens que pueda tomar
                    transitionQueues[transitionToBeFired].acquire();
                    if (finalized)
                        return false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            else {
                numberOfTransitionsFired++;
                if (numberOfTransitionsFired == 1000)
                    finalized = true;
                // System.out.println("Se disparo transicion T" +
                // Constants.transitionIndexes[transitionToBeFired]);
                System.out.println("Indice transicion : " + transitionToBeFired);
                System.out.println("Transiciones disparadas: " + numberOfTransitionsFired);

                // Que transiciones estan sensibilizadas
                int[][] sensTransitions = petriNet.getSensTransitions();

                // int[] queuesLength = new int[Constants.TRANSITIONS_COUNT];
                // for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
                // queuesLength[i] = waitingThreads[i];
                // }
                // System.out.println("Colas de transiciones: " +
                // java.util.Arrays.toString(queuesLength));
                System.out.println("Transiciones sensibilizadas:    " + java.util.Arrays.toString(sensTransitions[0]));
                System.out.print("Marcado actual: " + java.util.Arrays.deepToString(petriNet.getCurrentMarking()));
                System.out.println("\n-----------------------------------------------------------------------");

                // De estas transiciones, cual tiene hilos esperando
                for (int i = 0; i < queuesLength.length; i++) {
                    if (sensTransitions[0][i] == 1 && waitingThreads[i] > 0) {
                        // Disparo el primer hilo que este esperando //TODO Usar politicas
                        Monitor.transitionToBeFired = i;
                        waitingThreads[i]--;
                        transitionQueues[i].release();
                        return true;
                    }
                }

                /*
                 * Si no hay hilos esperando, cuyas transiciones esten sensibilizadas
                 * entonces salgo del monitor y el proximo hilo a entrar va a ser
                 * de la cola de entrada y no de las colas de transiciones
                 */
                canBeFired = false;
            }
        }

        mutex.release();
        return true;
    }

    public Boolean isFinalized() {
        return finalized;
    }
}
