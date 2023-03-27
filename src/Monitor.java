import java.util.Arrays;
import java.util.concurrent.Semaphore;
import utils.Constants;

public class Monitor {
    private static Monitor instance = null;
    private static Policy policy;
    private static Semaphore[] transitionQueues;
    private static int waitingThreads[];
    private static Semaphore mutex;
    private static PetriNet petriNet;
    private static int numberOfTransitionsFired;
    private static Boolean finalized;
    private Boolean canBeFired;

    private Monitor() {
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

    /**
     * Dispara la transición indicada por el índice. Si no puede dispararse, ingresa
     * a una cola de espera, donde cada elemento del arreglo corresponde a una
     * transición.
     * En el caso en el que sí puede dispararse, actualiza el marcado de la red y
     * dispara la primera transición que tenga hilos esperando.
     * Cuando un hilo despierta de la cola de espera, llama recursivamente a la
     * función, sin intentar tomar el mutex y con la transición correspondiente como
     * parámetro.
     * 
     * @param transitionIndex
     * @param wentToSleep
     */
    public void fire(int transitionIndex, boolean wentToSleep) {
        // Si entra desde la cola de entrada, intenta tomar el mutex.
        if (!wentToSleep) {
            try {
                // print thread id and what is trying to get the mutex
                mutex.acquire(); // si no lo puedo tomar me voy a la cola
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // si tengo el mutex puedo disparar
        canBeFired = petriNet.tryUpdateMarking(transitionIndex);

        if (canBeFired) {
            // Si puede dispararse, incrementa el número de transiciones disparadas
            numberOfTransitionsFired++;
            policy.increment(Constants.transitionIndexes[transitionIndex]);
            System.out.println("Thread " + Thread.currentThread().getId() + " fired "
                    + Constants.transitionIndexes[transitionIndex] + "\nNumber of transitions fired: "
                    + numberOfTransitionsFired);
            if (numberOfTransitionsFired == 1000) {
                finalized = true;
                return;
            }
            int[][] sensTransitions = petriNet.getSensTransitions();
            // Disparo el hilo que pertenezca al invariante con menor promedio de disparos
            int transitionToWakeUp = policy.whoToFire(sensTransitions, waitingThreads);

            if (transitionToWakeUp != -1) {
                waitingThreads[transitionToWakeUp]--;
                transitionQueues[transitionToWakeUp].release();
                return;
            }

            // si no hay hilos esperando y que puedan ser disparados, libero el mutex
            mutex.release();
            return;
            // si entra al else es porque no se puede disparar, adentro se chequea si tiene
            // que dormir o ir a la cola de espera
        } else {
            try {

                // ************************ Caso 1: tiene que dormir
                if (petriNet.sleepingThreads[transitionIndex] > 0) {

                    int[][] sensTransitions = petriNet.getSensTransitions();
                    int transitionToWakeUp = policy.whoToFire(sensTransitions, waitingThreads);
                    if (transitionToWakeUp != -1) {
                        waitingThreads[transitionToWakeUp]--;
                        transitionQueues[transitionToWakeUp].release();
                    } else {
                        mutex.release();
                    }
                    long timeToSleep = petriNet.howMuchToSleep(transitionIndex);
                    // print id of the current thread
                    System.out.println("Thread " + Thread.currentThread().getId() + " tried to fire "
                            + Constants.transitionIndexes[transitionIndex] + " and is going to sleep for " + timeToSleep
                            + " ms");
                    Thread.sleep(timeToSleep);
                    petriNet.sleepingThreads[transitionIndex] = 0;
                    System.out.println("Thread " + Thread.currentThread().getId() + " woke up from sleeping");

                    /*
                     * waitingThreads[transitionIndex]++;
                     * 
                     * System.out.println("Waiting threads:" + Arrays.toString(waitingThreads));
                     * 
                     * transitionQueues[transitionIndex].acquire();
                     */
                    fire(transitionIndex, false); // ver flag wentToSleep

                    // ************************ Caso 2: no está sensibilizada
                } else if (!(petriNet.isTransitionValid(transitionIndex))) {
                    System.out.println("Thread " + Thread.currentThread().getId() + " tried to fire "
                            + Constants.transitionIndexes[transitionIndex] + " but it's not sensibilized");
                    waitingThreads[transitionIndex]++; // incremento la cantidad de hilos esperando
                    mutex.release();
                    transitionQueues[transitionIndex].acquire(); // cola de espera de recursos
                    // Cuando despierta, se llama recursivamente, sin intentar tomar el mutex y con
                    // la transición correspondiente.
                    fire(transitionIndex, true);

                    // ************************ Caso 3: ya hay alguien esperando o se pasó de la
                    // ventana temporal
                } /*
                   * else {
                   * // print thread id and what transtion it tried to fire
                   * System.out.println("Thread " + Thread.currentThread().getId() +
                   * " tried to fire "
                   * + Constants.transitionIndexes[transitionIndex] + " but it's not valid");
                   * mutex.release();
                   * fire(transitionIndex, false);
                   * }
                   */
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean isFinalized() {
        return finalized;
    }
    // ⢸⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⡷⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
    // ⢸⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠢⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
    // ⢸⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠈⠑⢦⡀⠀⠀⠀⠀⠀
    // ⢸⠀⠀⠀⠀⢀⠖⠒⠒⠒⢤⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠙⢦⡀⠀⠀⠀⠀
    // ⢸⠀⠀⣀⢤⣼⣀⡠⠤⠤⠼⠤⡄⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠙⢄⠀⠀⠀⠀
    // ⢸⠀⠀⠑⡤⠤⡒⠒⠒⡊⠙⡏⠀⢀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠑⠢⡄⠀
    // ⢸⠀⠀⠀⠇⠀⣀⣀⣀⣀⢀⠧⠟⠁⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀
    // ⢸⠀⠀⠀⠸⣀⠀⠀⠈⢉⠟⠓⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸
    // ⢸⠀⠀⠀⠀⠈⢱⡖⠋⠁⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸
    // ⢸⠀⠀⠀⠀⣠⢺⠧⢄⣀⠀⠀⣀⣀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸
    // ⢸⠀⠀⠀⣠⠃⢸⠀⠀⠈⠉⡽⠿⠯⡆⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸
    // ⢸⠀⠀⣰⠁⠀⢸⠀⠀⠀⠀⠉⠉⠉⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸
    // ⢸⠀⠀⠣⠀⠀⢸⢄⠀⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸
    // ⢸⠀⠀⠀⠀⠀⢸⠀⢇⠀⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸
    // ⢸⠀⠀⠀⠀⠀⡌⠀⠈⡆⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸
    // ⢸⠀⠀⠀⠀⢠⠃⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸
    // ⢸⠀⠀⠀⠀⢸⠀⠀⠀⠁⠀⠀⠀⠀⠀⠀⠀⠷

    public void fire2(int transitionIndex, Boolean hasMutex) {
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
            fire2(transitionIndex, true);
        }

        // Si esta sensibilizada reviso si esta en la ventana de disparo
        long ms = petriNet.timeToWindow(transitionIndex);

        // Se paso de la ventana temporal
        if (ms == -1) {
            System.out.println("Out of window. Timestamp is bigger than beta.");
            mutex.release();
        }
        // Se puede disparar
        else if (ms == 0) {
            int[][] fireSequence = new int[1][Constants.TRANSITIONS_COUNT];
            fireSequence[0][transitionIndex] = 1;
            int[][] oldSensTransitions = petriNet.getSensTransitions();
            petriNet.updateMarking(fireSequence);
            int[][] newSensTransitions = petriNet.getSensTransitions();

            for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
                if (PetriNet.timeSensitiveTransitions.containsKey(i)) {
                    if (oldSensTransitions[0][i] == 0 && newSensTransitions[0][i] == 1)
                        petriNet.setNewTimeStamp(i);
                }
            }

            numberOfTransitionsFired++;
            policy.increment(Constants.transitionIndexes[transitionIndex]);
            System.out.println("Thread " + Thread.currentThread().getId() + " fired "
                    + Constants.transitionIndexes[transitionIndex] + "\nNumber of transitions fired: "
                    + numberOfTransitionsFired);
            if (numberOfTransitionsFired == 1000) {
                finalized = true;
                return;
            }
            int[][] sensTransitions = petriNet.getSensTransitions();
            // Disparo el hilo que pertenezca al invariante con menor promedio de disparos
            int transitionToWakeUp = policy.whoToFire(sensTransitions, waitingThreads);

            if (transitionToWakeUp != -1) {
                waitingThreads[transitionToWakeUp]--;
                transitionQueues[transitionToWakeUp].release();
                return;
            }

            // si no hay hilos esperando y que puedan ser disparados, libero el mutex
            mutex.release();
            return;
        }
        // Tiene que dormir
        else {
            try {
                // Avisa que esta esperando la ventana temporal
                petriNet.sleepingThreads[transitionIndex] = 1;

                // Libera el mutex y se va a dormir
                mutex.release();
                Thread.sleep(ms);

                // Cuando se despierta vuelve a intentar disparar
                mutex.acquire();
                petriNet.sleepingThreads[transitionIndex] = 0;

                fire2(transitionIndex, true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}