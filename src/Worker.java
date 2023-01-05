public class Worker implements Runnable {

    private int index;
    private int[] transitionsIndex;
    private int transitionsFired;;
    private Monitor monitor;

    public Worker(Monitor _monitor, int[] _transitionsIndex) {
        monitor = _monitor;
        transitionsIndex = _transitionsIndex;
        index = 0;
        transitionsFired = 0;
    }

    @Override
    public void run() {
        while (transitionsFired < 10) {
            monitor.fire(transitionsIndex[index]);
            index = (index + 1) % transitionsIndex.length;
            transitionsFired++;
        }
    }
}
