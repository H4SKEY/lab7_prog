package org.example.network;

import java.io.Serial;
import java.io.Serializable;

public class CommandInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 500L;
    private String commandName;
    private CommandData commandData;

    public CommandInfo(String commandName, CommandData commandData) {
        this.commandName = commandName;
        this.commandData = commandData;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public CommandData getCommandData() {
        return commandData;
    }

    public void setCommandData(CommandData commandData) {
        this.commandData = commandData;
    }
}
