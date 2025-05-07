package org.example.commands;

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
    public String execute(String[] args, Object data) {
        Ticket ticket = (Ticket) data;
        // Генерация ID - находим максимальный существующий и добавляем 1
        int newId = collectionManager.generateNewId();
        ticket.setId(newId);
        collectionManager.addTicket(ticket);
        return "Билет добавлен с ID: " + newId;
    }
}