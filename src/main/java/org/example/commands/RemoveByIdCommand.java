package org.example.commands;

import org.example.util.CollectionManager;

import java.io.Serial;
import java.io.Serializable;

/**
 * Команда удаления элемента по ID
 */
public class RemoveByIdCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 111L;

    public RemoveByIdCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "remove_by_id id - удалить элемент по ID";
    }

    @Override
    public String execute(String[] args, Object data) {
        if (args.length < 1) {
            return "Не указан ID элемента";
        }
        String result;
        try {
            int id = Integer.parseInt(args[0]);
            collectionManager.removeTicket(id);
            result = "Элемент с ID " + id + " удален";
        } catch (NumberFormatException e) {
            result = "Неверный формат ID";
        }
        return result;
    }
}