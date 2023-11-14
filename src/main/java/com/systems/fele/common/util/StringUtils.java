package com.systems.fele.common.util;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class StringUtils {
    private StringUtils() {}

    public static String escape(String unescaped) {
        var sb = new StringBuilder();
        for (int i = 0; i < unescaped.length(); i++) {
            switch (unescaped.charAt(i)) {
            case '\0':
                sb.append("\\0");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\n':
                sb.append("\\n");
                break;
            default:
                if (Character.isISOControl(unescaped.charAt(i))) {
                    var asInt = Character.getNumericValue(unescaped.charAt(i));
                    sb.append( "\\x" + Integer.toHexString(asInt) );
                } else {
                    sb.append(unescaped.charAt(i));    
                }
            }
        }
        return sb.toString();
    }

    public static IndexOfResult indexOf(String str, char ch) {
        if (str == null) return null;
        return new IndexOfResult(str.indexOf(ch), str);
    }

    public static IndexOfReverse rBegin(String str) {
        return new IndexOfReverse(-1, str);
    }

    public static IndexOfReverse rEnd(String str) {
        return new IndexOfReverse(str.length()-1, str);
    }

    public interface CharPredicate {
        boolean test(char ch);
    }

    //TODO:MEDIUM Move to a dedicated file
    public abstract static class IndexOf {
        protected int index;
        protected String str;

        protected IndexOf(int index, String str) {
            this.index = index;
            this.str = str;
        }

        protected abstract void advance(int n);
        protected abstract int getEOF();
        protected abstract IndexOf spawn(int index, String str);
        public abstract IndexOf rev();

        public boolean isEOF() {
            return index == -1 || index >= str.length();
        }

        public char peekChar() {
            return str.charAt(index);
        }

        public IndexOf skip(int n) {
            var child = spawn(index, str);
            child.advance(n);
            return child;
        }

        public int getIndex() {
            return index;
        }

        public IndexOf skipString(String skippable) {
            return skip(skippable.length());
        }

        public IndexOf skipWhile(CharPredicate predicate) {
            var child = spawn(index, str);
            while (!child.isEOF() && predicate.test(child.peekChar())) {
                child.advance(1);
            }
            return child;
        }

        public IndexOf skipTo(char ch) {
            var child = spawn(index, str);
            while (!child.isEOF() && child.peekChar() != ch) {
                child.advance(1);
            }
            return child;
        }

        public StringSlice slice() {
            return new StringSlice(this, getEOF(), str);
        }

        public StringSlice slice(UnaryOperator<IndexOf> end) {
            return new StringSlice(this, end.apply(this).index, str);
        }

        public String toString() {
            if (isEOF()) return "EOF";
            return new StringBuilder(str).replace(index, index+1, "_").toString();
        }
    }

    //TODO:MEDIUM Move to a dedicated file
    public static class IndexOfResult extends IndexOf {

        protected IndexOfResult(int index, String str) {
            super(index, str);
        }

        @Override
        protected void advance(int n) {
            index += n;
            if (index > str.length()) index = str.length();
        }

        @Override
        protected IndexOf spawn(int index, String str) {
            return new IndexOfResult(index, str);
        }

        @Override
        protected int getEOF() {
            return str.length();
        }

        @Override
        public IndexOf rev() {
            return new IndexOfResult(index, str);
        }
        
    }

    public static class IndexOfReverse extends IndexOf {

        protected IndexOfReverse(int index, String str) {
            super(index, str);
        }

        @Override
        protected void advance(int n) {
            index -= n;
            if (index < 0) index = 0;
        }

        @Override
        protected int getEOF() {
            return 0;
        }

        @Override
        protected IndexOf spawn(int index, String str) {
            return new IndexOfReverse(index, str);
        }

        @Override
        public IndexOf rev() {
            return new IndexOfResult(index, str);
        }
        
    }

    //TODO:MEDIUM Move to a dedicated file
    public static record StringSlice(IndexOf begin, int end, String str) {
        public StringSlice take(int n) {
            return new StringSlice(begin, begin.skip(n).index, str);
        }

        public StringSlice takeString(String skippable) {
            return take( begin.skipString(skippable).index - begin().index );
        }

        public StringSlice takeWhile(CharPredicate predicate) {
            return take( begin.skipWhile(predicate).index - begin().index );
        }

        public String toString() {
            return str.substring(begin.index, end);
        }

        public int toInt() {
            return Integer.parseInt(toString());
        }

        public <T> T map(Function<String, T> mapper) {
            return mapper.apply(toString());
        }
    }
}
