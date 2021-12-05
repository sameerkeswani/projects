package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.TreeMap;
import java.util.Date;
import java.util.Set;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import static gitlet.Directories.*;

/**Commit class.
 * @author Sameer Keswani
 */
public class Commit implements Serializable {

    /** Constructor for Commit. Takes in a
     * MESSAGE and pointer to the PARENT commit.
     */
    public Commit(String message, Commit parent) {
        _message = message;
        _parent = parent;
        _mergedParent = null;
        if (parent == null) {
            _date = "Wed Dec 31 16:00:00 1969 -0800";
            _blobs = null;
        } else {
            Date date = new Date();
            SimpleDateFormat formatDate =
                    new SimpleDateFormat("EEE LLL d HH:mm:ss yyyy Z");
            _date = formatDate.format(date);
            _blobs = new TreeMap<String, String>();
            if (parent.getBlobs() != null) {
                setBlobs(parent.getBlobs());
            }
        }
        byte[] serializedCommit = Utils.serialize(this);
        _sha = Utils.sha1(serializedCommit);

    }

    /** Returns the message of the commit. */
    public String getMessage() {
        return _message;
    }

    /** Returns the date of the commit. */
    public String getDate() {
        return _date;
    }

    /** Returns the parent of the commit. */
    public Commit getParent() {
        return _parent;
    }
    /** Returns the sha id of the commit. */
    public String getSHA() {
        return _sha;
    }

    /** Returns the merged parent of the commit. */
    public Commit getMerged() {
        return _mergedParent;
    }

    /** Sets the merged parent of the commit to C. */
    public void setMerged(Commit c) {
        _mergedParent = c;
    }

    /** Returns the parent of the commit. */
    public TreeMap<String, String> getBlobs() {
        return _blobs;
    }

    /** Sets the blobs of the commit to STAGED. */
    public void setBlobs(TreeMap<String, String> staged) {
        Set<String> keySet = staged.keySet();
        for (String key : keySet) {
            _blobs.put(key, staged.get(key));
        }
    }

    /** Removes blobs with certain NAMES from commit. */
    public void removeBlobs(ArrayList<String> names) {
        for (String name : names) {
            _blobs.remove(name);
        }
    }

    /** Serialize the commit object into COMMITS directory. */
    public void save() {
        File saved = Utils.join(COMMITS, this.getSHA());
        try {
            saved.createNewFile();
            Utils.writeObject(saved, this);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /** Serialize blobs into the BLOBS directory. */
    public void saveBlobs() {
        Set<String> keySet = _blobs.keySet();
        for (String key : keySet) {
            byte[] serializedBlob = Utils.readContents(new File(key));
            String fileSHA = Utils.sha1(serializedBlob);
            File blobFile = Utils.join(BLOBS, fileSHA);
            Utils.writeContents(blobFile, serializedBlob);
        }
    }

    /** Message of the commit. */
    private String _message;

    /** Date of the commit. */
    private String _date;

    /** Pointer to the parent of this commit. */
    private Commit _parent;

    /** Pointer to merged parent, if this is a merge commit. */
    private Commit _mergedParent;

    /** SHA ID of commit. */
    private String _sha;

    /** All the blobs in current commit. */
    private TreeMap<String, String> _blobs;
}

