package com.example.x6.util;

public class StringUtil {
    public static String trim(String source) {
        return source != null?source.trim():source;
    }

    public static boolean isEmpty(String source) {
        return source == null || source.length() == 0;
    }

    public static boolean isNotEmpty(String source) {
        return !isEmpty(source);
    }

    public static boolean isBlank(String source) {
        return source == null || source.length() <= 0 || source.trim().length() <= 0;
    }

    public static boolean isNotBlank(String source) {
        return !isBlank(source);
    }

}
