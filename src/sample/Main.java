package sample;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application implements EventHandler<ActionEvent> {

    private TextField txt;
    private TextArea txtOutput;
    private Button button;
    private BackgroundTask backgroundTask;

    private final String TEXT_COUNT_DOWN = "Count down!";
    private final String TEXT_CANCEL = "Cancel!";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simple GUI example");

        BorderPane pane = new BorderPane();

        txt = new TextField("3");
        button = new Button(TEXT_COUNT_DOWN);
        txtOutput = new TextArea();
        button.addEventHandler(ActionEvent.ACTION, this);

        pane.setTop(txt);
        pane.setCenter(txtOutput);
        pane.setBottom(button);

        Scene scene = new Scene(pane,400,400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void handle(ActionEvent event) {
        if(button.getText().equals(TEXT_COUNT_DOWN)) {
            int value ;
            try {
                value = Integer.parseInt(txt.getText());
                if (value <= 0) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error encountered");
                    alert.setHeaderText("Invalid value");
                    alert.setContentText("Please enter a number greater than zero");
                    alert.showAndWait();
                    return;
                }

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error encountered");
                alert.setHeaderText("Invalid value");
                alert.setContentText("Please enter a valid integer");
                alert.showAndWait();
                return;
            }

            this.backgroundTask = new BackgroundTask(value);
            Thread thread = new Thread(this.backgroundTask);
            thread.start();

            backgroundTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    txtOutput.appendText("Finished!\n ");
                    button.setText(TEXT_COUNT_DOWN);
                }
            });

            backgroundTask.messageProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    txtOutput.appendText(newValue + System.lineSeparator());
                }
            });

            backgroundTask.setOnCancelled(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    txtOutput.appendText("Cancelled! ");
                }
            });

            button.setText(TEXT_CANCEL);
        } else {
            this.backgroundTask.cancel();
            button.setText(TEXT_COUNT_DOWN);
        }

        // -- time intensive start
//        try {
//            String cmd = "for (( i = "+value+" ; $i > 0; i=i-1)) ; do echo $i ; sleep 1; done";
//            ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
//
//            Process process = builder.start();
//
//            InputStream out = process.getInputStream();
//            BufferedReader stdout = new BufferedReader(new InputStreamReader(out));
//
//            String line = null;
//            while ((line = stdout.readLine()) != null ) {
//                txtOutput.appendText(line + System.lineSeparator());
//            }
//
//            txtOutput.appendText("Finished! ");
//
//        } catch(Exception ex) {
//            ex.printStackTrace();
//        }
        // -- time intensive end
    }

    public static void main(String[] args) {
        launch(args);
    }

}
