package com.systems.fele.common.util;

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

    public static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}
