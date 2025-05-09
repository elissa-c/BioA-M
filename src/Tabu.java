import java.util.*;

public class Tabu {
    public static int[] tabuSearch(int[] initialSolution, Fitness fit,  Random RANDOM, int maxTime, SolutionSaver s) {
        int step = 0;
        int eval = 0;
        int size = initialSolution.length;
        int tabuTenure = size / 4;
        int candidateSampleSize = (int) (0.2 * size * size); // 20% of possible swaps
        int eliteSampleSize = Math.max(1, candidateSampleSize / 5); // top 20% of sample

        int[][] tabuList = new int[size][size]; // Tenure matrix for swaps
        int iteration = 0;
        int noImprovementCounter = 0;

        int[] bestSolution = Arrays.copyOf(initialSolution, size);
        int bestCost = fit.calculateCost(bestSolution);

        int[] currentSolution = Arrays.copyOf(initialSolution, size);
        int currentCost = bestCost;

        long startingTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startingTime < maxTime && noImprovementCounter < 20) {
            List<Move> candidateMoves = new ArrayList<>();

            // Generate 20% of all possible swaps
            for (int i = 0; i < candidateSampleSize; i++) {
                int a = RANDOM.nextInt(size);
                int b = RANDOM.nextInt(size);
                if (a == b) continue;

                int delta = fit.computeDelta(currentSolution, a, b);
                eval++;

                candidateMoves.add(new Move(a, b, delta));
            }

            // Sort by best delta (ascending)
            candidateMoves.sort(Comparator.comparingInt(m -> m.delta));

            // Choose top 20% elite candidates
            List<Move> eliteMoves = candidateMoves.subList(0, eliteSampleSize);

            Move bestMove = null;

            for (Move move : eliteMoves) {
                boolean isTabu = tabuList[move.i][move.j] > iteration;

                int candidateCost = currentCost + move.delta;

                // Aspiration criteria: allow if improves global best
                if (!isTabu || candidateCost < bestCost) {
                    bestMove = move;
                    break;
                }
            }

            if (bestMove == null) {
                break; // No admissible move found
            }

            // Apply move
            int temp = currentSolution[bestMove.i];
            currentSolution[bestMove.i] = currentSolution[bestMove.j];
            currentSolution[bestMove.j] = temp;

            currentCost= fit.calculateCost(currentSolution);

            // Update tabu list
            tabuList[bestMove.i][bestMove.j] = iteration + tabuTenure;
            tabuList[bestMove.j][bestMove.i] = iteration + tabuTenure;
            step++;

            // Update best solution
            if (currentCost < bestCost) {
                bestSolution = Arrays.copyOf(currentSolution, size);
                bestCost = currentCost;
                noImprovementCounter = 0;
            } else {
                noImprovementCounter++;
            }

            iteration++;
        }

        s.saveSolution(initialSolution, fit.calculateCost(initialSolution),
                bestSolution, bestCost, step, eval);
        return bestSolution;
    }

}

class Move {
    int i, j, delta;

    public Move(int i, int j, int delta) {
        this.i = i;
        this.j = j;
        this.delta = delta;
    }
}
