package org.example.commands;

import org.example.util.CollectionManager;
import org.example.util.CommandManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Scanner;

public class ExecuteScriptCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 105L;

    private final CommandManager commandManager;

    public ExecuteScriptCommand(CollectionManager collectionManager, CommandManager commandManager) {
        super(collectionManager);
        this.commandManager = commandManager;
    }

    public String description() {
        return "execute_script file_name - выполнить скрипт";
    }

    @Override
    public String execute(String[] args, Object data) {
        StringBuilder result = new StringBuilder();
        String filename = args[0];
        HashMap<String, AbstractCommand> commands = commandManager.getCommands();

        try (Scanner scriptScanner = new Scanner(new File(filename))) {
            while (scriptScanner.hasNextLine()) {
                String line = scriptScanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                result.append(line).append("\n");
                //result.append("Выполняю: ").append(line).append("\n");
//                String[] parts = line.split(" ", 2);
//                String commandName = parts[0].toLowerCase();
//                String[] commandArgs = parts.length > 1 ? parts[1].split(" ") : new String[0];

//                if (commandName.equals("execute_script")) {
//                    //result.append("Ошибка: Рекурсивный вызов скриптов запрещен\n");
//                    continue;
//                }
//
//                AbstractCommand command = commands.get(commandName);
//                if (command != null) {
//                    command.setCollectionManager(collectionManager);
//                    if (commandManager.getCommandsWithTicket().containsValue(command)) {
//                        result.append("Для команды ").append(commandName).append(" требуется ввод Ticket\n");
//                    } else if (commandManager.getCommandsWithPerson().containsValue(command)) {
//                        result.append("Для команды ").append(commandName).append(" требуется ввод Person\n");
//                    } else {
//                        result.append(command.execute(commandArgs, null)).append("\n");
//                    }
//                    if (commandName.equals("exit")) {
//                        return result.toString();
//                    }
//            }
//                else {
//                    result.append("Неизвестная команда: ").append(commandName).append("\n");
//                }
            }
        } catch (FileNotFoundException e) {
            //result = new StringBuilder("Файл скрипта не найден: " + filename);
        } catch (Exception e) {
            //result = new StringBuilder("Ошибка выполнения скрипта: " + e.getMessage());
        }
        return result.toString();
    }
}