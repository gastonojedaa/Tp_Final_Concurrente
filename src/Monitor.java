import java.util.concurrent.Semaphore;
import utils.Constants;
import java.util.Collection;

public class Monitor {

    private Semaphore executeTask;
    private Semaphore enterMonitor;
    private PetriNet petriNet;
    private int waitingQueueLength;

    // constructor
    public Monitor(PetriNet petriNet) {
        this.petriNet = petriNet;
        executeTask = new Semaphore(1, true); // acquire executeTask
        // fairness is set to true to avoid starvation of threads in the queue
        enterMonitor = new Semaphore(1, true);
    }

    public PetriNet getPetriNet() {
        return petriNet;
    }

    /***
     * Este método es llamado por los hilos que quieren entrar al monitor. Toma como
     * parámetro el hilo.
     * 
     * @param thread
     * @throws InterruptedException
     */
    public void enterMonitor(TaskThread thread) throws InterruptedException {
        waitingQueueLength = executeTask.getQueueLength(); // hilos que ya están adentro del monitor, pero no terminaron
                                                           // su ejecución
        try {
            enterMonitor.acquire(); // Entrada al monitor. Si no lo puede tomar, se coloca en la cola
            executeTask.acquire(); // Monitor.
            System.out.println("Thread " + thread.getId() + " entered monitor");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        execute(thread);
    }

    /**
     * 
     * @param thread
     * @throws InterruptedException
     */
    private void execute(TaskThread thread) throws InterruptedException {
        if (petriNet.isFireable(thread.getFireSequence())) { // Si la transición es disparable
            petriNet.fireTransition(thread.getFireSequence()); // Dispara la transición
            if (waitingQueueLength > 0) { // Si hay hilos en la cola
                executeTask.release(); // Libera el monitor a los que ya están adentro
                System.out.println("Thread " + thread.getId() + " released monitor");
            } else {
                enterMonitor.release(); // Libera la entrada al monitor
                System.out.println("Thread " + thread.getId() + " released monitor");
            }
        }
    }
}
