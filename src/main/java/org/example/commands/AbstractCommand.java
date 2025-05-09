package org.example.commands;

import org.example.network.Request;
import org.example.util.CollectionManager;

import java.io.Serial;
import java.io.Serializable;

public abstract class AbstractCommand implements Command, Serializable {
    @Serial
    private static final long serialVersionUID = 100L;
    protected CollectionManager collectionManager;

    public void setCollectionManager(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public AbstractCommand(CollectionManager collectionManager) {
        setCollectionManager(collectionManager);
    }

    public abstract String execute(Request request);

    public abstract String description();
}