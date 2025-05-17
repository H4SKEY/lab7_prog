package org.example.commands;

import org.example.network.Request;
import org.example.util.CollectionManager;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Команда вывода справки
 */
public class HelpCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 107L;

    public HelpCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "help - вывести справку по командам";
    }

    @Override
    public String execute(Request request) {
        HashMap<String, AbstractCommand> commands = (HashMap<String, AbstractCommand>) request.getData();
        return commands.values().stream()
                .map(AbstractCommand::description) // Извлекаем описание команды
                .collect(Collectors.joining("\n")); // Соединяем описания в одну строку с разделением на новые строки
    }
}