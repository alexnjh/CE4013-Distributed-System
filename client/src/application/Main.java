package application;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;

public class Main extends Application {

	Stage window;
	Scene scene2, manageFacility, bookFacility,bookDate, viewBook, monBook, modBook;
	BorderPane border, sRoom;
	Label ipLabel,portLabel, selectRoom;
	Button button1, button2, button3, button4, vbutton;
	Button create, view, modify, remove, monitor;
	String roomValue;
	
	Connection connect;
	

	@Override
	public void start(Stage primaryStage) throws UnknownHostException, MalformedURLException{
		window = primaryStage;
		primaryStage.setResizable(false);;
		primaryStage.setTitle("Client Interface");

		// Only add booking is working currently

	
	
		////////////////////////////////////////////view Booking////////////////////////////////////////////
		GridPane vBook = new GridPane();
		vBook.setPadding(new Insets(10, 10, 10, 10));
		vBook.setVgap(8);// set vertical gap
		vBook.setHgap(10);
		Label conId = new Label("Enter Confirmation ID: ");
		GridPane.setConstraints(conId, 0, 0);

		// text field
		TextField id = new TextField();
		GridPane.setConstraints(id, 1, 0);
		
		vbutton = new Button("Checking");
		// button1.setText("Click to Start");
		GridPane.setConstraints(vbutton, 1, 2);
		
		///new window to show the time the date and the booking they have
		
		
		
		
		
		
		EventHandler<ActionEvent> idCheck = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if(id.getText().isEmpty()) {
					
					Alert alert2 = new Alert(AlertType.ERROR);
					alert2.setTitle("Error Mesaage");
					alert2.setHeaderText("You forgotten to enter the Confirmation ID");
					alert2.setContentText("Please enter the confirmation id again");
					alert2.showAndWait();
					
				}else
					//sent the confirmation id to the server
					
					//wait for the reply
					//view the bookings
					//change booking button
//					modBook = new Scene();
//					modify = new Button();
//					modify.setOnAction(e->modBook);
					//get the id , result then sort the message out
					Message.viewDis("con","hello",roomValue,"sdate","edate");
					window.setScene(scene2);
			}
		};	
		vbutton.setOnAction(idCheck);
		
		vBook.getChildren().addAll(conId, id, vbutton);
		vBook.setAlignment(Pos.CENTER);
		viewBook = new Scene(vBook, 300, 300);
		
		
		byte[] buffer = new byte[1024];
		
////////////////monitoring booking ///////let the user have facility name day and time slot they want to check
		GridPane mBook = new GridPane();
//		mBook.setVgap(8);
//		mBook.setPadding(new Insets(5, 5, 5, 5));
//		mBook.add(new Label("Day"), 0, 0);
//		
//		mBook.add(dropDates, 1, 0);
//		mBook.add(new Label("Start Time: "), 4, 0);
//		mBook.add(sthrs_combo, 5, 0);
//		mBook.add(new Label(": "), 6, 0);
//		mBook.add(stmin_combo, 8, 0);
//		
//		mBook.setVgap(5);
//		mBook.add(new Label("End Time: "), 4, 1);
//		mBook.add(endhrs_combo, 5, 1);
//		mBook.add(new Label(": "), 6, 1);
//		mBook.add(endmin_combo, 8, 1);
//		
//		
//		
//		
//		
//		monBook = new Scene(mBook,300,300);
		
		
		///	///////////////////////////////////	


		//window.setScene(bookDate);
		//window.setScene(viewBook);
//		window.setScene(bookFacility);
		ConnectionScene.showScene(window);
//		window.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
