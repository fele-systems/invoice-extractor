package com.systems.fele.extractor.banks;

import java.util.List;
import java.util.function.Predicate;

public class LineStream {
    private List<String> lines;
    private int currentLine;
    private int markedLine;

    public LineStream(List<String> lines) {
        this.lines = lines;
    }

    public List<String> lines() {
        return lines;
    }

    public int getLineCount() {
        return lines.size();
    }

    public void skip(int n) {
        currentLine += n;
    }

    public String advanceAndGet() {
        skip(1);
        return getLine();
    }

    public String getAndAdvance() {
        String line = getLine();
        skip(1);
        return line;
    }

    public String getLine() {
        return lines.get(currentLine);
    }

    public void mark() {
        markedLine = currentLine;
    }

    public void rollback() {
        currentLine = markedLine;
    }

    public void reset() {
        currentLine = 0;
    }

    public boolean eof() {
        return currentLine >= lines.size();
    }

    public boolean find(String lineContent) {
        return find(lineContent::equals);
    }

    public boolean find(Predicate<String> lineMatcher) {
        for (; currentLine < lines.size(); currentLine++) {
            if (lineMatcher.test(getLine()))
                return true;
        }
        return false;
    }
}
