package com.systems.fele;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

public abstract class PdfExtractor implements Extractor {

    public static class LineStream {
        private List<String> lines;
        private int currentLine;
        private int markedLine;

        public LineStream(List<String> lines) {
            this.lines = lines;
        }

        int getLineCount() {
            return lines.size();
        }

        void skip(int n) {
            currentLine += n;
        }

        String advanceAndGet() {
            skip(1);
            return getLine();
        }

        String getAndAdvance() {
            String line = getLine();
            skip(1);
            return line;
        }

        String getLine() {
            return lines.get(currentLine);
        }

        void mark() {
            markedLine = currentLine;
        }

        void rollback() {
            currentLine = markedLine;
        }

        boolean find(String lineContent) {
            return find(s -> s.equals(lineContent));
        }

        boolean find(Predicate<String> lineMatcher) {
            for (; currentLine < lines.size(); currentLine++) {
                if (lineMatcher.test(getLine())) return true;
            }
            return false;
        }
    }

    @Override
    public final Invoice extract(InputStream stream, String password) {
        try (PDDocument doc = password == null
            ? PDDocument.load(stream)
            : PDDocument.load(stream, password)) {
            var stripper = new PDFTextStripper();
            return extract(stripper.getText(doc).lines().collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Invoice extract(List<String> stream) {
        return extract(new LineStream(stream));
    }

    protected abstract Invoice extract(LineStream stream);
}
