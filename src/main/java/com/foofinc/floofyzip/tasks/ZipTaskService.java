package com.foofinc.floofyzip.tasks;

import com.foofinc.floofyzip.file_zipping.FileZipperAPI;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;

public class ZipTaskService extends Service<Void> {

    private final File zipFile;
    private final File destination;
    private Task<Void> task;

    public ZipTaskService(File zipFile, File destination) {
        this.zipFile = zipFile;
        this.destination = destination;
    }

    @Override
    public boolean cancel() {
        return super.cancel();
    }

    @Override
    protected Task<Void> createTask() {
        if (zipFile.isDirectory()) {
            return new Task<>() {
                @Override
                protected Void call() {
                    FileZipperAPI.zipDirectory(zipFile, destination);
                    return null;
                }
            };
        } else {
            return new Task<>() {
                @Override
                protected Void call() {
                    FileZipperAPI.zipSingleFileToDestination(zipFile, destination);
                    return null;
                }
            };
        }
    }
}
