package org.example.commands;

import org.example.network.Request;

public interface Command {
    String execute(Request request);

    String description();
}