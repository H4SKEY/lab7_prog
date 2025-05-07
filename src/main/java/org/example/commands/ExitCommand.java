package org.example.commands;

import org.example.util.CollectionManager;

import java.io.Serial;
import java.io.Serializable;

public class ExitCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 106L;

    public ExitCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "exit - завершение программы";
    }

    @Override
    public String execute(String[] args, Object data) {
        return "Завершение программы...";
    }
}
