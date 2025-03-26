import java.util.Arrays;

public class Greedy {
    private static Fitness fit;
    public static int[] greedySolver(int[][] distance, int[][] flow) {
        int n = distance.length;
        int[] assignment = new int[n];
        Arrays.fill(assignment, -1);
        boolean[] facilityAssigned = new boolean[n];
        boolean[] locationAssigned = new boolean[n];

        // Start with the most interacting facility pair at the most distant locations
        int[] firstPair = findMostInteractingPair(flow);
        int[] firstLocations = findMostDistantPair(distance);

        assignment[firstLocations[0]] = firstPair[0];
        assignment[firstLocations[1]] = firstPair[1];
        facilityAssigned[firstPair[0]] = true;
        facilityAssigned[firstPair[1]] = true;
        locationAssigned[firstLocations[0]] = true;
        locationAssigned[firstLocations[1]] = true;

        // Greedily assign remaining facilities
        for (int i = 2; i < n; i++) {
            int bestLoc = -1, bestFac = -1;
            int bestCost = Integer.MAX_VALUE;

            for (int loc = 0; loc < n; loc++) {
                if (locationAssigned[loc]) continue;

                for (int fac = 0; fac < n; fac++) {
                    if (facilityAssigned[fac]) continue;

                    int cost = Fitness.calculateCost(assignment, fac, loc, flow, distance);

                    if (cost < bestCost) {
                        bestCost = cost;
                        bestLoc = loc;
                        bestFac = fac;
                    }
                }
            }

            assignment[bestLoc] = bestFac;
            facilityAssigned[bestFac] = true;
            locationAssigned[bestLoc] = true;
        }

        return assignment;
    }


    private static int[] findMostInteractingPair(int[][] flow) {
        int maxFlow = Integer.MIN_VALUE;
        int[] pair = new int[2];
        int n = flow.length;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (flow[i][j] > maxFlow) {
                    maxFlow = flow[i][j];
                    pair[0] = i;
                    pair[1] = j;
                }
            }
        }
        return pair;
    }

    private static int[] findMostDistantPair(int[][] distance) {
        int maxDist = Integer.MIN_VALUE;
        int[] pair = new int[2];
        int n = distance.length;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (distance[i][j] > maxDist) {
                    maxDist = distance[i][j];
                    pair[0] = i;
                    pair[1] = j;
                }
            }
        }
        return pair;
    }


}
