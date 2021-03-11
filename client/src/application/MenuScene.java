package application;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class MenuScene {
	
	public static int currentSelected = 0;
	
	public static void showScene(Stage stage, Connection conn, String name)
	{
		//layout for the booking of meeting room give out the rooms
		TilePane roomButton = new TilePane();
		
		roomButton.setVgap(8);
		roomButton.setHgap(8);
		roomButton.setTileAlignment(Pos.CENTER);
		roomButton.setOrientation(Orientation.VERTICAL);
			
		//create/remove/modify/
		BorderPane border = new BorderPane();
		border.setPadding(new Insets(60,50,60,85));// top,right,bottom,left
		
		Label Name = new Label("Invocation Sementics + Mode");
		
		Button create = new Button("New Booking");
		Button remove = new Button("Remove Booking");
		Button view = new Button("View Booking");
		Button monitor = new Button("Monitor Booking");
		Button change = new Button("Update Booking");
		Button query = new Button("Query Availability");
		
		ComboBox dropInvocation = new ComboBox(FXCollections.observableArrayList(
				"At Least Once (Normal)",
				"At Most Once (Normal)",
				"At Least Once (Server->Client Lost)",
				"At Least Once (Client->Server Lost)",
				"At Least Once (Both Side Lost)",
				"At Most Once (Server->Client Lost)",
				"At Most Once (Client->Server Lost)",
				"At Most Once (Both Side Lost)"));
		dropInvocation.getSelectionModel().select(currentSelected);
		
		dropInvocation.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal)->{
			currentSelected = (int)newVal;
		});
		
		create.setOnAction(e -> NewBookingScene.showScene(stage, conn, name, currentSelected));
		remove.setOnAction(e ->RemoveBooking.showScene(stage, conn,name, currentSelected));
		view.setOnAction(e -> ViewBooking.showScene(stage, conn, name, currentSelected));
		change.setOnAction(e->UpdateBooking.showUpdateMenu(stage, conn, name, currentSelected));
		query.setOnAction(e->QueryAvailabilityScene.showScene(stage, conn, name, currentSelected));
		monitor.setOnAction(e -> MonitorBooking.showScene(stage, conn, name, currentSelected));
		
		create.setMaxWidth(Double.MAX_VALUE);
		remove.setMaxWidth(Double.MAX_VALUE);
		view.setMaxWidth(Double.MAX_VALUE);
		monitor.setMaxWidth(Double.MAX_VALUE);
		change.setMaxWidth(Double.MAX_VALUE);
		query.setMaxWidth(Double.MAX_VALUE);
		
		roomButton.getChildren().addAll(Name,dropInvocation, create, remove, view, monitor, change, query);
		
		border.setCenter(roomButton);
		Scene scene = new Scene(border,400,400); 
	    stage.setScene(scene);
	    stage.show();
	}
}
