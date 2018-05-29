package cn.hooo.translate.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
    private static final String SLASH = "/";

    public static String[] split(String word, String splitRegex) {
        if (word != null && !"".equals(word)) {
            return word.split(splitRegex);
        }
        return new String[] {};
    }

    public static String[] splitSlash(String word) {
        if (word != null && !"".equals(word)) {
            return word.split(SLASH);
        }
        return new String[] {};
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

}
