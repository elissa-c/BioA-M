import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.lang.Thread;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Random RANDOM = new Random(222);
        int listSize = 100;
        int nExperiments = 10;

        List<Integer> ret = new ArrayList<>(listSize);
        for (int i=0; i<listSize; i++) {
            ret.add(i);
        }


        long startTime = System.currentTimeMillis();
        for (int i = 1; i <= nExperiments; i++) {
            List<Integer> base = new ArrayList<>(ret);
            List<Integer> randomized = new ArrayList<>(listSize);
            for (int j =0; j<listSize; j++) {
                int id = RANDOM.nextInt(base.size());
                randomized.add(base.get(id));
                base.remove(id);
            }
            Thread.sleep(0);

        }
        long timeAll = System.currentTimeMillis() - startTime;
        long timeOnce = timeAll/nExperiments;

        System.out.println("Full time " + timeAll);
        System.out.println("Average time of a run: " + timeOnce);
    }
}