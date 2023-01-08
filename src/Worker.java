import utils.Constants;

public class Worker implements Runnable {

    private int index;
    private int[] transitionsIndex;
    private Monitor monitor;
    private Boolean working;

    public Worker(Monitor _monitor, int[] _transitionsIndex) {
        monitor = _monitor;
        transitionsIndex = _transitionsIndex;
        index = 0;
        working = true;
    }

    @Override
    public void run() {
        while (working) {
            working = monitor.fire(transitionsIndex[index]);
            index = (index + 1) % transitionsIndex.length;
        }
        System.out.println("Salí sin querer");
    }
}
