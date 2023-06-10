package com.foofinc.floofyzip.tasks;

import com.foofinc.floofyzip.file_zipping.FileZipperAPI;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.io.InputStream;

public class UnzipTaskService extends Service<Void> {

    private final InputStream fileStream;
    private final File destination;

    public UnzipTaskService(InputStream fileStream, File destination) {
        this.fileStream = fileStream;
        this.destination = destination;
    }

    @Override
    protected Task<Void> createTask() {
            return new Task<>() {
                @Override
                protected Void call() {
                    FileZipperAPI.unZipFilesToDestination(fileStream, destination);
                    return null;
                }
            };
    }
}