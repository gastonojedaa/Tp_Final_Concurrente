import utils.Constants;

public class Worker implements Runnable {

    private int index;
    private int[] transitionsIndex;
    private Monitor monitor;

    public Worker(Monitor _monitor, int[] _transitionsIndex) {
        monitor = _monitor;
        transitionsIndex = _transitionsIndex;
        index = 0;
    }

    @Override
    public void run() {
        while (!monitor.isFinalized()) {
            monitor.fire(transitionsIndex[index]);
            System.out.println(
                    "Disparo transicion T" + Constants.transitionIndexes[transitionsIndex[index]] + " desde el thread "
                            + Thread.currentThread().getId());
            index = (index + 1) % transitionsIndex.length;
        }
    }
}
