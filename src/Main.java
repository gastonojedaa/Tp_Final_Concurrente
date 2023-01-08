import utils.Constants;

public class Main {
    public static void main(String[] args) {
        Thread[] threads = new Thread[Constants.THREADS_COUNT];

        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(new Worker(Monitor.getNewInstance(), Constants.T_INVARIANT_1));
            threads[i].start();
        }

        for (int i = 3; i < 6; i++) {
            threads[i] = new Thread(new Worker(Monitor.getNewInstance(), Constants.T_INVARIANT_2));
            threads[i].start();
        }

        for (int i = 6; i < 9; i++) {
            threads[i] = new Thread(new Worker(Monitor.getNewInstance(), Constants.T_INVARIANT_3));
            threads[i].start();
        }

        for (int i = 9; i < 12; i++) {
            threads[i] = new Thread(new Worker(Monitor.getNewInstance(), Constants.T_INVARIANT_4));
            threads[i].start();
        }

        while (!Monitor.getNewInstance().isFinalized()) {
        }

        System.out.println("Finalizado");

    }
}