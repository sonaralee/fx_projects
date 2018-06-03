package css_fx;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;

public class Controller {
    @FXML
    private Slider volume;
    public void onButtonClicked() {
        volume.adjustValue(50);
    }
}
