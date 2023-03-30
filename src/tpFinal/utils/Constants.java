package tpFinal.utils;

public final class Constants {

        public static final int[][] INCIDENCE_MATRIX = new int[][] {
                        { -1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
                        { -1, 0, 0, 0, 0, 0, 0, 1, 0, -1, 1, 0 },
                        { 0, -1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 1, -1, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, -1, 0, 1, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 1, -1, 0, 0, 0, 0, 0 },
                        { 1, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0 },
                        { -1, -1, 1, 0, -1, 1, 0, 1, 0, -1, 1, 0 },
                        { -1, -1, 1, 0, 0, -1, 1, 1, 0, 0, -1, 1 },
                        { 0, 0, 0, 0, 0, 0, 0, -1, 1, -1, 1, 0 },
                        { 0, 0, -1, 1, -1, 1, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 0 },
                        { 0, -1, 1, 0, -1, 1, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 1 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1 } };

        public static final int[][] BACKWARD_MATRIX = new int[][] {
                        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
                        { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 },
                        { 1, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0 },
                        { 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0 },
                        { 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 },
                        { 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 },
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 } };

        public static final int[][] INITIAL_MARKING = { { 3, 2, 3, 0, 0, 3, 0, 0, 0, 3, 2, 1, 1, 0, 2, 3, 0, 0 } };

        public static final int P1_INDEX = 0;
        public static final int P100_INDEX = 1;
        public static final int P13_INDEX = 2;
        public static final int P14_INDEX = 3;
        public static final int P15_INDEX = 4;
        public static final int P16_INDEX = 5;
        public static final int P17_INDEX = 6;
        public static final int P18_INDEX = 7;
        public static final int P2_INDEX = 8;
        public static final int P200_INDEX = 9;
        public static final int P25_INDEX = 10;
        public static final int P26_INDEX = 11;
        public static final int P28_INDEX = 12;
        public static final int P3_INDEX = 13;
        public static final int P300_INDEX = 14;
        public static final int P4_INDEX = 15;
        public static final int P5_INDEX = 16;
        public static final int P6_INDEX = 17;

        public static final int PLACES_COUNT = INCIDENCE_MATRIX.length;
        public static final int TRANSITIONS_COUNT = INCIDENCE_MATRIX[0].length;
        public static final int THREADS_COUNT = 12;

        /*
         * public static final int T1_INDEX = 0;
         * public static final int T13_INDEX = 1;
         * public static final int T14_INDEX = 2;
         * public static final int T15_INDEX = 3;
         * public static final int T16_INDEX = 4;
         * public static final int T17_INDEX = 5;
         * public static final int T18_INDEX = 6;
         * public static final int T2_INDEX = 7;
         * public static final int T3_INDEX = 8;
         * public static final int T4_INDEX = 9;
         * public static final int T5_INDEX = 10;
         * public static final int T6_INDEX = 11;
         */

        public static final int[] transitionIndexes = { 1, 13, 14, 15, 16, 17, 18, 2, 3, 4, 5, 6 };

        public static final int[] T_INVARIANT_1 = { 0, 7, 8 };
        public static final int[] T_INVARIANT_2 = { 9, 10, 11 };
        public static final int[] T_INVARIANT_3 = { 1, 2, 3 };
        public static final int[] T_INVARIANT_4 = { 4, 5, 6 };

        public static final String LOG_FILE_PATH = "";
        public static final Boolean DEBUG = false;
        public static final Boolean CONSOLE_LOGGING = false;

        public static final int BASE_ALPHA = 5;

        public static final int[] ALPHA = { BASE_ALPHA, BASE_ALPHA, BASE_ALPHA, BASE_ALPHA };
        public static final int BETA = BASE_ALPHA * 100;
}
