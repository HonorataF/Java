package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Registration {
    public static void showRegistrationForm() {
        Stage registrationStage = new Stage();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 5, 25));

        // Fields for the User details
        TextField nameField = new TextField();
        TextField userLastNameField = new TextField();
        TextField addressField = new TextField();
        TextField mobileField = new TextField();
        TextField emailField = new TextField();
        PasswordField passwordField = new PasswordField();

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("User Last Name:"), 0, 1);
        grid.add(userLastNameField, 1, 1);
        grid.add(new Label("Address:"), 0, 2);
        grid.add(addressField, 1, 2);
        grid.add(new Label("Mobile:"), 0, 3);
        grid.add(mobileField, 1, 3);
        grid.add(new Label("Email:"), 0, 4);
        grid.add(emailField, 1, 4);
        grid.add(new Label("Password:"), 0, 5);
        grid.add(passwordField, 1, 5);

        // Save button
        Button btnSave = new Button("Save");
        btnSave.setOnAction(e -> {
            saveUser(
                    nameField.getText(),
                    addressField.getText(),
                    mobileField.getText(),
                    emailField.getText(),
                    userLastNameField.getText(),
                    passwordField.getText(), // In a real application, this should be hashed
                    "Customer" // Role is always "Customer"
            );
            registrationStage.close(); // Close the registration window after saving
        });

        VBox layout = new VBox(10); // Spacing between nodes
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(grid, btnSave); // Adding all the elements

        Scene scene = new Scene(layout);
        registrationStage.setScene(scene);
        registrationStage.setTitle("Registration");
        registrationStage.show();
    }

    private static void saveUser(String name, String address, String mobile, String email,
                                 String userLastName, String password, String role) {
        String sql = "INSERT INTO User (Name, Address, Mobile, Email, UserLastName, Password, Role) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Hash the password
        String hashedPassword = hashPassword(password);

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameters
            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, mobile);
            pstmt.setString(4, email);
            pstmt.setString(5, userLastName);
            pstmt.setString(6, hashedPassword); // Use the hashed password
            pstmt.setString(7, role);

            // Execute update
            pstmt.executeUpdate();
            // Display confirmation message
            Alert confirmation = new Alert(Alert.AlertType.INFORMATION);
            confirmation.setTitle("Registration Successful");
            confirmation.setHeaderText(null);
            confirmation.setContentText("You have been successfully registered. Please check your email for login instructions.");
            confirmation.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    private static String hashPassword(String password) {
        // Implement hashing algorithm here (e.g., SHA-256)
        return password; // Dummy implementation for demonstration
    }
}
