package org.example.commands;

import org.example.util.CollectionManager;

import java.io.Serial;
import java.io.Serializable;

/**
 * Команда вывода информации о коллекции
 */
public class InfoCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 108L;

    public InfoCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "info - информация о коллекции";
    }

    @Override
    public String execute(String[] args, Object data) {
        String result = "Тип коллекции: " + collectionManager.getCollectionType() + "\n";
        result += "Дата инициализации: " + collectionManager.getInitDate() + "\n";
        result += "Количество элементов: " + collectionManager.getCollectionSize();
        return result;
    }
}