package com.foofinc.floofyzip.controller;

import com.foofinc.floofyzip.tasks.UnzipTaskService;
import com.foofinc.floofyzip.tasks.ZipTaskService;
import com.foofinc.floofyzip.util.ImageRetriever;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
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
    public VBox zipFieldsVbox;


    public Button chooseDestinationButton;
    public Button chooseFileButton;
    public Button zipButton;

    public Button moreFloofButton;
    public Button lessFloofButton;

    public Button zipModeButton;
    public Button unzipModeButton;
    public Button floofButton;


    private boolean zipMode = true;
    private final String unzip = "Unzip";
    private final String zip = "Zip";

//    private Button cancelButton;
//    private Service<Void> runningService;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBackGroundImage();
        setUpButtonsAndTextFields();
        lessFloofClick();
        setToZipMode();
    }

    private void setUpButtonsAndTextFields() {

        final int buttonWidth = 80;
        final int textFieldWidth = 200;

        floofButton.setOnAction(event -> loadBackGroundImage());

        Button[] buttons = {chooseFileButton, chooseDestinationButton, zipButton,
                moreFloofButton, lessFloofButton, zipModeButton, unzipModeButton, floofButton};

        TextField[] textFields = {fileToZipField, zipDestinationField};
        for (Button button : buttons) {
            button.setPrefWidth(buttonWidth);
        }
        for (TextField textField : textFields) {
            textField.setPrefWidth(textFieldWidth);
        }
        setUpTextFieldListeners();
    }

    private void setUpTextFieldListeners() {

        Consumer<TextField> setCursor = field -> {
            field.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    field.positionCaret(field.getText().length());
                }
            });
        };

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

        setCursor.accept(fileToZipField);
        setCursor.accept(zipDestinationField);

        fileToZipField.textProperty().addListener((observableValue, oldVal, newVal) -> {

            if (Files.exists(new File(newVal).toPath())) {
                setTextFieldBorderTransparent(fileToZipField);
            } else {
                setTextFieldBorderRed(fileToZipField);
            }
        });

        zipDestinationField.textProperty().addListener((observableValue, oldVal, newVal) -> {

            if (Files.isDirectory(new File(newVal).toPath())) {
                setTextFieldBorderTransparent(zipDestinationField);
            } else {
                setTextFieldBorderRed(zipDestinationField);
            }
        });


    }

    private void loadBackGroundImage() {

        Image image = new ImageRetriever().getImageFromQueue();
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
        /*
        TODO Handle error if file is unzippable... CLEAN UP IMPLEMENTED LOGIC, TOO COUPLED AND ALL
         OVER THE PLACE.  RED outline stays after changing zip mode
        */
        File fileToZip = new File(fileToZipField.getText());
        File zipFileDestination = new File(zipDestinationField.getText());


        if (Files.exists(fileToZip.toPath()) && Files.isDirectory(zipFileDestination.toPath())) {

            if (zipMode) {
                //TODO ZipTask Goes here
                ZipTaskService zipTaskService = new ZipTaskService(fileToZip, zipFileDestination);
                executeTaskWithProgressbar(zipTaskService);
            } else {

                //fileToZip must be zipFile
                if (fileToZip.toString().endsWith(".zip")) {
                    InputStream unzipStream;
                    //TODO dilike try/
                    // catch here
                    try {
                        unzipStream = fileToZip.toURI().toURL().openStream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    UnzipTaskService zipTaskService = new UnzipTaskService(unzipStream, zipFileDestination);
                    executeTaskWithProgressbar(zipTaskService);
                    setTextFieldBorderTransparent(fileToZipField);
                } else {
                    setTextFieldBorderRed(fileToZipField);
                }
            }
        }
    }

    private void executeTaskWithProgressbar(Service<Void> service) {

//        runningService = service;
//        PopUpCreator popUpCreator = new PopUpCreator();
//
//        Stage progressBarPopUpStage = popUpCreator.getStage();
//        ProgressBar zipProgressBar = popUpCreator.getController().getZipProgressBar();

        VBox progressBarVbox = createProgressBarVbox();

        ProgressBar progressBar = null;
        Button cancelButton = null;
        for (Node child : progressBarVbox.getChildren()) {
            if (child instanceof ProgressBar) {
                progressBar = (ProgressBar) child;
            }
            if (child instanceof Button) {
                cancelButton = (Button) child;
            }
        }

        assert cancelButton != null;
        cancelButton.setOnAction(event -> {
            if (service != null && service.isRunning()) {
                service.cancel();
            }
        });

        service.setOnScheduled(e -> mainPane.setCenter(progressBarVbox));
        service.setOnSucceeded(e -> mainPane.setCenter(zipFieldsVbox));
        service.setOnCancelled(e -> mainPane.setCenter(zipFieldsVbox));

        assert progressBar != null;
        progressBar.progressProperty().bind(service.progressProperty());

        service.restart();
    }

    private VBox createProgressBarVbox() {

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);
        Button cancelButton = new Button("Cancel");

        VBox PBvBox = new VBox(progressBar, cancelButton);
        PBvBox.setSpacing(10);
        PBvBox.setAlignment(Pos.CENTER);

        return PBvBox;
    }

    private void setTextFieldBorderRed(TextField textField) {
        textField.setStyle("-fx-border-color: #ff0000");
    }

    private void setTextFieldBorderTransparent(TextField textField) {
        textField.setStyle("-fx-border-color: transparent");
    }

    public void setToZipMode() {
        zipButton.setText(zip);
        zipMode = true;
        zipModeButton.setDisable(true);
        unzipModeButton.setDisable(false);
    }

    public void setToUnzipMode() {
        zipMode = false;
        zipButton.setText(unzip);
        zipModeButton.setDisable(false);
        unzipModeButton.setDisable(true);
    }

    public void moreFloofClick() {
        setVboxChildrenVisibility(false);
        moreFloofButton.setDisable(true);
        lessFloofButton.setDisable(false);
    }

    public void lessFloofClick() {
        setVboxChildrenVisibility(true);
        moreFloofButton.setDisable(false);
        lessFloofButton.setDisable(true);
    }

    private void setVboxChildrenVisibility(boolean isVisible) {
        for (Node paneChild : mainPane.getChildren()) {
            if (paneChild instanceof VBox) {
                paneChild.setVisible(isVisible);
            }
        }
    }
}