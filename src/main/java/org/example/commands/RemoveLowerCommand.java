package org.example.commands;

import org.example.util.CollectionManager;
import org.example.data.Ticket;

import java.io.Serial;
import java.io.Serializable;

/**
 * Команда удаления элементов меньше заданного
 */
public class RemoveLowerCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 112L;

    public RemoveLowerCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "remove_lower - удалить меньшие элементы";
    }

    @Override
    public String execute(String[] args, Object data) {
        int newId = collectionManager.getTickets().stream()
                .mapToInt(Ticket::getId)
                .max()
                .orElse(0) + 1;
        Ticket ticket = (Ticket) data;
        ticket.setId(newId);
        int beforeSize = collectionManager.getCollectionSize();
        collectionManager.removeLower(ticket);
        int removed = beforeSize - collectionManager.getCollectionSize();
        return "Удалено элементов: " + removed;
    }
}