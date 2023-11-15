package com.systems.fele.common.strings;

public class Strings {
    private Strings() {}

    /**
     * Returns a forward advancing index pointing to
     * the first character of str
     * @param str String
     * @return the index
     */
    public static Index begin(String str) {
        if (str == null) str = "";
        return new Index(1, 0, str);
    }

    /**
     * Returns a index pointing to the EOF (str.length)
     * of str.
     * @param str String
     * @return the EOF index
     */
    public static Index eof(String str) {
        if (str == null) str = "";
        return new Index(1, str.length(), str);
    }

    /**
     * Returns a backward advancing index pointing to
     * the first character of str
     * @param str String
     * @return the index
     */
    public static Index rbegin(String str) {
        if (str == null) str = "";
        return new Index(-1, str.length()-1, str);
    }

    /**
     * Returns a index pointing to the reserve EOF (-1)
     * of str
     * @param str String
     * @return the index
     */
    public static Index reof(String str) {
        if (str == null) str = "";
        return new Index(-1, -1, str);
    }

    public static Index find(String str, char charToBeFound) {
        var i = str.indexOf(charToBeFound);
        if (i < 0) return eof(str);

        return new Index(1, i, str);
    }
}
