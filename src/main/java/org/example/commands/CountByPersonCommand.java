package org.example.commands;

import org.example.network.Request;
import org.example.util.CollectionManager;
import org.example.data.Person;

import java.io.Serial;
import java.io.Serializable;

/**
 * Команда подсчета по человеку
 */
public class CountByPersonCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 104L;

    public CountByPersonCommand(CollectionManager collectionManager) {
        super(collectionManager);
    }

    public String description() {
        return "count_by_person - подсчитать по человеку";
    }

    @Override
    public String execute(Request request) {
        Person person = (Person) request.getData();
        long count = collectionManager.countByPerson(person);
        return "Найдено элементов: " + count;
    }
}