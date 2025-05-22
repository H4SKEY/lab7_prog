package org.example.util;

import org.example.data.Person;
import org.example.data.Ticket;
import org.example.data.TicketType;
import org.example.dataBase.DataBaseManager;
import org.example.network.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class CollectionManager {
    private boolean reverse = false;
    private List<Ticket> tickets;
    private final LocalDateTime initDate;
    private DataBaseManager dataBaseManager;
    private final ReentrantLock lock = new ReentrantLock();

    public CollectionManager() {
        this.initDate = LocalDateTime.now();
        this.tickets = new ArrayList<>();
        this.dataBaseManager = null;
    }

    // Геттеры и сеттеры для reverse
    public boolean isReverse() {
        lock.lock();
        try {
            return reverse;
        } finally {
            lock.unlock();
        }
    }

    public void setReverse(boolean reverse) {
        lock.lock();
        try {
            this.reverse = reverse;
        } finally {
            lock.unlock();
        }
    }

    // Основные методы управления коллекцией
    public int addTicket(Ticket ticket, User user) {
        lock.lock();
        try {
            int newId = dataBaseManager.insertTicket(ticket, user);
            if (newId == -1) return -1;
            ticket.setId(newId);
            tickets.add(ticket);
            return newId;
        } finally {
            lock.unlock();
        }
    }

    public void updateTicket(int id, Ticket newTicket, User user) {
        lock.lock();
        try {
            if (dataBaseManager.updateTicket(id, newTicket, user)) {
                tickets.stream()
                        .filter(ticket -> ticket.getId() == id)
                        .findFirst()
                        .ifPresentOrElse(
                                ticket -> tickets.set(tickets.indexOf(ticket), newTicket),
                                () -> {
                                    throw new NoSuchElementException("Билет с ID " + id + " не найден");
                                }
                        );
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean removeTicket(int id, User user) {
        lock.lock();
        try {
            if (dataBaseManager.removeTicketById(id, user)) {
                return tickets.removeIf(t -> t.getId() == id);
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void sort() {
        lock.lock();
        try {
            tickets.sort(Comparator.naturalOrder());
            setReverse(false);
        } finally {
            lock.unlock();
        }
    }

    public void clear(User user) {
        lock.lock();
        try {
            List<Ticket> userTickets = tickets.stream()
                    .filter(t -> dataBaseManager.isTicketOwnedByUser(t.getId(), user))
                    .toList();

            userTickets.forEach(t -> removeTicket(t.getId(), user));
        } finally {
            lock.unlock();
        }
    }

    public void reorder() {
        lock.lock();
        try {
            Collections.reverse(tickets);
            reverse = !reverse;
        } finally {
            lock.unlock();
        }
    }

    public Ticket getMinTicket() {
        return tickets.stream().min(Ticket::compareTo).orElse(null);
    }

    public void removeLower(Ticket ticket, User user) {
        lock.lock();
        try {
            tickets.stream()
                    .filter(t -> t.compareTo(ticket) < 0)
                    .forEach(t -> removeTicket(t.getId(), user));
        } finally {
            lock.unlock();
        }
    }

    public void removeAnyByType(TicketType type, User user) {
        lock.lock();
        try {
            tickets.stream()
                    .filter(t -> t.getType() == type)
                    .forEach(t -> removeTicket(t.getId(), user));
        } finally {
            lock.unlock();
        }
    }


    public long countByPerson(Person person) {
        return tickets.stream()
                .filter(t -> t.getPerson().equals(person))
                .count();
    }

    public List<Person> getPersonsAscending() {
        return tickets.stream()
                .map(Ticket::getPerson)
                .sorted(Comparator.comparing(Person::getPassportID, Comparator.nullsFirst(String::compareTo)))
                .collect(Collectors.toList());
    }

    // Геттеры для информации о коллекции
    public List<Ticket> getTickets() {
        return new ArrayList<>(tickets); // Возвращаем копию для безопасности
    }

    public void setTickets(List<Ticket> tickets) {
        lock.lock();
        try {
            this.tickets = new ArrayList<>(tickets); // Сохраняем копию для безопасности
        } finally {
            lock.unlock();
        }
    }

    public LocalDateTime getInitDate() {
        return initDate; // Не требует синхронизации, так как final
    }

    public String getCollectionType() {
        return "ArrayList<Ticket>"; // Не требует синхронизации, так как константа
    }

    public int getCollectionSize() {
        return tickets.size();
    }

    public DataBaseManager getDataBaseManager() {
        return dataBaseManager;
    }

    public void setDataBaseManager(DataBaseManager dataBaseManager) {
        lock.lock();
        try {
            this.dataBaseManager = dataBaseManager;
        } finally {
            lock.unlock();
        }
    }
}