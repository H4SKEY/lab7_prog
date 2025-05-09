package org.example.commands;

import org.example.network.Request;
import org.example.util.CollectionManager;
import org.example.data.Ticket;

import java.io.Serial;
import java.io.Serializable;

/**
 * Команда добавления элемента
 */
public class AddCommand extends AbstractCommand implements Serializable {

    @Serial
    private static final long serialVersionUID = 101L;

    public AddCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "add - добавить новый элемент";
    }

    @Override
    public String execute(Request request) {
        Ticket ticket = (Ticket) request.getData();
        int newId = collectionManager.addTicket(ticket, request.getUser());
        if (newId != -1) {
            return "Билет добавлен с ID: " + newId;
        }
        return "Не удалось добавить билет";
    }
}