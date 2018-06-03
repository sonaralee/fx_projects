package todo;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import todo.datamodel.TodoData;
import todo.datamodel.TodoItem;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {
    //private List<TodoItem> todoItems;
    @FXML
    private ListView<TodoItem> todoListView;
    @FXML
    private TextArea todoDetailsView;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterButton;

    private FilteredList<TodoItem> filtered;

    private Predicate<TodoItem> wantAllItems;
    private Predicate<TodoItem> wantTodaysItems;

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("New Item");
        dialog.setHeaderText("Use this dialog to create a new todo item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog" +
                                                      ".fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
            //deadlineLabel.setText(item.getDeadline().toString());

        } catch(IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            DialogController dc = fxmlLoader.getController();
            TodoItem newItem = dc.processResults();
            todoListView.getSelectionModel().select(newItem);
        }
    }

    @FXML
    public void showEditItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Item");
        dialog.setHeaderText("Use this dialog to edit a todo item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("editItemDialog" +
                ".fxml"));
        try {
            TodoItem item = todoListView.getSelectionModel().getSelectedItem();
            Parent dialogContent = fxmlLoader.load();
            DialogController ec = fxmlLoader.getController();
            ec.populateForEdit(item);

            dialog.getDialogPane().setContent(dialogContent);

        } catch(IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            DialogController ec = fxmlLoader.getController();
            TodoItem item = todoListView.getSelectionModel().getSelectedItem();
            ec.processEdit(item);
            todoListView.refresh();
            todoDetailsView.setText(item.getDetails());
            deadlineLabel.setText(item.getDeadline().toString());
        }

    }

    public void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to back out.");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            TodoData.getInstance().deleteTodoItem(item);
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
        }
    }

    @FXML
    public void handleFilterButton() {
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(filterButton.isSelected()) {
            filtered.setPredicate(wantTodaysItems);
            if(filtered.isEmpty()) {
                todoDetailsView.clear();
                deadlineLabel.setText("");
            } else if (filtered.contains(selectedItem)) {
                todoListView.getSelectionModel().select(selectedItem);
            } else {
                todoListView.getSelectionModel().selectFirst();
            }
        } else {
            filtered.setPredicate(wantAllItems);
            todoListView.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }

//    @FXML
//    public void handleClickListView() {
//        TodoItem item = todoListView.getSelectionModel().getSelectedItem();
//        todoDetailsView.setText(item.getDetails());
//        deadlineLabel.setText(item.getDeadline().toString());
//    }

    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> {
            TodoItem item = todoListView.getSelectionModel().getSelectedItem();
            deleteItem(item);
        });

        listContextMenu.getItems().addAll(deleteMenuItem);

        todoListView.getSelectionModel().selectedItemProperty()
                    .addListener((observable, oldValue, newValue) -> {
                        if(newValue != null) {
                            TodoItem item = todoListView.getSelectionModel()
                                                        .getSelectedItem();
                            todoDetailsView.setText(item.getDetails());
                            DateTimeFormatter df =
                                    DateTimeFormatter.ofPattern("MMMM d, yyyy");
                            deadlineLabel.setText(df.format(item.getDeadline()));
                        }
                    });

        // create lists for filtering and auto-sorting
        wantAllItems = new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem todoItem) {
                return true;
            }
        };
        wantTodaysItems = new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem todoItem) {
                return todoItem.getDeadline().equals(LocalDate.now());
            }
        };
        filtered = new FilteredList<>(TodoData.getInstance().getTodoItems(),
                                                                        wantAllItems);
        SortedList<TodoItem> sorted = new SortedList<>(filtered,
                Comparator.comparing(TodoItem::getDeadline));

        todoListView.setItems(sorted);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<>() {
            public ListCell<TodoItem> call(ListView<TodoItem> param) {
                ListCell<TodoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(TodoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setText(null);
                        } else {
                            setText(item.getShortDescription());
                            if(item.getDeadline().equals(LocalDate.now())) {
                                setTextFill(Color.CORAL);
                            } else if(item.getDeadline().equals(LocalDate.now()
                                                        .plusDays(1))) {
                                setTextFill(Color.rgb(204,138,71));
                            } else if(item.getDeadline().isBefore(LocalDate.now())) {
                                setTextFill(Color.RED);
                            }
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );

                return cell;
            }
        });

    }

}
