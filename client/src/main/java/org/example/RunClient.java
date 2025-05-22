package org.example;

import java.util.Scanner;

public class RunClient {
    private static final String PORT = "LAB7_PORT";

    public static void main(String[] args) {
        String portName = System.getenv(PORT);
        if (portName == null) {
            System.out.println("Не указана переменная окружения LAB7_PORT");
            System.exit(1);
        }

        try {
            int port = Integer.parseInt(portName);
            Scanner scanner = new Scanner(System.in);
            Client client = new Client(port, scanner);
            client.run();
        } catch (NumberFormatException e) {
            System.out.println("Неверно указан порт");
            System.exit(1);
        }
    }
}
