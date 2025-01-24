import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Util {
    public static final int BLOCK_SIZE = 512;
    public static final String MAGIC = "4337PRJ3";
    public static final int T = 10; 
    public static final int MAX_KEYS = 2*T - 1; 
    public static final int MAX_CHILDREN = 2*T; 
    

    public static byte[] longToBytes(long value) {
        ByteBuffer bb = ByteBuffer.allocate(8);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putLong(value);
        return bb.array();
    }

    public static long bytesToLong(byte[] arr, int offset) {
        ByteBuffer bb = ByteBuffer.wrap(arr, offset, 8);
        bb.order(ByteOrder.BIG_ENDIAN);
        return bb.getLong();
    }

    public static boolean promptOverwrite(BufferedReader br, String filename) throws IOException {
        while(true) {
            System.out.print(filename + " already exists. Overwrite? (y/n): ");
            String line = br.readLine();
            if(line == null) return false;
            line = line.trim().toLowerCase();
            if(line.equals("y") || line.equals("yes")) return true;
            if(line.equals("n") || line.equals("no")) return false;
        }
    }
}
