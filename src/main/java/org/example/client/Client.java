package org.example.client;

import org.example.commands.AbstractCommand;
import org.example.util.Hasher;
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

        return new User(login, password);
    }

    public void run() {
        user = inputUser();

        while (true) {
            try (SocketChannel socketChannel = connectToServer()) {
                handleUserCommands(socketChannel);
            } catch (IOException | ClassNotFoundException e) {
                handleConnectionError(e);
            }
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

    private void handleUserCommands(SocketChannel socketChannel) throws IOException, ClassNotFoundException {
        while (true) {
            String input = readUserInput();
            if (input.isEmpty()) continue;

            String[] parts = input.split("\\s+", 2);
            String commandName = parts[0].toLowerCase();
            String[] commandArgs = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

            processCommand(socketChannel, commandName, commandArgs);
        }
    }

    private void processCommand(SocketChannel socketChannel, String commandName, String[] commandArgs)
            throws IOException, ClassNotFoundException {
        AbstractCommand command = commandManager.getCommands().get(commandName);
        if (command == null) {
            System.out.println("Неизвестная команда. Введите 'help' для списка команд.");
            return;
        }

        if (commandName.equals("execute_script")) {
            if (commandArgs.length == 0) {
                System.out.println("Ошибка: Не указано имя файла скрипта");
                return;
            }
            executeScript(commandArgs[0], socketChannel);
        } else if (commandName.equals("exit")) {
            handleExitCommand(command, commandArgs);
        } else {
            Request request = createRequest(command, commandName, commandArgs);
            sendRequest(socketChannel, request);
            String response = readResponse(socketChannel);
            System.out.println(response);
        }
    }

    private Request createRequest(AbstractCommand command, String commandName, String[] commandArgs) {
        if (commandManager.getCommandsWithTicket().containsKey(commandName)) {
            return new Request(command, commandArgs, inputManager.readTicket(), user);
        } else if (commandManager.getCommandsWithPerson().containsKey(commandName)) {
            return new Request(command, commandArgs, inputManager.readPerson(), user);
        }
        return new Request(command, commandArgs, null, user);
    }

    private void handleExitCommand(AbstractCommand command, String[] commandArgs) {
        System.out.println(command.execute(new Request(null, commandArgs, null, user)));
        System.exit(0);
    }

    private String readUserInput() {
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    private void executeScript(String fileName, SocketChannel socketChannel)
            throws IOException, ClassNotFoundException {
        File scriptFile = new File(fileName).getAbsoluteFile();

        if (!scriptFile.exists()) {
            System.out.println("Ошибка: Файл скрипта не найден: " + fileName);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(scriptFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                processScriptLine(line, socketChannel);
            }
        }
    }

    private void processScriptLine(String line, SocketChannel socketChannel)
            throws IOException, ClassNotFoundException {
        String[] parts = line.split("\\s+", 2);
        String commandName = parts[0].toLowerCase();
        String[] commandArgs = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

        // Запрет вложенных скриптов
        if (commandName.equals("execute_script")) {
            System.out.println("Ошибка: Вложенные скрипты запрещены");
            return;
        }

        AbstractCommand command = commandManager.getCommands().get(commandName);
        if (command == null) {
            System.out.println("Неизвестная команда в скрипте: " + commandName);
            return;
        }

        Request request = createRequest(command, commandName, commandArgs);
        sendRequest(socketChannel, request);
        String response = readResponse(socketChannel);
        System.out.println(response);
    }

    private void handleConnectionError(Exception e) {
        logger.error("Ошибка при подключении/общении с сервером: {}", e.getMessage());
        System.out.println("Не удалось подключиться к серверу. Повтор через 5 секунд...");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
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