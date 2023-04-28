import utils.Constants;
import utils.ConcurrentLogger;
//import utils.ConcurrentLogger;

public class Main {
    public static void main(String[] args) {

        // Iniciar hilo dedicado al Logger
        ConcurrentLogger logger = ConcurrentLogger.getInstance();
        Thread loggerThread = new Thread(logger);
        loggerThread.start();
        // save start time
        long start_time = System.currentTimeMillis();

        Thread[] threads = new Thread[Constants.THREADS_COUNT];

        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(new Worker(Constants.T_INVARIANT_1));
            threads[i].start();
        }

        for (int i = 3; i < 6; i++) {
            threads[i] = new Thread(new Worker(Constants.T_INVARIANT_2));
            threads[i].start();
        }

        for (int i = 6; i < 9; i++) {
            threads[i] = new Thread(new Worker(Constants.T_INVARIANT_3));
            threads[i].start();
        }

        for (int i = 9; i < 12; i++) {
            threads[i] = new Thread(new Worker(Constants.T_INVARIANT_4));
            threads[i].start();
        }

        while (!Monitor.getInstance().isFinalized()) {
        }

        printExecutionTime(start_time);

        printResults();

        System.out.println("Finalizado");
        System.exit(0);
    }

    private static void printExecutionTime(long start_time) {
        // save end time
        long end_time = System.currentTimeMillis();
        // calculate execution time
        long execution_time = end_time - start_time;
        System.out.println("--------------------------------------------");
        System.out.println("Tiempo de ejecucion: " + execution_time + " ms");
        System.out.println("--------------------------------------------");
    }

    private static void printResults() {
        System.out.println("Resultados:");
        System.out.println("--------------------------------------------");
        System.out.println("Total de transiciones disparadas : " + Monitor.numberOfTransitionsFired);
        System.out.println("--------------------------------------------");
        System.out.println("Total de invariantes ejecutados: " + Policy.totalInvariantsFired);
        System.out.println("--------------------------------------------");
        System.out.println("Contador inv 1: " + Policy.counters[0] + " \nContador inv 2: " +
                Policy.counters[1] + " \nContador inv 3: "
                + Policy.counters[2] + " \nContador inv 4: " + Policy.counters[3]);
        System.out.println("--------------------------------------------");
    }
}