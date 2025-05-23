package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.data.Person;
import org.example.util.CollectionManager;

/**
 * Команда подсчета по человеку
 */
public class CountByPersonCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public CountByPersonCommand(CollectionManager collectionManager) {
        super(DataObject.PERSON);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "count_by_person - подсчитать по человеку";
    }

    @Override
    public String execute(CommandData commandData) {
        Person person = (Person) commandData.getData();
        long count = collectionManager.countByPerson(person);
        return "Найдено элементов: " + count;
    }
}