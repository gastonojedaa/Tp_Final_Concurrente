import java.util.concurrent.Semaphore;
import utils.Constants;

public class Monitor {
    private static Monitor instance = null;
    private static Policy policy = Policy.getInstance();
    private static Semaphore[] transitionQueues;
    // private int[] transitionQueuesQuantity;
    private static Semaphore mutex;
    private static PetriNet petriNet;
    private static int[] waitingThreads;
    private static int numberOfTransitionsFired;
    private static Boolean finalized;
    // k del video de mico
    private Boolean canBeFired;

    private Monitor() {
    }

    public static synchronized Monitor getInstance() {
        if (Monitor.instance == null) {
            Monitor.instance = new Monitor();
            Monitor.petriNet = new PetriNet(Constants.INCIDENCE_MATRIX, Constants.BACKWARD_MATRIX,
                    Constants.INITIAL_MARKING);
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

/**
 * Dispara la transición indicada por el índice. Si no puede dispararse, ingresa a una cola de espera, donde cada elemento del arreglo corresponde a una transición.
 * En el caso en el que sí puede dispararse, actualiza el marcado de la red y dispara la primera transición que tenga hilos esperando.
 * Cuando un hilo despierta de la cola de espera, llama recursivamente a la función, sin intentar tomar el mutex y con la transición correspondiente como parámetro.
 * @param transitionIndex
 * @param wentToSleep
 */
    public void fire2(int transitionIndex, boolean wentToSleep) {
        //Si entra desde la cola de entrada, intenta tomar el mutex.
        if (!wentToSleep) {
            try {
                mutex.acquire(); // si no lo puedo tomar me voy a la cola
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //System.out.println("Current marking: " + java.util.Arrays.toString(petriNet.getCurrentMarking()[0]));
        System.out.println("Colas de condicion = " + java.util.Arrays.toString(waitingThreads));
        
        // si tengo el mutex puedo disparar
        canBeFired = petriNet.tryUpdateMarking(transitionIndex);

        if (canBeFired) {
            //Si puede dispararse, incrementa el número de transiciones disparadas
            numberOfTransitionsFired++;
           // print transition index;
            System.out.println("TransitionIndex = " + transitionIndex);
            System.out.println("Transición que se va a disparar = " + Constants.transitionIndexes[transitionIndex]);
            policy.increment(Constants.transitionIndexes[transitionIndex]);
            System.out.println("Number of transitions fired = " + numberOfTransitionsFired);
            if (numberOfTransitionsFired == 15){
                finalized = true;
                return;
            }
            int[][] sensTransitions = petriNet.getSensTransitions();
            System.out.println("Transiciones sens  = "+ java.util.Arrays.toString(sensTransitions[0]));
            // De estas transiciones, cual tiene hilos esperando
            for (int i = 0; i < waitingThreads.length; i++) {
                if (sensTransitions[0][i] == 1 && waitingThreads[i] > 0) {
                    // Disparo el primer hilo que este esperando //TODO Usar politicas
                    //System.out.println("Thread ID que quiere despertar un hilo: " + Thread.currentThread().getId());
                    i = policy.whoToFire(sensTransitions, waitingThreads);
                    waitingThreads[i]--;
                    transitionQueues[i].release();
                    return;
                }
            }
            //si no hay hilos esperando y que puedan ser disparados, libero el mutex
            //System.out.println("Thread ID de hilo que no pudo despertar a nadie: " + Thread.currentThread().getId());
            mutex.release();
            return;
        } else {
            //Si no puede dispararse, se va a dormir.
            try {
                //System.out.println("Thread " + Thread.currentThread().getName() + " quiere disparar " + transitionIndex);   
                waitingThreads[transitionIndex]++;
                mutex.release();
                //System.out.println("Thread ID de hilo que se fue a dormir: " + Thread.currentThread().getId());
                transitionQueues[transitionIndex].acquire();
                //Cuando despierta, se llama recursivamente, sin intentar tomar el mutex y con la transición correspondiente.
                fire2(transitionIndex, true);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Boolean isFinalized() {
        return finalized;
    }
}