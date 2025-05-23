package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.util.CollectionManager;

/**
 * Команда очистки коллекции
 */
public class ClearCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public ClearCommand(CollectionManager collectionManager) {
        super(DataObject.NONE);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "clear - очистить коллекцию";
    }

    @Override
    public String execute(CommandData commandData) {
        collectionManager.clear(commandData.getUser());
        return "Коллекция очищена";
    }
}