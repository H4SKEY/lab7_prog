package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;

public class ExitCommand extends AbstractCommand {

    public ExitCommand() {
        super(DataObject.NONE);
    }

    public String description() {
        return "exit - завершение программы";
    }

    @Override
    public String execute(CommandData commandData) {
        return "Завершение программы...";
    }
}
