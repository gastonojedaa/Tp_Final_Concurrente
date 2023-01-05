import java.util.concurrent.Semaphore;
import utils.Constants;

public class Monitor {

    private Semaphore[] transitionQueues;// TODO Revisar esto
    private Semaphore mutex;
    private PetriNet petriNet;
    private int transitionToBeFired;

    // k del video de mico
    private Boolean canBeFired;

    public Monitor() {
        petriNet = new PetriNet(Constants.INCIDENCE_MATRIX, Constants.BACKWARD_MATRIX, Constants.INITIAL_MARKING);

        // mutex del monitor
        mutex = new Semaphore(1);

        // colas de transiciones
        transitionQueues = new Semaphore[Constants.TRANSITIONS_COUNT];
        for (int i = 0; i < Constants.TRANSITIONS_COUNT; i++) {
            transitionQueues[i] = new Semaphore(3);// TODO Reemplazar 3 por constante
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
            canBeFired = petriNet.tryUpdateMarking(transitionToBeFired);

            if (!canBeFired) {
                mutex.release();
                try {
                    transitionQueues[transitionToBeFired].wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            else {

                // Que transiciones estan sensibilizadas
                int[][] sensTransitions = petriNet.getSensTransitions();

                // De estas transiciones, cual tiene hilos esperando
                for (int i = 0; i < sensTransitions.length; i++) {
                    if (sensTransitions[0][i] == 1 && transitionQueues[i].hasQueuedThreads()) {
                        // Disparo el primer hilo que este esperando //TODO Usar politicas
                        transitionToBeFired = i;
                        transitionQueues[i].notify();
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
}
