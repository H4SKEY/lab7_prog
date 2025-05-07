package org.example.server;

import org.example.commands.AbstractCommand;
import org.example.commands.SaveCommand;
import org.example.network.Request;
import org.example.util.CollectionManager;
import org.example.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Server {
    private final int port;
    private ServerSocket serverSocket;
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final CollectionManager collectionManager;
    private final FileManager fileManager;
    private final String fileName;

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public Server(int port, CollectionManager collectionManager, String fileName) {
        this.port = port;
        this.collectionManager = collectionManager;
        this.fileName = fileName;
        this.fileManager = new FileManager(collectionManager, fileName);
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Сервер запущен на порту {}", port);
            fileManager.loadCollection();
            acceptClient();
            logger.info("Получено соединение с клиентом");
            handle();  // Сервер продолжит обрабатывать запросы клиента
        } catch (IOException | ClassNotFoundException e) {
            // Закрытие соединения только в случае ошибки
            logger.warn("Завершение работы сервера...");
        } finally {
            save();
            closeClient();
        }
    }

    private void handle() throws IOException, ClassNotFoundException {
        // Цикл обработки запросов клиента
        while (true) {
            Request request = read();
            if (request == null) {
                break;
            }
            String response = execute(request);
            send(response);
        }
    }

    private void acceptClient() throws IOException {
        socket = serverSocket.accept();
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    private void closeClient() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null && !socket.isClosed()) socket.close();
            logger.info("Соединение с клиентом закрыто");
        } catch (IOException e) {
            logger.error("Ошибка при закрытии соединения с клиентом", e);
        }
    }

    private Request read() throws IOException, ClassNotFoundException {
        byte[] lengthBytes = inputStream.readNBytes(4);
        if (lengthBytes.length < 4) throw new EOFException("Не удалось прочитать длину объекта");

        int length = ByteBuffer.wrap(lengthBytes).getInt();
        byte[] objectBytes = inputStream.readNBytes(length);
        if (objectBytes.length < length) throw new EOFException("Не удалось прочитать объект полностью");

        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(objectBytes))) {
            Request request = (Request) objIn.readObject();
            logger.info("Получено сообщение от клиента");
            return request;
        }
    }

    private String execute(Request request) {
        AbstractCommand command = request.getCommand();
        command.setCollectionManager(collectionManager);
        return command.execute(request.getArgs(), request.getData());
    }

    private void send(String response) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(response);
            objOut.flush();

            byte[] data = byteOut.toByteArray();
            outputStream.write(ByteBuffer.allocate(4).putInt(data.length).array());
            outputStream.write(data);
            outputStream.flush();

            logger.info("Ответ отправлен клиенту");
            objOut.close();
            byteOut.close();
        } catch (IOException e) {
            logger.error("Ошибка при отправке ответа клиенту", e);
        }
    }

    private void save() {
        SaveCommand command = new SaveCommand(collectionManager);
        logger.info(command.execute(null, fileName));
    }
}
