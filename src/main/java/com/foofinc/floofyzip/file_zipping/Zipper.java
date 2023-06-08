package com.foofinc.floofyzip.file_zipping;

import java.io.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

    private static final BiFunction<File, String, FileOutputStream> fOSWithDestAndName = (f, s) -> {
        try {
            return new FileOutputStream(String.format("%s/%s.zip", f, s));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    };

    static void zipSingleFile(File fileToZip, File destFile) throws IOException {


        Function<File, String> getFileNameWithoutSuffix = file -> {
            String fileName = fileToZip.getName();
            int dotIndex = fileName.length();
            if (fileName.contains(".")) {
                for (int i = 0, limit = fileName.length(); i < limit; i++) {
                    if (fileName.charAt(i) == '.') dotIndex = i;
                }
            }
            return fileName.substring(0, dotIndex);
        };

        FileOutputStream fos = fOSWithDestAndName.apply(destFile, getFileNameWithoutSuffix.apply(fileToZip));
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        compressFile(fileToZip, fileToZip.getName(), zipOut);

        zipOut.close();
        fos.close();
    }

    static void zipMultipleFiles(File[] filesToZip, String zipName, File destFile) throws IOException {

        FileOutputStream fos = fOSWithDestAndName.apply(destFile, zipName);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        for (File fileToZip : filesToZip) {
            compressFile(fileToZip, fileToZip.getName(), zipOut);
        }

        zipOut.close();
        fos.close();
    }

    static void zipDirectory(File dirToZip, File destFile) throws IOException {

        FileOutputStream fos = fOSWithDestAndName.apply(destFile, dirToZip.getName());
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        zipFile(dirToZip, dirToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            assert children != null;
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        compressFile(fileToZip, fileName, zipOut);
    }

    private static void compressFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}
