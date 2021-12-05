package gitlet;

import java.util.TreeMap;
import java.io.Serializable;
import java.util.ArrayList;

import static gitlet.Directories.STAGE;


/** Stage class.
 * @author Sameer Keswani
 */
public class Stage implements Serializable {

    /** Stage constructor.
     */
    public Stage() {
        _addStage = new TreeMap<String, String>();
        _removeStage = new ArrayList<String>();
    }

    /** Returns addition stage. */
    public TreeMap<String, String> getAddStage() {
        return _addStage;
    }

    /** Returns removal stage. */
    public ArrayList<String> getRemoveStage() {
        return _removeStage;
    }

    /** Add a file with NAME and SHA to addition stage. */
    public void add(String name, String sha) {
        _addStage.put(name, sha);
    }

    /** Add a file with NAME to removal stage. */
    public void remove(String name) {
        _removeStage.add(name);
    }

    /** Remove file with NAME from addition stage. */
    public void removeFromAddStage(String name) {
        _addStage.remove(name);
    }

    /** Remove file with NAME from removal stage. */
    public void removeFromRemoveStage(String name) {
        _removeStage.remove(name);
    }

    /** Empties but the Addition Stage and Removal Stage. */
    public void empty() {
        _addStage.clear();
        _removeStage.clear();

    }

    /** Serializes stage to STAGE directory. */
    public void saveStage() {
        Utils.writeObject(STAGE, this);
    }

    /** Addition Stage. */
    private TreeMap<String, String> _addStage;

    /**Removal Stage. */
    private ArrayList<String> _removeStage;



}
