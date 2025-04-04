import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SolutionSaver {

    private String methodName;
    private String instance;
    private int runNumber;
    private int len;
    private int[] bestSol;
    private int bestFit;

    public SolutionSaver(String methodName, String instance, int len, int[] bestSolution, int bestFitness) {
        this.methodName = methodName;
        this.instance = instance;
        this.runNumber = 1;
        this.len = len;
        this.bestSol = bestSolution.clone();
        this.bestFit = bestFitness;
    }

    /**
     * Saves the solution details to a file.
     *
     * @param solution List of node IDs selected in this run.
     * @param objFuncValue Value of the objective function.
     */
    public void saveSolution(int[] initialSol, int iniFit, int[] solution,
                              int objFuncValue, int steps, int evaluations) {
        Path directoryPath = Paths.get("data", "method_outputs", instance, methodName);
        File dir = directoryPath.toFile();

        // Create directory if it does not exist
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = directoryPath.resolve(this.runNumber + ".txt").toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the metadata at the top of the file
            writer.write(String.valueOf(len));
            writer.newLine();
            writer.write(String.valueOf(iniFit));
            writer.newLine();
            for (Integer id : initialSol) {
                writer.write(id.toString());
                writer.newLine();
            }
            writer.write(String.valueOf(steps));
            writer.newLine();
            writer.write(String.valueOf(evaluations));
            writer.newLine();
            writer.write(String.valueOf(objFuncValue));
            writer.newLine();
            writer.write("Solution:");
            writer.newLine();

            // Write the solution
            for (Integer id : solution) {
                writer.write(id.toString());
                writer.newLine();
            }
            runNumber++;
            System.out.println("Solution saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    public void saveTime(int[] fullTime) {
        Path directoryPath = Paths.get("data", "method_outputs", instance, methodName);
        File dir = directoryPath.toFile();

        // Create directory if it does not exist
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filePath = directoryPath.resolve("Time" + ".txt").toString();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the metadata at the top of the file
            for (Integer i : fullTime){
                writer.write(String.valueOf(i));
                writer.newLine();
            }
            writer.write(String.valueOf(bestFit));
            writer.newLine();
            for (Integer i : bestSol){
                writer.write(String.valueOf(i));
                writer.newLine();
            }




            System.out.println("Time saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}
