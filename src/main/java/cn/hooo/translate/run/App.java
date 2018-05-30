package cn.hooo.translate.run;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import cn.hooo.translate.entity.PathNode;
import cn.hooo.translate.tools.CamelSplitUtil;
import cn.hooo.translate.tools.CommonUtil;
import cn.hooo.translate.tools.DictionaryUtil;

public class App {

    public static void main(String[] args) {
        App app = new App();
        app.exe(args);
    }

    private void exe(String[] args) {
        // or E:\\Temp\\spring-core-5.0.6.RELEASE-sources
        //        String path = "E:\\tools\\maven_repo\\org\\springframework\\spring-core\\5.0.6.RELEASE\\spring-core-5.0.6.RELEASE-sources.jar";
        String path = "E:\\sourcecode\\sts_space\\log4j";
        File f = new File(path);
        PrintCnPath p = null;
        if (f.isDirectory()) {
            p = new PrintFolderCnPath(path);
        } else {
            p = new PrintZipCnPath(path);
        }
        //打印
        p.print();
        System.out.println("");
    }

    interface PrintCnPath {
        void print();
    }

    class PrintZipCnPath implements PrintCnPath {
        private String path;

        public PrintZipCnPath(String path) {
            this.path = path;
        }

        public void print() {
            PathNode rootNode = new PathNode(path, new HashSet<PathNode>(), true);
            try (ZipFile zipFile = new ZipFile(path)) {
                Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();
                Predicate<ZipEntry> isJava = ze -> ze.getName().matches(".*java");
                zipFile.stream().filter(isFile.and(isJava)).forEach((ee) -> {

                    String fname = ee.getName();
                    //分割斜线并循环
                    String[] strs = CommonUtil.splitSlash(fname);
                    PathNode parentNode = rootNode;
                    if (strs.length > 0) {
                        for (int i = 0; i <= strs.length - 1; i++) {
                            String packagePathName = strs[i];
                            boolean isDir = i == strs.length - 1 ? false : true;
                            PathNode newTempNode = new PathNode(packagePathName, new HashSet<PathNode>(), isDir);
                            if (!parentNode.getChilds().contains(newTempNode)) {
                                parentNode.getChilds().add(newTempNode);
                            } else {
                                //没有类似get的方法，或者indexof的方法，就自己写循环匹配取出set的对应值
                                for (Iterator<PathNode> iterator = parentNode.getChilds().iterator(); iterator
                                        .hasNext();) {
                                    PathNode f = (PathNode) iterator.next();
                                    if (f.equals(newTempNode)) {//之所以能匹配到是因为FileNode重写了equals方法
                                        //取到后赋值
                                        newTempNode = f;
                                    }
                                }
                            }
                            parentNode = newTempNode;
                        }
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            displayDir(rootNode, "");

        }

        private void displayDir(PathNode dir, String prefix) {
            boolean isFile = !dir.isDir();
            String name = dir.getName();
            //翻译
            name = isFile && !prefix.equals("") ? name + "(" + translateFile(name) + ")"
                    : name + "(" + translateDir(name) + ")";
            //打印到控制台
            System.out.println(prefix + name);
            prefix = prefix.replace("├─", "│").replace("└─", " ");

            if (isFile) {
                return;
            }
            PathNode[] files = dir.getChilds().toArray(new PathNode[] {});
            //按字母和是否文件夹排序
            Arrays.sort(files, (a, b) -> {
                int rs = -1;
                if (a.isDir() && !b.isDir()) {//a文件 ，b文件{夹}时返回1
                    rs = -1;
                } else if (!a.isDir() && b.isDir()) {//a文件夹, b文件时返回-1
                    rs = 1;
                } else {
                    rs = a.compareTo(b);
                }
                return rs;
            });
            for (int i = 0; files != null && i < files.length; i++) {
                if (i == files.length - 1) {
                    displayDir(files[i], prefix + "  └─");
                } else {
                    displayDir(files[i], prefix + "  ├─");
                }
            }
        }
    }

    class PrintFolderCnPath implements PrintCnPath {
        private String path;

        public PrintFolderCnPath(String path) {
            this.path = path;
        }

        @Override
        public void print() {
            displayDir(new File(path), "");
        }

        private void displayDir(File dir, String prefix) {
            boolean isFile = dir.isFile();
            String name = dir.getName();
            //翻译
            name = isFile && !prefix.equals("") ? name + "(" + translateFile(name) + ")"
                    : name + "(" + translateDir(name) + ")";
            System.out.println(prefix + name);
            prefix = prefix.replace("├─", "│").replace("└─", " ");

            if (isFile) {
                return;
            }
            File files[] = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    //                   System.out.println(pathname);
                    return (pathname.isDirectory() && !pathname.getName().contains(".svn"))
                            || pathname.getName().endsWith(".java");
                }
            });
            //            File files[] = dir.listFiles(new FilenameFilter() {
            //                @Override
            //                public boolean accept(File dir, String name) {
            //                    System.out.println("dir:" + dir + "   name:" + name);
            //                    return name.endsWith(".java") ;
            //                }
            //            });
            Arrays.sort(files, (a, b) -> {
                int rs = -1;
                if (a.isDirectory() && b.isFile()) {
                    rs = -1;
                } else if (a.isFile() && b.isDirectory()) {
                    rs = 1;
                } else {
                    rs = a.compareTo(b);
                }
                return rs;
            });
            for (int i = 0; files != null && i < files.length; i++) {
                if (i == files.length - 1) {
                    displayDir(files[i], prefix + "  └─");
                } else {
                    displayDir(files[i], prefix + "  ├─");
                }
            }
        }
    }

    private String translateDir(String en) {
        String cn = DictionaryUtil.translate(en);
        return cn == null ? en : cn;
    }

    private String translateFile(String en) {
        StringBuilder sbf = new StringBuilder("");
        int dotIndex = en.indexOf(".");
        if (dotIndex > 0) {
            String f = en.substring(0, dotIndex);
            String[] cns = CamelSplitUtil.split(f);
            for (String c : cns) {
                //翻译分割后的
                sbf.append(translateDir(c));
            }
        }
        return sbf.toString();
    }
}
