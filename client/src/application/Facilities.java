package application;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Facilities {
	
	public static String facilities[]= {"RoomA","Meeting Room B", "RoomC"};
	
	public static void showScene(Stage stage)
	{	
		
		GridPane facSelect = new GridPane();
		
		ComboBox roomBook = new ComboBox(FXCollections.observableArrayList(facilities));
		Button backBtn = new Button("Back");
		Label selectRoom = new Label("Choose Facility: ");

		facSelect.add(roomBook, 0, 0, 1, 1);
		facSelect.add(backBtn, 0, 5, 1, 1);
		
//		facSelect.getChildren().addAll(selectRoom,roomBook);
//		//room.getChildren().addAll(new Label("Choose A Facility: "),roomBook);
//		
//		EventHandler<ActionEvent> bookAction = new EventHandler<ActionEvent>() {
//			public void handle(ActionEvent e) {
//				//request for the booking facility
//				//go to the booking date page
//				roomValue = roomBook.getValue().toString();
//				System.out.println(roomBook.getValue());
//				window.setScene(bookDate);
//			}
//		};
//		roomBook.setOnAction(bookAction);
		Scene scene = new Scene(facSelect,400,400);
	    stage.setScene(scene);
	    stage.show();
	}
	
}
