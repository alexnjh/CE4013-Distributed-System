package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
//Interface to enter the Client name.
public class UsernameScene {
	public static void showScene(Stage stage, Connection conn)
	{
		GridPane login = new GridPane();

		// User label
		Label userlabel = new Label("Username ");
		GridPane.setConstraints(userlabel, 0, 0);

		// text field
		TextField username = new TextField();
		GridPane.setConstraints(username, 1, 0);

		// button 2
		Button button = new Button("Manage Facility");
		GridPane.setConstraints(button, 1, 5);
		
		
		button.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
				if(username.getText().isEmpty()) {
					
					Alert alert2 = new Alert(AlertType.ERROR);
					alert2.setTitle("Error");
					alert2.setHeaderText(null);
					alert2.setContentText("Username cannot be empty");
					alert2.showAndWait();
					
				}else {
					// Show next scene
					MenuScene.showScene(stage, conn, username.getText());
				}
		    }
		});
		
		login.setVgap(8);// set vertical gap
		login.setHgap(10);
		login.setAlignment(Pos.CENTER);//make it center
		login.getChildren().addAll(userlabel, username ,button);
		Scene scene = new Scene(login, 400, 400);
	    stage.setScene(scene);
	    stage.show();
	}
}
