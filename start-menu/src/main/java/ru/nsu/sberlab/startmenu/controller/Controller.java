package ru.nsu.sberlab.startmenu.controller;

import java.io.IOException;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

/**
 * Класс Controller содержит статический метод для загрузки новой страницы с использованием FXML.
 */
public class Controller {

    /**
     * Загружает новую страницу FXML и устанавливает ее в качестве корневого элемента сцены.
     *
     * @param anchorPane элемент AnchorPane, на котором будет отображаться новая страница
     * @param fxmlName   имя файла FXML, представляющего новую страницу
     * @throws IOException если возникла ошибка ввода-вывода при загрузке файла FXML
     */
    public static void loadNewPage(final AnchorPane anchorPane, final String fxmlName)
        throws IOException {
        Parent root = FXMLLoader.load(
            Objects.requireNonNull(Controller.class.getResource(fxmlName)));
        anchorPane.getScene()
                  .setRoot(root);
    }
}
