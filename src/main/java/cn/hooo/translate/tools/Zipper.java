package cn.hooo.translate.tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class Zipper {
    public static ArrayList<ZipEntry> readEntries(String zipFileName) {
        ArrayList<ZipEntry> al = new ArrayList<ZipEntry>(2048);
        try (ZipFile zipFile = new ZipFile(zipFileName)) {
            Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();
            Predicate<ZipEntry> isJava = ze -> ze.getName().matches(".*java");
            al = zipFile.stream().filter(isFile.and(isJava)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return al;
    }

    public static void readZipFile(String file) throws Exception {
        ZipFile zf = new ZipFile(file);
        zf.getName();

        /*InputStream in = new BufferedInputStream(new FileInputStream(file));
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {
        	if (ze.isDirectory()) {
        		System.out.println(ze.getName());
        	} else {
        		System.err.println("file - " + ze.getName() + " : " + ze.getSize() + " bytes");
        		long size = ze.getSize();
        		if (size > 0) {
        			BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)));
        			String line;
        			while ((line = br.readLine()) != null) {
        				System.out.println(line);
        			}
        			br.close();
        		}
        		System.out.println();
        	}
        }
        zin.closeEntry();*/
    }

    public static void writeEntries(String zipFileName, List<ZipEntry> entryList) {

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream("D:\\test.zip"), Charset.forName("GBK"))) {
            for (ZipEntry zipEntry : entryList) {
                zos.putNextEntry(zipEntry);
            }
            zos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // ArrayList<ZipEntry> al = new ArrayList<ZipEntry>(2048);
        // try (ZipFile zipFile = new ZipFile(zipFileName)) {
        //// Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();
        //// Predicate<ZipEntry> isJava = ze -> ze.getName().matches(".*java");
        //// al =
        // zipFile.stream().filter(isFile.and(isJava)).collect(ArrayList::new,
        // ArrayList::add, ArrayList::addAll);
        //
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // return al;
    }

    public static void printEntries(PrintStream stream, String zipFileName) {
        try (ZipFile zipFile = new ZipFile(zipFileName)) {
            Predicate<ZipEntry> isFile = ze -> !ze.isDirectory();
            Predicate<ZipEntry> isJava = ze -> ze.getName().matches(".*java");
            Comparator<ZipEntry> bySize = (ze1, ze2) -> Long.valueOf(ze2.getSize())
                    .compareTo(Long.valueOf(ze1.getSize()));
            Map<String, List<ZipEntry>> result = zipFile.stream().filter(isFile.and(isJava)).sorted(bySize)
                    .collect(Collectors.groupingBy(Zipper::fileIndex));
            result.entrySet().stream().forEach(stream::println);

        } catch (IOException e) {
            // error while opening a ZIP file
        }
    }

    private static String fileIndex(ZipEntry zipEntry) {
        Path path = Paths.get(zipEntry.getName());
        Path fileName = path.getFileName();
        return fileName.toString().substring(0, 1).toLowerCase();
    }

}