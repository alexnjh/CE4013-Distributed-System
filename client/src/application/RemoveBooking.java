package application;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import application.NewBookingScene.ProgressForm;
import javafx.concurrent.Task;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class RemoveBooking {
private static ReplyMessage reply;
	
	
	public static void showScene(Stage stage, Connection conn, String name){
		GridPane rBook = new GridPane();
		rBook.setPadding(new Insets(10, 10, 10, 10));
		rBook.setVgap(8);// set vertical gap
		rBook.setHgap(10);
		Label conId = new Label("Enter ConfirmationID: ");
		GridPane.setConstraints(conId, 0, 0);

		// text field
		TextField id = new TextField();
		GridPane.setConstraints(id, 1, 0);
		
		Button rbutton = new Button("Checking");
		Button cancel = new Button("Cancel");
		
		HBox.setMargin(conId, new Insets(0, 10, 0, 0));
		rBook.add(new HBox(conId, id), 0,4);
		
		HBox.setMargin(rbutton, new Insets(0, 10, 0, 0));
	    rBook.add(new HBox(rbutton, cancel), 0, 5);
		
		rBook.setAlignment(Pos.CENTER);
		
	    rbutton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	
		    	String cid =(String)id.getText();
		    	
		    	Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				
								
				if(id.getText().isEmpty()) {
					
					alert.setContentText("Invalid Confirmation ID");
					alert.showAndWait();
				}
		    	//sent the request to the server           	
		    	ProgressForm pForm = new ProgressForm();
				
	            // Send message to server and wait for reply
	            Task<Void> task = new Task<Void>() {
	                @Override
	                public Void call() throws InterruptedException {
	                	
	                	// remove booking request
	                	RemoveRequest req = new RemoveRequest(id.getText());
	                	
	                	updateProgress(1, 10);
	                	try {
							reply = conn.sendMessage(req.Marshal());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.out.println(e.toString());
						}
	                	
	                    updateProgress(10, 10);
	                    return null ;
	                }
	            };
	            // binds progress of progress bars to progress of task:
	            pForm.activateProgressBar(task);
	            
	            // in real life this method would get the result of the task
	            // and update the UI based on its value:
	            task.setOnSucceeded(event -> {
	            	pForm.getDialogStage().close();
	            	
	            	System.out.println(reply.getType());
		    	
		      	if (reply.getType().equals("Error")) {
					Alert alert2 = new Alert(AlertType.ERROR);
					alert2.setTitle("Server reply");
					alert2.setHeaderText(null);
					alert2.setContentText(new String(reply.getPayload(), StandardCharsets.UTF_8));
					alert2.showAndWait();		
            	}else if (reply.getType().equals("Confirm")){
            		// Else show the confirmation ID
            		//need to show the success dialog box??
					Alert alert2 = new Alert(AlertType.INFORMATION);
					alert2.setTitle("Remove Request");
					alert2.setHeaderText(null);
					alert2.setContentText(new String(reply.getPayload(), StandardCharsets.UTF_8));
					alert2.showAndWait();	
            	}	
		      	MenuScene.showScene(stage, conn, name);
	      });
	     }
	 });
		    	
		
		cancel.setOnAction(e -> MenuScene.showScene(stage, conn, name));
		
		Scene scene = new Scene(rBook,400,400); 
	    stage.setScene(scene);
	    stage.show();
	       
	
	}
}