package ru.nsu.sberlab.startmenu.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 Класс SignUpController обрабатывает взаимодействие с графическим интерфейсом страницы регистрации пользователя.
 */
public class SignUpController {

    @FXML
    private TextField publicKeyTextField;

    @FXML
    private TextField privateKeyTextField;

    @FXML
    private Label messageText;

    @FXML
    private AnchorPane anchorPane;

    public void signUpButtonClick() {
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
