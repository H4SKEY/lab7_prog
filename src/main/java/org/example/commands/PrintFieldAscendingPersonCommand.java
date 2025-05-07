package org.example.commands;

import org.example.util.CollectionManager;
import org.example.data.Person;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class PrintFieldAscendingPersonCommand extends AbstractCommand implements Serializable {
    @Serial
    private static final long serialVersionUID = 109L;

    public PrintFieldAscendingPersonCommand(CollectionManager collectionManager) {
        super(collectionManager); // Передаем параметры в родительский класс
    }

    public String description() {
        return "print_field_ascending_person - вывести людей по возрастанию";
    }

    @Override
    public String execute(String[] args, Object data) {
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