package contacts;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Optional;

public class Controller {

    @FXML
    private TableView<Contact> table;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu contextMenu;
    private ContactData data;

    public void initialize() {
        contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> {
            Contact item = table.getSelectionModel().getSelectedItem();
            deleteItem(item);
        });

        contextMenu.getItems().addAll(deleteMenuItem);

        data = new ContactData();
        data.loadContacts();
        table.setItems(data.getContacts());

        table.setRowFactory(new Callback<>() {
            public TableRow<Contact> call(TableView<Contact> param) {
                    TableRow<Contact> row = new TableRow<>() {

                        @Override
                        protected void updateItem(Contact item, boolean empty) {
                            super.updateItem(item, empty);
                        }
                    };

                    row.emptyProperty().addListener(
                            (obs, wasEmpty, isNowEmpty) -> {
                                if(isNowEmpty) {
                                    row.setContextMenu(null);
                                } else {
                                    row.setContextMenu(contextMenu);
                                }
                            }
                    );

                    return row;
            }

        });

    }

    @FXML
    public void showAddContactDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Contact");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("addContactDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch(IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            ContactController cc = fxmlLoader.getController();
            Contact newC = cc.getNewContact();
            data.addContact(newC);
            data.saveContacts();
        }
    }

    @FXML
    public void showEditContactDialog() {
        Contact selected = table.getSelectionModel().getSelectedItem();
        if(selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No contact selected");
            alert.setHeaderText(null);
            alert.setContentText("Select the contact you want to edit.");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Contact");
        dialog.setHeaderText("Use this dialog to edit a contact");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(
                                    "editContactDialog.fxml"));
        try {
            Parent dialogContent = fxmlLoader.load();
            dialog.getDialogPane().setContent(dialogContent);

        } catch(IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        ContactController ec = fxmlLoader.getController();
        ec.populateForEdit(selected);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            Contact c = table.getSelectionModel().getSelectedItem();
            ec.processEdit(c);
            data.saveContacts();
        }

    }

    public void deleteItem(Contact c) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Contact");
        alert.setHeaderText("Delete contact: " + c.getFirstName()
                                            + " " + c.getLastName());
        alert.setContentText("Are you sure? Press OK to confirm, or cancel to back out.");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            data.deleteContact(c);
        }
    }

    public void showExitDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText(null);
        alert.setContentText("Exit the app? Press OK to confirm, or cancel to back out.");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            data.saveContacts();
            Platform.exit();
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        Contact selected = table.getSelectionModel().getSelectedItem();
        if(selected != null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selected);
            }
        }
    }
}
