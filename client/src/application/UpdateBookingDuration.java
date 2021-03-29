package application;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import application.NewBookingScene.ProgressForm;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
//Interface for the Update Booking Duration Service.
public class UpdateBookingDuration {
	
	private static ReplyMessage reply;
	//modify by duration increase or decrease the duration of the booking,
	public static void showScene(Stage stage, Connection conn, String name, int invocation)
	{
		GridPane updateDur = new GridPane();
		
	    // Position the pane at the center of the screen, both vertically and horizontally
		updateDur.setAlignment(Pos.CENTER);
	    
	    // Set the horizontal gap between columns
		updateDur.setHgap(10);

	    // Set the vertical gap between rows
		updateDur.setVgap(10);
		
		Label headerLabel = new Label("Increase/Decrease Duration");
	    headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
	    updateDur.add(headerLabel, 0,0,2,1);
	    GridPane.setHalignment(headerLabel, HPos.CENTER);
	    GridPane.setMargin(headerLabel, new Insets(20, 0,20,0));
	    
	    // ID Input
		Label conId = new Label("Enter ID: ");
		TextField id = new TextField();
		updateDur.add(conId, 0,1);
		updateDur.add(id, 1,1);
		
		
		//time the user wants to increase or decrease
		Label opMode = new Label("Operation: ");
		String op[]= {"Increase","Decrease"};
		ComboBox plusminus = new ComboBox(FXCollections.observableArrayList(op));
		plusminus.getSelectionModel().selectFirst();
		updateDur.add(opMode, 0,2);
		updateDur.add(plusminus, 1,2);
		
		// Offset time
		TextField hr = new TextField("0");
		TextField min = new TextField("0");
		Label startLabel = new Label("Duration : ");
		updateDur.add(startLabel, 0,3);
		updateDur.add(createHrPane(hr,min), 1,3);
		
		
		Button updateTime = new Button("Submit");
		Button cancel = new Button("Cancel");
		
		updateDur.setAlignment(Pos.CENTER);
	    HBox.setMargin(updateTime, new Insets(0, 10, 0, 0));
	    updateDur.add(new HBox(updateTime, cancel), 1,4);
		
	    updateTime.setOnAction(new EventHandler<ActionEvent>() {
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
				
				
				if (hrs > 24 || hrs < 0){
					alert.setContentText("Invalid starting hour value");
					alert.showAndWait();
					return;
				}else if (mins > 59 || mins < 0){
					alert.setContentText("Invalid starting minute value");
					alert.showAndWait();
					return;
				}
				
				
			//if all input is empty			
			if(!id.getText().isEmpty() && Helper.isNumeric(hr.getText()) && Helper.isNumeric(min.getText())) {
										
				//sent the request to the server with the id
	    		if(plusminus.getValue().toString().equalsIgnoreCase("increase")) {
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
	                	
	        			
						// update booking duration request
			UpdateDurationRequest req = new UpdateDurationRequest(conID, offset);
	                	
	                	updateProgress(1, 10);
	                	try {
	                		
							reply = conn.sendMessage(req.Marshal(invocation));
						} catch (Exception e) {
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
	            	
	            	
	            	if (reply == null) {
						Alert alert2 = new Alert(AlertType.ERROR);
						alert2.setTitle("Internal Error");
						alert2.setHeaderText(null);
						alert2.setContentText("Internal error, please try again");
						alert2.showAndWait();
	            	}
	            	
		            	// If reply is an error show the error
		            if (reply.getType().equals("Error")) {
							Alert alert2 = new Alert(AlertType.ERROR);
							alert2.setTitle("Server reply");
							alert2.setHeaderText(null);
							alert2.setContentText(new String(reply.getPayload(), StandardCharsets.UTF_8));
							alert2.showAndWait();		
		            }else if (reply.getType().equals("Confirm")){
		                    Alert alert2 = new Alert(AlertType.INFORMATION);
		                    alert2.setTitle("Update Duration Request");
		                    alert2.setHeaderText(null);
		                    String replyId = new String(reply.getPayload(), StandardCharsets.UTF_8);
		                    alert2.setContentText("Booking duration updated succesfully!\n\nRef: " + replyId);
		                    alert2.showAndWait();
		            }
		            MenuScene.showScene(stage, conn, name);
	            });
	            Thread thread = new Thread(task);
	            thread.start();
		    }else {
		    	
		            alert.setContentText("Invalid Confirmation ID/Time");
					alert.showAndWait();
		    }
				
		   }
	 });
		    	
		cancel.setOnAction(e -> MenuScene.showScene(stage, conn, name));
		
		Scene scene = new Scene(updateDur,400,400); 
	    stage.setScene(scene);
	    stage.show();
	       
	
	}
	//Helper for GUI
	private static HBox createHrPane(TextField t1, TextField t2) {
		
	    t1.setMaxWidth(40);
	    t2.setMaxWidth(40);
	    Label stdiv = new Label(" hr ");
	    Label endiv = new Label(" min ");

		return new HBox(t1, stdiv, t2, endiv);
}
}

		
