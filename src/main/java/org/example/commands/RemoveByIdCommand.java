package org.example.commands;

import org.example.network.Request;
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
    public String execute(Request request) {
        String[] args = request.getArgs();
        if (args.length < 1) {
            return "Не указан ID элемента";
        }
        String result;
        try {
            int id = Integer.parseInt(args[0]);
            boolean isRemoved = collectionManager.removeTicket(id, request.getUser());
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