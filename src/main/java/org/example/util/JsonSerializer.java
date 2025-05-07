package org.example.util;

import jakarta.json.*;
import jakarta.json.stream.JsonGenerator;
import org.example.data.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для сериализации и десериализации коллекции в JSON
 * без использования сторонних библиотек (только Java JSON API)
 */
public class JsonSerializer {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Сохраняет список билетов в JSON файл
     */
    public static void saveToFile(List<Ticket> tickets, String filename) throws IOException {
        // Настройка для красивого форматирования JSON
        Map<String, Object> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);

        JsonWriterFactory writerFactory = Json.createWriterFactory(config);

        try (OutputStream os = new FileOutputStream(filename);
             JsonWriter jsonWriter = writerFactory.createWriter(os)) {

            JsonArray jsonArray = ticketsToJsonArray(tickets);
            jsonWriter.writeArray(jsonArray);
        }
    }

    /**
     * Загружает список билетов из JSON файла
     */
    public static List<Ticket> loadFromFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        try (InputStream is = new FileInputStream(file);
             JsonReader jsonReader = Json.createReader(is)) {

            JsonArray jsonArray = jsonReader.readArray();
            return jsonArrayToTickets(jsonArray);
        }
    }

    /**
     * Преобразует список билетов в JsonArray
     */
    private static JsonArray ticketsToJsonArray(List<Ticket> tickets) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (Ticket ticket : tickets) {
            arrayBuilder.add(ticketToJsonObject(ticket));
        }

        return arrayBuilder.build();
    }

    /**
     * Преобразует билет в JsonObject
     */
    private static JsonObject ticketToJsonObject(Ticket ticket) {
        JsonObjectBuilder builder = Json.createObjectBuilder()
                .add("id", ticket.getId())
                .add("name", ticket.getName())
                .add("creationDate", ticket.getCreationDate().format(DATE_FORMATTER));

        // Координаты
        Coordinates coordinates = ticket.getCoordinates();
        if (coordinates != null) {
            builder.add("coordinates", Json.createObjectBuilder()
                    .add("x", coordinates.getX())
                    .add("y", coordinates.getY()));
        }

        // Остальные поля
        if (ticket.getPrice() != null) {
            builder.add("price", ticket.getPrice());
        }

        if (ticket.getDiscount() != null) {
            builder.add("discount", ticket.getDiscount());
        }

        if (ticket.getType() != null) {
            builder.add("type", ticket.getType().name());
        }

        // Person
        Person person = ticket.getPerson();
        if (person != null) {
            JsonObjectBuilder personBuilder = Json.createObjectBuilder();

            if (person.getPassportID() != null) {
                personBuilder.add("passportID", person.getPassportID());
            }

            if (person.getEyeColor() != null) {
                personBuilder.add("eyeColor", person.getEyeColor().name());
            }

            if (person.getHairColor() != null) {
                personBuilder.add("hairColor", person.getHairColor().name());
            }

            builder.add("person", personBuilder);
        }

        return builder.build();
    }

    /**
     * Преобразует JsonArray в список билетов
     */
    private static List<Ticket> jsonArrayToTickets(JsonArray jsonArray) {
        List<Ticket> tickets = new ArrayList<>();

        for (JsonValue value : jsonArray) {
            JsonObject jsonObject = (JsonObject) value;
            Ticket ticket = jsonObjectToTicket(jsonObject);
            if (ticket != null) {
                tickets.add(ticket);
            }
        }

        return tickets;
    }

    /**
     * Преобразует JsonObject в билет
     */
    private static Ticket jsonObjectToTicket(JsonObject jsonObject) {
        try {
            // Обязательные поля
            int id = jsonObject.getInt("id");
            String name = jsonObject.getString("name");
            LocalDateTime creationDate = LocalDateTime.parse(
                    jsonObject.getString("creationDate"), DATE_FORMATTER);

            // Координаты
            JsonObject coordinatesObj = jsonObject.getJsonObject("coordinates");
            Coordinates coordinates = new Coordinates(
                    coordinatesObj.getJsonNumber("x").longValue(),
                    coordinatesObj.getJsonNumber("y").longValue());

            // Необязательные поля
            Integer price = jsonObject.containsKey("price") ?
                    jsonObject.getInt("price") : null;

            Integer discount = jsonObject.containsKey("discount") ?
                    jsonObject.getInt("discount") : null;

            TicketType type = jsonObject.containsKey("type") ?
                    TicketType.valueOf(jsonObject.getString("type")) : null;

            // Person
            JsonObject personObj = jsonObject.getJsonObject("person");
            Person person = new Person(
                    personObj.containsKey("passportID") ?
                            personObj.getString("passportID") : null,
                    personObj.containsKey("eyeColor") ?
                            Color.valueOf(personObj.getString("eyeColor")) : null,
                    personObj.containsKey("hairColor") ?
                            Color.valueOf(personObj.getString("hairColor")) : null);

            return new Ticket(
                    id, name, coordinates, creationDate,
                    price, discount, type, person);

        } catch (Exception e) {
            System.err.println("Ошибка парсинга билета: " + e.getMessage());
            return null;
        }
    }
}