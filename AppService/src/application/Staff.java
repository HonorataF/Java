package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class Staff {
    private static TableView<User> userTableView = new TableView<>();

    // Fields for the User details for edit and delete
    private static TextField userIDField = new TextField();
    private static TextField nameField = new TextField();
    private static TextField userLastNameField = new TextField();
    private static TextField addressField = new TextField();
    private static TextField mobileField = new TextField();
    private static TextField emailField = new TextField();
    private static PasswordField passwordField = new PasswordField();

    public static void showAddCustomerForm() {
        Stage addCustomerStage = new Stage();
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 5, 25));

        // Fields for the User details
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

        // Role selection
        ChoiceBox<String> roleChoiceBox = new ChoiceBox<>();
        roleChoiceBox.getItems().addAll("Customer", "Staff");
        roleChoiceBox.setValue("Customer"); // Default value
        grid.add(new Label("Role:"), 0, 6);
        grid.add(roleChoiceBox, 1, 6);

        // Save button
        Button btnSave = new Button("Save");
        btnSave.setOnAction(e -> {
            saveCustomer(
                    nameField.getText(),
                    addressField.getText(),
                    mobileField.getText(),
                    emailField.getText(),
                    userLastNameField.getText(),
                    passwordField.getText(),
                    roleChoiceBox.getValue() // Get selected role from choice box
            );
            userTableView.setItems(getAllUsers()); // Refresh the table view
        });

        // Bind the selected user from the TableView to the input fields
        userTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                userLastNameField.setText(newSelection.getUserLastName());
                addressField.setText(newSelection.getAddress());
                mobileField.setText(newSelection.getMobile());
                emailField.setText(newSelection.getEmail());
            }
        });

        
        Button btnDelete = new Button("Delete");
        btnDelete.setOnAction(e -> deleteUser());

        Button btnEdit = new Button("Edit");
        btnEdit.setOnAction(e -> {
            // Get the values from the input fields
            int userID = Integer.parseInt(userIDField.getText());
            String name = nameField.getText();
            String userLastName = userLastNameField.getText();
            String address = addressField.getText();
            String mobile = mobileField.getText();
            String email = emailField.getText();
            String password = passwordField.getText();

            // Call updateUser method
            updateUser(userID, name, address, mobile, email, userLastName, password);
        });

        // User ID field and OK button
        userIDField.setPromptText("User ID");
        Button btnOK = new Button("OK");
        btnOK.setOnAction(e -> {
            int userID = Integer.parseInt(userIDField.getText());
            User user = getUserByID(userID);
            if (user != null) {
                // Populate fields with user data
                nameField.setText(user.getName());
                userLastNameField.setText(user.getUserLastName());
                addressField.setText(user.getAddress());
                mobileField.setText(user.getMobile());
                emailField.setText(user.getEmail());
            } else {
                // Handle case where user ID is not found
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("User not found. Please enter a valid User ID.");
                alert.showAndWait();
            }
        });

        HBox editDeleteBox = new HBox(10, new Label("User ID:"), userIDField, btnOK, btnEdit, btnDelete);
        editDeleteBox.setAlignment(Pos.CENTER);

        // TableView for User data
        userTableView = setupUserTableView();
        userTableView.setItems(getAllUsers()); // Populate TableView with users

        // Layout for form and TableView
        VBox layout = new VBox(10); // Spacing between nodes
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.getChildren().addAll(grid, btnSave, editDeleteBox, userTableView); // Adding all the elements

        Scene scene = new Scene(layout);
        addCustomerStage.setScene(scene);
        addCustomerStage.setTitle("Add/Edit Customer");
        addCustomerStage.show();
    }

    private static void updateUser(int userID, String name, String address, String mobile, String email,
            String userLastName, String password) {
        String sql = "UPDATE User SET Name=?, UserLastName=?, Address=?, Mobile=?, Email=?, Password=? WHERE UserID=?";

        // Hash the password
        String hashedPassword = hashPassword(password);

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameters
            pstmt.setString(1, name);
            pstmt.setString(2, userLastName);
            pstmt.setString(3, address);
            pstmt.setString(4, mobile);
            pstmt.setString(5, email);
            pstmt.setString(6, hashedPassword); // Use the hashed password
            pstmt.setInt(7, userID);

            // Execute update
            int rowsUpdated = pstmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Database update successful
                System.out.println("User updated successfully.");
                // Refresh the table view after updating the user
                userTableView.setItems(getAllUsers());
            } else {
                // Database update failed
                System.out.println("Failed to update user.");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }


    static void deleteUser() {
        String userIDText = userIDField.getText();
        if (userIDText.isEmpty()) {
            // If the userIDField is empty, display an error message and return
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid User ID.");
            alert.showAndWait();
            return;
        }

        int userID = Integer.parseInt(userIDText);

        // Display confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this user?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed, delete the user from the database
            String sql = "DELETE FROM User WHERE UserID=?";

            try (Connection conn = DbConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, userID);

                pstmt.executeUpdate();

                // Refresh the table view after deleting the user
                userTableView.setItems(getAllUsers());
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exceptions appropriately
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static TableView<User> setupUserTableView() {
        TableView<User> tableView = new TableView<>();

        // Define columns
        TableColumn<User, Integer> columnUserID = new TableColumn<>("UserID");
        columnUserID.setCellValueFactory(new PropertyValueFactory<>("userID"));

        TableColumn<User, String> columnName = new TableColumn<>("Name");
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<User, String> columnAddress = new TableColumn<>("Address");
        columnAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<User, String> columnMobile = new TableColumn<>("Mobile");
        columnMobile.setCellValueFactory(new PropertyValueFactory<>("mobile"));

        TableColumn<User, String> columnEmail = new TableColumn<>("Email");
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<User, String> columnUsername = new TableColumn<>("UserLastName");
        columnUsername.setCellValueFactory(new PropertyValueFactory<>("userLastName"));

        TableColumn<User, String> columnPassword = new TableColumn<>("Password");
        columnPassword.setCellValueFactory(new PropertyValueFactory<>("password"));

        TableColumn<User, String> columnRole = new TableColumn<>("Role");
        columnRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        // Add columns to the table
        tableView.getColumns().addAll(columnUserID, columnName, columnAddress, columnMobile, columnEmail, columnUsername, columnPassword, columnRole);

        // Set column widths if desired (optional)
        columnUserID.setMinWidth(50);
        columnName.setMinWidth(100);
        columnAddress.setMinWidth(200);
        columnMobile.setMinWidth(100);
        columnEmail.setMinWidth(200);
        columnUsername.setMinWidth(100);
        columnPassword.setMinWidth(100);
        columnRole.setMinWidth(100);

        return tableView;
    }

    private static void saveCustomer(String name, String address, String mobile, String email,
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
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    private static String hashPassword(String password) {
        // Implement hashing algorithm here (e.g., SHA-256)
        return password; // Dummy implementation for demonstration
    }

    private static ObservableList<User> getAllUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT * FROM User";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("UserID"),
                        rs.getString("Name"),
                        rs.getString("Address"),
                        rs.getString("Mobile"),
                        rs.getString("Email"),
                        rs.getString("UserLastName"),
                        rs.getString("Password"), // Retrieve the password if necessary
                        rs.getString("Role")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
        return users;
    }
    
    private static User getUserByID(int userID) {
        String sql = "SELECT * FROM User WHERE UserID = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("UserID"),
                            rs.getString("Name"),
                            rs.getString("Address"),
                            rs.getString("Mobile"),
                            rs.getString("Email"),
                            rs.getString("UserLastName"),
                            rs.getString("Password"), // Retrieve the password if necessary
                            rs.getString("Role")
                    );
                    // Debug print
                    System.out.println("User details retrieved: " + user);
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
        return null;
    }
}
