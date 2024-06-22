package org.example.eiscuno.view.alert;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.example.eiscuno.model.unoenum.EISCUnoEnum;
import org.example.eiscuno.view.alert.interfaces.AlertBox;

public class CustomAlert implements AlertBox {
    @Override
    public void showInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource(EISCUnoEnum.FAVICON.getFilePath()))));
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
