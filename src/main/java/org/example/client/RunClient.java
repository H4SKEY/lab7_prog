package org.example.client;

import org.example.util.CollectionManager;
import org.example.util.CommandManager;

import java.util.Scanner;

public class RunClient {
    private static final String PORT = "LAB6_PORT";

    public static void main(String[] args) {
        String portName = System.getenv(PORT);
        if (portName == null) {
            System.out.println("Не указана переменная окружения LAB6_PORT");
            System.exit(1);
        }

        try {
            int port = Integer.parseInt(portName);
            Scanner scanner = new Scanner(System.in);
            CollectionManager collectionManager = new CollectionManager();
            CommandManager commandManager = new CommandManager(collectionManager);
            Client client = new Client(port, commandManager, scanner);
            client.run();
        } catch (NumberFormatException e) {
            System.out.println("Неверно указан порт");
            System.exit(1);
        }
    }
}
