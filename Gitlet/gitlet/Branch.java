package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Directories.BRANCHES;
import static gitlet.Directories.CURRENTBRANCH;

/** Representation of a branch.
 * @author Sameer Keswani
 */
public class Branch implements Serializable {

    /** Branch constructor which has a NAME and HEAD commit.
     */
    public Branch(String name, Commit head) {
        _name = name;
        _head = head;
    }

    /** Returns the name of a branch. */
    public String getName() {
        return  _name;
    }

    /** Returns the head commit of a branch. */
    public Commit getHead() {
        return _head;
    }

    /** Sets head of branch to NEWHEAD. */
    public void setHead(Commit newHead) {
        _head = newHead;
    }

    /** Serializes the current branch and stores it in
     * the branches directory. */
    public void saveBranch() {
        File saved = Utils.join(BRANCHES, this.getName());
        try {
            saved.createNewFile();
            Utils.writeObject(saved, this);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /** Saves a branch as the current branch. */
    public void saveAsCurrentBranch() {
        Utils.writeObject(CURRENTBRANCH, this);
    }

    /** Name of the branch. */
    private String _name;

    /** Head commit of the branch. */
    private Commit _head;

}
