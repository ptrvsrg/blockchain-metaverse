package ru.nsu.sberlab.startmenu.controller;

import io.neow3j.wallet.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 Класс SignUpController обрабатывает взаимодействие с графическим интерфейсом страницы регистрации пользователя.
 */
public class SignUpController {

    @FXML
    public Text wifKeyText;

    @FXML
    public Button signUpButton;

    @FXML
    private Label messageText;

    @FXML
    private AnchorPane anchorPane;

    public void signUpButtonClick() {
        messageText.setText("Save your secret wif key");
        Account account = Account.create();
        String key = account.getECKeyPair().exportAsWIF();
        wifKeyText.setText(key);
        signUpButton.setDisable(true);
    }

    /**
     Обработчик события нажатия на кнопку "Back".
     Возвращает пользователя на страницу выбора (choice).
     @throws IOException если возникла ошибка ввода-вывода при загрузке страницы
     */
    public void backButtonClick() throws IOException {
        Controller.loadNewPage(anchorPane, "/fxml/choice.fxml");
    }

    public void copyTextButtonClick(ActionEvent event) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(wifKeyText.getText());
        clipboard.setContent(content);
        messageText.setText("Copied");
    }
}
