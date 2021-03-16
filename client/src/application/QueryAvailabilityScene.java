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
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class QueryAvailabilityScene {
	private static ReplyMessage reply;
		
	public static void showScene(Stage stage, Connection conn, String name, int invocation)
	{
	    // Instantiate a new Grid Pane
	    GridPane pane = new GridPane();
	    
	    // Position the pane at the center of the screen, both vertically and horizontally
	    pane.setAlignment(Pos.CENTER);
	    
	    // Set the horizontal gap between columns
	    pane.setHgap(10);

	    // Set the vertical gap between rows
	    pane.setVgap(10);
	    
	    // Query Availability
	    Label headerLabel = new Label("Query availibility dates");
	    headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
	    pane.add(headerLabel, 0,0,2,1);
	    GridPane.setHalignment(headerLabel, HPos.CENTER);
	    GridPane.setMargin(headerLabel, new Insets(20, 0,20,0));
	    
	    // Add Fac selection
	    Label facLabel = new Label("Facility : ");
	    pane.add(facLabel, 0,1);
		ComboBox dropFac = new ComboBox(FXCollections.observableArrayList(Facilities.facilities));
		dropFac.getSelectionModel().selectFirst();//get the first value in the combobox
		dropFac.setEditable(true);
	    pane.add(dropFac, 1,1);
	    
	    // Day selection
	    
        CheckBox m = new CheckBox("Monday");
        CheckBox t = new CheckBox("Tuesday");
        CheckBox w = new CheckBox("Wednesday");
        CheckBox th = new CheckBox("Thursday");
        CheckBox f = new CheckBox("Friday");
        CheckBox sat = new CheckBox("Saturday");
        CheckBox sun = new CheckBox("Sunday");
        
	    pane.add(m, 1,2);
	    pane.add(t, 1,3);
	    pane.add(w, 1,4);
	    pane.add(th, 1,5);
	    pane.add(f, 1,6);
	    pane.add(sat, 1,7);
	    pane.add(sun, 1,8);
	    
	    
	    Button submit = new Button("Submit");
	    Button cancel = new Button("Cancel");
	    HBox.setMargin(submit, new Insets(0, 10, 0, 0));
	    pane.add(new HBox(submit, cancel), 1,9);
	    
	    
	    submit.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	
		    	// Get all required values
				String fac;
				
            	boolean[] datArr = new boolean[] {false,false,false,false,false,false,false};
            	
            	// Check which day to query
				if (m.isSelected()) {
					datArr[0] = true;
				}
				
				if (t.isSelected()) {
					datArr[1] = true;
				}
				
				if (w.isSelected()) {
					datArr[2] = true;
				}
				
				if (th.isSelected()) {
					datArr[3] = true;
				}
				
				if (f.isSelected()) {
					datArr[4] = true;
				}
				
				if (sat.isSelected()) {
					datArr[5] = true;
				}
				
				if (sun.isSelected()) {
					datArr[6] = true;
				}
				
				if (Helper.areAllFalse(datArr)) {
					Alert alert2 = new Alert(AlertType.ERROR);
					alert2.setTitle("Error");
					alert2.setHeaderText(null);
					alert2.setContentText("Please select atleast one day.");
					alert2.showAndWait();	
					return;
				}
				
				
				fac = (String) dropFac.getValue();
					
				ProgressForm pForm = new ProgressForm();
				
	            // Send message to server and wait for reply
	            Task<Void> task = new Task<Void>() {
	                @Override
	                public Void call() throws InterruptedException {
	                	
						// Create query request
						QueryRequest req;
						try {
							req = new QueryRequest(datArr,fac);
							
							
		                	updateProgress(1, 10);
		                	try {
								reply = conn.sendMessage(req.Marshal(invocation));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								System.out.println(e.toString());
							}
							
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	                	
	                    updateProgress(10, 10);
	                    return null;
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
		            	}else if (reply.getType().equals("Availability")){
		            		
		            		System.out.println(Helper.bytesToHex(reply.getPayload()));
		            		
		            		AvailabilityReply a = new AvailabilityReply(reply.getPayload());
		            		
		                    
		                    a.print();
		                    
		                    HBox box = new HBox();
		                    
		            	    for(Day b : Day.values()) {
		            	    	if (a.getDateRanges(b) != null  && a.getDateRanges(b).length > 0) {
		            	    		TitledPane tempPane = new TitledPane(b.name() , generateList(a.getDateRanges(b)));
		            	    		tempPane.setCollapsible(false);
		            	    		box.getChildren().add(tempPane);
		            	    	}
		            	    }
		            		// Else show the confirmation ID
							Alert alert2 = new Alert(AlertType.INFORMATION);
							alert2.setTitle("Available Dates");
							alert2.getDialogPane().setContent(box);
							alert2.setHeaderText(null);
							alert2.showAndWait();	
		            	}
		            });
		            
		            
		            Thread thread = new Thread(task);
		            thread.start();
		    }
	    });
	    
	    cancel.setOnAction(e -> MenuScene.showScene(stage, conn, name));
		Scene scene = new Scene(pane,400,400); 
	    stage.setScene(scene);
	    stage.show();
	    
	}
	
	public static Label generateList(DateRange[] d) {
		String temp = "";

	    for(DateRange b : d) {
	    	temp = temp+b.toString()+'\n';	
	    }
	    
	    System.out.println(temp);
	    
        Label label = new Label(temp);
	    
		return label;
	}
}
	    
