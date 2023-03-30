package tpFinal;

import tpFinal.utils.Constants;
import tpFinal.utils.ConcurrentLogger;
//import utils.ConcurrentLogger;

public class Main {
    public static void main(String[] args) {

        // Iniciar hilo dedicado al Logger
        ConcurrentLogger logger = ConcurrentLogger.getInstance();
        Thread loggerThread = new Thread(logger);
        loggerThread.start();

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

        try {
            loggerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Finalizado");
    }
}