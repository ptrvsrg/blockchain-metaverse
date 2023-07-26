package ru.nsu.sberlab.startmenu.db;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;

/**
 * Класс DBHandler предоставляет методы для работы с базой данных, хранящей информацию о сетевых
 * подключениях.
 */
@Log4j2
public class DBHandler {

    private static final String SQL_TABLE_URL = "jdbc:sqlite:./login.sqlite";
    private static final String SQL_CREATE_WIF_TABLE = "CREATE TABLE IF NOT EXISTS wif (wif STRING NOT NULL UNIQUE);";
    private static final String SQL_CREATE_HOST_TABLE = "CREATE TABLE IF NOT EXISTS host (host STRING NOT NULL UNIQUE);";
    private static final String SQL_CREATE_PORT_TABLE = "CREATE TABLE IF NOT EXISTS port (port INT NOT NULL UNIQUE);";
    private static final String SQL_INSERT_WIF = "INSERT OR IGNORE INTO wif (wif) VALUES (?);";
    private static final String SQL_INSERT_HOST = "INSERT OR IGNORE INTO host (host) VALUES (?);";
    private static final String SQL_INSERT_PORT = "INSERT OR IGNORE INTO port (port) VALUES (?);";
    private static final String SQL_GET_ALL_WIFS = "SELECT wif FROM wif;";
    private static final String SQL_GET_ALL_HOSTS = "SELECT host FROM host;";
    private static final String SQL_GET_ALL_PORTS = "SELECT port FROM port;";

    /**
     * Создает таблицы в базе данных для хранения сетевых данных, если они еще не существуют. Если
     * таблицы уже существуют, метод не выполняет никаких действий.
     */
    public static void createTable() {
        try (Connection connection = DriverManager.getConnection(SQL_TABLE_URL);
             Statement statement = connection.createStatement()) {

            statement.execute(SQL_CREATE_WIF_TABLE);
            statement.execute(SQL_CREATE_HOST_TABLE);
            statement.execute(SQL_CREATE_PORT_TABLE);
        } catch (Exception e) {
            log.catching(Level.ERROR, e);
        }
    }

    /**
     * Вставляет информацию о сетевом подключении в соответствующие таблицы базы данных. Если
     * переданные значения уже существуют в таблицах, они игнорируются.
     *
     * @param wif  Строковое значение WIF (Wallet Import Format), которое будет добавлено в таблицу
     *             'wif'.
     * @param host IP-адрес или хост, который будет добавлен в таблицу 'host'.
     * @param port Порт, который будет добавлен в таблицу 'port'.
     */
    public static void insertConnectionData(String wif, InetAddress host, int port) {
        try (Connection connection = DriverManager.getConnection(SQL_TABLE_URL);
             PreparedStatement preparedStatementWif = connection.prepareStatement(SQL_INSERT_WIF);
             PreparedStatement preparedStatementHost = connection.prepareStatement(SQL_INSERT_HOST);
             PreparedStatement preparedStatementPort = connection.prepareStatement(
                 SQL_INSERT_PORT)) {

            preparedStatementWif.setString(1, wif);
            preparedStatementHost.setString(1, host.getHostAddress());
            preparedStatementPort.setInt(1, port);

            preparedStatementWif.executeUpdate();
            preparedStatementHost.executeUpdate();
            preparedStatementPort.executeUpdate();

        } catch (SQLException e) {
            log.catching(Level.ERROR, e);
        }
    }

    /**
     * Возвращает множество уникальных значений WIF (Wallet Import Format), которые хранятся в
     * таблице 'wif' базы данных.
     *
     * @return Множество уникальных WIF-значений.
     */
    public static Set<String> getAllWif() {
        Set<String> wifSet = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(SQL_TABLE_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SQL_GET_ALL_WIFS)) {

            while (resultSet.next()) {
                String wif = resultSet.getString("wif");
                wifSet.add(wif);
            }
        } catch (SQLException e) {
            log.catching(Level.ERROR, e);
        }

        return wifSet;
    }

    /**
     * Возвращает множество уникальных IP-адресов или хостов, которые хранятся в таблице 'host' базы
     * данных.
     *
     * @return Множество уникальных IP-адресов или хостов.
     */
    public static Set<String> getAllHosts() {
        Set<String> hostSet = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(SQL_TABLE_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SQL_GET_ALL_HOSTS)) {

            while (resultSet.next()) {
                String host = resultSet.getString("host");
                hostSet.add(host);
            }
        } catch (SQLException e) {
            log.catching(Level.ERROR, e);
        }

        return hostSet;
    }

    /**
     * Возвращает множество уникальных портов, которые хранятся в таблице 'port' базы данных.
     *
     * @return Множество уникальных портов в виде строковых значений.
     */
    public static Set<String> getAllPorts() {
        Set<String> portSet = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(SQL_TABLE_URL);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(SQL_GET_ALL_PORTS)) {

            while (resultSet.next()) {
                int port = resultSet.getInt("port");
                portSet.add(Integer.toString(port));
            }
        } catch (SQLException e) {
            log.catching(Level.ERROR, e);
        }

        return portSet;
    }
}
