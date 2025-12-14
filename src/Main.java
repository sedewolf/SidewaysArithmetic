import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final String wordfile = "dictionary.txt";
    private static final int N_THREADS = 8; // Number of worker threads for puzzle generator
    private static final int MAX_HARDNESS = 2; // Harder puzzles have fewer letters in common between addends/sum
    private static final boolean SINGULAR_ONLY = true; // Generates puzzles with exactly one solution

    public static void main(String[] args) {
        if (args.length == 1 || args.length == 2) {
            generatePuzzles(args);
        } else if (args.length == 3 || args.length == 5) {
            solvePuzzles(args);
        } else {
            System.out.println(USAGE);
        }
    }

    public static void generatePuzzles(String[] args) {
        final ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);
        final PuzzleGenerator generator = new PuzzleGenerator(SINGULAR_ONLY, MAX_HARDNESS);
        final long start = System.currentTimeMillis();
        System.out.println("Puzzle generator mode.");
        try {
            final List<String> all = readDictionary();
            final Set<Puzzle> candidates = args.length == 1 ?
                    generator.getCandidates(all, new Word(args[0])) :
                    generator.getCandidates(all, new Word(args[0]), new Word(args[1]));
            System.out.println("Found " + candidates.size() + " candidates @ max hardness " + MAX_HARDNESS);
            final Vector<SolvedPuzzle> puzzles = generator.filterSolvable(executor, candidates);
            if (!puzzles.isEmpty()) {
                System.out.println("=================================");
                puzzles.forEach(puzzle -> {
                    String summary = puzzle.getPuzzle().getSummary();
                    if (puzzle.getSolutions().size() > 1) {
                        summary += " (" + puzzle.getSolutions().size() + ")";
                    }
                    System.out.println(summary);
                });
                System.out.println("=================================");
            }
            System.out.println("Found " + puzzles.size() + " solutions in " +
                    (System.currentTimeMillis() - start) + " ms");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Failed to read dictionary file: " + e.getMessage());
            System.exit(1);
        } finally {
            executor.shutdown();
        }
    }

    private static void solvePuzzles(String[] args) {
        System.out.println("Puzzle solver mode.");
        final Puzzle puzzle = args.length == 3 ?
                new AdditionPuzzle(new Word(args[0]), new Word(args[1]), new Word(args[2])) :
                new MultiplicationPuzzle(new Word(args[0]), new Word(args[1]),
                                            new Word(args[2]), new Word(args[3], 1), new Word(args[4]));
        System.out.println();
        System.out.println(puzzle);
        System.out.println();
        final List<Map<Character, Integer>> solutions = puzzle.solve(false);
        if (!solutions.isEmpty()) {
            System.out.println("Solution" + (solutions.size() == 1 ? "" : "s"));
            final List<Character> letterList = new ArrayList<>(solutions.get(0).keySet());
            letterList.sort(Character::compareTo);
            letterList.forEach(c -> System.out.print("=="));
            System.out.println();
            letterList.forEach(letter -> System.out.print(letter + " "));
            System.out.println();

            for (Map<Character, Integer> solution : solutions) {
                for (Character letter : letterList.stream().sorted().toList()) {
                    System.out.print(solution.get(letter) + " ");
                }
                System.out.println();
            }
            if (solutions.size() == 1) {
                printSubs(solutions.get(0), puzzle);
            }
        } else {
            System.out.println("No solution found.");
        }
    }

    private static List<String> readDictionary() throws IOException {
        final List<String> all = new LinkedList<>();
        try (final InputStream stream = Main.class.getResourceAsStream(wordfile)) {
            if (stream == null) {
                System.out.println("Wordfile not found: " + wordfile);
                System.exit(1);
            }
            final BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line = br.readLine();
            while (line != null) {
                all.add(line);
                line = br.readLine();
            }
            System.out.println("Read " + all.size() + " words from dictionary");
            return all;
        }
    }

    private static void printSubs(Map<Character, Integer> subs, Puzzle puzzle) {
        for (Character key : subs.keySet()) {
            final int sub = subs.get(key);
            puzzle = puzzle.substituting(key, sub);
        }
        System.out.println();
        System.out.println(puzzle);
    }

    private static final String USAGE = """
         Usage:
         =========================
         | Puzzle generator mode |
         =========================
         1. Search for addition puzzles with a unique solution, given the sum word.
    
           Example:
             ./wayside.sh cheddar
    
           Output:
             HYDRATE + DECLARE = CHEDDAR
             CARRIED + SACRED = CHEDDAR
    
         2. Search for addition puzzles with a unique solution, given the addend words.
    
           Example:
             ./wayside.sh patrol trolls
    
           Output:
             PATROL + TROLLS = SPROUT
    
         ======================
         | Puzzle solver mode |
         ======================
         1. Solve a puzzle in the form:
    
              CANINE
           +  FELINE
             -------
             BALANCE
    
         Example:
           ./wayside.sh canine feline balance
    
         Output:
           A B C E F I L N
           5 1 8 0 7 4 6 9
    
         2. Solve a puzzle in the form:
    
               SAY
           x    SI
             -----
              NOSY
           +  ICY
             -----
             ANNOY
    
         Example:
           ./wayside.sh say si nosy icy annoy
    
         Output:
           A C I N O S Y
           1 4 9 2 8 3 5
    """;
}
