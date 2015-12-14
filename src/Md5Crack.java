import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Hendrik on 11.12.2015.
 */
public class Md5Crack {

    String hash;
    String prefix;
    int minSize;
    int maxSize;
    char[] alphabet;
    long cracksPerMilliSecond;
    int threads;

    ExecutorService executor;

    public Md5Crack(String hash, String prefix, int minSize, int maxSize, char[] alphabet, long cracksPerMilliSecond, int threads) {
        this.hash = hash;
        this.prefix = prefix;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.alphabet = alphabet;
        this.cracksPerMilliSecond = cracksPerMilliSecond;
        this.threads = threads;
    }

    public String timeCalculationtoString() {
        StringBuilder sb = new StringBuilder();
        for(int i = minSize; i <= maxSize; i++) {
            sb.append("Plaintext with ")
                    .append(i)
                    .append(" chars will max take ")
                    .append(Md5Crack.calculateTime(alphabet.length, i, cracksPerMilliSecond) / 1000 / threads)
                    .append(" seconds.")
                    .append(System.lineSeparator());
        }
        return sb.toString();
    }

    public List<Future<CrackResult>> start() throws NoSuchAlgorithmException {
        executor = Executors.newFixedThreadPool(threads);
        List<Future<CrackResult>> list = new ArrayList<>();

        // create a thread for each start character
        for(int usedSize = minSize; usedSize <= maxSize; usedSize++) {
            for (char anAlphabet : alphabet) {
                Md5CrackWorker md5CrackWorker = new Md5CrackWorker(hash, prefix + anAlphabet, usedSize - 1, alphabet);
                Future<CrackResult> future = executor.submit(md5CrackWorker);
                list.add(future);
            }
        }

        return list;
    }

    /**
     * kill all running threads in the executor
     */
    public void stop() {
        executor.shutdownNow();
    }

    /**
     * how many calculations are necessary in worst case
     * @param alphabetSize
     *          amount of chars in the alphabet
     * @param stringLength
     *          amount of unknown chars of the plaintext string
     * @return
     *          amount of calculations needed for a 100% check.
     */
    public static BigInteger getNeededCalculations(int alphabetSize, int stringLength) {
        return (BigInteger.valueOf(alphabetSize)).pow(stringLength);
    }

    /**
     * calculates how many seconds are needed for the passed environment
     * @param alphabetSize
     *          amount of chars in the alphabet
     * @param stringLength
     *          amount of unknown chars of the plaintext string
     * @param cracksPerMilliSecond
     *          how many calculations per milli second are available on the current machine
     * @return
     *          runtime for 100% search in seconds
     *          normally the correct plaintext will be found before all other options are checked
     */
    public static long calculateTime(int alphabetSize, int stringLength, long cracksPerMilliSecond) {
        BigInteger neededCalculations = getNeededCalculations(alphabetSize, stringLength);
        return neededCalculations.divide(BigInteger.valueOf(cracksPerMilliSecond)).longValue();
    }

    /**
     * converts a byte array to a String using hex to write down the bytes
     * @param arrayBytes
     *          The byte array to become converted to hex
     * @return
     *          A String with the hex representation of the byte array
     */
    private static String byteArrayToHexString(byte[] arrayBytes) {
        StringBuilder stringBuffer = new StringBuilder();
        for (byte arrayByte : arrayBytes) {
            stringBuffer.append(Integer.toString((arrayByte & 0xff) + 0x100, 16).substring(1));
        }
        return stringBuffer.toString();
    }

    /**
     * converts a hex string to a byte array
     * @param s
     *          The hex string representing a byte array
     * @return
     *          The byte array represented by the passed string
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
