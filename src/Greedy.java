import java.util.Arrays;
import java.util.*;

public class Greedy {

    public static int[] greedySolver(int[] ini, int[][] distance, int[][] flow, Fitness fit, SolutionSaver s) {
        int n = distance.length;
        int[] assignment = new int[n];
        Arrays.fill(assignment, -1);
        boolean[] facilityAssigned = new boolean[n];
        boolean[] locationAssigned = new boolean[n];

        int step=0, eval=0;

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

                    int cost = fit.calculateCost(assignment, fac, loc);
                    eval++;

                    if (cost < bestCost) {
                        bestCost = cost;
                        bestLoc = loc;
                        bestFac = fac;
                        step++;
                    }
                }
            }

            assignment[bestLoc] = bestFac;
            facilityAssigned[bestFac] = true;
            locationAssigned[bestLoc] = true;
        }
        int value = fit.calculateCost(assignment);
        s.saveSolution(ini, fit.calculateCost(ini), assignment, value, step, eval);
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

    public static int[] greedyConstruction(int[] ini, int[][] distance, int[][] flow, Fitness fit, SolutionSaver s) {
        int size = ini.length;
        List<Integer> unassignedFacilities = new ArrayList<>();
        List<Integer> unassignedLocations = new ArrayList<>();
        List<int[]> assigned = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            unassignedFacilities.add(i);
            unassignedLocations.add(i);
        }
        int steps = 0;

        unassignedFacilities.sort((i, j) -> Integer.compare(facilityFlowSum(flow, j), facilityFlowSum(flow, i)));

        while (!unassignedFacilities.isEmpty()) {
            int facility = unassignedFacilities.remove(unassignedFacilities.size() - 1);

            unassignedLocations.sort((i, j) -> Integer.compare(
                    calculateIncrementalCost(flow, distance, facility, i, assigned),
                    calculateIncrementalCost(flow, distance, facility, j, assigned)));

            int location = unassignedLocations.remove(unassignedLocations.size() - 1);

            assigned.add(new int[]{facility, location});

            steps++;
        }

        int[] solution = new int[size];
        for (int[] pair : assigned) {
            solution[pair[1]] = pair[0];
        }
        int value = fit.calculateCost(solution);
        s.saveSolution(ini, fit.calculateCost(ini), solution, value, steps, steps);

        return solution;
    }

    private static int facilityFlowSum(int[][] flow, int facility) {
        int sum = 0;
        for (int i = 0; i < flow.length; i++) {
            sum += flow[facility][i];
        }
        return sum;
    }

    private static int calculateIncrementalCost(int[][] flow, int[][] distance, int facility, int location, List<int[]> assigned) {
        int cost = 0;
        for (int[] pair : assigned) {
            int f = pair[0], l = pair[1];
            cost += flow[facility][f] * distance[location][l];
            cost += flow[f][facility] * distance[l][location];
        }
        return cost;
    }


}
