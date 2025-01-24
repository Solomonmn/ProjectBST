# ProjectBST


This project implements a B-Tree index file with commands to create, open, insert, search, load, print, and extract data. A B-Tree of minimal degree 10 is used, storing keys/values in big-endian format. The index file consists of 512-byte blocks. The first block stores the file header, and subsequent blocks store nodes.

**Compilation:**  
`javac *.java`

**Execution:**  
`java BTreeManager`

**Commands:**  
- **create:** Create a new index file  
- **open:** Open an existing index file  
- **insert:** Insert a key/value pair  
- **search:** Search for a key  
- **load:** Insert pairs from a file  
- **print:** Display all pairs in sorted order  
- **extract:** Save pairs to a file  
- **quit:** Exit

Follow prompts as directed.  
