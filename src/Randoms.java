import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Randoms {
    public static int[] shuffleList (int[] nodeList, Random RANDOM){
        List<Integer> base = IntStream.of(nodeList).boxed().collect(Collectors.toList());
        int[] randomized = new int[nodeList.length];
        for (int j =0; j<nodeList.length; j++) {
            int id = RANDOM.nextInt(base.size());
            randomized[j] = base.get(id);
            base.remove(id);
        }
        return randomized;
    }

    public static int[] shuffleInt(List<Integer> nodeList, Random RANDOM){
        List<Integer> base = new ArrayList<>(nodeList);
        int[] randomized = new int[nodeList.size()];
        for (int j =0; j<nodeList.size(); j++) {
            int id = RANDOM.nextInt(base.size());
            randomized[j] = base.get(id);
            base.remove(id);
        }
        return randomized;
    }

    public static int[] randomSolver(int[] initial, Random RANDOM, Fitness fitness, int maxItter, SolutionSaver s) {
        int[] bestSol = initial.clone();
        int bestFit = fitness.calculateCost(initial);

        int steps = 0;
        int evaluations=0;

        long startTime = System.currentTimeMillis();

        while(System.currentTimeMillis()-startTime<8){
            int[] sol = shuffleList(initial, RANDOM);
            int fit = fitness.calculateCost(sol);
            if (fit<bestFit) {
                bestSol = sol;
                bestFit = fit;
            }
            steps++;
            evaluations++;
        }
        s.saveSolution(initial, fitness.calculateCost(initial), bestSol, bestFit, steps, evaluations);
        return bestSol;
    }

    public static int[] randomWalkSolver(int[] initial, Fitness fitness, Random RANDOM, int restarts, int maxItter, SolutionSaver s) {
        int[] bestSol = initial.clone();
        int bestFit = fitness.calculateCost(initial);

        int steps = 0;
        int evaluations=0;

        long startTime = System.currentTimeMillis();

        while(System.currentTimeMillis()-startTime<8){
            List<Integer> switched = random2(initial, RANDOM);
            int[] swapped = initial.clone();
            swapped[switched.getFirst()] =  initial[switched.getLast()];
            swapped[switched.getLast()] = initial[switched.getFirst()];
            int fit = fitness.calculateCost(swapped);
            if (fit<bestFit) {
                bestSol = swapped;
                bestFit = fit;
                steps++;
            }

            evaluations++;

        }
        s.saveSolution(initial, fitness.calculateCost(initial), bestSol, bestFit, steps, evaluations);
        return bestSol;
    }

    public static List<Integer> random2(int[] nodeList, Random RANDOM){
        int a = RANDOM.nextInt(nodeList.length);
        int b = RANDOM.nextInt(nodeList.length-1);
        if (b>=a) { b= b+1;}
        int finalB = b;
        return new ArrayList<>() {{add(a); add(finalB);}};
    }
}
