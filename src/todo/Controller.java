package todo;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import todo.datamodel.TodoItem;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private List<TodoItem> todoItems;
    @FXML
    private ListView todoListView;

    public void initialize() {
        TodoItem item1 = new TodoItem("Mail birthday card",
                "Buy a 30th birthday card for John",
                LocalDate.of(2018, Month.APRIL, 25));
        TodoItem item2 = new TodoItem("Doctor's appointment",
                "See Dr. Smith, bring paperwork",
                LocalDate.of(2018, Month.MAY, 23));
        TodoItem item3 = new TodoItem("Pick up Doug at train station",
                "Arriving on March 23 on 5:00 train",
                LocalDate.of(2018, Month.JUNE, 12));
        TodoItem item4 = new TodoItem("Finish design proposal",
                "I promised Mike I'd email mockups by 7/11",
                LocalDate.of(2018, Month.JULY, 11));
        TodoItem item5 = new TodoItem("Pick up dry cleaning",
                "Clothes should be ready by Wednesday",
                LocalDate.of(2018, Month.JUNE, 27));

        todoItems = new ArrayList<>();
        todoItems.add(item1);
        todoItems.add(item2);
        todoItems.add(item3);
        todoItems.add(item4);
        todoItems.add(item5);

        todoListView.getItems().setAll(todoItems);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
}
