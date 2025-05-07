package org.example.commands;


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
    public String execute(String[] args, Object data) {
        if (args.length < 1) {
            return "Не указан тип билета";
        }
        String result;

        try {
            TicketType type = TicketType.valueOf(args[0].toUpperCase());
            collectionManager.removeAnyByType(type);
            result = "Элемент типа " + type + " удален";
        } catch (IllegalArgumentException e) {
            result = "Неверный тип билета. Допустимые значения: " + Arrays.toString(TicketType.values());
        }
        return result;
    }
}