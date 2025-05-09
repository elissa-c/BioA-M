import java.util.List;

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

    int calculateCost(List<Integer> assignment) {
        int cost = 0;
        for (int c = 0; c < assignment.size(); c++) {
            for (int d=0; d<assignment.size(); d++) {
                cost += flowMatrix[c][d] * distanceMatrix[assignment.get(c)][assignment.get(d)];
            }
        }
        return cost;
    }
    int calculateCost(List<Integer> assignment, int fac, int loc) {
        int cost = 0;
        for (int c = 0; c < assignment.size(); c++) {
            if (assignment.get(c) != -1) {
                cost += flowMatrix[fac][assignment.get(c)] * distanceMatrix[loc][c];
                cost += flowMatrix[assignment.get(c)][fac] * distanceMatrix[c][loc];
            }
        }
        return cost;
    }

    int computeDelta(int[] solution, int i, int j) {
        int delta = 0;
        int pi_i = solution[i];
        int pi_j = solution[j];

        for (int k = 0; k < solution.length; k++) {
            if (k == i || k == j) continue;
            int pi_k = solution[k];
            delta +=
                    flowMatrix[i][k] * (distanceMatrix[pi_j][pi_k] - distanceMatrix[pi_i][pi_k]) +
                            flowMatrix[j][k] * (distanceMatrix[pi_i][pi_k] - distanceMatrix[pi_j][pi_k]) +
                            flowMatrix[k][i] * (distanceMatrix[pi_k][pi_j] - distanceMatrix[pi_k][pi_i]) +
                            flowMatrix[k][j] * (distanceMatrix[pi_k][pi_i] - distanceMatrix[pi_k][pi_j]);
        }

        delta +=
                (flowMatrix[i][i] + flowMatrix[j][j]) * (distanceMatrix[pi_j][pi_j] - distanceMatrix[pi_i][pi_i]) +
                (flowMatrix[i][j] + flowMatrix[j][i]) * (distanceMatrix[pi_j][pi_i] - distanceMatrix[pi_i][pi_j]);

        return delta;
    }
}
