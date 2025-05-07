package org.example.server;

import org.example.util.CollectionManager;

public class RunServer {
    private static final String ENV_VAR_NAME = "LAB5_FILE";
    private static final String PORT = "LAB6_PORT";

    public static void main(String[] args) {
        String fileName = System.getenv(ENV_VAR_NAME);
        if (fileName == null) {
            System.out.println("Не указана переменная окружения LAB5_FILE");
            System.exit(1);
        }

        String portName = System.getenv(PORT);
        if (portName == null) {
            System.out.println("Не указана переменная окружения LAB6_PORT");
            System.exit(1);
        }

        try {
            int port = Integer.parseInt(portName);
            CollectionManager collectionManager = new CollectionManager();
            Server server = new Server(port, collectionManager, fileName);
            server.run();
        } catch (NumberFormatException e) {
            System.out.println("Неверно указан порт");
            System.exit(1);
        }
    }
}
