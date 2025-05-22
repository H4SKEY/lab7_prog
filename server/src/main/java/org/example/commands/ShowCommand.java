package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.data.Ticket;
import org.example.util.CollectionManager;

import java.util.List;
import java.util.stream.Collectors;

public class ShowCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public ShowCommand(CollectionManager collectionManager) {
        super(DataObject.NONE);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "show - показать все элементы коллекции";
    }

    @Override
    public String execute(CommandData commandData) {
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