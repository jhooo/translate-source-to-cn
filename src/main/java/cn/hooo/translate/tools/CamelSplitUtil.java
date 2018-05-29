package cn.hooo.translate.tools;

public class CamelSplitUtil {

    public static String[] split(String word) {
        String regex = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";
        return word.split(regex);
    }

}
