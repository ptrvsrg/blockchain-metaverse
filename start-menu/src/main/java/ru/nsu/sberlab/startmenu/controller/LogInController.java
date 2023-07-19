package ru.nsu.sberlab.startmenu.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 Класс LogInController обрабатывает взаимодействие с графическим интерфейсом страницы входа.
 */
public class LogInController {

    @FXML
    private TextField privateKeyTextField;

    @FXML
    private Label messageText;

    @FXML
    private AnchorPane anchorPane;

    /**
     Обработчик события нажатия на кнопку "Log In".
     Проверяет учетную запись пользователя и загружает страницу подключения в случае успеха.
     @throws IOException если возникла ошибка ввода-вывода при загрузке страницы
     */
    public void logInButtonClick() throws IOException {
        //TODO CHECK ACCOUNT
        Controller.loadNewPage(anchorPane, "/fxml/connect.fxml");

    }

    /**
     Обработчик события нажатия на кнопку "Back".
     Возвращает пользователя на страницу выбора (choice).
     @throws IOException если возникла ошибка ввода-вывода при загрузке страницы
     */
    public void backButtonClick() throws IOException {
        Controller.loadNewPage(anchorPane, "/fxml/choice.fxml");
    }
}
