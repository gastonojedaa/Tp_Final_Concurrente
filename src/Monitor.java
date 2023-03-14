import java.util.concurrent.Semaphore;
import utils.Constants;


public class Monitor {
    private static Monitor instance = null;
    private static Policy policy = Policy.getInstance();
    private static Semaphore[] transitionQueues;
    private static WaitingQueues waitingThreads;
    //private static WaitingQueues sleepingThreads;
    private static Semaphore mutex;
    private static PetriNet petriNet;
    private static int numberOfTransitionsFired;
    private static Boolean finalized;
    private Boolean canBeFired; // k del video de mico
                 

    private Monitor() {
    }

    public static synchronized Monitor getInstance() {
        if (Monitor.instance == null) {
            Monitor.instance = new Monitor();
            Monitor.petriNet = new PetriNet(Constants.INCIDENCE_MATRIX, Constants.BACKWARD_MATRIX,
                    Constants.INITIAL_MARKING, Constants.ALPHA, Constants.BETA);
            Monitor.waitingThreads = new WaitingQueues(Constants.TRANSITIONS_COUNT);
            //Monitor.sleepingThreads = new WaitingQueues(Constants.TRANSITIONS_COUNT);
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
 * Dispara la transición indicada por el índice. Si no puede dispararse, ingresa a una cola de espera, donde cada elemento del arreglo corresponde a una transición.
 * En el caso en el que sí puede dispararse, actualiza el marcado de la red y dispara la primera transición que tenga hilos esperando.
 * Cuando un hilo despierta de la cola de espera, llama recursivamente a la función, sin intentar tomar el mutex y con la transición correspondiente como parámetro.
 * @param transitionIndex
 * @param wentToSleep
 */
    public void fire(int transitionIndex, boolean wentToSleep) {
        //Si entra desde la cola de entrada, intenta tomar el mutex.
        if (!wentToSleep) {
            try {
                mutex.acquire(); // si no lo puedo tomar me voy a la cola
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        // si tengo el mutex puedo disparar
        canBeFired = petriNet.tryUpdateMarking(transitionIndex);

        if (canBeFired) {
            //Si puede dispararse, incrementa el número de transiciones disparadas
            numberOfTransitionsFired++;
            //print thread id and what fired
            System.out.println("Thread " + Thread.currentThread().getId() + " fired " + Constants.transitionIndexes[transitionIndex]);
            policy.increment(Constants.transitionIndexes[transitionIndex]);
            System.out.println("Number of transitions fired = " + numberOfTransitionsFired);
            if (numberOfTransitionsFired == 100){
                finalized = true;
                return;
            }
            int[][] sensTransitions = petriNet.getSensTransitions();
            // De estas transiciones, cual tiene hilos esperando
            for (int i = 0; i < waitingThreads.getQueue().length; i++) {
                if (sensTransitions[0][i] == 1 && waitingThreads.getQueue()[i] > 0) {
                    // Disparo el hilo que pertenezca al invariante con menor promedio de disparos
                    i = policy.whoToFire(sensTransitions, waitingThreads.getQueue());
                    waitingThreads.increment(transitionIndex);
                    transitionQueues[i].release();
                    return;
                }
            }   
            //si no hay hilos esperando y que puedan ser disparados, libero el mutex
            mutex.release();
            return;
            //si entra al else es porque no se puede disparar, adentro se chequea si tiene que dormir o ir a la cola de espera
        }else{
            //Si no puede dispararse, se va a la cola de espera.
            try {
                if(petriNet.sleepingThreads[transitionIndex] > 0){
                    mutex.release();
                    long timeToSleep = petriNet.howMuchToSleep(transitionIndex);
                    // print id of the current thread
                    System.out.println("Thread " + Thread.currentThread().getId() + " tried to fire " + Constants.transitionIndexes[transitionIndex] + " and is going to sleep for " + timeToSleep + " ms");                    
                    Thread.sleep(timeToSleep); 
                    petriNet.sleepingThreads[transitionIndex] = 0;
                    System.out.println("Thread "+ Thread.currentThread().getId() + " woke up from sleeping");
                    
                    fire(transitionIndex, true); // ver flag wentToSleep
                }else{
                    waitingThreads.decrement(transitionIndex);
                    mutex.release();
                    //como transitionIndex es un parámetro con el que se llama a la función, este es propio de cada hilo, ya no se sobreescribe una variable como ocurría antes.
                    transitionQueues[transitionIndex].acquire(); // cola de espera de recursos
                    //Cuando despierta, se llama recursivamente, sin intentar tomar el mutex y con la transición correspondiente.
                    fire(transitionIndex, true);
                }
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
}