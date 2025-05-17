package org.example.server;

import org.example.commands.AbstractCommand;
import org.example.dataBase.User;
import org.example.network.Request;
import org.example.util.CollectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.*;

public class Server {
    private final int port;
    private ServerSocket serverSocket;
    private final CollectionManager collectionManager;
    private final ExecutorService requestProcessingPool;
    private final ExecutorService responseSendingPool;

    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    private static final int PROCESSING_THREADS = 10; // Количество потоков для обработки запросов

    public Server(int port, CollectionManager collectionManager) {
        this.port = port;
        this.collectionManager = collectionManager;
        this.requestProcessingPool = Executors.newFixedThreadPool(PROCESSING_THREADS);
        this.responseSendingPool = Executors.newCachedThreadPool();
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            logger.info("Сервер запущен на порту {}", port);
            collectionManager.setTickets(collectionManager.getDataBaseManager().loadCollection());

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (IOException e) {
                    logger.error("Ошибка при принятии соединения", e);
                }
            }
        } catch (IOException e) {
            logger.error("Критическая ошибка сервера", e);
        } finally {
            shutdown();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream()) {

            logger.info("Обработка нового клиента: {}", clientSocket.getRemoteSocketAddress());

            while (!clientSocket.isClosed()) {
                Request request = readRequest(input);
                if (request == null) {
                    logger.info("Клиент отключился");
                    break;
                }

                // Обработка запроса в отдельном потоке из FixedThreadPool
                requestProcessingPool.submit(() -> {
                    try {
                        String response = processRequest(request);
                        // Отправка ответа в отдельном потоке из CachedThreadPool
                        responseSendingPool.submit(() -> sendResponse(output, response));
                    } catch (Exception e) {
                        logger.error("Ошибка обработки запроса", e);
                    }
                });
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Ошибка при работе с клиентом", e);
        } finally {
            try {
                clientSocket.close();
                logger.info("Соединение с клиентом закрыто");
            } catch (IOException e) {
                logger.error("Ошибка при закрытии соединения", e);
            }
        }
    }

    private Request readRequest(InputStream input) throws IOException, ClassNotFoundException {
        byte[] lengthBytes = input.readNBytes(4);
        if (lengthBytes.length < 4) return null;

        int length = ByteBuffer.wrap(lengthBytes).getInt();
        byte[] objectBytes = input.readNBytes(length);
        if (objectBytes.length < length) return null;

        try (ObjectInputStream objIn = new ObjectInputStream(new ByteArrayInputStream(objectBytes))) {
            return (Request) objIn.readObject();
        }
    }

    private String processRequest(Request request) {
        if (!checkUser(request.getUser())) {
            return "Неверно введен пароль пользователя";
        }

        AbstractCommand command = request.getCommand();
        command.setCollectionManager(collectionManager);
        return command.execute(request);
    }

    private void sendResponse(OutputStream output, String response) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(response);
            objOut.flush();

            byte[] data = byteOut.toByteArray();
            synchronized (output) {
                output.write(ByteBuffer.allocate(4).putInt(data.length).array());
                output.write(data);
                output.flush();
            }
            logger.info("Ответ отправлен клиенту");
        } catch (IOException e) {
            logger.error("Ошибка при отправке ответа", e);
        }
    }

    private boolean checkUser(User user) {
        logger.info("Проверка пользователя");
        if (collectionManager.getDataBaseManager().registerUser(user)) {
            logger.info("Регистрация пользователя в БД");
            return true;
        }
        return collectionManager.getDataBaseManager().checkPassword(user.getLogin(), user.getPassword());
    }

    private void shutdown() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            requestProcessingPool.shutdown();
            responseSendingPool.shutdown();
            if (!requestProcessingPool.awaitTermination(5, TimeUnit.SECONDS)) {
                requestProcessingPool.shutdownNow();
            }
            if (!responseSendingPool.awaitTermination(5, TimeUnit.SECONDS)) {
                responseSendingPool.shutdownNow();
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Ошибка при завершении работы сервера", e);
        }
    }
}