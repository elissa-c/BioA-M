import java.util.Arrays;
import java.util.Random;

public class LocalSearch {
    public static int[] Greedy(int [] initial, Fitness fit, int maxItter, Random RANDOM, int restarts, SolutionSaver s) {
        int[] bestSol = new int[initial.length];
        int bestFit = Integer.MAX_VALUE;
        int step = 0;
        int evals = 0;

        for (int t = 0; t<restarts; t++) {

            int[] currentSol = initial.clone();

            for (int i = 0; i < maxItter ; i++) {
                boolean improve = false;

                int[] order = Randoms.shuffleList(initial, RANDOM);
                for (int c = 0; c < initial.length; c++) {
                    int a = order[c];
                    for (int b = 0; b < initial.length; b++) {
                        int delta = fit.computeDelta(currentSol, a, b);
                        evals++;

                        if (delta < 0) {
                            int x = currentSol[a];
                            currentSol[a] = currentSol[b];
                            currentSol[b] = x;
                            improve = true;
                            step++;
                            break;
                        }
                    }
                    if (improve) {
                        break;
                    }
                }
                if (!improve) {
                    break;
                }
            }
            int currentFit = fit.calculateCost(currentSol);
            if (currentFit < bestFit) {
                bestSol = currentSol;
                bestFit = currentFit;
            }
        }
        s.saveSolution(initial, fit.calculateCost(initial), bestSol, bestFit, step, evals);
        return bestSol;
    }

    public static int[] Steepest(int[] initial, Fitness fit, int maxItter, Random RANDOM, int restarts, SolutionSaver s) {
        int[] bestSol = new int[initial.length];
        int bestFit = Integer.MAX_VALUE;
        int step = 0;
        int evals = 0;

        for (int t = 0; t < 1; t++) {
            int[] currentSol = Arrays.copyOf(initial, initial.length);

            for (int i = 0; i < maxItter; i++) {
                int bestDelta = 0;
                int[] swap = new int[2];
                boolean foundImprovement = false;

                int[] order = Randoms.shuffleList(initial, RANDOM);
                for (int c = 0; c < initial.length; c++) {
                    int a = order[c];
                    for (int b = 0; b < initial.length; b++) {
                        if (a == b) continue;
                        int delta = fit.computeDelta(currentSol, a, b);
                        evals++;
                        if (delta < bestDelta) {
                            swap[0] = a;
                            swap[1] = b;
                            bestDelta = delta;
                            foundImprovement = true;
                        }
                    }
                }

                if (foundImprovement) {
                    int temp = currentSol[swap[0]];
                    currentSol[swap[0]] = currentSol[swap[1]];
                    currentSol[swap[1]] = temp;
                    step++;
                } else {
                    break;
                }
            }

            int currentFit = fit.calculateCost(currentSol);
            evals++;
            if (currentFit < bestFit) {
                bestSol = Arrays.copyOf(currentSol, currentSol.length);
                bestFit = currentFit;
            }
        }

        s.saveSolution(initial, fit.calculateCost(initial), bestSol, bestFit, step, evals);
        return bestSol;
    }

}
