import utils.Constants;
import utils.Matrix;

import java.util.Arrays;

public class Main {
    public static void main(String[] args){
        System.out.println(Arrays.toString(Matrix.transpose(Constants.INCIDENCE_MATRIX)[0]));
    }
}

