package org.example.commands;

import org.example.network.Request;
import org.example.util.CollectionManager;

import java.io.Serial;
import java.io.Serializable;


public class ExecuteScriptCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 105L;

    public ExecuteScriptCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "execute_script file_name : выполнить скрипт из файла";
    }

    @Override
    public String execute(Request request) {
        return request.getArgs()[0];
    }
}