package application;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import application.NewBookingScene.ProgressForm;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class UpdateBooking {
	
	private static ReplyMessage reply;
	
	public static void showScene(Stage stage, Connection conn, String name)
	{
		GridPane updateBook = new GridPane();
		updateBook.setPadding(new Insets(10, 10, 10, 10));
		updateBook.setVgap(8);// set vertical gap
		updateBook.setHgap(10);
		Label conId = new Label("Enter ID: ");
		
		// text field
		TextField id = new TextField();
		
		HBox.setMargin(conId, new Insets(0, 10, 0, 0));
		updateBook.add(new HBox(conId, id), 0,0);
		
		
		//time the user wants to increase or decrease
		String offset[]= {"+","-"};
		ComboBox dif = new ComboBox(FXCollections.observableArrayList(offset));
		
		TextField hr = new TextField();
		TextField min = new TextField();
		Label startLabel = new Label("Offset Time : ");
		
		updateBook.add(startLabel, 3,1);
		updateBook.add(new HBox(startLabel, dif),0, 2);
		
		Label time = new Label("Hr::Min "); 
		HBox.setMargin(time, new Insets(0, 10, 0, 0));
		updateBook.add(new HBox(time, createHrPane(hr,min)), 0, 3);
		
		
		Button updateBut = new Button("Checking");
		Button cancel = new Button("Cancel");
		
		HBox.setMargin(updateBut, new Insets(0, 10, 0, 0));
	    updateBook.add(new HBox(updateBut, cancel), 0, 6);
		
		updateBook.setAlignment(Pos.CENTER);
		
	    updateBut.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	//get required values
		    	String conID;
		    	int hrs,mins ,offset ;
		    	
		    	
		    	Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				
				conID = (String)id.getText();
				hrs = Integer.parseInt(hr.getText());
				mins = Integer.parseInt(min.getText());
				
						//if all input is empty			
			if(id.getText().isEmpty() && Helper.isNumeric(hr.getText()) && Helper.isNumeric(min.getText())) {
										
					alert.setContentText("Invalid Confirmation ID/Time");
					alert.showAndWait();
		    }else {
		    	//sent the request to the server with the id
		    		if(dif.getValue().toString().equalsIgnoreCase("+")) {
		    			//offset will be addition to previous
		    			// sent request to server
		    			offset = mins + (hrs*60); 
		       		}else{
		    		//offset will be minus
		       			offset = (-1)*(mins + (hrs*60)); 
		       		}
		    	
		    		ProgressForm pForm = new ProgressForm();
					
		            // Send message to server and wait for reply
		            Task<Void> task = new Task<Void>() {
		                @Override
		                public Void call() throws InterruptedException {
		                	
		        			
							// Create booking request
				UpdateRequest req = new UpdateRequest(conID, offset);
		                	
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
		            	// If reply is an error show the error
		            if (reply.getType().equals("Error")) {
							Alert alert2 = new Alert(AlertType.ERROR);
							alert2.setTitle("Server reply");
							alert2.setHeaderText(null);
							alert2.setContentText(new String(reply.getPayload(), StandardCharsets.UTF_8));
							alert2.showAndWait();		
		            }else if (reply.getType().equals("Confirm")){
		            		// Else show the confirmation ID
							Alert alert2 = new Alert(AlertType.INFORMATION);
							alert2.setTitle("Booking Confirmation ID");
							alert2.setHeaderText(null);
							alert2.setContentText(new String(reply.getPayload(), StandardCharsets.UTF_8));
							alert2.showAndWait();	
		            }
		            MenuScene.showScene(stage, conn, name);
		            });
		            Thread thread = new Thread(task);
		            thread.start();
		    }
				
		   }
	 });
		    	
		cancel.setOnAction(e -> MenuScene.showScene(stage, conn, name));
		
		Scene scene = new Scene(updateBook,400,400); 
	    stage.setScene(scene);
	    stage.show();
	       
	
	}
	private static HBox createHrPane(TextField t1, TextField t2) {
			
		    t1.setMaxWidth(60);
		    t2.setMaxWidth(60);
		    Label stdiv = new Label(" - ");
	
			return new HBox(t1, stdiv, t2);
		}
}

		
