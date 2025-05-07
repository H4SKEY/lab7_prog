package org.example.commands;

import org.example.util.CollectionManager;

import java.io.Serial;
import java.io.Serializable;

/**
 * Команда реверса коллекции
 */
public class ReorderCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 113L;

    public ReorderCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "reorder - отсортировать в обратном порядке";
    }

    @Override
    public String execute(String[] args, Object data) {
        collectionManager.reorder();
        return "Коллекция отсортирована в обратном порядке";
    }
}