package org.example.commands;

import org.example.network.Request;
import org.example.util.CollectionManager;
import org.example.util.CommandManager;

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

    private final CommandManager commandManager;

    public HelpCommand(CollectionManager collectionManager, CommandManager commandManager) {
        super(collectionManager);
        this.commandManager = commandManager;
    }

    public String description() {
        return "help - вывести справку по командам";
    }

    @Override
    public String execute(Request request) {
        HashMap<String, AbstractCommand> commands = commandManager.getCommands();
        return commands.values().stream()
                .map(AbstractCommand::description) // Извлекаем описание команды
                .collect(Collectors.joining("\n")); // Соединяем описания в одну строку с разделением на новые строки
    }
}