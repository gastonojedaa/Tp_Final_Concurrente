import java.util.ArrayList;
import java.util.Arrays;

import utils.Constants;

public class Policy {
    private static Policy instance = null;
/*     private int invariant_1_counter = 0;
    private int invariant_2_counter = 0;
    private int invariant_3_counter = 0;
    private int invariant_4_counter = 0; */
    private static int index;
    private static int transitionInv;
    private static int[] counters = new int[4];
    private static Boolean[] invariants = new Boolean[4];
    private int fireableTransitions[] = new int[Constants.TRANSITIONS_COUNT];
    private static ArrayList<Integer> transInv1;
    private static ArrayList<Integer> transInv2;
    private static ArrayList<Integer> transInv3;
    private static ArrayList<Integer> transInv4;

    private Policy(){

    }

    public static Policy getInstance(){
        if(Policy.instance == null){
            Policy.instance = new Policy();
            for(int i = 0; i < 4; i++){
                Policy.invariants[i] = false;
                Policy.counters[i] = 0;
                Policy.index = 0;
                transInv1 = new ArrayList<>(Arrays.asList(1, 2, 3));
                transInv2 = new ArrayList<>(Arrays.asList(4, 5, 6));
                transInv3 = new ArrayList<>(Arrays.asList(13, 14, 15));
                transInv4 = new ArrayList<>(Arrays.asList(16, 17, 18));
            }
        }
        return Policy.instance;
    }

    public void increment(int transitionIndex){
        transitionInv = whatInvIs(transitionIndex);
        counters[transitionInv]++;
        System.out.println("Contador 1: " + counters[0] + " Contador 2: " + counters[1] + " Contador 3: " + counters[2] + " Contador 4: " + counters[3]);
    }

    static int whatInvIs(int trans){ // codigo pro LGBTQIA+ 
        if (transInv1.contains(trans))
            return 0;
        else if (transInv2.contains(trans))
            return 1;
        else if (transInv3.contains(trans))
            return 2;
        else
            return 3;
    }

    public int whoToFire(int[][] sensTransitions, int[] waitingThreads){
        // reset invariants
        for(int i = 0; i < invariants.length; i++){
            invariants[i] = false;
        }
        // reset fireableTransitions
        for(int i = 0; i < fireableTransitions.length; i++){
            fireableTransitions[i] = 0;
        }        

        for(int i = 0; i < sensTransitions[0].length; i++){
            if(sensTransitions[0][i] == 1 && waitingThreads[i] > 0){
                fireableTransitions[i] = 1;
                transitionInv = whatInvIs(i);
                invariants[transitionInv] = true;
            }
        }

       /*  int min = 0;
        for(int i = 0; i < 4; i++){
            if(invariants[i] == true){
                if(counters[i] < counters[min]){
                    min = i;
                }
            }
        }  */
        /* int min = 0;
        for(int i = 0; i < fireableTransitions.length; i++){
            if(fireableTransitions[i] == 1){
                if(counters[whatInvIs(i)] < counters[min]){
                    min = counters[i];
                }
            }
        } */
        
        System.out.println("Contador 1: " + counters[0] + " \nContador 2: " + counters[1] + " \nContador 3: " + counters[2] + " \nContador 4: " + counters[3]);
       // System.out.println("Contador min: " + min);
        System.out.println("FireableTransitions: " + java.util.Arrays.toString(fireableTransitions));
        
 
        // choose index of fireable transition which has smaller counter value
        int aux = 0;
        int temp = Integer.MAX_VALUE;
        for(int i = 0; i < fireableTransitions.length; i++){
            if(fireableTransitions[i] == 1){
                if(counters[whatInvIs(Constants.transitionIndexes[i])] < temp){  
                    System.out.println("Contador mas chico que encontré: " + counters[whatInvIs(Constants.transitionIndexes[i])] + " que pertenece a "+ (whatInvIs(Constants.transitionIndexes[i])+1) + " en la pos " + i);
                    temp = counters[whatInvIs(Constants.transitionIndexes[i])];
                    aux = i;
                    index = aux;
                } // ver caso donde la transicion disparable no coincide con el invariante con menos disparos
                else{
                    System.out.println("Entré al else con: " + (counters[whatInvIs(Constants.transitionIndexes[i])] + " que pertenece a "+ whatInvIs(Constants.transitionIndexes[i])+1) + " en la pos " + i);
                    index = aux;
                }
            }
        } 
        System.out.println("Index: " + index);
        return index;
    }
}
