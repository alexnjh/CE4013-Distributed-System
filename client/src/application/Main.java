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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {

	Stage window;
	Scene scene2, manageFacility, bookFacility,bookDate, viewBook, monBook, modBook;
	BorderPane border, sRoom;
	Label ipLabel,portLabel, selectRoom;
	Button button1, button2, button3, button4, vbutton;
	Button create, view, modify, remove, monitor;
	String roomValue;
	
//	Connections connect = new Connections();
	

	@Override
	public void start(Stage primaryStage) throws UnknownHostException, MalformedURLException{
		window = primaryStage;
		primaryStage.setTitle("Client Interface");

		GridPane startPage = new GridPane();
		startPage.setPadding(new Insets(10, 10, 10, 10));
		startPage.setVgap(8);// set vertical gap
		startPage.setHgap(10);
		// IP Address label
		ipLabel = new Label("IP Address");
		GridPane.setConstraints(ipLabel, 0, 0);

		// text field
		TextField ipInput = new TextField();
		GridPane.setConstraints(ipInput, 1, 0);

		// port number label
		Label portlabel = new Label("Port No.");
		GridPane.setConstraints(portlabel, 0, 1);

		// text field
		TextField portInput = new TextField();
		GridPane.setConstraints(portInput, 1, 1);

		button1 = new Button("Checking");
		// button1.setText("Click to Start");
		GridPane.setConstraints(button1, 1, 2);
		
		
		EventHandler<ActionEvent> conCheck = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if(portInput.getText().isEmpty()) {
					
					Alert alert1 = new Alert(AlertType.ERROR);
					alert1.setTitle("Error Mesaage");
					alert1.setHeaderText("You forgotten to Enter the Port");
					alert1.setContentText("Please enter the Port again");
					alert1.showAndWait();
					
				}else
					window.setScene(scene2);
			}
		};	
		button1.setOnAction(conCheck);
		

		startPage.setAlignment(Pos.CENTER);//make it center
		startPage.getChildren().addAll(ipLabel, ipInput, portlabel, portInput, button1);
		
		Scene scene = new Scene(startPage, 400, 400);

		/////////////////// layout 2 for Login page
		GridPane login = new GridPane();

		// User label
		Label userlabel = new Label("Username ");
		GridPane.setConstraints(userlabel, 0, 0);

		// text field
		TextField username = new TextField();
		GridPane.setConstraints(username, 1, 0);

		// button 2
		button2 = new Button("Manage Facility");
		GridPane.setConstraints(button2, 1, 5);
		button2.setOnAction(e -> window.setScene(manageFacility));

		login.setVgap(8);// set vertical gap
		login.setHgap(10);
		login.setAlignment(Pos.CENTER);//make it center
		login.getChildren().addAll(userlabel, username ,button2);
		scene2 = new Scene(login, 400, 400);

		//layout for the booking of meeting room give out the rooms
		GridPane room = new GridPane();
		TilePane roomButton = new TilePane();
		
		
		roomButton.setVgap(8);
		roomButton.setHgap(8);
		roomButton.setTileAlignment(Pos.CENTER);
		roomButton.setOrientation(Orientation.VERTICAL);
			
		//create/remove/modify/
		border = new BorderPane();
		border.setPadding(new Insets(120,50,120,140));// top,right,bottom,left
		
		create = new Button("New Booking");
		remove = new Button("Remove Booking");
		view = new Button("View Booking");
		monitor = new Button("Monitor Booking");
		
		create.setOnAction(e -> window.setScene(bookFacility));
		remove.setOnAction(e -> window.setScene(bookFacility));
		view.setOnAction(e -> window.setScene(viewBook));
		monitor.setOnAction(e -> window.setScene(bookFacility));
		
		create.setMaxWidth(Double.MAX_VALUE);
		remove.setMaxWidth(Double.MAX_VALUE);
		view.setMaxWidth(Double.MAX_VALUE);
		monitor.setMaxWidth(Double.MAX_VALUE);
		
		roomButton.getChildren().addAll(create, remove, view, monitor);
		
		border.setCenter(roomButton);
		manageFacility = new Scene(border,400,400); 
		
		
		String bookRoom[]= {"RoomA","RoomB", "RoomC"};
		ComboBox roomBook = new ComboBox(FXCollections.observableArrayList(bookRoom));
		
		selectRoom = new Label("Choose Facility: ");
		GridPane.setConstraints(selectRoom, 0, 1);
		room.setAlignment(Pos.CENTER);
		
		GridPane.setConstraints(roomBook, 0, 3);
		room.getChildren().addAll(selectRoom,roomBook);
		//room.getChildren().addAll(new Label("Choose A Facility: "),roomBook);
		
		EventHandler<ActionEvent> bookAction = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				//request for the booking facility
				//go to the booking date page
				roomValue = roomBook.getValue().toString();
				System.out.println(roomBook.getValue());
				window.setScene(bookDate);
			}
		};
		roomBook.setOnAction(bookAction);
		bookFacility = new Scene(room,400,400);
	
	
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
		
		
		///////////////////////////////////////////////////////////////////////////////
				//////layout 4 for adding booking facility
		GridPane bookGrid = new GridPane();
		bookGrid.setHgap(10);
		bookGrid.setPadding(new Insets(5, 5, 5, 5));
		bookGrid.add(new Label("Day"), 0, 0);

		ComboBox dropDates = new ComboBox(FXCollections.observableArrayList(Day.values()));
		dropDates.getSelectionModel().selectFirst();//get the first value in the combobox
		// star time day
		// combo to show the 1 to 24
		ComboBox<Integer> sthrs_combo = new ComboBox();
		sthrs_combo.getItems().setAll(IntStream.rangeClosed(0, 23).boxed().collect(Collectors.toList()));
		sthrs_combo.getSelectionModel().selectFirst();
		
		ComboBox<Integer> stmin_combo = new ComboBox();
		stmin_combo.getItems().setAll(IntStream.rangeClosed(0, 59).boxed().collect(Collectors.toList()));
		stmin_combo.getSelectionModel().selectFirst();
		
		bookGrid.add(dropDates, 1, 0);
		bookGrid.add(new Label("Start Time: "), 4, 0);
		bookGrid.add(sthrs_combo, 5, 0);
		bookGrid.add(new Label(": "), 6, 0);
		bookGrid.add(stmin_combo, 8, 0);

		// end time
		ComboBox<Integer> endhrs_combo = new ComboBox();
		endhrs_combo.getItems().setAll(IntStream.rangeClosed(00, 23).boxed().collect(Collectors.toList()));
		endhrs_combo.getSelectionModel().selectFirst();
		ComboBox<Integer> endmin_combo = new ComboBox();
		endmin_combo.getItems().setAll(IntStream.rangeClosed(00, 59).boxed().collect(Collectors.toList()));
		endmin_combo.getSelectionModel().selectFirst();
		
		
		bookGrid.setVgap(5);
		bookGrid.add(new Label("End Time: "), 4, 1);
		bookGrid.add(endhrs_combo, 5, 1);
		bookGrid.add(new Label(": "), 6, 1);
		bookGrid.add(endmin_combo, 8, 1);
		
		byte[] buffer = new byte[1024];
		
		EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				int sthrs, stmin, endhrs, endmin ;
				Day day;
				day = (Day) dropDates.getValue();
				sthrs = sthrs_combo.getValue();
				stmin = stmin_combo.getValue();
				
				endhrs = endhrs_combo.getValue();
				endmin = endmin_combo.getValue();
				
				Alert alert2 = new Alert(AlertType.ERROR);
				alert2.setTitle("Error");
				
				//end hrs cannot be smaller than start hrs
				if((endhrs-sthrs)<0) {
					System.out.println("Hello");
						alert2.setContentText("Please choose a time again");
						alert2.showAndWait();
				
				}else if(endhrs == sthrs) {
					//end time must be bigger than the start minute
					if(!(endmin >= stmin))	{							
						alert2.setContentText("Please choose a time again");
						alert2.showAndWait();
					}else {
						Date stdate = new Date(day,sthrs,stmin);
						Date enddate = new Date(day,endhrs,endmin);
						//display the code and confirmation id
						//request for id 
						Message.viewDis("ConfirmationID","ID is +++++", roomValue, stdate.toString(), enddate.toString());
						window.setScene(manageFacility);
						
//booker name length (1 byte) | booker name (x bytes) | start date/time (7 bytes) | end date/time (7 bytes) | facility name length (1 byte) | facility name (x bytes)
					}
				}
				else {
				Date stdate = new Date(day,sthrs,stmin);
				Date enddate = new Date(day,endhrs,endmin);
				System.out.println(stdate + "\nEnd: " + enddate);
				//request server for id
				
				//display the date they book
				Message.viewDis("confirmiD", "Hello", roomValue ,stdate.toString(), enddate.toString());
				window.setScene(manageFacility);
				}
			}
		};

		// Set button 4 to add booking
		button4 = new Button("Submit ");
		bookGrid.setConstraints(button4, 1, 5);
		bookGrid.add(button4, 10, 0);
		button4.setOnAction(event);
		bookDate = new Scene(bookGrid, 600, 300);
		
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


		window.setScene(bookDate);
		//window.setScene(viewBook);
		//window.setScene(scene);
		window.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
