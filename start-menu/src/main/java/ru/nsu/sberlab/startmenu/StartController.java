package ru.nsu.sberlab.startmenu;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
    protected void onStartButtonClick() {
        messageText.setText("Example");
        //TODO обработка значений port/host, запуск класса Launcher
    }

    @FXML
    private TextField host;

    @FXML
    private TextField port;

    /**
     * Устанавливает текст в поле сообщения.
     *
     * @param message Текст сообщения.
     */
    public void setLabelMessage(String message){
        messageText.setText(message);
    }

    /**
     * Возвращает значение поля "Host".
     *
     * @return Значение поля "Host".
     */
    public String getHostValue(){
        return host.getText();
    }

    /**
     * Возвращает значение поля "Port".
     *
     * @return Значение поля "Port".
     */
    public String getPOrtValue(){
        return port.getText();
    }
}