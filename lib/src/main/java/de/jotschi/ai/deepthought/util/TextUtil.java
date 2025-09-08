package de.jotschi.ai.deepthought.util;

import java.util.regex.Matcher;

public final class TextUtil {

    public static String quote(String text) {
        return text.replaceAll(".*\\R|.+\\z", Matcher.quoteReplacement("> ") + "$0");
    }
}
