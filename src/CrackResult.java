/**
 * Created by Hendrik on 10.12.2015.
 */
public class CrackResult {
    String result;
    String prefix;
    int size;
    int alphabetSize;
    long time;

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

    public int getCracksPerSecond() {
        int cracksPerSecond = -1;
        if(time > 1000)
            cracksPerSecond = (int) (((int) Math.pow(alphabetSize, size)) / (time / 1000));
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

        return (isCracked() ? "CRACKED" : "Not cracked") + " - " + pattern + " - Cracks per Second: " + getCracksPerSecond();
    }
}