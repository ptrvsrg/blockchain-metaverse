package ru.nsu.sberlab.startmenu.controller;

import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.nsu.sberlab.blockchain_interaction.MapInteraction;
import ru.nsu.sberlab.gameintegration.Launcher;
import ru.nsu.sberlab.startmenu.view.StartApplication;

import java.io.IOException;
import java.util.Objects;

import static ru.nsu.sberlab.startmenu.view.StartApplication.TITLE;

/**
 * Класс LogInController обрабатывает взаимодействие с графическим интерфейсом страницы входа.
 */
public class LogInController {

    @FXML
    public TextField hostTextField;

    @FXML
    public TextField portTextField;

    @FXML
    private TextField wifKeyTextField;

    @FXML
    private Label messageText;

    @FXML
    private AnchorPane anchorPane;

    /**
     * Обработчик события нажатия на кнопку "Log In".
     * Проверяет учетную запись пользователя и загружает страницу подключения в случае успеха.
     *
     * @throws IOException если возникла ошибка ввода-вывода при загрузке страницы
     */
    public void logInButtonClick() throws Throwable {

        String host = hostTextField.getText();
        String port = portTextField.getText();

        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();

        /*
        Пример ввода
         launcher.launch(new MapInteraction(
                "http://45.9.24.41:20032",
                Account.fromWIF("L2btC2CKdpBE32hz4qTeLjYsP9dYKWNzYQH4Bmkt8BzRSviNZW1X"),
                new Hash160("2c88a4ff37e4e269e01c14439c7894d7c46c1a7c"),
                new Hash160("c49920e21449a3fb1cd19685644093c034bb576e")
        * */

        try {
            Launcher launcher = new Launcher();
            launcher.launch(new MapInteraction("http://" + host + ":" + port,
                    Account.fromWIF(wifKeyTextField.getText()),
                    new Hash160("2c88a4ff37e4e269e01c14439c7894d7c46c1a7c"),
                    new Hash160("c49920e21449a3fb1cd19685644093c034bb576e")));
        }
        catch (Exception e) {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    LogInController.class.getResource("/fxml/log-in.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.getIcons().add(new Image(Objects.requireNonNull(StartApplication.class
                    .getResourceAsStream("/image/icon.png"))));
            stage.show();
        }
    }


    /**
     * Обработчик события нажатия на кнопку "Back".
     * Возвращает пользователя на страницу выбора (choice).
     *
     * @throws IOException если возникла ошибка ввода-вывода при загрузке страницы
     */
    public void backButtonClick() throws IOException {
        Controller.loadNewPage(anchorPane, "/fxml/choice.fxml");
    }
}
