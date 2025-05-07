package org.example.util;

import org.example.data.Person;
import org.example.data.Ticket;
import org.example.data.TicketType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 202L;

    private boolean reverse = false;
    private List<Ticket> tickets;
    private TreeSet<Integer> ids;
    private final LocalDateTime initDate;

    public CollectionManager() {
        this.initDate = LocalDateTime.now();
        this.tickets = new ArrayList<>();
        this.ids = new TreeSet<>();
    }

    // Геттеры и сеттеры для reverse
    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    // Основные методы управления коллекцией

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        ids.add(ticket.getId());
    }

    public void updateTicket(int id, Ticket newTicket) {
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


    public void removeTicket(int id) {
        if (!tickets.removeIf(t -> t.getId() == id)) {
            throw new NoSuchElementException("Билет с ID " + id + " не найден");
        }
        ids.remove(id);
    }

    public void sort() {
        tickets.sort(Comparator.naturalOrder());
        setReverse(false);
    }

    public void clear() {
        tickets.clear();
        ids.clear();
    }

    public void reorder() {
        Collections.reverse(tickets);
        reverse = !reverse;
    }

    public Ticket getMinTicket() {
        return tickets.stream().min(Ticket::compareTo).orElse(null);
    }

    public void removeLower(Ticket ticket) {
        int ticketId = ticket.getId();
        tickets.removeIf(t -> t.compareTo(ticket) < 0);
        ids.removeIf(id -> id < ticketId);
    }

    public void removeAnyByType(TicketType type) {
        List<Integer> idsToRemove = tickets.stream()
                .filter(t -> t.getType() == type)
                .map(Ticket::getId)
                .toList();
        tickets.removeIf(t -> t.getType() == type);
        idsToRemove.forEach(ids::remove);
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
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public LocalDateTime getInitDate() {
        return initDate;
    }

    public String getCollectionType() {
        return "ArrayList<Ticket>";
    }

    public int getCollectionSize() {
        return tickets.size();
    }

    /**
     * Генерирует новый уникальный ID для билета
     */
    public int generateNewId() {
        if (ids.isEmpty()) {
            ids.add(1);
            return 1;
        }
        int id = ids.last() + 1;
        ids.add(id);
        return id;
    }

    public void setIds(TreeSet<Integer> ids) {
        this.ids = ids;
    }
}
