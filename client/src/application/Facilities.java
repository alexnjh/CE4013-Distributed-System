package application;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
//A list of facilities
public class Facilities {
	
	public static String facilities[]= {"Meeting Room A","Meeting Room B", "Meeting Room C"};
	//Interface to choose facilities.
	public static void showScene(Stage stage)
	{	
		
		GridPane facSelect = new GridPane();
		
		ComboBox roomBook = new ComboBox(FXCollections.observableArrayList(facilities));
		Button backBtn = new Button("Back");
		Label selectRoom = new Label("Choose Facility: ");

		facSelect.add(roomBook, 0, 0, 1, 1);
		facSelect.add(backBtn, 0, 5, 1, 1);
		
		Scene scene = new Scene(facSelect,400,400);
	    stage.setScene(scene);
	    stage.show();
	}
	
}
