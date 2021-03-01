package application;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javafx.collections.FXCollections;
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
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ViewBooking {
	private static ReplyMessage reply;
	
	
	public static void showScene(Stage stage, Connection conn, String name){
		GridPane vBook = new GridPane();
		vBook.setPadding(new Insets(10, 10, 10, 10));
		vBook.setVgap(8);// set vertical gap
		vBook.setHgap(10);
		Label conId = new Label("Enter ConfirmationID: ");
		GridPane.setConstraints(conId, 0, 0);

		// text field
		TextField id = new TextField();
		GridPane.setConstraints(id, 1, 0);
		
		Button vbutton = new Button("Checking");
		Button cancel = new Button("Cancel");
		
		HBox.setMargin(conId, new Insets(0, 10, 0, 0));
		vBook.add(new HBox(conId, id), 0,4);
		
		HBox.setMargin(vbutton, new Insets(0, 10, 0, 0));
	    vBook.add(new HBox(vbutton, cancel), 0, 5);
		
		vBook.setAlignment(Pos.CENTER);
		
	    vbutton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	
		    	String conID;
		    	
		    	
		    	Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				
				conID = (String)id.getText();
				
				if(id.getText().isEmpty()) {
					
					alert.setContentText("Invalid Confirmation ID");
					alert.showAndWait();
		    }else {
		    	//sent the request to the server with the id
		    	
		    	
		    	
		    	
		    	if (reply.getType().equals("Error")) {
					Alert alert2 = new Alert(AlertType.ERROR);
					alert2.setTitle("Server reply");
					alert2.setHeaderText(null);
					alert2.setContentText(new String(reply.getPayload(), StandardCharsets.UTF_8));
					alert2.showAndWait();		
            	}else if (reply.getType().equals("Confirm")){
            		// Else show the confirmation ID and the booking they made
//       			Alert alert2 = new Alert(AlertType.INFORMATION);
//					alert2.setTitle("Booking Confirmation ID");
//					alert2.setHeaderText(null);
//					alert2.setContentText(new String(reply.getPayload(), StandardCharsets.UTF_8));
//					alert2.showAndWait();	
            		//need to unmarshall the things and show
            		viewBook("View Booking", "conID", "Room", "sTime", "eTime");
					
            	}
		    	
		    }
				
		   }
	 });
		    	
		
		cancel.setOnAction(e -> MenuScene.showScene(stage, conn, name));
		
		Scene scene = new Scene(vBook,400,400); 
	    stage.setScene(scene);
	    stage.show();
	       
	
	}
public static void viewBook(String title, String id, String name, String sTime, String eTime) {
		
		Stage vBook = new Stage();
		vBook.initModality(Modality.APPLICATION_MODAL);
		vBook.setTitle(title);
		vBook.setMinWidth(400);
		vBook.setMinHeight(300);
		
		
		Label label = new Label();
		label.setText(id);
		
		Label label2 = new Label("ID: ");
		Label fac = new Label("Facility Booked:");
		
		Label facName = new Label();
		facName.setText(name);
		
		//time
		Label sT = new Label("Start Time: ");
		Label startTime =new Label(sTime);
		
		Label eT = new Label("End Time: "); 
		Label endTime = new Label(eTime);
		
		Button closeButton = new Button("Close the window");
		closeButton.setOnAction(e ->vBook.close());
		
		GridPane layout = new GridPane();
		layout.setMinSize(400, 200); 
		layout.setPadding(new Insets(10, 10, 10, 10)); 
		layout.setVgap(5); 
	    layout.setHgap(5);
	    
	    Label headerLabel = new Label("Booking:");
		headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		layout.add(headerLabel, 0,0,2,1);
		GridPane.setHalignment(headerLabel, HPos.CENTER);
		GridPane.setMargin(headerLabel, new Insets(20, 0,20,0));
		
	    
		//addAll(label, facName, sT, startTime, eT, endTime, closeButton);
		
		//Button modify= new Button("Modify");
		Button close = new Button("Close");
		layout.setAlignment(Pos.CENTER);
		
		HBox.setMargin(label2, new Insets(0, 10, 0, 0));
		layout.add(new HBox(label2, label), 0,1);
		
		HBox.setMargin(fac, new Insets(0, 10, 0, 0));
		layout.add(new HBox(fac, facName), 0,2);
		
		HBox.setMargin(sT, new Insets(0, 10, 0, 0));
		layout.add(new HBox(sT, startTime), 0,3);
		
		HBox.setMargin(eT, new Insets(0, 10, 0, 0));
		layout.add(new HBox(eT, endTime), 0,4);
		
		//HBox.setMargin(modify, new Insets(0, 10, 0, 0));
		layout.add(close, 0,5);
		
		
		close.setOnAction(e->vBook.close());
		
		Scene scene = new Scene(layout);
		vBook.setScene(scene);
		vBook.showAndWait();
	}
}
