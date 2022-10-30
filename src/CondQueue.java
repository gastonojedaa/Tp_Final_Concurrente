import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import utils.Matrix;

public class CondQueue {
    private int size;
    private ArrayList<Semaphore> condQueue;

    public CondQueue(int size) {
        this.size = size;
        this.condQueue = new ArrayList<Semaphore>(size);
        for (int i = 0; i < size; i++) {
            this.condQueue.add(new Semaphore(0)); /*
                                                   * A counting semaphore. Conceptually, a semaphore maintains a set of
                                                   * permits. Each acquire() blocks if necessary until a permit is
                                                   * available, and then takes it. Each release() adds a permit,
                                                   * potentially releasing a blocking acquirer. However, no actual
                                                   * permit objects are used; the Semaphore just keeps a count of the
                                                   * number available and acts accordingly.
                                                   */
        }
    }

    /**
     * 
     * @return la cola de transiciones (semáforos)
     */
    public ArrayList<Semaphore> getCondQueue() {
        return this.condQueue;
    }

    /**
     * @param fireVector
     * @return indice de la transición que se busca disparar
     */
    public int getQueuePosition(int[][] fireVector) {
        int index = 0;

        for (int i = 0; i < fireVector[0].length; i++) {
            if (fireVector[0][i] == 1)
                break;
            else
                index++;
        }
        return index;
    }

    /**
     * 
     * @return un vector con aquellas transiciones que tienen hilos encolados
     *  ...
     */
    public int[][] getWaitingQueue(){
        int[][] waitingQueue = new int[0][size];
        for(Semaphore queue : condQueue){
            if(queue.hasQueuedThreads()){  //Queries whether any threads are waiting to acquire. (método de Semaphore)
                waitingQueue[0][condQueue.indexOf(queue)] = 1;
            }
            else{
                waitingQueue[0][condQueue.indexOf(queue)] = 0;
            }
        }
        return waitingQueue;
    }
}
