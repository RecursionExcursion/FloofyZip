package com.foofinc.floofyzip;

import com.foofinc.floofyzip.file_zipping.FileZipperAPI;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class MainViewController implements Initializable {

    public TextField fileToZipField;
    public TextField zipDestinationField;

    public BorderPane mainPane;

    public Button chooseDestinationButton;
    public Button chooseFileButton;
    public Button zipButton;
    public Button zipModeButton;
    public Button floofButton;

    private boolean zipMode = true;
    private String zip = "Zip";
    private String unzip = "Unzip";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadBackGroundImage();
        setButtonsAndTextFields();
        makeTextFieldsDraggable();
    }

    private void setButtonsAndTextFields() {

        final int buttonWidth = 80;
        final int textFieldWidth = 200;

        Button[] buttons = {chooseFileButton, chooseDestinationButton,
                zipButton, zipModeButton, floofButton};
        TextField[] textFields = {fileToZipField, zipDestinationField};
        for (Button button : buttons) {
            button.setPrefWidth(buttonWidth);
        }
        for (TextField textField : textFields) {
            textField.setPrefWidth(textFieldWidth);
        }
    }

    private void makeTextFieldsDraggable() {

        Consumer<TextField> makeTextFieldsDraggable = field -> {
            field.setOnDragOver(event -> {
                if (event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
                event.consume();
            });
            field.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    field.setText(String.valueOf(db.getFiles().get(0)));
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            });
        };

        makeTextFieldsDraggable.accept(fileToZipField);
        makeTextFieldsDraggable.accept(zipDestinationField);
    }

    private void loadBackGroundImage() {

        Image image = new ImageRetriever().getImage();
        BackgroundSize bSize = new BackgroundSize(0, 0,
                                                  false, false, true, false);

        BackgroundImage bgi = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
                                                  BackgroundRepeat.NO_REPEAT,
                                                  BackgroundPosition.CENTER,
                                                  bSize);
        mainPane.setBackground(new Background(bgi));
    }

    public void chooseFileToZipButtonClick(ActionEvent actionEvent) {
        File file = new FileChooser().showOpenDialog(getWindowFromEvent(actionEvent));
        if (file != null) {
            fileToZipField.textProperty().setValue(file.toString());
        }
    }

    public void chooseZipDestinationButtonClick(ActionEvent actionEvent) {
        File dir = new DirectoryChooser().showDialog(getWindowFromEvent(actionEvent));
        if (dir != null) {
            zipDestinationField.textProperty().setValue(dir.toString());
        }
    }

    private Window getWindowFromEvent(ActionEvent event) {
        Node eventNode = (Node) event.getSource();
        return eventNode.getScene().getWindow();
    }


    public void zipButtonClick() {

        //TODO Handle error if file is unzippable

        File fileToZip = new File(fileToZipField.getText());
        File zipFileDestination = new File(zipDestinationField.getText());
        if (Files.exists(fileToZip.toPath()) && Files.isDirectory(zipFileDestination.toPath())) {

            if (zipMode) {
                FileZipperAPI.zipSingleFileToDestination(fileToZip, zipFileDestination);
            }
            try {
                InputStream inputStream = fileToZip.toURI().toURL().openStream();
                FileZipperAPI.unZipFilesToDestination(inputStream, zipFileDestination);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }

    public void FloofClick() {
        loadBackGroundImage();
    }

    public void changeZipMode() {
        if (zipMode) {
            zipButton.setText(unzip);
        } else {
            zipButton.setText(zip);
        }
        zipMode = !zipMode;
    }
}