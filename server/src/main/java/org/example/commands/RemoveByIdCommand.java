package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.util.CollectionManager;

/**
 * Команда удаления элемента по ID
 */
public class RemoveByIdCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public RemoveByIdCommand(CollectionManager collectionManager) {
        super(DataObject.NONE);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "remove_by_id id - удалить элемент по ID";
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
            boolean isRemoved = collectionManager.removeTicket(id, commandData.getUser());
            if (isRemoved) {
                result = "Элемент с ID " + id + " удален";
            } else {
                result = "Элемент с ID " + id + " не удалось удалить";
            }
        } catch (NumberFormatException e) {
            result = "Неверный формат ID";
        }
        return result;
    }
}