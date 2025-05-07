package org.example.commands;

public interface Command {
    String execute(String[] args, Object data);

    String description();
}