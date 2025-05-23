package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.data.Ticket;
import org.example.util.CollectionManager;

/**
 * Команда добавления элемента
 */
public class AddCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public AddCommand(CollectionManager collectionManager) {
        super(DataObject.TICKET);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "add - добавить новый элемент";
    }

    @Override
    public String execute(CommandData commandData) {
        Ticket ticket = (Ticket) commandData.getData();
        int newId = collectionManager.addTicket(ticket, commandData.getUser());
        if (newId != -1) {
            return "Билет добавлен с ID: " + newId;
        }
        return "Не удалось добавить билет";
    }
}