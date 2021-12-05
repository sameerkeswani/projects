package gitlet;

import java.io.File;

/** Contains all the directories needed for Gitlet.
 * @author Sameer Keswani
 */
public class Directories {
    /** Pathway of current working directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Pathway of .gitlet directory. */
    static final File GITLET = Utils.join(CWD, ".gitlet");

    /** Pathway of commits directory. */
    static final File COMMITS = Utils.join(GITLET, "commits");

    /** Pathway of staging area file. */
    static final File STAGE = Utils.join(GITLET, "stage");

    /** Pathway of head commit file. */
    static final File HEAD = Utils.join(GITLET, "head");

    /** Pathway of blobs directory. */
    static final File BLOBS = Utils.join(COMMITS, "blobs");

    /** Pathway of current branch. */
    static final File CURRENTBRANCH = Utils.join(GITLET, "currentBranch");

    /** Pathway of branches. */
    static final File BRANCHES = Utils.join(GITLET, "branches");
}
