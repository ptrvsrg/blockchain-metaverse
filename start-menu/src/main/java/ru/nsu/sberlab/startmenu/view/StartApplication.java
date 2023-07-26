package ru.nsu.sberlab.startmenu.view;

import java.io.IOException;
import java.util.Objects;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Главный класс приложения для запуска JavaFX.
 */
public class StartApplication
    extends Application {

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
    public void start(final Stage stage)
        throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
            StartApplication.class.getResource("/fxml/choice.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.getIcons()
             .add(new Image(Objects.requireNonNull(
                 StartApplication.class.getResourceAsStream("/image/icon.png"))));

        stage.show();
    }

    public void launchApplication() {
        launch();
    }
}
