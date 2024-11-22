package org.example.demojavafx;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.util.ExportDirUnit;

public class OKHLTM {
    public void openSourceCode(ActionEvent actionEvent) {
        //close the window
        ((Stage) ((Button) actionEvent.getSource()).getScene().getWindow()).close();
        String dir = ExportDirUnit.getExportDir() + "\\code";
        // open file browser in the directory of the project
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
