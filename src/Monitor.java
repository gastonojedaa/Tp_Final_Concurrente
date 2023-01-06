import java.util.concurrent.Semaphore;
import utils.Constants;

public class Monitor {

    private Semaphore[] transitionQueues;// TODO Revisar esto
    private Semaphore mutex;
    private PetriNet petriNet;
    private int transitionToBeFired;
    private int numberOfTransitionsFired;
    private Boolean finalized;

    // k del video de mico
    private Boolean canBeFired;

    public Monitor() {
        petriNet = new PetriNet(Constants.INCIDENCE_MATRIX, Constants.BACKWARD_MATRIX, Constants.INITIAL_MARKING);
        numberOfTransitionsFired = 0;
        finalized = false;

        // mutex del monitor
        mutex = new Semaphore(1);

        // colas de transiciones
        transitionQueues = new Semaphore[Constants.TRANSITIONS_COUNT];
        for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
            transitionQueues[i] = new Semaphore(0);
        }
    }

    public void fire(int transitionIndex) {

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
            if (finalized)
                return;
            canBeFired = petriNet.tryUpdateMarking(transitionToBeFired);

            if (!canBeFired) {
                mutex.release();
                try {
                    transitionQueues[transitionToBeFired].acquire();
                    if (finalized)
                        return;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            else {
                numberOfTransitionsFired++;
                if (numberOfTransitionsFired == 50)
                    finalized = true;
                // System.out.println("Disparo transicion T" +
                // Constants.transitionIndexes[transitionToBeFired]);
                // System.out.println("Transiciones disparadas: " + numberOfTransitionsFired);
                // System.out.println("-------------------------------------------------------");
                // Que transiciones estan sensibilizadas
                int[][] sensTransitions = petriNet.getSensTransitions();

                int[] queuesLength = new int[Constants.TRANSITIONS_COUNT];
                for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
                    queuesLength[i] = transitionQueues[i].getQueueLength();
                }
                System.out.println("Colas de transiciones: " + java.util.Arrays.toString(queuesLength));

                // De estas transiciones, cual tiene hilos esperando
                for (int i = 0; i < sensTransitions.length; i++) {
                    if (sensTransitions[0][i] == 1 && transitionQueues[i].hasQueuedThreads()) {
                        // Disparo el primer hilo que este esperando //TODO Usar politicas
                        transitionToBeFired = i;
                        transitionQueues[i].release();
                        return;
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
    }

    public Boolean isFinalized() {
        return finalized;
    }
}
