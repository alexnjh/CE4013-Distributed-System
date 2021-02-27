package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class MenuScene {
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
		border.setPadding(new Insets(120,50,120,140));// top,right,bottom,left
		
		Button create = new Button("New Booking");
		Button remove = new Button("Remove Booking");
		Button view = new Button("View Booking");
		Button monitor = new Button("Monitor Booking");
		
		create.setOnAction(e -> NewBookingScene.showScene(stage, conn, name));
//		remove.setOnAction(e -> window.setScene(bookFacility));
//		view.setOnAction(e -> window.setScene(viewBook));
//		monitor.setOnAction(e -> window.setScene(bookFacility));
		
		create.setMaxWidth(Double.MAX_VALUE);
		remove.setMaxWidth(Double.MAX_VALUE);
		view.setMaxWidth(Double.MAX_VALUE);
		monitor.setMaxWidth(Double.MAX_VALUE);
		
		roomButton.getChildren().addAll(create, remove, view, monitor);
		
		border.setCenter(roomButton);
		Scene scene = new Scene(border,400,400); 
	    stage.setScene(scene);
	    stage.show();
	}
}
