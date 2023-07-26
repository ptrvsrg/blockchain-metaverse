package ru.nsu.sberlab.startmenu.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс ConnectionConfig предоставляет методы для доступа и получения свойств, связанных с
 * настройками соединения, из файла конфигурации "connection.properties".
 */
public class ConnectionConfig {

    private static final String CONFIG_FILE_NAME = "/config/connection.properties";
    private static Properties properties;

    /**
     * Считывает свойства из файла конфигурации "connection.properties".
     *
     * @throws IOException если возникла ошибка при чтении файла.
     */
    private static void readConfigFile()
        throws IOException {
        InputStream configFile = ConnectionConfig.class.getResourceAsStream(CONFIG_FILE_NAME);
        properties = new Properties();
        properties.load(configFile);
    }

    /**
     * Возвращает свойство "connection.hash160_1" из файла конфигурации. Если свойства еще не были
     * загружены, вызывает метод readConfigFile() для загрузки.
     *
     * @return значение свойства "connection.hash160_1" или null, если свойства не были загружены
     * или свойство отсутствует.
     * @throws IOException если возникла ошибка при чтении файла.
     */
    public static String getHash160Map()
        throws IOException {
        if (properties == null) {
            readConfigFile();
        }

        return properties.getProperty("connection.hash160_map");
    }

    /**
     * Возвращает свойство "connection.hash160_2" из файла конфигурации. Если свойства еще не были
     * загружены, вызывает метод readConfigFile() для загрузки.
     *
     * @return значение свойства "connection.hash160_2" или null, если свойства не были загружены
     * или свойство отсутствует.
     * @throws IOException если возникла ошибка при чтении файла.
     */
    public static String getHash160State()
        throws IOException {
        if (properties == null) {
            readConfigFile();
        }

        return properties.getProperty("connection.hash160_state");
    }
}
