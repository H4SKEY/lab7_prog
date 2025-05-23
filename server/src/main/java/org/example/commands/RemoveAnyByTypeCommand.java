package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.data.TicketType;
import org.example.util.CollectionManager;

import java.util.Arrays;

/**
 * Команда удаления по типу
 */
public class RemoveAnyByTypeCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public RemoveAnyByTypeCommand(CollectionManager collectionManager) {
        super(DataObject.NONE);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "remove_any_by_type type - удалить по типу";
    }

    @Override
    public String execute(CommandData commandData) {
        String[] args = commandData.getArgs();
        if (args.length < 1) {
            return "Не указан тип билета";
        }
        String result;

        try {
            TicketType type = TicketType.valueOf(args[0].toUpperCase());
            collectionManager.removeAnyByType(type, commandData.getUser());
            result = "Элементы типа " + type + " удалены";
        } catch (IllegalArgumentException e) {
            result = "Неверный тип билета. Допустимые значения: " + Arrays.toString(TicketType.values());
        }
        return result;
    }
}