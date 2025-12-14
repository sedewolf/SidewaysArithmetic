import java.util.Set;

public class MultiplicationPuzzle extends AdditionPuzzle {

    private final Word mul1;
    private final Word mul2;

    public MultiplicationPuzzle(Word mul1, Word mul2, Word word1, Word word2, Word answer) {
        super(word1, word2, answer);
        if (mul1 == null || mul2 == null) {
            throw new NullPointerException("Multiplication puzzle requires five words");
        }
        this.mul1 = mul1;
        this.mul2 = mul2;
    }

    @Override
    public Puzzle substituting(char letter, int sub) {
        final Word word1prime = word1.substituting(letter, sub);
        final Word word2prime = word2.substituting(letter, sub);
        final Word answerPrime = answer.substituting(letter, sub);
        final Word mul1prime = mul1.substituting(letter, sub);
        final Word mul2prime = mul2.substituting(letter, sub);
        return new MultiplicationPuzzle(mul1prime, mul2prime, word1prime, word2prime, answerPrime);
    }

    @Override
    public Set<Character> getClosure() {
        final Set<Character> closure = super.getClosure();
        mul1.addLetters(closure);
        mul2.addLetters(closure);
        return closure;
    }

    @Override
    public boolean isValid() {
        if (!super.isValid()) {
            return false;
        }
        int maxlen = Math.max(mul1.len(), mul2.len());
        maxlen = Math.max(maxlen, word1.len());
        maxlen = Math.max(maxlen, word2.len());
        Integer carry = 0;
        if (!mul1.isValid() || !mul2.isValid() || !word1.isValid() || !word2.isValid()) {
            return false;
        }
        final Word[] answers = new Word[] {word1, word2};
        for (int i = 0; i < answers.length; i++) {
            final char c2 = mul2.pos(i);
            for (int j = 0; j < maxlen; j++) {
                final char c1 = mul1.pos(j);
                final char c3 = answers[i].pos(j + i);
                if (Character.isLetter(c1) || Character.isLetter(c2) || Character.isLetter(c3)) {
                    carry = null;
                    continue;
                }

                final int value1 = numericValueOf(c1);
                final int value2 = numericValueOf(c2);
                final int expected = numericValueOf(c3);
                final int prod;
                final int dig;
                if (carry == null) {
                    return true;
                } else {
                    prod = value1 * value2 + carry;
                }
                dig = prod % 10;
                carry = prod / 10;
                if (dig != expected) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getSummary() {
        return mul1 + " * " + mul2 + " = " + super.getSummary();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        int maxlen = Math.max(word1.len(), word2.len());
        maxlen = Math.max(maxlen, answer.len());
        sb.append("  " + mul1.padding(maxlen));
        sb.append("\nx " + mul2.padding(maxlen));
        sb.append("\n  " + dashes(maxlen));
        sb.append("\n  " + word1.padding(maxlen));
        sb.append("\n+ " + word2.padding(maxlen));
        sb.append("\n  " + dashes(maxlen));
        sb.append("\n  " + answer);
        return sb.toString();
    }
}
