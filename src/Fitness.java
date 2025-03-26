public class Fitness {
    static int calculateCost(int[] assignment, int fac, int loc, int[][] flow, int[][] distance) {
        int cost = 0;
        for (int c = 0; c < assignment.length; c++) {
            if (assignment[c] != -1) {
                cost += flow[fac][assignment[c]] * distance[loc][c];
                cost += flow[assignment[c]][fac] * distance[c][loc];
            }
        }
        return cost;
    }

    static int calculateCost(int[] assignment, int[][] flow, int[][] distance) {
        int cost = 0;
        for (int c = 0; c < assignment.length; c++) {
            for (int d=0; d<assignment.length; d++) {
                cost += flow[c][d] * distance[assignment[c]][assignment[d]];
            }
        }
        return cost;
    }
}
