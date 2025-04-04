import java.util.Random;

public class LocalSearch {
    public static int[] Greedy(int [] initial, Fitness fit, int maxItter, Random RANDOM, int restarts, SolutionSaver s) {
        int[] bestSol = new int[initial.length];
        int bestFit = Integer.MAX_VALUE;
        int step = 0;
        int evals = 0;

        for (int t = 0; t<restarts; t++) {

            int[] currentSol = Randoms.shuffleList(initial, RANDOM);
            int currentFit = fit.calculateCost(currentSol);

            for (int i = 0; i < maxItter ; i++) {
                boolean improve = false;

                int[] order = Randoms.shuffleList(initial, RANDOM);
                for (int c = 0; c < initial.length; c++) {
                    int a = order[c];
                    for (int b = 0; b < initial.length; b++) {
                        int[] swapped = initial.clone();
                        swapped[a] = initial[b];
                        swapped[b] = initial[a];

                        int newFit = fit.calculateCost(swapped);
                        evals++;

                        if (newFit < currentFit) {
                            currentSol = swapped;
                            currentFit = newFit;
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
            if (currentFit < bestFit) {
                bestSol = currentSol;
                bestFit = currentFit;
            }
        }
        s.saveSolution(initial, fit.calculateCost(initial), bestSol, bestFit, step, evals);
        return bestSol;
    }

    public static int[] Steepest(int [] initial, Fitness fit, int maxItter, Random RANDOM, int restarts, SolutionSaver s) {
        int[] bestSol = new int[initial.length];
        int bestFit = Integer.MAX_VALUE;
        int step = 0;
        int evals = 0;

        for (int t = 0; t<restarts; t++) {

            int[] currentSol = Randoms.shuffleList(initial, RANDOM);
            int currentFit = fit.calculateCost(currentSol);

            for (int i = 0; i < maxItter ; i++) {
                int[] topChange = currentSol.clone();
                int topFit = currentFit;

                int[] order = Randoms.shuffleList(initial, RANDOM);
                for (int c = 0; c < initial.length; c++) {
                    int a = order[c];
                    for (int b = 0; b < initial.length; b++) {
                        int[] swapped = initial.clone();
                        swapped[a] = initial[b];
                        swapped[b] = initial[a];

                        int newFit = fit.calculateCost(swapped);
                        evals++;

                        if (newFit < topFit) {
                            topChange = swapped;
                            topFit = newFit;
                        }
                    }

                }
                if (topFit < currentFit) {
                    currentSol = topChange;
                    currentFit = topFit;
                    step++;
                } else {
                    break;
                }
            }
            if (currentFit < bestFit) {
                bestSol = currentSol;
                bestFit = currentFit;
            }
        }
        s.saveSolution(initial, fit.calculateCost(initial), bestSol, bestFit, step, evals);
        return bestSol;
    }
}
