package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard {

    private static final int BUTTON_WIDTH = 200; // Set the width for the buttons

    public static void showStaffDashboard() {
        Stage staffStage = new Stage();
        VBox staffLayout = new VBox(15);
        staffLayout.setAlignment(Pos.CENTER);
        staffLayout.setPadding(new Insets(20));

        // Create buttons and set their width
        Button addCustomerButton = createButton("Manage Customers");
        addCustomerButton.setOnAction(event -> Staff.showAddCustomerForm());

        Button manageServicesButton = createButton("Manage Services");
        Button viewQuotesButton = createButton("View Quotes");
        Button generateReportsButton = createButton("Generate Reports");
        Button logOutButton = createButton("Log Out");

        // Add action for log out button
        logOutButton.setOnAction(event -> {
            staffStage.close(); // Close the staff dashboard window
            Main main = new Main();
            Stage loginStage = new Stage();
            try {
                main.start(loginStage); // Show the login window
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Show goodbye message
            Alert goodbyeAlert = new Alert(Alert.AlertType.INFORMATION);
            goodbyeAlert.setTitle("Goodbye!");
            goodbyeAlert.setHeaderText(null);
            goodbyeAlert.setContentText("Goodbye, see you soon!");
            goodbyeAlert.showAndWait();
        });

        // Add the buttons to the layout
        staffLayout.getChildren().addAll(
                addCustomerButton, manageServicesButton, viewQuotesButton, generateReportsButton, logOutButton
        );

        Scene staffScene = new Scene(staffLayout, 300, 250); // Adjusted window size
        staffStage.setScene(staffScene);
        staffStage.setTitle("Staff Dashboard");
        staffStage.show();
    }

    // Helper method to create a button with a standard width
    private static Button createButton(String text) {
        Button button = new Button(text);
        button.setMinWidth(BUTTON_WIDTH);
        button.setMaxWidth(BUTTON_WIDTH);
        button.setPrefWidth(BUTTON_WIDTH);
        // Set any other button styles or event handlers here if needed
        return button;
    }

    public static void showCustomerDashboard() {
        Stage customerStage = new Stage();
        VBox customerLayout = new VBox(10);
        customerLayout.setAlignment(Pos.CENTER);
        customerLayout.setPadding(new Insets(20));

        // Add buttons or other controls for customer functionality here
        Button manageAccount = createButton("Manage Account");
        manageAccount.setOnAction(event -> {
            showManageAccountForm();
        });

        Button viewServicesButton = createButton("Browse Services");
        viewServicesButton.setOnAction(event -> {
            Service.viewServices();
        });

        Button requestQuoteButton = createButton("Request Quote");
        requestQuoteButton.setOnAction(event -> {
            sentQoute();
        });

        Button viewAppointmentsButton = createButton("View Appointments");
        viewAppointmentsButton.setOnAction(event -> {
            // Handle view appointments action
        });

        Button logOutButton = createButton("Log Out");
        logOutButton.setOnAction(event -> {
            customerStage.close(); // Close the customer dashboard window
            Main main = new Main();
            Stage loginStage = new Stage();
            try {
                main.start(loginStage); // Show the login window
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Show goodbye message
            Alert goodbyeAlert = new Alert(Alert.AlertType.INFORMATION);
            goodbyeAlert.setTitle("Goodbye!");
            goodbyeAlert.setHeaderText(null);
            goodbyeAlert.setContentText("Goodbye, see you soon!");
            goodbyeAlert.showAndWait();
        });

        customerLayout.getChildren().addAll(manageAccount, viewServicesButton, requestQuoteButton, viewAppointmentsButton, logOutButton);

        Scene customerScene = new Scene(customerLayout, 300, 250); // Adjusted window size
        customerStage.setScene(customerScene);
        customerStage.setTitle("Customer Dashboard");
        customerStage.show();
    }

    private static void showManageAccountForm() {
        // Retrieve the current user's details from the database
        User currentUser = getCurrentUserFromDatabase();

        // Create the stage and layout
        Stage manageAccountStage = new Stage();
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

        // Set the fields with the current user's data
        nameField.setText(currentUser.getName());
        userLastNameField.setText(currentUser.getUserLastName());
        addressField.setText(currentUser.getAddress());
        mobileField.setText(currentUser.getMobile());
        emailField.setText(currentUser.getEmail());
        passwordField.setText(currentUser.getPassword()); // Note: This is not recommended for security reasons

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

        // Update button
        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> {
            boolean confirmed = showConfirmationDialog("Update Account", "Are you sure you want to update this account?");
            if (confirmed) {
                updateUser(currentUser, nameField.getText(), addressField.getText(), mobileField.getText(), emailField.getText(), userLastNameField.getText(), passwordField.getText());
                manageAccountStage.close();
            }
        });

        // Delete button
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            boolean confirmed = showConfirmationDialog("Delete Account", "Are you sure you want to delete this account?");
            if (confirmed) {
                deleteUser(currentUser);
                manageAccountStage.close();
            }
        });

        // Add buttons to the layout
        HBox buttonBox = new HBox(10, updateButton, deleteButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(10); // Spacing between nodes
        layout.setAlignment(Pos.TOP_LEFT);
        layout.getChildren().addAll(grid, buttonBox); // Adding all the elements

        Scene scene = new Scene(layout);
        manageAccountStage.setScene(scene);
        manageAccountStage.setTitle("Manage Account");
        manageAccountStage.show();
    }


    private static boolean showConfirmationDialog(String title, String message) {
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle(title);
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText(message);
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private static void updateUser(User user, String name, String address, String mobile, String email, String userLastName, String password) {
        String sql = "UPDATE User SET name = ?, address = ?, mobile = ?, email = ?, userLastName = ?, password = ? WHERE userID = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, address);
            pstmt.setString(3, mobile);
            pstmt.setString(4, email);
            pstmt.setString(5, userLastName);
            pstmt.setString(6, password); // Remember to hash the password before setting it
            pstmt.setInt(7, user.getUserID());

            pstmt.executeUpdate();
            showAlert(AlertType.INFORMATION, "Success", "User details updated successfully.");

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Error updating user details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void deleteUser(User user) {
        String sql = "DELETE FROM User WHERE userID = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, user.getUserID());
            pstmt.executeUpdate();
            showAlert(AlertType.INFORMATION, "Success", "User deleted successfully.");

        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Database Error", "Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private static User getCurrentUserFromDatabase() {
        User currentUser = null;
        String sql = "SELECT * FROM User WHERE userID = ?"; // Replace 'userID' with the column name for user ID

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Assuming you have a method to get the current user ID
            int currentUserId = getCurrentUserId(); // Replace this with your actual method

            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Retrieve user data from the result set
                int userID = rs.getInt("userID");
                String name = rs.getString("name");
                String address = rs.getString("address");
                String mobile = rs.getString("mobile");
                String email = rs.getString("email");
                String userLastName = rs.getString("userLastName");
                String password = rs.getString("password"); // Remember to hash the password
                String role = rs.getString("role");

                // Create a User object with the retrieved data
                currentUser = new User(userID, name, address, mobile, email, userLastName, password, role);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }

        return currentUser;
    }
    
    private static int getCurrentUserId() {
        return CurrentUserContext.getCurrentUserId();
    }
    
    
    public static void sentQoute() {
        ObservableList<Service> services = Service.getServicesFromDatabase();

        ListView<Service> serviceListView = new ListView<>(services);
        serviceListView.setPrefSize(200, 150);

        Label nameLabel = new Label("Name: ");
        Label descriptionLabel = new Label("Description: ");
        Label estimatedDurationLabel = new Label("Estimated Duration: ");
        Label costLabel = new Label("Cost: ");

        // Set action for selecting a service
        serviceListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                nameLabel.setText("Name: " + newValue.getName());
                descriptionLabel.setText("Description: " + newValue.getDescription());
                estimatedDurationLabel.setText("Estimated Duration: " + newValue.getEstimatedDuration());
                costLabel.setText("Cost: $" + String.format("%.2f", newValue.getCost()));
            }
        });

        GridPane serviceDetailsGrid = new GridPane();
        serviceDetailsGrid.setPadding(new Insets(10));
        serviceDetailsGrid.setVgap(5);
        serviceDetailsGrid.setHgap(5);
        serviceDetailsGrid.addRow(0, new Label(""), nameLabel);
        serviceDetailsGrid.addRow(1, new Label(""), descriptionLabel);
        serviceDetailsGrid.addRow(2, new Label(""), estimatedDurationLabel);
        serviceDetailsGrid.addRow(3, new Label(""), costLabel);

        Button sendQuoteButton = new Button("Send Quote");
        sendQuoteButton.setOnAction(event -> {
            Service selectedService = serviceListView.getSelectionModel().getSelectedItem();
            if (selectedService != null) {
                insertQuote(selectedService);
            } else {
                showAlert(Alert.AlertType.ERROR, "Selection Error", "No service selected for the quote.");
            }
        });

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(serviceListView, serviceDetailsGrid, sendQuoteButton);

        Scene scene = new Scene(layout, 400, 300);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Sent Quote");
        stage.show();
    }
    
    private static void insertQuote(Service service) {
        // The CustomerID and UserUserID need to be acquired from the current user context, not hardcoded
        int customerID = CurrentUserContext.getCurrentUserId(); // This should be the ID of the logged-in customer
        int userUserID = CurrentUserContext.getCurrentUserId(); // This should be the ID of the logged-in user

        String sql = "INSERT INTO Quote (CustomerID, ServiceID, PriceEstimate, Status, UserUserID) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerID);
            pstmt.setInt(2, service.getServiceID());
            pstmt.setDouble(3, service.getCost());
            pstmt.setString(4, "Pending"); // Status can be "Pending" or however your business logic defines it
            pstmt.setInt(5, userUserID);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Quote successfully sent to the database.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failure", "Failed to send the quote to the database.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
