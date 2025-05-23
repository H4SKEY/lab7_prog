package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.util.CommandManager;

import java.util.stream.Collectors;

/**
 * Команда вывода справки
 */
public class HelpCommand extends AbstractCommand {
    private final CommandManager commandManager;

    public HelpCommand(CommandManager commandManager) {
        super(DataObject.NONE);
        this.commandManager = commandManager;
    }

    public String description() {
        return "help - вывести справку по командам";
    }

    @Override
    public String execute(CommandData commandData) {
        return commandManager.getCommands().values().stream()
                .map(AbstractCommand::description) // Извлекаем описание команды
                .collect(Collectors.joining("\n")); // Соединяем описания в одну строку с разделением на новые строки
    }
}