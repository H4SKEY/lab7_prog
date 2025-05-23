package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.data.Ticket;
import org.example.util.CollectionManager;

/**
 * Команда обновления элемента по ID
 */
public class UpdateCommand extends AbstractCommand {
    private final CollectionManager collectionManager;


    public UpdateCommand(CollectionManager collectionManager) {
        super(DataObject.TICKET);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "update id - обновить элемент по ID";
    }

    @Override
    public String execute(CommandData commandData) {
        String[] args = commandData.getArgs();
        if (args.length < 1) {
            return "Не указан ID элемента";
        }
        String result;
        try {
            int id = Integer.parseInt(args[0]);
            Ticket existing = collectionManager.getTickets().stream()
                    .filter(t -> t.getId() == id)
                    .findFirst()
                    .orElse(null);

            if (existing == null) {
                return "Элемент с ID " + id + " не найден";
            }

            Ticket updated = (Ticket) commandData.getData();
            collectionManager.updateTicket(id, updated, commandData.getUser());
            result = "Элемент с ID " + id + " обновлен";
        } catch (NumberFormatException e) {
            result = "Неверный формат ID";
        }
        return result;
    }
}