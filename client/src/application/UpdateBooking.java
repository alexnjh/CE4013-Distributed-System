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
import javafx.geometry.Orientation;
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
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class UpdateBooking {
	
	private static ReplyMessage reply;
	//modify by offset , postpone or advanced the timing, bring forward/backward for the timing
	public static void showScene(Stage stage, Connection conn, String name, int invocation)
	{
		GridPane updateBook = new GridPane();
		
	    // Position the pane at the center of the screen, both vertically and horizontally
		updateBook.setAlignment(Pos.CENTER);
	    
	    // Set the horizontal gap between columns
		updateBook.setHgap(10);

	    // Set the vertical gap between rows
		updateBook.setVgap(10);
		
		
		Label headerLabel = new Label("Advanced/Postpone Booking");
	    headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
	    updateBook.add(headerLabel, 0,0,2,1);
	    GridPane.setHalignment(headerLabel, HPos.CENTER);
	    GridPane.setMargin(headerLabel, new Insets(20, 0,20,0));
		
	    // ID Input
		Label conId = new Label("Enter ID: ");
		TextField id = new TextField();
		updateBook.add(conId, 0,1);
		updateBook.add(id, 1,1);
		
		//time the user wants to increase or decrease
		Label opMode = new Label("Operation: ");
		String op[]= {"BringForward","Postpone"};
		ComboBox dif = new ComboBox(FXCollections.observableArrayList(op));
		dif.getSelectionModel().selectFirst();
		updateBook.add(opMode, 0,2);
		updateBook.add(dif, 1,2);
		
		// Offset time
		TextField hr = new TextField("0");
		TextField min = new TextField("0");
		Label startLabel = new Label("Offset Time : ");
		updateBook.add(startLabel, 0,3);
		updateBook.add(createHrPane(hr,min), 1,3);
		
		Button updateBut = new Button("Submit");
		Button cancel = new Button("Cancel");
		
		updateBook.setAlignment(Pos.CENTER);
	    HBox.setMargin(updateBut, new Insets(0, 10, 0, 0));
		updateBook.add(new HBox(updateBut, cancel), 1,4);
		
		
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
			//if(id.getText().isEmpty() && Helper.isNumeric(hr.getText()) && Helper.isNumeric(min.getText())) {
			if(!id.getText().isEmpty() &&Helper.isNumeric(hr.getText()) && Helper.isNumeric(min.getText())) {							
				//sent the request to the server with the id
	    		if(dif.getValue().toString().equalsIgnoreCase("postpone")) {
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
	                	
	        			
						// Create booking offset request
			UpdateRequest req = new UpdateRequest(conID, offset);
	                	
	                	updateProgress(1, 10);
	                	try {
	                		System.out.println(req.Marshal(invocation));
							System.out.println("updating....");
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
	                    alert2.setTitle("Update Booking Request");
	                    alert2.setHeaderText(null);
	                    String replyId = new String(reply.getPayload(), StandardCharsets.UTF_8);
	                    alert2.setContentText("Booking updated succesfully!\n\nRef: " + replyId);
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
		
		Scene scene = new Scene(updateBook,400,400); 
	    stage.setScene(scene);
	    stage.show();
	       
	
	}
	private static HBox createHrPane(TextField t1, TextField t2) {
			
		    t1.setMaxWidth(40);
		    t2.setMaxWidth(40);
		    Label stdiv = new Label(" hr ");
		    Label endiv = new Label(" min ");
	
			return new HBox(t1, stdiv, t2, endiv);
	}
	
	public static void showUpdateMenu(Stage stage, Connection conn, String name, int invocation) {
		//Stage stage, Connection conn, String name
		TilePane menuUpdate = new TilePane();
		menuUpdate.setAlignment(Pos.CENTER);
		menuUpdate.setVgap(8);// set vertical gap
		menuUpdate.setHgap(10);
		menuUpdate.setOrientation(Orientation.VERTICAL);
		Button duration = new Button("Modify Duration of Booking");
		Button offset  = new Button("Advance/Postpone Booking");
		Button cancel = new Button("Cancel");
		
		duration.setOnAction(e-> UpdateBookingDuration.showScene(stage, conn, name, invocation));
		offset.setOnAction(e-> UpdateBooking.showScene(stage, conn, name, invocation));
		cancel.setOnAction(e -> MenuScene.showScene(stage, conn, name));
		menuUpdate.getChildren().addAll(duration, offset, cancel);
		
		Scene scene = new Scene(menuUpdate,400,400); 
	    stage.setScene(scene);
	    stage.show();
	}
}

		
