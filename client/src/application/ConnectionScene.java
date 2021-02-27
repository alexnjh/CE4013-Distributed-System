package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ConnectionScene {
	
	public static void showScene(Stage stage)
	{
		GridPane startPage = new GridPane();
		startPage.setPadding(new Insets(10, 10, 10, 10));
		startPage.setVgap(8);	// set vertical gap
		startPage.setHgap(10);  // set horizontal gap
		
		
		// IP Address label
		Label ipLabel = new Label("IP Address");
		GridPane.setConstraints(ipLabel, 0, 0);

		// text field
		TextField ipInput = new TextField("127.0.0.1");
		GridPane.setConstraints(ipInput, 1, 0);

		// port number label
		Label portlabel = new Label("Port No.");
		GridPane.setConstraints(portlabel, 0, 1);

		// text field
		TextField portInput = new TextField("2222");
		GridPane.setConstraints(portInput, 1, 1);

		Button button1 = new Button("Connect");
		// button1.setText("Click to Start");
		GridPane.setConstraints(button1, 1, 2);
		
		
		button1.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
				if(ipInput.getText().isEmpty()) {
					
					Alert alert1 = new Alert(AlertType.ERROR);
					alert1.setTitle("Error");
					alert1.setHeaderText(null);
					alert1.setContentText("Hostname cannot be empty");
					alert1.showAndWait();
					
				}else if (portInput.getText().isEmpty()){
					
					Alert alert1 = new Alert(AlertType.ERROR);
					alert1.setTitle("Error");
					alert1.setHeaderText(null);
					alert1.setContentText("Port number cannot be empty");
					alert1.showAndWait();
					
				}else if (isNumeric(portInput.getText()) != true){
					
					Alert alert1 = new Alert(AlertType.ERROR);
					alert1.setTitle("Error");
					alert1.setHeaderText(null);
					alert1.setContentText("Port number entered is invalid");
					alert1.showAndWait();
					
				}else {
					
					// Initialize connection object
					Connection connect = new Connection(ipInput.getText(),Integer.parseInt(portInput.getText()));
					
					// Show next scene
					UsernameScene.showScene(stage, connect);
				}
		    }
		});
		

		startPage.setAlignment(Pos.CENTER);//make it center
		startPage.getChildren().addAll(ipLabel, ipInput, portlabel, portInput, button1);
		
		Scene scene = new Scene(startPage, 400, 400);
	    stage.setScene(scene);
	    stage.show();
	}
	
	public static boolean isNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Integer.parseInt(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
}
