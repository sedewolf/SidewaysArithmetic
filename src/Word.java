import java.util.Locale;
import java.util.Set;

public class Word {
    private final char[] chars;

    public Word(final char[] chars) {
        this.chars = chars;
    }

    public Word(String word) {
        this(word, 0);
    }

    public Word(String word, int shift) {
        chars = new char[word.length() + shift];
        word = word.trim().toUpperCase(Locale.ROOT);
        for (int i = 0; i < word.length(); i++) {
            chars[i] = word.charAt(i);
            if (Character.isDigit(chars[i])) {
                throw new IllegalArgumentException("Word must not contain digits: " + word);
            }
        }
        for (int i = word.length(); i < chars.length; i++) {
            chars[i] = ' ';
        }
    }

    public Word substituting(final char letter, final int number) {
        final char[] clone = new char[chars.length];
        for (int i = 0; i < chars.length; i++) {
            if (Character.isLetter(chars[i])) {
                if (chars[i] == letter) {
                    clone[i] = Character.forDigit(number, 10);
                    continue;
                }
            }
            clone[i] = this.chars[i];
        }
        return new Word(clone);
    }

    // Index 0 = Least significant digit
    public char pos(final int index) {
        if (index < chars.length) {
            return chars[chars.length - 1 - index];
        }
        return '0';
    }

    public int len() {
        return chars.length;
    }

    @Override
    public String toString() {
        return new String(chars);
    }

    public boolean isValid() {
        return chars[0] != '0';
    }

    public String padding(int paddedLen) {
        final StringBuilder sb = new StringBuilder();
        paddedLen -= len();
        while (paddedLen > 0) {
            paddedLen--;
            sb.append(' ');
        }
        sb.append(this);
        return sb.toString();
    }

    public void addLetters(final Set<Character> letters) {
        for (int i = 0; i < len(); i++) {
            if (!Character.isDigit(chars[i]) && chars[i] != ' ') {
                letters.add(chars[i]);
            }
        }
    }
}
