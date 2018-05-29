package cn.hooo.translate.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DictionaryUtil {
    private static final ConcurrentHashMap<String, String> dicMap = new ConcurrentHashMap<>(4096);
    private static final String DICS = "dics/";
    private static final String LANG_DIC = "/lang.dic";
    private static final String SLASH = "/";

    static {
        String filePath = DictionaryUtil.class.getResource(LANG_DIC).getPath();
        try {
            List<String> lines = Files.readAllLines(new File(filePath).toPath());
            for (String line : lines) {
                String[] tmpLine = line.split("=");
                dicMap.put(tmpLine[0], tmpLine[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String translate(String en) {
        String cn = "";
        String lowerEN = en.toLowerCase();
        URL base = DictionaryUtil.class.getResource(SLASH);
        // 先从缓存中获取
        cn = dicMap.get(lowerEN);
        // 如果获取不到则从按字母的字典文件中搜索
        try {
            if (cn == null) {
                int wordMinLength = Math.min(1, en.length());
                String dicFileName = en.substring(0, wordMinLength).toLowerCase();
                //判断是否是24个英文字母的首字母
                Pattern p = Pattern.compile("[A-z]|[a-z]");
                Matcher m = p.matcher(dicFileName);
                if (!m.find()) {
                    return en;
                }

                File dicFile = new File(base.getFile(), DICS + dicFileName);
                List<String> lines = Files.readAllLines(dicFile.toPath());
                for (String dic : lines) {
                    if (!"".equals(dic) && dic != null) {
                        String[] tmpLine = dic.split("=");
                        String key = tmpLine[0].toLowerCase();
                        String value = tmpLine[1];
                        if (key.equals(lowerEN)) {
                            dicMap.put(key, value);
                            cn = value;
                            break;
                        }
                    }
                }
                lines.clear();
                lines = null;

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        cn = cn == null ? en : cn;
        return cn;
    }

}
