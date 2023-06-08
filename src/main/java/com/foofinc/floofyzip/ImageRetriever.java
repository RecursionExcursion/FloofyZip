package com.foofinc.floofyzip;

import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ImageRetriever {

    private final Queue<Image> imageQueue;
    private final Random rand;
    private final List<Image> images;

    public ImageRetriever() {
        images = getImages();
        rand = new Random();
        imageQueue = new ArrayDeque<>();
        fillImageQueue();
    }

    public Image getImageFromQueue() {
        Image pollImage = imageQueue.poll();
        fillImageQueue();
        return pollImage;
    }


    private List<Image> getImages() {
        try {

            URL resources = getClass().getResource("/images");
            assert resources != null;
            Path imagesDir = Paths.get(resources.toURI());
            List<Image> imageList = new ArrayList<>();
            try (DirectoryStream<Path> images = Files.newDirectoryStream(imagesDir)) {
                for (Path imagePath : images) {
                    URI uri = imagePath.toUri();
                    URL url = uri.toURL();
                    Image image = new Image(url.openStream());
                    imageList.add(image);
                }
            }
            return imageList;

        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillImageQueue() {
        while (imageQueue.size() < 30) {
            imageQueue.add(images.get(rand.nextInt(images.size())));
        }
    }
}
