package org.example.commands;

import org.example.util.CollectionManager;
import org.example.util.FileManager;

import java.io.Serial;
import java.io.Serializable;

/**
 * Команда сохранения коллекции
 */
public class SaveCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 114L;

    public SaveCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "save - сохранить коллекцию в файл";
    }

    @Override
    public String execute(String[] args, Object data) {
        FileManager fileManager = new FileManager(collectionManager, (String) data);
        if (fileManager.saveCollection()) {
            return "Коллекция успешно сохранена";
        }
        return "Ошибка сохранения";
    }
}