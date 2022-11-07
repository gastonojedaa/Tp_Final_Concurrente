import java.util.concurrent.Semaphore;
import utils.Constants;

public class Monitor {

    private CondQueue conditionQueue;
    private Semaphore mutex;
    private Semaphore entryQueue;
    private PetriNet petriNet;

    private boolean isInsideMonitor;

    // constructor
    public Monitor(PetriNet petriNet) {
        this.petriNet = petriNet;
        mutex = new Semaphore(1, true); // acquire Mutex
        // fairness is set to true to avoid starvation of threads in the queue
        entryQueue = new Semaphore(1, true);
        conditionQueue = new CondQueue(TRANSITIONS_COUNT);
    }

    public CondQueue getCondQueue() {
        return conditionQueue;
    }

    public PetriNet getPetriNet() {
        return petriNet;
    }

    public void enterMonitor(TaskThread thread) throws InterruptedException {
        try {
            mutex.acquire();
            System.out.println("Thread " + thread.getId() + " entered monitor");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // conditional fire transition
    public boolean tryFireTransition(int[][] fireSequence) throws InterruptedException {
        try {
            enterMonitor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (petriNet.updateMarking(fireSequence) == -1) {
            exitMonitor();
            int queuePosition = conditionQueue.getQueuePosition(fireSequence);
            try {
                conditionQueue.getCondQueue().get(queuePosition).acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
