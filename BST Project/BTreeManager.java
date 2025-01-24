import java.io.*;

public class BTreeManager {
    private static void printMenu(String currentFile) {
        System.out.println();
        if (currentFile != null && !currentFile.isEmpty()) {
            System.out.println("--- Menu (" + currentFile + ") ---");
        } else {
            System.out.println("--- Menu ---");
        }

        System.out.println("create    Create new index");
        System.out.println("open      Set current index");
        System.out.println("insert    Insert a new key/value pair into current index");
        System.out.println("search    Search for a key in current index");
        System.out.println("load      Insert key/value pairs from a file into current index");
        System.out.println("print     Print all key/value pairs in current index in key order");
        System.out.println("extract   Save all key/value pairs in current index into a file");
        System.out.println("quit      Exit the program");
        System.out.println();
    }

    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        IndexFile currentFile = null;
        BTree currentTree = null;
        String currentFilename = "";

        while(true) {
            printMenu(currentFile != null && currentFile.isOpen() ? currentFilename : "");
            System.out.print("Enter command: ");
            String line = null;
            try {
                line = br.readLine();
            } catch(IOException e) {
                e.printStackTrace();
                break;
            }
            if(line == null) break;
            line = line.trim().toLowerCase();

            if(line.equals("quit")) {
                break;
            } else if(line.equals("create")) {
                try {
                    System.out.print("Enter filename: ");
                    String fname = br.readLine();
                    if(fname == null) continue;
                    fname = fname.trim();
                    IndexFile f = new IndexFile(fname);
                    boolean overwrite = true;
                    if(new File(fname).exists()) {
                        if(!Util.promptOverwrite(br, fname)) {
                            continue;
                        }
                    }
                    if(!f.create(overwrite)) {
                        System.out.println("Could not create file.");
                        continue;
                    }
                    currentFile = f;
                    currentTree = new BTree(currentFile);
                    currentFilename = fname;
                    System.out.println("Index '" + fname + "' created.");
                } catch(IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if(line.equals("open")) {
                try {
                    System.out.print("Enter filename: ");
                    String fname = br.readLine();
                    if(fname == null) continue;
                    fname = fname.trim();
                    IndexFile f = new IndexFile(fname);
                    if(!f.open()) {
                        System.out.println("Error: Could not open file or invalid magic number.");
                        continue;
                    }
                    currentFile = f;
                    currentTree = new BTree(currentFile);
                    currentFilename = fname;
                    System.out.println("Index '" + fname + "' opened.");
                } catch(IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if(line.equals("insert")) {
                if(currentFile == null || !currentFile.isOpen()) {
                    System.out.println("No open file.");
                    continue;
                }
                try {
                    System.out.print("Enter key: ");
                    String skey = br.readLine(); 
                    if(skey==null) continue;
                    System.out.print("Enter value: ");
                    String sval = br.readLine(); 
                    if(sval==null) continue;
                    long k = Long.parseUnsignedLong(skey.trim());
                    long v = Long.parseUnsignedLong(sval.trim());
                    currentTree.insert(k,v);
                    // You could print a success message if desired:
                    // System.out.println("Inserted (" + k + "," + v + ").");
                } catch(NumberFormatException e) {
                    System.out.println("Invalid input.");
                } catch(IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if(line.equals("search")) {
                if(currentFile == null || !currentFile.isOpen()) {
                    System.out.println("No open file.");
                    continue;
                }
                try {
                    System.out.print("Enter the key to search for: ");
                    String skey = br.readLine(); if(skey==null) continue;
                    long k = Long.parseUnsignedLong(skey.trim());
                    Long val = currentTree.search(k);
                    if(val == null) {
                        System.out.println("Key '" + k + "' not found.");
                    } else {
                        System.out.println("Value '" + val + "' is at key '" + k + "'");
                    }
                } catch(NumberFormatException e) {
                    System.out.println("Invalid input.");
                } catch(IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if(line.equals("load")) {
                if(currentFile == null || !currentFile.isOpen()) {
                    System.out.println("No open file.");
                    continue;
                }
                try {
                    System.out.print("Enter load filename: ");
                    String lf = br.readLine();
                    if(lf==null) continue;
                    lf=lf.trim();
                    currentTree.loadFromFile(lf);
                } catch(IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if(line.equals("print")) {
                if(currentFile == null || !currentFile.isOpen()) {
                    System.out.println("No open file.");
                    continue;
                }
                try {
                    currentTree.printAll();
                } catch(IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else if(line.equals("extract")) {
                if(currentFile == null || !currentFile.isOpen()) {
                    System.out.println("No open file.");
                    continue;
                }
                try {
                    System.out.print("Enter filename to extract to: ");
                    String ef = br.readLine();
                    if(ef==null) continue;
                    ef=ef.trim();
                    File outF = new File(ef);
                    if(outF.exists()) {
                        if(!Util.promptOverwrite(br, ef)) {
                            continue;
                        }
                    }
                    currentTree.extractToFile(ef);
                    System.out.println("All key/value pairs extracted to '" + ef + "'.");
                } catch(IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Unknown command.");
            }
        }

        try {
            if(currentFile != null) currentFile.close();
        } catch(IOException e) {
        }
        System.out.println("Goodbye.");
    }
}
