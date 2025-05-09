package org.example.commands;

import org.example.network.Request;
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
    public String execute(Request request) {
        String result;
        Ticket newTicket = (Ticket) request.getData();
        Ticket minTicket = collectionManager.getMinTicket();

        if (minTicket == null || newTicket.compareTo(minTicket) < 0) {
            int newId = collectionManager.addTicket(newTicket, request.getUser());
            if (newId != -1) {
                result = "Элемент добавлен (ID: " + newId + ")";
            } else {
                result = "Элемент не удалось добавить";
            }
        } else {
            result = "Элемент не является минимальным, добавление отменено";
        }
        return result;
    }
}
