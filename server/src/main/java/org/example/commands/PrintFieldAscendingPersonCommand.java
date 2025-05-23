package org.example.commands;

import org.example.network.CommandData;
import org.example.network.DataObject;
import org.example.data.Person;
import org.example.util.CollectionManager;

import java.util.List;
import java.util.stream.Collectors;

public class PrintFieldAscendingPersonCommand extends AbstractCommand {
    private final CollectionManager collectionManager;

    public PrintFieldAscendingPersonCommand(CollectionManager collectionManager) {
        super(DataObject.NONE);
        this.collectionManager = collectionManager;
    }

    public String description() {
        return "print_field_ascending_person - вывести людей по возрастанию";
    }

    @Override
    public String execute(CommandData commandData) {
        List<Person> persons = collectionManager.getPersonsAscending();

        if (persons.isEmpty()) {
            return "Коллекция пуста";
        }

        collectionManager.sort();

        return persons.stream()
                .map(Person::toString) // Преобразуем каждого человека в строку
                .collect(Collectors.joining("\n", "Значения поля person в порядке возрастания:\n", ""));
    }
}