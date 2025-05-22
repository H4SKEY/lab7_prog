package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.data.Ticket;
import org.example.util.CollectionManager;

/**
 * Команда удаления элементов меньше заданного
 */
public class RemoveLowerCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public RemoveLowerCommand(CollectionManager collectionManager) {
        super(DataObject.TICKET);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "remove_lower - удалить меньшие элементы";
    }

    @Override
    public String execute(CommandData commandData) {
        Ticket ticket = (Ticket) commandData.getData();
        int beforeSize = collectionManager.getCollectionSize();
        collectionManager.removeLower(ticket, commandData.getUser());
        int removed = beforeSize - collectionManager.getCollectionSize();
        return "Удалено элементов: " + removed;
    }
}