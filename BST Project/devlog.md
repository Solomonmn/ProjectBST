# Devlog

## 2024-12-07 09:00

**“I thought about working on the B-tree project. I knew that I had to do a B-Tree index file structure with a minimal degree of 10.’’** From the specification I am to keep in memory not more than three nodes and I am also to implement big-endian storage. I have outlined the encompasses of basic I/O and node graphs which omits what seems to be the hardest ¬− – node insertion as well as splitting them. Finally, however, I am to ensure that the commit history is accurately recorded and ensure the upkeep of a devlog.

**Summary of what's to be done in this session:** Such as the file header creation and opening of files, and then checking the header for errors. The procedures to be performed include `create` and `open` command instructions, make sure that the expected magic number; `4337PRJ3` fits the criteria for being written and read properly, and install some primary error checking. When done, I will release the alterations.

*Commit:* `git add .; git commit -m “Add the commands for creating and opening files and add header validation ”`

## 2024-12-07 12:30

**Reflections:** The `create` and `open` activities work like a charm. The last case correctly places the values for the magic number, the header block, and the initial root and next block identifiers. I verified that calling to open a file not existing went through gracefully and included protections for the case in errors. It has a nice feeling about it. I do notice some weak spots and one of them is that I will definitely require another unit for reading and writing nodes. Furthermore, I would like to see begin loaded nodes when it is appropriate.

**Plan this session:** Create a `BTreeNode` class and implement the read/write logic of the node. Once done, I will push these changes. I also would like to verify that the conversion to big endian is working fine as well.

*Commit:* g [gremiis@i-htop dt]$ git add .; git commit -m ‘adding class for BTreeNode and implementing node reading and writing functionality’

## 2024-12-08 10:00

**Do I have completed tasks:** Till now, I have completed node I/O. Moving forward insertion needs to be done. Insertion of a node into a b-tree is the most difficult because the parent nodes have to be separated and their pointers have to be taken into account. In this case, I will require a helper within the insertion function of the binary tree which will check for fullness of the node before proceeding to a splitting. In addition, it should be noted that from the beginning, I take care that memory utilization should not be more than 3 nodes. One method is to load in 1 node, process in 1 node, and write in 1 node without delay.

**Plan this session: ** Use the ‘insert’ method, ‘insert (key, value)’. Begin with the adding of nodes to an empty tree. Then initialize a test that would allow for finding a required node until a split occurs. To build confidence in the model, I will be able to hard code a few tests. Upon this success, I will push the code.

*Commit:* g [gremiis@i-htop dt]$ git add .; git commit -m ‘writing and flushing node splits along with the basic insertion logic.’

## 2024-12-08 14:00

**Reflections: ** Most of the work pseudocodes on inserting within those three have been completed – only the insertion of a single node needs to be shown. Currently, I can insert within two states – an empty state and a state of one level. Splitting appears like it is functional, however stronger tests must be deployed. There is an appropriate error message printed when the duplicates are being searched as per the caption required.

**Plan this session:** Implement `search(key)` and run some tests with a few of the values inserted. Once I do that successfully, I'll initiate a small scenario and see if returned expected values. Then I will commit the changes.

*Commit:* `git add .; git commit -m "Implement search functionality and test insertion"`

## 2024-12-09 09:00

**Thoughts so far:** Insert and search have proved to be more or less reliable. It is now time for me to complete `load` and `print` implementation. The `load` command loads and inserts comma separated pairs from a file. `print` goes through the full tree in order and this entails sorting. It involves the use of an in-order traversal. Extraction in this case is just the opposite of load where it is a case of pair writing out. It is necessary for me to design needed prompts for the end user and the appropriate format of documents.

**Plan this session:** The tasks for the day include ‘print’ and the ‘extract’ commands and completing the user prompts by adjusting positioning so as to give an appealing final look. Thereafter, I’ll commit and regard the project as almost finished and with minimal fuss. 

*Commit:* `git add .; git commit -m "Add print and extract functionalities, finalize prompt style"