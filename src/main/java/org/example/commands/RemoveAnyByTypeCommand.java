package org.example.commands;


import org.example.network.Request;
import org.example.util.CollectionManager;
import org.example.data.TicketType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Команда удаления по типу
 */
public class RemoveAnyByTypeCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 110L;

    public RemoveAnyByTypeCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "remove_any_by_type type - удалить по типу";
    }

    @Override
    public String execute(Request request) {
        String[] args = request.getArgs();
        if (args.length < 1) {
            return "Не указан тип билета";
        }
        String result;

        try {
            TicketType type = TicketType.valueOf(args[0].toUpperCase());
            collectionManager.removeAnyByType(type, request.getUser());
            result = "Элементы типа " + type + " удалены";
        } catch (IllegalArgumentException e) {
            result = "Неверный тип билета. Допустимые значения: " + Arrays.toString(TicketType.values());
        }
        return result;
    }
}