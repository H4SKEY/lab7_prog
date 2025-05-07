package org.example.data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Класс, представляющий билет
 */
public class Ticket implements Comparable<Ticket>, Serializable {
    @Serial
    private static final long serialVersionUID = 300L;
    private int id; //Значение поля должно быть больше 0, уникальное, генерируется автоматически
    private String name; //Поле не может быть null, строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creationDate; //Поле не может быть null, генерируется автоматически
    private Integer price; //Поле может быть null, значение должно быть больше 0
    private Integer discount; //Поле может быть null, значение должно быть больше 0, максимум 100
    private TicketType type; //Поле может быть null
    private Person person; //Поле не может быть null

    public Ticket(int id, String name, Coordinates coordinates, LocalDateTime creationDate,
                  Integer price, Integer discount, TicketType type, Person person) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.discount = discount;
        this.type = type;
        this.person = person;
    }

    public Ticket(String name, Coordinates coordinates, LocalDateTime creationDate,
                  Integer price, Integer discount, TicketType type, Person person) {
        this.id = 0;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.price = price;
        this.discount = discount;
        this.type = type;
        this.person = person;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getDiscount() {
        return discount;
    }

    public TicketType getType() {
        return type;
    }

    public Person getPerson() {
        return person;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    @Override
    public int compareTo(Ticket other) {
        return Integer.compare(this.id, other.id);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", price=" + price +
                ", discount=" + discount +
                ", type=" + type +
                ", person=" + person +
                '}';
    }
}