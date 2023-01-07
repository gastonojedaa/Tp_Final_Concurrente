import utils.Constants;

public class Main {
    public static void main(String[] args) {

        Monitor monitor = new Monitor();

        Thread[] threads = new Thread[Constants.THREADS_COUNT];

        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(new Worker(monitor, Constants.T_INVARIANT_1));
            threads[i].start();
        }

        for (int i = 3; i < 6; i++) {
            threads[i] = new Thread(new Worker(monitor, Constants.T_INVARIANT_2));
            threads[i].start();
        }

        for (int i = 6; i < 9; i++) {
            threads[i] = new Thread(new Worker(monitor, Constants.T_INVARIANT_3));
            threads[i].start();
        }

        for (int i = 9; i < 12; i++) {
            threads[i] = new Thread(new Worker(monitor, Constants.T_INVARIANT_4));
            threads[i].start();
        }

        while (!monitor.isFinalized()) {
        }

        System.out.println("Finalizado");

    }
}