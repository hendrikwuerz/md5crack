import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        //String hash = "7b3b0302e25b01bf8a82add7b4ab6041"; // MySecret123
        String hash = "d1bf93299de1b68e6d382c893bf1215f"; // Hallo

        String prefix = "";
        int maxSize = 12;
        int minSize = 5;
        String digits = "0123456789";
        String chars = "abcdefghijklmnopqrstuvwxyz";
        char[] alphabet = (chars + chars.toUpperCase() + digits).toCharArray();

        System.out.println("I will need " + Md5Crack.calculateTime(alphabet.length, maxSize, 4925445) + " seconds");


        long start = System.currentTimeMillis();
        //System.out.println(md5Crack.crack());

        //Get ExecutorService from Executors utility class, thread pool size is 10
        ExecutorService executor = Executors.newFixedThreadPool(4);

        //create a list to hold the Future object associated with Callable
        List<Future<CrackResult>> list = new ArrayList<>();

        for(int usedSize = minSize; usedSize <= maxSize; usedSize++) {
            for (char anAlphabet : alphabet) {
                Md5Crack md5Crack = new Md5Crack(hash, prefix + anAlphabet, usedSize - 1, alphabet);
                Future<CrackResult> future = executor.submit(md5Crack);
                list.add(future);
            }
        }
        for(Future<CrackResult> fut : list){
            try {
                CrackResult result = fut.get();
                System.out.println(new Date()+ "::"+ result);
                if(result.isCracked()) { // solution found
                    executor.shutdownNow();
                    break;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Needed " + (System.currentTimeMillis() - start) / 1000 + " seconds");


    }

}