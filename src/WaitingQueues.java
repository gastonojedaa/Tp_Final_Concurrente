

public class WaitingQueues {
    private static int[] queue; // transiciones esperando entrar en la ventana temporal
    
    public WaitingQueues(int size) {
        queue = new int[size];
    }
    
    public void increment(int transition) {
        queue[transition]++;
    }

    public void decrement(int transition) {
        queue[transition]--;
    }

    public int[] getQueue() {
        return queue;
    }
}
