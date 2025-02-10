package zxc.mrdrag0nxyt.rpserver.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([a-fA-F\\d]{6})");
    public static final char COLOR_CHAR = '§';

    public static String colorize(String input) {
        if (input == null || input.isEmpty()) return input;

        final Matcher matcher = HEX_PATTERN.matcher(input);
        final StringBuffer stringBuffer = new StringBuffer(input.length() + 32);

        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(stringBuffer,
                    COLOR_CHAR + "x" +
                            COLOR_CHAR + group.charAt(0) +
                            COLOR_CHAR + group.charAt(1) +
                            COLOR_CHAR + group.charAt(2) +
                            COLOR_CHAR + group.charAt(3) +
                            COLOR_CHAR + group.charAt(4) +
                            COLOR_CHAR + group.charAt(5)
            );
        }

        String output = matcher.appendTail(stringBuffer).toString();
        return translateAlternateColorCodes('&', output);
    }

    private static String translateAlternateColorCodes(char alternateColorChar, String input) {
        final char[] chars = input.toCharArray();

        for (int i = 0, length = chars.length - 1; i < length; ++i) {
            if (chars[i] == alternateColorChar && isValidColorChar(chars[i + 1])) {
                chars[i++] = COLOR_CHAR;
                chars[i] |= 0x20;
            }
        }

        return new String(chars);
    }

    private static boolean isValidColorChar(char c) {
        return (c >= '0' && c <= '9') ||
                (c >= 'a' && c <= 'f') ||
                c == 'r' ||
                (c >= 'k' && c <= 'o') ||
                c == 'x' ||
                (c >= 'A' && c <= 'F') ||
                c == 'R' ||
                (c >= 'K' && c <= 'O') ||
                c == 'X';
    }

}
