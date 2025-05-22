package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.util.CollectionManager;

/**
 * Команда вывода информации о коллекции
 */
public class InfoCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public InfoCommand(CollectionManager collectionManager) {
        super(DataObject.NONE);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "info - информация о коллекции";
    }

    @Override
    public String execute(CommandData commandData) {
        String result = "Тип коллекции: " + collectionManager.getCollectionType() + "\n";
        result += "Дата инициализации: " + collectionManager.getInitDate() + "\n";
        result += "Количество элементов: " + collectionManager.getCollectionSize();
        return result;
    }
}