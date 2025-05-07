package org.example.commands;

import org.example.util.CollectionManager;
import org.example.data.Ticket;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class ShowCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 115L;

    public ShowCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "show - показать все элементы коллекции";
    }

    @Override
    public String execute(String[] args, Object data) {
        List<Ticket> tickets = collectionManager.getTickets();

        if (tickets.isEmpty()) {
            return "Коллекция пуста";
        }

        if (!collectionManager.isReverse()) {
            collectionManager.sort();
        }

        return tickets.stream()
                .map(this::formatTicket) // Преобразуем каждый билет в строку
                .collect(Collectors.joining("\n", "=== Элементы коллекции ===\n",
                        "\n=== Всего элементов: " + tickets.size() + " ==="));
    }

    private String formatTicket(Ticket ticket) {
        return String.format(
                "ID: %d | Название: %s | Цена: %d | Тип: %s",
                ticket.getId(),
                ticket.getName(),
                ticket.getPrice(),
                ticket.getType()
        );
    }
}