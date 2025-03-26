import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class INput {

    private int length;
    private int[][] distanceMatrix;
    private int [][] flowMatrix;
    private int[] bestSolution;
    private int bestCost;

    public INput(String file) {
        String filePath = "";
        String solutionPath = "";
        // Read file name
        if (file == null){
            filePath = "qapdatsol/bur26a.dat";
            solutionPath = "qapdatsol/bur26a.sln";
        }
        else {
            filePath = String.format("qapdatsol/%s.dat", file);
            solutionPath = String.format("qapdatsol/%s.sln", file);
        }

        //Generate flow matrix
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            length = Integer.parseInt(br.readLine());

            distanceMatrix = new int[length][length];
            flowMatrix = new int[length][length];

            for (int i = 0; i<length;i++){
                line = br.readLine();
                if (line==null || line.equals(" ")|| line.isEmpty()) {line = br.readLine();}

                String[] data = line.split(" ");

                int a =0;
                for (String item: data) {
                    if (item.equals(" ") || item.isEmpty()) { continue; }
                    else{
                        int x = Integer.parseInt(item.trim());
                        flowMatrix[i][a] = x;
                        a++;
                    }
                }
            }
            // Generate distance matrix
            for (int i = 0; i<length;i++){
                line = br.readLine();
                if (line==null || line.equals(" ")|| line.isEmpty()) {line = br.readLine();}

                String[] data = line.split(" ");

                int a =0;
                for (String item: data) {
                    if (item.equals(" ") || item.isEmpty()) { continue; }
                    int x = Integer.parseInt(item.trim());
                    distanceMatrix[i][a] = x;
                    a++;
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return;
        }

        // Read solution file
        try (BufferedReader br = new BufferedReader(new FileReader(solutionPath))) {
            String sLine = br.readLine();
            String[] sData = sLine.split(" ");
            bestCost = Integer.parseInt(sData[sData.length - 1].trim());
            bestSolution = new int[length];

            int c = 0;
            while (c<length) {
                sLine = br.readLine();
                sData = sLine.split(" ");

                for (String a : sData) {
                    if (a.equals(" ") || a.isEmpty()) { continue; }
                    bestSolution[c] = Integer.parseInt(a)-1;
                    c++;
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return;
        }


    }

    public int[][] getDistanceMatrix() {
        return distanceMatrix;
    }

    public int[][] getFlowMatrix() {
        return flowMatrix;
    }

    public int[] getBestSolution() {
        return bestSolution;
    }

    public int getBestCost() {
        return bestCost;
    }

    public int getLength() {
        return length;
    }
}
