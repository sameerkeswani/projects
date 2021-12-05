package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.TreeMap;

import static gitlet.Directories.*;

/** Class for all the commands for a Gitlet repository.
 * @author Sameer Keswani
 */

public class Commands {

    /** Constructor for Commands. */
    public Commands() {
        if (STAGE.exists()) {
            _stage = Utils.readObject(STAGE, Stage.class);
        }
        if (CURRENTBRANCH.exists()) {
            _currentBranch = Utils.readObject(CURRENTBRANCH, Branch.class);
            _head = _currentBranch.getHead();
        }


    }

    /** Command that initializes a Gitlet repo. */
    public void init() {
        if (GITLET.exists()) {
            System.out.println("A Gitlet version-control system already "
                    + "exists in the current directory.");
        }
        GITLET.mkdirs();
        COMMITS.mkdirs();
        BLOBS.mkdirs();
        BRANCHES.mkdirs();
        try {
            HEAD.createNewFile();
            STAGE.createNewFile();
            CURRENTBRANCH.createNewFile();
        } catch (IOException e) {
            System.err.println(e);
        }


        Commit initialCommit = new Commit("initial commit", null);
        initialCommit.save();

        _stage = new Stage();
        _stage.saveStage();

        _currentBranch = new Branch("master", initialCommit);
        _currentBranch.saveBranch();
        _currentBranch.saveAsCurrentBranch();

    }

    /** Command adds a text file with NAME to staging area.
     * If the file is in the removal stage, then it is removed
     * from the removal stage. */
    public void add(String name) {
        File addFile = Utils.join(CWD, name);
        if (!(addFile.exists())) {
            System.out.println("File does not exist");
        } else {
            String fileSHA = Utils.sha1(Utils.readContents(addFile));
            if (_stage.getRemoveStage().contains(name)) {
                _stage.removeFromRemoveStage(name);
                _stage.saveStage();
            } else if (_head.getBlobs() != null
                    && _head.getBlobs().containsKey(name)
                    && _head.getBlobs().get(name).equals(fileSHA)) {
                if (_stage.getAddStage().containsKey(name)) {
                    _stage.removeFromAddStage(name);
                } else {
                    _stage.add(name, fileSHA);
                    _stage.saveStage();
                }
            } else {
                _stage.add(name, fileSHA);
                _stage.saveStage();
            }
        }
    }

    /** Command that creates a commit object with a MESSAGE. */
    public void commit(String message) {
        if (_stage.getAddStage().isEmpty()
                && _stage.getRemoveStage().isEmpty()) {
            System.out.println("No changes added to the commit");
        }
        Commit commit = new Commit(message, _head);
        commit.setBlobs(_stage.getAddStage());
        commit.removeBlobs(_stage.getRemoveStage());
        commit.save();
        _currentBranch.setHead(commit);
        _currentBranch.saveAsCurrentBranch();
        _currentBranch.saveBranch();
        commit.saveBlobs();
        _stage.empty();
        _stage.saveStage();
    }

    /** Command that creates a log of the history of all commits up
     * to the current commit.
     */
    public void log() {
        Commit curr = _head;
        while (curr != null) {
            System.out.println("===");
            System.out.println("commit " + curr.getSHA());
            System.out.println("Date: " + curr.getDate());
            System.out.println(curr.getMessage());
            System.out.println();
            curr = curr.getParent();
        }
    }

    /** Takes in a NAME and a boolean ISFILENAME which signifies if
     * the name is that of a text file or a branch. Depending on which
     * it is, it performs the checkout operation on the text file/branch.
     */
    public void checkout(String name, boolean isFileName) {
        if (isFileName) {
            checkoutFile(name);
        } else {
            checkoutBranch(name);
        }
    }

    /** Helper for checkout that checks out a file with NAME. */
    public void checkoutFile(String name) {
        if (_head.getBlobs().containsKey(name)) {
            File blobFile = Utils.join(BLOBS, _head.getBlobs().get(name));
            byte[] serBlob = Utils.readContents(blobFile);
            File cwdFile = Utils.join(CWD, name);
            if (cwdFile.exists()) {
                Utils.restrictedDelete(cwdFile);
            }
            File newFile = Utils.join(CWD, name);
            Utils.writeContents(newFile, serBlob);
        } else {
            System.out.println("File does not exist in that commit.");
        }
    }

    /** Helper for checkout that checks out a branch with NAME. */
    public void checkoutBranch(String name) {
        File branchFile = Utils.join(BRANCHES, name);
        List<String> filesCWD = Utils.plainFilenamesIn(CWD);
        if (!branchFile.exists()) {
            System.out.println("No such branch exists.");
            return;
        } else if (_currentBranch.getName().equals(name)) {
            System.out.println("No need to checkout the current branch");
            return;
        }
        Branch branch = Utils.readObject(branchFile, Branch.class);
        Commit branchHead = branch.getHead();
        for (String fileName : filesCWD) {
            if (branchHead.getBlobs() != null
                    && (branchHead.getBlobs().containsKey(fileName))
                    && (_head.getBlobs() != null
                    && !_head.getBlobs().containsKey(fileName))) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
            } else if (branchHead.getBlobs() != null
                    && (branchHead.getBlobs().containsKey(fileName))
                    && (_head.getBlobs() == null)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first");
            }
        }
        List<String> branchNames = Utils.plainFilenamesIn(BRANCHES);
        if (branchNames.contains(name)) {
            for (String fileName : filesCWD) {
                if ((_head.getBlobs() != null
                        && _head.getBlobs().containsKey(fileName))
                        && (branchHead.getBlobs() != null
                        && !branchHead.getBlobs().containsKey(fileName))) {
                    File fileToDelete = Utils.join(CWD, fileName);
                    Utils.restrictedDelete(fileToDelete);
                }
            }
            if (branchHead.getBlobs() == null) {
                for (String fileName : filesCWD) {
                    File fileToDelete = Utils.join(CWD, fileName);
                    Utils.restrictedDelete(fileToDelete);
                }
            } else {
                for (String fileName : branchHead.getBlobs().keySet()) {
                    File blobFile = Utils.join(BLOBS,
                            branchHead.getBlobs().get(fileName));
                    byte[] serBlob = Utils.readContents(blobFile);
                    File cwdFile = Utils.join(CWD, fileName);
                    if (cwdFile.exists()) {
                        Utils.restrictedDelete(cwdFile);
                    }
                    File newFile = Utils.join(CWD, fileName);
                    Utils.writeContents(newFile, serBlob);

                }
            }
            branch.saveAsCurrentBranch();
            _stage.empty();
        }
    }

    /** Checkout command that takes in commit SHA and a text file
     * NAME and checks this file out of that commit if it exists.
     */
    public void checkout(String sha, String name) {
        List<String> commitFileNames = Utils.plainFilenamesIn(COMMITS);
        File commitFile = null;
        for (String commitSHA : commitFileNames) {
            if (commitSHA.contains(sha)) {
                commitFile = Utils.join(COMMITS, commitSHA);
            }
        }
        if (commitFile == null) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit commit = Utils.readObject(commitFile, Commit.class);
        if (commit.getBlobs().containsKey(name)) {
            File blobFile = Utils.join(BLOBS, commit.getBlobs().get(name));
            byte[] serBlob = Utils.readContents(blobFile);
            File newFile = Utils.join(CWD, name);
            Utils.writeContents(newFile, serBlob);
        } else {
            System.out.println("File does not exist in that commit");
        }
    }

    /** Command that removes and untracks a file with NAME
     * if it is tracked. Removed from adding stage if it is present
     * there.
     */
    public void rm(String name) {
        File removeFile = Utils.join(CWD, name);
        boolean removed = false;
        if (_stage.getAddStage() == null && _head.getBlobs() == null) {
            System.out.println("No reason to remove the file");
        }
        if (_stage.getAddStage() != null
                && _stage.getAddStage().containsKey(name)) {
            removed = true;
            _stage.removeFromAddStage(name);
        }
        if (_head.getBlobs() != null && _head.getBlobs().containsKey(name)) {
            _stage.remove(name);
            Utils.restrictedDelete(removeFile);
            removed = true;
        }
        _stage.saveStage();
        if (!removed) {
            System.out.println("No reason to remove the file");
        }
    }

    /** Command that displays all commits made. */
    public void globalLog() {
        List<String> commits = Utils.plainFilenamesIn(COMMITS);
        for (String commitSHA : commits) {
            File commitFile = Utils.join(COMMITS, commitSHA);
            Commit commit = Utils.readObject(commitFile, Commit.class);
            System.out.println("===");
            System.out.println("commit " + commit.getSHA());
            System.out.println("Date: " + commit.getDate());
            System.out.println(commit.getMessage());
            System.out.println();
        }
    }

    /** Command that finds a certain commit with MESSAGE. */
    public void find(String message) {
        List<String> commits = Utils.plainFilenamesIn(COMMITS);
        boolean found = false;
        for (String commitSHA : commits) {
            File commitFile = Utils.join(COMMITS, commitSHA);
            Commit commit = Utils.readObject(commitFile, Commit.class);
            if (commit.getMessage().equals(message)) {
                found = true;
                System.out.println(commit.getSHA());
            }
        }
        if (!found) {
            System.out.println("Found no commit with that message");
        }
    }

    /** Command that creates a new branch with specified NAME. */
    public void branch(String name) {
        List<String> branchNames = Utils.plainFilenamesIn(BRANCHES);
        if (branchNames.contains(name)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Branch newBranch = new Branch(name, _head);
        newBranch.saveBranch();
    }

    /** Command that prints out current status of our repository. */
    public void status() {
        System.out.println("=== Branches ===");
        System.out.println("*" + _currentBranch.getName());
        List<String> branchNames = Utils.plainFilenamesIn(BRANCHES);
        for (String name : branchNames) {
            if (!name.equals(_currentBranch.getName())) {
                System.out.println(name);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        Set<String> stagedFiles = _stage.getAddStage().keySet();
        for (String name : stagedFiles) {
            System.out.println(name);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");
        for (String name : _stage.getRemoveStage()) {
            System.out.println(name);
        }
        System.out.println();

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    /** Command that removes a branch with NAME. */
    public void rmBranch(String name) {
        List<String> branchNames = Utils.plainFilenamesIn(BRANCHES);
        if (branchNames != null && !branchNames.contains(name)) {
            System.out.println("A branch with that name does not exist");
        } else if (_currentBranch.getName().equals(name)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            File branchFile = Utils.join(BRANCHES, name);
            branchFile.delete();
        }
    }

    /** Command that resets head of current branch to specified commit ID. */
    public void reset(String id) {
        File commitFile = Utils.join(COMMITS, id);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            return;
        }
        List<String> filesCWD = Utils.plainFilenamesIn(CWD);
        Commit commit = Utils.readObject(commitFile, Commit.class);
        for (String fileName : filesCWD) {
            File fileCWD = Utils.join(CWD, fileName);
            if (commit.getBlobs().containsKey(fileName)
                    && !_head.getBlobs().containsKey(fileName)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
            }
        }
        for (String fileName : filesCWD) {
            File fileToDelete = Utils.join(CWD, fileName);
            Utils.restrictedDelete(fileToDelete);
        }
        for (String fileName : commit.getBlobs().keySet()) {
            File blobFile = Utils.join(BLOBS, commit.getBlobs().get(fileName));
            byte[] serBlob = Utils.readContents(blobFile);
            File newFile = Utils.join(CWD, fileName);
            Utils.writeContents(newFile, serBlob);
        }
        _currentBranch.setHead(commit);
        _currentBranch.saveAsCurrentBranch();
        _currentBranch.saveBranch();
        _stage.empty();
        _stage.saveStage();
    }

    /** Command that merges given BRANCHNAME to the current branch. */
    public void merge(String branchName) {
        boolean error = mergeErrors(branchName);
        if (error) {
            return;
        }
        File branchFile = Utils.join(BRANCHES, branchName);
        Branch givenBranch = Utils.readObject(branchFile, Branch.class);
        Commit given = givenBranch.getHead();
        boolean untracked = checkUntracked(given);
        if (untracked) {
            return;
        }
        Commit splitPoint = findLatestCommonAncestor(given, 0);
        if (splitPoint.getSHA().equals(given.getSHA())) {
            System.out.println("Given branch is an ancestor "
                    + "of the current branch.");
            return;
        } else if (splitPoint.getSHA().equals(_head.getSHA())) {
            checkout(givenBranch.getName(), false);
            System.out.println("Current branch fast-forwarded.");
            return;
        } else {
            boolean conflict = false;
            if (splitPoint.getBlobs() != null) {
                for (String fileName : splitPoint.getBlobs().keySet()) {
                    boolean error2 = mergeHelper(fileName,
                            splitPoint, given, _head);
                    if (error2) {
                        conflict = true;
                    }
                }
            }
            for (String fileName : _head.getBlobs().keySet()) {
                if (((splitPoint.getBlobs() == null)
                        || !splitPoint.getBlobs().containsKey(fileName))
                        && !given.getBlobs().containsKey(fileName)) {
                    int pass;
                } else if (((splitPoint.getBlobs() == null)
                        || !splitPoint.getBlobs().containsKey(fileName))
                        && !_head.getBlobs().get(fileName)
                        .equals(given.getBlobs().get(fileName))) {
                    conflict = true;
                    mergeHelper2(fileName, given, _head);
                }
            }
            for (String fileName : given.getBlobs().keySet()) {
                if (((splitPoint.getBlobs() == null)
                        || !splitPoint.getBlobs().containsKey(fileName))
                        && !_head.getBlobs().containsKey(fileName)) {
                    checkout(given.getSHA(), fileName);
                    _stage.add(fileName, given.getBlobs().get(fileName));
                }
            }
            if (conflict) {
                System.out.println("Encountered a merge conflict.");
            }
        }
        mergeHelper3(givenBranch, given);
    }

    /** Method that accounts for errors when BRANCHNAME is passed into
     * checkout. Returns true if there is an error.
     */
    public boolean mergeErrors(String branchName) {
        File branchFile = Utils.join(BRANCHES, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            return true;
        } else if (_stage.getAddStage().size() != 0
                || _stage.getRemoveStage().size() != 0) {
            System.out.println("You have uncommitted changes.");
            return true;
        } else if (branchName.equals(_currentBranch.getName())) {
            System.out.println("Cannot merge a branch with itself.");
            return true;
        } else {
            return false;
        }
    }

    /** Adds all visited nodes from C to VISITED arraylist. */
    public void markVisited(Commit c, ArrayList<String> visited) {
        while (c != null) {
            if (!visited.contains(c.getSHA())) {
                visited.add(c.getSHA());
            }
            if (c.getMerged() != null) {
                markVisited(c.getMerged(), visited);
            }
            c = c.getParent();
        }
    }

    /** Adds all possible split points that can be reached FROM
     * a certain DISTANCE to a TreeMap called SPLITPOINTS, which
     * keeps track of all possible ancestors in the commit tree.
     * Uses an arraylist of VISITED commits to do this.
     */
    public void findSplitPoints(Commit from, int distance,
                                TreeMap<Integer, Commit> splitPoints,
                                ArrayList<String> visited) {
        Commit curr = from;
        int dist = distance;
        while (curr != null) {
            if (visited.contains(curr.getSHA())) {
                splitPoints.put(dist, curr);
            } else if (curr.getMerged() != null) {
                findSplitPoints(curr.getMerged(),
                        dist + 1, splitPoints, visited);
            }
            curr = curr.getParent();
            dist += 1;
        }
    }

    /** Returns a commit that is the latest common ancestor of
     * the GIVEN and current commits. This ancestor is the smallest
     * DISTANCE away from the current branch head.
     */
    public Commit findLatestCommonAncestor(Commit given, int distance) {
        ArrayList<String> visited = new ArrayList<String>();
        markVisited(given, visited);
        TreeMap<Integer, Commit> splits = new TreeMap<Integer, Commit>();
        findSplitPoints(_head, distance, splits, visited);
        return splits.get(splits.firstKey());

    }

    /** Helper for merge function.
     * @param fileName - name of file.
     * @param splitPoint - the latest common ancestor.
     * @param given - given branch head.
     * @param current - current branch head.
     * @return - true if there is a conflict in the process.
     */
    public boolean mergeHelper(String fileName, Commit splitPoint,
                                Commit given, Commit current) {
        boolean conflict = false;
        String blobSHA = splitPoint.getBlobs().get(fileName);
        if (given.getBlobs().containsKey(fileName)
                && current.getBlobs().containsKey(fileName)) {
            String givenSHA = given.getBlobs().get(fileName);
            String currentSHA = current.getBlobs().get(fileName);
            if (blobSHA.equals(currentSHA) && !blobSHA.equals(givenSHA)) {
                checkout(given.getSHA(), fileName);
                _stage.add(fileName, given.getBlobs().get(fileName));
                int pass;
            } else if (!blobSHA.equals(currentSHA)
                    && blobSHA.equals(givenSHA)) {
                int pass;
            } else if (!blobSHA.equals(currentSHA)
                    && (givenSHA.equals(currentSHA))) {
                int pass;
            } else {
                mergeHelper2(fileName, given, current);
                conflict = true;
            }
        } else if (splitPoint.getBlobs().containsKey(fileName)
                && (!given.getBlobs().containsKey(fileName)
                && !current.getBlobs().containsKey(fileName))) {
            int doNothing;
        } else if (((given.getBlobs() == null)
                || !given.getBlobs().containsKey(fileName))
                    && current.getBlobs().containsKey(fileName)) {
            if (blobSHA.equals(current.getBlobs().get(fileName))) {
                rm(fileName);
            } else {
                mergeHelper2(fileName, null, current);
                conflict = true;
            }
        } else if (given.getBlobs().containsKey(fileName)
                && ((current.getBlobs() == null)
                || !current.getBlobs().containsKey(fileName))) {
            if (!blobSHA.equals(given.getBlobs().get(fileName))) {
                mergeHelper2(fileName, given, null);
                conflict = true;
            }
        } else {
            mergeHelper2(fileName, given, current);
            conflict = true;
        }
        return conflict;
    }

    /** Helper2 for merge function.
     * @param fileName - Name of the file that has a conflict.
     * @param given - Given branch head.
     * @param current - Current branch head.
     */
    public void mergeHelper2(String fileName, Commit given, Commit current) {
        if (given == null) {
            File currentFile = Utils.join(BLOBS,
                    current.getBlobs().get(fileName));
            byte[] currSer = Utils.readContents(currentFile);
            File file = Utils.join(CWD, fileName);
            Utils.writeContents(file, "<<<<<<< HEAD\n",
                    currSer, "=======\n", ">>>>>>>\n");
        } else if (current == null) {
            File givenFile = Utils.join(BLOBS, given.getBlobs().get(fileName));
            byte[] givenSer = Utils.readContents(givenFile);
            File file = Utils.join(CWD, fileName);
            Utils.writeContents(file, "<<<<<<< HEAD\n",
                    "=======\n", givenSer, ">>>>>>>\n");
        } else {
            File givenFile = Utils.join(BLOBS,
                    given.getBlobs().get(fileName));
            File currentFile = Utils.join(BLOBS,
                    current.getBlobs().get(fileName));
            byte[] currSer = Utils.readContents(currentFile);
            byte[] givenSer = Utils.readContents(givenFile);

            File file = Utils.join(CWD, fileName);
            Utils.writeContents(file, "<<<<<<< HEAD\n",
                    currSer, "=======\n", givenSer, ">>>>>>>\n");
        }
    }

    /** Creates new merged commit using the GIVENBRANCH and
     * its head GIVEN. */
    public void mergeHelper3(Branch givenBranch, Commit given) {
        Commit merged = new Commit("Merged "
                + givenBranch.getName() + " into "
                + _currentBranch.getName() + ".", _head);
        merged.setMerged(given);
        merged.setBlobs(_stage.getAddStage());
        merged.removeBlobs(_stage.getRemoveStage());
        merged.save();
        _currentBranch.setHead(merged);
        _currentBranch.saveAsCurrentBranch();
        _currentBranch.saveBranch();
        merged.saveBlobs();
        _stage.empty();
        _stage.saveStage();
    }

    /** Helper for merge.
     * @param given - Given branch head.
     * @return - Returns true if true if untracked files present.
     */
    public boolean checkUntracked(Commit given) {
        List<String> filesCWD = Utils.plainFilenamesIn(CWD);
        for (String fileName : filesCWD) {
            if (given.getBlobs().containsKey(fileName)
                    && !_head.getBlobs().containsKey(fileName)) {
                System.out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return true;
            }
        }
        return false;
    }

    /** The staging area of the repository. */
    private Stage _stage;

    /** The current branch being operated on. */
    private Branch _currentBranch;

    /** The most recent commit in the current branch. */
    private Commit _head;
}




