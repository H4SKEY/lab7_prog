package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.data.Ticket;
import org.example.util.CollectionManager;

/**
 * Команда добавления если элемент минимальный
 */
public class AddIfMinCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public AddIfMinCommand(CollectionManager collectionManager) {
        super(DataObject.TICKET);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "add_if_min - добавить если меньше минимального";
    }

    @Override
    public String execute(CommandData commandData) {
        String result;
        Ticket newTicket = (Ticket) commandData.getData();
        Ticket minTicket = collectionManager.getMinTicket();

        if (minTicket == null || newTicket.compareTo(minTicket) < 0) {
            int newId = collectionManager.addTicket(newTicket, commandData.getUser());
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
