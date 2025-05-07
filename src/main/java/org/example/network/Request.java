package org.example.network;

import org.example.commands.AbstractCommand;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    private AbstractCommand command;
    private String[] args;
    private Object data;

    @Serial
    private static final long serialVersionUID = 200L;

    public AbstractCommand getCommand() {
        return command;
    }

    public void setCommand(AbstractCommand command) {
        this.command = command;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Request(AbstractCommand command, String[] args, Object data) {
        setCommand(command);
        setArgs(args);
        setData(data);
    }
}
