package org.example.demojavafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.util.OpenAPITranslator;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
        OpenAPITranslator.parseOPenAPI();
    }
}