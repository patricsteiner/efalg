import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NonoMain extends Application {
	
	public static void main(String[] args) throws FileNotFoundException {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Nonogram nonogram = new Nonogram(new FileInputStream(new File("14x22.txt")));
		NonoPane nonoPane = new NonoPane(nonogram);
		nonoPane.setPrefWidth(700);
		nonoPane.setPrefHeight(700);
		Scene s = new Scene(nonoPane);
		primaryStage.setScene(s);
		nonoPane.setVisible(true);
		primaryStage.show();		
	}

}
