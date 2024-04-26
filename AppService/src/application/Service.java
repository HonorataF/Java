package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Service {
    private int serviceID;
    private String name;
    private String description;
    private String estimatedDuration;
    private double cost;
    private int userUserID;

    public static void viewServices() {
        // Assuming you have a method to retrieve services from the database
        ObservableList<Service> services = getServicesFromDatabase();

        // Create the service list view
        ListView<Service> serviceListView = new ListView<>(services);
        serviceListView.setPrefSize(200, 150);

        // Create the service details labels
        Label nameLabel = new Label();
        Label descriptionLabel = new Label();
        Label estimatedDurationLabel = new Label();
        Label costLabel = new Label();

        // Create the service details grid
        GridPane serviceDetailsGrid = new GridPane();
        serviceDetailsGrid.setPadding(new Insets(10));
        serviceDetailsGrid.setVgap(5);
        serviceDetailsGrid.setHgap(5);
        serviceDetailsGrid.addRow(0, new Label("Name:"), nameLabel);
        serviceDetailsGrid.addRow(1, new Label("Description:"), descriptionLabel);
        serviceDetailsGrid.addRow(2, new Label("Estimated Duration:"), estimatedDurationLabel);
        serviceDetailsGrid.addRow(3, new Label("Cost:"), costLabel);

        // Set action for selecting a service
        serviceListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                nameLabel.setText(newValue.getName());
                descriptionLabel.setText(newValue.getDescription());
                estimatedDurationLabel.setText(newValue.getEstimatedDuration());
                costLabel.setText(String.valueOf(newValue.getCost()));
            }
        });

        // Create the layout
        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(serviceListView, serviceDetailsGrid);

        // Create the scene and stage
        Scene scene = new Scene(layout, 400, 300);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Browse Services");
        stage.show();
    }

    // Method to retrieve services from the database
    static ObservableList<Service> getServicesFromDatabase() {
        ObservableList<Service> services = FXCollections.observableArrayList();
        String sql = "SELECT ServiceID, Name, Description, EstimatedDuration, Cost, UserID FROM Service";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                services.add(new Service(
                        rs.getInt("ServiceID"),
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getString("EstimatedDuration"),
                        rs.getDouble("Cost"),
                        rs.getInt("UserID")
                ));
            }
        } catch (SQLException e) {
            System.err.println("SQL exception in getServicesFromDatabase: " + e.getMessage());
            // Add any other error handling that's appropriate for your application
        }
        return services;
    }
    
    @Override
    public String toString() {
        return name + " - " + description + " - $" + String.format("%.2f", cost);
    }

    // Constructor
    public Service(int serviceID, String name, String description, String estimatedDuration, double cost, int userUserID) {
        this.serviceID = serviceID;
        this.name = name;
        this.description = description;
        this.estimatedDuration = estimatedDuration;
        this.cost = cost;
        this.userUserID = userUserID; // Make sure you have this field in your database
    }

    // Getters and setters
    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(String estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getUserUserID() {
        return userUserID;
    }

    public void setUserUserID(int userUserID) {
        this.userUserID = userUserID;
    }
}
