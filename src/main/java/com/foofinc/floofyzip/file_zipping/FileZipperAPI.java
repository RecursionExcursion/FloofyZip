package com.foofinc.floofyzip.file_zipping;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FileZipperAPI {


    public static void unZipFilesToDestination(InputStream stream, File destDir) {

        if (filePathExists(destDir)) {
            try {
                UnZipper.upZip(stream, destDir);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void zipSingleFileToDestination(File fileToZip, File destDir) {

        if (filePathExists(fileToZip, destDir)) {
            try {
                Zipper.zipSingleFile(fileToZip, destDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void zipMultipleFilesToDestination(File[] filesToZip, String zipName, File destDir) {

        if (filePathExists(filesToZip) && filePathExists(destDir)) {
            try {
                Zipper.zipMultipleFiles(filesToZip, zipName, destDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void zipDirectory(File dirToZip, File destDir) {

        if (filePathExists(dirToZip, destDir)) {
            try {
                Zipper.zipDirectory(dirToZip, destDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static boolean filePathExists(File... files) {
        for (File file : files) {
            if (!Files.exists(file.toPath())) return false;
        }
        return true;
    }
}
