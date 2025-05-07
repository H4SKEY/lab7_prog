package org.example.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

import org.example.commands.*;


public class CommandManager implements Serializable {
    @Serial
    private static final long serialVersionUID = 201L;

    private final HashMap<String, AbstractCommand> commands = new HashMap<>();
    private final HashMap<String, AbstractCommand> commandsWithTicket = new HashMap<>();
    private final HashMap<String, AbstractCommand> commandsWithPerson = new HashMap<>();
    private final CollectionManager collectionManager;

    public CommandManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        registerCommands();
    }

    private void registerCommands() {
        commands.put("help", new HelpCommand(collectionManager, this));
        commands.put("info", new InfoCommand(collectionManager));
        commands.put("show", new ShowCommand(collectionManager));
        commands.put("add", new AddCommand(collectionManager));
        commands.put("update", new UpdateCommand(collectionManager));
        commands.put("remove_by_id", new RemoveByIdCommand(collectionManager));
        commands.put("clear", new ClearCommand(collectionManager));
        //commands.put("save", new SaveCommand(collectionManager));
        commands.put("execute_script", new ExecuteScriptCommand(collectionManager, this));
        commands.put("exit", new ExitCommand(collectionManager));
        commands.put("add_if_min", new AddIfMinCommand(collectionManager));
        commands.put("remove_lower", new RemoveLowerCommand(collectionManager));
        commands.put("reorder", new ReorderCommand(collectionManager));
        commands.put("remove_any_by_type", new RemoveAnyByTypeCommand(collectionManager));
        commands.put("count_by_person", new CountByPersonCommand(collectionManager));
        commands.put("print_field_ascending_person", new PrintFieldAscendingPersonCommand(collectionManager));

        commandsWithTicket.put("add", commands.get("add"));
        commandsWithTicket.put("add_if_min", commands.get("add_if_min"));
        commandsWithTicket.put("remove_lower", commands.get("remove_lower"));
        commandsWithTicket.put("update", commands.get("update"));

        commandsWithPerson.put("count_by_person", commands.get("count_by_person"));
    }

    public HashMap<String, AbstractCommand> getCommands() {
        return commands;
    }

    public HashMap<String, AbstractCommand> getCommandsWithTicket() {
        return commandsWithTicket;
    }

    public HashMap<String, AbstractCommand> getCommandsWithPerson() {
        return commandsWithPerson;
    }
}
