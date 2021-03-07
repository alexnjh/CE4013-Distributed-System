package application;

import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MonitorBooking {

    private static ReplyMessage reply;

    public static void showScene(Stage stage, Connection conn, String name) {
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
        mpane.add(headerLabel, 0, 0, 2, 1);
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setMargin(headerLabel, new Insets(20, 0, 20, 0));


        // Add Fac selection
        Label facLabel = new Label("Facility : ");
        mpane.add(facLabel, 0, 1);
        ComboBox dropFac = new ComboBox(FXCollections.observableArrayList(Facilities.facilities));
        dropFac.getSelectionModel().selectFirst();//get the first value in the combobox
        mpane.add(dropFac, 1, 1);

        TextField mhr = new TextField();
        TextField mmin = new TextField();
        TextField msec = new TextField();
        Label durLabel = new Label("Duration : ");
        mpane.add(durLabel, 0, 3);
        mpane.add(createDuration(mhr, mmin, msec), 1, 3);


        Button submit = new Button("Create");
        Button cancel = new Button("Cancel");
        HBox.setMargin(submit, new Insets(0, 10, 0, 0));
        mpane.add(new HBox(submit, cancel), 1, 6);

        submit.setOnAction(e -> {

            // Get all required values
            String fac;

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);

            fac = (String) dropFac.getValue();

            // Calculate seconds
            long seconds = 0;
            boolean hrV = Helper.isNumeric(mhr.getText());
            boolean minV = Helper.isNumeric(mmin.getText());
            boolean secV = Helper.isNumeric(msec.getText());
            if (hrV) {
                int durHr = Integer.parseInt(mhr.getText());
                seconds += (durHr * 3600L); // 1 hour = 3600 seconds
            }

            if (minV) {
                int durMin = Integer.parseInt(mmin.getText());
                seconds += (durMin * 60L); // 1 min = 60 seconds
            }

            if (secV) {
                int durSec = Integer.parseInt(msec.getText());
                seconds += durSec;
            }

            if (Helper.isFalse(hrV, minV, secV)) {
                alert.setContentText("Duration to monitor cannot be blank");
                alert.showAndWait();
                return;

            } else if (seconds <= 0) {
                alert.setContentText("Duration to monitor must be greater than 0 seconds");
                alert.showAndWait();
                return;
            }

            final long submitMonitorDura = seconds;
            System.out.println("Requesting to monitor " + fac + " for " + submitMonitorDura + " seconds");

            ProgressForm pForm = new ProgressForm();

            // Send message to server and wait for reply
            Task<Void> task = new Task<>() {
                @Override
                public Void call() {

                    // Generate bytes

                    // Create booking request
                    MonitorRequest req = new MonitorRequest(fac, submitMonitorDura);

                    updateProgress(1, 10);
                    try {
                        reply = conn.sendMessage(req.Marshal());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        System.out.println(e.toString());
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


                System.out.println(reply.getType());

                // If reply is an error show the error
                if (reply.getType().equals("Error")) {
                    Alert alert2 = new Alert(AlertType.ERROR);
                    alert2.setTitle("Server reply");
                    alert2.setHeaderText(null);
                    alert2.setContentText(new String(reply.getPayload(), StandardCharsets.UTF_8));
                    alert2.showAndWait();
                } else if (reply.getType().equals("Confirm")) {
                    // Else show the users and the monitoring people
                    // TODO: Monitor receive
                }
                MenuScene.showScene(stage, conn, name);
            });


            Thread thread = new Thread(task);
            thread.start();

        });

        cancel.setOnAction(e -> MenuScene.showScene(stage, conn, name));
        Scene scene = new Scene(mpane, 400, 400);
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

        public void activateProgressBar(final Task<?> task) {
            pb.progressProperty().bind(task.progressProperty());
            pin.progressProperty().bind(task.progressProperty());
            dialogStage.show();
        }

        public Stage getDialogStage() {
            return dialogStage;
        }
    }

    private static HBox createDuration(TextField t1, TextField t2, TextField t3) {
        t1.setMaxWidth(40);
        t2.setMaxWidth(40);
        t3.setMaxWidth(40);
        return new HBox(t1, new Label(" hr "), t2, new Label(" min "), t3, new Label(" sec"));
    }


}
