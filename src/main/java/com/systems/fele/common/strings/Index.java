package com.systems.fele.common.strings;

import lombok.Getter;

/**
 * Represents a index inside a string and provides
 * some chained operations to be done with it.
 * 
 * <p> All indexes are immutable, all of the skipping
 * methods return a copy.
 */
@Getter
public class Index {
    
    private int direction;
    private int index;
    private String string;

    /**
     * Creates a new index object
     * 
     * @param direction Direction of skip operations. This should be set to 1 for
     *                  forward skipping, and -1 for backward skipping. This value
     *                  will always be normalized independently of the passed value
     *                  (any positive or 0 will be 1, and any negative will be -1).
     * @param index The actual index. A begin index will be the first usable
     *              character, while the end will the the index *AFTER* the last
     *              usable character.
     * @param string The string this index indexes.
     */
    Index(int direction, int index, String string) {
        this.direction = direction < 0 ? -1 : 1; // Normalize direction to 1 or -1
        this.index = index;
        this.string = string;
    }

    /**
     * Skip some characters
     * @param howManyChars
     * @return A new index pointing to the new location
     */
    public Index skip(int howManyChars) {
        /* The logic here is that amount will add or
         * subtract depending on direction.
         * 
         * Then to calculate if we reached the EOF we
         * subtract the eof from the calculated target
         * index, which will give how many characters we
         * skipped past eof (positively for forward and
         * negatively for backward). Then, multiply that
         * to direction and the value will be normalized
         * (positive for "yes, we skipped")
         */

        var amount = howManyChars * direction;
        var target = index + amount;
        var eof = EOF();
        if ( (target - eof) * direction > 0) {
            target = eof;
        }

        return new Index(direction, target, string);
    }

    /**
     * Skip any sequential characters the satisfies the
     * predicate.
     * @param predicate Char tester
     * @return a new index pointing to a possibly new location
     */
    public Index skipWhile(CharPredicate predicate) {
        var tIndex = index;
        var eof = EOF();
        while (tIndex != eof && predicate.test(peekChar())) {
            tIndex += direction;
        }
        return new Index(direction, tIndex, string);
    }

    /**
     * Returns the char pointed by the index
     * @return a char
     */
    public char peekChar() {
        return string.charAt(index);
    }

    /**
     * Tests if this index is the EOF. Does not check
     * for skipping past EOF.
     * @return
     */
    public boolean isEOF() {
        return index == EOF();
    }

    /**
     * Returns the EOF index.
     * @return EOF index
     */
    public int EOF() {
        return eofOf(direction, string);
    }

    /**
     * Creates a empty slice
     * @return empty slice
     */
    public Slice slice() {
        return new Slice(this, index);
    }

    /**
     * Creates a slice containing characters until the
     * end of string
     * @return a slice
     */
    public Slice sliceToEOF() {
        return new Slice(this, EOF());
    }

    @Override
    public String toString() {
        var buffer = new StringBuilder();
        if (direction < 0 && isEOF()) {
            buffer.append(' '); // This is so we can append a character before the first character
        }

        buffer.append(string).append('\n');

        var directionChar = direction < 0 ? '<' : '>';

        for (int i = -1; i < index; i++) {
            buffer.append(directionChar);
        }

        buffer.append('^');

        for (int i = index+1; i < string.length(); i++) {
            buffer.append(directionChar);
        }

        return buffer.toString();
    }

    /**
     * Returns what would be the EOF of this string, acording
     * to direction.
     * @param direction the direction
     * @param string a string
     * @return the eof index
     */
    public static int eofOf(int direction, String string) {
        if (direction < 0) {
            return -1; // backward
        } else {
            return string.length(); // forward
        }
    }
}
