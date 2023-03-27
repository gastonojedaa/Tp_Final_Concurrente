public class Worker implements Runnable {

    private int index = 0;
    // private Boolean working = true;
    private int[] transitionsIndex;
    private Monitor monitor;

    public Worker(int[] _transitionsIndex) {
        transitionsIndex = _transitionsIndex;
        monitor = Monitor.getInstance();
    }

    @Override
    public void run() {
        while (true) {
            monitor.fire2(transitionsIndex[index], false);
            index = index == 2 ? 0 : index + 1;// TODO Cambiar a modulo
        }
    }
}