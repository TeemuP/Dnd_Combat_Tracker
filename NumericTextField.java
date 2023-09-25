

import javafx.scene.control.TextField;

public class NumericTextField extends TextField {
    public NumericTextField() {
        this.setPromptText("Enter a number");

        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                this.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
}
