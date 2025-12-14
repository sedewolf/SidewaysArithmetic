import java.util.*;

public interface Puzzle {

    /**
     * Addition puzzles with a carry-over letter in the answer can infer the value of the carry-over to be 1.
     * @return Carry-over letter, or null if answer is same length as addends.
     */
    Character getCarryLetter();

    /**
     * @return Set of letters from all words in the puzzle.
     */
    Set<Character> getClosure();

    /**
     * @return Single-line form of puzzle (ELF + ELF = FOOL)
     */
    String getSummary();

    /**
     * @return true if puzzle is solvable, false otherwise
     */
    boolean isValid();

    /**
     * Substitute a letter for a digit, and return that as a new puzzle.
     * @param find Character to replace
     * @param replace Digit to replace it with
     * @return New instance of puzzle with replaced letter
     */
    Puzzle substituting(char find, int replace);

    /**
     * @return Set of solutions to the puzzle, each solution being a set of letter->digit mappings.
     */
    default List<Map<Character, Integer>> solve(final boolean firstSolutionOnly) {
        final List<Map<Character, Integer>> solutions = new ArrayList<>();
        final Map<Character, Integer> subs = new LinkedHashMap<>();
        final Set<Integer> used = new LinkedHashSet<>();

        final Set<Character> unknowns = getClosure();

        // Optimization to solve the carry digit immediately
        final Character carry = getCarryLetter();
        Puzzle puzzle = this;
        if (carry != null) {
            unknowns.remove(carry);
            subs.put(carry, 1);
            used.add(1);
            puzzle = puzzle.substituting(carry, 1);
        }
        recurse(solutions, subs, unknowns, used, puzzle, firstSolutionOnly);

        return solutions;
    }

    private static void recurse(List<Map<Character, Integer>> solutions,
                                Map<Character, Integer> subs, Set<Character> unknowns, Set<Integer> used,
                                Puzzle puzzle, boolean firstSolutionOnly) {
        if (!puzzle.isValid()) {
            return;
        }
        if ((firstSolutionOnly && solutions.size() > 1)) {
            return;
        }
        if (unknowns.isEmpty()) { // capture the solution
            solutions.add(new LinkedHashMap<>(subs));
            return;
        }
        final char letter = unknowns.iterator().next();
        unknowns.remove(letter);
        for (int sub = 0; sub < 10; sub++) {
            if (!used.contains(sub)) {
                subs.put(letter, sub);
                used.add(sub);
                recurse(solutions, subs, unknowns, used, puzzle.substituting(letter, sub), firstSolutionOnly);
                subs.remove(letter);
                used.remove(sub);
            }
        }
        unknowns.add(letter);
    }

}
