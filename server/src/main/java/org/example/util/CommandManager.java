package org.example.util;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.commands.*;
import org.example.network.DataObject;


public class CommandManager {

    private final HashMap<String, AbstractCommand> commands = new HashMap<>();
    private final CollectionManager collectionManager;

    public CommandManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        registerCommands();
    }

    private void registerCommands() {
        commands.put("help", new HelpCommand(this));
        commands.put("info", new InfoCommand(collectionManager));
        commands.put("show", new ShowCommand(collectionManager));
        commands.put("add", new AddCommand(collectionManager));
        commands.put("update", new UpdateCommand(collectionManager));
        commands.put("remove_by_id", new RemoveByIdCommand(collectionManager));
        commands.put("clear", new ClearCommand(collectionManager));
        commands.put("execute_script", new ExecuteScriptCommand());
        commands.put("exit", new ExitCommand());
        commands.put("add_if_min", new AddIfMinCommand(collectionManager));
        commands.put("remove_lower", new RemoveLowerCommand(collectionManager));
        commands.put("reorder", new ReorderCommand(collectionManager));
        commands.put("remove_any_by_type", new RemoveAnyByTypeCommand(collectionManager));
        commands.put("count_by_person", new CountByPersonCommand(collectionManager));
        commands.put("print_field_ascending_person", new PrintFieldAscendingPersonCommand(collectionManager));
    }

    public HashMap<String, AbstractCommand> getCommands() {
        return commands;
    }

    public Map<String, DataObject> getCommandsData() {
        return commands.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getDataObject()
                ));
    }
}
