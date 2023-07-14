package ru.nsu.sberlab.startmenu.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Контроллер, отвечающий за взаимодействие с графическим интерфейсом стартового окна.
 */
public class StartController {

    @FXML
    private Label messageText;

    /**
     * Обработчик события нажатия на кнопку "Start".
     * Устанавливает текст в поле сообщения и выполняет необходимые действия.
     */
    @FXML
    protected void onStartButtonClick(){
        try {
            InetAddress inetAddress = getHost();
        }
        catch (UnknownHostException e) {
            messageText.setText("Invalid host format");
            clearFields();
            return;
        }
        try {
            int port = getPort();
        }
        catch (NumberFormatException e) {
            messageText.setText("The entered port is not a number");
            clearFields();
            return;
        }

        //TODO запуск класса Launcher с параметрами inetAddress, port
    }

    @FXML
    private TextField hostTextField;

    @FXML
    private TextField portTextField;

    /**
     * Получает значение порта из текстового поля.
     *
     * @return значение порта
     */
    private int getPort() throws NumberFormatException {
        String port = portTextField.getText();
        return Integer.parseInt(port);
    }

    /**
     * Получает хост из текстового поля и возвращает объект InetAddress.
     *
     * @return объект InetAddress, представляющий хост
     * @throws UnknownHostException если формат хоста недействителен
     */
    private InetAddress getHost() throws UnknownHostException {
        String host = hostTextField.getText();
        return InetAddress.getByName(host);
    }

    /**
     * Очищает текстовые поля и устанавливает пустой текст в поле сообщения через 5 секунд.
     */
    private void clearFields() {
        portTextField.clear();
        hostTextField.clear();

        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exc) {
                throw new Error("Unexpected interruption", exc);
            }
            Platform.runLater(() -> messageText.setText(""));
        });

        thread.setDaemon(true);
        thread.start();
    }
}