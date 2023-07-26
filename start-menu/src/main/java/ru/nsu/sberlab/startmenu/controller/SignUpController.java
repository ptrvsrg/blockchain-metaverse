package ru.nsu.sberlab.startmenu.controller;

import io.neow3j.wallet.Account;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;

/**
 * Класс SignUpController обрабатывает взаимодействие с графическим интерфейсом страницы регистрации
 * пользователя.
 */
public class SignUpController
    implements Initializable {

    @FXML
    private TextField wifKeyText;

    @FXML
    private Button signUpButton;

    @FXML
    private AnchorPane anchorPane;

    /**
     * Обработчик события нажатия кнопки "Зарегистрироваться". Создает новый аккаунт, генерирует и
     * отображает секретный ключ в формате WIF (Wallet Import Format) и выводит сообщение для
     * сохранения секретного ключа. Кнопка "Зарегистрироваться" становится неактивной после
     * выполнения метода.
     */
    public void signUpButtonClick() {
        Account account = Account.create();
        String key = account.getECKeyPair()
                            .exportAsWIF();
        wifKeyText.setVisible(true);
        signUpButton.setVisible(false);

        wifKeyText.setText(key);

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(key);
        clipboard.setContent(content);
    }

    /**
     * Обработчик события нажатия на кнопку "Back". Возвращает пользователя на страницу выбора
     * (choice).
     *
     * @throws IOException если возникла ошибка ввода-вывода при загрузке страницы
     */
    public void backButtonClick()
        throws IOException {
        Controller.loadNewPage(anchorPane, "/fxml/choice.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        wifKeyText.setVisible(false);
    }
}
