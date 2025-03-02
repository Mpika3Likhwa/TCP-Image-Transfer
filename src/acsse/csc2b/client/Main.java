package acsse.csc2b.client;

import java.io.File;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application implements EventHandler<ActionEvent> {
	Client client = null;
	Label lblStatus = null;
	TextArea txtStatus = null;
	Button btnList = null;
	Label lblList = null;
	TextArea txtList = null;
	Label lblID = null;
	TextField txtID = null;
	Button btnDown = null;
	ImageView viewer = null;
	Button btnUpload = null;
	ArrayList<String> list = null;
	Stage ps = null;

	@Override
	public void start(Stage primaryStage) {
		ps = primaryStage;
		client = new Client();
		lblStatus = new Label("Program Status");
		txtStatus = new TextArea();
		txtStatus.setDisable(true);
		btnList = new Button("Get List Of Available Images");
		btnList.setOnAction(this);
		lblList = new Label("List of Available Images:");
		txtList = new TextArea();
		lblID = new Label("Enter ID of Image to Retrieve/Download");
		txtID = new TextField();
		btnDown = new Button("Download Image");
		btnDown.setOnAction(this);
		viewer = new ImageView();
		btnUpload = new Button("Upload Image");
		btnUpload.setOnAction(this);

		VBox root = new VBox(10);
		// root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(lblStatus, txtStatus, btnList, lblList, txtList, lblID, txtID, btnDown, viewer,
				btnUpload);
		ScrollPane scrollpane = new ScrollPane(root);
		Scene scene = new Scene(scrollpane, 400, 500);
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void handle(ActionEvent event) {
		// TODO Auto-generated method stub
		if (event.getSource() == btnList) {

			Task<ArrayList<String>> task = client.getList();
			task.setOnSucceeded(e -> {
				list = task.getValue();
				txtStatus.clear();
				for (String line : list) {
					txtList.appendText(line + "\n");
				}
				txtStatus.clear();
				txtStatus.appendText("-List successfully retrieved");
			});
			task.setOnFailed(e -> {
				txtStatus.clear();
				txtStatus.appendText("-Failed To Retrieve List");
			});

		} else if (event.getSource() == btnDown) {
			if (list != null) {
				int ID = Integer.parseInt(txtID.getText());
				if (ID >= 1 && ID <= list.size()) {
					Task<File> task = client.Down(ID);
					task.setOnSucceeded(e -> {
						File Fimage = task.getValue();
						Image image = new Image("file:" + Fimage.getAbsolutePath());
						viewer.setImage(image);
						txtStatus.clear();
						txtStatus.appendText("-Image successfully Retrieved");

					});
				} else {
					txtStatus.clear();
					txtStatus.appendText("-Requested Image/Specified ID Invalid");
				}

			} else {
				txtStatus.clear();
				txtStatus.appendText("-Request List a List of Images first");
			}
		} else if (event.getSource() == btnUpload) {
			if (list != null) {
				FileChooser fc = new FileChooser();
				fc.setInitialDirectory(new File("data/client"));
				File imgFile = fc.showOpenDialog(ps);
				Task<String> task = client.Upload(list.size() + 1, imgFile);
				task.setOnSucceeded(e -> {
					txtStatus.clear();
					txtStatus.appendText("-" + task.getValue());
				});
			} else {
				txtStatus.clear();
				txtStatus.appendText("-Request List a List of Images first");
			}

		}
	}
}
