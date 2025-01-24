public class BTreeNode {
    public long blockId;
    public long parentId;
    public int numKeys;
    public long[] keys;
    public long[] values;
    public long[] children; 

    public BTreeNode() {
        keys = new long[Util.MAX_KEYS];
        values = new long[Util.MAX_KEYS];
        children = new long[Util.MAX_CHILDREN];
        blockId = 0;
        parentId = 0;
        numKeys = 0;
    }

    public boolean isLeaf() {
        for (long c : children) {
            if (c != 0) return false;
        }
        return true;
    }
}
