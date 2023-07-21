package ru.nsu.sberlab.startmenu.controller;

import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import javafx.application.Platform;
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

import java.util.Objects;

import static ru.nsu.sberlab.startmenu.view.StartApplication.TITLE;

/**
 Класс ConnectionController отвечает за управление окном подключения к серверу.
 Позволяет пользователю ввести хост и порт для установки соединения с сервером.
 */
public class ConnectionController {

    /**
    Время, перед тем, как поле для сообщений очистится.
    */
    private static final int TIME_CLEAR = 5000;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField hostTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private Label messageText;

    @FXML
    private TextField mapContractTextField;

    @FXML
    private TextField stateContractTextField;

    @FXML
    private String privateKey;

    /**
     Обработчик события нажатия на кнопку "Enter".
     Проверяет введенные данные хоста и порта, устанавливает текст в поле сообщения
     и выполняет необходимые действия. Если хост или порт некорректны, выводит
     соответствующее сообщение об ошибке и очищает текстовые поля.
     */
    @FXML
    protected void onEnterButtonClick() throws Throwable {

        String inetAddress = getHost();
        String port = getPort();

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
            launcher.launch(new MapInteraction(
                    "http://" + inetAddress + ":" + port,
                    Account.fromWIF(privateKey),
                    new Hash160(mapContractTextField.getText()),
                    new Hash160(stateContractTextField.getText())
            ));
        } catch (Exception e) {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    StartApplication.class.getResource("/fxml/connect.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.getIcons().add(new Image(Objects.requireNonNull(StartApplication.class
                    .getResourceAsStream("/image/icon.png"))));
            stage.show();

            ConnectionController connectionController = fxmlLoader.getController();
            connectionController.setMessageText("Invalid input");
        }

    }

    private String getPort() throws NumberFormatException {
        return portTextField.getText();
    }

    private String getHost() {
        return hostTextField.getText();
    }

    /**
     Очищает текстовые поля и устанавливает пустой текст в поле сообщения через TIME_CLEAR секунд.
     Если значение clearMessage = true, то поле для сообщений также очищается.

     @param clearMessage флаг очистки messageLabel
     */
    public void clearFields(final boolean clearMessage) {
        portTextField.clear();
        hostTextField.clear();

        if (clearMessage) {
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(TIME_CLEAR);
                } catch (InterruptedException exc) {
                    throw new Error("Unexpected interruption", exc);
                }
                Platform.runLater(() -> messageText.setText(""));
            });

            thread.setDaemon(true);
            thread.start();
        }
    }


    /**
     * @param privateKey приватный ключ
     */
    public void setPrivateKey(final String privateKey) {
        this.privateKey = privateKey;
    }

    /**
     * @param text текст, который необходимо установить в поле сообщений
     */
    public void setMessageText(final String text) {
        messageText.setText(text);
    }
}
