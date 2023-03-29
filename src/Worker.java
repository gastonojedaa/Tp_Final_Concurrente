public class Worker implements Runnable {

    private int index;
    private int[] transitionsIndex;
    private Monitor monitor;

    public Worker(int[] _transitionsIndex) {
        index = 0;
        transitionsIndex = _transitionsIndex;
        monitor = Monitor.getInstance();
    }

    @Override
    public void run() {
        while (true) {
            monitor.fire(transitionsIndex[index], false);
            index = (index + 1) % 3;
        }
    }
}