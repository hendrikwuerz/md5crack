import java.math.BigInteger;

/**
 * Created by Hendrik on 10.12.2015.
 */
public class CrackResult {
    String result; // The result (plain text) from the calculation, null if nothing was found
    String prefix; // The known prefix of the plaintext
    int size; // number of unknown chars behind the prefix
    int alphabetSize; // amount of chars is the alphabet
    long time; // needed time in ms for the calculation

    public CrackResult(String result, String prefix, int size, int alphabetSize, long time) {
        this.result = result;
        this.prefix = prefix;
        this.size = size;
        this.alphabetSize = alphabetSize;
        this.time = time;
    }

    public boolean isCracked() {
        return result != null;
    }

    public int getCracksPerMilliSecond() {
        int cracksPerSecond = -1;
        if(time > 1000) {
            BigInteger neededCalculations = Md5Crack.getNeededCalculations(alphabetSize, size);
            cracksPerSecond = neededCalculations.divide(BigInteger.valueOf(time)).intValue();
        }
        return cracksPerSecond;
    }

    @Override
    public String toString() {
        String pattern = result;
        if(!isCracked()) {
            String extend = "";
            for(int i = 0; i < size; i++) {
                extend += "?";
            }
            pattern = prefix + extend;
        }

        return (isCracked() ? "CRACKED" : "Not cracked") +
                " - " + pattern +
                " - Used time: " + time +
                " - Total Cracks: " + Md5Crack.getNeededCalculations(alphabetSize, size) +
                " - Cracks per Millisecond: " + getCracksPerMilliSecond();
    }
}