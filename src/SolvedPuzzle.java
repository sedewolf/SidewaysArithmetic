import java.util.List;
import java.util.Map;

public class SolvedPuzzle {

    private final Puzzle puzzle;
    private final List<Map<Character, Integer>> solutions;

    public SolvedPuzzle(final Puzzle puzzle, final List<Map<Character, Integer>> solutions) {
        this.puzzle = puzzle;
        this.solutions = solutions;
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public List<Map<Character, Integer>> getSolutions() {
        return solutions;
    }
}
