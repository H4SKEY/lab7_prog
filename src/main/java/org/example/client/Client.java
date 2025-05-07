package org.example.client;

import org.example.commands.AbstractCommand;
import org.example.dataBase.Hasher;
import org.example.dataBase.User;
import org.example.network.Request;
import org.example.util.CommandManager;
import org.example.util.InputManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
    private final int port;
    private final String serverAddress;
    private final CommandManager commandManager;
    private final InputManager inputManager;
    private final Scanner scanner;
    private User user;

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public Client(int port, CommandManager commandManager, Scanner scanner) {
        this.port = port;
        this.serverAddress = "localhost";
        this.commandManager = commandManager;
        this.inputManager = new InputManager(scanner);
        this.scanner = scanner;
    }

    public User inputUser() {
        System.out.println("Введите логин: ");
        String login = scanner.nextLine();

        System.out.println("Введите пароль: ");
        String password = scanner.nextLine();

        password = Hasher.hashPassword(password);
        User user = new User(login, password);

        return user;
    }

    public void run() {
        user = inputUser();

        while (true) {
            try (SocketChannel socketChannel = SocketChannel.open()) {
                socketChannel.configureBlocking(false);
                socketChannel.connect(new InetSocketAddress(serverAddress, port));
                while (!socketChannel.finishConnect()) {
                    // Ждём установления соединения
                }
                logger.info("Подключение установлено с {}:{}", serverAddress, port);

                while (true) {
                    System.out.print("> ");
                    String input = scanner.nextLine().trim();
                    if (input.isEmpty()) continue;

                    String[] parts = input.split("\\s+", 2);
                    String commandName = parts[0].toLowerCase();
                    String[] commandArgs = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

                    AbstractCommand command = commandManager.getCommands().get(commandName);
                    if (command != null) {
                        if (commandName.equals("execute_script")) {
                            String line = command.execute(commandArgs, null);
                            execute_script(line, socketChannel);
                        } else {
                            Request request;
                            if (commandManager.getCommandsWithTicket().containsKey(commandName)) {
                                Object ticket = inputManager.readTicket();
                                request = new Request(command, commandArgs, ticket);
                            } else if (commandManager.getCommandsWithPerson().containsKey(commandName)) {
                                Object person = inputManager.readPerson();
                                request = new Request(command, commandArgs, person);
                            } else {
                                request = new Request(command, commandArgs, null);
                            }

                            sendRequest(socketChannel, request);
                            String response = readResponse(socketChannel);
                            System.out.println(response);
                            if (commandName.equals("exit")) {
                                return;
                            }
                        }
                    } else {
                        System.out.println("Неизвестная команда. Введите 'help' для списка команд.");
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Ошибка при подключении/общении с сервером: {}", e.getMessage());
                System.out.println("Не удалось подключиться к серверу. Повтор через 5 секунд...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }



    private void execute_script(String line, SocketChannel socketChannel) throws IOException, ClassNotFoundException {
        String[] inputs = line.split("\n");
        for (String input : inputs) {

            String[] parts = input.split("\\s+", 2);
            String commandName = parts[0].toLowerCase();
            String[] commandArgs = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

            AbstractCommand command = commandManager.getCommands().get(commandName);
            if (command != null) {
                Request request;
                if (commandManager.getCommandsWithTicket().containsKey(commandName)) {
                    Object ticket = inputManager.readTicket();
                    request = new Request(command, commandArgs, ticket);
                } else if (commandManager.getCommandsWithPerson().containsKey(commandName)) {
                    Object person = inputManager.readPerson();
                    request = new Request(command, commandArgs, person);
                } else {
                    request = new Request(command, commandArgs, null);
                }

                sendRequest(socketChannel, request);
                String response = readResponse(socketChannel);
                System.out.println(response);
                if (commandName.equals("exit")) {
                    return;
                }
            } else {
                System.out.println("Неизвестная команда. Введите 'help' для списка команд.");
            }
        }
    }

    private void sendRequest(SocketChannel channel, Request request) throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(request);
        objOut.flush();

        byte[] data = byteOut.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        logger.info("Запрос отправлен серверу");
    }

    private String readResponse(SocketChannel channel) throws IOException, ClassNotFoundException {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        while (lengthBuffer.hasRemaining()) {
            if (channel.read(lengthBuffer) == -1) throw new EOFException("Сервер закрыл соединение");
        }
        lengthBuffer.flip();
        int length = lengthBuffer.getInt();

        ByteBuffer dataBuffer = ByteBuffer.allocate(length);
        while (dataBuffer.hasRemaining()) {
            if (channel.read(dataBuffer) == -1) throw new EOFException("Сервер закрыл соединение");
        }
        byte[] data = dataBuffer.array();

        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(data))) {
            Object response = objIn.readObject();
            logger.info("Получен ответ от сервера");
            return (response instanceof String) ? (String) response : "Неверный формат ответа от сервера";
        }
    }
}