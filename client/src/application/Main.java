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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;

public class Main extends Application {

	Stage window;
	Scene scene2, bookFacility ,bookDate;
	Label ipLabel,portLabel;
	Button button1, button2, button3, button4;
	Connections connect = new Connections();
	

	@Override
	public void start(Stage primaryStage) throws UnknownHostException, MalformedURLException{
		window = primaryStage;
		primaryStage.setTitle("Client Interface");

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(8);// set vertical gap
		grid.setHgap(10);
		// IP Address label
		ipLabel = new Label("IP address");
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
		button1.setOnAction(e -> window.setScene(scene2));


		grid.getChildren().addAll(ipLabel, ipInput, portlabel, portInput, button1);
		Scene scene = new Scene(grid, 300, 200);

		// layout 2 for login page
		GridPane login = new GridPane();

		// User label
		Label userlabel = new Label("Username ");
		GridPane.setConstraints(userlabel, 0, 0);

		// text field
		TextField username = new TextField();
		GridPane.setConstraints(username, 1, 0);

		// password label
		Label passlabel = new Label("Password ");
		GridPane.setConstraints(passlabel, 0, 1);

		// password field
		TextField password = new TextField();
		password.setPromptText("Password");
		GridPane.setConstraints(password, 1, 1);

		// button 2
		button2 = new Button("Login");
		GridPane.setConstraints(button2, 1, 5);
		button2.setOnAction(e -> window.setScene(bookFacility));

		login.setVgap(8);// set vertical gap
		login.setHgap(10);
		login.setAlignment(Pos.CENTER);//make it center
		login.getChildren().addAll(userlabel, username, passlabel, password, button2);
		scene2 = new Scene(login, 300, 200);

//		GridPane layout3 =new GridPane();	
		//layout for the booking of meeting room give out the rooms
		GridPane room = new GridPane();
		String bookRoom[]= {"RoomA","RoomB", "RoomC"};
		ComboBox roomBook = new ComboBox(FXCollections.observableArrayList(bookRoom));
		
		bookFacility = new Scene(roomBook,300,300);
		EventHandler<ActionEvent> bookAction = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				//request for the booking facility
				//go to the booking date page
				
				System.out.println(roomBook.getValue());
				window.setScene(bookDate);
			}
		};
			
		roomBook.setOnAction(bookAction);
		

		// layout 4 for booking
		GridPane bookGrid = new GridPane();
		bookGrid.setHgap(10);
		bookGrid.setPadding(new Insets(5, 5, 5, 5));
		bookGrid.add(new Label("Day"), 0, 0);

		//String week_days[] = { "Monday", "Tuesday", "Wednesday", "Thrusday", "Friday" };
		ComboBox dropDates = new ComboBox(FXCollections.observableArrayList(Day.values()));

		// star time day
		// combo to show the 1 to 24
		ComboBox starthrs_combo = new ComboBox();
		starthrs_combo.getItems().setAll(IntStream.rangeClosed(0, 23).boxed().collect(Collectors.toList()));
		ComboBox startmin_combo = new ComboBox();
		startmin_combo.getItems().setAll(IntStream.rangeClosed(0, 59).boxed().collect(Collectors.toList()));

		bookGrid.add(dropDates, 1, 0);
		bookGrid.add(new Label("Start Time: "), 4, 0);
		bookGrid.add(starthrs_combo, 5, 0);
		bookGrid.add(new Label(": "), 6, 0);
		bookGrid.add(startmin_combo, 8, 0);

		// end time
		ComboBox endhrs_combo = new ComboBox();
		endhrs_combo.getItems().setAll(IntStream.rangeClosed(00, 23).boxed().collect(Collectors.toList()));
		ComboBox endmin_combo = new ComboBox();
		endmin_combo.getItems().setAll(IntStream.rangeClosed(00, 59).boxed().collect(Collectors.toList()));

		bookGrid.setVgap(5);
		bookGrid.add(new Label("End Time: "), 4, 1);
		bookGrid.add(endhrs_combo, 5, 1);
		bookGrid.add(new Label(": "), 6, 1);
		bookGrid.add(endmin_combo, 8, 1);

		
		EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				int hrs, min;
				Day day;
				day = (Day) dropDates.getValue();
				hrs = (int) starthrs_combo.getValue();
				min = (int) startmin_combo.getValue();
				Date dating = new Date(day,hrs,min);
				System.out.println( dating);
			}
		};

		// Set on action
		button4 = new Button("Submit ");
		bookGrid.setConstraints(button4, 1, 5);
		bookGrid.add(button4, 10, 0);
		button4.setOnAction(event);

		// TilePane x = new TilePane(dropdown ,selected);

		bookDate = new Scene(bookGrid, 600, 300);
		window.setScene(scene);
		window.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

}
