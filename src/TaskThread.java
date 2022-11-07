public class TaskThread implements Runnable {
    //los hilos son los "caminos"
    int[] fireSequence;
    Monitor monitor;
    private int threadId;

    public TaskThread(int[] fireSequence, Monitor monitor, int threadId) {
        this.fireSequence = fireSequence;
        this.monitor = monitor;
        this.threadId = threadId;
    }

    @Override
    public void run(){
        try {
            monitor.enterMonitor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } 
    }

    /**
     * Devuelve el id del hilo
     * @return the fireSequence
     */
    public int getId() {
        return threadId;
    }

    /** Devuelve la secuencia de disparo del hilo 
     * @return the fireSequence
     */
    public int[] getFireSequence() {
        return fireSequence;
    }
}


