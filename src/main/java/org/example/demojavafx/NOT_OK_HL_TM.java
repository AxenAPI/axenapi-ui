package org.example.demojavafx;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class NOT_OK_HL_TM {
    public void tryAgain(ActionEvent actionEvent) {
        Stage window = (Stage) ((javafx.scene.Node) (actionEvent.getTarget())).getScene().getWindow();
        window.close();
    }
}
