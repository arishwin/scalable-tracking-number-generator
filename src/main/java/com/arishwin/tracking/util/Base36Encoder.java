package com.arishwin.tracking.util;

public class Base36Encoder {
    private static final char[] CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String encode(long value) {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(CHARS[(int)(value % 36)]);
            value /= 36;
        } while (value > 0);
        return sb.reverse().toString();
    }
}