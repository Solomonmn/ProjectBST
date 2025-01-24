import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class IndexFile {
    private RandomAccessFile raf;
    private String filename;
    private long rootBlockId;
    private long nextBlockId;

    public IndexFile(String filename) {
        this.filename = filename;
    }

    public boolean create(boolean overwrite) throws IOException {
        File f = new File(filename);
        if(f.exists() && !overwrite) {
            return false;
        }
        raf = new RandomAccessFile(f, "rw");
       
        writeHeader(0, 1); 
        return true;
    }

    public boolean open() throws IOException {
        File f = new File(filename);
        if(!f.exists()) {
            return false;
        }
        raf = new RandomAccessFile(f, "rw");
        if(!readHeader()) {
            raf.close();
            raf = null;
            return false;
        }
        return true;
    }

    public boolean isOpen() {
        return raf != null;
    }

    public void close() throws IOException {
        if(raf != null) raf.close();
        raf = null;
    }

    private void writeHeader(long rootId, long nextId) throws IOException {
        raf.seek(0);
        byte[] block = new byte[Util.BLOCK_SIZE];
        byte[] magic = Util.MAGIC.getBytes("ASCII");
        System.arraycopy(magic, 0, block, 0, magic.length);

        byte[] rootBytes = Util.longToBytes(rootId);
        System.arraycopy(rootBytes, 0, block, 8, 8);
        byte[] nextBytes = Util.longToBytes(nextId);
        System.arraycopy(nextBytes, 0, block, 16, 8);

        raf.write(block);
        this.rootBlockId = rootId;
        this.nextBlockId = nextId;
    }

    private boolean readHeader() throws IOException {
        raf.seek(0);
        byte[] block = new byte[Util.BLOCK_SIZE];
        int read = raf.read(block);
        if(read < Util.BLOCK_SIZE) return false;

        String magicCheck = new String(block,0,8,"ASCII");
        if(!Util.MAGIC.equals(magicCheck)) return false;

        this.rootBlockId = Util.bytesToLong(block,8);
        this.nextBlockId = Util.bytesToLong(block,16);
        return true;
    }

    public long getRootId() { return rootBlockId; }
    public void setRootId(long rootId) throws IOException {
        this.rootBlockId = rootId;
        writeHeader(rootBlockId, nextBlockId);
    }

    public long getNextBlockId() { return nextBlockId; }

    public long allocateNode() throws IOException {
        long id = nextBlockId;
        nextBlockId++;
        writeHeader(rootBlockId, nextBlockId);
        return id;
    }

    public BTreeNode readNode(long blockId) throws IOException {
        raf.seek(blockId * Util.BLOCK_SIZE);
        byte[] block = new byte[Util.BLOCK_SIZE];
        int read = raf.read(block);
        if(read < Util.BLOCK_SIZE) return null;

        BTreeNode node = new BTreeNode();
        node.blockId = Util.bytesToLong(block, 0);
        node.parentId = Util.bytesToLong(block, 8);
        long numKeysL = Util.bytesToLong(block, 16);
        node.numKeys = (int)numKeysL;

        int offset = 24;
        for(int i=0;i<Util.MAX_KEYS;i++) {
            node.keys[i] = Util.bytesToLong(block, offset);
            offset+=8;
        }
        for(int i=0;i<Util.MAX_KEYS;i++) {
            node.values[i] = Util.bytesToLong(block, offset);
            offset+=8;
        }
        for(int i=0;i<Util.MAX_CHILDREN;i++) {
            node.children[i] = Util.bytesToLong(block, offset);
            offset+=8;
        }

        return node;
    }

    public void writeNode(BTreeNode node) throws IOException {
        byte[] block = new byte[Util.BLOCK_SIZE];
        System.arraycopy(Util.longToBytes(node.blockId),0,block,0,8);
        System.arraycopy(Util.longToBytes(node.parentId),0,block,8,8);
        System.arraycopy(Util.longToBytes(node.numKeys),0,block,16,8);

        int offset = 24;
        for(int i=0;i<Util.MAX_KEYS;i++) {
            System.arraycopy(Util.longToBytes(node.keys[i]),0,block,offset,8);
            offset+=8;
        }
        for(int i=0;i<Util.MAX_KEYS;i++) {
            System.arraycopy(Util.longToBytes(node.values[i]),0,block,offset,8);
            offset+=8;
        }
        for(int i=0;i<Util.MAX_CHILDREN;i++) {
            System.arraycopy(Util.longToBytes(node.children[i]),0,block,offset,8);
            offset+=8;
        }

        raf.seek(node.blockId * Util.BLOCK_SIZE);
        raf.write(block);
    }

    public boolean fileExists() {
        return new File(filename).exists();
    }
}
