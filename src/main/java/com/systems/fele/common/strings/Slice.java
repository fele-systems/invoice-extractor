package com.systems.fele.common.strings;

import java.util.function.Function;

/**
 * Represents a slice of a string, containing a begin and an end.
 * <p>Both the begin and end goes to the same direction.
 */
public record Slice(Index begin, int sliceEOF) {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        offloadTo(sb);
        return sb.toString();
    }

    /**
     * Returns the sliceEOF as index. Keeps the original direction of this slice.
     * @return a index
     */
    public Index sliceEOFAsIndex() {
        return new Index(begin.getDirection(), sliceEOF, begin.getString());
    }

    /**
     * Increases this slice so that more characters fit at the end of the slice,
     * direction aware.
     * @param howManyChars number of characters
     * @return a new slice
     */
    public Slice take(int howManyChars) {
        return new Slice(begin, sliceEOFAsIndex().skip(howManyChars).getIndex());
    }

    /**
     * Increases this slice so that more characters fit at the end of the slice,
     * direction aware, while the characters satifies the predicate. Will stop
     * and point at the first occurence of a non-satisfied predicate
     * @param howManyChars number of characters
     * @return a new slice
     */
    public Slice takeWhile(CharPredicate predicate) {
        return new Slice(begin, sliceEOFAsIndex().skipWhile(predicate).getIndex());
    }

    /**
     * Decreases this slice so that it skips the first characters at the start of
     * slice, direction aware.
     * @param howManyChars number of characters
     * @return a new slice
     */
    public Slice skip(int howManyChars) {
        return new Slice(begin.skip(howManyChars), sliceEOF);
    }

    /**
     * Adjusts this slice so that it has the same size (expect when reaching a eof
     * boundary) but skips the first characters and accept new characters past the
     * previous end.
     * 
     * <p>This is equivalent to skip(n).take(n) or take(n).skip(n).
     * @param howManyPositions number of characters
     * @return a new slice
     */
    public Slice shift(int howManyPositions) {
        return new Slice(begin.skip(howManyPositions),
                sliceEOFAsIndex().skip(howManyPositions).getIndex());
    }

    /**
     * Reverses this slice so that the begin will be end and vice versa.
     * A rev'ed slice will have the exact same content as the original, but
     * the characters of the string will be in the reverse order.
     */
    public Slice rev() {
        int newDirection = -begin.getDirection();
        int newBegin = sliceEOF + newDirection;
        int newEOF = begin.getIndex() + newDirection;
        
        return new Slice(new Index(newDirection, newBegin, begin.getString()), newEOF);
    }

    public <T> T map(Function<String, T> mapper) {
        return mapper.apply(toString());
    }

    public int toInt() {
        return Integer.parseInt(toString());
    }

    /**
     * Appends characters included in this slice into output, direction aware.
     * 
     * @param output the target to append characters to
     */
    private void offloadTo(StringBuilder output) {
        var tIndex = begin.getIndex();
        final var eof = begin.EOF();
        while (tIndex != eof && tIndex != sliceEOF) {
            output.append(begin.getString().charAt(tIndex));
            tIndex += begin.getDirection();
        }
    }
}
