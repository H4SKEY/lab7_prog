package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.util.CollectionManager;

/**
 * Команда реверса коллекции
 */
public class ReorderCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public ReorderCommand(CollectionManager collectionManager) {
        super(DataObject.NONE);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "reorder - отсортировать в обратном порядке";
    }

    @Override
    public String execute(CommandData commandData) {
        collectionManager.reorder();
        return "Коллекция отсортирована в обратном порядке";
    }
}