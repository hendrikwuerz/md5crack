import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.*;

public class Main {

    private static final String DIGITS = "0123456789";
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String EXTENDED_CHARS = "φόδί";
    private static final String SPECIAL_CHARS = ",;.:-_#'+*~<>|!\"§$%&/()=?\\΄`";

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        //String hash = "7b3b0302e25b01bf8a82add7b4ab6041"; // MySecret123
        //String hash = "d1bf93299de1b68e6d382c893bf1215f"; // Hallo
        String hash = "d3eb9a9233e52948740d7eb8c3062d14"; // 99999
        //String hash = "9cc9c27e4a7a69dc64001bf7cb67d89d"; // blubb

        String prefix = "";
        int maxSize = 12;
        int minSize = 5;
        char[] alphabet = (CHARS + CHARS.toUpperCase() + DIGITS).toCharArray();
        long cracksPerMilliSecond = 4000;
        int threads = Runtime.getRuntime().availableProcessors();


        Md5Crack crack = new Md5Crack(hash, prefix, minSize, maxSize, alphabet, cracksPerMilliSecond, threads);

        System.out.println(crack.timeCalculationtoString());

        long start = System.currentTimeMillis();

        List<Future<CrackResult>> list = crack.start();

        for(Future<CrackResult> fut : list){
            try {
                CrackResult result = fut.get();
                System.out.println(new Date()+ "::"+ result);
                if(result.isCracked()) { // solution found
                    crack.stop();
                    System.out.println("All threads become shutdown");
                    break;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Needed " + (System.currentTimeMillis() - start) / 1000 + " seconds");


    }

}