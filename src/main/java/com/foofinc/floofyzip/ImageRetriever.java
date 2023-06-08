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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageRetriever {

    public Image getImage() {

        try {
            List<Image> images = getImages();
            Random r = new Random();
            return images.get(r.nextInt(images.size()));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Image> getImages() throws URISyntaxException, IOException {
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
    }
}
