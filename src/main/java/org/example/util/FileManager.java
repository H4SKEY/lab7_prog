package org.example.util;

import org.example.data.Ticket;

import java.io.IOException;
import java.util.*;

public class FileManager {
    public final CollectionManager collectionManager;
    private final String fileName;

    public FileManager(CollectionManager collectionManager, String fileName) {
        this.collectionManager = collectionManager;
        this.fileName = fileName;
    }

    public boolean saveCollection() {
        try {
            JsonSerializer.saveToFile(collectionManager.getTickets(), fileName);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void loadCollection() {
        try {
            List<Ticket> loadedTickets = JsonSerializer.loadFromFile(fileName);
            List<Ticket> tickets = collectionManager.getTickets();

            if (loadedTickets != null) {
                // Проверка на уникальность ID
                TreeSet<Integer> ids = new TreeSet<>();
                List<Ticket> validTickets = new ArrayList<>();

                for (Ticket ticket : loadedTickets) {
                    if (ids.contains(ticket.getId())) {
                        System.out.println("Ошибка: Найден дубликат ID " + ticket.getId());
                        continue;
                    }
                    if (ticket.getId() <= 0) {
                        System.out.println("Ошибка: Неверный ID " + ticket.getId());
                        continue;
                    }
                    ids.add(ticket.getId());
                    validTickets.add(ticket);
                }

                collectionManager.setTickets(validTickets);
                collectionManager.setIds(ids);
                System.out.println("Загружено " + tickets.size() + " элементов из файла " + fileName);
            }
        } catch (IOException e) {
            System.out.println("Ошибка загрузки файла: " + e.getMessage());
            System.out.println("Будет создана пустая коллекция");
            collectionManager.setTickets(new ArrayList<>());
            collectionManager.setIds(new TreeSet<>());
        } catch (Exception e) {
            System.out.println("Ошибка при загрузке коллекции: " + e.getMessage());
            collectionManager.setTickets(new ArrayList<>());
            collectionManager.setIds(new TreeSet<>());
        }
    }
}
