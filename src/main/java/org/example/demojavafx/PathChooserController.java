package org.example.demojavafx;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.example.demojavafx.markers.Exporter;
import org.example.util.ExportDirUnit;

import java.io.File;

public class PathChooserController {
    @FXML
    private TextField pathField;

    private String directory;

    private Exporter exporter;

    public void initialize() {
        reloadDefaultPath();
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

    public void setExporter(Exporter exporter) {
        this.exporter = exporter;
        reloadDefaultPath();
    }

    private void reloadDefaultPath() {
        String defaultPath = ExportDirUnit.getExportDir();
        directory = defaultPath;
        if(exporter != null && exporter.getType() == Exporter.Type.CODE) {
            directory = defaultPath + "\\code";
        } else if (exporter != null && exporter.getType() == Exporter.Type.SPEC) {
            directory = defaultPath + "\\spec";
        }
        pathField.setText(directory);
    }

    public String getDirectory() {
        return directory;
    }
}
