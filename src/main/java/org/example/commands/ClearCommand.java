package org.example.commands;

import org.example.util.CollectionManager;

import java.io.Serial;
import java.io.Serializable;

/**
 * Команда очистки коллекции
 */
public class ClearCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 103L;

    public ClearCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "clear - очистить коллекцию";
    }

    @Override
    public String execute(String[] args, Object data) {
        collectionManager.clear();
        return "Коллекция очищена";
    }
}