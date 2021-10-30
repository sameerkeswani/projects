package jump61;

import java.util.ArrayList;
import java.util.Random;

import static jump61.Side.*;

/** An automated Player.
 *  @author Sameer Keswani
 */
class AI extends Player {

    /** A new player of GAME initially COLOR that chooses moves automatically.
     *  SEED provides a random-number seed used for choosing moves.
     */
    AI(Game game, Side color, long seed) {
        super(game, color);
        _random = new Random(seed);
    }

    @Override
    String getMove() {
        Board board = getGame().getBoard();

        assert getSide() == board.whoseMove();
        int choice = searchForMove();
        getGame().reportMove(board.row(choice), board.col(choice));
        return String.format("%d %d", board.row(choice), board.col(choice));
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private int searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert getSide() == work.whoseMove();
        _foundMove = -1;
        if (getSide() == RED) {
            value = minMax(work, 5, true, 1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
        } else {
            value = minMax(work, 5, true, -1,
                    Integer.MIN_VALUE, Integer.MAX_VALUE);
        }
        if (_foundMove == -1) {
            int counter = 0;
            ArrayList<Integer> legalMoves = getLegalMoves(work, getSide());
            for (int move : legalMoves) {
                if (work.get(move).getSpots() == work.neighbors(move)) {
                    _foundMove = move;
                    return _foundMove;
                }
            }
            if (_foundMove == -1) {
                int index = _random.nextInt(legalMoves.size());
                _foundMove = legalMoves.get(index);
            }
        }
        return _foundMove;
    }


    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int minMax(Board board, int depth, boolean saveMove,
                       int sense, int alpha, int beta) {
        if (board.getWinner() != null || depth == 0) {
            return staticEval(board, Integer.MAX_VALUE);
        }

        int biggest = Integer.MIN_VALUE;
        int smallest = Integer.MAX_VALUE;
        Board b;
        if (sense == 1) {
            for (int i : getLegalMoves(board, RED)) {
                b = new Board(board);
                b.addSpot(RED, i);
                int response = minMax(b, depth - 1, false, -sense, alpha, beta);
                b.undo();
                if (response > biggest) {
                    biggest = response;
                    alpha = Integer.max(alpha, biggest);
                    if (saveMove) {
                        _foundMove = i;
                    }
                    if (alpha >= beta) {
                        return biggest;
                    }
                }
            }
            return biggest;
        } else {
            for (int i : getLegalMoves(board, BLUE)) {
                b = new Board(board);
                b.addSpot(BLUE, i);
                int response = minMax(b, depth - 1, false, -sense, alpha, beta);
                b.undo();
                if (response < smallest) {
                    smallest = response;
                    beta = Integer.min(beta, smallest);
                    if (saveMove) {
                        _foundMove = i;
                    }
                    if (alpha >= beta) {
                        return smallest;
                    }
                }
            }
            return smallest;
        }
    }

    /** Return a heuristic estimate of the value of board position B.
     *  Use WINNINGVALUE to indicate a win for Red and -WINNINGVALUE to
     *  indicate a win for Blue. */
    private int staticEval(Board b, int winningValue) {
        int guess = 0;
        if (b.getWinner() == RED) {
            return winningValue;
        } else if (b.getWinner() == BLUE) {
            return -winningValue;
        } else {
            ArrayList<Integer> aiLegalMoves = getLegalMoves(b, getSide());
            ArrayList<Integer> playerLegalMoves = getLegalMoves(b,
                    getSide().opposite());

            if (getSide() == RED) {
                for (int sq : aiLegalMoves) {
                    if (b.get(sq).getSpots() == b.neighbors(sq)) {
                        guess += 10;
                    }
                }
                for (int sq : playerLegalMoves) {
                    if (b.get(sq).getSpots() == b.neighbors(sq)) {
                        guess -= 10;
                    }
                }
                guess += (b.numOfSide(RED) - b.numOfSide(BLUE)) * 2;
                guess += aiLegalMoves.size() - playerLegalMoves.size();
            } else {
                for (int sq : aiLegalMoves) {
                    if (b.get(sq).getSpots() == b.neighbors(sq)) {
                        guess -= 10;
                    }
                }
                for (int sq : playerLegalMoves) {
                    if (b.get(sq).getSpots() == b.neighbors(sq)) {
                        guess += 10;
                    }
                }
                guess -= (b.numOfSide(BLUE) - b.numOfSide(RED)) * 2;
                guess -= aiLegalMoves.size() - playerLegalMoves.size();

            }

        }
        return guess;
    }

    /** Returns a list of all legal moves for a side S from a certain square.
     * from board B.
     * */
    private ArrayList<Integer> getLegalMoves(Board b, Side s) {
        ArrayList<Integer> legalMoves = new ArrayList<Integer>();

        for (int i = 0; i < b.size() * b.size(); i += 1) {
            if (b.isLegal(s, i) && b.isLegal(s)) {
                legalMoves.add(i);
            }
        }
        return legalMoves;
    }

    /** A random-number generator used for move selection. */
    private Random _random;

    /** Used to convey moves discovered by minMax. */
    private int _foundMove;
}
