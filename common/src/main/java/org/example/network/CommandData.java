package org.example.network;

import java.io.Serial;
import java.io.Serializable;

public class CommandData implements Serializable {
    @Serial
    private static final long serialVersionUID = 501L;
    private String[] args;
    private Object data;
    private User user;

    public CommandData(String[] args, User user) {
        this.args = args;
        this.user = user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
