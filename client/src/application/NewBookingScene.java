package application;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
//Interface for Adding Booking Service.
public class NewBookingScene {
	
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
	    
	    // Add Header
	    Label headerLabel = new Label("Create new booking");
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
	    
	    Label dayLabel = new Label("Day : ");
	    pane.add(dayLabel, 0,2);
		ComboBox dropDay = new ComboBox(FXCollections.observableArrayList(Day.values()));
		dropDay.getSelectionModel().selectFirst();//get the first value in the combobox
	    pane.add(dropDay, 1,2);
	    
	    
		
	    TextField sthr = new TextField();
	    TextField stmin = new TextField();
	    Label startLabel = new Label("Start Time : ");
	    pane.add(startLabel, 0,3);
	    pane.add(createHrPane(sthr,stmin), 1,3);
	    
		
	    TextField enhr = new TextField();
	    TextField enmin = new TextField();
	    Label endLabel = new Label("End Time : ");
	    pane.add(endLabel, 0,4);
	    pane.add(createHrPane(enhr,enmin), 1,4);
		
		
	    Button submit = new Button("Create");
	    Button cancel = new Button("Cancel");
	    HBox.setMargin(submit, new Insets(0, 10, 0, 0));
	    pane.add(new HBox(submit, cancel), 1,5);
		
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
					
					if (sthrs > 24 || sthrs < 0){
						alert.setContentText("Invalid starting hour value");
						alert.showAndWait();
						return;
					}else if (endhrs > 24 || endhrs < 0){
						alert.setContentText("Invalid ending hour value");
						alert.showAndWait();
						return;
					}else if (stmins > 59 || stmins < 0){
						alert.setContentText("Invalid starting minute value");
						alert.showAndWait();
						return;
					}else if (endmins > 59 || endmins < 0){
						alert.setContentText("Invalid ending minute value");
						alert.showAndWait();
						return;
					}else if (sthrs == 24 && stmins > 0){
						alert.setContentText("Invalid starting time");
						alert.showAndWait();
						return;
					}else if (endhrs == 24 && endmins > 0){
						alert.setContentText("Invalid ending time");
						alert.showAndWait();
						return;
					}
					
					
					//end hrs cannot be smaller than start hrs
					if((endhrs-sthrs)<0) {
						alert.setContentText("Invalid booking duration");
						alert.showAndWait();
						return;
					
					}else if(endhrs == sthrs) {
						//end time must be bigger than the start minute
						if(!(endmins > stmins))	{							
							alert.setContentText("Invalid booking duration");
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
							BookingRequest req = new BookingRequest(name,fac,stdate,enddate);
		                	
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
		            		
		            		
		            		TextArea textArea = new TextArea(new String(reply.getPayload(), StandardCharsets.UTF_8));
		            		textArea.setEditable(false);
		            		textArea.setWrapText(true);
		            		GridPane gridPane = new GridPane();
		            		gridPane.setMaxWidth(Double.MAX_VALUE);
		            		gridPane.add(textArea, 0, 0);
		            		
		            		// Else show the confirmation ID
							Alert alert2 = new Alert(AlertType.INFORMATION);
							alert2.setTitle("Booking Confirmation ID");
							alert2.getDialogPane().setContent(gridPane);
							alert2.setHeaderText(null);
							
							alert2.showAndWait();	
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
		Scene scene = new Scene(pane,400,400); 
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
	//Helper for GUI
	private static HBox createHrPane(TextField t1, TextField t2) {
		
	    t1.setMaxWidth(60);
	    t2.setMaxWidth(60);
	    Label stdiv = new Label(" - ");

		return new HBox(t1, stdiv, t2);
	}
	
}
