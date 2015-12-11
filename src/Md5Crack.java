import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Callable;

/**
 * Created by Hendrik on 10.12.2015.
 */
public class Md5Crack implements Callable<CrackResult> {

    private MessageDigest md; // object to generate md5 hash
    private byte[] hash;
    private String prefix;
    private int size; // how many chars are taken from the alphabet
    private byte[] alphabet; // allowed chars

    private byte[] testPlainText; // String to be checked as an answer


    public int getSize() {
        return size;
    }

    public String getPrefix() {
        return (new String(testPlainText).substring(0, testPlainText.length - size));
    }

    public Md5Crack(String hash, String prefix, int size, char[] alphabet) throws NoSuchAlgorithmException {
        md = MessageDigest.getInstance("MD5");

        this.hash = hexStringToByteArray(hash);
        this.prefix = prefix;
        this.size = size;

        // init alphabet
        this.alphabet = new byte[alphabet.length];
        for(int i = 0; i < alphabet.length; i++) {
            this.alphabet[i] = (byte)alphabet[i];
        }

        // init plaintext
        testPlainText = new byte[prefix.length() + size];
        for(int i = 0; i < prefix.length(); i++) {
            testPlainText[i] = (byte)prefix.charAt(i);
        }

    }



    @Override
    public CrackResult call() {
        return crack();
    }

    public CrackResult crack() {
        long start = System.currentTimeMillis();
        String crack = crack(size);
        return new CrackResult(crack, prefix, size, alphabet.length, System.currentTimeMillis() - start);
    }

    /**
     * cracks the hash in attribute 'hash' based on brute force
     * @param size
     *          current amount of used chars from alphabet at the end of the array testPlainText
     * @return
     *          The plaintext if found or null if nothing was found
     */
    private String crack(int size) {

        // as long as there are undefined chars try everyone
        if(size > 0) {
            for(byte selectedChar : alphabet) {
                testPlainText[testPlainText.length - size] = selectedChar; // try a char from the alphabet
                String result = crack(size - 1);
                if (result != null) { // found the solution
                    return result;
                }
            }
        } else { // all places filled
            // get hashed value
            md.update(testPlainText);
            byte[] testHash = md.digest();
            //System.out.println("I try " + new String(testPlainText) + " with hash " + byteArrayToHexString(testHash));
            if(isByteArrayEquals(hash, testHash)) { // Solution found
                return new String(testPlainText);
            }
        }
        return null;
    }


    /**
     * checks the two arrays to be equals.
     * Expects both arrays to have same size.
     * @param a
     *          First array
     * @param b
     *          Second array
     * @return
     *          true if both arrays are equals, false otherwise
     */
    private boolean isByteArrayEquals(byte[] a, byte[] b) {
        for(int i = 0; i < a.length; i++) {
            if(a[i] != b[i]) return false;
        }
        return true;
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

    public static long calculateTime(int alphabetSize, int stringLength, int cracksPerSecond) {
        return (long)Math.pow(alphabetSize, stringLength) / cracksPerSecond;
    }
}
