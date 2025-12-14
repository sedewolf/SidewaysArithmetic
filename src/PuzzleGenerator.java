import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class PuzzleGenerator {

    private final boolean singularOnly;
    private final int maxHardness;

    public PuzzleGenerator(final boolean singularOnly, final int maxHardness) {
        this.singularOnly = singularOnly;
        this.maxHardness = maxHardness;
    }

    public Vector<SolvedPuzzle> filterSolvable(final ExecutorService executor, final Set<Puzzle> candidates) {
        final Vector<SolvedPuzzle> puzzles = new Vector<>();
        final List<Future<Void>> futures = new LinkedList<>();
        for (Puzzle puzzle : candidates) {
            futures.add(executor.submit(() -> {
                final List<Map<Character, Integer>> solutions = puzzle.solve(singularOnly);
                if (!solutions.isEmpty()) {
                    if (singularOnly && solutions.size() > 1) {
                        return null;
                    }
                    puzzles.add(new SolvedPuzzle(puzzle, solutions));
                }
                return null;
            }));
        }
        futures.forEach(future -> {
            try {
                future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return puzzles;
    }

    public Set<Puzzle> getCandidates(final List<String> dict, final Word word1, final Word word2) {
        final Set<Puzzle> puzzles = new HashSet<>();
        final List<Word> closure = new ArrayList<>();
        final Set<Character> unknowns1 = new HashSet<>();
        word1.addLetters(unknowns1);
        final Set<Character> unknowns2 = new HashSet<>();
        word2.addLetters(unknowns2);
        for (String w : dict) {
            if (w.length() == Math.max(word1.len(), word2.len()) ||
                    w.length() == Math.max(word1.len(), word2.len()) + 1) {
                final Word answer = new Word(w);
                if (overlap(unknowns1, answer, word1.len() - maxHardness) &&
                        overlap(unknowns2, answer, word2.len() - maxHardness)) {
                    closure.add(answer);
                }
            }
        }
        closure.forEach(answer -> puzzles.add(new AdditionPuzzle(word1, word2, answer)));
        return puzzles;
    }

    public Set<Puzzle> getCandidates(List<String> dict, Word answer) {
        final Set<Puzzle> puzzles = new HashSet<>();
        final List<Word> closure = new ArrayList<>();
        final Set<Character> unknowns = new HashSet<>();
        answer.addLetters(unknowns);
        for (String w : dict) {
            if (w.length() == answer.len() || w.length() == answer.len() - 1) {
                final Word word = new Word(w);
                if (overlap(unknowns, word, answer.len() - maxHardness)) {
                    closure.add(word);
                }
            }
        }

        for (int i = 0; i < closure.size(); i++) {
            final Word word2 = closure.get(i);
            for (Word word1 : closure) {
                final AdditionPuzzle puzzle = new AdditionPuzzle(word1, word2, answer);
                puzzles.add(puzzle);
            }
        }
        return puzzles;
    }

    private boolean overlap(final Set<Character> letterSet, final Word candidateWord, final int min) {
        int overlap = 0;
        final Set<Character> letterSuperset = new HashSet<>(letterSet);
        candidateWord.addLetters(letterSuperset);
        if (letterSuperset.size() > 10) {
            return false; // Only 10 digits may be assigned
        }
        for (Character c : letterSet) {
            for (int i = 0; i < candidateWord.len(); i++) {
                if (candidateWord.pos(i) == c) {
                    overlap++;
                    break;
                }
            }
        }
        return overlap >= min;
    }
}
