package ru.nsu.sberlab.startmenu.controller;

import static ru.nsu.sberlab.startmenu.view.StartApplication.TITLE;

import io.neow3j.types.Hash160;
import io.neow3j.wallet.Account;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import ru.nsu.sberlab.blockchainintegration.MapInteraction;
import ru.nsu.sberlab.gameintegration.Launcher;
import ru.nsu.sberlab.startmenu.config.ConnectionConfig;
import ru.nsu.sberlab.startmenu.db.DBHandler;
import ru.nsu.sberlab.startmenu.view.StartApplication;

/**
 * Класс LogInController обрабатывает взаимодействие с графическим интерфейсом страницы входа.
 */
@Log4j2
public class LogInController
    implements Initializable {

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
    private AnchorPane anchorPane;

    /**
     * Проверяет, что указанный порт находится в допустимом диапазоне от 0 до 65535 (включительно).
     *
     * @param port Порт для проверки.
     * @throws IllegalArgumentException если порт находится за пределами допустимого диапазона (от 0
     *                                  до 65535).
     */
    private static void checkPort(int port) {
        if (port < 0 || port > 0xFFFF) {
            throw new IllegalArgumentException("port out of range:" + port);
        }
    }

    /**
     * Обработчик события нажатия на кнопку "Log In". Проверяет учетную запись пользователя и
     * загружает страницу подключения в случае успеха.
     *
     * @throws IOException если возникла ошибка ввода-вывода при загрузке страницы
     */
    public void logInButtonClick()
        throws Throwable {
        boolean errorFlag = false;

        restoreTextFieldStyle();

        Account account = null;
        InetAddress host = null;
        int port = 0;

        try {
            account = Account.fromWIF(wifKeyTextField.getText());
        } catch (Exception ex) {
            wifKeyTextField.setStyle(
                "-fx-border-color: RED; -fx-border-width: 1; -fx-border-radius: 5;");
            errorFlag = true;
        }

        try {
            if (hostTextField.getText()
                             .isEmpty()) {
                hostTextField.setStyle(
                    "-fx-border-color: RED; -fx-border-width: 1; -fx-border-radius: 5;");
                errorFlag = true;
            }
            host = InetAddress.getByName(hostTextField.getText());
        } catch (Exception ex) {
            hostTextField.setStyle(
                "-fx-border-color: RED; -fx-border-width: 1; -fx-border-radius: 5;");
            errorFlag = true;
        }

        try {
            port = Integer.parseInt(portTextField.getText());
            checkPort(port);
        } catch (Exception ex) {
            portTextField.setStyle(
                "-fx-border-color: RED; -fx-border-width: 1; -fx-border-radius: 5;");
            errorFlag = true;
        }

        if (errorFlag) {
            return;
        }

        if (checkBox.isSelected()) {
            DBHandler.insertConnectionData(wifKeyTextField.getText(), host, port);
        }

        Stage stage = (Stage) anchorPane.getScene()
                                        .getWindow();
        stage.close();

        try {
            Launcher launcher = new Launcher();
            launcher.launch(
                new MapInteraction("http://" + host.getHostAddress() + ":" + port, account,
                                   new Hash160(ConnectionConfig.getHash160Map()),
                                   new Hash160(ConnectionConfig.getHash160State())));
        } catch (Exception e) {
            log.catching(Level.ERROR, e);
            FXMLLoader fxmlLoader = new FXMLLoader(
                LogInController.class.getResource("/fxml/log-in.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            stage.setTitle(TITLE);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.setFullScreen(true);
            stage.getIcons()
                 .add(new Image(Objects.requireNonNull(
                     StartApplication.class.getResourceAsStream("/image/icon.png"))));
            stage.show();
        }
    }

    private void restoreTextFieldStyle() {
        wifKeyTextField.setStyle(
            "-fx-border-color: WHITE; -fx-border-width: 1; -fx-border-radius: 5;");
        hostTextField.setStyle(
            "-fx-border-color: WHITE; -fx-border-width: 1; -fx-border-radius: 5;");
        portTextField.setStyle(
            "-fx-border-color: WHITE; -fx-border-width: 1; -fx-border-radius: 5;");
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

    /**
     * Метод инициализации, вызывается после загрузки FXML-файла. Создает таблицу в базе данных,
     * обрабатывает текстовые поля и список предложений.
     *
     * @param url            Путь к FXML-файлу (не используется).
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
     *
     * @param listView  Список предложений для автозаполнения.
     * @param textField Текстовое поле, которое будет обрабатываться.
     * @param set       Набор предложений для автозаполнения.
     */
    private void textFieldHandle(ListView listView, TextField textField, Set<String> set) {

        listView.setVisible(false);

        textField.focusedProperty()
                 .addListener((arg0, oldPropertyValue, newPropertyValue) -> {
                     if (!newPropertyValue) {
                         listView.setVisible(false);
                     }
                 });

        textField.setOnMouseClicked(mouseEvent -> {
            listView.getItems()
                    .clear();
            for (String suggestion : set) {
                listView.getItems()
                        .add(suggestion);
            }
            if (!listView.getItems()
                         .isEmpty()) {
                listView.setVisible(true);
            }
        });

        textField.setOnKeyReleased(event -> {
            listView.getItems()
                    .clear();
            listView.setVisible(true);
            String inputText = textField.getText()
                                        .toLowerCase();
            listView.getItems()
                    .clear();

            for (String suggestion : set) {
                if (suggestion.toLowerCase()
                              .startsWith(inputText)) {
                    listView.getItems()
                            .add(suggestion);
                }
            }

            listView.setVisible(!listView.getItems()
                                         .isEmpty());
        });

        listView.setOnMousePressed(event -> {
            String selectedItem = (String) listView.getSelectionModel()
                                                   .getSelectedItem();
            if (selectedItem != null) {
                textField.setText(selectedItem);
            }
            listView.setVisible(false);
        });
    }
}
