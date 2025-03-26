import java.util.Arrays;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.lang.Thread;


public class Main {
    private static Random RANDOM;
    private static int maxItter = 30;
    private static int maxNonImpr = 300;
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


        System.out.println(listSize);

        List<Integer> ret = new ArrayList<>(listSize);
        for (int i=0; i<listSize; i++) {
            ret.add(i);
        }


        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= nExperiments; i++) {
            int[] randomSolution = shuffleInt(ret);
            //List<Integer> pair = random2(ret);
            //int[] greedySolution = greedySolver(flowMatrix, distanceMatrix);
            int[] LSSol = localSearch(randomSolution, flowMatrix, distanceMatrix);
            for (int j = 0; j < listSize; j++)
                System.out.print(LSSol[j]+" ");
            System.out.println(Fitness.calculateCost(LSSol, flowMatrix, distanceMatrix));
            System.out.println();
        }
        float timeAll = System.currentTimeMillis() - startTime;
        float timeOnce = timeAll/nExperiments;

        for (int j = 0; j < listSize; j++)
            System.out.print(bestAns[j]+" ");
        System.out.println(Fitness.calculateCost(bestAns, flowMatrix, distanceMatrix));
        System.out.println();

        System.out.println("Full time " + timeAll);
        System.out.println("Average time of a run: " + timeOnce);
    }

    public static List<Integer> shuffleList(List<Integer> nodeList){
        List<Integer> base = new ArrayList<>(nodeList);
        List<Integer> randomized = new ArrayList<>(nodeList.size());
        for (int j =0; j<nodeList.size(); j++) {
            int id = RANDOM.nextInt(base.size());
            randomized.add(base.get(id));
            base.remove(id);
        }
        return randomized;
    }

    public static int[] shuffleInt(List<Integer> nodeList){
        List<Integer> base = new ArrayList<>(nodeList);
        int[] randomized = new int[nodeList.size()];
        for (int j =0; j<nodeList.size(); j++) {
            int id = RANDOM.nextInt(base.size());
            randomized[j] = base.get(id);
            base.remove(id);
        }
        return randomized;
    }

    public static List<Integer> random2(List<Integer> nodeList){
        int a = RANDOM.nextInt(nodeList.size());
        int b = RANDOM.nextInt(nodeList.size()-1);
        if (b>=a) { b= b+1;}
        int finalB = b;
        return new ArrayList<>() {{add(a); add(finalB);}};
    }

    public static int[] localSearch(int [] initial, int[][] flow, int[][] distance) {
        int currentFit = Fitness.calculateCost(initial, flow, distance);
        int[] currentSol = initial.clone();

        int nonImpr = 0;
        for (int i = 0; i<maxItter && nonImpr<maxNonImpr; i++) {
            boolean improve = false;

            for (int a =0; a<initial.length; a++) {
                for (int b=0; b<initial.length; b++) {
                    int[] swapped = initial.clone();
                    swapped[a] =  initial[b];
                    swapped[b] = initial[a];

                    int newFit = Fitness.calculateCost(swapped, flow, distance);

                    if (newFit<currentFit) {
                        currentSol = swapped;
                        currentFit = newFit;
                        improve = true;
                        break;
                    }
                }
                if (improve) {
                    break;
                }
            }
            if (improve) {
                nonImpr = 0;
            }
            else {
                nonImpr++;
            }
        }
        return currentSol;
    }


}

// loading data for QTSP problem
// heuristics
// 2-opt neighborhood
// analysis in python
// output initial, final solution, time, n of evaluations, number of solutions(?)