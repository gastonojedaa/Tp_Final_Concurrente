import java.util.concurrent.Semaphore;
import utils.Constants;

public class Monitor {

    private CondQueue condQueue;
    private Semaphore mutex;
    private PetriNet petriNet;

    //constructor
    public Monitor(PetriNet petriNet) {
        this.petriNet = petriNet;
        inQueue = new Semaphore(1, true);
        conditionQueue = new CondQueue(TRANSITIONS_COUNT);
        
    }
    

}
