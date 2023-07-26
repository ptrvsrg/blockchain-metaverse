package ru.nsu.sberlab.startmenu.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

/**
 * Класс ChoiceController отвечает за управление выбором действий на главной странице. Здесь
 * пользователь может выбрать вход в систему или регистрацию нового аккаунта.
 */
public class ChoiceController {

    @FXML
    private AnchorPane anchorPane;

    /**
     * Обработчик события нажатия на кнопку "Вход". Загружает страницу для входа в систему и
     * отображает её на переданном AnchorPane.
     *
     * @throws IOException если возникают проблемы с загрузкой FXML-файла для входа.
     */
    @FXML
    protected void onLogInButtonClick()
        throws IOException {
        Controller.loadNewPage(anchorPane, "/fxml/log-in.fxml");
    }

    /**
     * Обработчик события нажатия на кнопку "Регистрация". Загружает страницу для регистрации нового
     * аккаунта и отображает её на переданном AnchorPane.
     *
     * @throws IOException если возникают проблемы с загрузкой FXML-файла для регистрации.
     */
    @FXML
    protected void onSignUpButtonClick()
        throws IOException {
        Controller.loadNewPage(anchorPane, "/fxml/sign-up.fxml");
    }
}
