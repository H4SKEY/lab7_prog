package org.example;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.network.CommandInfo;
import org.example.network.RequestType;
import org.example.network.User;
import org.example.network.Request;
import org.example.util.Hasher;
import org.example.util.InputManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;

public class Client {
    private final Set<String> scriptFileNames;
    private final int port;
    private final String serverAddress;
    private Map<String, DataObject> commands;
    private final InputManager inputManager;
    private final Scanner scanner;
    private User user;
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public Client(int port, Scanner scanner) {
        this(port, "localhost", scanner);
    }

    public Client(int port, String serverAddress, Scanner scanner) {
        this.port = port;
        this.serverAddress = serverAddress;
        this.inputManager = new InputManager(scanner);
        this.scanner = scanner;
        this.scriptFileNames = new HashSet<>();
    }

    public void run() {
        while (true) {
            try (SocketChannel socketChannel = connectToServer()) {
                authorize(socketChannel);
                getCommands(socketChannel);
                handleUserCommands(socketChannel);
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

    public User inputUser() {
        System.out.println("Введите логин: ");
        String login = scanner.nextLine();

        System.out.println("Введите пароль: ");
        String password = scanner.nextLine();

        password = Hasher.hashPassword(password);

        return new User(login, password);
    }

    private void authorize(SocketChannel socketChannel) throws IOException, ClassNotFoundException {
        user = inputUser();
        Request request = new Request(RequestType.AUTHORIZATION, user);
        sendRequest(socketChannel, request);
        boolean isAuthorized = (Boolean) readResponse(socketChannel);
        if (isAuthorized) {
            logger.info("Успешная авторизация");
        } else {
            System.out.println("Неверно введен пароль, повторите вход:");
            authorize(socketChannel);
        }

    }

    private SocketChannel connectToServer() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(serverAddress, port));
        while (!socketChannel.finishConnect()) {
            // Ожидание соединения
        }
        logger.info("Подключение установлено с {}:{}", serverAddress, port);
        return socketChannel;
    }

    private void getCommands(SocketChannel socketChannel) throws IOException, ClassNotFoundException {
        Request request = new Request(RequestType.GET_COMMANDS, user);
        sendRequest(socketChannel, request);
        Object response = readResponse(socketChannel);
        if (response instanceof Map<?, ?>) {
            commands = (Map<String, DataObject>) response;
            logger.info("Получен список команд");
        } else {
            getCommands(socketChannel);
        }
    }

    private void handleUserCommands(SocketChannel socketChannel) throws IOException, ClassNotFoundException {
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+", 2);
            String commandName = parts[0].toLowerCase();
            String[] commandArgs = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

            processCommand(socketChannel, commandName, commandArgs);
        }
    }

    private void processCommand(SocketChannel socketChannel, String commandName, String[] commandArgs)
            throws IOException, ClassNotFoundException {
        DataObject dataObject = commands.get(commandName);
        if (dataObject == null) {
            System.out.println("Неизвестная команда. Введите 'help' для списка команд.");
            return;
        }
        Request request = new Request(RequestType.EXECUTE_COMMAND, user);
        CommandData commandData = new CommandData(commandArgs, user);
        switch (dataObject) {
            case PERSON -> commandData.setData(inputManager.readPerson());
            case TICKET -> commandData.setData(inputManager.readTicket());
        }
        request.setData(new CommandInfo(commandName, commandData));

        sendRequest(socketChannel, request);
        String response = (String) readResponse(socketChannel);
        System.out.println(response);
        if (commandName.equals("execute_script")) {
            executeScript(commandArgs, socketChannel);
        } else if (commandName.equals("exit")) {
            System.exit(0);
        }
    }

    private void executeScript(String[] commandArgs, SocketChannel socketChannel)
            throws IOException, ClassNotFoundException {

        // Проверка наличия имени файла
        if (commandArgs.length == 0) {
            System.out.println("Ошибка: Не указано имя файла скрипта");
            return;
        }

        String fileName = commandArgs[0];
        File scriptFile = new File(fileName).getAbsoluteFile();

        // Проверка существования файла
        if (!scriptFile.exists()) {
            System.out.println("Ошибка: Файл скрипта не найден: " + fileName);
            return;
        }

        // Проверка на рекурсию
        if (scriptFileNames.contains(fileName)) {
            System.out.println("Ошибка: найдена рекурсия: " + fileName);
            return;
        }

        scriptFileNames.add(fileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(scriptFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Пропускаем пустые строки и комментарии
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Разбиваем строку на команду и аргументы
                String[] parts = line.split("\\s+", 2);
                String commandName = parts[0].toLowerCase();
                String[] args = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

                // Обрабатываем команду
                processCommand(socketChannel, commandName, args);
            }
        } finally {
            scriptFileNames.remove(fileName);
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

    private Object readResponse(SocketChannel channel) throws IOException, ClassNotFoundException {
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
            return response;
        }
    }
}