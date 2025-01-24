import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BTree {
    private IndexFile indexFile;

    public BTree(IndexFile file) {
        this.indexFile = file;
    }

    public boolean isEmpty() {
        return indexFile.getRootId() == 0;
    }

    public void insert(long key, long value) throws IOException {
        if(isEmpty()) {
          
            BTreeNode root = new BTreeNode();
            root.blockId = indexFile.allocateNode();
            root.numKeys = 1;
            root.keys[0] = key;
            root.values[0] = value;
            indexFile.writeNode(root);
            indexFile.setRootId(root.blockId);
            return;
        }

        if(search(key) != null) {
            System.out.println("Error: Key already exists.");
            return;
        }

        long rootId = indexFile.getRootId();
        BTreeNode root = indexFile.readNode(rootId);
        if(root.numKeys == Util.MAX_KEYS) {
            BTreeNode newRoot = new BTreeNode();
            newRoot.blockId = indexFile.allocateNode();
            newRoot.children[0] = root.blockId;
            root.parentId = newRoot.blockId;
            indexFile.writeNode(root);

            splitChild(newRoot, 0, root);
            insertNonFull(newRoot, key, value);
            indexFile.writeNode(newRoot);
            indexFile.setRootId(newRoot.blockId);
        } else {
            insertNonFull(root, key, value);
            indexFile.writeNode(root);
            if(root.parentId == 0) {
                indexFile.setRootId(root.blockId);
            }
        }
    }

    private void insertNonFull(BTreeNode node, long key, long value) throws IOException {
        int i = node.numKeys - 1;
        if(node.isLeaf()) {
            while(i>=0 && key<node.keys[i]) {
                node.keys[i+1] = node.keys[i];
                node.values[i+1] = node.values[i];
                i--;
            }
            node.keys[i+1] = key;
            node.values[i+1] = value;
            node.numKeys++;
            indexFile.writeNode(node);
        } else {
            while(i>=0 && key<node.keys[i]) i--;
            i++;
            BTreeNode child = indexFile.readNode(node.children[i]);
            if(child.numKeys == Util.MAX_KEYS) {
                splitChild(node, i, child);
                if(key > node.keys[i]) {
                    i++;
                }
            }
            child = indexFile.readNode(node.children[i]);
            insertNonFull(child, key, value);
        }
    }

    private void splitChild(BTreeNode parent, int i, BTreeNode fullChild) throws IOException {
        BTreeNode newNode = new BTreeNode();
        newNode.blockId = indexFile.allocateNode();
        newNode.parentId = parent.blockId;

        int t = Util.T; 
        newNode.numKeys = t-1;
        for(int j=0; j<t-1; j++) {
            newNode.keys[j] = fullChild.keys[j+t];
            newNode.values[j] = fullChild.values[j+t];
        }

        if(!fullChild.isLeaf()) {
            for(int j=0; j<t; j++) {
                newNode.children[j] = fullChild.children[j+t];
                if(newNode.children[j] != 0) {
                    BTreeNode c = indexFile.readNode(newNode.children[j]);
                    c.parentId = newNode.blockId;
                    indexFile.writeNode(c);
                }
            }
        }

        fullChild.numKeys = t-1;

        for(int j=parent.numKeys; j>=i+1; j--) {
            parent.children[j+1] = parent.children[j];
        }
        parent.children[i+1] = newNode.blockId;

        for(int j=parent.numKeys-1; j>=i; j--) {
            parent.keys[j+1] = parent.keys[j];
            parent.values[j+1] = parent.values[j];
        }
        parent.keys[i] = fullChild.keys[t-1];
        parent.values[i] = fullChild.values[t-1];
        parent.numKeys++;

        indexFile.writeNode(fullChild);
        indexFile.writeNode(newNode);
        indexFile.writeNode(parent);
    }

    public Long search(long key) throws IOException {
        if(isEmpty()) return null;
        BTreeNode root = indexFile.readNode(indexFile.getRootId());
        return searchNode(root, key);
    }

    private Long searchNode(BTreeNode node, long key) throws IOException {
        int i=0;
        while(i<node.numKeys && key>node.keys[i]) {
            i++;
        }
        if(i<node.numKeys && key==node.keys[i]) {
            return node.values[i];
        } else if(node.isLeaf()) {
            return null;
        } else {
            BTreeNode child = indexFile.readNode(node.children[i]);
            return searchNode(child, key);
        }
    }

    public void printAll() throws IOException {
        if(isEmpty()) {
            return;
        }
        List<Long> keys = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        traverse(indexFile.getRootId(), keys, values);
        for(int i=0;i<keys.size();i++) {
            System.out.println(keys.get(i) + "," + values.get(i));
        }
    }

    private void traverse(long blockId, List<Long> keys, List<Long> values) throws IOException {
        BTreeNode node = indexFile.readNode(blockId);
        for(int i=0;i<node.numKeys;i++) {
            if(node.children[i]!=0) {
                traverse(node.children[i], keys, values);
            }
            keys.add(node.keys[i]);
            values.add(node.values[i]);
        }
        if(node.children[node.numKeys]!=0) {
            traverse(node.children[node.numKeys], keys, values);
        }
    }

    public void extractToFile(String outFilename) throws IOException {
        if(isEmpty()) {
            try(PrintWriter pw = new PrintWriter(new FileWriter(outFilename))) {}
            return;
        }
        List<Long> keys = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        traverse(indexFile.getRootId(), keys, values);
        try(PrintWriter pw = new PrintWriter(new FileWriter(outFilename))) {
            for(int i=0;i<keys.size();i++) {
                pw.println(keys.get(i) + "," + values.get(i));
            }
        }
    }

    public void loadFromFile(String loadFile) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(loadFile))) {
            String line;
            while((line = br.readLine()) != null) {
                line = line.trim();
                if(line.isEmpty()) continue;
                String[] parts = line.split(",");
                if(parts.length!=2) continue;
                long k,v;
                try {
                    k = Long.parseUnsignedLong(parts[0]);
                    v = Long.parseUnsignedLong(parts[1]);
                } catch(NumberFormatException e) {
                    System.out.println("Invalid line: "+line);
                    continue;
                }
                if(search(k)!=null) {
                    System.out.println("Error: Key already exists: "+k);
                    continue;
                }
                insert(k,v);
            }
        }
    }
}
