package org.example.commands;

import org.example.util.CollectionManager;
import org.example.data.Ticket;

import java.io.Serial;
import java.io.Serializable;

/**
 * Команда добавления если элемент минимальный
 */
public class AddIfMinCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 102L;

    public AddIfMinCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "add_if_min - добавить если меньше минимального";
    }

    @Override
    public String execute(String[] args, Object data) {
        String result;
        int newId = collectionManager.getTickets().stream()
                .mapToInt(Ticket::getId)
                .max()
                .orElse(0) + 1;

        Ticket newTicket = (Ticket) data;
        newTicket.setId(newId);
        Ticket minTicket = collectionManager.getMinTicket();

        if (minTicket == null || newTicket.compareTo(minTicket) < 0) {
            collectionManager.addTicket(newTicket);
            result = "Элемент добавлен (ID: " + newId + ")";
        } else {
            result = "Элемент не является минимальным, добавление отменено";
        }
        return result;
    }
}
