package ru.nsu.sberlab.startmenu.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Главный класс приложения для запуска JavaFX.
 */
public class StartApplication extends Application {

    /**
     * Заголовок главного окна приложения.
     */
    public static final String TITLE = "MyCraft";

    /**
     * Метод, вызываемый при запуске приложения.
     *
     * @param stage Главная сцена приложения.
     * @throws IOException Если возникают проблемы при загрузке FXML-файла.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getClassLoader().getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setFullScreen(true);

        stage.show();
    }

    /**
     * Точка входа в приложение.
     *
     * @param args Аргументы командной строки (не используются).
     */
    public static void main(String[] args) {
        launch();
    }
}
