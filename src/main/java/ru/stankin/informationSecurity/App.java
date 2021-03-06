package ru.stankin.informationSecurity;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.regex.Pattern;

public class App extends Application {
    private static final String restriction = "(?=.*[A-Za-z])(?=.*[ёЁА-Яа-я])(?=.*[.,!?;:])(?=\\S+$).{0,15}";

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("views/index.fxml")));
        primaryStage.setTitle("information.security");

        Scene scene = new Scene(root, 1080, 720);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
//        System.out.printf(String.valueOf("lд.".matches(restriction)));
        launch(args);
    }
}
