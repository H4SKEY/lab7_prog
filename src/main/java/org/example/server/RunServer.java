package org.example.server;

import org.example.dataBase.DataBaseConnector;
import org.example.dataBase.DataBaseManager;
import org.example.util.CollectionManager;

import java.sql.SQLException;

public class RunServer {
    private static final String PORT = "LAB7_PORT";
    private static final String USER = "LAB7_USER";
    private static final String PASSWORD = "LAB7_PASSWORD";
    //private static final String URL = "jdbc:postgresql://pg:5432/studs";
    private static final String URL = "jdbc:postgresql://localhost:5432/lab7_prog";

    public static void main(String[] args) {
        String portName = System.getenv(PORT);
        if (portName == null) {
            System.err.println("Не указана переменная окружения LAB7_PORT");
            System.exit(1);
        }

        String user = System.getenv(USER);
        if (user == null) {
            System.err.println("Не указана переменная окружения LAB7_USER");
            System.exit(1);
        }

        String password = System.getenv(PASSWORD);
        if (password == null) {
            System.err.println("Не указана переменная окружения LAB7_PASSWORD");
            System.exit(1);
        }

        try {
            int port = Integer.parseInt(portName);
            CollectionManager collectionManager = new CollectionManager();
            DataBaseConnector connector = new DataBaseConnector(URL, user, password);
            DataBaseManager dataBaseManager = new DataBaseManager(connector.getConnection());
            collectionManager.setDataBaseManager(dataBaseManager);
            Server server = new Server(port, collectionManager);
            server.run();
        } catch (NumberFormatException e) {
            System.err.println("Неверно указан порт");
            System.exit(1);
        } catch (SQLException e) {
            System.err.println("Не удалось подключится к БД " + e.getMessage());
            System.exit(1);
        }
    }
}
