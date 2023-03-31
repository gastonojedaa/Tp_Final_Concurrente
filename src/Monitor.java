import java.util.Arrays;
import java.util.concurrent.Semaphore;
import utils.Constants;
import utils.ConcurrentLogger;

public class Monitor {
    private static Monitor instance = null;
    private static Policy policy;
    private static Semaphore[] transitionQueues;
    private static int waitingThreads[];
    private static Semaphore mutex;
    private static PetriNet petriNet;
    public static int numberOfTransitionsFired;
    private static Boolean finalized;
    private ConcurrentLogger logger;

    private Monitor() {
        logger = ConcurrentLogger.getInstance();
    }

    public static synchronized Monitor getInstance() {
        if (Monitor.instance == null) {
            Monitor.instance = new Monitor();
            policy = Policy.getInstance();
            Monitor.petriNet = new PetriNet(Constants.INCIDENCE_MATRIX, Constants.BACKWARD_MATRIX,
                    Constants.INITIAL_MARKING, Constants.ALPHA, Constants.BETA);
            waitingThreads = new int[Constants.TRANSITIONS_COUNT];
            for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
                waitingThreads[i] = 0;
            }
            Monitor.numberOfTransitionsFired = 0;
            Monitor.finalized = false;

            // mutex del monitor
            Monitor.mutex = new Semaphore(1);

            // colas de transiciones
            Monitor.transitionQueues = new Semaphore[Constants.TRANSITIONS_COUNT];
            for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
                Monitor.transitionQueues[i] = new Semaphore(0);
            }
        }
        return Monitor.instance;
    }

    public Boolean isFinalized() {
        return finalized;
    }

    public void fire(int transitionIndex, Boolean hasMutex) {
        // Cola de entrada
        if (!hasMutex) {
            try {
                mutex.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Veo si la transición está sensibilizada
        Boolean sens = petriNet.isTransitionValid(transitionIndex);

        // Veo si hay alguien durmiendo
        Boolean isSomeoneSleeping = petriNet.sleepingThreads[transitionIndex] > 0;

        // Si no está sensibilizada, la pongo en la cola de espera
        if (!sens || isSomeoneSleeping) {
            waitingThreads[transitionIndex]++; // incremento la cantidad de hilos esperando
            mutex.release();
            try {
                transitionQueues[transitionIndex].acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // cola de espera de recursos
            // Cuando despierta, se llama recursivamente, sin intentar tomar el mutex y con
            // la transición correspondiente.
            fire(transitionIndex, true);
            return;
        }

        // Si esta sensibilizada reviso si esta en la ventana de disparo
        long ms = petriNet.timeToWindow(transitionIndex);

        // Se paso de la ventana temporal
        if (ms == -1) {/*
                        * System.out.println("Out of window. Timestamp is bigger than beta.");
                        */
            mutex.release();
            fire(transitionIndex, false);
            return;
        }
        // Se puede disparar
        else if (ms == 0) {
            int[][] fireSequence = new int[1][Constants.TRANSITIONS_COUNT];
            fireSequence[0][transitionIndex] = 1;
            int[][] oldSensTransitions = petriNet.getSensTransitions();
            // System.out.println("-----------Init-----------");
            // System.out.println("strSensTransitions: " + "T1 T13 T14 T15 T16 T17 T18 T2 T3
            // T4 T5 T6");
            // System.out.println("oldSensTransitions: " +
            // Arrays.deepToString(oldSensTransitions));
            petriNet.updateMarking(fireSequence);
            int[][] newSensTransitions = petriNet.getSensTransitions();
            // System.out.println("newSensTransitions: " +
            // Arrays.deepToString(newSensTransitions));

            for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
                if (PetriNet.timeSensitiveTransitions.containsKey(i)) {
                    if (oldSensTransitions[0][i] == 0 && newSensTransitions[0][i] == 1)
                        petriNet.setNewTimeStamp(i);
                }
            }

            numberOfTransitionsFired++;
            policy.increment(transitionIndex);
            logger.logInfo("T" + Constants.transitionIndexes[transitionIndex]);
            /*
             * System.out.println("Thread " + Thread.currentThread().getId() + " fired "
             * + Constants.transitionIndexes[transitionIndex] +
             * "\nNumber of transitions fired: "
             * + numberOfTransitionsFired);
             */
            if (Policy.totalInvariantsFired == 1000) {
                finalized = true;
                return;
            }
            int[][] sensTransitions = petriNet.getSensTransitions();
            // Disparo el hilo que pertenezca al invariante con menor promedio de disparos
            int transitionToWakeUp = policy.whoToFire(sensTransitions, waitingThreads);

            if (transitionToWakeUp != -1) {
                /*
                 * System.out.println("transitionToWakeUp: " +
                 * Constants.transitionIndexes[transitionToWakeUp]);
                 * System.out.println("-----------End-----------");
                 */
                waitingThreads[transitionToWakeUp]--;
                transitionQueues[transitionToWakeUp].release();
                return;
            } /*
               * System.out.println("There is no transitionToWakeUp: ");
               * System.out.println("-----------End-----------");
               */
            // si no hay hilos esperando y que puedan ser disparados, libero el mutex
            mutex.release();
            return;
        }
        // Tiene que dormir
        else {
            try {
                // Avisa que esta esperando la ventana temporal
                petriNet.sleepingThreads[transitionIndex] = 1;

                // Libera el mutex
                mutex.release();

                // Se va a dormir
                Thread.sleep(ms);

                // Cuando se despierta vuelve a intentar disparar
                mutex.acquire();
                petriNet.sleepingThreads[transitionIndex] = 0;

                fire(transitionIndex, true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}