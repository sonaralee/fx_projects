package contacts;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ContactController {

    @FXML
    private TextField fNameField;
    @FXML
    private TextField lNameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField notesField;


    public Contact getNewContact() {
        String first = fNameField.getText();
        String last = lNameField.getText();
        String p = phoneField.getText();
        String n = notesField.getText();

        return new Contact(first, last, p, n);
    }

    public void populateForEdit(Contact c) {
        fNameField.setText(c.getFirstName());
        lNameField.setText(c.getLastName());
        phoneField.setText(c.getPhoneNumber());
        notesField.setText(c.getNotes());
    }

    public void processEdit(Contact c) {
        c.setFirstName(fNameField.getText());
        c.setLastName(lNameField.getText());
        c.setPhoneNumber(phoneField.getText());
        c.setNotes(notesField.getText());
    }

}
