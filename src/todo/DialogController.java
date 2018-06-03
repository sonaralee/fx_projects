package todo;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import todo.datamodel.TodoData;
import todo.datamodel.TodoItem;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDesc;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadlinePicker;

    public void populateForEdit(TodoItem item) {
        //TodoItem item = new TodoItem(TodoData.getInstance())
        shortDesc.setText(item.getShortDescription());
        detailsArea.setText(item.getDetails());
        deadlinePicker.setValue(item.getDeadline());
    }

    public TodoItem processResults() {
        String shortDescription = shortDesc.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadline = deadlinePicker.getValue();

        TodoItem newItem = new TodoItem(shortDescription, details,
                                        deadline);
        TodoData.getInstance().addTodoItem(newItem);
        return newItem;
    }

    public void processEdit(TodoItem item) {
        item.setDeadline(deadlinePicker.getValue());
        item.setDetails(detailsArea.getText());
        item.setShortDescription(shortDesc.getText());
    }
}
