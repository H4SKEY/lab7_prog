package org.example.network;

import java.io.Serial;
import java.io.Serializable;

public class Request implements Serializable {
    private RequestType requestType;
    private Object data;
    private User user;

    @Serial
    private static final long serialVersionUID = 200L;

    public Request(RequestType requestType, User user) {
        this.requestType = requestType;
        this.user = user;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
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
