package org.example.commands;

import org.example.network.CommandData;

public interface Command {
    String execute(CommandData commandData);

    String description();
}