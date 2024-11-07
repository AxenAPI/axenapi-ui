package org.example.demojavafx;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.example.util.ExportDirUnit;

import java.io.File;

public class PathChooserController {
    @FXML
    private TextField pathField;

    private String directory;

    public void initialize() {
        String defaultPath = ExportDirUnit.getExportDir();
        pathField.setText(defaultPath);
    }

    public void choosePath(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(pathField.getText()));
        File file = directoryChooser.showDialog(((Node) actionEvent.getTarget()).getScene().getWindow());
        if (file != null && file.isDirectory()) {
            pathField.setText(file.getAbsolutePath());
            directory = file.getAbsolutePath();
            System.out.println(directory);
        }
    }

}
