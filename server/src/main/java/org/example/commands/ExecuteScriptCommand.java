package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;

public class ExecuteScriptCommand extends AbstractCommand {
    public ExecuteScriptCommand() {

        super(DataObject.NONE);
    }

    public String description() {
        return "execute_script file_name : выполнить скрипт из файла";
    }

    @Override
    public String execute(CommandData commandData) {
        return commandData.getArgs()[0];
    }
}