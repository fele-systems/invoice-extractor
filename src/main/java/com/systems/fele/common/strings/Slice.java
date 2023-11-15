package com.systems.fele.common.strings;

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
