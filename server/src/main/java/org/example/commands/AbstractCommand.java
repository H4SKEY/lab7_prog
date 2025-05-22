package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;

public abstract class AbstractCommand implements Command {
    private final DataObject dataObject;

    public DataObject getDataObject() {
        return dataObject;
    }

    public AbstractCommand(DataObject dataObject) {
        this.dataObject = dataObject;
    }

    @Override
    public abstract String execute(CommandData commandData);

    @Override
    public abstract String description();
}