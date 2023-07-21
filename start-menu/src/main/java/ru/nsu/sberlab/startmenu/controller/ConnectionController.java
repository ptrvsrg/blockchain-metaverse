package ru.nsu.sberlab.startmenu.controller;

import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.nsu.sberlab.blockchain_interaction.MapInteraction;
import ru.nsu.sberlab.gameintegration.Launcher;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

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

    /**
     Обработчик события нажатия на кнопку "Enter".
     Проверяет введенные данные хоста и порта, устанавливает текст в поле сообщения
     и выполняет необходимые действия. Если хост или порт некорректны, выводит
     соответствующее сообщение об ошибке и очищает текстовые поля.
     */
    @FXML
    protected void onEnterButtonClick() throws Throwable {

        try {
            InetAddress inetAddress = getHost();
        } catch (UnknownHostException e) {
            messageText.setText("Invalid host format");
            clearFields(true);
            return;
        }
        try {
            int port = getPort();
        } catch (NumberFormatException e) {
            messageText.setText("The entered port is not a number");
            clearFields(true);
            return;
        }

        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();

        Launcher launcher = new Launcher();
        launcher.launch(new MapInteraction(
                "http://45.9.24.41:20032",
                Account.fromWIF("L2btC2CKdpBE32hz4qTeLjYsP9dYKWNzYQH4Bmkt8BzRSviNZW1X"),
                new Hash160("2c88a4ff37e4e269e01c14439c7894d7c46c1a7c"),
                new Hash160("c49920e21449a3fb1cd19685644093c034bb576e")
        ));
    }

    /**
     Получает значение порта из текстового поля.

     @return значение порта
     @throws NumberFormatException если введенное значение не является числом
     */
    private int getPort() throws NumberFormatException {
        String port = portTextField.getText();
        return Integer.parseInt(port);
    }

    /**
     Получает хост из текстового поля и возвращает объект InetAddress.

     @return объект InetAddress, представляющий хост
     @throws UnknownHostException если формат хоста недействителен
     */
    private InetAddress getHost() throws UnknownHostException {
        String host = hostTextField.getText();
        return InetAddress.getByName(host);
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
}
