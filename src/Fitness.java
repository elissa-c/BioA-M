public class Fitness {
    private int[][] distanceMatrix;
    private int [][] flowMatrix;
    public Fitness(int[][] flow, int[][] distance){
        flowMatrix = flow.clone();
        distanceMatrix = distance.clone();
    }
    int calculateCost(int[] assignment, int fac, int loc) {
        int cost = 0;
        for (int c = 0; c < assignment.length; c++) {
            if (assignment[c] != -1) {
                cost += flowMatrix[fac][assignment[c]] * distanceMatrix[loc][c];
                cost += flowMatrix[assignment[c]][fac] * distanceMatrix[c][loc];
            }
        }
        return cost;
    }

    int calculateCost(int[] assignment) {
        int cost = 0;
        for (int c = 0; c < assignment.length; c++) {
            for (int d=0; d<assignment.length; d++) {
                cost += flowMatrix[c][d] * distanceMatrix[assignment[c]][assignment[d]];
            }
        }
        return cost;
    }
}
