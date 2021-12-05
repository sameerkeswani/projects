package gitlet;

import static gitlet.Directories.*;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Sameer Keswani
 */
public class Main {
    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        Commands commands = new Commands();
        checkArguments(args);
        checkInput(args);
        switch (args[0]) {
        case "init":
            commands.init();
            break;
        case "add":
            commands.add(args[1]);
            break;
        case "commit":
            if (args.length == 1 || args[1].equals("")) {
                System.out.println("Please enter a commit message");
                break;
            } else {
                commands.commit(args[1]);
                break;
            }
        case "log":
            commands.log();
            break;
        case "checkout":
            checkoutHelper(commands, args);
            break;
        case "rm":
            commands.rm(args[1]);
            break;
        case "global-log":
            commands.globalLog();
            break;
        case "find":
            commands.find(args[1]);
            break;
        case "status":
            commands.status();
            break;
        case "branch":
            commands.branch(args[1]);
            break;
        case "rm-branch":
            commands.rmBranch(args[1]);
            break;
        case "reset":
            commands.reset(args[1]);
            break;
        case "merge":
            commands.merge(args[1]);
            break;
        default:
            System.out.println("No command with that name exists");
            break;
        }
        System.exit(0);
    }

    /** Checks that there is a command and that gitlet repo
     * is initialized.
     * @param args - The input.
     */
    public static void checkArguments(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command");
            System.exit(0);
        } else if (!GITLET.exists() && !args[0].equals("init")) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

    /** Cheks that ARGS is formatted correctly. */
    public static void checkInput(String[] args) {
        if (args[0].equals("init") && args.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[0].equals("add") && args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[0].equals("commit") && args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[0].equals("rm") && args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[0].equals("log") && args.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[0].equals("reset") && args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[0].equals("rm-branch") && args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[0].equals("branch") && args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[0].equals("status") && args.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[0].equals("find") && args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[0].equals("merge") && args.length != 2) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        } else if (args[0].equals("global-log") && args.length != 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /** Helper method for checkout.
     * @param commands - "Commands" object which allows you to call checkout.
     * @param args - The input.
     */
    public static void checkoutHelper(Commands commands, String[] args) {
        if (args.length == 3) {
            commands.checkout(args[2], true);
        } else if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands");
                System.exit(0);
            }
            commands.checkout(args[1], args[3]);
        } else if (args.length == 2) {
            commands.checkout(args[1], false);
        } else {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }
}
