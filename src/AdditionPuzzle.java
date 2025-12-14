import java.util.LinkedHashSet;
import java.util.Set;

public class AdditionPuzzle implements Puzzle {

    protected final Word word1;
    protected final Word word2;
    protected final Word answer;

    public AdditionPuzzle(Word word1, Word word2, Word answer) {
        if (word1 == null || word2 == null || answer == null) {
            throw new NullPointerException("Addition puzzle requires three words");
        }
        this.word1 = word1;
        this.word2 = word2;
        this.answer = answer;
    }

    @Override
    public boolean isValid() {
        int maxlen = Math.max(word1.len(), word2.len());
        maxlen = Math.max(maxlen, answer.len());
        Integer carry = 0;
        if (!word1.isValid() || !word2.isValid() || !answer.isValid()) {
            return false;
        }
        for (int i = 0; i < maxlen; i++) {
            final char c1 = word1.pos(i);
            final char c2 = word2.pos(i);
            final char c3 = answer.pos(i);
            if (Character.isLetter(c1) || Character.isLetter(c2) || Character.isLetter(c3)) {
                carry = null; // downstream effects of carry-out can't be determined
                continue;
            }
            final int value1 = numericValueOf(c1);
            final int value2 = numericValueOf(c2);
            final int expected = numericValueOf(c3);
            int sum;
            int sumWithCarryIn;
            int dig;
            int digWithCarryIn;
            int carryOutWithCarryIn;
            if (carry == null) { // for undetermined carry-in, have to test both sums
                sum = value1 + value2;
                sumWithCarryIn = value1 + value2 + 1;
            } else {
                sum = value1 + value2 + carry;
                sumWithCarryIn = sum;
            }
            dig = sum % 10;
            carry = sum / 10;
            digWithCarryIn = sumWithCarryIn % 10;
            carryOutWithCarryIn = sumWithCarryIn / 10;
            if (carryOutWithCarryIn != carry) { // check whether carry-out could be affected by prior carry-in
                carry = null; // carry-out undetermined
            }
            if (dig != expected && digWithCarryIn != expected) {
                return false;
            }
        }
        return carry == null || carry == 0;
    }

    @Override
    public Character getCarryLetter() {
        if (answer.len() == Math.max(word1.len(), word2.len()) + 1) {
            return answer.pos(answer.len() - 1);
        }
        return null;
    }

    @Override
    public Puzzle substituting(char letter, int sub) {
        final Word word1prime = word1.substituting(letter, sub);
        final Word word2prime = word2.substituting(letter, sub);
        final Word answerPrime = answer.substituting(letter, sub);
        return new AdditionPuzzle(word1prime, word2prime, answerPrime);
    }

    @Override
    public Set<Character> getClosure() {
        final Set<Character> closure = new LinkedHashSet<>();
        word1.addLetters(closure);
        word2.addLetters(closure);
        answer.addLetters(closure);
        return closure;
    }

    @Override
    public String getSummary() {
        return word1 + " + " + word2 + " = " + answer;
    }

    @Override
    public int hashCode() {
        return word1.hashCode() + word2.hashCode() + 31 * answer.hashCode();
    }

    @Override
    public boolean equals(Object puzzle) {
        if (puzzle instanceof AdditionPuzzle additionPuzzle) {
            if (!additionPuzzle.answer.equals(this.answer)) {
                return false;
            }
            if (additionPuzzle.word1.equals(this.word1)) {
                return additionPuzzle.word2.equals(this.word2);
            }
            if (additionPuzzle.word1.equals(this.word2)) {
                return additionPuzzle.word2.equals(this.word1);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        int maxlen = Math.max(word1.len(), word2.len());
        maxlen = Math.max(maxlen, answer.len());
        sb.append("  " + word1.padding(maxlen));
        sb.append("\n+ " + word2.padding(maxlen));
        sb.append("\n  " + dashes(maxlen));
        sb.append("\n  " + answer);
        return sb.toString();
    }

    protected static int numericValueOf(final char c) {
        if (c == ' ') {
            return 0;
        }
        if (!Character.isDigit(c)) {
            throw new IllegalArgumentException(c + " isn't a number");
        }
        return c - '0';
    }

    protected static String dashes(int len) {
        final StringBuilder sb = new StringBuilder();
        while (len > 0) {
            len--;
            sb.append("-");
        }
        return sb.toString();
    }
}
