package application;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;

public class Main extends Application {

	Stage window;
	
	Connection connect;
	

	@Override
	public void start(Stage primaryStage) throws UnknownHostException, MalformedURLException{
		window = primaryStage;
		primaryStage.setResizable(false);;
		primaryStage.setTitle("Client Interface");

	
		ConnectionScene.showScene(window);


	}

	public static void main(String[] args) {
		launch(args);
	}

}
