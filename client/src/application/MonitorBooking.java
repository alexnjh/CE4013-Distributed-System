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
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MonitorBooking {

private static ReplyMessage reply;
	public static void showScene(Stage stage, Connection conn, String name)
	{
	    // Instantiate a new Grid Pane
	    GridPane mpane = new GridPane();
	    
	    // Position the pane at the center of the screen, both vertically and horizontally
	    mpane.setAlignment(Pos.CENTER);
	    
	    // Set the horizontal gap between columns
	    mpane.setHgap(10);

	    // Set the vertical gap between rows
	    mpane.setVgap(10);
	    
	    // Add Header
	    Label headerLabel = new Label("Monitor booking");
	    headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
	    mpane.add(headerLabel, 0,0,2,1);
	    GridPane.setHalignment(headerLabel, HPos.CENTER);
	    GridPane.setMargin(headerLabel, new Insets(20, 0,20,0));
	    
	    
	    // Add Fac selection
	    Label facLabel = new Label("Facility : ");
	    mpane.add(facLabel, 0,1);
		ComboBox dropFac = new ComboBox(FXCollections.observableArrayList(Facilities.facilities));
		dropFac.getSelectionModel().selectFirst();//get the first value in the combobox
	    mpane.add(dropFac, 1,1);
	    
	    Label dayLabel = new Label("Day : ");
	    mpane.add(dayLabel, 0,2);
		ComboBox dropDay = new ComboBox(FXCollections.observableArrayList(Day.values()));
		dropDay.getSelectionModel().selectFirst();//get the first value in the combobox
	    mpane.add(dropDay, 1,2);
	    
	    
		
	    TextField sthr = new TextField();
	    TextField stmin = new TextField();
	    Label startLabel = new Label("Start Time : ");
	    mpane.add(startLabel, 0,3);
	    mpane.add(createHrPane(sthr,stmin), 1,3);
	    
		
	    TextField enhr = new TextField();
	    TextField enmin = new TextField();
	    Label endLabel = new Label("End Time : ");
	    mpane.add(endLabel, 0,4);
	    mpane.add(createHrPane(enhr,enmin), 1,4);
		
		
	    Button submit = new Button("Create");
	    Button cancel = new Button("Cancel");
	    HBox.setMargin(submit, new Insets(0, 10, 0, 0));
	    mpane.add(new HBox(submit, cancel), 1,5);
		
	    submit.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	
		    	// Get all required values
				int sthrs, stmins, endhrs, endmins ;
				Day day;
				String fac;
				
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				
				fac = (String) dropFac.getValue();
				day = (Day) dropDay.getValue();

				
				if (Helper.isNumeric(sthr.getText()) &&
					Helper.isNumeric(stmin.getText()) &&
					Helper.isNumeric(enhr.getText()) &&
					Helper.isNumeric(enmin.getText())) {
					
					
					sthrs = Integer.parseInt(sthr.getText());
					stmins = Integer.parseInt(stmin.getText());
					endhrs = Integer.parseInt(enhr.getText());
					endmins = Integer.parseInt(enmin.getText());
					
					
					//end hrs cannot be smaller than start hrs
					if((endhrs-sthrs)<0) {
						alert.setContentText("Invalid monitoring duration");
						alert.showAndWait();
						return;
					
					}else if(endhrs == sthrs) {
						//end time must be bigger than the start minute
						if(!(endmins > stmins))	{							
							alert.setContentText("Invalid monitoring duration");
							alert.showAndWait();
							return;
						}
					}
					
					
					ProgressForm pForm = new ProgressForm();
					
		            // Send message to server and wait for reply
		            Task<Void> task = new Task<Void>() {
		                @Override
		                public Void call() throws InterruptedException {
		                	
		                	// Generate bytes
							Date stdate = new Date(day,sthrs,stmins);
							Date enddate = new Date(day,endhrs,endmins);
							
							// Create booking request
							MonitorRequest req = new MonitorRequest(name,fac,stdate,enddate);
		                	
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
		            		// Else show the users and the monitoring people
							
		            	}
		            	MenuScene.showScene(stage, conn, name);
		            });
		            
		            
		            Thread thread = new Thread(task);
		            thread.start();
					
//					Date stdate = new Date(day,sthrs,stmins);
//					Date enddate = new Date(day,endhrs,endmins);
					
					//booker name length (1 byte) | booker name (x bytes) | start date/time (7 bytes) | end date/time (7 bytes) | facility name length (1 byte) | facility name (x bytes)
					
					
					
				}else {
					alert.setContentText("Please check that all inputs are entered correctly and no inputs are left blank!");
					alert.showAndWait();
				}
						
		    }
		});
		
	    cancel.setOnAction(e -> MenuScene.showScene(stage, conn, name));
		Scene scene = new Scene(mpane,400,400); 
	    stage.setScene(scene);
	    stage.show();
		
		
	}
	

    public static class ProgressForm {
        private final Stage dialogStage;
        private final ProgressBar pb = new ProgressBar();
        private final ProgressIndicator pin = new ProgressIndicator();

        public ProgressForm() {
            dialogStage = new Stage();
            dialogStage.initStyle(StageStyle.UTILITY);
            dialogStage.setResizable(false);
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // PROGRESS BAR
            final Label label = new Label();
            label.setText("alerto");

            pb.setProgress(-1F);
            pin.setProgress(-1F);

            final HBox hb = new HBox();
            hb.setSpacing(5);
            hb.setAlignment(Pos.CENTER);
            hb.getChildren().addAll(pb, pin);

            Scene scene = new Scene(hb);
            dialogStage.setScene(scene);
        }

        public void activateProgressBar(final Task<?> task)  {
            pb.progressProperty().bind(task.progressProperty());
            pin.progressProperty().bind(task.progressProperty());
            dialogStage.show();
        }

        public Stage getDialogStage() {
            return dialogStage;
        }
    }
	
	private static HBox createHrPane(TextField t1, TextField t2) {
		
	    t1.setMaxWidth(60);
	    t2.setMaxWidth(60);
	    Label stdiv = new Label(" - ");

		return new HBox(t1, stdiv, t2);
	}
	

}
