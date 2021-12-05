# Gitlet Design Document

**Sameer Keswani**:

## Classes and Data Structures
###1. Commit
Represents a single commit object with several fields pertaining to a commit

**Fields**
- `String message`: The message of the commit
- `Date timestamp`: Refers to the date of the commit
- `Commit parent1`: A pointer to the commit done before the current commit
- `Commit parent2`: A pointer to a potential second parent (if doing git merge)
- `int hashCode`: The hashcode of the current commit
- `HashMap<String, File> blobs`: Maps the name of the file to a certain blob

###2. Tree
This will keep track of the files and commits in the .gitlet directory

**Fields**
- `ArrayList<String> files`: all the files present in .gitlet directory

###3. StagingArea
This is the place where files will be sent to before the commit when git add or git rm is declared

**Fields**
- `ArrayList<File file> stagedFiles`: List of all the files that are staged when running git add; emptied when commit is created 


###4. Commands
This will contain each git command from the spec (add, commit, etc.). This will be kind of like an utils class.

## Algorithms
###1. Commit Class
The Commit() constructor will take in a message and the parent(s) of the current commit. With this, it will move all the files from the staging area to the repository, modifying any files in order to do so.
Methods might include updating the contents of every file that is in the blobs HashMap. Or, another method can remove a file from the directory.
There will also be a method that adds the new commit to the commit folder of every file being committed.

###2. Tree
This will contain the structure of the .gitlet directory. One method for this class will add a file to the structure whenever a new file is committed, so that all the files in the .gitlet directory are known.

###3. Staging Area
The StagingArea constructor will set up an empty staging area. Two methods for this object will be to add files to the staging area (whenever git add is called) and to remove files from the staging area (whenver git rm is called).
When a commit is created, everything from the staging area is emptied. This requires another method that empties all the files that are in the staging area.

###4. Commands
Most of the logic done here is contained in the spec. Each method will be the git command that is being developed.

## Persistence
- Every file will have its own directory in the .gitlet directory
  - In each of these directories will be a commit directory, where every file being modified in the current commit is added to the commit folder for that file
  - In specific, these files in the committed folders will be the blobs, or the content of each file
- Everytime commit is created/run, all the commit folders for each file will have a new file, which is the new, committed file
- If a new file is committed, then a new directory is added, with its own commit folder, where new commits to the file will be stored


