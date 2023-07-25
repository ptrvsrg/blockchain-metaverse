package ru.nsu.sberlab.startmenu.controller;

import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;

import java.net.InetAddress;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import ru.nsu.sberlab.blockchain_interaction.MapInteraction;
import ru.nsu.sberlab.gameintegration.Launcher;
import ru.nsu.sberlab.startmenu.config.ConnectionConfig;
import ru.nsu.sberlab.startmenu.db.DBHandler;
import ru.nsu.sberlab.startmenu.view.StartApplication;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;

import static ru.nsu.sberlab.startmenu.view.StartApplication.TITLE;

/**
 * Класс LogInController обрабатывает взаимодействие с графическим интерфейсом страницы входа.
 */
public class LogInController implements Initializable {

    @FXML
    public TextField hostTextField;

    @FXML
    public TextField portTextField;

    @FXML
    public CheckBox checkBox;

    @FXML
    public ListView listViewWif;

    @FXML
    public ListView listViewHost;

    @FXML
    public ListView listViewPort;

    @FXML
    private TextField wifKeyTextField;

    @FXML
    private Label messageText;

    @FXML
    private AnchorPane anchorPane;

    /**
     * Обработчик события нажатия на кнопку "Log In".
     * Проверяет учетную запись пользователя и загружает страницу подключения в случае успеха.
     *
     * @throws IOException если возникла ошибка ввода-вывода при загрузке страницы
     */
    public void logInButtonClick() throws Throwable {
        Account account;
        try {
            account = Account.fromWIF(wifKeyTextField.getText());
        }
        catch (Exception ex) {
            // TODO: печать ошибки на окне
            return;
        }

        InetAddress host;
        try {
            host = InetAddress.getByName(hostTextField.getText());
        }
        catch (Exception ex) {
            // TODO: печать ошибки на окне
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portTextField.getText());
        }
        catch (Exception ex) {
            // TODO: печать ошибки на окне
            return;
        }

        if (checkBox.isSelected()) {
            DBHandler.insertConnectionData(wifKeyTextField.getText(), host, port);
        }

        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();

        try {
            Launcher launcher = new Launcher();
            launcher.launch(new MapInteraction("http://" + host.getHostAddress() + ":" + port, account,
                    new Hash160(ConnectionConfig.getHash160Map()), new Hash160(ConnectionConfig.getHash160State())));
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            FXMLLoader fxmlLoader = new FXMLLoader(LogInController.class.getResource("/fxml/log-in.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.getIcons().add(new Image(Objects.requireNonNull(StartApplication.class
                    .getResourceAsStream("/image/icon.png"))));
            stage.show();
        }
    }

    /**
     * Обработчик события нажатия на кнопку "Back".
     * Возвращает пользователя на страницу выбора (choice).
     *
     * @throws IOException если возникла ошибка ввода-вывода при загрузке страницы
     */
    public void backButtonClick() throws IOException {
        Controller.loadNewPage(anchorPane, "/fxml/choice.fxml");
    }

    /**
     * Метод инициализации, вызывается после загрузки FXML-файла.
     * Создает таблицу в базе данных, обрабатывает текстовые поля и список предложений.
     * @param url Путь к FXML-файлу (не используется).
     * @param resourceBundle Ресурсные бандлы (не используются).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DBHandler.createTable();
        textFieldHandle(listViewWif, wifKeyTextField, DBHandler.getAllWif());
        textFieldHandle(listViewHost, hostTextField, DBHandler.getAllHosts());
        textFieldHandle(listViewPort, portTextField, DBHandler.getAllPorts());
    }

    /**
     * Обрабатывает текстовые поля и список предложений для автозаполнения.
     * @param listView Список предложений для автозаполнения.
     * @param textField Текстовое поле, которое будет обрабатываться.
     * @param set Набор предложений для автозаполнения.
     */
    private void textFieldHandle(ListView listView, TextField textField, Set<String> set) {

        listView.setVisible(false);

        textField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
            if (!newPropertyValue) {
                listView.setVisible(false);
            }
        });

        textField.setOnMouseClicked(mouseEvent -> {
            listView.getItems().clear();
            for (String suggestion : set) {
                listView.getItems().add(suggestion);
            }
            if (!listView.getItems().isEmpty()) {
                listView.setVisible(true);
            }
        });

        textField.setOnKeyReleased(event -> {
            listView.getItems().clear();
            listView.setVisible(true);
            String inputText = textField.getText().toLowerCase();
            listView.getItems().clear();

            for (String suggestion : set) {
                if (suggestion.toLowerCase().startsWith(inputText)) {
                    listView.getItems().add(suggestion);
                }
            }

            listView.setVisible(!listView.getItems().isEmpty());
        });

        listView.setOnMousePressed(event -> {
            String selectedItem = (String) listView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                textField.setText(selectedItem);
            }
            listView.setVisible(false);
        });
    }
}
