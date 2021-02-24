package application;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.geometry.*;

public class Message {
	private String errorMessage;
	
	Message(String s) {
		this.errorMessage = s;
	}
	
	/*public static byte[] ErrorMessage() {
		return message;
	}*/
	public static void display(String title, String message) {
		Stage errorWin = new Stage();
		errorWin.initModality(Modality.APPLICATION_MODAL);
		errorWin.setTitle(title);
		errorWin.setMinWidth(300);
		errorWin.setMinHeight(250);
		
		Label label = new Label();
		label.setText(message);
		Button closeButton = new Button("Close the window");
		closeButton.setOnAction(e ->errorWin.close());
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, closeButton);		
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		errorWin.setScene(scene);
		errorWin.showAndWait();
		
	}
	
	//haven't display the confirmation id
	public static void viewDis(String title, String message, String name ,String sTime, String eTime) {
		
		Stage vBook = new Stage();
		vBook.initModality(Modality.APPLICATION_MODAL);
		vBook.setTitle(title);
		vBook.setMinWidth(400);
		vBook.setMinHeight(500);
		
		Label label = new Label();
		label.setText(message);
		
		Label fac = new Label();
		label.setText(name);
		
		//time
		Label sT = new Label("Start Time: ");
		Label startTime =new Label(sTime);
		
		Label eT = new Label("End Time: "); 
		Label endTime = new Label(eTime);
		
		Button closeButton = new Button("Close the window");
		closeButton.setOnAction(e ->vBook.close());
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, fac ,sT, startTime, eT, endTime, closeButton);
		
		
		
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		vBook.setScene(scene);
		vBook.showAndWait();
	}
	
	
	
	
public static void viewMod(String title, String id, String name, String sTime, String eTime) {
		
		Stage vBook = new Stage();
		vBook.initModality(Modality.APPLICATION_MODAL);
		vBook.setTitle(title);
		vBook.setMinWidth(400);
		vBook.setMinHeight(300);
		
		Label label = new Label();
		label.setText(id);
		
		Label facName = new Label();
		facName.setText(name);
		
		//time
		Label sT = new Label("Start Time: ");
		Label startTime =new Label(sTime);
		
		Label eT = new Label("End Time: "); 
		Label endTime = new Label(eTime);
		
		Button closeButton = new Button("Close the window");
		closeButton.setOnAction(e ->vBook.close());
		
		VBox layout = new VBox(10);
		layout.getChildren().addAll(label, facName, sT, startTime, eT, endTime, closeButton);
		layout.setSpacing(10);
		
		
		layout.setAlignment(Pos.CENTER);
		
		Scene scene = new Scene(layout);
		vBook.setScene(scene);
		vBook.showAndWait();
	}
	
	
	
}

