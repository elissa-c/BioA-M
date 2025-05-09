import java.util.*;

public class SimAnnealing {
    private static Random RANDOM;
    private static Fitness fit;
    private static double weight = 0.5;
    private static double mutationProbability = 0.35;
    private static boolean useMutation = true;
    private static Integer operator = 2;
    private static int step=0;
    private static int evals = 0;
    private static long start;
    private static int maxTime;




    public static int[] generateHEA(List<List<Integer>> initialpop, Fitness f, Random R, int Time, SolutionSaver s) {
        RANDOM = R;
        fit = f;
        maxTime = Time;
        List<Integer> costs = evaluatePopulation(initialpop);
        List<Integer> ini = initialpop.get(getIdWithGivenCost("min", costs));
        List<Integer> child = new ArrayList<>();
        int childF;
        int idWorstCost;

        int size = initialpop.getFirst().size();
        int itter = 0;

        // (3) Repeat until stopping conditions are met
        start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < maxTime) {
            itter+=1;
            // System.out.println("CURRENT BEST: " + costs.get(getIdWithGivenCost("min", costs)));
            // select parents
            List<List<Integer>> parents = pickNRandom(initialpop, 2);

            // offspring creation
            if (operator == 1){
                child = operator1(parents);
            } else {
                child = operator2(parents);

            }

            if (RANDOM.nextDouble()<0.5){
                child = localSearch(child);
            }
            else{
                child = simulatedAnnealing(child, 10000, 0.995, 1);
            }
            if (RANDOM.nextDouble() < mutationProbability && useMutation) {
                // Perform mutation: destroy and repair from LNS
                child = destroy(child);
                child = repair(child, size);
            }

            childF = fit.calculateCost(child);
            if (!costs.contains(childF)) {
                idWorstCost = getIdWithGivenCost("max", costs);

                // if the child's cost is better (lower), replace the worst solution
                if (costs.get(idWorstCost) > childF) {
                    initialpop.set(idWorstCost, child);
                    costs.set(idWorstCost, childF);
                } // elite 20
            }
            step++;
        }
        System.out.println(itter);
        List<Integer> bestSol = initialpop.get(getIdWithGivenCost("min", costs));
        s.saveSolution(ini.stream().mapToInt(Integer::intValue).toArray(), fit.calculateCost(ini),
                bestSol.stream().mapToInt(Integer::intValue).toArray(), Collections.min(costs),
                step, evals);
        return  bestSol.stream().mapToInt(Integer::intValue).toArray();
    }

    public static List<Integer> simulatedAnnealing(List<Integer> initialSolution, double T, double coolingRate, double stoppingTemperature
    ) {
        List<Integer> currentSolution = new ArrayList<>(initialSolution);
        double currentCost = fit.calculateCost(currentSolution);
        List<Integer> bestSolution = new ArrayList<>(currentSolution);
        double bestCost = currentCost;
        int itter = 0;

        while (T > stoppingTemperature && itter<initialSolution.size()*8 && System.currentTimeMillis() - start < maxTime) {
            List<Integer> neighbor = perturb(new ArrayList<>(currentSolution));
            double neighborCost = fit.calculateCost(neighbor);
            evals++;

            if (neighborCost < currentCost || RANDOM.nextDouble() < Math.exp((currentCost - neighborCost) / T)) {
                currentSolution = new ArrayList<>(neighbor);
                currentCost = neighborCost;

                if (neighborCost < bestCost) {
                    bestSolution = new ArrayList<>(neighbor);
                    bestCost = neighborCost;
                }
            }
            itter++;
            T *= coolingRate;
        }

        return bestSolution;
    }


    public static List<Integer> doubleBridgeMove(List<Integer> solution) {
        int[] indices = RANDOM.ints(1, solution.size() - 2).distinct().limit(3).sorted().toArray();
        int a = indices[0], b = indices[1], c = indices[2];
        List<Integer> newSolution = new ArrayList<>();
        newSolution.addAll(solution.subList(0, a));
        newSolution.addAll(solution.subList(c, solution.size()));
        newSolution.addAll(solution.subList(b, c));
        newSolution.addAll(solution.subList(a, b));
        return newSolution;
    }

    public static List<Integer> shuffleSubTour(List<Integer> solution) {
        int n = solution.size();
        int subTourLength = RANDOM.nextInt((int) (0.15 * n) - (int) (0.05 * n) + 1) + (int) (0.05 * n);
        int startIdx = RANDOM.nextInt(n - subTourLength);
        int endIdx = startIdx + subTourLength;

        List<Integer> subTour = new ArrayList<>(solution.subList(startIdx, endIdx));
        Collections.shuffle(subTour);

        List<Integer> newSolution = new ArrayList<>();
        newSolution.addAll(solution.subList(0, startIdx));
        newSolution.addAll(subTour);
        newSolution.addAll(solution.subList(endIdx, n));

        return newSolution;
    }

    public static List<Integer> randomJump(List<Integer> solution) {
        int n = solution.size();
        int subTourLength = RANDOM.nextInt((int) (0.15 * n) - (int) (0.05 * n) + 1) + (int) (0.05 * n);
        int startIdx = RANDOM.nextInt(n - subTourLength);
        int endIdx = startIdx + subTourLength;

        List<Integer> subTour = new ArrayList<>(solution.subList(startIdx, endIdx));
        List<Integer> newSolution = new ArrayList<>(solution);
        newSolution.subList(startIdx, endIdx).clear();

        int insertIdx = RANDOM.nextInt(newSolution.size() + 1);
        newSolution.addAll(insertIdx, subTour);

        return newSolution;
    }


    public static List<Integer> perturb(List<Integer> solution) {
        List<java.util.function.Function<List<Integer>, List<Integer>>> perturbations = Arrays.asList(
                SimAnnealing::doubleBridgeMove,
                SimAnnealing::shuffleSubTour,
                SimAnnealing::randomJump
        );

        java.util.function.Function<List<Integer>, List<Integer>> action = perturbations.get(RANDOM.nextInt(perturbations.size()));
        return action.apply(new ArrayList<>(solution));
    }



    public static List<Integer> operator2(List<List<Integer>> parents) {
        // Step 1: Get the parents
        List<Integer> parent1 = parents.get(0);
        List<Integer> parent2 = parents.get(1);
        int size = parent1.size();

        // Step 2: Start with one parent as the initial solution
        List<Integer> child = new ArrayList<>(parent1);

        // Step 3: Remove edges and nodes from the first parent that are not in the second parent
        // A B C D E F
        // A E C F D G
        // if edge DE not in second parent, but nodes D E in, then delete DE either way (?)
        // but we could leave then either C -> E or D -> F instead - which to choose
        // for i and i+1 (edge) in first parent, if not in second then
        //     if any of nodes i, i+1nnot in sol remove this
        //     else remove i
        // or just remove i each time

        int sizeChild = child.size();

        // so we have the same nodes, but now the order shall be preserved
        Set<Set<Integer>> edges1 = new HashSet<>();
        Set<Set<Integer>> edges2 = new HashSet<>();
        for (int i = 0; i < size; i++) {
            int next = (i + 1) % size;
            edges1.add(new HashSet<>(Arrays.asList(parent1.get(i), parent1.get(next))));
            edges2.add(new HashSet<>(Arrays.asList(parent2.get(i), parent2.get(next))));
        }
        Set<Set<Integer>> commonEdges = new HashSet<>(edges1);
        commonEdges.retainAll(edges2);
        List<Integer> toRemove = new ArrayList<>();
        for (int i = 0; i < sizeChild - 1; i++) {
            Set<Integer> edge = new HashSet<>(Arrays.asList(child.get(i), child.get(i + 1)));
            if (!commonEdges.contains(edge)) {
                toRemove.add(i);
            }
        }
        child.removeAll(toRemove);
        // System.err.println(child.size());

        // Step 4: repair
        child = repair(child, sizeChild);

        return child;
    }

    public static List<Integer> operator1(List<List<Integer>> parents) {
        // Step 1: Get the parents
        List<Integer> parent1 = parents.get(0);
        List<Integer> parent2 = parents.get(1);
        int size = parent1.size();

        // Step 2: Initialize the offspring with null values
        List<Integer> offspring = new ArrayList<>(Collections.nCopies(size, null));

        // Step 3: Locate common nodes and edges in both parents
        // A -> edges
        Set<Set<Integer>> edges1 = new HashSet<>();
        Set<Set<Integer>> edges2 = new HashSet<>();
        for (int i = 0; i < size; i++) {
            int next = (i + 1) % size;
            edges1.add(new HashSet<>(Arrays.asList(parent1.get(i), parent1.get(next))));
            edges2.add(new HashSet<>(Arrays.asList(parent2.get(i), parent2.get(next))));
        }
        Set<Set<Integer>> commonEdges = new HashSet<>(edges1);
        commonEdges.retainAll(edges2);

        // place in offspring (in order of p1 simply)
        for (int i = 0; i < size - 1; i++) {
            Set<Integer> edge = new HashSet<>(Arrays.asList(parent1.get(i), parent1.get(i + 1)));
            if (commonEdges.contains(edge)) {
                offspring.set(i, parent1.get(i));
                offspring.set(i + 1, parent1.get(i + 1));
            }
        }



        // Step 4: Fill remaining null positions with random nodes, ensuring no duplicates
        List<Integer> remaining = new ArrayList<>();
        for (int i=0; i<size; i++) {
            if (!offspring.contains(i)) {
                remaining.add(i);
            }
        }
        Collections.shuffle(remaining); // shuffle to randomize remaining nodes
        int remainingIndex = 0;
        for (int i = 0; i < size; i++) {
            if (offspring.get(i) == null) {
                offspring.set(i, remaining.get(remainingIndex));
                remainingIndex++;
            }
        }

        return offspring;
    }

    private static List<Integer> repair(List<Integer> partialSolution, int size) {

        // track visited nodes
        boolean[] visited = new boolean[size];
        for (int node : partialSolution) {
            visited[node] = true; // Mark nodes in the partial solution as visited
        }

        // start from the given partial solution
        List<Integer> currCycle = new ArrayList<>(partialSolution);

        // Repeat until all nodes are added
        while (currCycle.size() < size) {
            int bestNodeIndex = -1;
            int bestPosition = -1;
            double bestWeightedSum = Double.MAX_VALUE;

            // Iterate over all unvisited nodes
            for (int j = 0; j < size; j++) {
                if (!visited[j]) {
                    int newNode = j;
                    int bestInc = Integer.MAX_VALUE;
                    int secondInc = Integer.MAX_VALUE;
                    int bestPlace = -1;

                    // Find the best and second-best position for this node
                    for (int pos = 0; pos < currCycle.size(); pos++) {

                        // Obj.func. change
                        int increase = fit.calculateCost(currCycle, pos, j);
                        evals++;

                        if (increase < bestInc) {
                            secondInc = bestInc;
                            bestInc = increase;
                            bestPlace = pos + 1;
                        } else if (increase < secondInc) {
                            secondInc = increase;
                        }
                    }

                    // regret
                    int regret = secondInc - bestInc;

                    // weighted sum: objective function (greedy) + regret (weighted)
                    // bestInc is objective func val
                    double weightedSum = (1-weight) * bestInc + weight * (-1.0 * regret);
                    // Select the node with the smallest weighted sum
                    if (weightedSum < bestWeightedSum) {
                        bestNodeIndex = newNode;
                        bestPosition = bestPlace;
                        bestWeightedSum = weightedSum;
                    }
                }
            }

            // insert selected node into best position
            currCycle.add(bestPosition, bestNodeIndex);
            visited[bestNodeIndex] = true;
        }

        return currCycle;
    }

    private static List<Integer> destroy(List<Integer> solution) {
        List<Integer> destroyed = new ArrayList<>(solution);

        int n_subpaths = RANDOM.nextInt(2) + 2; // randomly 2 or 3 subpaths
        int percentage = RANDOM.nextInt(11) + 20; // randomly 20-30% nodes
        //System.out.println("n_subpaths = " + n_subpaths + " and percentage: " + percentage);
        int length_subpath = (solution.size() * percentage) / 100 / n_subpaths;
        int n_to_remove = solution.size() - ((solution.size() * percentage) / 100);

        List<Map.Entry<List<Integer>, Integer>> subpaths = new ArrayList<>();
        for (int i = 0; i < solution.size() - length_subpath + 1; i++) {
            List<Integer> subpath = solution.subList(i, i + length_subpath);
            Integer obj_f = fit.calculateCost(subpath);
            evals++;
            subpaths.add(new AbstractMap.SimpleEntry<>(new ArrayList<>(subpath), obj_f));
        }

        // sort subpaths by objective function in descending order (highest scores first)
        subpaths.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        // calculate probabilities based on the highest score
        int totalScore = subpaths.stream().mapToInt(Map.Entry::getValue).sum();
        List<Double> probabilities = new ArrayList<>();
        for (Map.Entry<List<Integer>, Integer> entry : subpaths) {
            probabilities.add(entry.getValue() / (double) totalScore);
        }

        // remove
        // Randomly select and remove subpaths based on probabilities
        for (int i = 0; i < n_subpaths && !subpaths.isEmpty(); i++) {
            double rand = RANDOM.nextDouble(); // generate random probability [0, 1)
            double cumulativeProbability = 0.0;
            for (int j = 0; j < subpaths.size(); j++) {
                cumulativeProbability += probabilities.get(j);
                if (rand <= cumulativeProbability) {
                    List<Integer> toRemove = subpaths.get(j).getKey();
                    destroyed.removeAll(toRemove);
                    subpaths.remove(j);
                    probabilities.remove(j);
                    break;
                }
            }
        }
        int n_removed = solution.size() - destroyed.size();

        while (n_removed < n_to_remove) {
            int randomIndex = RANDOM.nextInt(destroyed.size()); // Random index
            destroyed.remove(randomIndex); // Remove a random node
            n_removed++;
        }

        return destroyed;
    }


    private static List<Integer> evaluatePopulation(List<List<Integer>> population) {
        List<Integer> costs = new ArrayList<>();

        // Iterate through each solution in the population
        for (List<Integer> solution : population) {
            int cost = fit.calculateCost(solution);
            costs.add(cost);
        }

        return costs; // Return the list of costs
    }

    // generateSteepest2edgesMoveEvals:
    private static List<Integer> localSearch(List<Integer> initialSolution) {
        List<Integer> currentSolution = new ArrayList<>(initialSolution);
        double currentCost = fit.calculateCost(currentSolution);

        boolean improvement = true;

        while (improvement && System.currentTimeMillis() - start < maxTime) {
            improvement = false;
            List<Integer> bestSolution = new ArrayList<>(currentSolution);
            double bestCost = currentCost;

            // Try all pairwise swaps (basic local search / 2-opt style)
            for (int i = 0; i < currentSolution.size() - 1; i++) {
                for (int j = i + 1; j < currentSolution.size(); j++) {
                    List<Integer> newSolution = new ArrayList<>(currentSolution);
                    // Swap two elements
                    Collections.swap(newSolution, i, j);

                    double newCost = fit.calculateCost(newSolution);
                    evals++;
                    if (newCost < bestCost) {
                        bestCost = newCost;
                        bestSolution = newSolution;
                        improvement = true;
                    }
                }
            }

            currentSolution = bestSolution;
            currentCost = bestCost;
        }

        return currentSolution;
    }

    public static List<List<Integer>> pickNRandom(List<List<Integer>> lst, int n) {
        List<List<Integer>> copy = new ArrayList<>(lst); // create a copy to avoid modifying the original list
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(n, copy.size()));
    }


    private static Integer getIdWithGivenCost(String type, List<Integer> costs) {
        if (type.equals("max")) {
            int maxCostIndex = 0;
            for (int i = 1; i < costs.size(); i++) {
                if (costs.get(i) > costs.get(maxCostIndex)) {
                    maxCostIndex = i;
                }
            }
            return maxCostIndex;
        } else {  // for "min"
            int minCostIndex = 0;
            for (int i = 1; i < costs.size(); i++) {
                if (costs.get(i) < costs.get(minCostIndex)) {
                    minCostIndex = i;
                }
            }
            return minCostIndex;
        }
    }
}
