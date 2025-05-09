package org.example.commands;

import org.example.network.Request;
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
    public String execute(Request request) {
        Ticket ticket = (Ticket) request.getData();
        int beforeSize = collectionManager.getCollectionSize();
        collectionManager.removeLower(ticket, request.getUser());
        int removed = beforeSize - collectionManager.getCollectionSize();
        return "Удалено элементов: " + removed;
    }
}