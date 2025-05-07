package org.example.util;

import org.example.data.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Класс для обработки пользовательского ввода с валидацией
 */
public class InputManager {

    private final Scanner scanner;

    public InputManager(Scanner scanner) {
        this.scanner = scanner;
    }

    // Основные методы чтения данных

    public String readString(String prompt, boolean allowEmpty) {
        while (true) {
            System.out.print(prompt + (allowEmpty ? " (можно пустое): " : ": "));
            String input = scanner.nextLine().trim();
            if (!allowEmpty && input.isEmpty()) {
                System.out.println("Ошибка: значение не может быть пустым");
                continue;
            }
            return input.isEmpty() ? null : input;
        }
    }

    public int readInt(String prompt, int minValue) {
        while (true) {
            try {
                System.out.print(prompt + " (минимум " + minValue + "): ");
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value < minValue) {
                    System.out.println("Ошибка: значение должно быть не меньше " + minValue);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число");
            }
        }
    }

    public long readLong(String prompt, long maxValue) {
        while (true) {
            try {
                System.out.print(prompt + " (максимум " + maxValue + "): ");
                long value = Long.parseLong(scanner.nextLine().trim());
                if (value > maxValue) {
                    System.out.println("Ошибка: значение должно быть не больше " + maxValue);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число");
            }
        }
    }

    public Integer readInt(String prompt, int minValue, boolean canBeNull) {
        if (!canBeNull) {
            readInt(prompt, minValue);
        }
        while (true) {
            System.out.print(prompt + " (минимум " + minValue + " или пустое): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                int value = Integer.parseInt(input);
                if (value < minValue) {
                    System.out.println("Ошибка: значение должно быть не меньше " + minValue);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число или оставьте пустым");
            }
        }
    }

    public <T extends Enum<T>> T readEnum(Class<T> enumClass, String prompt) {
        System.out.println(prompt + ". Допустимые значения: " + Arrays.toString(enumClass.getEnumConstants()));
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                return null;
            }
            try {
                return Enum.valueOf(enumClass, input.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: введите одно из допустимых значений или оставьте пустым");
            }
        }
    }

    // Методы для чтения сложных объектов

    public Coordinates readCoordinates() {
        System.out.println("--- Ввод координат ---");
        long x = readLong("X", Long.MAX_VALUE);
        long y = readLong("Y", 166);
        return new Coordinates(x, y);
    }

    public Person readPerson() {
        System.out.println("--- Ввод данных человека ---");
        String passportID = readString("Passport ID (4-49 символов)", true);
        if (passportID != null && (passportID.length() < 4 || passportID.length() > 49)) {
            System.out.println("Ошибка: длина passport ID должна быть от 4 до 49 символов");
            return readPerson();
        }
        Color eyeColor = readEnum(Color.class, "Цвет глаз");
        Color hairColor = readEnum(Color.class, "Цвет волос");
        return new Person(passportID, eyeColor, hairColor);
    }

    public Ticket readTicket() {
        System.out.println("--- Ввод данных билета ---");
        String name = readString("Название билета", false);
        Coordinates coordinates = readCoordinates();
        Integer price = readInt("Цена билета", 1, true);
        Integer discount = readInt("Скидка (1-100)", 1, true);
        if (discount != null && discount > 100) {
            System.out.println("Ошибка: скидка не может быть больше 100");
            discount = readInt("Скидка (1-100)", 1, true);
        }
        TicketType type = readEnum(TicketType.class, "Тип билета");
        Person person = readPerson();
        Ticket ticket = new Ticket(name, coordinates, LocalDateTime.now(), price, discount, type, person);
        System.out.println(ticket);
        return ticket;
    }
}