package ru.nsu.sberlab.startmenu.controller;

import io.neow3j.wallet.Account;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 Класс SignUpController обрабатывает взаимодействие с графическим интерфейсом страницы регистрации пользователя.
 */
public class SignUpController {

    @FXML
    private TextField wifKeyText;

    @FXML
    private Button signUpButton;

    @FXML
    private Label messageText;

    @FXML
    private AnchorPane anchorPane;

    /**
     * Обработчик события нажатия кнопки "Зарегистрироваться".
     * Создает новый аккаунт, генерирует и отображает секретный ключ в формате WIF
     * (Wallet Import Format) и выводит сообщение для сохранения секретного ключа.
     * Кнопка "Зарегистрироваться" становится неактивной после выполнения метода.
     */
    public void signUpButtonClick() {
        messageText.setText("The key was saved in the exchange voucher");
        Account account = Account.create();
        String key = account.getECKeyPair().exportAsWIF();
        wifKeyText.setText(key);
        signUpButton.setDisable(true);

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(key);
        clipboard.setContent(content);
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
