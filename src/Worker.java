public class Worker implements Runnable {
    
    private int index = 0;
    //private Boolean working = true;
    private int[] transitionsIndex;

    public Worker(int[] _transitionsIndex) {
        transitionsIndex = _transitionsIndex;
    }

    @Override
    public void run() {
        while (true) {
            Monitor.getInstance().fire(transitionsIndex[index], false);            
            //Monitor.getInstance().fire(transitionsIndex[index]);
            index = index == 2 ? 0 : index + 1;
        }
    }
}