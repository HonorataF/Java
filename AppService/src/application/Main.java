package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        Label usernameLabel = new Label("Username:");
        gridPane.add(usernameLabel, 0, 0);

        TextField usernameTextField = new TextField();
        gridPane.add(usernameTextField, 1, 0);

        Label passwordLabel = new Label("Password:");
        gridPane.add(passwordLabel, 0, 1);

        PasswordField passwordField = new PasswordField();
        gridPane.add(passwordField, 1, 1);

        Button loginButton = new Button("Login");
        gridPane.add(loginButton, 1, 2);
        GridPane.setMargin(loginButton, new Insets(20, 0, 0, 0));

        Button registerButton = new Button("Register");
        gridPane.add(registerButton, 0, 2);
        GridPane.setMargin(registerButton, new Insets(20, 0, 0, 0));

        loginButton.setOnAction(e -> {
            User authenticatedUser = authenticate(usernameTextField.getText(), passwordField.getText());
            if (authenticatedUser != null) {
                CurrentUserContext.setCurrentUserId(authenticatedUser.getUserID());
                showAlert(AlertType.INFORMATION, "Login Successful", "Welcome, " + usernameTextField.getText() + "!");
                primaryStage.hide(); // Hide the login window
                openDashboard(authenticatedUser.getRole()); // Open the appropriate dashboard
            } else {
                showAlert(AlertType.ERROR, "Login Failed", "Incorrect username or password.");
            }
        });

        registerButton.setOnAction(e -> {
            Registration.showRegistrationForm(); // Open the registration form
        });

        Scene scene = new Scene(gridPane, 300, 200);
        primaryStage.setTitle("Login Window");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private User authenticate(String username, String password) {
        String sql = "SELECT * FROM User WHERE Name = ? AND Password = ?";
        User authenticatedUser = null;

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Retrieve user data from the result set
                int userID = rs.getInt("userID");
                String name = rs.getString("name");
                String address = rs.getString("address");
                String mobile = rs.getString("mobile");
                String email = rs.getString("email");
                String userLastName = rs.getString("userLastName");
                String hashedPassword = rs.getString("password"); // Remember to hash the password
                String role = rs.getString("role");

                // Create a User object with the retrieved data
                authenticatedUser = new User(userID, name, address, mobile, email, userLastName, hashedPassword, role);
            }
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "General Error", "An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return authenticatedUser;
    }


    private void openDashboard(String userType) {
        if ("Staff".equals(userType)) {
            Dashboard.showStaffDashboard();
        } else if ("Customer".equals(userType)) {
            Dashboard.showCustomerDashboard();
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
