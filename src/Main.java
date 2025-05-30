import java.util.*;
import java.lang.Thread;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main {
    private static Random RANDOM;
    private static int maxItter = 100;
    private static int restarts = 10;
    private static Fitness Fitness;
    public static void main(String[] args) throws InterruptedException {
        RANDOM = new Random(23);

        int nExperiments = 10;

        String instance = "";
        try {
            instance = args[0];
        } catch (Exception e) {
            System.out.println("No instance provided. Defaulting to 'bur26a'.");
            instance = null;
        }
        INput input = new INput(instance);
        int[][] distanceMatrix = input.getDistanceMatrix();
        int[][] flowMatrix = input.getFlowMatrix();
        int listSize = input.getLength();
        int[] bestAns = input.getBestSolution();
        String inst = input.getCurrentFile();

        Fitness = new Fitness(flowMatrix, distanceMatrix);

        System.out.println(listSize);

        List<Integer> ret = new ArrayList<>(listSize);
        int[] rett = new int[listSize];
        for (int i=0; i<listSize; i++) {
            ret.add(i);
            rett[i] = i;
        }


        String[] methods = {"RandomSearch", "RandomWalk", "Heuristic", "Greedy", "Steepest", "SimulatedAnnealing", "Tabu"};

        SolutionSaver current = new SolutionSaver(methods[3], inst, listSize, bestAns, Fitness.calculateCost(bestAns));

        int[] time = new int[nExperiments];

        for (int i = 1; i <= nExperiments; i++) {
            List<List<Integer>> population = new ArrayList<>();
            for (int id = 0; id < 20; id++) {
                Collections.shuffle(ret);
                List<Integer> sol = ret;
                population.add(sol);
            }
            int[] randomSolution = Randoms.shuffleInt(ret, RANDOM);
            long startTime = System.currentTimeMillis();

            //int[] solution = Randoms.randomSolver(rett, RANDOM, Fitness, maxItter, current);
            //int[] solution = Randoms.randomWalkSolver(randomSolution, Fitness, RANDOM, startTime, current);
            //int[] solution = Greedy.greedySolver(randomSolution, distanceMatrix, flowMatrix, Fitness, current);
            int[] solution = LocalSearch.Greedy(randomSolution, Fitness, maxItter, RANDOM, restarts, current);
            //int[] solution = LocalSearch.Steepest(randomSolution, Fitness, maxItter, RANDOM, restarts, current);
            //int[] solution = SimAnnealing.generateHEA(population, Fitness, RANDOM, 1415, current);
            //int[] solution = Tabu.tabuSearch(randomSolution, Fitness, RANDOM, 3000, current);

            long timeAll = System.currentTimeMillis() - startTime;
            time[i-1] = (int) timeAll;

            for (int j = 0; j < listSize; j++)
                System.out.print(solution[j]+" ");
            System.out.println(Fitness.calculateCost(solution));
            System.out.println();
        }

        current.saveTime(time);

        for (int j = 0; j < listSize; j++)
            System.out.print(bestAns[j]+" ");
        System.out.println(Fitness.calculateCost(bestAns));
        System.out.println();

        System.out.println("Full time " + Arrays.stream(time).sum());
    }







}

// loading data for QTSP problem
// heuristics
// 2-opt neighborhood
// analysis in python
// output initial, final solution, time, n of evaluations, number of solutions(?)